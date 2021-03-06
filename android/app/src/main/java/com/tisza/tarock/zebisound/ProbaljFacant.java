package com.tisza.tarock.zebisound;

import android.content.*;
import com.tisza.tarock.*;
import com.tisza.tarock.game.*;
import com.tisza.tarock.game.card.*;

class ProbaljFacant extends ZebiSound
{
	private boolean active;

	public ProbaljFacant(Context context)
	{
		super(context, 0.2F, R.raw.probaljfacant);
	}

	@Override
	public void startGame(GameType gameType, int beginnerPlayer)
	{
		active = true;
	}

	@Override
	public void playCard(int player, Card card)
	{
		if (card instanceof TarockCard)
			active = false;

		if (active)
			activate();
	}

	@Override
	public void cardsTaken(int winnerPlayer)
	{
		active = false;
	}
}
