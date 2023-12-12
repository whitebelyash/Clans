package ru.whbex.develop.common.lang;

import ru.whbex.develop.common.Clans;
import ru.whbex.develop.common.misc.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static ru.whbex.develop.common.misc.StringUtils.simpleformat;

// === Language file ===
// path.example=Legit
// path.example = Legit (not supported yet)
public class LangFile {


    private boolean empty = false;
    // TODO: Separate LangFile instance create and load
    private boolean loaded = true;
    private final Map<String, String> strings;
    public LangFile(File file) {
        Clans.dbg("Creating language file at " + file.getAbsolutePath());
        this.empty = !file.exists();
        this.strings = new HashMap<>();

        // locale init

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
                            Clans.LOGGER.warning(simpleformat("String length in language file {0} too small at {1} line, skipping", file.getAbsolutePath(), line_num));
                            continue;
                        }
                        // also ignore commented text in locale value (everything after #)
                        int commentIndex = line.indexOf('#');
                        if(commentIndex > 3 && line.charAt(commentIndex - 1) != '\\'){
                            line = line.substring(0, commentIndex - 1);
                        }
                        int separatorIndex = line.indexOf('=');
                        String key = line.substring(0, separatorIndex);
                        String value = line.substring(separatorIndex + 1);
                        strings.put(key, value);
                    } catch (Exception e) {
                        Clans.LOGGER.severe(simpleformat("Couldn't read line {0} ({1}): {2}, langfile: {3}", line, line_num, e.getMessage(), file.getAbsolutePath()));
                    }
                }
            } catch (Exception e) {
                Clans.LOGGER.severe(simpleformat("Couldn't read language file {0}: {1}", file.getAbsolutePath(), e.getMessage()));
                if (Clans.DEBUG)
                    e.printStackTrace();
            }

        }
        else {
            Clans.LOGGER.warning(simpleformat("Couldn't load language file {0} because it doesn't exist or it's empty", file.getAbsolutePath()));
            empty = true;
        }
    }
    public String getString(String path){
        return this.getString(path, path);

    }
    public String getString(String path, String def){
        if(empty || !strings.containsKey(path)) {
            Clans.dbg("Access to language with empty file or unknown string");
            return def;
        }
        return strings.get(path);
    }

    public boolean pathExists(String path){
        return empty || strings.containsKey(path);
    }

    public boolean isLoaded() {
        return loaded;
    }
    public boolean isEmpty(){
        return empty;
    }
}
