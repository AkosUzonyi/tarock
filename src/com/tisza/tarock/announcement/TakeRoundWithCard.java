package com.tisza.tarock.announcement;

import java.util.*;

import com.tisza.tarock.card.*;
import com.tisza.tarock.game.*;

public abstract class TakeRoundWithCard extends AnnouncementBase
{
	private int roundIndex;
	private Card cardToTakeWith;
		
	TakeRoundWithCard(int roundIndex, Card cardToTakeWith)
	{
		this.roundIndex = roundIndex;
		this.cardToTakeWith = cardToTakeWith;
	}

	public Result isSuccessful(GameInstance gi, Team team)
	{
		Round round = gi.gameplay.getRoundsPassed().get(roundIndex);
		int theCardPlayer = round.getCards().indexOf(cardToTakeWith);
		if (theCardPlayer < 0) return Result.FAILED;
		
		if (gi.calling.getPlayerPairs().getTeam(theCardPlayer) != team)
		{
			return Result.FAILED;
		}
		else
		{
			if (round.getWinner() == theCardPlayer)
			{
				return canBeSilent() ? Result.SUCCESSFUL_SILENT : Result.SUCCESSFUL;
			}
			else
			{
				return canBeSilent() ? Result.FAILED_SILENT : Result.FAILED;
			}
		}
	}
	
	public boolean canBeAnnounced(Announcing announcing, Team team)
	{
		Map<Integer, TakeRoundWithCard> ultimokFromMyCard = Announcements.ultimok.get(cardToTakeWith);
		
		for (int r = 0; r <= roundIndex; r++)
		{
			TakeRoundWithCard announcement = ultimokFromMyCard.get(r);
			if (announcement != null && announcing.isAnnounced(team, announcement))
			{
				return false;
			}
		}
		
		return super.canBeAnnounced(announcing, team);
	}
	
	public Announcement getAnnouncementOverridden(Announcing announcing, Team team)
	{
		Map<Integer, TakeRoundWithCard> ultimokFromMyCard = Announcements.ultimok.get(cardToTakeWith);
		
		for (int r = 8; r > roundIndex; r--)
		{
			TakeRoundWithCard announcement = ultimokFromMyCard.get(r);
			if (announcement != null && announcing.isAnnounced(team, announcement))
			{
				return announcement;
			}
		}
		return null;
	}

	public final int getRoundIndex()
	{
		return roundIndex;
	}

	public final Card getCardToTakeWith()
	{
		return cardToTakeWith;
	}

	public boolean canBeSilent()
	{
		return false;
	}
	
	public boolean isShownToUser()
	{
		return false;
	}
}
