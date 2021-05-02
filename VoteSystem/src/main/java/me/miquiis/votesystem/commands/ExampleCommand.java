package me.miquiis.votesystem.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ExampleCommand extends mCommand {

    @Override
    public String getName() {
        return "example";
    }

    @Override
    public String getDescription() {
        return "Example Base Command.";
    }

    @Override
    public String getSyntax() {
        return "/example";
    }

    @Override
    public String getPermission() {
        return "example.usage";
    }

    @Override
    public String getPermissionMessage() {
        return "";
    }

    @Override
    public TextComponent getHelp() {
        TextComponent main = new TextComponent("§f[§b§lExample§r§f]§9 List of Commands and Usage:");

        for (mSubCommand subCommand : getSubcommand())
        {
            TextComponent subText = new TextComponent("\n§2> §a" + subCommand.getSyntax());
            subText.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                    "§bCommand: §2" + subCommand.getName() + "\n" +
                            "§bDescription: §2" + subCommand.getDescription() + "\n" +
                            "§bUsage: §2" + subCommand.getSyntax() + "\n" +
                            "§bPermission: §2" + subCommand.getPermission()
            ).create() ) );
            subText.setClickEvent( new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, subCommand.getSyntax()));
            main.addExtra(subText);
        }

        return main;
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

        String doubleCommand = "";

        if (args.size() > 1)
        {
           doubleCommand = args.get(0) + " " + args.get(1);
        }

        if (args.size() > 0) {
            for (mSubCommand sub : getSubcommand()) {
                if (args.get(0).equalsIgnoreCase(sub.getName()) || doubleCommand.equalsIgnoreCase(sub.getName())) {

                    if (!(sender instanceof Player) && !sub.canConsole())
                    {
                        sender.sendMessage("§cYou must be a player to execute this command");
                        return;
                    }

                    if (!sender.hasPermission(sub.getPermission())) {
                        sender.sendMessage(getPermissionMessage());
                        return;
                    }

                    for (int i = 0; i < sub.getName().split(" ").length; i++)
                        args.remove(0);

                    sub.perform(this, sender, args);
                    return;

                }
            }
        }

        if (!sender.hasPermission("example.help")) {
            sender.sendMessage(getPermissionMessage());
            return;
        }

        sender.spigot().sendMessage(getHelp());
        return;
    }
}
