package me.tibinonest.plugins.serverstatus.listeners;

import me.tibinonest.plugins.serverstatus.ServerStatus;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;

public final class PlayerLeave implements Listener {
    private final ServerStatus plugin;

    public PlayerLeave(ServerStatus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        ArrayList<String> players = plugin.getPlayerNames();
        players.remove(event.getPlayer().getDisplayName());
        plugin.updateData(players);
    }
}
