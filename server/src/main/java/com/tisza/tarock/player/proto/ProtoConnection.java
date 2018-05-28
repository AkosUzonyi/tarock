package com.tisza.tarock.player.proto;

import com.tisza.tarock.proto.MainProto.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProtoConnection
{
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private List<MessageHandler> packetHandlers = new ArrayList<MessageHandler>();
	private BlockingQueue<Message> messagesToSend = new LinkedBlockingQueue<Message>();
	private boolean closeRequested = false;
	
	private Thread readerThread = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				while (!closeRequested)
				{
					Message message = Message.parseDelimitedFrom(is);

					if (message == null)
						break;

					synchronized (packetHandlers)
					{
						for (MessageHandler handler : packetHandlers)
						{
							handler.handleMessage(message);
						}
					}
				}
			}
			catch (EOFException e) {}
			catch (SocketException e) {}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				closeRequest();
			}
		}
	});
	
	private Thread writerThread = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				while (!closeRequested || !messagesToSend.isEmpty())
				{
					try
					{
						Message message = messagesToSend.take();
						message.writeDelimitedTo(os);
					}
					catch (InterruptedException e) {}
				}
			}
			catch (SocketException e) {}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				closeRequest();
				try
				{
					close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	});
	
	public ProtoConnection(Socket s) throws IOException
	{
		socket = s;
		socket.setTcpNoDelay(true);
		is = socket.getInputStream();
		os = socket.getOutputStream();
	}
	
	public void start()
	{
		if (!closeRequested)
		{
			readerThread.start();
			writerThread.start();
		}
	}
	
	public void addMessageHandler(MessageHandler handler)
	{
		synchronized (packetHandlers)
		{
			packetHandlers.add(handler);
		}
	}
	
	public void removeMessageHandler(MessageHandler handler)
	{
		synchronized (packetHandlers)
		{
			packetHandlers.remove(handler);
		}
	}
	
	public void sendMessage(Message message)
	{
		if (isAlive())
		{
			messagesToSend.offer(message);
		}
	}
	
	public void closeRequest()
	{
		if (!closeRequested)
		{
			closeRequested = true;
			writerThread.interrupt();
		}
	}
	
	public boolean isAlive()
	{
		return !closeRequested;
	}
	
	private void close() throws IOException
	{
		closeRequested = true;
		if (socket != null)
		{
			socket.close();
			socket = null;
			synchronized (packetHandlers)
			{
				for (MessageHandler handler : packetHandlers)
				{
					handler.connectionClosed();
				}
			}
			packetHandlers = null;
		}
	}
}
