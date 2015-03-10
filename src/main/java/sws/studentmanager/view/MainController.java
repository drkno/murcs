package sws.studentmanager.view;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sws.studentmanager.model.Student;
import sws.studentmanager.persistent.Reader;
import sws.studentmanager.persistent.Writer;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * @author dpv11@uclive.ac.nz (Daniel van Wichen)
 */
public class MainController {

    private static final ObservableList<Student> students = FXCollections.observableArrayList();

    @FXML
    TextField textFieldName;
    @FXML
    DatePicker datePickerDob;
    @FXML
    ListView<Student> listViewStudents;
    @FXML
    Button buttonSave;
    @FXML
    Button buttonDelete;
    @FXML
    Button buttonClear;

    private ReadOnlyProperty<Student> selectedStudent;

    /**
     * Adds a student to the list of students.
     *
     * @param student the student to be added
     */
    public static void addStudent(final Student student) {
        students.add(student);
        students.sort(new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                return student1.getName().compareTo(student2.getName());
            }
        });
    }

    @FXML
    void initialize() {
        selectedStudent = listViewStudents.getSelectionModel().selectedItemProperty();
        Student[] readStudents = Reader.readStudents(Main.filePath);
        if (readStudents != null)
            students.setAll(readStudents);

        listViewStudents.setItems(students);

        selectedStudent.addListener(new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {
                if (newValue != null) {
                    textFieldName.setText(newValue.getName());
                    datePickerDob.setValue(newValue.getDateOfBirth());
                }
            }
        });

        clearFields();
    }

    @FXML
    private void buttonActionClear(ActionEvent event) {
        listViewStudents.getSelectionModel().clearSelection();
        clearFields();
    }

    @FXML
    private void buttonActionDelete(ActionEvent event) {
        if (selectedStudent != null) {
            students.remove(selectedStudent.getValue());
            listViewStudents.getSelectionModel().clearSelection();
            clearFields();
        }

        writeStudents();
    }

    @FXML
    private void buttonActionSave(ActionEvent event) {
        Student student = selectedStudent.getValue();
        String studentName = textFieldName.getText();
        LocalDate studentDob = datePickerDob.getValue();

        if (student != null) {
            student.setName(studentName);
            student.setDateOfBirth(studentDob);
            listViewStudents.setItems(null);
            listViewStudents.setItems(students);
        } else {
            student = new Student(studentName, studentDob);
            addStudent(student);
            listViewStudents.getSelectionModel().select(student);
        }

        writeStudents();
    }

    /**
     * Writes the students to the json file.
     */
    private void writeStudents() {
        Student[] studentArray = new Student[students.size()];
        for (int i = 0; i < students.size(); i++)
            studentArray[i] = students.get(i);
        Writer.writeStudents(studentArray, Main.filePath);
    }

    /**
     * Clear all the information fields.
     */
    private void clearFields() {
        listViewStudents.getItems().clear();
        textFieldName.clear();
        datePickerDob.setValue(null);

    }

    /**
     * Empty studentList.
     */
    public static void emptyStudentList() {
        students.clear();
    }
}
