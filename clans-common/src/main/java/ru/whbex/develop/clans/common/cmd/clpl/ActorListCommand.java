package ru.whbex.develop.clans.common.cmd.clpl;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.misc.PagedListView;
import ru.whbex.develop.clans.common.player.PlayerActor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActorListCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        Collection<PlayerActor> e = ClansPlugin.Context.INSTANCE.plugin.getPlayerManager().getOnlinePlayerActors();
        List<PlayerActor> l = new ArrayList<>(e);
        PagedListView<PlayerActor> lp = new PagedListView<>(l);
        int p = args.length < 2 ? 1 : Integer.parseInt(args[1]);
        lp.page(p).forEach(v -> {
            actor.sendMessage("| id: " + v.getUniqueId() + ", name: " + v.getName());
        });
    }

    @Override
    public String name() {
        return "actorlist";
    }
}
