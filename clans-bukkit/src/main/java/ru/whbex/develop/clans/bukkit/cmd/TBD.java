package ru.whbex.develop.clans.bukkit.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.whbex.develop.clans.bukkit.MainBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.clan.ClanMeta;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.CommandError;
import ru.whbex.develop.clans.common.lang.Language;
import ru.whbex.develop.clans.common.misc.ClanUtils;
import ru.whbex.develop.clans.common.misc.StringUtils;
import ru.whbex.develop.clans.common.player.PlayerActor;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

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
        cmd.put("actor-ls", this::listactors);
        cmd.put("status", this::status);
        cmd.put("breakcon", this::breakcon);
        cmd.put("conn", this::conn);
        cmd.put("setlvl", this::setlvl);
        cmd.put("addexp", this::addexp);
        cmd.put("export", this::export);
        cmd.put("import", this::_import);
        cmd.put("profile", this::profile);
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
        if(cm.getClans().isEmpty()){
            p.sendMessageT("command.list.no-clans");
            return;
        }
        p.sendMessageT("command.list.header");
        StringBuilder msg = new StringBuilder();
        p.sendMessage("&cisDeleted");
        p.sendMessage(" ");
        cm.getClans().forEach(c -> {
            ClanMeta m = c.getMeta();
            if(c.isDeleted())
                msg.append("&c");
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
        String name = StringUtils.simpleformat(Constants.CLAN_NAME_FORMAT, tag);
        if(cm.clanExists(tag))
            throw new CommandError("command.create.clan-exists", tag);
        cm.createClan(tag, name, ((PlayerActor) p).getUniqueId());
        p.sendMessageT("command.create.success", tag);
    }
    private void seen(CommandActor p, String[] args){
        /*
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

         */
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

    private void listactors(CommandActor p, String[] args){
        p.sendMessage("--- Online actors ---");
        main.getPlayerManager().getOnlinePlayerActors().forEach(a -> {
            p.sendMessage(a.toString() + '\n');
        });
        p.sendMessage(" ---");
    }
    private void status(CommandActor p, String[] args) {
        // TODO: PRETTY EXPERIMNETAL, IMPLEMENT PROPER ASYNC!!!!!!
        p.sendMessage("--- Clans Status ---");
        boolean db;
        try {
            db = !ClansPlugin.Context.INSTANCE.plugin.getSQLAdapter().isClosed() && ClansPlugin.Context.INSTANCE.plugin.getSQLAdapter().isValid();
        } catch (SQLException e) {
            db = false;
            ClansPlugin.dbg("db check failed !!!");
            ClansPlugin.dbg_printStacktrace(e);
        }
        p.sendMessage("| db: " + (db ? "ok" : "failed"));
    }
    private void breakcon(CommandActor p, String[] args){
        p.sendMessage("Breaking db conn");
        try {
            ClansPlugin.Context.INSTANCE.plugin.getSQLAdapter().disconnect();
        } catch (SQLException e) {
            p.sendMessage("FAIL!");
            ClansPlugin.dbg("Disconnect fail");
            ClansPlugin.dbg_printStacktrace(e);
        }
        p.sendMessage("Success!");
    }
    private void conn(CommandActor p, String[] args){
        p.sendMessage("Connect to db");
        try {
            ClansPlugin.Context.INSTANCE.plugin.getSQLAdapter().connect();
        } catch (SQLException e) {
            p.sendMessage("FAIL!");
            ClansPlugin.dbg("Connect fail");
            ClansPlugin.dbg_printStacktrace(e);
        }
        p.sendMessage("Success!");
    }
    private void setlvl(CommandActor p, String[] args){
        if(args.length < 3)
            throw new CommandError("meta.command.usage");
        String tag = args[1];
        if(!cm.clanExists(tag))
            throw new CommandError("meta.command.unknown-clan");
        int n = 0;
        try {
            n = Integer.parseInt(args[2]);
        } catch (NumberFormatException e){
            throw new CommandError("meta.command.usage");
        }
        this.cm.getClan(tag).getLevelling().setLevel(n, true);
        p.sendMessage("Set level {0} for clan {1}", n, tag);
    }
    private void addexp(CommandActor p, String[] args){
        if(args.length < 3)
            throw new CommandError("meta.command.usage");
        String tag = args[1];
        if(!cm.clanExists(tag))
            throw new CommandError("meta.command.unknown-clan");
        int n = 0;
        try {
            n = Integer.parseInt(args[2]);
        } catch (NumberFormatException e){
            throw new CommandError("meta.command.usage");
        }
        this.cm.getClan(tag).getLevelling().addExperience(n);
        p.sendMessage("Add exp amount {0} for clan {1}", n, tag);
    }
    private void export(CommandActor p, String[] args){
        if(args.length < 2)
            throw new CommandError("meta.command.usage");
        String tag = args[1];
        if(!cm.clanExists(tag))
            throw new CommandError("meta.command.unknown-clan");
        cm.tmpExportClan(cm.getClan(tag));
    }
    private void _import(CommandActor p, String[] args){
        if(args.length < 2)
            throw new CommandError("meta.command.usage");
        String tag = args[1];
        if(cm.clanExists(tag))
            throw new CommandError("already loaded");
        cm.tmpImportClan(tag);
    }
    private void profile(CommandActor p, String[] args){
        if(args.length < 2)
            throw new CommandError("meta.command.usage");
        String tag = args[1];
        if(!cm.clanExists(tag))
            throw new CommandError("meta.command.unknown-clan");
        Clan c = cm.getClan(tag);
        p.sendMessage("--- Clan profile ---");
        p.sendMessage("- tag: {0}", c.getMeta().getTag());
        p.sendMessage("- name: {0}", c.getMeta().getName());
        p.sendMessage("- descr: {0}", c.getMeta().getDescription());
        p.sendMessage("- created on: {0}", StringUtils.epochAsString(null, c.getMeta().getCreationTime()));
        p.sendMessage("- default rank: {0}", c.getMeta().getDefaultRank());
        PlayerActor leader = ClansPlugin.Context.INSTANCE.plugin.getPlayerManager().getPlayerActor(c.getMeta().getLeader());
        p.sendMessage("- leader: {0}", leader.getName());
        p.sendMessage("- lvl/exp: {0}/{1}", c.getLevelling().getLevel(), c.getLevelling().getExperience());
        p.sendMessage("--------------------");
    }
}
