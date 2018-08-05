package com.tisza.tarock.zebisound;

import android.content.*;
import com.tisza.tarock.*;
import com.tisza.tarock.game.*;
import com.tisza.tarock.game.card.*;

import java.util.*;

class CatchXIX extends ZebiSound
{
	private boolean catchCardPlayed;

	public CatchXIX(Context context)
	{
		super(context, 1F, R.raw.ugyeeztereznikell);
	}

	@Override
	public void startGame(int myID, List<String> playerNames, GameType gameType, int beginnerPlayer)
	{
		catchCardPlayed = false;
	}

	@Override
	public void cardPlayed(int player, Card playedCard)
	{
		if (playedCard instanceof TarockCard)
		{
			int tarockValue = ((TarockCard)playedCard).getValue();

			if (tarockValue > 19)
				catchCardPlayed = true;

			if (tarockValue == 19 && catchCardPlayed)
				activate();
		}
	}

	@Override
	public void cardsTaken(int winnerPlayer)
	{
		catchCardPlayed = false;
	}
}