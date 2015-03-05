package persistent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Student;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Handles the saving of all student information.
 *
 * @author dpv11@uclive.ac.nz (Daniel van Wichen)
 */
public class Writer {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Saves a collection of students to the specified location.
     *
     * @param students A collection of students
     * @param filePath The specified save location
     */

    public static void writeStudents(Student[] students, String filePath) {
        String profileString = gson.toJson(students);

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(profileString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
