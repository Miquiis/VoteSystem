package me.miquiis.votesystem.commands.vote;

import me.miquiis.votesystem.VoteSystem;
import me.miquiis.votesystem.commands.mCommand;
import me.miquiis.votesystem.commands.mSubCommand;
import me.miquiis.votesystem.data.VoteData;
import me.miquiis.votesystem.managers.ConfigManager;
import me.miquiis.votesystem.managers.MessagesManager;
import me.miquiis.votesystem.managers.VoteManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class VoteCommand extends mCommand {

    private final ConfigManager configManager;
    private final VoteManager voteManager;
    private final MessagesManager messagesManager;

    public VoteCommand(VoteSystem plugin)
    {
        this.configManager = plugin.getConfigManager();
        this.voteManager = plugin.getVoteManager();
        this.messagesManager = plugin.getMessagesManager();
    }

    @Override
    public String getName() {
        return "vote";
    }

    @Override
    public String getDescription() {
        return "Allows a player to initiate a vote poll.";
    }

    @Override
    public String getSyntax() {
        return "/vote <voteoption> [votetarget]";
    }

    @Override
    public String getPermission() {
        return "vote.usage";
    }

    @Override
    public String getPermissionMessage() {
        return "&cYou don't have enough permissions for this command.";
    }

    @Override
    public TextComponent getHelp() {
        return null;
    }

    @Override
    public ArrayList<mSubCommand> getSubcommand() {
        return new ArrayList<mSubCommand>(){
            {

            }
        };
    }

    @Override
    public void perform(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        if (args.size() <= 2 && args.size() > 0)
        {
            if (voteManager.getPollOption(args.get(0)).isPresent())
            {
                if (voteManager.getCurrentPoll() != null)
                {
                    if (sender instanceof Player)
                    {
                        Player player = (Player)sender;

                        if (voteManager.getCurrentPoll().hasVoted(player.getUniqueId()))
                            return;

                        player.sendMessage(messagesManager.getMessage("vote-count"));

                        voteManager.getCurrentPoll().castVote(new VoteData(player.getUniqueId(), true));
                        return;
                    }
                    sender.sendMessage(messagesManager.getMessage("vote-poll-up"));
                    return;
                }

                if (voteManager.isInCooldown())
                {
                    sender.sendMessage(messagesManager.getMessage("vote-poll-cooldown"));
                    return;
                }

                sender.sendMessage(messagesManager.getMessage("vote-poll-initialize"));

                if (args.size() == 2)
                {
                    Player player = Bukkit.getPlayer(args.get(1));
                    if (player == null)
                    {
                        sender.sendMessage(messagesManager.getMessage("vote-poll-player-not-found"));
                        return;
                    }
                }

                if (sender instanceof Player)
                {
                    voteManager.initiatePoll((Player)sender, args);
                }
                else
                {
                    voteManager.initiatePoll(null, args);
                }

                return;
            }
        }

        sender.sendMessage("§aTry using this syntax: §e" + getSyntax());
    }
}
