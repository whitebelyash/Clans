package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.cmd.Command;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.CommandError;
import ru.whbex.develop.clans.common.misc.StringUtils;
import ru.whbex.develop.clans.common.player.PlayerActor;

public class ClanCreateCommand implements Command {
    private final ClanManager cm = ClansPlugin.Context.INSTANCE.plugin.getClanManager();
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(args.length < 2)
            throw new CommandError("meta.command.usage");
        if(!actor.isPlayer())
            throw new CommandError("meta.command.player-required");
        PlayerActor pa = (PlayerActor) actor;
        String tag = args[1];
        String name = StringUtils.simpleformat(Constants.CLAN_NAME_FORMAT, tag);
        ClanManager.Error e = cm.createClan(tag, name, pa.getUniqueId());
        if(e!=null){
            // do not fix
            switch(e){
                case CLAN_TAG_EXISTS -> throw new CommandError("command.create-clan-exists");
            }
            return;
        }
        actor.sendMessageT("command.create.success", tag);
    }

    @Override
    public String name() {
        return "create";
    }
}
