package com.tisza.tarock.game.card;

public class TarockCard extends Card
{
	private final int value;

	TarockCard(int v)
	{
		if (v < 1 || v >= 23)
			throw new IllegalArgumentException("invalid tarock value: " + v);
		value = v;
	}
	
	public int getValue()
	{
		return value;
	}
	
	@Override
	public int getPoints()
	{
		return isHonor() ? 5 : 1;
	}

	@Override
	public boolean isHonor()
	{
		return value == 1 || value == 21 || value == 22;
	}

	@Override
	public boolean doesBeat(Card otherCard)
	{
		if (otherCard instanceof SuitCard)
		{
			return true;
		}
		else if (otherCard instanceof TarockCard)
		{
			TarockCard otherTarockCard = (TarockCard)otherCard;
			return value > otherTarockCard.value;
		}
		else
		{
			throw new IllegalArgumentException("invalid card type: " + otherCard.getClass().getName());
		}
	}

	@Override
	public String getID()
	{
		return "t" + value;
	}

	public String toString()
	{
		return "Tarock " + getValue();
	}
}
