package ru.whbex.develop.clans.common.misc;

import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.lib.collections.PagedListView;
import ru.whbex.lib.lang.Language;
import ru.whbex.lib.sql.conn.ConnectionConfig;
import ru.whbex.lib.string.StringUtils;

import java.io.File;
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

    public static ConnectionConfig systemConnectionConfig(Config.DatabaseType type, File workdir){
        return new ConnectionConfig(
                System.getProperty("clans.db-name"),
                !type.isFile() ? System.getProperty("clans.db-address") : new File(workdir, System.getProperty("clans.db-address")).getAbsolutePath(),
                System.getProperty("clans.db-user"),
                System.getProperty("clans.db-password")
        );
    }
    public static ConnectionConfig systemConnectionConfig(){
        return new ConnectionConfig(
                System.getProperty("clans.db-name"),
                System.getProperty("clans.db-address"),
                System.getProperty("clans.db-user"),
                System.getProperty("clans.db-password")
        );
    }

}
