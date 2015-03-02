package dontclick;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class DontClickMe {
    private ArrayList<String> messages = new ArrayList<String>();
    private int currentIndex;

    private boolean loaded;

    public DontClickMe(boolean load){
        if (load)
            load();
    }

    public void load() {
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

    public ArrayList<String> getMessages(){
        return messages;
    }

    public String currentText(){
        return messages.get(currentIndex);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
