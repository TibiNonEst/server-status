package me.tibinonest.plugins.serverstatus;

import java.util.List;

public final class ConfigData {
    public int maxPlayers;
    public String statusMessage, statusType, textMessage, voiceMessage;
    public List<String> textChannels, voiceChannels;

    public ConfigData(String textMessage,
                      String voiceMessage,
                      int maxPlayers,
                      List<String> textChannels,
                      List<String> voiceChannels,
                      String statusType,
                      String statusMessage) {
        this.textMessage = textMessage;
        this.voiceMessage = voiceMessage;
        this.maxPlayers = maxPlayers;
        this.textChannels = textChannels;
        this.voiceChannels = voiceChannels;
        this.statusType = statusType;
        this.statusMessage = statusMessage;
    }
}
