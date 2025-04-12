package top.alazeprt.ndpp.util;

import net.md_5.bungee.api.CommandSender;

public class BCCommandSource extends NCommandSource {
    private final CommandSender commandSender;

    public BCCommandSource(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Override
    public void sendMessage(String message) {
        commandSender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }

    @Override
    public String getName() {
        return commandSender.getName();
    }
}
