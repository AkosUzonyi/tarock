package com.tisza.tarock.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.tisza.tarock.message.MessageIO;
import com.tisza.tarock.proto.ActionOuterClass.*;

public class PacketAction extends Packet
{
	private Action action;
	
	PacketAction() {}
	
	public PacketAction(Action action)
	{
		this.action = action;
	}

	public Action getAction()
	{
		return action;
	}

	protected void readData(DataInputStream dis) throws IOException
	{
		action = Action.parseDelimitedFrom(dis);
	}

	protected void writeData(DataOutputStream dos) throws IOException
	{
		action.writeDelimitedTo(dos);
	}
}
