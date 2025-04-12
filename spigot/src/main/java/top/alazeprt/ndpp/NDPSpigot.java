package top.alazeprt.ndpp;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import top.alazeprt.ndpp.util.NBanEntry;
import top.alazeprt.ndpp.util.NOnlinePlayer;
import top.alazeprt.ndpp.util.SpigotOnlinePlayer;

import java.io.File;
import java.util.Date;

public class NDPSpigot extends JavaPlugin implements NDPPlugin, CommandExecutor, Listener {

    public static FileConfiguration data;

    @Override
    public void onEnable() {
        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    @Override
    public void initConfig() {
        File file = new File(getDataFolder(), "data.yml");
        if (!file.exists()) {
            saveResource("data.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(file);
        for (String key : data.getKeys(false)) {
            player2IpMap.put(key, data.getString(key));
        }
    }

    @Override
    public void initCommand() {
        getCommand("ndp").setExecutor(this);
    }

    @Override
    public void initListener() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean isOnline(String name) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList().contains(name);
    }

    @Override
    public NOnlinePlayer getPlayer(String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equals(name)) {
                return new SpigotOnlinePlayer(player);
            }
        }
        return null;
    }

    @Override
    public void addLocalBan(NBanEntry banEntry, String sender) {
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        banList.addBan(banEntry.name(), banEntry.reason(), (Date) null, sender);
        BanList banList1 = Bukkit.getBanList(BanList.Type.IP);
        banList1.addBan(banEntry.ip(), banEntry.reason(), (Date) null, sender);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            onCommand(new SpigotOnlinePlayer(player), args);
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        NOnlinePlayer player = new SpigotOnlinePlayer(event.getPlayer());
        onJoin(player);
    }
}
