package com.tisza.tarock.announcement;

import com.tisza.tarock.card.*;
import com.tisza.tarock.game.*;

public class KezbeVacak extends AnnouncementBase
{
	private int roundIndex;
	private Card cardToTakeWith;
		
	KezbeVacak(int roundIndex, Card cardToTakeWith)
	{
		this.roundIndex = roundIndex;
		this.cardToTakeWith = cardToTakeWith;
	}

	public Result isSuccessful(GameState gameState, Team team)
	{
		Round round = gameState.getRound(roundIndex);
		int theCardPlayer = round.getPlayerOfCard(cardToTakeWith);
		if (theCardPlayer < 0) return Result.FAILED;
		
		if (gameState.getPlayerPairs().getTeam(theCardPlayer) != team)
			return Result.FAILED;
		
		if (round.getWinner() != theCardPlayer)
			return Result.FAILED;
		
		for (int i = 0; i < roundIndex; i++)
		{
			round = gameState.getRound(i);
			int winner = round.getWinner();
			
			if (gameState.getPlayerPairs().getTeam(winner) != team)
				return Result.FAILED;
		}
		
		return Result.SUCCESSFUL;
	}

	public int getPoints()
	{
		return 10;
	}
}