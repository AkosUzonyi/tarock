package com.tisza.tarock.announcement;

import com.tisza.tarock.card.*;
import com.tisza.tarock.card.filter.*;
import com.tisza.tarock.game.*;

public class TarockCount implements Announcement
{
	private int count;
	
	TarockCount(int count)
	{
		this.count = count;
	}
	
	public int calculatePoints(GameState gameState, Team team)
	{
		return 0;
	}

	public boolean canBeAnnounced(IAnnouncing announcing)
	{
		Team team = announcing.getCurrentTeam();
		
		if (announcing.isAnnounced(team, this))
			return false;
		
		PlayerCards cards = announcing.getCards(announcing.getCurrentPlayer());
		return cards.filter(new TarockFilter()).size() == count;
	}

	public void onAnnounce(IAnnouncing announcing)
	{
	}
	
	public boolean canContra()
	{
		return false;
	}

	public boolean isShownToUser()
	{
		return true;
	}

	public boolean requireIdentification()
	{
		return true;
	}
}
