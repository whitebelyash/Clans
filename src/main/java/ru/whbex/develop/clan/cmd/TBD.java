package ru.whbex.develop.clan.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.whbex.develop.Clans;
import ru.whbex.develop.bukkit.BukkitUtils;
import ru.whbex.develop.clan.ClanManager;
import ru.whbex.develop.clan.ClanMeta;
import ru.whbex.develop.lang.LocaleString;
import ru.whbex.develop.player.CPlayer;
import ru.whbex.develop.player.CommandPerformer;

import java.util.Objects;
import java.util.StringJoiner;

public class TBD implements CommandExecutor {
    private final ClanManager cm = Clans.instance().getClanManager();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equals("clans"))
            clans(commandSender, command, strings);

        return true;
    }
    private void clans(CommandSender s, Command command, String[] args){
        if(args.length < 1){
            s.sendMessage("Not enough args");
            return;
        }
        if(!(s instanceof Player)){
            s.sendMessage("Player required");
            return;
        }
        CPlayer p = (CPlayer) BukkitUtils.asPerformer(s);
        Objects.requireNonNull(p, "CPlayer is null!");
        switch(args[0]){
            case "create":
                if(args.length < 2){
                    p.sendMessage(LocaleString.META_COMMAND_USAGE, LocaleString.COMMAND_CREATE_USAGE);
                    return;
                }
                String tag = args[1];
                String name = args.length >= 3 ? args[2] : null;
                try {
                    cm.createClan(tag, name, p.getPlayerId());
                } catch (IllegalArgumentException ex){
                    p.sendMessage(LocaleString.COMMAND_CREATE_TAG_TAKEN);
                    return;
                }
                p.sendMessage(LocaleString.COMMAND_CREATE_SUCCESS);
                break;
            case "list":
                p.sendMessage("Clan list");
                cm.getAll().forEach(c -> {
                    ClanMeta meta = c.getMeta();
                    p.sendMessage(String.join(", ", meta.getTag(), meta.getName(), meta.getLeader().toString(), String.valueOf(meta.getCreationTime())));
                });
                break;
            case "disband":
                break;
            case "test-messaging":


        }

    }
}
