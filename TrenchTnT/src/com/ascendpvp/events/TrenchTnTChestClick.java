package com.ascendpvp.events;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ascendpvp.TrenchTnTMain;
import com.ascendpvp.utils.Helpers;

public class TrenchTnTChestClick implements Listener {

	TrenchTnTMain plugin;
	public TrenchTnTChestClick(TrenchTnTMain plugin) {
		this.plugin = plugin;
	}
	Helpers help = new Helpers();

	@EventHandler
	public void onChestClick(PlayerInteractEvent e){

		if(e.getPlayer().getItemInHand() == null) return;
		if(e.getPlayer().getItemInHand().getType() == Material.AIR) return;
		if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName() == null) return;
		String iName = e.getPlayer().getItemInHand().getItemMeta().getDisplayName();
		if(e.getAction() != Action.LEFT_CLICK_BLOCK) return;

		if(e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
			if(!iName.equals(help.cc(plugin.getConfig().getString("trench_tnt_name")))) return;
			//Check if custom trenchtnt item is stacked
			if(e.getPlayer().getItemInHand().getAmount() > 1){
				e.getPlayer().sendMessage(help.cc(plugin.getConfig().getString("messages.trench_tnt_stacked")));
				return;
			}

			//Iteration to determine amount of tnt in chest clicked
			Chest clickedchest = (Chest)e.getClickedBlock().getState();
			int amountToAdd = 0;
			ItemStack[] arrayOfItemStack;
			int i = (arrayOfItemStack = clickedchest.getInventory().getContents()).length;
			clickedchest.getInventory().remove(Material.TNT);
			for (int y = 0; y < i; y++) { 
				ItemStack z = arrayOfItemStack[y];
				if (z == null) continue;
				if (!z.getType().equals(Material.TNT)) continue;
				amountToAdd += z.getAmount();
			}

			//Add balance/change lore to trenchtnt item
			ItemStack iteminhand = e.getPlayer().getItemInHand();
			ItemMeta itemmeta = iteminhand.getItemMeta();
			String loress[] = itemmeta.getLore().get(0).split(" ");
			int current = Integer.parseInt(loress[2]);
			ArrayList<String> lores = new ArrayList<String>();
			e.getPlayer().sendMessage(help.cc(plugin.getConfig().getString("messages.tnt_added").replaceAll("#amount#", String.valueOf(amountToAdd))));
			int newamount = current + amountToAdd;
			lores.add(ChatColor.WHITE + "Tnt Balance: " + newamount);
			itemmeta.setLore(lores);
			iteminhand.setItemMeta(itemmeta);
		}
	}
}
