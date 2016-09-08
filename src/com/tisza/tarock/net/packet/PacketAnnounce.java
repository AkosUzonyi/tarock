package com.tisza.tarock.net.packet;

import java.io.*;

import com.tisza.tarock.announcement.*;
import com.tisza.tarock.game.*;

public class PacketAnnounce extends PacketGameAction
{
	private AnnouncementContra announcementContra;
	
	PacketAnnounce() {}
	
	public PacketAnnounce(AnnouncementContra announcement, int player)
	{
		super(player);
		this.announcementContra = announcement;
	}

	public AnnouncementContra getAnnouncement()
	{
		return announcementContra;
	}

	protected void readData(DataInputStream dis) throws IOException
	{
		super.readData(dis);
		
		int id = dis.readShort();
		if (id < 0)
		{
			announcementContra = null;
		}
		else
		{
			Announcement a = Announcements.getFromID(id);
			int contraLevel = dis.readByte();
			announcementContra = new AnnouncementContra(a, contraLevel);
		}
	}

	protected void writeData(DataOutputStream dos) throws IOException
	{
		super.writeData(dos);
		
		if (announcementContra == null)
		{
			dos.writeShort(-1);
		}
		else
		{
			dos.writeShort(Announcements.getID(announcementContra.getAnnouncement()));
			dos.writeByte(announcementContra.getContraLevel());
		}
	}
}
