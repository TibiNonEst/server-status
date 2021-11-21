package me.tibinonest.plugins.serverstatus;

import me.tibinonest.plugins.serverstatus.commands.*;
import me.tibinonest.plugins.serverstatus.listeners.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public final class ServerStatus extends Plugin {
    private Configuration configuration, statusConfig;
    private DiscordBot bot;

    @Override
    public void onEnable() {
        handleEnable();

        getProxy().getPluginManager().registerListener(this, new PlayerJoin(this));
        getProxy().getPluginManager().registerListener(this, new PlayerLeave(this));
        getProxy().getPluginManager().registerCommand(this, new MainCommand(this));
        getProxy().getPluginManager().registerCommand(this, new StatusCommand(this));

        Metrics metrics = new Metrics(this, 12069);
    }

    @Override
    public void onDisable() {
        handleDisable();
    }

    public void updateData(ArrayList<String> usersOnline) {
        bot.updateData(usersOnline);
    }

    public ArrayList<String> getPlayerNames() {
        ArrayList<String> players = new ArrayList<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) players.add(player.getDisplayName());
        return players;
    }

    public void updateActivity(String type, String message) {
        bot.updateActivity(type, message);
    }

    private void handleEnable() {
        loadConfig();
        loadStatus();

        ConfigData data = new ConfigData(
                configuration.getString("text-message"),
                configuration.getString("voice-message"),
                configuration.getInt("max-players"),
                configuration.getStringList("channels.text"),
                configuration.getStringList("channels.voice"),
                statusConfig.getString("type"),
                statusConfig.getString("message")
        );

        bot = new DiscordBot(configuration.getString("token"), data);
        bot.updateData(new ArrayList<>());
    }

    private void handleDisable() {
        HashMap<String, String> status = bot.disable();
        statusConfig.set("type", status.get("type"));
        statusConfig.set("message", status.get("message"));
        writeStatus();
    }

    public void handleReload() {
        handleDisable();
        handleEnable();
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStatus() {
        File file = new File(getDataFolder(), "status.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("status.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            statusConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "status.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeStatus() {
        File file = new File(getDataFolder(), "status.yml");
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("status.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(statusConfig, new File(getDataFolder(), "status.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
