package me.tibinonest.plugins.serverstatus.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import me.tibinonest.plugins.serverstatus.ServerStatus;

public class PlayerJoin {
    private final ServerStatus plugin;

    public PlayerJoin(ServerStatus plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onPlayerJoin(PostLoginEvent event) {
        plugin.updateData(plugin.getPlayerNames());
    }
}
