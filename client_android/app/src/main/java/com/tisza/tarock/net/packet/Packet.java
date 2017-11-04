package com.tisza.tarock.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Packet
{
	private static final Map<Integer, Class<? extends Packet>> idToPacket = new HashMap<Integer, Class<? extends Packet>>();
	private static final Map<Class<? extends Packet>, Integer> packetToID = new HashMap<Class<? extends Packet>, Integer>();
	
	private static void register(int id, Class<? extends Packet> cls)
	{
		if (idToPacket.containsKey(id) || packetToID.containsKey(cls))
			throw new IllegalArgumentException();
		
		idToPacket.put(id, cls);
		packetToID.put(cls, id);
	}
	
	public int getID()
	{
		return packetToID.get(getClass());
	}

	public static Packet readPacket(InputStream is) throws IOException
	{
		DataInputStream dis = new DataInputStream(is);
		int id = dis.readByte();
		Class<? extends Packet> cls = idToPacket.get(id);
		Packet p;
		try
		{
			p = cls.newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		p.readData(dis);
		return p;
	}
	
	public void writePacket(OutputStream os) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeByte(getID());
		writeData(dos);
		dos.flush();
	}
	
	protected abstract void readData(DataInputStream dis) throws IOException;
	protected abstract void writeData(DataOutputStream dos) throws IOException;
	
	static
	{
		register(0, PacketLogin.class);
		register(1, PacketLoginFailed.class);
		register(2, PacketAction.class);
		register(3, PacketEvent.class);
	}
}