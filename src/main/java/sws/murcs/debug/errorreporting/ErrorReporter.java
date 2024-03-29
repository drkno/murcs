package sws.murcs.debug.errorreporting;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import sws.murcs.arguments.ArgumentsManager;
import sws.murcs.controller.JavaFXHelpers;
import sws.murcs.controller.MainController;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.view.App;

import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handles sending error reports in to SWS when something
 * goes wrong when it should not have.
 */
public final class ErrorReporter {

    /**
     * The URL to report bugs to.
     */
    private static final String BUG_REPORT_URL = "http://bugs.sws.nz/api/submitbug";

    /**
     * Current instance of this singleton.
     */
    private static ErrorReporter reporter;

    /**
     * An instance of the error reporter shown to the user.
     */
    private ErrorReportPopup popup;

    /**
     * The thread which the error was reported on.
     */
    private Thread thread;

    /**
     * The throwable which was given to the error report.
     */
    private Throwable throwable;

    /**
     * The description the user inputs with the error report.
     */
    private String userDescription;

    /**
     * The description that the program provides.
     */
    private String progDescription;

    /**
     * The pop over for displaying helpful messages.
     */
    private PopOver popOver;

    /**
     * Determines if the popover should be shown.
     */
    private boolean shouldShowPopover;

    /**
     * Check variable to prevent multiple instances of the reporter window.
     */
    private boolean reporterIsOpen;

    /**
     * Reports that we will try sending later.
     */
    private Collection<String> queuedReports;

    /**
     * Gets the current ErrorReporter instance.
     * @return the current ErrorReporter instance.
     */
    public static ErrorReporter get() {
        if (reporter == null) {
            reporter = new ErrorReporter();
        }
        return reporter;
    }

    /**
     * Arguments the program was started with.
     */
    private String arguments;

    /**
     * Whether to print stack traces for caught exceptions to the console.
     */
    private boolean printStackTraces;

