package ru.whbex.develop.clans.common.cmd.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.log.LogContext;

public class ClanRecoverCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if (!actor.isPlayer())
            throw new CommandError("meta.command.player-required");
        if (!ClansPlugin.clanManager().isClanLeader(((PlayerActor) actor).getUniqueId()))
            throw new CommandError("meta.command.leadership-required");
        ClanManager cm = ClansPlugin.clanManager();
        ClanManager.Error e = cm.recoverClan(cm.getClan((PlayerActor) actor), args.length > 2 ? args[2] : null);
        switch(e){
            case CLAN_NOT_FOUND -> throw new CommandError("meta.command.unknown-clan");
            case CLAN_REC_EXISTS -> throw new CommandError("command.recover.fail-exists-self");
            case CLAN_TAG_EXISTS -> throw new CommandError("command.recover.fail-tag-taken-self");
            case SUCCESS -> actor.sendMessageT("command.recover.success-self");
            default -> LogContext.log(Level.WARN, "Unknown ClanManager return state {0}. Contact developer", e);
        }
    }

    @Override
    public String name() {
        return "recover";
    }
}
