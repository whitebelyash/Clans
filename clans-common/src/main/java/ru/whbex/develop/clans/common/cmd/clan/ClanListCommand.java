package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.clan.ClanMeta;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.misc.MiscUtils;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.collections.PagedListView;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.string.StringUtils;

import java.util.ArrayList;

public class ClanListCommand implements Command {
    private final ClanManager cm = ClansPlugin.clanManager();
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(cm.getAllClans().isEmpty()){
            actor.sendMessageT("command.list.no-clans");
            return;
        }
        int p = 1;
        if(args.length > 2)
            p = StringUtils.parseInt(args[2], 1);

        actor.sendMessageT("command.list.header");
        PagedListView<Clan> pviewer = new PagedListView<>(new ArrayList<>(ClansPlugin.clanManager().getClans()));
        Debug.print("pageview: {0}", pviewer.page(p).size());
        pviewer.page(p > pviewer.pageAmount() ? 1 : p).forEach(clan -> {
            String entry_t;
            if(actor instanceof PlayerActor && clan.isMember((PlayerActor) actor))
                entry_t = "command.list.entry-self";
            else entry_t = "command.list.entry";
            PlayerActor lead = ClansPlugin.playerManager().getPlayerActor(clan.getMeta().getLeader());
            actor.sendMessage(StringUtils.simpleformat(ClansPlugin.mainLanguage().getPhrase(entry_t), clan.getMeta().getTag(), clan.getMeta().getName(), lead.getName()));
        });
        if(pviewer.pageAmount() > 1)
            actor.sendMessageT(MiscUtils.makeNavPane(ClansPlugin.mainLanguage(), '/' + command.name() + " " + this.name() + " [страница]", pviewer, p));

    }

    @Override
    public String name() {
        return "list";
    }
}
