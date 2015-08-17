package sws.murcs.search;

import javafx.application.Platform;
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
    private Collection<SearchResult> searchResults;

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
     * The iteration of this search.
     */
    private long searchIteration;

    /**
     * Creates a new search thread which manages the searching of a particular collection.
     * @param list observable list to store search results in.
     * @param modelType the type of model that this thread searches.
     * @param searchableCollection collection to search for matches.
     */
    public SearchThread(final Collection<SearchResult> list, final ModelType modelType,
                        final Collection<T> searchableCollection) {
        searchResults = list;
        collection = searchableCollection;
        shouldSearch = false;
        passZeroFields = new ArrayList<>();
        passOneFields = new ArrayList<>();
        passTwoFields = new ArrayList<>();
        passThreeFields = new ArrayList<>();
        searchType = modelType;
        searchIteration = 0;
        try {
            searchThread = new Thread(this::performSearch);
            searchThread.setDaemon(true);
            searchThread.start();
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Could not spawn a search thread.");
        }
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
        synchronized (this) {
            searchValidator = theSearchValidator;
        }
        shouldSearch = true;
        try {
            synchronized (this) {
                this.notify();
            }
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Could not notify search thread.");
        }
    }

    /**
     * Aborts the search currently occurring on this thread.
     * Not guaranteed to have completed by the time the method returns,
     * instead is guaranteed to ensure any new results found from previous
     * search will not future ones *after this method terminates*.
     */
    public final void stop() {
        if (searchThread != null) {
            searchIteration++;
            shouldSearch = false;
            searchValidator = null;
        }
    }

    /**
     * Main search that performs object specific searching in
     * three search phases.
     *
     * IMPORTANT DESIGN DECISIONS:
     *  -   This method will not terminate. This is by design because spawning threads is
     *      very very slow, so instead it waits for a notification on 'this' to start
     *      searching again. This will reduce the overhead of spawning a new thread
     *      every single time a new character is typed for search.
     *  -   Rather than adding search results individually they are added in groups. This
     *      is because sorting has a very large overhead and in the JavaFX SortedList
     *      must occur on the GUI thread (I know, don't ask). By adding in groups we
     *      reduce the number of times this occurs.
     */
    private void performSearch() {
        while (true) {
            synchronized (this) {
                try {
                    this.wait();
                }
                catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Waiting on a search thread failed.");
                }
            }

            Collection<SearchResult> passZeroResults = new ArrayList<>();
            searchFields(passZeroFields, passZeroResults);
            long iteration = searchIteration;
            Platform.runLater(() -> {
                if (iteration == searchIteration) {
                    searchResults.addAll(passZeroResults);
                }
            });
            if (Token.getMaxSearchPriority().equals(SearchPriority.Ultra) || !shouldSearch) {
                continue;
            }

            Collection<SearchResult> passOneResults = new ArrayList<>();
            searchFields(passOneFields, passOneResults);
            Platform.runLater(() -> {
                if (iteration == searchIteration) {
                    searchResults.addAll(passOneResults);
                }
            });
            if (Token.getMaxSearchPriority().equals(SearchPriority.High) || !shouldSearch) {
                continue;
            }

            Collection<SearchResult> passTwoResults = new ArrayList<>();
            searchFields(passTwoFields, passTwoResults);
            Platform.runLater(() -> {
                if (iteration == searchIteration) {
                    searchResults.addAll(passTwoResults);
                }
            });
            if (Token.getMaxSearchPriority().equals(SearchPriority.Medium) || !shouldSearch) {
                continue;
            }

            Collection<SearchResult> passThreeResults = new ArrayList<>();
            searchFields(passThreeFields, passThreeResults);
            Platform.runLater(() -> {
                if (iteration == searchIteration) {
                    searchResults.addAll(passThreeResults);
                }
            });
        }
    }

    /**
     * Searches the current collection using a set of provided fields.
     * @param passFields fields to search within the collection objects.
     * @param results results collection to add found result too.
     */
    private void searchFields(final Collection<Field> passFields, final Collection<SearchResult> results) {
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
                            if (find((Model) model, f, object, results)) {
                                shouldBreak = true;
                                break;
                            }
                        }
                        if (shouldBreak) {
                            break;
                        }
                    }
                    else {
                        if (find((Model) model, f, val, results)) {
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
     * @param r the results collection to add found results to.
     * @return if a match was found.
     */
    private boolean find(final Model model, final Field f, final Object o, final Collection<SearchResult> r) {
        String s = o.toString();
        SearchResult result = null;
        synchronized (this) {
            if (searchValidator != null) {
                result = searchValidator.matches(s);
            }
        }

        if (result == null) {
            return false;
        }

        Searchable searchable = f.getAnnotation(Searchable.class);
        String fieldName = searchable.fieldName();
        if (fieldName.equals("")) {
            fieldName = f.getName();
        }

        result.setModel(model, fieldName, searchable.value());
        r.add(result);
        return true;
    }

    /**
     * Gets the type of model that this thread is searching.
     * @return the type of model.
     */
    public final ModelType getSearchType() {
        return searchType;
    }
}
