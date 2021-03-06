package com.tisza.tarock.zebisound;

import android.content.*;
import com.tisza.tarock.*;
import com.tisza.tarock.game.*;
import com.tisza.tarock.game.card.*;

class PikkKiralyIndul extends ZebiSound
{
	private boolean firstCard;

	public PikkKiralyIndul(Context context)
	{
		super(context, 1F, R.raw.pikkkiralyindul);
	}

	@Override
	public void startGame(GameType gameType, int beginnerPlayer)
	{
		firstCard = true;
	}

	@Override
	public void playCard(int player, Card card)
	{
		if (firstCard && card.equals(Card.getSuitCard(3, 5)))
			activate();

		firstCard = false;
	}
}
