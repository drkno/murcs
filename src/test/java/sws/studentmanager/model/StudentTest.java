package sws.studentmanager.model;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class StudentTest {

    private static final Student student = new Student("Daniel van Wichen", LocalDate.of(1994, 12, 25));


    @Test
    public void testGetName() throws Exception {
        assertEquals(student.getName(), "Daniel van Wichen");
    }

    @Test
    public void testSetName() throws Exception {

    }

    @Test
    public void testGetDateOfBirth() throws Exception {
        assertEquals(student.getDateOfBirth(), LocalDate.of(1994, 12, 25));
    }

    @Test
    public void testSetDateOfBirth() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }
}