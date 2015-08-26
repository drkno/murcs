package sws.murcs.controller.pipes;

import javafx.scene.Node;

public interface FormErrors {

    void clearErrors();

    void clearErrors(final String sectionName);

    void addFormError(final Node invalidNode, final String helpfulMessage);

    void addFormError(final String sectionName, final Node invalidNode, final String helpfulMessage);

}
