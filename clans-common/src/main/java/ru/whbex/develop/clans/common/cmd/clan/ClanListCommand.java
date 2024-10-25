package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.clan.ClanMeta;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.CommandActor;

public class ClanListCommand implements Command {
    private final ClanManager cm = ClansPlugin.Context.INSTANCE.plugin.getClanManager();
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(cm.getAllClans().isEmpty()){
            actor.sendMessageT("command.list.no-clans");
            return;
        }
        actor.sendMessageT("command.list.header");
        StringBuilder msg = new StringBuilder();
        actor.sendMessage("&cisDeleted");
        actor.sendMessage(" ");
        cm.getAllClans().forEach(c -> {
            ClanMeta m = c.getMeta();
            if(c.isDeleted())
                msg.append("&c");
            msg.append(String.join(", ", c.getId().toString(), m.getTag(), m.getName(),
                    String.valueOf(c.getLevelling().getLevel()), String.valueOf(m.getCreationTime()), m.getLeader().toString()));
            msg.append('\n');
        });

        actor.sendMessage(msg.toString());
    }

    @Override
    public String name() {
        return "list";
    }
}
