package me.elgamer.publicbuilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.elgamer.publicbuilds.Main;

public class ClaimCommand {
	

	
	public ClaimCommand(Main plugin) {
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You cannot create a plot!");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("plot")) {
			Selection sel = getWorldEdit().getSelection(p);
			
			if (sel == null) {
				p.sendMessage(ChatColor.RED + "You must make a selection!");
				return true; 
			}
			
			ProtectedRegion region = new ProtectedCuboidRegion("plot" + p.getName(),
					new BlockVector(sel.getNativeMinimumPoint()), 
					new BlockVector(sel.getNativeMaximumPoint())
			);
			
			DefaultDomain owners = new DefaultDomain();
			owners.addPlayer(getWorldGuard().wrapPlayer(p));
			
			region.setOwners(owners);
			
			getWorldGuard().getRegionManager(p.getWorld()).addRegion(region);
			
			p.sendMessage(ChatColor.GREEN + "Created plot with ID" + region.getId());
		}
		
		return true;
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}
		
		return (WorldGuardPlugin) plugin;
	}
	
	private WorldEditPlugin getWorldEdit() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		
		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			return null;
		}
		
		return (WorldEditPlugin) plugin;
	}
}
