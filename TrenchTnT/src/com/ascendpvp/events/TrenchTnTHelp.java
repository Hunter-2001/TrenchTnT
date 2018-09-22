package com.ascendpvp.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.ascendpvp.TrenchTnTMain;
import com.ascendpvp.utils.Helpers;

public class TrenchTnTHelp implements Listener {

	TrenchTnTMain plugin;
	public TrenchTnTHelp(TrenchTnTMain plugin) {
		this.plugin = plugin;
	}
	Helpers help = new Helpers();

	@EventHandler
	public void onClick(PlayerInteractEvent e) {

		//Basic checks
		Action eA = e.getAction();
		if(e.getPlayer().getItemInHand() == null) return;
		if(e.getPlayer().getItemInHand().getType() == Material.AIR) return;
		if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName() == null) return;

		if(eA == Action.LEFT_CLICK_BLOCK || eA == Action.LEFT_CLICK_AIR) {
			if(!e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(help.cc(plugin.getConfig().getString("trench_tnt_name")))) return;
			e.getPlayer().sendMessage(help.cc(plugin.getConfig().getString("messages.help_message")));
		}
	}
}

