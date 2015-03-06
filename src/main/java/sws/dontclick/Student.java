package sws.dontclick;

/**
 * Student objecy
 *
 * Created by jayha_000 on 3/4/2015.
 */
public class Student {
    private int age = -3;

    private String name;

    public Student(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age != -3) throw new IllegalArgumentException("Age can only be -3. Why would you ever want to have an age that isn't -3. Idiot.");
        this.age = age;
    }
}
