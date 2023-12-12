package ru.whbex.develop.common.lang;

import ru.whbex.develop.common.Clans;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LangFile {

    // path.example=Legit
    // path.example = Legit
    private final boolean empty;
    private final boolean loaded = true;
    private final Map<String, String> strings;
    public LangFile(File file) throws IOException {
        this.empty = file.exists() || !file.isFile();
        this.strings = new HashMap<>();

        // locale init

        if(!empty){
            BufferedReader r = new BufferedReader(new FileReader(file));
            try {
                String line;
                while((line = r.readLine()) != null){
                    // TODO: allow using spaces in key value separator
                    if(line.length() < 3)
                        throw new IllegalStateException("Line string length too small: " + line);
                    int separatorIndex = line.indexOf('=');
                    String key = line.substring(0, separatorIndex);
                    String value = line.substring(separatorIndex + 1);
                    Clans.dbg("Init string " + key + "=" + value);
                    strings.put(key, value);
                }
            } catch(IOException e){
                Clans.dbg("Lang init fail");
                // TODO: properly handle exception here
                e.printStackTrace();
            } finally {
                r.close();
            }

        }
    }
    public String getString(String path){
        if(empty || !strings.containsKey(path)) {
            Clans.dbg("Access to language with empty file or unknown string");
            return path;
        }
        return strings.get(path);
    }
    public String translate(String s){
        StringBuilder sb = new StringBuilder(s);
        if(s.charAt(0) == '@'){
            sb.deleteCharAt(0);
            return getString(sb.toString());
        }
        return s;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
