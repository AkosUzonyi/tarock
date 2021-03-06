package com.tisza.tarock.game.announcement;

import com.tisza.tarock.game.card.*;
import com.tisza.tarock.game.*;

public class Trull extends TakeCards
{
	Trull(){}

	@Override
	public String getID()
	{
		return "trull";
	}

	@Override
	public GameType getGameType()
	{
		return GameType.PASKIEVICS;
	}

	@Override
	protected boolean hasToBeTaken(Card card)
	{
		return card.isHonor();
	}

	@Override
	public int getPoints()
	{
		return 2;
	}

	@Override
	public boolean canBeSilent()
	{
		return true;
	}
}
