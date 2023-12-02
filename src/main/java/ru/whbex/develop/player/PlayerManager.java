package ru.whbex.develop.player;

import ru.whbex.develop.storage.PlayerStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PlayerManager {
    private final PlayerStorage pstorage;
    private final Map<UUID, CPlayer> players;
    private final CommandPerformer console;

    public PlayerManager(PlayerStorage ps, CommandPerformer console){
        this.pstorage = ps;
        players = new HashMap<>();
        this.console = console;
    }

    public void createPlayer(UUID playerId, String name){
        if(players.containsKey(playerId))
            throw new IllegalArgumentException("Player already exists!");
        players.put(playerId, new CPlayer(playerId, name));
    }
    public void removePlayer(UUID playerId){
        if(!players.containsKey(playerId))
            throw new NoSuchElementException("Player not found!");
        players.remove(playerId);
    }
    public CPlayer getPlayer(UUID playerId){
        return players.get(playerId);
    }
    public void saveProfiles(){

    }

    public CommandPerformer getConsole(){
        return console;
    }



}
