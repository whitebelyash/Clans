package ru.whbex.develop.clans.common.misc;

import ru.whbex.lib.collections.PagedListView;
import ru.whbex.lib.lang.Language;
import ru.whbex.lib.string.StringUtils;

public class MiscUtils {

    public static String makeNavPane(Language lang, String cmd, PagedListView<?> e, int currentPage){
        return StringUtils.simpleformat(lang.getPhrase("meta.tui.navpane"), currentPage, e.pageAmount(), cmd);
    }
}
