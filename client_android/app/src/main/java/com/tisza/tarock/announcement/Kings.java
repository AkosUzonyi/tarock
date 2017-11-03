package com.tisza.tarock.announcement;

import com.tisza.tarock.card.*;
import com.tisza.tarock.game.*;

public class Kings extends AnnouncementBase
{
	private static final int[] points = new int[]{8, 60, 100};
	
	private int count;
		
	Kings(int count)
	{
		if (count < 1 || count >= 4)
			throw new IllegalArgumentException();
		
		this.count = count;
	}

	public Result isSuccessful(GameState gameState, Team team)
	{
		for (int i = 0; i < count; i++)
		{
			int roundIndex = 8 - i;
			if (isRoundOK(gameState, team, roundIndex))
			{
				return Result.SUCCESSFUL;
			}
		}
		return Result.FAILED;
	}
	
	private boolean isRoundOK(GameState gameState, Team team, int roundIndex)
	{
		Round round = gameState.getRound(roundIndex);
		for (int p = 0; p < 4; p++)
		{
			Card card = round.getCardByPlayer(p);
			boolean isItUs = gameState.getPlayerPairs().getTeam(p) == team;
			boolean isKing = card instanceof SuitCard && ((SuitCard)card).getValue() == 5;
			boolean isWon = round.getWinner() == p;
			if (isItUs && isKing && isWon)
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean canBeAnnounced(IAnnouncing announcing)
	{
		Team team = announcing.getCurrentTeam();
		
		for (int i = 3; i >= count; i--)
		{
			if (announcing.isAnnounced(team, Announcements.kings[i - 1]))
			{
				return false;
			}
		}
		
		return super.canBeAnnounced(announcing);
	}
	
	public void onAnnounce(IAnnouncing announcing)
	{
		Team team = announcing.getCurrentTeam();
		
		for (int i = 1; i < count; i++)
		{
			announcing.clearAnnouncement(team, Announcements.kings[i - 1]);
		}
	}

	protected int getPoints()
	{
		return points[count - 1];
	}
}
