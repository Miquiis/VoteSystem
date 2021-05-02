package me.miquiis.votesystem;

import me.miquiis.lobbyrestart.LobbyRestart;
import me.miquiis.teams.Teams;
import me.miquiis.votesystem.commands.CommandManager;
import me.miquiis.votesystem.events.EventsHandler;
import me.miquiis.votesystem.managers.ConfigManager;
import me.miquiis.votesystem.managers.MessagesManager;
import me.miquiis.votesystem.managers.VoteManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteSystem extends JavaPlugin {

    private static VoteSystem instance;

    private Teams teamsInstance;
    private LobbyRestart lobbyRestartInstance;

    private ConfigManager configManager;
    private MessagesManager messagesManager;
    private VoteManager voteManager;
    private CommandManager commandManager;

    private EventsHandler eventsHandler;

    @Override
    public void onEnable()
    {
        instance = this;

        if (getServer().getPluginManager().getPlugin("Teams") != null)
            teamsInstance = Teams.getInstance();
        if (getServer().getPluginManager().getPlugin("LobbyRestart") != null)
            lobbyRestartInstance = LobbyRestart.getInstance();

        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this);
        voteManager = new VoteManager(this);
        commandManager = new CommandManager(this);

        eventsHandler = new EventsHandler(this);
    }

    @Override
    public void onDisable()
    {

    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    public static VoteSystem getInstance() {
        return instance;
    }

    public LobbyRestart getLobbyRestart() {
        return lobbyRestartInstance;
    }

    public Teams getTeams() {
        return teamsInstance;
    }
}
