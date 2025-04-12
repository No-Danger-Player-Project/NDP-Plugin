package top.alazeprt.ndpp.util;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BCOnlinePlayer extends NOnlinePlayer {
    private final ProxiedPlayer player;

    public BCOnlinePlayer(ProxiedPlayer player) {
        super(player.getAddress().getAddress(), player.getName());
        this.player = player;
    }

    @Override
    public void kick(String reason) {
        player.disconnect(reason);
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
