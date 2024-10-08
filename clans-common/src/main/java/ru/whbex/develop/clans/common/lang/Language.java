package ru.whbex.develop.clans.common.lang;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
                phrases.put(phrase[0], phrase[1]);
                file.next();
            }
        } catch (Exception e) {
            ClansPlugin.log(Level.ERROR, "Failed reading LangFile {0} at {1} line", file.getFile().getName(), file.getPosition());
        }
        ClansPlugin.dbg("Loaded Language at {0}. Will load metadata now", file.toString());
        if((name = phrases.get("locale")) == null)
            ClansPlugin.log(Level.WARN, "Locale {0} has no name!", file.getFile().getName());
        if((locale = Locale.forLanguageTag(phrases.get("locale.tag"))) == null)
            ClansPlugin.log(Level.WARN, "Locale {0} has no locale tag, or it's invalid!", file.getFile().getName());
        if((nameLocalized = phrases.get("locale.name")) == null)
            ClansPlugin.log(Level.WARN, "Locale {0} has no localized name! Check locale.name tag in the file.", file.getFile().getName());
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

    @Override
    public String toString() {
        return "Language{" +
                "locale=" + locale +
                ", nameLocalized='" + nameLocalized + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
