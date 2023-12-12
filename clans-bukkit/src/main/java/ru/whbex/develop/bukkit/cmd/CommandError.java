package ru.whbex.develop.bukkit.cmd;

public class CommandError extends RuntimeException {
    private final String message;
    private final Object[] args;

    CommandError(String message, Object... args){
        this.message = message;
        this.args = args;
    }

    CommandError(String message){
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
