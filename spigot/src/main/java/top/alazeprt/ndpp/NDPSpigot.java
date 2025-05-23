package top.alazeprt.ndpp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import top.alazeprt.ndpp.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    public void saveNConfig() {
        File file = new File(getDataFolder(), "data.json");
        JsonObject jsonObject = new JsonObject();
        for (String key : player2IpMap.keySet()) {
            jsonObject.addProperty(key, player2IpMap.get(key));
        }
        Gson gson = new Gson();
        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        gson.toJson(jsonObject, writer);
    }

    @Override
    public void initConfig() {
        File file = new File(getDataFolder(), "data.json");
        try {
            Files.createDirectories(getDataFolder().toPath());
            if (!file.exists()) {
                file.createNewFile();
            }
            Gson gson = new Gson();
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject != null) {
                for (String key : jsonObject.keySet()) {
                    player2IpMap.put(key, jsonObject.get(key).getAsString());
                }
            }
            File configFile = new File(getDataFolder(), "config.json");
            if (!configFile.exists()) {
                file.createNewFile();
                OutputStreamWriter writer  = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8);
                writer.write("{\n  \"url\": \"https://localhost:8080/\",\n  \"token\": \"your_token_here\"\n}");
                writer.close();
            }
            InputStreamReader configReader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);
            JsonObject config = gson.fromJson(configReader, JsonObject.class);
            if (config != null && config.has("url") && config.has("token")) {
                httpUtil.url = config.get("url").getAsString();
                httpUtil.token = config.get("token").getAsString();
            } else {
                httpUtil.url = "https://localhost:8080/";
                httpUtil.token = "your_token_here";
            }
            reader.close();
            configReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        onCommand(new SpigotCommandSource(sender), args);
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        NOnlinePlayer player = new SpigotOnlinePlayer(event.getPlayer());
        onJoin(player);
    }
}
