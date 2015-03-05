package persistent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Student;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class ReaderTest {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File tmpStudentsJson;

    @Before
    public void setUp() throws Exception {
        java.io.Reader reader = new InputStreamReader(getClass().getResourceAsStream("students.json"));
        Scanner scanner = new Scanner(reader);

        tmpStudentsJson = File.createTempFile("students", "json");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpStudentsJson));

        while (scanner.hasNextLine()) {
            bw.write(scanner.nextLine());
        }

        scanner.close();
        bw.close();
    }

    @Test
    public void testReadStudents() throws Exception {
        Student[] students = Reader.readStudents(tmpStudentsJson.getAbsolutePath());
        Student s = students[0];

        assertEquals(s.getName(), "Daniel van Wichen");
        assertEquals(s.getDateOfBirth(), LocalDate.of(1994, 9, 25));
    }


}