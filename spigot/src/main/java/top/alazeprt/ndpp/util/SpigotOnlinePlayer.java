package top.alazeprt.ndpp.util;

import org.bukkit.entity.Player;

public class SpigotOnlinePlayer extends NOnlinePlayer {
    private final Player player;

    public SpigotOnlinePlayer(Player player) {
        super(player.getAddress().getAddress(), player.getName());
        this.player = player;
    }

    @Override
    public void kick(String reason) {
        player.kickPlayer(reason);
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}
