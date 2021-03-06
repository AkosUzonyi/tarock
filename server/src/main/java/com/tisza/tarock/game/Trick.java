package com.tisza.tarock.game;

import com.tisza.tarock.game.card.*;

import java.util.*;

public class Trick
{
	private final PlayerSeat beginnerPlayer;
	private PlayerSeat currentPlayer;
	private PlayerSeat winnerPlayer;
	private PlayerSeatMap<Card> cards = new PlayerSeatMap<>();
	private boolean finished = false;
	
	public Trick(PlayerSeat beginnerPlayer)
	{
		if (beginnerPlayer == null)
			throw new IllegalArgumentException("beginnerPlayer == null");
		
		this.beginnerPlayer = beginnerPlayer;
		currentPlayer = beginnerPlayer;
		winnerPlayer = currentPlayer;
	}
	
	public PlayerSeat getBeginnerPlayer()
	{
		return beginnerPlayer;
	}
	
	public PlayerSeat getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	public PlayerSeat getWinner()
	{
		if (!isFinished())
			throw new IllegalStateException("Trick has not finished");
		return winnerPlayer;
	}
	
	public Card getFirstCard()
	{
		return getCardByIndex(0);
	}
	
	public Card getCardByIndex(int n)
	{
		PlayerSeat player = PlayerSeat.fromInt((beginnerPlayer.asInt() + n) % 4);
		return getCardByPlayer(player);
	}
	
	public Card getCardByPlayer(PlayerSeat player)
	{
		return cards.get(player);
	}
	
	public PlayerSeat getPlayerOfCard(Card card)
	{
		for (PlayerSeat player : PlayerSeat.getAll())
		{
			if (getCardByPlayer(player).equals(card))
			{
				return player;
			}
		}
		return null;
	}

	public Collection<Card> getCards()
	{
		return cards.values();
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
	public void placeCard(Card card)
	{
		if (isFinished())
			throw new IllegalStateException();
		
		cards.put(currentPlayer, card);
		
		Card currentWinnerCard = cards.get(winnerPlayer);
		if (card.doesBeat(currentWinnerCard))
		{
			winnerPlayer = currentPlayer;
		}
		
		currentPlayer = currentPlayer.nextPlayer();

		if (currentPlayer == beginnerPlayer)
		{
			finished = true;
		}
	}
}
