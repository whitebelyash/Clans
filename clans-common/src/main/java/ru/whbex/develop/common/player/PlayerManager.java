package ru.whbex.develop.common.player;

import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.cmd.CommandActor;

import java.util.*;

public class PlayerManager {
    /*
    private final PlayerStorage pstorage;
    private final Map<UUID, ClanActor> players;
    private final Map<String, ClanActor> byName;
    private final ClansPlugin inst = ClansPlugin.Context.INSTANCE.plugin;
    private final CommandActor console;

    public PlayerManager(PlayerStorage ps){
        this.pstorage = ps;
        players = new HashMap<>();
        byName = new HashMap<>();
    }

    public void createPlayer(UUID playerId, String name){
        // TODO: Refactor
        if(players.containsKey(playerId))
            throw new IllegalArgumentException("Player already exists!");
        ClanActor c = new ClanActor(playerId, name);
        players.put(playerId, c);
        byName.put(name, c);
    }
    public void removePlayer(UUID playerId){
        if(!players.containsKey(playerId))
            throw new NoSuchElementException("Player not found!");
        ClanActor c = getPlayer(playerId);
        players.remove(playerId);
        if(byName.containsValue(c))
            byName.remove(c.getName(), c);
    }
    public ClanActor getPlayer(UUID playerId){
        return players.get(playerId);
    }
    public ClanActor getPlayer(String name){
        return byName.get(name);
    }
    public Collection<ClanActor> getPlayers(){
        return this.players.values();

    }
    public void saveProfiles(){

    }

    public CommandActor getConsole(){
        return console;
    }
     */
}
