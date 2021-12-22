package me.tibinonest.plugins.serverstatus.commands;

import com.velocitypowered.api.command.SimpleCommand;
import me.tibinonest.plugins.serverstatus.ServerStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class StatusCommand implements SimpleCommand {
    private final ServerStatus plugin;
    private final HashMap<String, String> display;

    public StatusCommand(ServerStatus plugin) {
        this.plugin = plugin;

        display = new HashMap<>(4);
        display.put("PLAYING", "Playing ");
        display.put("LISTENING", "Listening to ");
        display.put("WATCHING", "Watching ");
        display.put("COMPETING", "Competing in ");
    }

    @Override
    public void execute(final Invocation invocation) {
        var source = invocation.source();
        var args = invocation.arguments();

        if (args.length < 2) {
            source.sendMessage(Component.text("Not enough arguments.", NamedTextColor.RED));
            return;
        }

        if (!display.containsKey(args[0].toUpperCase())) {
            source.sendMessage(Component.text("Incorrect type.", NamedTextColor.RED));
            return;
        }

        var messageArgs = new ArrayList<>(Arrays.asList(args));
        messageArgs.remove(0);

        var message = String.join(" ", messageArgs);

        plugin.updateActivity(args[0].toUpperCase(), message);
        source.sendMessage(Component.text( "Updated activity to: " + display.get(args[0].toUpperCase()) + message).color(NamedTextColor.GOLD));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("serverstatus.status");
    }
}
