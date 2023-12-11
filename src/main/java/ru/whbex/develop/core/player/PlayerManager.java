package ru.whbex.develop.core.player;

import ru.whbex.develop.core.storage.PlayerStorage;

import java.util.*;

public class PlayerManager {
    private final PlayerStorage pstorage;
    private final Map<UUID, CPlayer> players;
    private final Map<String, CPlayer> byName;
    private final CommandPerformer console;

    public PlayerManager(PlayerStorage ps, CommandPerformer console){
        this.pstorage = ps;
        players = new HashMap<>();
        byName = new HashMap<>();
        this.console = console;
    }

    public void createPlayer(UUID playerId, String name){
        // TODO: Refactor
        if(players.containsKey(playerId))
            throw new IllegalArgumentException("Player already exists!");
        CPlayer c = new CPlayer(playerId, name);
        players.put(playerId, c);
        byName.put(name, c);
    }
    public void removePlayer(UUID playerId){
        if(!players.containsKey(playerId))
            throw new NoSuchElementException("Player not found!");
        CPlayer c = getPlayer(playerId);
        players.remove(playerId);
        if(byName.containsValue(c))
            byName.remove(c.getName(), c);
    }
    public CPlayer getPlayer(UUID playerId){
        return players.get(playerId);
    }
    public CPlayer getPlayer(String name){
        return byName.get(name);
    }
    public Collection<CPlayer> getPlayers(){
        return this.players.values();

    }
    public void saveProfiles(){

    }

    public CommandPerformer getConsole(){
        return console;
    }



}
