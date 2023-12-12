package ru.whbex.develop.bukkit.cmd;

import ru.whbex.develop.bukkit.misc.BukkitUtils;
import ru.whbex.develop.common.Clans;
import ru.whbex.develop.common.clan.member.Member;
import ru.whbex.develop.common.misc.ClanUtils;
import ru.whbex.develop.common.misc.StringUtils;
import ru.whbex.develop.common.player.CPlayer;
import ru.whbex.develop.common.player.CommandPerformer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.clan.ClanMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

// Temporary commands
public class TBD implements CommandExecutor {
    private final ClanManager cm = Clans.instance().getClanManager();
    private final Map<String, BiConsumer<CommandPerformer, String[]>> cmd = new HashMap<>();
    public TBD(){
        cmd.put("list", this::list);
        cmd.put("create", this::create);
        cmd.put("seen", this::seen);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        CommandPerformer p = BukkitUtils.asPerformer(commandSender);
        if(strings.length < 1 || !cmd.containsKey(strings[0])){
            p.sendMessage("/clans " + String.join("|", cmd.keySet()));
            return true;
        }
        try {
            cmd.get(strings[0]).accept(p, strings);
        } catch (RuntimeException e){
            p.sendMessage(e.getMessage());
        }
        return true;
    }

    private void list(CommandPerformer p, String[] args){
        if(cm.getAll().isEmpty()){
            p.sendMessage("No clans");
            return;
        }
        p.sendMessage("=== Clan list ===\nUUID, Tag, Name, Level, Creation Date, LeaderID");
        StringBuilder msg = new StringBuilder();


        cm.getAll().forEach(c -> {
            ClanMeta m = c.getMeta();
            msg.append(String.join(", ", c.getId().toString(), m.getTag(), m.getName(),
                    String.valueOf(c.getLevelling().getLevel()), String.valueOf(m.getCreationTime()), m.getLeader().toString()));
            msg.append('\n');
        });

        p.sendMessage(msg.toString());
    }
    private void create(CommandPerformer p, String[] args){
        if(args.length < 2)
            throw new RuntimeException("Invalid usage");
        if(!p.isPlayer())
            throw new RuntimeException("Must be a player");
        CPlayer cp = (CPlayer) p;
        if(ClanUtils.isClanMember(cp.getPlayerId()))
            throw new RuntimeException("Leave from your clan to create new");
        String tag = args[1];
        String name = null;
        if(cm.clanExists(tag))
            throw new RuntimeException("Clan exists: " + tag);
        cm.createClan(tag, name, ((CPlayer) p).getPlayerId());
        p.sendMessage("Success, maybe. Check /clans list");
    }
    private void seen(CommandPerformer p, String[] args){
        if(args.length < 2)
            throw new RuntimeException("Invalid usage");
        String name = args[1];
        if(Clans.instance().getPlayerManager().getPlayer(name) == null)
            throw new RuntimeException("Unknown player: " + name);
        UUID id = Clans.instance().getPlayerManager().getPlayer(name).getPlayerId();
        p.sendMessage(StringUtils.simpleformat("=== {0}'s clan profile ===\n", name));
        StringBuilder sb = new StringBuilder();
        sb.append("UUID: " + id).append('\n');
        sb.append("Clan: ");
        if(!cm.getMemberHolder().memberExists(id))
            sb.append("Player isn't a clan member").append('\n');
        else {
            Member m = cm.getMemberHolder().getMember(id);
            sb.append(m.getClan().getMeta().getTag()).append('\n');
            sb.append("Rank: " + m.getRank()).append("\n");
            sb.append("Experience: " + m.getExp()).append('\n');
            sb.append("Deaths/Kills: " + m.getDeaths() + '/' + m.getKills()).append('\n');
        }
        p.sendMessage(sb.toString());

    }
}
