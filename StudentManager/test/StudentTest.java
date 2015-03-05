import model.Student;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link model.Student}
 *
 * @author dpv11@uclive.ac.nz (Daniel van Wichen)
 */
public class StudentTest {

    @Test
    public void test() {
        Student student = new Student("Daniel van Wichen", LocalDate.of(1994, 12, 25));

        assertEquals(student.getDateOfBirth(), LocalDate.of(1994, 12, 25));
        assertEquals(student.getName(), "Daniel van Wichen");
    }
}
