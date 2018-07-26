package com.tisza.tarock.game.announcement;

import com.tisza.tarock.game.card.*;
import com.tisza.tarock.game.*;
import com.tisza.tarock.game.phase.*;

public class KezbeVacak extends RoundAnnouncement
{
	private final int roundIndex;
	private final Card cardToTakeWith;
		
	KezbeVacak(int roundIndex, Card cardToTakeWith)
	{
		this.roundIndex = roundIndex;
		this.cardToTakeWith = cardToTakeWith;
	}

	@Override
	public AnnouncementID getID()
	{
		return new AnnouncementID("kezbevacak").setRound(roundIndex);
	}

	public int getRound()
	{
		return roundIndex;
	}

	public Card getCard()
	{
		return cardToTakeWith;
	}

	@Override
	public GameType getGameType()
	{
		return GameType.ILLUSZTRALT;
	}

	@Override
	public Result isSuccessful(GameState gameState, Team team)
	{
		Round round = gameState.getRound(roundIndex);
		PlayerSeat theCardPlayer = round.getPlayerOfCard(cardToTakeWith);
		if (theCardPlayer == null) return Result.FAILED;
		
		if (gameState.getPlayerPairs().getTeam(theCardPlayer) != team)
			return Result.FAILED;
		
		if (round.getWinner() != theCardPlayer)
			return Result.FAILED;
		
		for (int i = 0; i < roundIndex; i++)
		{
			round = gameState.getRound(i);
			PlayerSeat winner = round.getWinner();
			
			if (gameState.getPlayerPairs().getTeam(winner) != team)
				return Result.FAILED;
		}
		
		return Result.SUCCESSFUL;
	}

	@Override
	protected boolean containsRound(int round)
	{
		return this.roundIndex == round;
	}

	@Override
	protected boolean canOverrideAnnouncement(RoundAnnouncement announcement)
	{
		return false;
	}

	@Override
	public int getPoints()
	{
		return 10;
	}
}