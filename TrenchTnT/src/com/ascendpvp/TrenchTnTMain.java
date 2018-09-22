package com.ascendpvp;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.ascendpvp.commands.TrenchTnTAddBal;
import com.ascendpvp.events.TrenchTnTAntiPlace;
import com.ascendpvp.events.TrenchTnTChestClick;
import com.ascendpvp.events.TrenchTnTHelp;
import com.ascendpvp.events.TrenchTnTPlace;

import net.milkbowl.vault.economy.Economy;


public class TrenchTnTMain extends JavaPlugin {
	
    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    
	public void onEnable() {
		getCommand("trenchtntadmin").setExecutor(new TrenchTnTAddBal(this));
		Bukkit.getPluginManager().registerEvents(new TrenchTnTChestClick(this), this);
		Bukkit.getPluginManager().registerEvents(new TrenchTnTHelp(this), this);
		Bukkit.getPluginManager().registerEvents(new TrenchTnTPlace(this), this);
		Bukkit.getPluginManager().registerEvents(new TrenchTnTAntiPlace(this), this);
		saveDefaultConfig();
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
	}
	
	//Eco setup
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public static Economy getEcononomy() {
        return econ;
    }
}
