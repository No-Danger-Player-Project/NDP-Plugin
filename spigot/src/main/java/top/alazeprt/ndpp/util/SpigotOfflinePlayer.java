package top.alazeprt.ndpp.util;

import org.bukkit.OfflinePlayer;

public class SpigotOfflinePlayer extends NOfflinePlayer {
    public SpigotOfflinePlayer(OfflinePlayer player) {
        super(player.getName());
    }
}
