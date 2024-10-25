package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.cmd.exec.CommandUsageError;

public class ClanDisbandCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        // WIP
        if(args.length < 2)
            throw new CommandUsageError();
        String tag = args[1];
        ClanManager.Error e = ClansPlugin.Context.INSTANCE.plugin.getClanManager().disbandClan(tag);
        if(e!=null)
            switch (e){
                case CLAN_NOT_FOUND -> throw new CommandError("meta.command.unknown-clan");
            }
        actor.sendMessageT("command.disband.success");
    }

    @Override
    public String name() {
        return "disband";
    }
}
