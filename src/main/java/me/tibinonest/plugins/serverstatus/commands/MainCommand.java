package me.tibinonest.plugins.serverstatus.commands;

import me.tibinonest.plugins.serverstatus.ServerStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public final class MainCommand extends Command {
    private final ServerStatus plugin;

    public MainCommand(ServerStatus plugin) {
        super("serverstatus", "serverstatus.reload", "ssr", "ssreload", "srvstatus");
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {
        plugin.handleReload();
        sender.sendMessage(new TextComponent(ChatColor.GOLD + "Server Status reloaded!"));
    }
}
