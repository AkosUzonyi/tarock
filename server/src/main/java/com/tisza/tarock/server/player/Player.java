package com.tisza.tarock.server.player;

import com.tisza.tarock.game.*;
import com.tisza.tarock.server.*;
import com.tisza.tarock.server.database.*;
import com.tisza.tarock.message.*;

public abstract class Player
{
	private User user;

	private PlayerSeat seat;
	private GameSession gameSession;
	private int points = 0;

	public Player(User user)
	{
		this.user = user;
	}

	public abstract void handleEvent(Event event);

	public User getUser()
	{
		return user;
	}

	public PlayerSeat getSeat()
	{
		return seat;
	}

	public GameSession getGameSession()
	{
		return gameSession;
	}

	public void setGame(GameSession gameSession, PlayerSeat seat)
	{
		this.gameSession = gameSession;
		this.seat = seat;
	}

	public int getPoints()
	{
		return points;
	}

	public void setPoints(int points)
	{
		this.points = points;
	}

	public void addPoints(int points)
	{
		this.points += points;
	}

	protected void chat(String message)
	{
		if (gameSession != null)
			gameSession.chat(user, message);
	}

	protected void doAction(Action action)
	{
		if (gameSession != null && seat != null)
			gameSession.action(seat, action);
	}

	protected void requestHistory()
	{
		if (gameSession != null)
			gameSession.requestHistory(this);
	}
}
