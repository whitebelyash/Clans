package ru.whbex.develop.common.lang;

import ru.whbex.develop.common.ClansPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
            ClansPlugin.dbg("Failed to open LangFIle at {0}!", file.getFile().getAbsolutePath());
        }
        ClansPlugin.dbg("Position: {0}", file.getPosition());
        // LangFile is now pointing at first line
        try {
            while(file.hasNextLine()){
                if(file.isCommented()){
                    file.next(); continue;
                }
                String[] phrase = file.getCurrentPhrase();
                switch(phrase[0]){
                    case "locale":
                        ClansPlugin.dbg("Locale name: {0}", phrase[1]);
                        if(file.getFile().getName().equals(phrase[1]))
                            ClansPlugin.dbg("Locale name should have same name with LangFile. Expect this");
                        name = phrase[0];
                        break;
                    case "locale.name":
                        ClansPlugin.dbg("Locale name (localized): {0}", phrase[1]);
                        nameLocalized = phrase[1];
                        break;
                    case "locale.tag":
                        ClansPlugin.dbg("Locale tag: {0}", phrase[1]);
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
