package me.tibinonest.plugins.serverstatus.listeners;

import me.tibinonest.plugins.serverstatus.ServerStatus;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class PlayerJoin implements Listener {
    private final ServerStatus plugin;

    public PlayerJoin(ServerStatus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        plugin.updateData(plugin.getPlayerNames());
    }
}
