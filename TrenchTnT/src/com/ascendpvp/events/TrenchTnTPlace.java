package com.ascendpvp.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.ascendpvp.TrenchTnTMain;
import com.ascendpvp.utils.Helpers;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;


public class TrenchTnTPlace implements Listener {

	TrenchTnTMain plugin;
	public TrenchTnTPlace(TrenchTnTMain plugin) {
		this.plugin = plugin;
	} 
	Helpers help = new Helpers();

	HashMap<String, Location> timeout = new HashMap<String, Location>();

	@EventHandler
	public void onPlaceTrench(BlockPlaceEvent e) {

		int maxY = 0;
		Player p = (Player) e.getPlayer();
		MPlayer fplayer = MPlayer.get(e.getPlayer());
		Faction factionAt = BoardColl.get().getFactionAt(PS.valueOf(e.getBlockPlaced().getLocation()));
		String printPrice = String.valueOf(plugin.getConfig().getInt("place_tnt_price"));
		String printLayers = String.valueOf(plugin.getConfig().getInt("layers_to_break"));
		String pName = p.getName();

		//If-not-return checks
		if(!e.getItemInHand().hasItemMeta()) return;
		if(e.getItemInHand().getItemMeta().getDisplayName() == null) return;
		if(!e.getItemInHand().getItemMeta().getDisplayName().equals(help.cc(plugin.getConfig().getString("trench_tnt_name")))) return;
		e.setCancelled(true);
		if(!p.hasPermission("trenchtnt.use")) {
			p.sendMessage(help.cc(plugin.getConfig().getString("messages.no_permission_place")));
			return;
		}
		if(e.getItemInHand().getAmount() != 1) {
			p.sendMessage(help.cc(plugin.getConfig().getString("messages.trench_tnt_stacked")));
			return;
		}
		int tntBal = Integer.parseInt(e.getItemInHand().getItemMeta().getLore().get(0).split(" ")[2]);
		if(!(tntBal >= 1000)) {
			p.sendMessage(help.cc(plugin.getConfig().getString("messages.not_enough_tnt").replaceAll("#placePrice#", printPrice)));
			return;
		}
		if(!factionAt.getMPlayers().contains(fplayer)) return;

		//Give player a certian amount of time to replace the block in the same location
		if(!timeout.containsKey(pName)) {
			timeout.put(pName, e.getBlockPlaced().getLocation());
			new BukkitRunnable() {
				public void run() {	
					if(timeout.containsKey(pName)) {
						p.sendMessage(help.cc(plugin.getConfig().getString("messages.confirm_timeout")));
						timeout.remove(pName);
					}
				}
			}.runTaskLater(this.plugin, plugin.getConfig().getLong("place_timeout") * 20L);
			p.sendMessage(help.cc(plugin.getConfig().getString("messages.confirm_placement")));
			return;
		}

		//Remove player from timeout if player doesn't replace block in same location
		if(!e.getBlockPlaced().getLocation().equals(timeout.get(pName))) {
			timeout.remove(pName);
			p.sendMessage(help.cc(plugin.getConfig().getString("messages.confirm_not_same_block")));
			return;
		}

		//Remove player from timeout and remove balance from trenchtnt
		timeout.remove(pName);
		ArrayList<Block> removeIncrement = new ArrayList<Block>();
		ItemStack hand = e.getPlayer().getItemInHand();
		ItemMeta meta = hand.getItemMeta();
		int currentBalance = Integer.parseInt(meta.getLore().get(0).split(" ")[2]);
		int newBalance = currentBalance - plugin.getConfig().getInt("place_tnt_price");
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Tnt Balance: " + newBalance));
		hand.setItemMeta(meta);
		Chunk placedAtChunk = e.getBlockPlaced().getChunk();

		heightCheckLoop:
			//Iterate from the top of the skybox in the selected chunk and find the highest block in the Y axis
			for(int y=256; y > 0; y--) {
				for(int x=0; x<16; x++) {
					for(int z=0; z<16; z++) {
						Block blockCheck = placedAtChunk.getBlock(x, y, z);
						Material bt = blockCheck.getType();
						if(bt != Material.AIR && bt != Material.WATER && bt != Material.STATIONARY_WATER && bt != Material.MOB_SPAWNER && bt != Material.CHEST && bt != Material.TRAPPED_CHEST && bt != Material.SIGN && bt != Material.HOPPER && bt != Material.OBSIDIAN && bt != Material.LAVA && bt != Material.STATIONARY_LAVA) {
							maxY = y;
							break heightCheckLoop;
						}
					}
				}
			}
		//Start with the max "Y" value from the loop ^ and begin breaking blocks downward
		for(int y = maxY; y > maxY - plugin.getConfig().getInt("layers_to_break"); y--) {
			for(int x=0;x<16;x++) {
				for(int z=0;z<16;z++) {
					Block blockRemove = placedAtChunk.getBlock(x, y, z);
					Material bt = blockRemove.getType();
					if(bt != Material.BEDROCK && bt != Material.WATER && bt != Material.STATIONARY_WATER && bt != Material.MOB_SPAWNER && bt != Material.CHEST && bt != Material.TRAPPED_CHEST && bt != Material.SIGN && bt != Material.HOPPER && bt != Material.OBSIDIAN && bt != Material.AIR && bt != Material.LAVA && bt != Material.STATIONARY_LAVA) {
						removeIncrement.add(blockRemove);
					}
				}
			}
		}

		//Create bukkit runnable to iterate through all Blocks in ArrayList and remove ONE at a time every TICK for visual effect
		Iterator<Block> itr = removeIncrement.iterator();
		new BukkitRunnable() {
			public void run() {
				blockspeedloop:
					for(int removeSpeed=plugin.getConfig().getInt("blocks_per_tick"); removeSpeed > 0; removeSpeed--) {
						if(itr.hasNext()) {
							Block remove = itr.next();
							int removeX = remove.getLocation().getBlockX();
							int removeY = remove.getLocation().getBlockY();
							int removeZ = remove.getLocation().getBlockZ();
							e.getPlayer().getWorld().createExplosion(removeX, removeY+1, removeZ, 0, false, false);
							remove.setType(Material.AIR);
							itr.remove();
						} else {
							cancel();
							p.sendMessage(help.cc(plugin.getConfig().getString("messages.place_success").replaceAll("#layerAmount#", printLayers)));
							break blockspeedloop;
						}
					}
			}
		}.runTaskTimer(this.plugin, plugin.getConfig().getLong("wait_before_trench"), 1L);

		//Spawn a falling block with a tnt texture and add particles for effect
		Location spawnLoc = e.getBlockPlaced().getLocation().add(0, plugin.getConfig().getInt("spawn_falling_tnt_height"), 0);
		@SuppressWarnings("deprecation")
		Entity tnt = p.getWorld().spawnFallingBlock(spawnLoc, Material.TNT, (byte)0);
		//Give tnt custom metadata
		tnt.setMetadata("test", new FixedMetadataValue(plugin, 2121212121));
		p.sendMessage(help.cc(plugin.getConfig().getString("messages.place_started")));
		new BukkitRunnable() {
			public void run() {
				Location loc = e.getBlockPlaced().getLocation();
				loc.getWorld().playEffect(loc, Effect.EXPLOSION_HUGE, 1);

				//Math to spawn fire particles sporadically
				for(int i = 0; i <360; i+=5){
					Location flameloc = loc;
					double flameX  = flameloc.getBlockX() + 0.5;
					double flameZ = flameloc.getBlockZ() + 0.5;
					flameloc.setZ(flameZ + Math.cos(i)*1);
					flameloc.setX(flameX + Math.sin(i)*1);
					loc.getWorld().playEffect(flameloc, Effect.FLAME, 1);
				}
			}
		}.runTaskLater(this.plugin, plugin.getConfig().getLong("wait_before_particles"));
	}

	//Seperate event for removing the falling tnt block based on custom metadata
	@SuppressWarnings("static-access")
	@EventHandler
	public void tntFallCancel(EntityChangeBlockEvent e) {
		if(!e.getEntityType().equals(e.getEntityType().FALLING_BLOCK)) return;
		if(!e.getEntity().hasMetadata("test")) return;
		e.setCancelled(true);
	}
}