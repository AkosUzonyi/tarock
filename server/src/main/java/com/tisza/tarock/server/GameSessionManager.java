package com.tisza.tarock.server;

import com.tisza.tarock.game.*;
import com.tisza.tarock.server.database.*;
import com.tisza.tarock.game.doubleround.*;
import com.tisza.tarock.server.player.*;
import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.disposables.*;

import java.util.*;

public class GameSessionManager
{
	private static final int MAX_GAME_IDLE_TIME = 6 * 3600 * 1000;

	private final Server server;
	private Map<Integer, GameSession> gameSessions = new HashMap<>();
	private List<User> bots = new ArrayList<>();

	private CompositeDisposable disposables = new CompositeDisposable();

	public GameSessionManager(Server server)
	{
		this.server = server;
	}

	public void initialize()
	{
		server.getDatabase().getActiveGameSessionIDs()
				.flatMapSingle(id -> GameSession.load(id, server.getDatabase()))
				.doOnNext(gameSession -> gameSessions.put(gameSession.getID(), gameSession))
				.ignoreElements().blockingAwait();

		bots.addAll(server.getDatabase().getBotUsers());
	}

	public Single<GameSession> createGameSession(GameType gameType, List<User> users, DoubleRoundType doubleRoundType)
	{
		return
		Observable.fromIterable(users).map(User::createPlayer).toList().flatMap(players ->
		GameSession.createNew(gameType, doubleRoundType, server.getDatabase()).doOnSuccess(gameSession ->
		{
			for (Player player : players)
				gameSession.addPlayer(player);

			if (gameSession.isLobbyFull())
				gameSession.start();

			gameSessions.put(gameSession.getID(), gameSession);
			server.broadcastStatus();
		}));
	}

	public void startGameSessionLobbyWithBots(int id)
	{
		GameSession gameSession = gameSessions.get(id);
		if (gameSession.getState() != GameSession.State.LOBBY)
			throw new IllegalStateException("GameSession already started");

		for (User botUser : bots)
		{
			while (!gameSession.isLobbyFull())
				gameSession.addPlayer(botUser.createPlayer());

			gameSession.start();
			server.broadcastStatus();
		}
	}

	public GameSession getGameSession(int id)
	{
		return gameSessions.get(id);
	}

	public Collection<GameSession> getGameSessions()
	{
		return gameSessions.values();
	}

	public void deleteOldGames()
	{
		for (GameSession gameSession : gameSessions.values())
			if (gameSession.getLastModified() < System.currentTimeMillis() - MAX_GAME_IDLE_TIME)
				gameSession.endSession();

		gameSessions.entrySet().removeIf(entry -> entry.getValue().getState() == GameSession.State.ENDED);

		server.broadcastStatus();
	}

	public void shutdown()
	{
		disposables.dispose();
	}
}
