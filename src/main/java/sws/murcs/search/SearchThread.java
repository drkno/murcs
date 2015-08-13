package sws.murcs.search;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Model;
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
     * Creates a new search thread which manages the searching of a particular collection.
     * @param list observable list to store search results in.
     * @param searchableCollection collection to search for matches.
     */
    public SearchThread(final ObservableList<SearchResult> list, final Collection<T> searchableCollection) {
        searchResults = list;
        collection = searchableCollection;
        shouldSearch = false;
        passOneFields = new ArrayList<>();
        passTwoFields = new ArrayList<>();
        passThreeFields = new ArrayList<>();
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
        searchFields(passOneFields);
        searchFields(passTwoFields);
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
                    String s = val.toString();
                    SearchResult result = searchValidator.matches(s);
                    if (result != null) {
                        result.setModel((Model) model);
                        Platform.runLater(() -> {
                            searchResults.add(result);
                        });
                        break;
                    }
                }
                catch (IllegalAccessException e) {
                    // Java screwed up as usual
                    ErrorReporter.get().reportError(e, "Could not access object, even when accessible is true.");
                }
            }
        }
    }
}
