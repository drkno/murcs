import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class DontClickMe {
    private ArrayList<String> messages = new ArrayList<String>();
    private int currentIndex;

    public DontClickMe(){
        load();
    }

    private void load() {
        URL uri = getClass().getResource("messages.txt");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(uri.getFile()));

            String line;
            while ((line = reader.readLine()) != null) {
                messages.add(line);
            }

            reader.close();
        }catch (IOException e){

        }
    }

    public void click(){
        currentIndex = (currentIndex + 1) % messages.size();
    }

    public String currentText(){
        return messages.get(currentIndex);
    }
}
