package com.ascendpvp.commands;

import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ascendpvp.TrenchTnTMain;
import com.ascendpvp.utils.Helpers;

public class TrenchTnTAddBal implements CommandExecutor {

	TrenchTnTMain plugin;
	public TrenchTnTAddBal(TrenchTnTMain plugin) {
		this.plugin = plugin;
	}
	Helpers help = new Helpers();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Basic checks
		if(!(sender instanceof Player)) return false;
		Player p = (Player) sender;
		if(args.length != 2) return false;
		if(!p.hasPermission("trenchtnt.admin")) return false;
		if(!args[0].equalsIgnoreCase("addbal")) {
			p.sendMessage(help.cc("&bCorrect usage: /trenchtntadmin addbal (amount)"));
			return false;
		}
		if(!help.isInt(args[1])) {
			p.sendMessage(help.cc("&bYou're an admin! You should know how to input a valid amount!"));
			return false;
		}

		//Change lore of trenchtnt
		int argToInt = Integer.parseInt(args[1]);
		ItemStack hand = p.getItemInHand();
		ItemMeta meta = hand.getItemMeta();
		int currentBalance = Integer.parseInt(meta.getLore().get(0).split(" ")[2]);
		int newBalance = currentBalance + argToInt;
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Tnt Balance: " + newBalance));
		hand.setItemMeta(meta);
		return false;
	}
}
