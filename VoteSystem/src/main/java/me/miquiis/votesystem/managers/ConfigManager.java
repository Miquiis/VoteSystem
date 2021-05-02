package me.miquiis.votesystem.managers;

import me.miquiis.votesystem.VoteSystem;
import me.miquiis.votesystem.data.PollOptionData;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final VoteSystem plugin;
    private final FileConfiguration config;

    public ConfigManager(VoteSystem plugin)
    {
        this.plugin = plugin;
        this.config = new YamlConfiguration();
        load();
    }

    public String getString(String path, String def)
    {
        return config.getString(path, def).replace('&', '§');
    }

    public Integer getInt(String path, Integer def)
    {
        return config.getInt(path, def);
    }

    public Double getDouble(String path) { return config.getDouble(path); }

    public List<PollOptionData> getVoteList()
    {
        List<PollOptionData> pollOptionData = new ArrayList<>();

        for (String votename : config.getConfigurationSection("votes").getKeys(false))
        {
            String path = "votes." + votename + ".";
            pollOptionData.add(
                    new PollOptionData(
                            votename,
                            getString(path + "vote-type", "default"),
                            getString(path+  "vote-start-message", "&bThere is a new vote poll up!\n&aType &eYES&a to accept.\n&cType &eNO&c to deny."),
                            getString(path+  "vote-success-message", "&bThe poll vote was a success."),
                            getString(path+  "vote-fail-message", "&cThe poll vote has failed."),
                            getString(path+  "vote-immune", ""),
                            getString(path+  "vote-ignore-in", ""),
                            getInt(path + "vote-restriction", 0),
                            getInt(path + "vote-percentage", 100),
                            config.getStringList(path + "commands")
                    )
            );
        }

        return pollOptionData;
    }

    private void load()
    {
        try
        {
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists())
            {
                plugin.saveResource("config.yml", false);
            }
            config.load(configFile);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }

}
