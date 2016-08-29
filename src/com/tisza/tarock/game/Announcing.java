package com.tisza.tarock.game;

import java.util.*;

import com.tisza.tarock.announcement.*;
import com.tisza.tarock.card.*;
import com.tisza.tarock.game.Bidding.Invitation;

public class Announcing
{
	private final PlayerPairs playerPairs;
	private int currentPlayer;
	
	private int lastAnnouncer = -1;
	private IdentityTracker idTrack;
	
	private Map<Announcement, AnnouncementState> announcementStates = new HashMap<Announcement, AnnouncementState>();
	
	private AllPlayersCards playerHands;
	
	public Announcing(PlayerPairs pp, Invitation invit)
	{
		playerPairs = pp;
		currentPlayer = playerPairs.getCaller();
		idTrack = new IdentityTracker(playerPairs, invit);
		
		for (Announcement a : Announcements.getAll())
		{
			announcementStates.put(a, new AnnouncementState());
		}
		
		announce(playerPairs.getCaller(), Announcements.game);
	}
	
	public boolean announce(int player, Announcement announcement)
	{
		if (isFinished())
			throw new IllegalStateException();
		
		if (player != currentPlayer)
			return false;
		
		if (announcement == null)
		{
			currentPlayer++;
			return true;
		}
		
		if (announcement != null && !idTrack.isIdentityKnown(player))
			return false;
		
		Team team = playerPairs.getTeam(player);
		announcementStates.get(announcement).team(team).announce();
		idTrack.identityRevealed(player);
		lastAnnouncer = currentPlayer;
		
		return true;
	}
	
	public boolean contra(int player, Contra contra)
	{
		if (isFinished())
			throw new IllegalStateException();
		
		if (player != currentPlayer)
			return false;
		
		Team playerTeam = playerPairs.getTeam(player);
		Team announcerTeam = contra.isSelf() ? playerTeam : playerTeam.getOther();
		AnnouncementState.PerTeam as = announcementStates.get(contra.getAnnouncement()).team(announcerTeam);
		if (as.isAnnounced() && as.getNextTeamToContra() == playerTeam && as.getContraLevel() == contra.getLevel())
		{
			as.contra();
			idTrack.identityRevealed(player);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public List<Contra> getPossibleContras()
	{
		List<Contra> result = new ArrayList<Contra>();
		for (Announcement a : announcementStates.keySet())
		{
			AnnouncementState as = announcementStates.get(a);
			for (Team t : Team.values())
			{
				AnnouncementState.PerTeam ast = as.team(t);
				if (ast.isAnnounced() && ast.getNextTeamToContra() == playerPairs.getTeam(currentPlayer))
				{
					result.add(new Contra(a, ast.getContraLevel()));
				}
			}
		}
		return result;
	}
	
	public boolean isFinished()
	{
		return lastAnnouncer == currentPlayer;
	}
	
	private static class IdentityTracker
	{
		private final PlayerPairs playerPairs;
		private final Invitation invit;
		private boolean[] identityKnown = new boolean[4];
		
		public IdentityTracker(PlayerPairs pp, Invitation i)
		{
			playerPairs = pp;
			invit = i;
			identityKnown[playerPairs.getCaller()] = true;
			if (invit != Invitation.NONE)
			{
				Arrays.fill(identityKnown, true);
			}
		}
		
		public void identityRevealed(int player)
		{
			if (player == playerPairs.getCalled())
			{
				Arrays.fill(identityKnown, true);
			}
			identityKnown[player] = true;
		}
		
		public boolean isIdentityKnown(int player)
		{
			return identityKnown[player];
		}
	}
}