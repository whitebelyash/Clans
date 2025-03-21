package ru.whbex.develop.clans.common.cmd.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.log.LogContext;

public class ClanDisbandCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(args.length < 2){
            // self execute
            if(!(actor instanceof PlayerActor))
                throw new CommandError("meta.command.player-required");
            PlayerActor pa = (PlayerActor) actor;
            execute_self(pa);
        } else {
            // other
            // TODO: Add permission check
            ClanManager.Result e = ClansPlugin.clanManager().disbandClan(args[1], actor);
            switch (e){
                case CLAN_NOT_FOUND -> throw new CommandError("meta.command.unknown-clan");
                case CLAN_ALR_DISBAND -> throw new CommandError("command.disband.fail-deleted");
                case SUCCESS -> actor.sendMessageT("command.disband.success");
                default -> LogContext.log(Level.WARN, "Unknown ClanManager return status {0}. Contact developer", e);
            }
        }

    }


    // snake_case in java :skull:
    private void execute_self(PlayerActor pa){
        if(!ClansPlugin.clanManager().isClanLeader(pa.getUniqueId()))
            throw new CommandError("meta.command.leadership-required");
        ClanManager.Result e = ClansPlugin.clanManager().disbandClan(ClansPlugin.clanManager().getClan(pa), (CommandActor) pa);
        switch(e){
            case CLAN_NOT_FOUND -> throw new CommandError("meta.command.leadership-required");
            case CLAN_ALR_DISBAND -> throw new CommandError("command.disband.fail-deleted-self");
            case SUCCESS -> ((CommandActor) pa).sendMessageT("command.disband.success-self"); // TODO: Fix this after adding components
            default -> LogContext.log(Level.WARN, "Unknown ClanManager error {0}. Contact developer", e);
        }

    }

    @Override
    public String name() {
        return "disband";
    }
}
