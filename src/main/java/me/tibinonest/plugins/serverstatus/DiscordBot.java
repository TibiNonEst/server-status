package me.tibinonest.plugins.serverstatus;

import org.apache.commons.text.StringSubstitutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class DiscordBot {
    private final DiscordApi api;
    private final ConfigData config;
    private final HashMap<String, ActivityType> statusTypes;

    public DiscordBot(String token, ConfigData config) {
        this.config = config;

        api = new DiscordApiBuilder()
                .setToken(token)
                .login()
                .join();

        statusTypes = new HashMap<>(4);
        statusTypes.put("PLAYING", ActivityType.PLAYING);
        statusTypes.put("LISTENING", ActivityType.LISTENING);
        statusTypes.put("WATCHING", ActivityType.WATCHING);
        statusTypes.put("COMPETING", ActivityType.COMPETING);

        if (!statusTypes.containsKey(config.statusType.toUpperCase())) return;
        api.updateActivity(statusTypes.get(config.statusType.toUpperCase()), config.statusMessage);
    }

    public void updateData(ArrayList<String> usersOnline) {
        for (String channel : config.textChannels) {
            Map<String, String> values = new HashMap<>();
            values.put("current", String.valueOf(usersOnline.size()));
            values.put("max", String.valueOf(config.maxPlayers));
            values.put("users", String.join(", ", usersOnline));

            StringSubstitutor substitutor = new StringSubstitutor(values, "%", "%");
            String message = substitutor.replace(config.textMessage);

            api.getServerTextChannelById(channel).ifPresent(textChannel -> textChannel.updateTopic(message));
        }

        for (String channel : config.voiceChannels) {
            Map<String, String> values = new HashMap<>();
            values.put("current", String.valueOf(usersOnline.size()));
            values.put("max", String.valueOf(config.maxPlayers));
            values.put("users", usersOnline.toString());

            StringSubstitutor substitutor = new StringSubstitutor(values, "%", "%");
            String message = substitutor.replace(config.voiceMessage);

            api.getServerVoiceChannelById(channel).ifPresent(voiceChannel -> voiceChannel.updateName(message));
        }
    }

    public void updateActivity(String type, String message) {
        if (!statusTypes.containsKey(type.toUpperCase())) return;
        api.updateActivity(statusTypes.get(type.toUpperCase()), message);
        config.statusType = type.toUpperCase();
        config.statusMessage = message;
    }

    public HashMap<String, String> disable() {
        api.disconnect();
        HashMap<String, String> status = new HashMap<>();
        status.put("type", config.statusType);
        status.put("message", config.statusMessage);
        return status;
    }
}
