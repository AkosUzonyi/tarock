package com.tisza.tarock.game;

import com.tisza.tarock.card.Card;
import com.tisza.tarock.card.PlayerCards;
import com.tisza.tarock.card.TarockCard;
import com.tisza.tarock.card.filter.SkartableCardFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Changing extends Phase
{
	private static final SkartableCardFilter cardFilter = new SkartableCardFilter();
	
	private List<List<Card>> cardsFromTalon = null;
	private boolean[] donePlayer = new boolean[4];
	private int[] tarockCounts = new int[4];
	
	public Changing(GameSession gameSession)
	{
		super(gameSession);
	}
	
	public PhaseEnum asEnum()
	{
		return PhaseEnum.CHANGING;
	}
	
	public void onStart()
	{
		dealCardsFromTalon();
		for (int i = 0; i < 4; i++)
		{
			gameSession.getPlayerEventQueue(i).cardsFromTalon(cardsFromTalon.get(i));
		}
	}
	
	private void dealCardsFromTalon()
	{
		cardsFromTalon = new ArrayList<List<Card>>(4);
		for (int i = 0; i < 4; i++)
		{
			cardsFromTalon.add(new ArrayList<Card>());
		}
		
		List<Card> cardsRemaining = new LinkedList<Card>(currentGame.getTalon());
		for (int i = 0; i < 4; i++)
		{
			int player = (currentGame.getBidWinnerPlayer() + i) % 4;
			
			int cardCount;
			if (player == currentGame.getBidWinnerPlayer())
			{
				cardCount = currentGame.getWinnerBid();
			}
			else
			{
				cardCount = (int)Math.ceil((float)cardsRemaining.size() / (4 - i));
			}
			
			for (int j = 0; j < cardCount; j++)
			{
				cardsFromTalon.get(player).add(cardsRemaining.remove(0));
			}
		}
	}
	
	public void change(int player, List<Card> cardsToSkart)
	{
		if (donePlayer[player])
			return;
		
		PlayerCards skartingPlayerCards = currentGame.getPlayerCards(player);
		List<Card> cardsFromTalonForPlayer = cardsFromTalon.get(player);
		
		if (cardsToSkart.size() != cardsFromTalonForPlayer.size())
		{
			//gameSession.sendEvent(player, new EventActionFailed(Reason.WRONG_SKART_COUNT));
			return;
		}
		
		List<Card> checkedSkartCards = new ArrayList<Card>();
		for (Card c : cardsToSkart)
		{
			if (!cardFilter.match(c))
			{
				//gameSession.sendEvent(player, new EventActionFailed(Reason.INVALID_SKART));
				return;
			}
			
			if (checkedSkartCards.contains(c))
				return;
			
			checkedSkartCards.add(c);
			
			if (!skartingPlayerCards.hasCard(c) && !cardsFromTalonForPlayer.contains(c))
				return;
		}
		
		int tarockCount = 0;
		for (Card c : cardsToSkart)
		{
			if (c instanceof TarockCard)
			{
				tarockCount++;
				if (c.equals(new TarockCard(20)))
				{
					currentGame.setPlayerSkarted20(player);
				}
			}

			currentGame.addCardToSkart(player == currentGame.getBidWinnerPlayer() ? Team.CALLER : Team.OPPONENT, c);
		}
		tarockCounts[player] = tarockCount;
		
		skartingPlayerCards.getCards().addAll(cardsFromTalonForPlayer);
		skartingPlayerCards.getCards().removeAll(cardsToSkart);
		
		donePlayer[player] = true;
		
		gameSession.getPlayerEventQueue(player).playerCards(skartingPlayerCards);
		gameSession.getBroadcastEventQueue().changeDone(player);

		if (isFinished())
		{
			gameSession.getBroadcastEventQueue().skartTarock(tarockCounts);
			gameSession.changePhase(new Calling(gameSession));
		}
	}
	
	public void throwCards(int player)
	{
		PlayerCards cards = currentGame.getPlayerCards(player);
		if (cards.canBeThrown(true))
		{
			gameSession.getBroadcastEventQueue().throwCards(player);
			gameSession.changePhase(new PendingNewGame(gameSession, true));
		}
	}
	
	public boolean isFinished()
	{
		for (boolean b : donePlayer)
		{
			if (!b) return false;
		}
		return true;
	}
}