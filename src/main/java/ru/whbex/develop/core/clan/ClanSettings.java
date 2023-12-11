package ru.whbex.develop.core.clan;

public class ClanSettings {

    // TODO: move to Constants.java
    private boolean visible = true;
    private boolean viewable = true;
    private boolean publicJoinable = false;


    // New instance
    public ClanSettings(){}

    // idk
    // maybe convert to builder or use map idk idk
    public ClanSettings(boolean visible, boolean viewable, boolean publicJoinable){
        this.viewable = viewable;
        this.visible = visible;
        this.publicJoinable = publicJoinable;
    }


}
