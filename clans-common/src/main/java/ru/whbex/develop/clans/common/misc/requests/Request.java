package ru.whbex.develop.clans.common.misc.requests;

import ru.whbex.develop.clans.common.player.PlayerActor;

public interface Request {

    enum Type {
        INVITE_REQUEST,
        ALLY_REQUEST,
        UNRIVAL_REQUEST
    }
    // unix epoch
    long date();
    // seconds until expire
    int time();
    default boolean hasExpired(){
        return System.currentTimeMillis() / 1000L > date() + time();
    }

    PlayerActor recipient();
    PlayerActor sender();
    Type type();
}
