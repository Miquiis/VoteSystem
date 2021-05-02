package me.miquiis.votesystem.managers;

import me.miquiis.teams.data.TeamData;
import me.miquiis.teams.managers.TeamManager;
import me.miquiis.votesystem.VoteSystem;
import me.miquiis.votesystem.data.PollData;
import me.miquiis.votesystem.data.PollOptionData;
import me.miquiis.votesystem.data.VoteData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Optional;

public class VoteManager {

    private final VoteSystem plugin;

    private final ConfigManager configManager;

    private final MessagesManager messagesManager;

    private PollData currentPoll;

    private Long lastPollVote;

    private final List<PollOptionData> pollOptions;

    private BukkitTask pollClose;

    public VoteManager(VoteSystem plugin)
    {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.messagesManager = plugin.getMessagesManager();

        this.lastPollVote = 0L;
        this.pollOptions = configManager.getVoteList();
    }

    public void initiatePoll(Player voter, List<String> args)
    {
        String voteName = args.get(0);

        if (currentPoll == null && !isInCooldown())
        {
            Optional<PollOptionData> pollOptionData = getPollOption(voteName);
            if (!pollOptionData.isPresent()) return;

            if (pollClose != null) pollClose.cancel();

            PollOptionData pollOption = pollOptionData.get();

            if (!pollOption.getVoteIgnore().isEmpty())
            {
                switch (pollOption.getVoteIgnore())
                {
                    case "lobby-locked":
                        if (plugin.getLobbyRestart() != null)
                        {
                            if (plugin.getLobbyRestart().getLobbyManager().isLocked())
                            {
                                voter.sendMessage(messagesManager.getMessage("vote-poll-ignored"));
                                return;
                            }
                        }
                        else
                        {
                            plugin.getLogger().warning("Trying to use LobbyRestart, but plugin is not loaded!");
                        }
                        break;
                    case "lobby-unlocked":
                        if (plugin.getLobbyRestart() != null)
                        {
                            if (!plugin.getLobbyRestart().getLobbyManager().isLocked())
                            {
                                voter.sendMessage(messagesManager.getMessage("vote-poll-ignored"));
                                return;
                            }
                        }
                        else
                        {
                            plugin.getLogger().warning("Trying to use LobbyRestart, but plugin is not loaded!");
                        }
                        break;
                }
            }

            if (pollOption.getVoteRestriction() > Bukkit.getOnlinePlayers().size())
            {
                if (voter != null) voter.sendMessage(messagesManager.getMessage("vote-restriction-count"));
                return;
            }

            if (pollOption.getPollType().equals("player"))
            {
                Player player = Bukkit.getPlayer(args.get(1));

                if (!pollOption.getVoteImmune().isEmpty() && player != null)
                {
                    switch (pollOption.getVoteImmune().toLowerCase())
                    {
                        case "speedrunner":
                            if (plugin.getTeams() != null)
                            {
                                if (plugin.getTeams().getTeamManager().getPlayerTeam(player.getUniqueId()).isPresent())
                                {
                                    TeamData playerTeam = plugin.getTeams().getTeamManager().getPlayerTeam(player.getUniqueId()).get();
                                    if (TeamManager.isEquals(playerTeam, plugin.getTeams().getTeamManager().getSpeedrunner()))
                                    {
                                        voter.sendMessage(messagesManager.getMessage("vote-poll-denied"));
                                        return;
                                    }
                                }
                            }
                            else
                            {
                                plugin.getLogger().warning("Trying to use LobbyRestart, but plugin is not loaded!");
                            }
                            break;
                        case "hunter":
                            if (plugin.getTeams() != null)
                            {
                                if (plugin.getTeams().getTeamManager().getPlayerTeam(player.getUniqueId()).isPresent())
                                {
                                    TeamData playerTeam = plugin.getTeams().getTeamManager().getPlayerTeam(player.getUniqueId()).get();
                                    if (TeamManager.isEquals(playerTeam, plugin.getTeams().getTeamManager().getHunter()))
                                    {
                                        voter.sendMessage(messagesManager.getMessage("vote-poll-denied"));
                                        return;
                                    }
                                }
                            }
                            else
                            {
                                plugin.getLogger().warning("Trying to use LobbyRestart, but plugin is not loaded!");
                            }
                            break;
                    }
                }

                currentPoll = new PollData(pollOption, player, Bukkit.getOnlinePlayers().size());
            }
            else
            {
                currentPoll = new PollData(pollOption, Bukkit.getOnlinePlayers().size());
            }

            PollData currentPoll = this.currentPoll;

            if (voter != null) currentPoll.castVote(new VoteData(voter.getUniqueId(), true));

            Bukkit.getOnlinePlayers().forEach(p -> {

                if (p != null && currentPoll != null)
                {
                    if (currentPoll.getTarget() != null && currentPoll.getTarget().equals(p))
                    {
                        currentPoll.castVote(new VoteData(((Player)p).getUniqueId(), false));
                    }
                    else
                    {
                        if (p.getGameMode() != GameMode.SURVIVAL)
                        {
                            currentPoll.castVote(new VoteData(((Player)p).getUniqueId(), true));
                        }
                    }
                }
            });

            pollClose = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {

                finishPoll(false);

            }, configManager.getInt("vote-timeout", 5) * 20);

            if (Bukkit.getOnlinePlayers().size() > 0)
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p != voter && p.getGameMode() == GameMode.SURVIVAL) p.sendMessage(pollOption.getVoteStart().replace("%PLAYER%", currentPoll.getTargetName()).replace("%POLL_NAME%", pollOption.getName()));
                });
        }
    }

    public void finishPoll(boolean result)
    {
        if (result)
        {
            Bukkit.broadcastMessage(currentPoll.getPollOptionData().getVoteSuccess().replace("%PLAYER%", currentPoll.getTargetName()).replace("%POLL_NAME%", currentPoll.getPollOptionData().getName()));

            final List<String> commands = currentPoll.getPollOptionData().getCommands();
            final PollData pollData = currentPoll;

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                commands.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("&", "§").replace("%PLAYER%", pollData.getTargetName())));
            });
        }
        else
        {
            Bukkit.broadcastMessage(currentPoll.getPollOptionData().getVoteFail().replace("%PLAYER%", currentPoll.getTargetName()).replace("%POLL_NAME%", currentPoll.getPollOptionData().getName()));
        }

        currentPoll = null;
        if (pollClose != null) pollClose.cancel();
        lastPollVote = System.currentTimeMillis() / 1000;
    }

    public boolean isInCooldown()
    {
        return (System.currentTimeMillis() / 1000) - lastPollVote < configManager.getInt("vote-cooldown", 5);
    }

    public Optional<PollOptionData> getPollOption(String name)
    {
        return getPollOptions().stream().filter(pollOptionData -> pollOptionData.getName().equalsIgnoreCase(name)).findFirst();
    }

    public PollData getCurrentPoll() {
        return currentPoll;
    }

    public List<PollOptionData> getPollOptions() {
        return pollOptions;
    }
}
