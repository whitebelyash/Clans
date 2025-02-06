package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.cmd.exec.CommandUsageError;
import ru.whbex.lib.string.StringUtils;

import java.util.UUID;

public class ClanDeleteCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(args.length < 2)
            throw new CommandUsageError();
        String cid_s = args[1];
        UUID cid;
        if((cid = StringUtils.UUIDFromString(cid_s)) == null)
            throw new CommandUsageError();

        if(!actor.hasData("cmd-delete-confirm")){
            actor.sendMessageT("command.delete.warn");
            actor.setData("cmd-delete-confirm", cid);
            ClansPlugin.TaskScheduler().runLater(() -> actor.removeData("cmd-delete-confirm"), 2500L);
        } else {
            UUID f = (UUID) actor.getData("cmd-delete-confirm");
            if(!f.equals(cid)) {
                actor.sendMessageT("command.delete.cancelled");
                actor.removeData("cmd-delete-confirm");
                return;
            }
            actor.removeData("cmd-delete-confirm");
        }


        String tag = ClansPlugin.clanManager().getClan(cid).getMeta().getTag();
        ClanManager.Error e = ClansPlugin.clanManager().removeClan(cid);
        switch(e){
            case SUCCESS -> actor.sendMessageT("command.delete.success", tag);
            case CLAN_SYNC_ERROR -> throw new CommandError(null);
            case CLAN_NOT_FOUND -> actor.sendMessageT("meta.command-unknown-clan");
        }

    }

    @Override
    public String name() {
        return "delete";
    }
}
