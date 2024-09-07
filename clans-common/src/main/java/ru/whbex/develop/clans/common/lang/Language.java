package ru.whbex.develop.clans.common.lang;

import ru.whbex.develop.clans.common.ClansPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class Language {

    private String name;
    private String nameLocalized;
    private Locale locale;

    private final Map<String, String> phrases = new HashMap<>();

    public Language(LangFile file){
        if(file.isEmpty())
            throw new IllegalArgumentException("LangFile is empty!");
        try {
            file.open();
        } catch (IOException e) {
            ClansPlugin.log(Level.INFO, "Failed to initialize language file at " + file.getFile().getPath());
            ClansPlugin.dbg_printStacktrace(e);
        }
        // LangFile is now pointing at first line
        try {
            while(file.hasNextLine()){
                if(file.isCommented()){
                    file.next(); continue;
                }
                String[] phrase = file.getCurrentPhrase();
                switch(phrase[0]){
                    case "locale":
                        name = phrase[0];
                        break;
                    case "locale.name":
                        nameLocalized = phrase[1];
                        break;
                    case "locale.tag":
                        locale = Locale.forLanguageTag(phrase[1]);
                        break;
                    default:
                        break;
                }
                phrases.put(phrase[0], phrase[1]);
                file.next();
            }
        } catch (Exception e) {
            ClansPlugin.dbg("Fail reading LangFile at {0} line", file.getPosition());
            e.printStackTrace();
        }
        ClansPlugin.dbg("Loaded Language at {0}", file.toString());
        try {
            file.close();
        } catch (IOException e) {
            ClansPlugin.dbg("Failed to close LangFile {0}!", file.toString());
        }
    }

    public String getName() {
        return name;
    }
    public String getNameLocalized() {
        return nameLocalized;
    }

    public Locale getLocale() {
        return locale;
    }
    public boolean hasPhrase(String key){
        return phrases.containsKey(key);
    }
    public String getPhrase(String key){
        return hasPhrase(key) ? phrases.get(key) : key;
    }


    public Map<String, String> getPhrases() {
        return phrases;
    }
}
