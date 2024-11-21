package ru.whbex.develop.clans.common.cmd.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.cmd.exec.CommandUsageError;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.log.LogContext;

public class ClanDisbandCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        // WIP
        boolean self = false;
        if(args.length < 2)
            self = true;
        PlayerActor pa = (PlayerActor) actor;
        if(self){
            if(ClansPlugin.Context.INSTANCE.plugin.getClanManager().getClan(pa) == null)
                throw new CommandError("meta.command.leadership-needed");
        }
        Clan clan = self ? ClansPlugin.Context.INSTANCE.plugin.getClanManager().getClan(pa) :
                            ClansPlugin.Context.INSTANCE.plugin.getClanManager().getClan(args[0]);
        if(clan == null)
            throw new CommandError("meta.command.unknown-clan");
        ClanManager.Error e = ClansPlugin.Context.INSTANCE.plugin.getClanManager().disbandClan(clan);
        if(e!=null)
            switch (e){
                case CLAN_NOT_FOUND -> throw new CommandError("meta.command.unknown-clan");
                case CLAN_ALR_DISBAND -> throw new CommandError(self ? "command.disband.fail-deleted" : "command.disband.fail-deleted-self");
                default -> LogContext.log(Level.WARN, "Unknown ClanManager error {0}. Contact developer", e);
            }
        actor.sendMessageT(self ? "command.disband.success-self" : "command.disband.success");
    }

    @Override
    public String name() {
        return "disband";
    }
}
