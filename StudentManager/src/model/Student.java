package model;

import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;

/**
 * A class representing a generic student.
 *
 * @author dpv11@uclive.ac.nz (Daniel van Wichen)
 */
public class Student {

    private final SimpleObjectProperty<String> name;
    private final SimpleObjectProperty<LocalDate> dateOfBirth;

    /**
     * Constructor.
     *
     * @param name
     * @param dateOfBirth
     */
    public Student(String name, LocalDate dateOfBirth) {
        this.name = new SimpleObjectProperty<String>(name);
        this.dateOfBirth = new SimpleObjectProperty<LocalDate>(dateOfBirth);
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth.getValue();
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth.setValue(dateOfBirth);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
