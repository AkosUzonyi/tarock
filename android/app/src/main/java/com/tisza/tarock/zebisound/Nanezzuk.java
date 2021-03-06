package com.tisza.tarock.zebisound;

import android.content.*;
import com.tisza.tarock.*;
import com.tisza.tarock.game.*;
import com.tisza.tarock.game.card.*;

import java.util.*;

class Nanezzuk extends ZebiSound
{
	public Nanezzuk(Context context)
	{
		super(context, 0.2F, R.raw.nanezzuk);
	}

	@Override
	public void phaseChanged(PhaseEnum phase)
	{
		if (phase == PhaseEnum.GAMEPLAY)
			activate();
	}
}
