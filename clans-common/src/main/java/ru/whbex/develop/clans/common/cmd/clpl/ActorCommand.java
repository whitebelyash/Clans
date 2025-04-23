package ru.whbex.develop.clans.common.cmd.clpl;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.cmd.exec.CommandUsageError;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.string.StringUtils;

import java.util.UUID;

public class ActorCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(args.length < 2) throw new CommandUsageError();
        PlayerActor target;
        if((target = ClansPlugin.playerManager().resolvePlayerActor(args[1])) != null)
            actor.sendMessage("Resolved actor from name " + args[1]);
        else {
            UUID uuid;
            if((uuid = StringUtils.UUIDFromString(args[1])) == null) throw new CommandUsageError();
            if((target = ClansPlugin.playerManager().playerActor(uuid)) != null)
                actor.sendMessage("Resolved actor from uuid " + args[1]);
        }
        if(target == null) throw new CommandError(null);
        if(target.getProfile() == null){
            actor.sendMessage("Provided actor doesn't have PlayerProfile");
            return;
        }
        actor.sendMessage("--- Actor info ---");
        UUID id = target.getProfile().getOwner(); actor.sendMessage(" - ID: " + id);
        String name = target.getProfile().getName(); actor.sendMessage("- Name: " + name);
        String oname = target.getOnlineName(); actor.sendMessage("- Online Name: " + oname);
        UUID cid = target.getProfile().getClanId(); actor.sendMessage(" - Bound Clan: " + cid);
        long reg = target.getProfile().getRegDate(); actor.sendMessage("- Reg Date: " + reg);
        long seen = target.getProfile().getLastSeen(); actor.sendMessage("- Last Seen: " + seen);
        boolean fetch = target.getFetcher() != null && target.getFetcher().isDone(); actor.sendMessage("- PP Fetch in progress: " + fetch);
    }

    @Override
    public String name() {
        return "actor";
    }
}
