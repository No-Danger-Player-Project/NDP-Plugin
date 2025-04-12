package top.alazeprt.ndpp.util;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public class VelocityOnlinePlayer extends NOnlinePlayer {
    private final Player player;

    public VelocityOnlinePlayer(Player player) {
        super(player.getVirtualHost().get().getAddress(), player.getUsername());
        this.player = player;
    }

    @Override
    public void kick(String reason) {
        player.disconnect(Component.text(reason));
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(Component.text(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}
