package ru.whbex.develop.clans.common.cmd;

public class CommandError extends RuntimeException {
    private final String message;
    private final Object[] args;

    public CommandError(String message, Object... args){
        this.message = message;
        this.args = args;
    }

    public CommandError(String message){
        this(message, new Object[0]);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Object[] getArgs() {
        return args;
    }
    public boolean hasArgs(){
        return args.length > 0;
    }
}
