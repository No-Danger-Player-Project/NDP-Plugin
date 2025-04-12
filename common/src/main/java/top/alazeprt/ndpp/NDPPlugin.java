package top.alazeprt.ndpp;

import org.apache.hc.core5.http.ParseException;
import top.alazeprt.ndpp.util.HttpUtil;
import top.alazeprt.ndpp.util.NBanEntry;
import top.alazeprt.ndpp.util.NOnlinePlayer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.spi.AbstractResourceBundleProvider;

public interface NDPPlugin {
    Map<String, String> player2IpMap = new HashMap<>();

    default void enable() {
        HttpUtil.init();
        initConfig();
        initCommand();
        initListener();
    }
    
    default void disable() {
        HttpUtil.stop();
        saveConfig();
    }

    void saveConfig();

    void initConfig();

    void initCommand();

    void initListener();

    default void onJoin(NOnlinePlayer player) {
        Optional<NBanEntry> entry = HttpUtil.getBan(player.getName());
        Optional<NBanEntry> entry2 = HttpUtil.getBan(player.getAddress().getHostAddress());
        if (entry.isPresent()) {
            player.kick("[NDP] 你被禁止进入服务器! 原因: \n" + entry.get().reason());
        } else
            entry2.ifPresent(nBanEntry -> player.kick( "[NDP] 你被禁止进入服务器! 原因: \n" +
                    nBanEntry.reason()));
        player2IpMap.put(player.getName(), player.getAddress().getHostAddress());
    }

    default void onCommand(NOnlinePlayer player, String[] args) {
        if (!player.hasPermission("ndp.admin")) {
            player.sendMessage("§c你没有权限!");
        }
        if (args.length != 2 && args.length != 3) {
            player.sendMessage("§c用法: /ndp <ban/pardon> <name> [reason]");
        }
        Optional<NBanEntry> entry = HttpUtil.getBan(args[1]);
        if (!entry.isPresent() && args[0].equals("pardon")) {
            player.sendMessage("§c未查询到该记录!");
        } else if (entry.isPresent() && args[0].equals("ban")) {
            player.sendMessage("§c该记录已存在!");
        } else if (entry.isPresent() && args[0].equals("pardon")) {
            player.sendMessage("§a已向远程服务器请求删除该记录!");
            new Thread(() -> {
                try {
                    HttpUtil.removeBan(entry.get(), args.length == 3 ? args[2] : "由管理员" + player.getName() + "移除");
                } catch (IOException | ParseException e) {
                    player.sendMessage("§c在请求过程中遇到错误: " + e.getMessage());
                }
            }).start();
        } else {
            String ip = player2IpMap.getOrDefault(args[1], null);
            if (ip == null) {
                player.sendMessage("§c该玩家的 IP 未知!");
            }
            String reason = args.length == 3 ? args[2] : "由管理员" + player.getName() + "添加";
            NBanEntry banEntry = new NBanEntry(args[1], ip, reason);
            getPlayer(args[1]).kick("§c[NDP] 你被禁止进入服务器! 原因: \n" + banEntry.reason());
            player.sendMessage("§a该玩家的 IP 是: " + ip);
            addLocalBan(banEntry, player.getName());
            new Thread(() -> {
                try {
                    player.sendMessage("§a已向远程服务器请求添加该记录!");
                    HttpUtil.addBan(banEntry);
                } catch (IOException | ParseException e) {
                    player.sendMessage("§c在请求过程中遇到错误: " + e.getMessage());
                }
            }).start();
        }
    }

    boolean isOnline(String name);

    NOnlinePlayer getPlayer(String name);

    void addLocalBan(NBanEntry banEntry, String sender);
}