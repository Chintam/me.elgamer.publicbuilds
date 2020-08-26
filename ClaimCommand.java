package me.elgamer.publicbuilds.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ClaimCommand implements CommandExecutor {
	
	public ClaimCommand() {
	}
	
	private int guestArea = 400;
	private int apprenticeArea = 750;
	private int builderArea = 1000;
	
	
	private int area = 0;
	private int width = 0;
	private int length = 0;	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You cannot create a plot!");
			return true;
		}
		
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("plot")) {

			if (p.hasPermission("group.builder")) {
				if (createRegion(builderArea, p)) {
					return true;
				}
			}
			else if (p.hasPermission("group.apprentice")) {
				if (createRegion(apprenticeArea, p)) {
					return true;
				}
			}
			else {
				if (createRegion(guestArea, p)) {
					return true;
				}
			}
			return true;
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
	
	private int getArea(Selection sel) {
		
		width = sel.getWidth();
		length = sel.getLength();
		
		area = width*length;
		return area;
	}
	
	private boolean createRegion(int maxArea, Player p) {
		
			Selection sel = getWorldEdit().getSelection(p);
			
		if (sel == null) {
			p.sendMessage(ChatColor.RED + "You must make a selection!");
			return true; 
		}
						
		area = getArea(sel);

			
		if (area > maxArea) {
			p.sendMessage(ChatColor.RED + "Your selection exceeds the maximum of " + maxArea + " blocks");
			return true;
		}
		
		World claimWorld = Bukkit.getServer().getWorld("claimWorld");
		//World buildWorld = Bukkit.getServer().getWorld("buildWorld");
		
		RegionContainer container = getWorldGuard().getRegionContainer();
		RegionManager claimRegions = container.get(claimWorld);
		//RegionManager buildRegions = container.get(buildWorld);
			
		ProtectedRegion region = new ProtectedCuboidRegion("plot" + p.getName(),
				new BlockVector(sel.getNativeMinimumPoint()), 
				new BlockVector(sel.getNativeMaximumPoint())
		);
		
		ApplicableRegionSet set = claimRegions.getApplicableRegions(region);
		if (set.size() != 0) {
			p.sendMessage(ChatColor.RED + "Your selection overlaps with another claim!");
			return true;
		}
			
		DefaultDomain owners = new DefaultDomain();
		owners.addPlayer(getWorldGuard().wrapPlayer(p));
			
		region.setOwners(owners);
		
		claimRegions.addRegion(region);
		//buildRegions.addRegion(region);
		
			
		p.sendMessage(ChatColor.GREEN + "Created plot with ID" + region.getId());
		
		return true;
	}
}
