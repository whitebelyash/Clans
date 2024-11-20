package ru.whbex.develop.clans.common.cmd.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.cmd.exec.CommandUsageError;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.string.StringUtils;

public class ClanCreateCommand implements Command {
    private final ClanManager cm = ClansPlugin.Context.INSTANCE.plugin.getClanManager();
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(args.length < 2)
            throw new CommandUsageError();
        if(!actor.isPlayer())
            throw new CommandError("meta.command.player-required");
        PlayerActor pa = (PlayerActor) actor;
        // Check for clan deleted bit
        if(cm.getClan(pa) != null && cm.getClan(pa).isDeleted()){
            // Notify if not notified before
            if(!pa.hasData("cmd-create-sugcont")){
                actor.sendMessageT("command.create.suggest-recovery");
                pa.setData("cmd-create-sugcont", new Object());
                ClansPlugin.Context.INSTANCE.plugin.getTaskScheduler().runLater(() -> pa.removeData("cmd-create-sugcont"), 2500L);
                return;
            }
            else {
                // Will remove clan as player already notified
                cm.removeClan(cm.getClan(pa).getId());
            }
        }
        String tag = args[1];
        String name = StringUtils.simpleformat(Constants.CLAN_NAME_FORMAT, tag);
        ClanManager.Error e = cm.createClan(tag, name, pa.getUniqueId());
        if(e!=null){
            switch(e){
                case CLAN_TAG_EXISTS -> throw new CommandError("command.create-clan-exists");
                case LEAD_HAS_CLAN -> throw new CommandError("command.create.leave-leader");
                default -> LogContext.log(Level.WARN, "Unknown ClanManager error {0}. Contact developer", e);
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
