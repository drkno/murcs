package sws.murcs.magic.tracking;

import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.magic.tracking.listener.ChangeListenerHandler;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Organisation;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.util.AbstractMap.SimpleEntry;

/**
 * Manages undo and redo operations.
 */
public final class UndoRedoManager {

    /**
     * Instance of the UndoRedoManager.
     */
    private static UndoRedoManager instance;

    /**
     * Gets the current UndoRedoManager instance.
     * @return the instance of the UndoRedoManager.
     */
    public static UndoRedoManager get() {
        if (instance == null) {
            instance = new UndoRedoManager();
        }
        return instance;
    }

    /**
     * Private constructor as this is a utility singleton class.
     */
    private UndoRedoManager() {
        revertStack = new ArrayDeque<>();
        remakeStack = new ArrayDeque<>();
        commitNumber = 0;
        maximumCommits = -1;
        changeListeners = new ArrayList<>();
        disabled = false;
        modelState = new ArrayList<>();
        addedFields = new ArrayList<>();
        removedFields = new ArrayList<>();
    }

    /**
     * The head object, which represents the current state.
     */
    private Commit head;

    /**
     * The revert stack, represents all states that can be reverted to.
     */
    private Deque<Commit> revertStack;

    /**
     * The remake stack, represents all states that can be remaked to.
     */
    private Deque<Commit> remakeStack;

    /**
     * The last commit number that was used.
     */
    private long commitNumber;

    /**
     * Maximum number of commits that can be reverted to. -1 for infinity.
     */
    private long maximumCommits;

    /**
     * Listeners that have subscribed to the UndoRedoManager.
     */
    private List<ChangeListenerHandler> changeListeners;

    /**
     * If the UndoRedoManager is disabled.
     */
    private boolean disabled;

    /**
     * Current state of the model. Using these objects we can search for changes.
     */
    private Collection<Map.Entry<TrackableObject, FieldValuePair>> modelState;

    /**
     * Fields that have been added since the last commit.
     */
    private Collection<Map.Entry<TrackableObject, FieldValuePair>> addedFields;

    /**
     * Fields that have been removed since the last commit.
     */
    private Collection<Map.Entry<TrackableObject, FieldValuePair>> removedFields;

    /**
     * Adds an object to be tracked.
     * @param object new object to be tracked.
     */
    public void add(final TrackableObject object) {
        for (Field field : object.getTrackedFields()) {
            try {
                Map.Entry<TrackableObject, FieldValuePair> newField
                        = new SimpleEntry<>(object, new FieldValuePair(field, object));
                modelState.add(newField);
                addedFields.add(newField);
            }
            catch (Exception e) {
                ErrorReporter.get().reportError(e, "Could not get the field of an object when adding it to Undo/Redo");
            }
        }
    }

    /**
     * Removes an object from tracking.
     * @param object object to be removed from tracking.
     */
    public void remove(final TrackableObject object) {
        Collection<Map.Entry<TrackableObject, FieldValuePair>> removed
                = modelState.stream().filter(kvp -> kvp.getKey().equals(object)).collect(Collectors.toList());
        modelState.removeAll(removed);
        removedFields.addAll(removed);
    }

    /**
     * Searches for changes to the model.
     * @param beforeValues values as they were before the model was committed.
     * @param afterValues values as they are after the model has been committed.
     */
    private void findChanges(final Collection<FieldValuePair> beforeValues,
                                    final Collection<FieldValuePair> afterValues) {
        modelState.forEach(kvp -> {
            FieldValuePair value = kvp.getValue();
            FieldValuePair[] valueChange = value.update();
            if (valueChange != null) {
                beforeValues.add(valueChange[0]);
                afterValues.add(valueChange[1]);
            }
        });
    }

