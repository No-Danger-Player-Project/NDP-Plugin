package top.alazeprt.ndpp.util;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.text.Component;

public class VelocityCommandSource extends NCommandSource {
    private final CommandSource source;

    public VelocityCommandSource(CommandSource source) {
        this.source = source;
    }

    @Override
    public void sendMessage(String message) {
        source.sendMessage(Component.text(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public String getName() {
        if (source instanceof Player player) {
            return player.getUsername();
        } else {
            return source.getOrDefault(Pointer.pointer(String.class, Key.key("name")), "CONSOLE");
        }
    }
}
