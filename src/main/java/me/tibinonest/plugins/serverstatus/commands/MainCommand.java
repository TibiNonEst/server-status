package me.tibinonest.plugins.serverstatus.commands;

import com.velocitypowered.api.command.SimpleCommand;
import me.tibinonest.plugins.serverstatus.ServerStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class MainCommand implements SimpleCommand {
    private final ServerStatus plugin;

    public MainCommand(ServerStatus plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final Invocation invocation) {
        var source = invocation.source();

        plugin.handleReload();
        source.sendMessage(Component.text("Server Status reloaded!").color(NamedTextColor.GOLD));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("serverstatus.reload");
    }
}