    /**
     * Saves the current state so that it can be restored at a later point in time.
     * @param message description of changes since last commit.
     * @return the unique commit number.
     * @throws Exception if an internal error occurs while committing.
     */
    public long commit(final String message) throws Exception {
        if (disabled) {
            return -1;
        }

        Collection<FieldValuePair> beforeValues = new ArrayList<>();
        Collection<FieldValuePair> afterValues = new ArrayList<>();
        findChanges(beforeValues, afterValues);

        // no changes made
        if (afterValues.isEmpty() && canRevert()) {
            if (!head.getMessage().contains(message)) {
                head.modifyMessage(head.getMessage() + ", " + message);
            }
            return commitNumber++;
        }

        if (head != null) {
            // add FieldValuePair so that undo is possible. this is done retrospectively because it is significantly faster
            beforeValues.stream().filter(fvp -> head.getPairs().stream()
                    .noneMatch(rvfp -> Objects.equals(rvfp.getObject(), fvp.getObject())
                            && rvfp.getField().equals(fvp.getField()))).forEach(head::addPair);

            revertStack.push(head);
        }

        head = new Commit(commitNumber, message, afterValues, addedFields, removedFields);
        addedFields = new ArrayList<>();
        removedFields = new ArrayList<>();

        if (maximumCommits >= 0 && revertStack.size() > maximumCommits) {
            revertStack.removeLast();
        }

        if (canRemake()) {
            remakeStack.clear();
        }
        notifyListeners(ChangeState.Commit);

        return commitNumber++;
    }

    /**
     * Forgets about the current commits.
     * Does NOT stop tracking currently added objects.
     */
    public void forget() {
        forget(false);
    }

    /**
     * Forgets about the current commits.
     * @param deleteSavedObjects true to forget about current objects added for tracking, false otherwise.
     */
    public void forget(final boolean deleteSavedObjects) {
        revertStack.clear();
        remakeStack.clear();
        if (deleteSavedObjects) {
            modelState.clear();
            addedFields.clear();
            removedFields.clear();
            head = null;
        }
        notifyListeners(ChangeState.Forget);
    }

    /**
     * Reverts the current changes to the last commit sequentially.
     * @throws Exception if an internal error occurs during the operation.
     */
    public void revert() throws Exception {
        revert(revertStack.peek().getCommitNumber());
    }

    /**
     * Reverts to a specified commit or the initial commit
     * if the specified commit is not found.
     * @param revertCommitNumber commit number to revert to.
     * @throws Exception if an internal error occurs during the operation.
     */
    public void revert(final long revertCommitNumber) throws Exception {
        while (!revertStack.isEmpty()) {
            remakeStack.push(head);
            Commit commit = revertStack.pop();
            commit.apply();
            modelState.removeAll(commit.getRemovedFields());
            modelState.addAll(commit.getAddedFields());
            head = commit;
            if (commit.getCommitNumber() == revertCommitNumber) {
                break;
            }
        }
        notifyListeners(ChangeState.Revert);
    }

    /**
     * Checks if the revert (undo) operation is available.
     * @return true if remake can be done, false otherwise.
     */
    public boolean canRevert() {
        return !revertStack.isEmpty();
    }

    /**
     * Gets the message associated with the first revert (undo) commit.
     * @return the commit message or null if cannot revert.
     */
    public String getRevertMessage() {
        if (canRevert()) {
            return head.getMessage();
        }
        else {
            return null;
        }
    }

    /**
     * Remakes (redoes) the current changes from the next commit sequentially.
     * @throws Exception if an internal error occurs during the operation.
     */
    public void remake() throws Exception {
        remake(remakeStack.peek().getCommitNumber());
    }

    /**
     * Remakes (redoes) to a specified commit or the final commit
     * if the specified commit is not found.
     * @param remakeCommitNumber commit number to remake to.
     * @throws Exception if an internal error occurs during the operation.
     */
    public void remake(final long remakeCommitNumber) throws Exception {
        while (!remakeStack.isEmpty()) {
            revertStack.push(head);
            Commit commit = remakeStack.pop();
            commit.apply();
            modelState.removeAll(commit.getRemovedFields());
            modelState.addAll(commit.getAddedFields());
            head = commit;
            if (commit.getCommitNumber() == remakeCommitNumber) {
                break;
            }
        }
        notifyListeners(ChangeState.Remake);
    }

    /**
     * Checks if the remake (redo) operation is available.
     * @return true if remake can be done, false otherwise.
     */
    public boolean canRemake() {
        return !remakeStack.isEmpty();
    }

    /**
     * Gets the message associated with the first remake (redo) commit.
     * @return the commit message or null if cannot remake.
     */
    public String getRemakeMessage() {
        if (canRemake()) {
            return remakeStack.peek().getMessage();
        }
        else {
            return null;
        }
    }

    /**
     * Gets the latest commit.
     * @return the latest commit.
     */
    public Commit getHead() {
        return head;
    }

    /**
     * Reverts the current state to the latest commit.
     * @throws Exception if an error occurs during this operation.
     */
    @SuppressWarnings("unused")
    public void revertToHead() throws Exception {
        head.apply();
    }

