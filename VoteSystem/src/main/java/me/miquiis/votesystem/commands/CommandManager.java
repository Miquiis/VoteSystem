package me.miquiis.votesystem.commands;

import me.miquiis.votesystem.VoteSystem;
import me.miquiis.votesystem.commands.vote.VoteCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager implements CommandExecutor {

    private VoteSystem main;
    private ArrayList<mCommand> commands;

    public CommandManager(VoteSystem main)
    {
        this.main = main;

        commands = new ArrayList<mCommand>() {
            {
                add(new VoteCommand(main));
            }
        };

        for (mCommand command : commands)
        {
            main.getCommand(command.getName()).setExecutor(this);
        }
    }

    public void close()
    {
        if (commands != null)
            commands.clear();

        commands = null;
        main = null;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String label, String[] args) {

        if (commands != null && !commands.isEmpty())
        {
            for (mCommand command : commands)
            {
                if (c.getName().equalsIgnoreCase(command.getName()))
                {
                    command.perform(s, new ArrayList<String>(Arrays.asList(args)));
                    return true;
                }
            }
        }

        return false;
    }

}
