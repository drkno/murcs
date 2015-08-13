package sws.murcs.search;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.search.tokens.Token;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Thread handler object to manage searching for model object on a thread.
 * @param <T> the model type to search.
 */
public class SearchThread<T> {

    /**
     * Thread that performs the actual work.
     */
    private Thread searchThread;

    /**
     * Iteration variable so that the thread can be terminated.
     */
    private boolean shouldSearch;

    /**
     * Token that will be used to validate each object.
     */
    private Token searchValidator;

    /**
     * Collection of objects to search.
     */
    private Collection<T> collection;

    /**
     * Determines if the fields have been setup.
     */
    private boolean hasSetupFields;

    /**
     * Observable list to store the search results in.
     */
    private ObservableList<SearchResult> searchResults;

    /**
     * Fields that will be searched in pass zero.
     */
    private Collection<Field> passZeroFields;

    /**
     * Fields that will be searched in pass one.
     */
    private Collection<Field> passOneFields;

    /**
     * Fields that will be searched in pass two.
     */
    private Collection<Field> passTwoFields;

    /**
     * Fields that will be searched in pass three.
     */
    private Collection<Field> passThreeFields;

    /**
     * The type of model that this thread searches.
     */
    private ModelType searchType;

    /**
     * Creates a new search thread which manages the searching of a particular collection.
     * @param list observable list to store search results in.
     * @param modelType the type of model that this thread searches.
     * @param searchableCollection collection to search for matches.
     */
    public SearchThread(final ObservableList<SearchResult> list, final ModelType modelType,
                        final Collection<T> searchableCollection) {
        searchResults = list;
        collection = searchableCollection;
        shouldSearch = false;
        passZeroFields = new ArrayList<>();
        passOneFields = new ArrayList<>();
        passTwoFields = new ArrayList<>();
        passThreeFields = new ArrayList<>();
        searchType = modelType;
    }

    /**
     * Starts a new search on this thread using a provided validator.
     * @param theSearchValidator the search validator to be used when checking for matches.
     */
    public final void start(final Token theSearchValidator) {
        if (!hasSetupFields && collection.size() > 0) {
            Class clazz = collection.iterator().next().getClass();
            while (clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Searchable.class)) {
                        Searchable searchable = field.getAnnotation(Searchable.class);
                        switch (searchable.value()) {
                            case Ultra:
                                passZeroFields.add(field);
                                break;
                            case High:
                                passOneFields.add(field);
                                break;
                            case Medium:
                                passTwoFields.add(field);
                                break;
                            case Low:
                            default:
                                passThreeFields.add(field);
                                break;
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
            hasSetupFields = true;
        }

        if (searchThread != null && searchThread.isAlive()) {
            stop();
        }
        searchValidator = theSearchValidator;
        shouldSearch = true;
        try {
            searchThread = new Thread(this::performSearch);
            searchThread.start();
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Could not start a search thread.");
        }

    }

    /**
     * Aborts the search currently occurring on this thread.
     */
    public final void stop() {
        try {
            if (searchThread != null) {
                shouldSearch = false;
                searchThread.join();
                searchValidator = null;
            }
        } catch (InterruptedException e) {
            /*
                Thrown cause Java. Should not be reported cause if an exception is thrown
                what we wanted to happen was achieved anyway just under less than normal
                circumstances.
             */
        }
    }

    /**
     * Main search that performs object specific searching in
     * three search phases.
     */
    private void performSearch() {
        searchFields(passZeroFields);
        if (Token.getMaxSearchPriority().equals(SearchPriority.Ultra)) {
            return;
        }
        searchFields(passOneFields);
        if (Token.getMaxSearchPriority().equals(SearchPriority.High)) {
            return;
        }
        searchFields(passTwoFields);
        if (Token.getMaxSearchPriority().equals(SearchPriority.Medium)) {
            return;
        }
        searchFields(passThreeFields);
    }

    /**
     * Searches the current collection using a set of provided fields.
     * @param passFields fields to search within the collection objects.
     */
    private void searchFields(final Collection<Field> passFields) {
        for (T model : collection) {
            for (Field f : passFields) {
                if (!shouldSearch) {
                    return;
                }

                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }

                try {
                    Object val = f.get(model);
                    if (val == null) {
                        continue;
                    }
                    if (val instanceof Collection) {
                        boolean shouldBreak = false;
                        for (Object object : (Collection) val) {
                            if (find((Model) model, f, object)) {
                                shouldBreak = true;
                                break;
                            }
                        }
                        if (shouldBreak) {
                            break;
                        }
                    }
                    else {
                        if (find((Model) model, f, val)) {
                            break;
                        }
                    }
                }
                catch (IllegalAccessException e) {
                    // Java screwed up as usual
                    ErrorReporter.get().reportError(e, "Could not access object, even when accessible is true.");
                }
            }
        }
    }

    /**
     * Searches a model for a match.
     * @param model the model to search.
     * @param f the field to search.
     * @param o the object to search.
     * @return if a match was found.
     */
    private boolean find(final Model model, final Field f, final Object o) {
        String s = o.toString();
        SearchResult result = searchValidator.matches(s);
        if (result != null) {
            Searchable searchable = f.getAnnotation(Searchable.class);
            String fieldName = searchable.fieldName();
            if (fieldName.equals("")) {
                fieldName = f.getName();
            }

            result.setModel(model, fieldName, searchable.value());
            Platform.runLater(() -> searchResults.add(result));
            return true;
        }
        return false;
    }

    /**
     * Gets the type of model that this thread is searching.
     * @return the type of model.
     */
    public final ModelType getSearchType() {
        return searchType;
    }
}
