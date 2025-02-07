package ru.whbex.develop.clans.common.misc;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.Clan;

import java.util.HashMap;
import java.util.Map;

// Source: ClanSystem (2022)
// TODO: Refactor
public class TagFormatter {

    private static final char[] NUMBERS = "0123456789".toCharArray();
    private static char[] styled_numbers = {};

    public TagFormatter(){
        styled_numbers = ClansPlugin.mainLanguage().getPhrase("meta.tag.stylized-level").toCharArray();
    }
    // tag для чата (цветные скобки и уровень с кастом форматом тут
    public static String getTagChatFormatted(){
        /*
        PluginPlayer asWhoPlugin = playermgr.getPlayer(asWho);
        if(!clanmgr.isClanMember(asWhoPlugin))
            return " none ";
        Member member = clanmgr.getClanByMember(asWhoPlugin).getMember(asWhoPlugin);
        String tag = clanmgr.getClanByMember(asWhoPlugin).getTag();
        int level = clanmgr.getClanByMember(asWhoPlugin).getLevel();
        String format = config.getString(CSConfig.ConfigEntry.TAG_FORMATTER_CHAT_FORMAT,
                Parameter.build(Replacements.TAG.get(), tag),
                Parameter.build(Replacements.RANKCOLOR.get(), member.getRank().getColor().toString()),
                Parameter.build(Replacements.LEVEL.get(), String.valueOf(level)),
                Parameter.build(Replacements.LEVELFORMAT.get(), getStyledLevel(level)),
                Parameter.build(Replacements.LEVELCOLOR.get(), getColorByLevel(level).toString()));
        return Utils.replaceColorChar(format);
         */
        return null;
    }
    // [Clan Tag]
    public static String getTagSimpleFormatted(Clan clan){
        return null;
    }
    public static String getTagNamePair(Clan clan){
        return null;
    }
    public static String getStyledLevel(int level){
        char[] level_sym = String.valueOf(level).toCharArray();
        StringBuilder l = new StringBuilder();
        // UNSAFE
        // TODO: Check for array out of bounds
        // TODO: check if casting char '0' to int does actually give zero
        for(int i : level_sym) {
            l.append(styled_numbers[i]);
        }
        return l.toString();
    }
    // TODO: заменить на обращение в конфиг за цветом
    // Source: VanillaCraft(s)
    public static char getColorByLevel(int level){
        // TODO: optimize
        if(level <= 9) return 'a';
        if(level <= 19) return 'e';
        if(level <= 29) return 'd';
        if(level <= 39) return '6';
        if(level <= 49) return 'b';
        return 'f';
    }

}
