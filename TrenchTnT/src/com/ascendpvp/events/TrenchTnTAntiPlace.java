package com.ascendpvp.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import com.ascendpvp.TrenchTnTMain;
import com.ascendpvp.utils.Helpers;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

public class TrenchTnTAntiPlace implements Listener {
	TrenchTnTMain plugin;
	public TrenchTnTAntiPlace(TrenchTnTMain plugin){
		this.plugin = plugin;
	}
	Helpers help = new Helpers();

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {

		MPlayer fplayer = MPlayer.get(e.getPlayer());
		Faction factionAtEvent = BoardColl.get().getFactionAt(PS.valueOf(e.getBlockPlaced().getLocation()));

		//Basic checks
		if(e.getItemInHand() == null) return;
		if(e.getItemInHand().getType() == Material.AIR) return;
		if(e.getItemInHand().getItemMeta().getDisplayName() == null) return;
		if(!e.getItemInHand().getItemMeta().getDisplayName().equals(help.cc( plugin.getConfig().getString("trench_tnt_name")))) return;
		if(factionAtEvent.getMPlayers().contains(fplayer)) return;
		e.setCancelled(true);
		e.getPlayer().sendMessage(help.cc( plugin.getConfig().getString("messages.place_not_own_land")));
	}
}