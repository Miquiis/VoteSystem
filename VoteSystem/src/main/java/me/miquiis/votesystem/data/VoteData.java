package me.miquiis.votesystem.data;

import java.util.UUID;

public class VoteData {

    private UUID voter;

    private boolean voteOption;

    public VoteData(UUID voter, boolean voteOption)
    {
        this.voter = voter;
        this.voteOption = voteOption;
    }

    public UUID getVoter() {
        return voter;
    }

    public boolean getVoteOption()
    {
        return voteOption;
    }
}
