package ru.whbex.develop.clans.common.clan;

import ru.whbex.develop.clans.common.ClansPlugin;

import java.util.EnumMap;
// TODO: Support object values, not only boolean
public class ClanSettings {
    private static final String LPREFIX = "settings.";
    public enum Setting {
        // Is this clan visible by other players.
        // Members of that clan (obviously) can see it, same thing applies to allied clans
        // idk what to do with rivals
        // ignored if player has specific permission
        VISIBLE("settings.visible.description", "settings.visible.enabled", "settings.visible.disabled", true),
        // Is this clan profile can be viewed by other players
        // same as VISIBLE
        // ignored if player has specific permission
        VIEWABLE("settings.viewable.description", "settings.viewable.enabled", "settings.viewable.disabled", true),
        // Is anyone able to join this clan
        // admins can do /c place instead
        EVERYONE_CAN_JOIN("settings.public.description", "settings.public.enabled", "settings.public.disabled", false);

        public final boolean defaultVal;
        public final String description;
        public final String enabled;
        public final String disabled;
        Setting(String description, String enabled, String disabled, boolean def){
            this.defaultVal = def;
            this.description = description;
            this.enabled = enabled;
            this.disabled = disabled;
        }
    }

    private EnumMap<Setting, Boolean> settings;

    private ClanSettings(){
        settings = new EnumMap<>(Setting.class);

    }
    private Builder createBuilder(){
        return new Builder();
    }
    public static Builder builder(){
        return new ClanSettings().createBuilder();
    }
    public boolean getSettingValue(Setting setting){
        if(!settings.containsKey(setting))
            return setting.defaultVal;
        return settings.get(setting);
    }
    public void setSettingValue(Setting setting, boolean val){
        this.settings.put(setting, val);
    }

    public class Builder {
        private Builder(){}

        public Builder set(Setting setting, boolean value){
            ClanSettings.this.settings.put(setting, value);
            return this;
        }
        public ClanSettings build(){
            if(ClansPlugin.Context.DEBUG){
                ClansPlugin.dbg("Created ClanSettings with values: ");
                ClanSettings.this.settings.forEach((k, v) -> {
                    ClansPlugin.dbg("- {0}: {1}", k, v);
                });
            }
            return ClanSettings.this;
        }


    }


}
