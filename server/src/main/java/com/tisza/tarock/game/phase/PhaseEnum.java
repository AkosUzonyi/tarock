package com.tisza.tarock.game.phase;

public enum PhaseEnum
{
	BIDDING, CHANGING, CALLING, ANNOUNCING, GAMEPLAY, END, INTERRUPTED;
	
	public boolean isAfter(PhaseEnum phase)
	{
		if (this == INTERRUPTED)
			return false;
		
		return ordinal() > phase.ordinal();
	}
}
