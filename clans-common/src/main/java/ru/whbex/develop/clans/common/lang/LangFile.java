package ru.whbex.develop.clans.common.lang;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.misc.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

// === Language file ===
// path.example=Legit
// path.example = Legit (not supported yet)
public class LangFile {


    private final File file;
    private BufferedReader reader;
    private String current;
    private boolean open = false;

    private int pos = -1;



    private boolean empty = false;
    // TODO: Separate LangFile instance create and load
    private final boolean loaded = true;
    public LangFile(File file) {
        ClansPlugin.dbg("Creating language file at " + file.getAbsolutePath());
        this.empty = !file.exists();
        this.file = file;
    }
    public void open() throws IOException {
        reader = new BufferedReader(new FileReader(file));
        this.open = true;
        current = reader.readLine();
        pos = 1;
    }
    public void close() throws IOException {
        reader.close();
    }
    public void setPosition(int pos) throws IOException {
        if(!open) return;
        this.pos = pos;
        reader.reset();
        for(int i = 0; i < pos - 1; i++){
            reader.readLine();
        }
        current = reader.readLine();
    }

    public int getPosition() {
        return pos;
    }

    public boolean hasNextLine() throws IOException {
        return open && current != null;
    }
    // returns raw string from langfile
    public String getCurrentString(){
        return current;
    }
    public boolean isCommented(){
        return current.charAt(0) == '#';
    }
    // returns split string (where 0 - key, 1 - value) with trimmed comments
    public String[] getCurrentPhrase(){
        if(isCommented())
            return null;
        current = current.trim();
        String[] ret = current.split("=", 2);
        // value can be empty
        if(ret.length != 2 || ret[0].isEmpty())
            throw new IllegalArgumentException(StringUtils.simpleformat("Invalid phrase at {0} line! (LangFile: {1})", pos, file.getAbsolutePath()));
        // ignore commented text in locale value (everything after #)
        int commentIndex = ret[1].indexOf('#');
        if(commentIndex > 0 && ret[1].charAt(commentIndex - 1) != '\\'){
            ret[1] = ret[1].substring(0, commentIndex - 1);
        }
        return ret;
    }
    // sets current string to next line
    public void next() throws IOException {
        if(!open)
            return;
        pos++;
        while((current = reader.readLine()) != null && current.isEmpty()){
            pos++;
        }
    }
    public boolean isStarted(){
        return open;

    }

    public boolean isLoaded() {
        return loaded;
    }
    public boolean isEmpty(){
        return empty;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "LangFile{" +
                "file=" + file +
                ", current='" + current + '\'' +
                ", open=" + open +
                ", pos=" + pos +
                ", empty=" + empty +
                '}';
    }
}
