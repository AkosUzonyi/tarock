package com.tisza.tarock.game;

import com.tisza.tarock.card.Card;
import com.tisza.tarock.card.PlayerCards;

import java.util.Collection;

class Gameplay extends Phase
{
	private Round currentRound;
	
	public Gameplay(GameSession gameSession)
	{
		super(gameSession);
	}
	
	@Override
	public PhaseEnum asEnum()
	{
		return PhaseEnum.GAMEPLAY;
	}

	@Override
	public void onStart()
	{
		currentRound = new Round(currentGame.getBeginnerPlayer());
		gameSession.getBroadcastEventSender().turn(currentRound.getCurrentPlayer());
	}

	@Override
	public void playCard(int player, Card card)
	{
		if (player != currentRound.getCurrentPlayer())
			return;
		
		if (!getPlaceableCards().contains(card))
		{
			//gameSession.sendEvent(player, new EventActionFailed(Reason.INVALID_CARD));
			return;
		}

		currentGame.getPlayerCards(player).removeCard(card);
		currentRound.placeCard(card);

		gameSession.getBroadcastEventSender().playCard(player, card);
		
		if (currentRound.isFinished())
		{
			currentGame.addRound(currentRound);
			int winner = currentRound.getWinner();
			currentGame.addWonCards(winner, currentRound.getCards());
			currentRound = currentGame.areAllRoundsPassed() ? null : new Round(winner);
			
			gameSession.getBroadcastEventSender().cardsTaken(winner);
		}
		
		if (currentRound != null)
		{
			gameSession.getBroadcastEventSender().turn(currentRound.getCurrentPlayer());
		}
		else
		{
			gameSession.changePhase(new PendingNewGame(gameSession, false));
			gameSession.sendStatistics();
		}
	}
	
	private Collection<Card> getPlaceableCards()
	{
		PlayerCards pc = currentGame.getPlayerCards(currentRound.getCurrentPlayer());
		Card firstCard = currentRound.getFirstCard();
		return pc.getPlaceableCards(firstCard);
	}
}
