package sws.studentmanager.persistent;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sws.studentmanager.model.Student;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Handles the loading of all student information.
 *
 * @author dpv11@uclive.ac.nz (Daniel van Wichen)
 */
public class Reader {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Student[] readStudents(String filePath) {
        Student[] students = null;
        File file = new File(filePath);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                students = gson.fromJson(br, new TypeToken<Student[]>() {
                }.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return students;
    }

}
