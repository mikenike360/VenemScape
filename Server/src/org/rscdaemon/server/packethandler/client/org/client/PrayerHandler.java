package org.rscdaemon.server.packethandler.client;

import org.rscdaemon.server.packethandler.PacketHandler;
import org.rscdaemon.server.model.*;
import org.rscdaemon.server.net.Packet;
import org.rscdaemon.server.net.RSCPacket;
import org.rscdaemon.server.entityhandling.EntityHandler;
import org.rscdaemon.server.entityhandling.defs.PrayerDef;
import org.apache.mina.common.IoSession;

public class PrayerHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();
//loginConnector
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		int pID = ((RSCPacket)p).getID();
		int prayerID = (int)p.readByte();
		if(prayerID < 0 || prayerID >= 14) {
			player.setSuspiciousPlayer(true);
			player.getActionSender().sendPrayers();
			return;
		}
		if(player.isDueling() && player.getDuelSetting(2)) {
			player.getActionSender().sendMessage("Prayer is disabled in this duel");
			player.getActionSender().sendPrayers();
			return;
		}
		PrayerDef prayer = EntityHandler.getPrayerDef(prayerID);
		switch(pID) {
			case 56:
				if(player.getMaxStat(5) < prayer.getReqLevel()) {
					player.setSuspiciousPlayer(true);
					player.getActionSender().sendMessage("Your prayer ability is not high enough to use this prayer");
					break;
				}
				if(player.getCurStat(5) <= 0) {
					player.setPrayer(prayerID, false);
					player.getActionSender().sendMessage("You have run out of prayer points. Return to a church to recharge");
					break;
				}
				activatePrayer(player, prayerID);
				break;
			case 248:
				deactivatePrayer(player, prayerID);
				break;
		}
		player.getActionSender().sendPrayers();
	}
	
	private boolean activatePrayer(Player player, int prayerID) {
		if(!player.isPrayerActivated(prayerID)) {
			if(prayerID == 11) {
				deactivatePrayer(player, 5);
				deactivatePrayer(player, 2);
			}
			else if(prayerID == 5) {
				deactivatePrayer(player, 2);
				deactivatePrayer(player, 11);
			}
			else if(prayerID == 2) {
				deactivatePrayer(player, 5);
				deactivatePrayer(player, 11);
			}
			else if(prayerID == 10) {
				deactivatePrayer(player, 4);
				deactivatePrayer(player, 1);
			}
			else if(prayerID == 4) {
				deactivatePrayer(player, 10);
				deactivatePrayer(player, 1);
			}
			else if(prayerID == 1) {
				deactivatePrayer(player, 10);
				deactivatePrayer(player, 4);
			}
			else if(prayerID == 9) {
				deactivatePrayer(player, 3);
				deactivatePrayer(player, 0);
			}
			else if(prayerID == 3) {
				deactivatePrayer(player, 9);
				deactivatePrayer(player, 0);
			}
			else if(prayerID == 0) {
				deactivatePrayer(player, 9);
				deactivatePrayer(player, 3);
			}
      			player.addPrayerDrain(prayerID);
      			player.setPrayer(prayerID, true);
      			return true;
      		}
      		return false;
	}
	
	private boolean deactivatePrayer(Player player, int prayerID) {
    		if(player.isPrayerActivated(prayerID)) {
    			player.removePrayerDrain(prayerID);
    			player.setPrayer(prayerID, false);
    			return true;
    		}
    		return false;
	}
	
}