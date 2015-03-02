package persistent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Student;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Handles the loading of all student information.
 * <p/>
 * Created by Haydon Baddock on 4/03/15.
 */
public class Reader {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Student[] readStudents(String filePath) {
        Student[] students = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            students = gson.fromJson(br, new TypeToken<Student[]>() {
            }.getType());
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
        return students;
    }

}
