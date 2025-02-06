package ru.whbex.develop.clans.common.misc;

import ru.whbex.lib.collections.PagedListView;
import ru.whbex.lib.lang.Language;
import ru.whbex.lib.string.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscUtils {
    // ft. llama3.2-4b because im too dumb for regex
    // Allows tags with only latin characters and length between 3-9
    private static final Pattern TAG_PATTERN = Pattern.compile("^[a-zA-Z]{3,9}$");
    // Allows names with cyrillic or latin characters, whitespaces and length between 3-16.
    // Length does not apply to the words, thus "Name C" is still valid
    // TODO: allow ukrainian and other "exotic" letters as the ?, ! chars?
    // TODO: also check for strange names like "   a  djj   dd" and other
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-Я\\s]{3,16}$");

    public static String makeNavPane(Language lang, String cmd, PagedListView<?> e, int currentPage){
        return StringUtils.simpleformat(lang.getPhrase("meta.tui.navpane"), currentPage, e.pageAmount(), cmd);
    }
    public static boolean validateClanTag(String tag){
        return TAG_PATTERN.matcher(tag).matches();
    }
    public static boolean validateClanName(String name){
        return NAME_PATTERN.matcher(name).matches();
    }
}
