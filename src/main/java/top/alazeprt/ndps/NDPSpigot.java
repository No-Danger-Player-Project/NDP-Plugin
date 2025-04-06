package top.alazeprt.ndps;

import org.apache.hc.core5.http.ParseException;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
import top.alazeprt.ndps.util.HttpUtil;
import top.alazeprt.ndps.util.NBanEntry;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NDPSpigot extends JavaPlugin implements CommandExecutor, Listener {

    public static final Map<String, String> map = new HashMap<>();
    public static FileConfiguration data;

    @Override
    public void onEnable() {
        getCommand("ndp").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        HttpUtil.init();
        File file = new File(getDataFolder(), "data.yml");
        if (!file.exists()) {
            saveResource("data.yml", false);
        }
        data = YamlConfiguration.loadConfiguration(file);
        for (String key : data.getKeys(false)) {
            map.put(key, data.getString(key));
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        HttpUtil.stop();
        for (String key : map.keySet()) {
            data.set(key, map.get(key));
        }
        try {
            data.save(new File(getDataFolder(), "data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ndp.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限!");
            return true;
        }
        if (args.length != 2 && args.length != 3) {
            sender.sendMessage(ChatColor.RED + "用法: /ndp <ban/pardon> <name> [reason]");
            return true;
        }
        Optional<NBanEntry> entry = HttpUtil.getBan(args[1]);
        if (!entry.isPresent() && args[0].equals("pardon")) {
            sender.sendMessage(ChatColor.RED + "未查询到该记录!");
            return true;
        } else if (entry.isPresent() && args[0].equals("ban")) {
            sender.sendMessage(ChatColor.RED + "该记录已存在!");
            return true;
        } else if (entry.isPresent() && args[0].equals("pardon")) {
            sender.sendMessage(ChatColor.GREEN + "已向远程服务器请求删除该记录!");
            new Thread(() -> {
                try {
                    HttpUtil.removeBan(entry.get(), args.length == 3 ? args[2] : "由管理员" + sender.getName() + "移除");
                } catch (IOException | ParseException e) {
                    sender.sendMessage(ChatColor.RED + "在请求过程中遇到错误: " + e.getMessage());
                }
            }).start();
            return false;
        } else {
            if (!Bukkit.getOfflinePlayer(args[1]).isOnline()) {
                sender.sendMessage(ChatColor.RED + "玩家不在线!");
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            InetSocketAddress address = player.getAddress();
            String ip = address.getAddress().getHostAddress();
            String reason = args.length == 3 ? args[2] : "由管理员" + sender.getName() + "添加";
            NBanEntry banEntry = new NBanEntry(player.getName(), ip, reason);
            player.kickPlayer(ChatColor.RED + "[NDP] 你被禁止进入服务器! 原因: \n" + banEntry.reason());
            sender.sendMessage(ChatColor.GREEN + "该玩家的 IP 是: " + player.getAddress().getAddress().getHostAddress());
            BanList banList = Bukkit.getBanList(BanList.Type.NAME);
            banList.addBan(player.getName(), reason, (Date) null, sender.getName());
            BanList banList1 = Bukkit.getBanList(BanList.Type.IP);
            banList1.addBan(player.getAddress().getAddress().getHostAddress(), reason, (Date) null, sender.getName());
            new Thread(() -> {
                try {
                    sender.sendMessage(ChatColor.GREEN + "已向远程服务器请求添加该记录!");
                    HttpUtil.addBan(banEntry);
                } catch (IOException | ParseException e) {
                    sender.sendMessage(ChatColor.RED + "在请求过程中遇到错误: " + e.getMessage());
                }
            }).start();
            return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Optional<NBanEntry> entry = HttpUtil.getBan(player.getName());
        Optional<NBanEntry> entry2 = HttpUtil.getBan(player.getAddress().getAddress().getHostAddress());
        if (entry.isPresent()) {
            player.kickPlayer(ChatColor.RED + "[NDP] 你被禁止进入服务器! 原因: \n" + entry.get().reason());
        } else
            entry2.ifPresent(nBanEntry -> player.kickPlayer(ChatColor.RED + "[NDP] 你被禁止进入服务器! 原因: \n" +
                    nBanEntry.reason()));
    }
}
