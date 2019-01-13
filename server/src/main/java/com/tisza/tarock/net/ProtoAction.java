package com.tisza.tarock.net;

import com.tisza.tarock.game.*;
import com.tisza.tarock.game.card.*;
import com.tisza.tarock.message.*;
import com.tisza.tarock.proto.*;

import java.util.stream.*;

public class ProtoAction implements Action
{
	private final ActionProto.Action actionProto;
	private final PlayerSeat player;

	public ProtoAction(PlayerSeat player, ActionProto.Action actionProto)
	{
		this.player = player;
		this.actionProto = actionProto;
	}

	@Override
	public void handle(ActionHandler handler)
	{
		switch (actionProto.getActionTypeCase())
		{
			case BID:
				handler.bid(player, actionProto.getBid().getBid());
				break;
			case CHANGE:
				handler.change(player, actionProto.getChange().getCardList().stream().map(Card::fromId).collect(Collectors.toList()));
				break;
			case CALL:
				handler.call(player, Card.fromId(actionProto.getCall().getCard()));
				break;
			case ANNOUNCE:
				handler.announce(player, Utils.announcementFromProto(actionProto.getAnnounce().getAnnouncement()));
				break;
			case ANNOUCE_PASSZ:
				handler.announcePassz(player);
				break;
			case PLAY_CARD:
				handler.playCard(player, Card.fromId((actionProto.getPlayCard().getCard())));
				break;
			case READY_FOR_NEW_GAME:
				handler.readyForNewGame(player);
				break;
			case THROW_CARDS:
				handler.throwCards(player);
				break;
			case CHAT:
				handler.chat(player, actionProto.getChat().getMessage());
				break;
			default:
				System.err.println("unkown action: " + actionProto.getActionTypeCase());
				break;
		}

	}
}
