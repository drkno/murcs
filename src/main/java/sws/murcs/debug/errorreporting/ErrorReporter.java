package sws.murcs.debug.errorreporting;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import sws.murcs.controller.NavigationManager;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.view.App;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
import java.util.Map;

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
     * Creates a new ErrorReporter and binds unhandled exceptions to this class.
     * @param args the arguments the program was started with.
     */
    public static void setup(final String[] args) {
        if (reporter == null) {
            reporter = new ErrorReporter(args);
        }
    }

    /**
     * Gets the current ErrorReporter instance.
     * @return the current ErrorReporter instance.
     */
    public static ErrorReporter get() {
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
     * @param arg arguments the program was started with.
     */
    private ErrorReporter(final String[] arg) {
        StringBuilder builder = new StringBuilder(arg.length * 2);
        for (String a : arg) {
            builder.append(a);
            if (a.equalsIgnoreCase("debug")) {
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
        report(Thread.currentThread(), null, "![MANUAL]", true, ErrorType.Manual);
    }

    /**
     * Performs a report of a Throwable (exception) error.
     * @param e the throwable to report.
     * @param description a brief message describing some context (for an issue report title).
     */
    public void reportError(final Throwable e, final String description) {
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
        report(Thread.currentThread(), e, description, false, ErrorType.Automatic);
    }

    /**
     * Callback for the uncaught exception handler.
     * @param thread thread error occurred on.
     * @param e error that occurred.
     */
    private void reportException(final Thread thread, final Throwable e) {
        report(thread, e, "![UNHANDLED]", true, ErrorType.Automatic);
    }

    /**
     * Reports an exception and gathers information from the user where possible.
     * @param thread thread error occurred on.
     * @param throwable the error that occurred.
     * @param progDescription the description of the problem provided by the program.
     * @param showDialog whether to show a dialog.
     * @param dialogType type of dialog to display.
     */
    private void report(final Thread thread, final Throwable throwable, final String progDescription,
                        final boolean showDialog, final ErrorType dialogType) {
        if (throwable != null && printStackTraces) {
            System.err.println(progDescription);
            throwable.printStackTrace();
        }

        if (!showDialog) {
            performReporting(thread, throwable, null, progDescription);
            return;
        }

        Platform.runLater(() -> {
            ErrorReportPopup popup = new ErrorReportPopup();
            popup.setType(dialogType);
            popup.setReportListener(description -> performReporting(thread, throwable, description, progDescription));
            popup.show();
        });
    }

    /**
     * Generates a report and sends it to the sws servers.
     * @param thread thread the error occurred on.
     * @param throwable the exception that caused the error.
     * @param userDescription the description the user provided.
     * @param progDescription the description the program provided.
     */
    private void performReporting(final Thread thread, final Throwable throwable,
                                  final String userDescription, final String progDescription) {
        String stackTrace = throwableToString(throwable);
        String threadInfo = thread.toString();
        String report = buildReport(userDescription, progDescription, stackTrace, threadInfo);
        sendReport(report);
    }

    /**
     * Creates a report based on the provided data from the user.
     * @param userDescription description of the problem provided by the user.
     * @param progDescription description of the problem provided by the program.
     * @param exceptionData the data from the exception that caused the error.
     * @param miscData other data that might be helpful.
     * @return a URL encoded string report.
     */
    private String buildReport(final String userDescription, final String progDescription,
                               final String exceptionData, final String miscData) {
        final int multiplier = 5;
        Map<String, String> reportFields = new HashMap<>();

        try {
            reportFields.put("userDescription", URLEncoder.encode(userDescription, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // encoding is hard coded so can never happen.
            e.printStackTrace();
        }

        reportFields.put("misc", miscData);
        reportFields.put("args", arguments);
        reportFields.put("exception", exceptionData);
        reportFields.put("screenshots", getScreenshots());
        reportFields.put("progDescription", progDescription);
        reportFields.put("dateTime", LocalDate.now().toString() + " " + LocalTime.now().toString());
        reportFields.put("osName", System.getProperty("os.name"));
        reportFields.put("osVersion", System.getProperty("os.version"));
        reportFields.put("javaVersion", System.getProperty("java.version"));
        reportFields.put("navForwardPossible", Boolean.toString(NavigationManager.canGoForward()));
        reportFields.put("navBackwardPossible", Boolean.toString(NavigationManager.canGoBack()));
        reportFields.put("histUndoPossible", Boolean.toString(UndoRedoManager.canRevert()));
        reportFields.put("histRedoPossible", Boolean.toString(UndoRedoManager.canRemake()));

        StringBuilder builder = new StringBuilder(reportFields.size() * multiplier + 2);
        builder.append("{");
        for (Map.Entry<String, String> entry : reportFields.entrySet()) {
            builder.append("\"");
            builder.append(entry.getKey());
            builder.append("\":\"");
            builder.append(entry.getValue());
            builder.append("\",");
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
            Collection<String> images = new ArrayList<>();
            for (Window window: App.getWindowManager().getAllWindows()) {
                Parent snapshotNode = window.getStage().getScene().getRoot();
                WritableImage image = snapshotNode.snapshot(new SnapshotParameters(), null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", outputStream);
                byte[] bytes = outputStream.toByteArray();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                images.add("data:image/png;base64," + base64);
            }
            return convertArrayToJSONString(images);
        }
        catch (Exception e) {
            // JavaFX probably hasn't started up yet.
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
        System.out.println(jsonArray);
        return jsonArray;
    }

    /**
     * Send the error report to the server.
     * @param report report to send.
     */
    private void sendReport(final String report) {
        final int successfulCode = 200;

        try {
            URL obj = new URL(BUG_REPORT_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "SWS Error Reporter");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(report);
            wr.flush();
            wr.close();

            if (con.getResponseCode() != successfulCode) {
                throw new Exception("Transmission failed.");
            }
        }
        catch (Exception e) {
            System.err.println("Could not submit error report.");
        }
    }
}