    /**
     * Gets the maximum number of commits that can be made before commits are forgotten.
     * This can be negative (defaults to -1) for infinite commits, or greater or equal
     * to zero for a set number.
     * @return The maximum number of commits
     */
    public long getMaximumCommits() {
        return maximumCommits;
    }

    /**
     * Sets the Maximum number of commits that can be made before commits are forgotten.
     * @param newMaximumCommits new maximum number of commits.
     * This can be negative (defaults to -1) for infinite commits,
     * or greater or equal to zero for a set number.
     */
    public void setMaximumCommits(final long newMaximumCommits) {
        maximumCommits = newMaximumCommits;
    }

    /**
     * Adds a listener for a change in state (eg commit, revert or remake performed)
     * that will be notified if such a change occurs.
     * @param eventListener the event listener to add.
     */
    public void addChangeListener(final UndoRedoChangeListener eventListener) {
        if (disabled) {
            return;
        }
        ChangeListenerHandler changeListenerHandler = new ChangeListenerHandler(eventListener);
        changeListeners.add(changeListenerHandler);
    }

    /**
     * Removes an change listener.
     * @param eventListener listener to remove.
     */
    public void removeChangeListener(final UndoRedoChangeListener eventListener) {
        if (disabled) {
            return;
        }
        ChangeListenerHandler listener = new ChangeListenerHandler(eventListener);
        changeListeners.remove(listener);
    }

    /**
     * Notifies listeners that a change has occurred.
     * @param changeType the type of change that occurred.
     */
    private void notifyListeners(final ChangeState changeType) {
        if (changeListeners.size() == 0) {
            return;
        }
        //ChangeListenerHandler.performGC(); Disabled as forcing a GC is very sloooowwwww
        for (int i = 0; i < changeListeners.size(); i++) {
            if (!changeListeners.get(i).eventNotification(changeType)) {
                changeListeners.remove(i);
                i--;
            }
        }
    }

    /**
     * Clears all listeners.
     */
    public void forgetListeners() {
        changeListeners.clear();
    }

    /**
     * Sets the disabled state of the Undo/Redo manager.
     * @param isDisabled new state.
     */
    public void setDisabled(final boolean isDisabled) {
        disabled = isDisabled;
    }

    /**
     * Sets if the Undo/Redo manager is disabled.
     * @return current disabled state.
     */
    public boolean getDisable() {
        return disabled;
    }

    /**
     * Removes history until a specific commit number.
     * WARNING: this is the nuclear option, you will not get history back.
     * @param assimilateCommitNumber commit number to remove until.
     * @throws Exception If you use this method when remake is possible.
     */
    public void assimilate(final long assimilateCommitNumber) throws Exception {
        if (head == null || head.getCommitNumber() == assimilateCommitNumber) {
            return;
        }
        if (canRemake()) {
            throw new Exception("Cannot assimilate while remake is possible.");
        }
        while (!revertStack.isEmpty()) {
            if (revertStack.peek().getCommitNumber() == assimilateCommitNumber) {
                break;
            }
            Commit commit = revertStack.pop();
            Collection<FieldValuePair> addablePairs = commit.getPairs().stream().filter(
                    p -> !head.getPairs().stream().anyMatch(
                    o -> o.getField().equals(p.getField())
                    && o.getObject().equals(p.getObject())))
                    .collect(Collectors.toList());
            addablePairs.forEach(head::addPair);

            head.getRemovedFields().addAll(commit.getAddedFields());
            head.getAddedFields().addAll(commit.getRemovedFields());
        }
        notifyListeners(ChangeState.Assimilate);
    }

    /**
     * Imports a organisation, so that it can be tracked by the undo/redo manager.
     * WARNING: will forget about anything relating to previous models or objects that
     * are currently being tracked.
     * @param model model to import.
     * @throws Exception when committing the changes fail.
     */
    public void importModel(final Organisation model) throws Exception {
        forget(false);
        add(model);
        model.getPeople().forEach(this::add);
        model.getTeams().forEach(this::add);
        model.getSkills().forEach(this::add);
        model.getProjects().forEach(this::add);
        model.getReleases().forEach(this::add);
        model.getStories().forEach(s -> {
            add(s);
            s.getAcceptanceCriteria().forEach(this::add);
            s.getTasks().forEach(t -> {
                add(t);
                add(t.getEstimateInfo());
            });
        });
        model.getBacklogs().forEach(this::add);
        model.getSprints().forEach(this::add);
        commit("open project");
    }
}
