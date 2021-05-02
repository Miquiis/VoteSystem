package me.miquiis.votesystem.data;

import java.util.List;

public class PollOptionData {

    private String name;

    private String pollType;

    private String voteStart;
    private String voteSuccess;
    private String voteFail;

    private String voteImmune;
    private String voteIgnore;

    private Integer voteRestriction;

    private Integer votePercentage;

    private List<String> commands;

    public PollOptionData(String name, String pollType, String voteStart, String voteSuccess, String voteFail, String voteImmune, String voteIgnore, Integer voteRestriction, Integer voterPercentage, List<String> commands)
    {
        this.name = name;
        this.pollType = pollType;
        this.voteStart = voteStart;
        this.voteSuccess = voteSuccess;
        this.voteFail = voteFail;
        this.voteImmune = voteImmune;
        this.voteIgnore = voteIgnore;
        this.voteRestriction = voteRestriction;
        this.votePercentage = voterPercentage;
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }

    public Integer getVoteRestriction() {
        return voteRestriction;
    }

    public Integer getVotePercentage() {
        return votePercentage;
    }

    public String getPollType() {
        return pollType;
    }

    public boolean hasVoteRestriction()
    {
        return voteRestriction > 0;
    }

    public boolean hasVotePercentage()
    {
        return voteRestriction < 100;
    }

    public String getName() {
        return name;
    }

    public String getVoteFail() {
        return voteFail;
    }

    public String getVoteStart() {
        return voteStart;
    }

    public String getVoteSuccess() {
        return voteSuccess;
    }

    public String getVoteIgnore() {
        return voteIgnore;
    }

    public String getVoteImmune() {
        return voteImmune;
    }
}
