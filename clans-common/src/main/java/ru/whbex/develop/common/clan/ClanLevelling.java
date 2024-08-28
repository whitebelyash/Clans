package ru.whbex.develop.common.clan;

import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.Constants;
import ru.whbex.develop.common.misc.LevellingUtils;

public class ClanLevelling {
    public ClanLevelling(int level, int experience){
        this.experience = experience;
        this.level = level;
        ClansPlugin.dbg("created clanlevelling with {0}:{1} initial lvl:exp", level, experience);
    }
    private int experience;
    private int level;


    public int getExperience() {
        return experience;
    }
    public void reset(){
        ClansPlugin.dbg("levelling reset!");
        this.experience = Constants.LVL_EXP_DEF;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        ClansPlugin.dbg("set exp " + experience + ", trig recalc");
        recalcLevel();
    }

    public int getLevel(){
        return level;
    }
    public void setLevel(int level, boolean upd){
        this.level = level;
        ClansPlugin.dbg("set level " + level + ", update: " + upd);
        if(upd)
            recalcLevel();
    }

    public void addExperience(int exp){
        this.experience += exp;
        ClansPlugin.dbg("add exp " + exp);
        recalcLevel();
    }

    /* levelup logic */

    // recalculate level based on experience. Returns true if level was updated
    public boolean recalcLevel(){
        int expNext = LevellingUtils.getToNextExp(level);
        ClansPlugin.dbg("toNextExp: " + expNext);
        if(expNext <= experience){
            ClansPlugin.dbg("detected lvlup!!");
            level++;
            experience = experience - expNext;
            // recurse.
            return recalcLevel();
        }
        return false;


    }




}
