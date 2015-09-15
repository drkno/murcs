package sws.murcs.controller.pipes;

import sws.murcs.model.Model;
import sws.murcs.model.ModelType;

/**
 * A class that allows for navigation of specific tabs.
 */
public class NavigableTabController implements Navigable {
    /**
     * The tab this is controlling.
     */
    private Tabbable currentTab;

    @Override
    public void goForward() {
        currentTab.goForward();
    }

    @Override
    public void goBack() {
        currentTab.goBack();
    }

    @Override
    public boolean canGoForward() {
        return currentTab.canGoForward();
    }

    @Override
    public boolean canGoBack() {
        return currentTab.canGoBack();
    }

    @Override
    public void navigateTo(final Model model) {
        currentTab.navigateTo(model);
    }

    @Override
    public void navigateTo(final ModelType type) {
        currentTab.navigateTo(type);
    }

    @Override
    public void navigateToNewTab(final Model model) {
        currentTab.navigateTo(model);
    }

    /**
     * Sets the current tab.
     * @param tab The new tab
     */
    public void setCurrentTab(final Tabbable tab) {
        this.currentTab = tab;
    }
}
