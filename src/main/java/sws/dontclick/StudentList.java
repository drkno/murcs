package main.java.sws.dontclick;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
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
        URL url = this.getClass().getResource("students.txt");
        System.out.println(url.toString());

        try {
            BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));

            String line;
            while ((line = reader.readLine()) != null) {
                students.add(new Student(line));
            }

            reader.close();
        }catch (IOException ignored){

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
