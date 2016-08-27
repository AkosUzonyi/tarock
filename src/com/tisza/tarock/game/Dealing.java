package com.tisza.tarock.game;

import java.util.*;

import com.tisza.tarock.card.*;

public class Dealing
{
	private final AllPlayersCards pc = new AllPlayersCards();
	private final List<Card> talon;
	
	public Dealing()
	{
		Random rnd = new Random(1);
		List<Card> cardsToDeal = new ArrayList<Card>(Card.all);
		for (int p = 0; p < 4; p++)
		{
			for (int i = 0; i < 9; i++)
			{
				pc.getPlayerCards(p).addCard(cardsToDeal.remove(rnd.nextInt(cardsToDeal.size())));
			}
		}
		talon = cardsToDeal;
	}
	
	public AllPlayersCards getCards()
	{
		return pc;
	}
	
	public List<Card> getTalon()
	{
		return Collections.unmodifiableList(talon);
	}
}
