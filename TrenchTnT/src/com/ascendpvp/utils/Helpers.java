package com.ascendpvp.utils;

import org.bukkit.ChatColor;

public class Helpers {
	
	public String cc(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public boolean isInt(String arg) {
		try {
			Integer.parseInt(arg);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
