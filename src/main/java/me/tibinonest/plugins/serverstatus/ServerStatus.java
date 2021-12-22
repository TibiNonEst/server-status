package me.tibinonest.plugins.serverstatus;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.tibinonest.plugins.serverstatus.commands.*;
import me.tibinonest.plugins.serverstatus.listeners.*;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Plugin(id = "server-status", name = "Server Status", description = "${description}", version = "${version}", authors = {"TibiNonEst"})
public class ServerStatus {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;
    private ConfigurationNode config;
    private DiscordBot bot;

    @Inject
    public ServerStatus(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Server Status v${version} initialized!");
        handleEnable();

        server.getEventManager().register(this, new PlayerJoin(this));
        server.getEventManager().register(this, new PlayerLeave(this));

        var MainCommandMeta = server.getCommandManager().metaBuilder("serverstatus")
                .aliases("ssr", "ssreload", "srvstatus").build();
        server.getCommandManager().register(MainCommandMeta, new MainCommand(this));

        var StatusCommandMeta = server.getCommandManager().metaBuilder("status").build();
        server.getCommandManager().register(StatusCommandMeta, new StatusCommand(this));

        metricsFactory.make(this, 12069);
    }

    @Subscribe
    public void onProxyReload(ProxyReloadEvent event) {
        handleReload();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        handleDisable();
    }

    public void updateData(ArrayList<String> usersOnline) {
        bot.updateData(usersOnline);
    }

    public ArrayList<String> getPlayerNames() {
        return server.getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toCollection(ArrayList::new));
    }

    public void updateActivity(String type, String message) {
        config.getNode("status").getNode("type").setValue(type);
        config.getNode("status").getNode("message").setValue(message);
        bot.updateActivity(type, message);
        saveConfig();
    }

    private void handleEnable() {
        var config = loadConfig();

        if (config.isEmpty()) {
            logger.warn("Config file could not be loaded.");
            return;
        }

        this.config = config.get();

        bot = new DiscordBot(this.config, server.getConfiguration().getShowMaxPlayers());
        bot.updateData(getPlayerNames());
        logger.info("Bot starting!");
    }

    public void handleReload() {
        bot.disable();
        handleEnable();
    }

    private void handleDisable() {
        bot.updateData(new ArrayList<>());
        bot.disable();
    }

    private Optional<ConfigurationNode> loadConfig() {
        var file = new File(dataDirectory.toFile(), "config.yml");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.yml")), file.toPath());
            } catch (IOException e) {
                logger.warn("Unable to load default config file. " + e.getMessage());
            }
        }
        try {
            return Optional.of(YAMLConfigurationLoader.builder().setFile(file).build().load());
        } catch (IOException e) {
            logger.warn("Unable to load config file. " + e.getMessage());
        }
        return Optional.empty();
    }

    public void saveConfig() {
        var file = new File(dataDirectory.toFile(), "config.yml");
        if (!file.getParentFile().exists() || !file.exists()) loadConfig();

        try {
            YAMLConfigurationLoader.builder().setFile(file).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setIndent(2).build().save(config);
        } catch (Exception e) {
            logger.warn("Could not write to config file. " + e.getMessage());
        }
    }
}