    /**
     * Creates a new ErrorReporter.
     * ErrorReporter handles the reporting of errors to the SWS server when they
     * are unhanded or unexpected.
     */
    private ErrorReporter() {
        queuedReports = new LinkedList<>();
        String[] arg = ArgumentsManager.get().getArguments();
        StringBuilder builder = new StringBuilder(arg.length * 2);
        for (String a : arg) {
            builder.append(a);
            if (a.equalsIgnoreCase("--debug") || a.equalsIgnoreCase("-d")) {
                printStackTraces = true;
            }
            builder.append(" ");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        arguments = builder.toString();
        Thread.setDefaultUncaughtExceptionHandler(this::reportException);
    }

    /**
     * Perform a manual report of an error.
     * This MUST be used on the JavaFX application thread.
     */
    public void reportManually() {
        shouldShowPopover = true;
        report(Thread.currentThread(), null, "![MANUAL]", true, ErrorType.Manual);
    }

    /**
     * Performs a report of a Throwable (exception) error.
     * @param e the throwable to report.
     * @param description a brief message describing some context (for an issue report title).
     */
    public void reportError(final Throwable e, final String description) {
        shouldShowPopover = true;
        report(Thread.currentThread(), e, description, true, ErrorType.Automatic);
    }

    /**
     * Performs a secret report of a Throwable (exception) error.
     * If this is used, there will be NO prompting of the user that something is wrong
     * and the user will NOT be notified that their data is being sent without their
     * permission.
     * Should ONLY be used within internal classes such as tests where user prompting
     * is not a viable option.
     * @param e the throwable to report.
     * @param description a brief message describing some context (for an issue report title).
     */
    public void reportErrorSecretly(final Throwable e, final String description) {
        shouldShowPopover = false;
        report(Thread.currentThread(), e, description, false, ErrorType.Automatic);
    }

    /**
     * Callback for the uncaught exception handler.
     * @param pThread thread error occurred on.
     * @param e error that occurred.
     */
    private void reportException(final Thread pThread, final Throwable e) {
        shouldShowPopover = true;
        report(pThread, e, "![UNHANDLED]", true, ErrorType.Automatic);
    }

    /**
     * Reports an exception and gathers information from the user where possible.
     * @param pThread thread error occurred on.
     * @param pThrowable the error that occurred.
     * @param pProgDescription the description of the problem provided by the program.
     * @param showDialog whether to show a dialog.
     * @param dialogType type of dialog to display.
     */
    private void report(final Thread pThread, final Throwable pThrowable, final String pProgDescription,
                        final boolean showDialog, final ErrorType dialogType) {
        if (pThrowable != null && printStackTraces) {
            System.err.println(pProgDescription);
            pThrowable.printStackTrace();
        }

        if (!showDialog) {
            shouldShowPopover = false;
            performReporting(pThread, pThrowable, null, pProgDescription);
            return;
        }

        Platform.runLater(() -> {
            try {
                popup = ErrorReportPopup.newErrorReporter();
                synchronized (ErrorReporter.class) {
                    if (popup != null && !reporterIsOpen) {
                        popup.setType(dialogType);
                        popup.setReportListener(description -> {
                            synchronized (ErrorReporter.class) {
                                reporterIsOpen = false;
                            }
                            performReporting(pThread, pThrowable, description, pProgDescription);
                        });
                        popup.setCloseListener(windowIsOpen -> {
                            synchronized (ErrorReporter.class) {
                                reporterIsOpen = windowIsOpen;
                            }
                        });
                        reporterIsOpen = true;
                        popup.show();
                    }
                }
            } catch (Exception e) {
                performReporting(pThread, pThrowable,
                        "User could not enter description. Exception killed the reporting window too.", pProgDescription);
            }
        });
    }

    /**
     * Sets up the popover.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void setupPopOver() {
        popOver = new PopOver(new Label("testing"));
        popOver.detachableProperty().set(true);
        popOver.detachedProperty().set(true);
        popOver.detachedCloseButtonProperty().set(false);

        //Get the top most main controller window.
        MainController controller = App.getMainController();

        //Possible if the app crashes on opening.
        if (controller != null) {
            popOver.show(controller.getToolBarController().getToolBar());
        }

        VBox loader = new VBox();

        ImageView imageView = new ImageView();
        Image spinner = new Image(getClass().getResourceAsStream("/sws/murcs/spinner.gif"));
        imageView.setImage(spinner);
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);
        loader.getChildren().add(imageView);

        Label helpfulMessage = new Label("Sending report");
        helpfulMessage.setTextFill(JavaFXHelpers.hex2RGB("#9e9e9e"));
        loader.getChildren().add(helpfulMessage);

        loader.setAlignment(Pos.CENTER);
        loader.setPadding(new Insets(10));
        popOver.contentNodeProperty().setValue(loader);
    }

    /**
     * Generates a report and sends it to the sws servers.
     * @param pThread thread the error occurred on.
     * @param pThrowable the exception that caused the error.
     * @param pUserDescription the description the user provided.
     * @param pProgDescription the description the program provided.
     */
    private void performReporting(final Thread pThread, final Throwable pThrowable,
                                  final String pUserDescription, final String pProgDescription) {
        if (shouldShowPopover) {
            setupPopOver();
        }

        thread = pThread;
        throwable = pThrowable;
        userDescription = pUserDescription;
        progDescription = pProgDescription;
        String stackTrace = throwableToString(throwable);
        String threadInfo = thread.toString();
        String report = buildReport(userDescription, progDescription, stackTrace, threadInfo);
        sendReport(report);
    }

    /**
     * Creates a report based on the provided data from the user.
     * @param pUserDescription description of the problem provided by the user.
     * @param pProgDescription description of the problem provided by the program.
     * @param exceptionData the data from the exception that caused the error.
     * @param miscData other data that might be helpful.
     * @return a URL encoded string report.
     */
    private String buildReport(final String pUserDescription, final String pProgDescription,
                               final String exceptionData, final String miscData) {
        final int multiplier = 5;
        Map<String, String> reportFields = new HashMap<>();

        try {
            reportFields.put("userDescription", URLEncoder.encode(pUserDescription, "UTF-8"));
        }
        catch (NullPointerException e) {
            // user description is null.
            reportFields.put("userDescription", null);
        }
        catch (UnsupportedEncodingException e) {
            // encoding is hard coded so can never happen. But because check style, stack trace
            e.printStackTrace();
        }

        reportFields.put("misc", miscData);
        reportFields.put("args", arguments);
        reportFields.put("exception", exceptionData);
        reportFields.put("screenshot", getScreenshots());
        reportFields.put("progDescription", pProgDescription);
        reportFields.put("dateTime", LocalDate.now().toString() + " " + LocalTime.now().toString());
        reportFields.put("osName", System.getProperty("os.name"));
        reportFields.put("osVersion", System.getProperty("os.version"));
        reportFields.put("javaVersion", System.getProperty("java.version"));
        reportFields.put("histUndoPossible", Boolean.toString(UndoRedoManager.get().canRevert()));
        reportFields.put("histRedoPossible", Boolean.toString(UndoRedoManager.get().canRemake()));

        StringBuilder builder = new StringBuilder(reportFields.size() * multiplier + 2);
        builder.append("{");
        for (Map.Entry<String, String> entry : reportFields.entrySet()) {
            if (Objects.equals(entry.getKey(), "screenshot")) {
                builder.append("\"");
                builder.append(entry.getKey());
                builder.append("\":");
                builder.append(entry.getValue());
                builder.append(",");
            }
            else {
                builder.append("\"");
                builder.append(entry.getKey());
                builder.append("\":\"");
                builder.append(entry.getValue());
                builder.append("\",");
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");

        String json = builder.toString();
        json = json.replace("\t", "");
        json = json.replace("\r", "");
        return json.replace("\n", "\\n");
    }

    /**
     * Converts a throwable to a string stacktrace.
     * @param e the throwable to convert.
     * @return the string representation.
     */
    private String throwableToString(final Throwable e) {
        if (e == null) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();
        String message = e.getMessage();
        return message + "\n" + stackTrace;
    }

    /**
     * Grabs the screenshots of the currently displayed screens.
     * @return screenshots as base64 PNG images.
     */
    private String getScreenshots() {
        try {
            if (popup != null && popup.submitScreenShots() && App.getWindowManager().getAllWindows().size() > 0) {
                Collection<String> images = new ArrayList<>();
                for (Window window : App.getWindowManager().getAllWindows()) {
                    // Don't include an instance of the feedback window as a screenshot.
                    if (window.getController() != ErrorReportPopup.class) {
                        Parent snapshotNode = window.getStage().getScene().getRoot();
                        WritableImage image = snapshotNode.snapshot(new SnapshotParameters(), null);
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", outputStream);
                        byte[] bytes = outputStream.toByteArray();
                        String base64 = Base64.getEncoder().encodeToString(bytes);
                        images.add("data:image/png;base64," + base64);
                    }
                }
                if (images.size() > 0) {
                    return convertArrayToJSONString(images);
                }
            }
            return "[]";
        }
        catch (Exception e) {
            // JavaFX probably hasn't started up yet.
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Creates a json array string from a collection or strings.
     * @param list The collection to create the json string from.
     * @return The json string.
     */
    private String convertArrayToJSONString(final Collection<String> list) {
        String jsonArray = "[";
        for (String item : list) {
            jsonArray += "\"" + item + "\",";
        }
        jsonArray = jsonArray.substring(0, jsonArray.length() - 1);
        jsonArray += "]";
        return jsonArray;
    }

    /**
     * Send the error report to the server.
     * @param report report to send.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void sendReport(final String report) {
        queuedReports.add(report);
        final int successfulCode = 200;
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.schedule(() -> {
            try {
                for (String reportToSend : queuedReports) {
                    URL obj = new URL(BUG_REPORT_URL);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", "SWS Error Reporter");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(reportToSend);
                    wr.flush();
                    wr.close();

                    if (con.getResponseCode() != successfulCode) {
                        if (printStackTraces) {
                            System.err.println("Error code returned was " + con.getResponseCode());
                            InputStream errorStream = con.getErrorStream();
                            InputStreamReader reader = new InputStreamReader(errorStream);
                            StringBuilder builder = new StringBuilder();
                            BufferedReader breader = new BufferedReader(reader);
                            String line = breader.readLine();
                            while (line != null) {
                                builder.append(line);
                                line = breader.readLine();
                            }
                            System.err.println("Response:\n" + builder.toString());
                        }
                        throw new Exception("Transmission failed.");
                    }
                }
                queuedReports.clear();

                if (shouldShowPopover) {
                    Label helpfulMessage = new Label("Report sent :)");
                    helpfulMessage.setPadding(new Insets(10));
                    helpfulMessage.setTextFill(JavaFXHelpers.hex2RGB("#4caf50"));
                    Platform.runLater(() -> {
                        popOver.contentNodeProperty().setValue(helpfulMessage);
                        hidePopOverAfterGivenTime(3, 0.5);
                    });
                }
            } catch (Exception e) {
                try {
                    FileWriter fileWriter = new FileWriter("FailedErrorReports.log", true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(report);
                    bufferedWriter.close();
                }
                catch (IOException e1) {
                    // not a lot we can do
                }

                if (shouldShowPopover) {
                    VBox errorMessage = new VBox();
                    Label helpfulMessage = new Label("Sending of report failed :(\n"
                            + "Email the developers, perhaps the server is down");
                    helpfulMessage.setTextFill(JavaFXHelpers.hex2RGB("#f44336"));
                    errorMessage.getChildren().add(helpfulMessage);

                    Hyperlink link = new Hyperlink("s302g1@cosc.canterbury.ac.nz");
                    link.setOnAction(event -> {
                        try {
                            Desktop.getDesktop().browse(new URL("mailto:s302g1@cosc.canterbury.ac.nz").toURI());
                        }
                        catch (Exception a) {
                            // the error reporter cant send, an exception was thrown within it and to
                            // top it all off we cant open a url. things are bad...
                            e.printStackTrace();
                        }
                    });
                    link.getStyleClass().add("zero-border");
                    errorMessage.getChildren().add(link);

                    errorMessage.setAlignment(Pos.CENTER);
                    errorMessage.setPadding(new Insets(10));
                    helpfulMessage.setPadding(new Insets(10));
                    Platform.runLater(() -> {
                        popOver.contentNodeProperty().setValue(errorMessage);
                        hidePopOverAfterGivenTime(5, 0.75);
                    });
                }
                System.err.println("Could not submit error report.");
            }
        }, 3, TimeUnit.SECONDS);
    }

    /**
     * Hides a popOver after a given amount of time.
     * @param delay delay before hiding
     * @param pFadeDuration duration of fade time for the pop over.
     */
    private void hidePopOverAfterGivenTime(final int delay, final double pFadeDuration) {
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.schedule(() -> popOver.hide(pFadeDuration), delay, TimeUnit.SECONDS);
    }
}
