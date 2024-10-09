package ru.whbex.develop.clans.common.cmd;

public interface ActorProxy<T> {

    CommandActor asActor(T performer);
}
