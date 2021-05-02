package me.miquiis.votesystem.data;

import me.miquiis.votesystem.VoteSystem;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PollData {

    private PollOptionData pollOptionData;

    private Player target;

    private String targetName;

    private Integer votesRequired;

    private Set<VoteData> votes;

    public PollData(PollOptionData pollOptionData, Integer votesRequired)
    {
        this.pollOptionData = pollOptionData;
        this.votesRequired = votesRequired;
        this.votes = new HashSet<>();
    }

    public PollData(PollOptionData pollOptionData, Player target, Integer votesRequired)
    {
        this.pollOptionData = pollOptionData;
        this.target = target;
        this.targetName = target.getName();
        this.votesRequired = votesRequired;
        this.votes = new HashSet<>();
    }

    public void castVote(VoteData voteData)
    {
        votes.add(voteData);
        checkVotes();
    }

    private void finishPoll(boolean result)
    {
        VoteSystem.getInstance().getVoteManager().finishPoll(result);
    }

    private Integer votePercentage()
    {
        return (int) (votes.size() / votesRequired.doubleValue() * 100);
    }

    private void checkVotes()
    {
        if (votes.stream().filter(voteData -> !voteData.getVoteOption()).count() > 0)
        {
            finishPoll(false);
        }
        else
        {
            if (pollOptionData.hasVotePercentage())
            {
                if (pollOptionData.hasVoteRestriction())
                {
                    if (votes.size() >= pollOptionData.getVoteRestriction() && votePercentage() >= pollOptionData.getVotePercentage())
                    {
                        finishPoll(true);
                    }
                }
                else
                {
                    if (votePercentage() >= pollOptionData.getVotePercentage())
                    {
                        finishPoll(true);
                    }
                }
            }
            else
            {
                if (pollOptionData.hasVoteRestriction())
                {
                    if (votes.size() >= pollOptionData.getVoteRestriction() && votes.size() >= votesRequired)
                    {
                        finishPoll(true);
                    }
                    return;
                }
                else
                {
                    if (votes.size() >= votesRequired)
                    {
                        finishPoll(true);
                    }
                }
            }
        }
    }

    public boolean hasVoted(UUID uuid)
    {
        return getVotes().stream().filter(v -> v.getVoter().equals(uuid)).count() == 1;
    }

    public Integer getVotesRequired() {
        return votesRequired;
    }

    public Set<VoteData> getVotes() {
        return votes;
    }

    public PollOptionData getPollOptionData() {
        return pollOptionData;
    }

    public Player getTarget() {
        return target;
    }

    public String getTargetName()
    {
        return targetName == null ? "" : targetName;
    }
}
