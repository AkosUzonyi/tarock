package com.tisza.tarock.gui;

import java.util.*;

import android.annotation.*;
import android.content.*;
import android.content.res.*;

import com.tisza.tarock.*;
import com.tisza.tarock.announcement.*;
import com.tisza.tarock.card.*;
import com.tisza.tarock.game.*;
import com.tisza.tarock.net.packet.*;

@SuppressLint("UseSparseArrays")
public class ResourceMappings
{
	public static final Map<Card, Integer> cardToImageResource = new HashMap<Card, Integer>();
	
	public static final Map<PacketTurn.Type, String> turnAcitionToMessage = new HashMap<PacketTurn.Type, String>();
	
	public static final Map<Integer, String> bidToName = new HashMap<Integer, String>();
	
	public static final Map<Announcement, String> announcementToName = new HashMap<Announcement, String>();
	public static final Map<Integer, String> contraLevelToName = new HashMap<Integer, String>();
	
	public static final Map<Integer, String> suitToName = new HashMap<Integer, String>();
	public static final Map<Integer, String> suitcardValueToName = new HashMap<Integer, String>();
	public static final Map<Card, String> cardToName = new HashMap<Card, String>();
	
	public static final Map<AnnouncementBase.Result, Integer> announcementResultToImage = new HashMap<AnnouncementBase.Result, Integer>();
	public static final Map<PacketLoginFailed.Reason, Integer> failureReasonToMessage = new HashMap<PacketLoginFailed.Reason, Integer>();
	
	private static String silent;
	private static String passz;
	
	public static String getAnnouncementContraContraName(AnnouncementContra ac)
	{
		if (ac == null)
			return passz;
		
		String result = "";
		
		if (!ac.isAnnounced())
		{
			result += silent;
		}
		else
		{
			result += ResourceMappings.contraLevelToName.get(ac.getContraLevel());
		}
		
		if (result.length() > 0)
			result += " ";
		
		result += ResourceMappings.announcementToName.get(ac.getAnnouncement());
		
		return result;
	}
	
	public static void init(Context context)
	{
		Resources resources = context.getResources();
		
		for (int s = 0; s < 4; s++)
		{
			int id = resources.getIdentifier("suits" + s, "string", context.getPackageName());
			suitToName.put(s, resources.getString(id));
		}
		
		for (int v = 1; v <= 5; v++)
		{
			int id = resources.getIdentifier("suitv" + v, "string", context.getPackageName());
			suitcardValueToName.put(v, resources.getString(id));
		}
		
		String[] suitNames = new String[]{"a", "b", "c", "d"};
		String[] valueNames = new String[]{"1", "2", "3", "4", "5"};
		for (int s = 0; s < 4; s++)
		{
			for (int v = 0; v < 5; v++)
			{
				Card card = new SuitCard(s, v + 1);
				String imaName = suitNames[s] + valueNames[v];
				
				int id = resources.getIdentifier(imaName, "drawable", context.getPackageName());
				cardToImageResource.put(card, id);
				
				String name = suitToName.get(s) + " " + suitcardValueToName.get(v + 1);
				cardToName.put(card, name);
			}
		}
		
		for (int i = 1; i <= 22; i++)
		{
			Card card = new TarockCard(i);
			String imgName = "t" + i;
			
			int drawableID = resources.getIdentifier(imgName, "drawable", context.getPackageName());
			cardToImageResource.put(card, drawableID);
			
			int nameID = resources.getIdentifier(imgName, "string", context.getPackageName());
			cardToName.put(card, resources.getString(nameID));
		}
		
		announcementResultToImage.put(AnnouncementBase.Result.SUCCESSFUL, R.drawable.successful);
		announcementResultToImage.put(AnnouncementBase.Result.SUCCESSFUL_SILENT, R.drawable.successful);
		announcementResultToImage.put(AnnouncementBase.Result.FAILED, R.drawable.failed);
		announcementResultToImage.put(AnnouncementBase.Result.FAILED_SILENT, R.drawable.failed);
		
		bidToName.put(-1, resources.getString(R.string.bid_passz));
		for (int i = 0; i <= 3; i++)
		{
			int id = resources.getIdentifier("bid" + i, "string", context.getPackageName());
			bidToName.put(i, resources.getString(id));
		}
		
		for (int i = 0; i < 7; i++)
		{
			int id = resources.getIdentifier("contra" + i, "string", context.getPackageName());
			contraLevelToName.put(i, resources.getString(id));
		}
		
		silent = resources.getString(R.string.silent);
		
		passz = resources.getString(R.string.passz);
		
		announcementToName.put(Announcements.game, resources.getString(R.string.jatek));
		announcementToName.put(Announcements.trull, resources.getString(R.string.trull));
		announcementToName.put(Announcements.xxiFogas, resources.getString(R.string.xxiFogas));
		for (int i = 0; i < 4; i++)
		{
			Banda banda = Announcements.bandak[i];
			announcementToName.put(banda, suitToName.get(i) + " " + resources.getString(R.string.banda));
		}
		announcementToName.put(Announcements.negykiraly, resources.getString(R.string.negykiraly));
		announcementToName.put(Announcements.dupla, resources.getString(R.string.dupla));
		announcementToName.put(Announcements.hosszuDupla, resources.getString(R.string.hosszuDupla));
		announcementToName.put(Announcements.nyolctarokk, resources.getString(R.string.nyolctarokk));
		announcementToName.put(Announcements.kilenctarokk, resources.getString(R.string.kilenctarokk));
		announcementToName.put(Announcements.szinesites, resources.getString(R.string.szinesites));
		announcementToName.put(Announcements.volat, resources.getString(R.string.volat));
		announcementToName.put(Announcements.centrum, resources.getString(R.string.round4));
		announcementToName.put(Announcements.kismadar, resources.getString(R.string.round5));
		announcementToName.put(Announcements.nagymadar, resources.getString(R.string.round6));
		announcementToName.put(Announcements.kings[0], resources.getString(R.string.kiralyultimo));
		announcementToName.put(Announcements.kings[1], resources.getString(R.string.ketkiralyok));
		announcementToName.put(Announcements.kings[2], resources.getString(R.string.haromkiralyok));
		announcementToName.put(Announcements.zaroparos, resources.getString(R.string.zaroparos));
		announcementToName.put(Announcements.pagatfacan, resources.getString(R.string.pagatfacan));
		announcementToName.put(Announcements.sasfacan, resources.getString(R.string.sasfacan));
		for (int i = 0; i < 4; i++)
		{
			Szincsalad kisszincsalad = Announcements.kisszincsaladok[i];
			Szincsalad nagyszincsalad = Announcements.nagyszincsaladok[i];
			announcementToName.put(kisszincsalad, suitToName.get(i) + " " + resources.getString(R.string.kisszincsalad));
			announcementToName.put(nagyszincsalad, suitToName.get(i) + " " + resources.getString(R.string.nagyszincsalad));
		}
		
		for (Card card : Announcements.ultimok.keySet())
		{
			Map<Integer, TakeRoundWithCard> map = Announcements.ultimok.get(card);
			for (int roundIndex : map.keySet())
			{
				String roundResName = "round" + roundIndex;
				int roundResID = resources.getIdentifier(roundResName, "string", context.getPackageName());
				String annName = cardToName.get(card) + " " + resources.getString(roundResID);
				announcementToName.put(map.get(roundIndex), annName);
			}
		}
		
		for (Announcement a : Announcements.getAll())
		{
			if (!announcementToName.containsKey(a)) announcementToName.put(a, a.getClass().getSimpleName());
		}
	}
}
