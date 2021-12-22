package me.tibinonest.plugins.serverstatus.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import me.tibinonest.plugins.serverstatus.ServerStatus;

import java.util.ArrayList;

public class PlayerLeave {
    private final ServerStatus plugin;

    public PlayerLeave(ServerStatus plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onPlayerLeave(DisconnectEvent event) {
        ArrayList<String> players = plugin.getPlayerNames();
        plugin.updateData(players);
    }
}
