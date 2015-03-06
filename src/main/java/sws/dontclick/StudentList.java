package sws.dontclick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * Loads and saves a list of students
 *
 * Created by jayha_000 on 3/2/2015.
 */
public class StudentList {
    private ArrayList<Student> students = new ArrayList<>();
    private int currentIndex;

    private boolean loaded;
    
    public StudentList(){
        load();
    }


    public void load() {
        Path path = null;
        try {
            path = Paths.get(getClass().getResource("/students.txt").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            if (path != null){
                Files.lines(path).forEachOrdered(e -> students.add(new Student(e)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void click(){
        currentIndex = (currentIndex + 1) % students.size();
    }

    public ArrayList<Student> getStudents(){
        return students;
    }

    public String currentStudentName(){
        return students.get(currentIndex).getName();
    }

    public Student currentStudent(){
        return students.get(currentIndex);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
