package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.RootCommand;

public abstract class ClanCommand<T> extends RootCommand<T> {

    @Override
    protected void registerAll() {
        register(new ClanCreateCommand());
        register(new ClanListCommand());
        register(new ClanDeleteCommand());
        register(new ClanDisbandCommand());
        register(new ClanRecoverOtherCommand());
        register(new ClanRecoverCommand());
        register(new ClanAcceptAllyCommand());
        register(new ClanDenyAllyCommand());
        register(new ClanInviteCommand());
        register(new ClanAcceptInviteCommand());
        register(new ClanDenyInviteCommand());
        register(new ClanRivalCommand());
        register(new ClanAllyCommand());
        register(new ClanAcceptUnrivalCommand());
        register(new ClanManageCommand());
        register(new ClanPlaceCommand());
        register(new ClanProfileCommand());
        register(new ClanEditTagCommand());
        register(new ClanEditNameCommand());
        register(new ClanEditDescrCommand());
        register(new ClanPvpToggleCommand());
        register(new ClanSetRankCommand());
        register(new ClanSetRoleCommand());
        register(new ClanWelcomeCommand());


        register(new ClanHelpCommand(cmds.values()));
    }

    protected void root(CommandActor a){
        cmds.get("help").execute(a, cmds.get("help"), "help");
    }
    @Override
    public String name() {
        return "clan";
    }




}
