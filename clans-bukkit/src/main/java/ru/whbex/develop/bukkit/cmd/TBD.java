package ru.whbex.develop.bukkit.cmd;

import org.bukkit.Bukkit;
import ru.whbex.develop.bukkit.MainBukkit;
import ru.whbex.develop.bukkit.misc.BukkitUtils;
import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.clan.member.Member;
import ru.whbex.develop.common.lang.LangFile;
import ru.whbex.develop.common.lang.Language;
import ru.whbex.develop.common.misc.ClanUtils;
import ru.whbex.develop.common.cmd.CommandActor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.clan.ClanMeta;
import ru.whbex.develop.common.player.PlayerActor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static ru.whbex.develop.common.misc.StringUtils.simpleformat;

// Temporary commands
public class TBD implements CommandExecutor {
    private final MainBukkit main = (MainBukkit) ClansPlugin.Context.INSTANCE.plugin;
    private final ClanManager cm = ClansPlugin.Context.INSTANCE.plugin.getClanManager();
    private final Map<String, BiConsumer<CommandActor, String[]>> cmd = new HashMap<>();
    public TBD(){
        cmd.put("list", this::list);
        cmd.put("create", this::create);
        cmd.put("seen", this::seen);
        cmd.put("look", this::seen);
        cmd.put("disband", this::disband);
        cmd.put("leave", this::leave);
        cmd.put("locale-ls", this::listlocales);
        cmd.put("locale-reload", this::localereload);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        CommandActor p = main.asCommandActor(commandSender);
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

    private void list(CommandActor p, String[] args){
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
    private void create(CommandActor p, String[] args){
        if(args.length < 2)
            throw new CommandError("meta.command.usage");
        if(!p.isPlayer())
            throw new CommandError("meta.command.player-required");
        PlayerActor pa = (PlayerActor) p;
        if(ClanUtils.isClanMember(pa.getUniqueId()))
            throw new CommandError("command.create.leave");
        String tag = args[1];
        String name = null;
        if(cm.clanExists(tag))
            throw new CommandError("command.create.clan-exists", tag);
        cm.createClan(tag, name, ((PlayerActor) p).getUniqueId());
        p.sendMessageT("command.create.success", tag);
    }
    private void seen(CommandActor p, String[] args){
        if(args.length < 2)
            throw new CommandError("meta.command.usage");
        String name = args[1];
        if(main.getPlayerActor(name) == null)
            throw new CommandError("meta.command.unknown-player", name);
        UUID id = main.getPlayerActor(name).getUniqueId();
        final String prefix = "command.seen.";
        p.sendMessageT(prefix + "header", name);
        StringBuilder sb = new StringBuilder();
        Language lang = main.getLanguage();
        sb.append(simpleformat(lang.getPhrase(prefix+"line.uuid"), id)).append('\n');
        String clan_baseline = lang.getPhrase(prefix+"line.clan");

        if(!cm.getMemberHolder().memberExists(id))
            sb.append(simpleformat(clan_baseline,lang.getPhrase("meta.misc.not-exists" ))).append('\n');
        else {
            Member m = cm.getMemberHolder().getMember(id);
            sb.append(simpleformat(clan_baseline, m.getClan().getMeta().getTag())).append('\n');
            String rankl = lang.getPhrase(m.getRank().name);
            sb.append(simpleformat(lang.getPhrase(prefix+"line.rank"), rankl)).append("\n");
            sb.append(simpleformat(lang.getPhrase(prefix+"line.experience"), m.getExp())).append('\n');
            sb.append(simpleformat(lang.getPhrase(prefix+"line.killdeath"), m.getDeaths(), m.getKills())).append('\n');
        }
        p.sendMessage(sb.toString());
    }
    private void disband(CommandActor p, String[] args){

    }
    private void leave(CommandActor p, String[] args){}
    @SuppressWarnings(value = "unchecked")
    private void listlocales(CommandActor p, String[] args){
        try {
            // Language#getPhrases exists i know... i know.
            Field fmap = Language.class.getDeclaredField("phrases");
            fmap.setAccessible(true);
            Map<String, String> map = (Map<String, String>) fmap.get(main.getLanguage());
            map.forEach((k, v) -> {
                p.sendMessage("Key: {0}, Value: {1}", k, v);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void localereload(CommandActor p, String[] args){

    }
}
