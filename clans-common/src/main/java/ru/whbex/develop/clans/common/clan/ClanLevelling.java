package ru.whbex.develop.clans.common.clan;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.lib.log.LogDebug;

public class ClanLevelling {
    public ClanLevelling(int level, int experience){
        this.experience = experience;
        this.level = level;
        LogDebug.print("created clanlevelling with {0}:{1} initial lvl:exp", level, experience);
    }
    private int experience;
    private int level;


    public int getExperience() {
        return experience;
    }
    public void reset(){
        LogDebug.print("levelling reset!");
        this.experience = Constants.LVL_EXP_DEF;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        LogDebug.print("set exp " + experience + ", trig recalc");
        recalcLevel();
    }

    public int getLevel(){
        return level;
    }
    public void setLevel(int level, boolean upd){
        this.level = level;
        LogDebug.print("set level " + level + ", update: " + upd);
        if(upd)
            recalcLevel();
    }

    public void addExperience(int exp){
        this.experience += exp;
        LogDebug.print("add exp " + exp);
        recalcLevel();
    }

    /* levelup logic */

    // recalculate level based on experience. Returns true if level was updated
    public boolean recalcLevel(){
        int expNext = Constants.NEXT_LEVEL_REQ + level * Constants.NEXT_LEVEL_STEP;
        LogDebug.print("toNextExp: " + expNext);
        if(expNext <= experience){
            LogDebug.print("detected lvlup!!");
            level++;
            experience = experience - expNext;
            // recurse.
            return recalcLevel();
        }
        return false;


    }




}
