package top.alazeprt.ndpp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import top.alazeprt.ndpp.util.NBanEntry;
import top.alazeprt.ndpp.util.NOnlinePlayer;
import top.alazeprt.ndpp.util.VelocityCommandSource;
import top.alazeprt.ndpp.util.VelocityOnlinePlayer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(name = "NDPVelocity", id = "ndpvelocity", version = "1.0", authors = {"alazeprt"})
public class NDPVelocity implements NDPPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public NDPVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void saveNConfig() {
        File file = new File(dataDirectory.toFile(), "data.json");
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
        File file = new File(dataDirectory.toFile(), "data.json");
        try {
            Files.createDirectories(dataDirectory);
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
            File configFile = new File(dataDirectory.toFile(), "config.json");
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
        CommandManager commandManager = server.getCommandManager();
        CommandMeta meta = commandManager.metaBuilder("ndp").plugin(this).build();
        SimpleCommand simpleCommand = invocation -> {
            String[] args = invocation.arguments();
            onCommand(new VelocityCommandSource(invocation.source()), args);
        };
        commandManager.register(meta, simpleCommand);
    }

    @Override
    public void initListener() {}

    @Override
    public boolean isOnline(String name) {
        return server.getPlayer(name).isPresent();
    }

    @Override
    public NOnlinePlayer getPlayer(String name) {
        for (Player player : server.getAllPlayers()) {
            if (player.getUsername().equals(name)) {
                return new VelocityOnlinePlayer(player);
            }
        }
        return null;
    }

    @Override
    public void addLocalBan(NBanEntry banEntry, String sender) {}

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        enable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        disable();
    }

    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        NOnlinePlayer player = new VelocityOnlinePlayer(event.getPlayer());
        onJoin(player);
    }
}
