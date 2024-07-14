package ru.whbex.develop.common.lang;

import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.misc.StringUtils;

import java.io.*;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static ru.whbex.develop.common.misc.StringUtils.simpleformat;

// === Language file ===
// path.example=Legit
// path.example = Legit (not supported yet)
public class LangFile {
    private final ClansPlugin.Context ctx = ClansPlugin.Context.INSTANCE;


    private final File file;
    private BufferedReader reader;
    private String current;
    private boolean open = false;

    private int pos = -1;



    private boolean empty = false;
    // TODO: Separate LangFile instance create and load
    private boolean loaded = true;
    public LangFile(File file) {
        ClansPlugin.dbg("Creating language file at " + file.getAbsolutePath());
        this.empty = !file.exists();
        this.file = file;

        // locale init
        /*

        if(!empty){
            // Catch IO exceptions and other
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                String line;
                int line_num = 0;
                while ((line = r.readLine()) != null) {
                    line_num++;
                    // Catch line format errors here
                    try {
                        if(line.isEmpty())
                            continue;
                        // Ignore commented lines
                        if (line.charAt(0) == '#')
                            continue;
                        // TODO: allow using spaces in key value separator
                        if (line.length() < 3) {
                            ctx.logger.warning(simpleformat("String length in language file {0} too small at {1} line, skipping", file.getAbsolutePath(), line_num));
                            continue;
                        }

                        strings.put(key, value);
                    } catch (Exception e) {
                        ctx.logger.severe(simpleformat("Couldn't read line {0} ({1}): {2}, langfile: {3}", line, line_num, e.getMessage(), file.getAbsolutePath()));
                    }
                }
            } catch (Exception e) {
                ctx.logger.severe(simpleformat("Couldn't read language file {0}: {1}", file.getAbsolutePath(), e.getMessage()));
                if (ClansPlugin.Context.DEBUG)
                    e.printStackTrace();
            }

        }
        else {
            ctx.logger.warning(simpleformat("Couldn't load language file {0} because it doesn't exist or it's empty", file.getAbsolutePath()));
            empty = true;
        }
         */
    }
    public void open() throws IOException {
        reader = new BufferedReader(new FileReader(file));
        this.open = true;
        ClansPlugin.dbg("Open LangFile at {0}", file.getAbsolutePath());
        current = reader.readLine();
        pos = 1;
    }
    public void close() throws IOException {
        reader.close();
        ClansPlugin.dbg("Closed LangFile at {0}", file.getAbsolutePath());
    }
    public void setPosition(int pos) throws IOException {
        if(!open) return;
        this.pos = pos;
        reader.reset();
        for(int i = 0; i < pos - 1; i++){
            reader.readLine();
        }
        current = reader.readLine();
        ClansPlugin.dbg("Set position at {0}", pos);
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
        ClansPlugin.dbg("ret {0}", Arrays.toString(ret));
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
