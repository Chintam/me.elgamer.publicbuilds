package me.elgamer.publicbuilds;

import org.bukkit.plugin.java.JavaPlugin;

import me.elgamer.publicbuilds.commands.ClaimCommand;
import me.elgamer.publicbuilds.listeners.ClaimEnter;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		
		new ClaimEnter(this);
		
		//Commands
		this.getCommand("plot").setExecutor(new ClaimCommand());
	
	}
}