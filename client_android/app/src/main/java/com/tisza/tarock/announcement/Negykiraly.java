package com.tisza.tarock.announcement;

import com.tisza.tarock.card.*;

import java.util.*;

public class Negykiraly extends TakeCards
{
	Negykiraly(){}

	protected List<Card> getCardsToTake()
	{
		List<Card> result = new ArrayList<Card>();
		for (int s = 0; s < 4; s++)
		{
			result.add(new SuitCard(s, 5));
		}
		return result;
	}

	public int getPoints()
	{
		return 2;
	}

	public boolean canBeSilent()
	{
		return true;
	}
}
