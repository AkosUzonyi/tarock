package com.tisza.tarock.gui;

import android.app.*;
import android.content.*;
import android.os.*;
import com.facebook.*;
import com.tisza.tarock.*;
import com.tisza.tarock.R;
import com.tisza.tarock.message.*;
import com.tisza.tarock.net.*;
import com.tisza.tarock.proto.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

public class MainActivity extends Activity implements MessageHandler, GameListAdapter.GameAdapterListener
{
	private LoginFragment loginFragment = new LoginFragment();

	private ProgressDialog progressDialog;

	private boolean loggedIn = false;
	private ProtoConnection connection;
	private ActionSender actionSender;
	private EventHandler eventHandler;

	private List<GameInfo> games = new ArrayList<>();
	private List<User> users = new ArrayList<>();
	private GameListAdapter gameListAdapter;
	private AvailableUsersAdapter availableUsersAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(this.getApplicationContext());

		setContentView(R.layout.main);

		progressDialog = new ProgressDialog(this);

		gameListAdapter = new GameListAdapter(this, this);
		availableUsersAdapter = new AvailableUsersAdapter(this);

		getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, loginFragment, "login")
				.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		loginFragment.onActivityResult(requestCode, resultCode, data);
	}

	public void onPlayButtonClicked()
	{
		if (loggedIn)
		{
			onSuccesfulLogin();
		}

		if (connection == null)
		{
			ConnectAsyncTask connectAsyncTask = new ConnectAsyncTask();
			connectAsyncTask.execute();
		}
	}

	private void login()
	{
		if (!loggedIn)
		{
			connection.sendMessage(MainProto.Message.newBuilder().setLogin(MainProto.Login.newBuilder()
					.setFacebookToken(AccessToken.getCurrentAccessToken().getToken())
					.build())
					.build());

			progressDialog.setTitle(R.string.logging_in);
			progressDialog.show();
		}
	}

	private void onSuccesfulLogin()
	{
		popBackToLoginScreen();

		GameListFragment gameListFragment = new GameListFragment();

		getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, gameListFragment, "gamelist")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
				.commit();
	}

	public void createNewGame()
	{
		CreateGameFragment createGameFragment = new CreateGameFragment();

		getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, createGameFragment, "create_game")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void joinGame(int gameID)
	{
		GameFragment gameFragment = new GameFragment();

		Bundle args = new Bundle();
		args.putInt("gameID", gameID);
		gameFragment.setArguments(args);

		getFragmentManager().beginTransaction()
				.add(R.id.fragment_container, gameFragment, "game")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void deleteGame(int gameID)
	{
		connection.sendMessage(MainProto.Message.newBuilder().setDeleteGame(MainProto.DeleteGame.newBuilder()
				.setGameId(gameID))
				.build());
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (connection != null)
		{
			try
			{
				connection.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleMessage(MainProto.Message message)
	{
		switch (message.getMessageTypeCase())
		{
			case LOGIN:
				loggedIn = message.getLogin().hasFacebookToken();

				if (loggedIn)
				{
					onSuccesfulLogin();
				}
				else
				{
					popBackToLoginScreen();
				}

				if (progressDialog.isShowing())
					progressDialog.dismiss();

				break;

			case EVENT:
				runOnUiThread(() -> new ProtoEvent(message.getEvent()).handle(eventHandler));
				break;

			case SERVER_STATUS:
				games.clear();
				for (MainProto.Game gameProto : message.getServerStatus().getAvailableGameList())
				{
					games.add(Utils.gameInfoFromProto(gameProto));
				}

				users.clear();
				for (MainProto.User userProto : message.getServerStatus().getAvailableUserList())
				{
					users.add(Utils.userFromProto(userProto));
				}

				runOnUiThread(() ->
				{
					gameListAdapter.setGames(games);
					availableUsersAdapter.setUsers(users);
				});

				break;

			default:
				System.err.println("unhandled message type: " + message.getMessageTypeCase());
				break;
		}
	}

	private void popBackToLoginScreen()
	{
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		for (String tag : new String[] {"gamelist", "create_game", "game"})
		{
			Fragment fragment = fragmentManager.findFragmentByTag(tag);
			if (fragment != null)
				fragmentTransaction.remove(fragment);
		}

		fragmentTransaction.commit();
	}

	@Override
	public void connectionClosed()
	{
		connection = null;
		loggedIn = false;
		popBackToLoginScreen();
	}

	public ActionSender getActionSender()
	{
		return actionSender;
	}

	public ProtoConnection getConnection()
	{
		return connection;
	}

	public void setEventHandler(EventHandler eventHandler)
	{
		this.eventHandler = eventHandler;
	}

	public GameListAdapter getGameListAdapter()
	{
		return gameListAdapter;
	}

	public AvailableUsersAdapter getAvailableUsersAdapter()
	{
		return availableUsersAdapter;
	}

	private class ConnectAsyncTask extends AsyncTask<Void, Void, ProtoConnection>
	{
		@Override
		protected void onPreExecute()
		{
			progressDialog.setTitle(R.string.connecting);
			progressDialog.show();
		}

		protected ProtoConnection doInBackground(Void... voids)
		{
			final String host = "akos0.ddns.net";
			final int port = 8128;

			try
			{
				KeyStore ks = KeyStore.getInstance("BKS");
				ks.load(getAssets().open("truststore"), "000000".toCharArray());

				KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
				kmf.init(ks, "000000".toCharArray());

				TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(ks);

				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

				Socket socket = sc.getSocketFactory().createSocket();
				socket.connect(new InetSocketAddress(host, port), 1000);

				return new ProtoConnection(socket);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(ProtoConnection resultProtoConnection)
		{
			if (progressDialog.isShowing())
				progressDialog.dismiss();

			if (resultProtoConnection != null)
			{
				connection = resultProtoConnection;
				connection.addMessageHandler(MainActivity.this);
				connection.start();
				actionSender = new ProtoActionSender(connection);
				login();
			}
		}
	}
}
