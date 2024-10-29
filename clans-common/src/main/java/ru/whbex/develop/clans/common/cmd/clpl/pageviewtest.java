package ru.whbex.develop.clans.common.cmd.clpl;

import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.lib.collections.PagedListView;

import java.util.List;
import java.util.stream.Collectors;

public class pageviewtest implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        List<Integer> l = List.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);
        actor.sendMessage("real list: " + join(l));
        PagedListView<Integer> p = new PagedListView<>(l);
        actor.sendMessage("List size: " + p.size());
        actor.sendMessage("List page amount: " + p.pageAmount());
        actor.sendMessage("List page 1: " + join(p.page(1)));
        actor.sendMessage("List page 2: " + join(p.page(2)));
    }
    private String join(List<Integer> l){
        return l.stream().map(String::valueOf).collect(Collectors.joining(", "));

    }

    @Override
    public String name() {
        return "pageview";
    }
}
