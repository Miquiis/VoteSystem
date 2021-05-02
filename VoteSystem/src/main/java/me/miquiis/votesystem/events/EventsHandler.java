package me.miquiis.votesystem.events;

import me.miquiis.votesystem.VoteSystem;
import me.miquiis.votesystem.data.PollData;
import me.miquiis.votesystem.data.VoteData;
import me.miquiis.votesystem.managers.ConfigManager;
import me.miquiis.votesystem.managers.MessagesManager;
import me.miquiis.votesystem.managers.VoteManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import java.util.*;

public class EventsHandler {

    private final VoteSystem plugin;

    private final ArrayList<CustomEventHandler> listeners = new ArrayList<CustomEventHandler>() {
        {
            add(new PollEventHandler());
        }
    };

    public EventsHandler(VoteSystem plugin)
    {
        this.plugin = plugin;

        for (CustomEventHandler listener : listeners)
        {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            listener.init(plugin);
        }
    }

    public void reload()
    {
        listeners.forEach(list -> {
            list.reload(plugin);
        });
    }

}

abstract class CustomEventHandler implements Listener {

    public abstract void init(VoteSystem plugin);

    public abstract void reload(VoteSystem plugin);

}

class PollEventHandler extends CustomEventHandler {

    private VoteManager voteManager;
    private ConfigManager configManager;
    private MessagesManager messagesManager;

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e)
    {
        if (e.getNewGameMode() != GameMode.SURVIVAL)
        {
            if (!voteManager.getCurrentPoll().hasVoted(e.getPlayer().getUniqueId()))
            {
                PollData currentPoll = voteManager.getCurrentPoll();
                currentPoll.castVote(new VoteData(e.getPlayer().getUniqueId(), !(currentPoll.getTarget() != null && currentPoll.getTarget().equals(e.getPlayer()))));
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e)
    {
        if (voteManager.getCurrentPoll() != null)
        {
            if (e.getMessage().equalsIgnoreCase("yes") || e.getMessage().equalsIgnoreCase("no"))
            {
                e.setCancelled(true);

                if (voteManager.getCurrentPoll().hasVoted(e.getPlayer().getUniqueId())) {
                    e.getPlayer().sendMessage(messagesManager.getMessage("vote-already-counted"));
                    return;
                }

                boolean voteCast = e.getMessage().equalsIgnoreCase("yes");

                e.getPlayer().sendMessage(messagesManager.getMessage("vote-count"));

                voteManager.getCurrentPoll().castVote(new VoteData(e.getPlayer().getUniqueId(), voteCast));
            }
        }
    }

    @Override
    public void init(VoteSystem plugin) {
        this.voteManager = plugin.getVoteManager();
        this.configManager = plugin.getConfigManager();
        this.messagesManager = plugin.getMessagesManager();
    }

    @Override
    public void reload(VoteSystem plugin) {
        this.voteManager = plugin.getVoteManager();
        this.configManager = plugin.getConfigManager();
        this.messagesManager = plugin.getMessagesManager();
    }
}

