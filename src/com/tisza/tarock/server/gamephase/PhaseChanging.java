package com.tisza.tarock.server.gamephase;

import java.util.*;

import com.tisza.tarock.card.*;
import com.tisza.tarock.game.*;
import com.tisza.tarock.net.packet.*;
import com.tisza.tarock.server.*;

public class PhaseChanging implements GamePhase
{
	private GameSession game;
	private Changing changing;
	
	public PhaseChanging(GameSession g)
	{
		game = g;
		AllPlayersCards cards = game.getGameHistory().dealing.getCards();
		List<Card> talon = game.getGameHistory().dealing.getTalon();
		int winnerPlayer = game.getGameHistory().bidding.getWinnerPlayer();
		int winnerBid = game.getGameHistory().bidding.getWinnerBid();
		changing = new Changing(cards, talon, winnerPlayer, winnerBid);
	}

	public void start()
	{
		for (int i = 0; i < 4; i++)
		{
			Collection<Card> cards = changing.getCardsObtainedFromTalon(i);
			game.sendPacketToPlayer(i, new PacketChange(cards, i));
		}
	}

	public void packetFromPlayer(int player, Packet packet)
	{
		if (packet instanceof PacketChange)
		{
			PacketChange packetChange = ((PacketChange)packet);
			if (packetChange.getPlayer() == player)
			{
				if (changing.skart(player, packetChange.getCards()))
				{
					if (changing.isFinished())
					{
						game.getGameHistory().changing = changing;
						game.changeGamePhase(new PhaseAnnouncing(game));
					}
				}
			}
		}
	}
}
