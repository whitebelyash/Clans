package ru.whbex.develop.clans.common.cmd.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.cmd.exec.CommandUsageError;
import ru.whbex.develop.clans.common.misc.MiscUtils;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.string.StringUtils;

import java.util.UUID;

public class ClanRecoverOtherCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(args.length < 2)
            throw new CommandUsageError();
        ClanManager cm = ClansPlugin.clanManager();
        // We can't recover other clan by tag as it does not exist in tagClans map. Using UUID
        String id_s = args[1];
        UUID id;
        if((id = StringUtils.UUIDFromString(id_s)) == null)
            throw new CommandUsageError();
        if(!cm.clanExists(id))
            throw new CommandError("meta.command.unknown-clan");
        String tag = args.length < 3 ? null : args[2];
        if(tag != null && MiscUtils.validateClanTag(tag))
            throw new CommandError("meta.clan-check.invalid-tag");
        ClanManager.Result e = cm.recoverClan(cm.getClan(id), tag, actor);
        switch(e){
            case CLAN_NOT_FOUND -> throw new CommandError("meta.command.unknown-clan");
            case CLAN_REC_EXISTS -> throw new CommandError("command.recover.fail-exists");
            case CLAN_TAG_EXISTS -> throw new CommandError("command.recover.fail-tag-taken");
            case SUCCESS -> actor.sendMessageT("command.recover.success");
            default -> LogContext.log(Level.WARN, "Unknown ClanManager return state {0}. Contact developer", e);
        }

    }


    @Override
    public String name() {
        return "recover-other";
    }
}
