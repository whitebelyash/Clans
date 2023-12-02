package ru.whbex.develop.clan;

import ru.whbex.develop.Constants;

// TODO: lvlup system
public class ClanLevelling {
    public ClanLevelling(int experience){
        this.experience = experience;
    }
    private int experience;


    public int getExperience() {
        return experience;
    }
    public void reset(){
        this.experience = Constants.LVL_EXP_DEF;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getLevel(){

        // TODO: Return real level
        return 1;
    }

}
