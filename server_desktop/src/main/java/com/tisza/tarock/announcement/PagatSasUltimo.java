package com.tisza.tarock.announcement;

import com.tisza.tarock.card.*;
import com.tisza.tarock.game.*;

public class PagatSasUltimo extends Ultimo
{
	PagatSasUltimo(int roundIndex, TarockCard cardToTakeWith)
	{
		super(roundIndex, cardToTakeWith);
	}
	
	public Result isSuccessful(GameState gameState, Team team)
	{
		Result zaroparosSuccessful = Announcements.zaroparos.isSuccessful(gameState, team);
		
		if (zaroparosSuccessful == Result.SUCCESSFUL)
		{
			return Result.FAILED;
		}
		
		return super.isSuccessful(gameState, team);
	}
	
	public boolean canBeAnnounced(IAnnouncing announcing)
	{
		Team team = announcing.getCurrentTeam();
		
		if (announcing.isAnnounced(team, Announcements.zaroparos))
			return false;
		
		if (getRoundIndex() == 8)
		{
			for (TarockCount tc : new TarockCount[]{Announcements.nyolctarokk, Announcements.kilenctarokk})
			{
				if (tc.canBeAnnounced(announcing))
				{
					return false;
				}
			}
		}
		
		return super.canBeAnnounced(announcing);
	}

	public int getPoints()
	{
		return 10 * (9 - getRoundIndex());
	}

	public boolean canBeSilent()
	{
		return getRoundIndex() == 8;
	}
}