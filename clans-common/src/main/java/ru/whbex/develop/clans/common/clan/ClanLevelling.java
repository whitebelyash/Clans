package ru.whbex.develop.clans.common.clan;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.lib.log.Debug;

public class ClanLevelling {
    public ClanLevelling(int level, int experience){
        this.experience = experience;
        this.level = level;
        Debug.print("created clanlevelling with {0}:{1} initial lvl:exp", level, experience);
    }
    private int experience;
    private int level;


    public int getExperience() {
        return experience;
    }
    public void reset(){
        Debug.print("levelling reset!");
        this.experience = Constants.LVL_EXP_DEF;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        Debug.print("set exp " + experience + ", trig recalc");
        recalcLevel();
    }

    public int getLevel(){
        return level;
    }
    public void setLevel(int level, boolean upd){
        this.level = level;
        Debug.print("set level " + level + ", update: " + upd);
        if(upd)
            recalcLevel();
    }

    public void addExperience(int exp){
        this.experience += exp;
        Debug.print("add exp " + exp);
        recalcLevel();
    }

    /* levelup logic */

    // recalculate level based on experience. Returns true if level was updated
    public boolean recalcLevel(){
        int expNext = nextExp();
        Debug.print("toNextExp: " + expNext);
        if(expNext <= experience){
            Debug.print("detected lvlup!!");
            level++;
            experience = experience - expNext;
            // recurse.
            return recalcLevel();
        }
        return false;
    }
    public int nextExp(){
        return Constants.NEXT_LEVEL_REQ + level * Constants.NEXT_LEVEL_STEP;
    }




}
