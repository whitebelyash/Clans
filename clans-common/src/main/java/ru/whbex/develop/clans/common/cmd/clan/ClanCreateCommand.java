package ru.whbex.develop.clans.common.cmd.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.cmd.exec.CommandUsageError;
import ru.whbex.develop.clans.common.misc.MiscUtils;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.string.StringUtils;

public class ClanCreateCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(args.length < 2)
            throw new CommandUsageError();
        if(!actor.isPlayer())
            throw new CommandError("meta.command.player-required");
        ClanManager cm = ClansPlugin.clanManager();
        PlayerActor pa = (PlayerActor) actor;
        String tag = args[1];
        if(!MiscUtils.validateClanTag(tag))
            throw new CommandError("meta.clan-check.invalid-tag");
        String name = args.length < 3 ? StringUtils.simpleformat(Constants.CLAN_NAME_FORMAT, tag) : args[2];
        if(!MiscUtils.validateClanName(name))
            throw new CommandError("meta.clan-check.invalid-name");
        // Check for clan deleted bit
        if(cm.getClan(pa) != null && cm.getClan(pa).isDeleted()){
            // Notify if not notified before
            if(!pa.hasData("cmd-create-sugcont")){
                actor.sendMessageT("command.create.suggest-recovery", cm.getClan(pa).getMeta().getTag(), cm.getClan(pa).getMeta().getName());
                pa.setData("cmd-create-sugcont", new Object());
                ClansPlugin.taskScheduler().runLater(() -> pa.removeData("cmd-create-sugcont"), 2500L);
                return;
            }
            else {
                // Will remove clan as player was already notified
                cm.removeClan(cm.getClan(pa).getId());
                pa.removeData("cmd-create-sugcont");
            }
        }

        ClanManager.Result e = cm.createClan(tag, name, pa.getUniqueId());
        switch(e){
            case CLAN_TAG_EXISTS -> throw new CommandError("command.create.clan-exists");
            case LEAD_HAS_CLAN -> throw new CommandError("command.create.leave-leader");
            case CLAN_SYNC_ERROR -> throw new CommandError(null); // null = meta.command.unknown-error. See RootCommand#execute
            case SUCCESS -> actor.sendMessageT("command.create.success", tag);
            default -> LogContext.log(Level.WARN, "Unknown ClanManager return status {0}. Contact developer", e);
        }
    }

    @Override
    public String name() {
        return "create";
    }
}
