package me.tibinonest.plugins.serverstatus.commands;

import me.tibinonest.plugins.serverstatus.ServerStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class StatusCommand extends Command {
    private final ServerStatus plugin;
    private final HashMap<String, String> display;

    public StatusCommand(ServerStatus plugin) {
        super("status", "serverstatus.status");
        this.plugin = plugin;

        display = new HashMap<>(4);
        display.put("PLAYING", "Playing ");
        display.put("LISTENING", "Listening to ");
        display.put("WATCHING", "Watching ");
        display.put("COMPETING", "Competing in ");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Not enough args."));
            return;
        }
        if (!display.containsKey(args[0].toUpperCase())) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Incorrect type."));
            return;
        }
        List<String> messageArgs = new ArrayList<>(Arrays.asList(args));
        messageArgs.remove(0);
        String message = String.join(" ", messageArgs);
        plugin.updateActivity(args[0].toUpperCase(), message);
        sender.sendMessage(new TextComponent(ChatColor.GOLD + "Updated activity to: " + display.get(args[0].toUpperCase()) + message));
    }
}
