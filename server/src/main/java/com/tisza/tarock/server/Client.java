package com.tisza.tarock.server;

import com.tisza.tarock.game.*;
import com.tisza.tarock.game.doubleround.*;
import com.tisza.tarock.proto.*;
import com.tisza.tarock.server.database.*;
import com.tisza.tarock.server.net.*;
import com.tisza.tarock.server.player.*;
import org.apache.log4j.*;

import java.io.*;
import java.net.*;

public class Client implements MessageHandler
{
	private static final Logger log = Logger.getLogger(Client.class);

	private final Server server;
	private ProtoConnection connection;
	private User loggedInUser = null;
	private ProtoPlayer currentPlayer = null;

	public Client(Server server, ProtoConnection connection)
	{
		this.server = server;
		this.connection = connection;
		connection.addMessageHandler(this);
	}

	public User getLoggedInUser()
	{
		return loggedInUser;
	}

	@Override
	public void handleMessage(MainProto.Message message)
	{
		if (loggedInUser == null && message.getMessageTypeCase() != MainProto.Message.MessageTypeCase.LOGIN)
			return;

		switch (message.getMessageTypeCase())
		{
			case LOGIN:
			{
				if (message.getLogin().hasFacebookToken())
				{
					String fbAccessToken = message.getLogin().getFacebookToken();
					server.getFacebookUserManager().newAccessToken(fbAccessToken).subscribe(this::userLogin, this::userLoginException);
				}
				else if (message.getLogin().hasGoogleToken())
				{
					String googleToken = message.getLogin().getGoogleToken();
					server.getGoogleUserManager().newToken(googleToken).subscribe(this::userLogin, this::userLoginException);
				}
				else
				{
					userLogin(null);
				}

				break;
			}

			case ACTION:
			case CHAT:
				break;

			case CREATE_GAME_SESSION:
			{
				MainProto.CreateGameSession createGame = message.getCreateGameSession();

				GameType gameType = GameType.fromID(createGame.getType());
				DoubleRoundType doubleRoundType = DoubleRoundType.fromID(createGame.getDoubleRoundType());
				server.getGameSessionManager().createGameSession(gameType, doubleRoundType, loggedInUser).subscribe();

				break;
			}

			case DELETE_GAME_SESSION:
			{
				int gameSessionID = message.getDeleteGameSession().getGameSessionId();
				GameSession gameSession = server.getGameSessionManager().getGameSession(gameSessionID);
				if (gameSession.isUserPlaying(loggedInUser))
					gameSession.deleteSession();

				server.broadcastStatus();
				break;
			}

			case JOIN_GAME_SESSION:
			{
				if (!message.getJoinGameSession().hasGameSessionId())
				{
					switchPlayer(null);
					break;
				}

				GameSession gameSession = server.getGameSessionManager().getGameSession(message.getJoinGameSession().getGameSessionId());
				if (gameSession.getState() == GameSession.State.DELETED)
					break;

				if (gameSession.isUserPlaying(loggedInUser))
				{
					ProtoPlayer player = (ProtoPlayer)gameSession.getPlayerByUser(loggedInUser);
					switchPlayer(player);
				}
				else if (gameSession.getState() == GameSession.State.LOBBY)
				{
					ProtoPlayer player = (ProtoPlayer)loggedInUser.createPlayer();
					boolean added = gameSession.addPlayer(player);
					if (added)
						switchPlayer(player);
				}
				else if (gameSession.getState() == GameSession.State.GAME)
				{
					ProtoPlayer player = (ProtoPlayer)loggedInUser.createPlayer();
					gameSession.addKibic(player);
					switchPlayer(player);
				}

				break;
			}

			case START_GAME_SESSION_LOBBY:
			{
				if (currentPlayer.getGameSession().isUserPlaying(loggedInUser))
					server.getGameSessionManager().startGameSessionLobbyWithBots(currentPlayer.getGameSession().getID());

				break;
			}

			case JOIN_HISTORY_GAME:
			{
				server.getGameSessionManager().createGameSessionHistoryView(message.getJoinHistoryGame().getGameId()).subscribe(gameSession ->
				{
					sendMessage(MainProto.Message.newBuilder().setJoinHistoryGameResult(MainProto.JoinHistoryGameResult.newBuilder().setGameSessionId(gameSession.getID())).build());
				});

				break;
			}

			case FCM_TOKEN:
			{
				String token = message.getFcmToken().getFcmToken();
				boolean active = message.getFcmToken().getActive();
				if (active)
					server.getDatabase().addFCMToken(token, loggedInUser);
				else
					server.getDatabase().removeFCMToken(token);
				break;
			}

			default:
				log.warn("Unhandled message type: " + message.getMessageTypeCase());
				break;
		}
	}

	private void switchPlayer(ProtoPlayer player)
	{
		if (player == currentPlayer)
			return;

		if (currentPlayer != null)
		{
			currentPlayer.removeConnection(connection);

			switch (currentPlayer.getGameSession().getState())
			{
				case LOBBY: currentPlayer.getGameSession().removePlayer(currentPlayer); break;
				case GAME: currentPlayer.getGameSession().removeKibic(currentPlayer); break;
			}
		}

		currentPlayer = player;

		if (currentPlayer != null)
			currentPlayer.addConnection(connection);

		server.broadcastStatus();
	}

	private void userLogin(User newUser)
	{
		if (connection == null)
			return;

		if (loggedInUser == newUser)
			return;

		switchPlayer(null);

		if (loggedInUser != null)
			logUserLoggedInStatus(false);

		loggedInUser = newUser;

		if (loggedInUser != null)
			logUserLoggedInStatus(true);

		MainProto.LoginResult.Builder loginMessageBuilder = MainProto.LoginResult.newBuilder();
		if (loggedInUser != null)
			loginMessageBuilder.setUserId(loggedInUser.getID());
		sendMessage(MainProto.Message.newBuilder().setLoginResult(loginMessageBuilder).build());

		server.broadcastStatus();
	}

	private void userLoginException(Throwable e)
	{
		log.error("Exception while logging in", e);
		sendMessage(MainProto.Message.newBuilder().setLoginResult(MainProto.LoginResult.newBuilder()).build());
	}

	public void sendMessage(MainProto.Message message)
	{
		connection.sendMessage(message);
	}

	@Override
	public void connectionClosed()
	{
		server.removeClient(this);
	}

	public void disconnect()
	{
		userLogin(null);
		try
		{
			connection.close();
		}
		catch (IOException e)
		{
			log.warn("Exception while closing client connection: " + e.getMessage());
		}
		connection = null;
	}

	private void logUserLoggedInStatus(boolean loggedIn)
	{
		User user = loggedInUser;
		SocketAddress address = connection != null ? connection.getRemoteAddress() : null;

		if (loggedIn)
			user.getName().subscribe(name -> log.info("User logged in: " + name + " (id: " + user.getID() + "; from: " + address + ")"));
		else
			user.getName().subscribe(name -> log.info("User logged out: " + name + " (id: " + user.getID() + ")"));
	}
}
