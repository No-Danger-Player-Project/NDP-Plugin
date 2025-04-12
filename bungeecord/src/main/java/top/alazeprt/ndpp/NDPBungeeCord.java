package top.alazeprt.ndpp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import top.alazeprt.ndpp.util.BCCommandSource;
import top.alazeprt.ndpp.util.BCOnlinePlayer;
import top.alazeprt.ndpp.util.NBanEntry;
import top.alazeprt.ndpp.util.NOnlinePlayer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class NDPBungeeCord extends Plugin implements NDPPlugin, Listener {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    @Override
    public void initCommand() {
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Command("ndp") {
            @Override
            public void execute(CommandSender commandSender, String[] strings) {
                onCommand(new BCCommandSource(commandSender), strings);
            }
        });
    }

    @Override
    public void initListener() {
        getProxy().getPluginManager().registerListener(this, this);
    }

    @Override
    public boolean isOnline(String name) {
        for (ProxiedPlayer player : getProxy().getPlayers()) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NOnlinePlayer getPlayer(String name) {
        return new BCOnlinePlayer(getProxy().getPlayer(name));
    }

    @Override
    public void addLocalBan(NBanEntry banEntry, String sender) {}

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        onJoin(new BCOnlinePlayer(event.getPlayer()));
    }
}
