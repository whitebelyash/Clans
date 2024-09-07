package ru.whbex.develop.clans.common.misc.requests;

import ru.whbex.develop.clans.common.player.PlayerActor;

public class InviteRequest implements Request {

    private final PlayerActor sender;
    private final PlayerActor target;
    private final long date = System.currentTimeMillis() / 1000L;
    private final int expire;
    public InviteRequest(PlayerActor sender, PlayerActor target, int expire){
        this.sender = sender;
        this.target = target;
        this.expire = expire;

    }
    @Override
    public long date() {
        return date;
    }

    @Override
    public int time() {
        return expire;
    }

    @Override
    public PlayerActor recipient() {
        return target;
    }

    @Override
    public PlayerActor sender() {
        return sender;
    }

    @Override
    public Type type() {
        return Type.INVITE_REQUEST;
    }

    @Override
    public String toString() {
        return "InviteRequest{" +
                "sender=" + sender.getUniqueId() +
                ", target=" + target.getUniqueId() +
                ", date=" + date +
                ", expire=" + expire +
                '}';
    }
}
