package ru.whbex.develop.bukkit.cmd;

import ru.whbex.develop.bukkit.misc.BukkitUtils;
import ru.whbex.develop.common.Clans;
import ru.whbex.develop.common.clan.member.Member;
import ru.whbex.develop.common.lang.LangFile;
import ru.whbex.develop.common.misc.ClanUtils;
import ru.whbex.develop.common.misc.StringUtils;
import ru.whbex.develop.common.player.CPlayer;
import ru.whbex.develop.common.player.CommandPerformer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.clan.ClanMeta;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static ru.whbex.develop.common.misc.StringUtils.simpleformat;

// Temporary commands
public class TBD implements CommandExecutor {
    private final ClanManager cm = Clans.instance().getClanManager();
    private final Map<String, BiConsumer<CommandPerformer, String[]>> cmd = new HashMap<>();
    public TBD(){
        cmd.put("list", this::list);
        cmd.put("create", this::create);
        cmd.put("seen", this::seen);
        cmd.put("look", this::seen);
        cmd.put("disband", this::disband);
        cmd.put("leave", this::leave);
        cmd.put("locale-ls", this::listlocales);
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
        } catch (CommandError error){
            String base = error.getMessage() == null ? "meta.command.unknown-error" : error.getMessage();
            if(error.hasArgs())
                p.sendMessageT(base, error.getArgs());
            else 
                p.sendMessageT(base);
        } catch (Exception e){
            p.sendMessageT("meta.command.unknown-error");
            e.printStackTrace();
        }
        return true;
    }

    private void list(CommandPerformer p, String[] args){
        if(cm.getAll().isEmpty()){
            p.sendMessageT("command.list.no-clans");
            return;
        }
        p.sendMessageT("command.list.header");
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
            throw new CommandError("meta.command.usage");
        if(!p.isPlayer())
            throw new CommandError("meta.command.player-required");
        CPlayer cp = (CPlayer) p;
        if(ClanUtils.isClanMember(cp.getPlayerId()))
            throw new CommandError("command.create.leave");
        String tag = args[1];
        String name = null;
        if(cm.clanExists(tag))
            throw new CommandError("command.create.clan-exists", tag);
        cm.createClan(tag, name, ((CPlayer) p).getPlayerId());
        p.sendMessageT("command.create.success", tag);
    }
    private void seen(CommandPerformer p, String[] args){
        if(args.length < 2)
            throw new CommandError("meta.command.usage");
        String name = args[1];
        if(Clans.instance().getPlayerManager().getPlayer(name) == null)
            throw new CommandError("meta.command.unknown-player", name);
        UUID id = Clans.instance().getPlayerManager().getPlayer(name).getPlayerId();
        final String prefix = "command.seen.";
        p.sendMessageT(prefix + "header", name);
        StringBuilder sb = new StringBuilder();
        LangFile lf = Clans.instance().getLanguage();
        sb.append(simpleformat(lf.getString(prefix+"line.uuid"), id)).append('\n');
        String clan_baseline = lf.getString(prefix+"line.clan");

        if(!cm.getMemberHolder().memberExists(id))
            sb.append(simpleformat(clan_baseline,lf.getString("meta.misc.not-exists" ))).append('\n');
        else {
            Member m = cm.getMemberHolder().getMember(id);
            sb.append(simpleformat(clan_baseline, m.getClan().getMeta().getTag())).append('\n');
            String rankl = lf.getString(m.getRank().name);
            sb.append(simpleformat(lf.getString(prefix+"line.rank"), rankl)).append("\n");
            sb.append(simpleformat(lf.getString(prefix+"line.experience"), m.getExp())).append('\n');
            sb.append(simpleformat(lf.getString(prefix+"line.killdeath"), m.getDeaths(), m.getKills())).append('\n');
        }
        p.sendMessage(sb.toString());
    }
    private void disband(CommandPerformer p, String[] args){

    }
    private void leave(CommandPerformer p, String[] args){}
    @SuppressWarnings(value = "unchecked")
    private void listlocales(CommandPerformer p, String[] args){
        try {
            Field fmap = LangFile.class.getDeclaredField("strings");
            fmap.setAccessible(true);
            Map<String, String> map = (Map<String, String>) fmap.get(Clans.instance().getLanguage());
            map.forEach((k, v) -> {
                p.sendMessage("Key: {0}, Value: {1}", k, v);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
