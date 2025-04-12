package top.alazeprt.ndpp.util;

import org.bukkit.command.CommandSender;

public class SpigotCommandSource extends NCommandSource {
    private final CommandSender sender;

    public SpigotCommandSource(CommandSender sender) {
        this.sender = sender;
    }


    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public String getName() {
        return sender.getName();
    }
}
