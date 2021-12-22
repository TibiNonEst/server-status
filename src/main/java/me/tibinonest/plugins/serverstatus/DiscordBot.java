package me.tibinonest.plugins.serverstatus;

import ninja.leaping.configurate.ConfigurationNode;
import org.apache.commons.text.StringSubstitutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscordBot {
    private final DiscordApi api;
    private final int maxPlayers;
    private final ConfigurationNode config;
    private final HashMap<String, ActivityType> statusTypes;

    public DiscordBot(ConfigurationNode config, int maxPlayers) {
        this.config = config;
        this.maxPlayers = maxPlayers;

        api = new DiscordApiBuilder().setToken(config.getNode("token").getString()).login().join();

        statusTypes = new HashMap<>(4);
        statusTypes.put("PLAYING", ActivityType.PLAYING);
        statusTypes.put("LISTENING", ActivityType.LISTENING);
        statusTypes.put("WATCHING", ActivityType.WATCHING);
        statusTypes.put("COMPETING", ActivityType.COMPETING);

        var currentStatusType = config.getNode("status").getNode("type").getString().toUpperCase();

        if (!statusTypes.containsKey(currentStatusType)) return;
        api.updateActivity(statusTypes.get(currentStatusType), config.getNode("status").getNode("message").getString());
    }

    public void updateData(ArrayList<String> usersOnline) {
        for (String channel : config.getNode("channels").getNode("text").getList(Object::toString)) {
            var values = new HashMap<String, String>();
            values.put("current", String.valueOf(usersOnline.size()));
            values.put("max", String.valueOf(maxPlayers));
            values.put("users", String.join(", ", usersOnline));

            var substitutor = new StringSubstitutor(values, "%", "%");
            var message = substitutor.replace(config.getNode("text-message").getString());

            api.getServerTextChannelById(channel).ifPresent(textChannel -> textChannel.updateTopic(message));
        }

        for (String channel : config.getNode("channels").getNode("voice").getList(Object::toString)) {
            var values = new HashMap<String, String>();
            values.put("current", String.valueOf(usersOnline.size()));
            values.put("max", String.valueOf(maxPlayers));
            values.put("users", usersOnline.toString());

            var substitutor = new StringSubstitutor(values, "%", "%");
            var message = substitutor.replace(config.getNode("voice-message").getString());

            api.getServerVoiceChannelById(channel).ifPresent(voiceChannel -> voiceChannel.updateName(message));
        }
    }

    public void updateActivity(String type, String message) {
        if (!statusTypes.containsKey(type.toUpperCase())) return;
        api.updateActivity(statusTypes.get(type.toUpperCase()), message);
    }

    public void disable() {
        api.disconnect();
    }
}
