package com.inrevo.puppyvet;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
	@Override
	public void onEnable(){
		getLogger().info("Enabling PuppyVet");
	}

	@Override
	public void onDisable() {
		getLogger().info("Disabling PuppyVet");
	}

	private boolean processPackHereCommand(Player player)
	{
		
		final Location loc = player.getLocation();
		final World world = player.getWorld();
		int countMoved=0;
		
		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity entity : chunk.getEntities())
			{
				if (entity instanceof Wolf)
				{
					Wolf wolf = (Wolf)entity;
					if (wolf.isTamed())
					{
						AnimalTamer target = wolf.getOwner();
						if (target != null)
						{
							if (target == player)
							{
								wolf.teleport(loc);
								countMoved++;
							}
						}
					}
				}
			}
		}
		String summary = String.format("Found %d of your pack", countMoved);
		player.sendMessage(summary);
		return true;
	}

	private boolean processWhosePackCommand(Player player, String[] args)
	{
		int radius = 10;
		final World world = player.getWorld();
		if (args.length > 0)
		{
			try
			{
				radius = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException e1)
			{
				// nothing to see, move along here 
			}
		}
		if (radius > 20)
			radius = 20;
		
		final Location loc = player.getLocation();
		int squareRadius = radius * radius;
		Map<String, Integer> ownerData = new HashMap<String, Integer>();

		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity entity : chunk.getEntities())
			{
				if (loc != null)
				{
					if (loc.distanceSquared(entity.getLocation()) > squareRadius)
					{
						continue;
					}
				}
				if (entity instanceof Wolf)
				{
					Wolf wolf = (Wolf)entity;
					if (wolf.isTamed())
					{
						AnimalTamer target = wolf.getOwner();
						String name = target.getName();
						Integer count = ownerData.get(name);
						if (count == null)
						{
							ownerData.put(name, 1);							
						}
						else 
						{
							ownerData.put(name, count + 1);
						}
					}
				}
			}
		}
		if (ownerData.isEmpty())
		{
			String summary = String.format("There were no tamed dogs found within %d blocks", radius);
			player.sendMessage(summary);
		}
		else
		{
			String resultString = "The following tamed wolves were found: ";
			Iterator<?> entries = ownerData.entrySet().iterator();
			while (entries.hasNext()) 
			{
			  Entry<?, ?> thisEntry = (Entry<?, ?>) entries.next();
			  String owner = (String) thisEntry.getKey();
			  Integer numDogs = (Integer) thisEntry.getValue();
			  String thisGuysDogDetails = String.format("\n%s has %d dogs", owner, numDogs);
			  resultString = resultString + thisGuysDogDetails; 
			}
			player.sendMessage(resultString);
		}
		return true;
	}

	private boolean processCalmCommand(Player player, String[] args)
	{
		int radius = 10;
		final World world = player.getWorld();
		if (args.length > 0)
		{
			try
			{
				radius = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException e1)
			{
				// nothing to see, move along here 
			}
		}
		final Location loc = player.getLocation();
		int squareRadius = radius * radius;
		int countAll=0;
		int countCalmed=0;

		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity entity : chunk.getEntities())
			{
				if (loc != null)
				{
					if (loc.distanceSquared(entity.getLocation()) > squareRadius)
					{
						continue;
					}
				}
				if (entity instanceof Wolf)
				{
					Wolf wolf = (Wolf)entity;
					if (wolf.isAngry())
					{
						wolf.setAngry(false);
						if (!wolf.isTamed()) // don't reset the owner of tamed dogs
						{
							wolf.setTarget(null);
						}
						countCalmed++;
					}
					countAll++;
				}
			}
		}
		String summary = String.format("Calmed %d of %d wolves/dogs in a radius of %d", countCalmed, countAll, radius);
		player.sendMessage(summary);
		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("calm"))
		{ 
			if (!(sender instanceof Player)) 
			{
				sender.sendMessage("This command can only be run by a player.");
			} 
			else 
			{
				Player player = (Player) sender;
				return processCalmCommand(player, args);
			}
		}
		if(cmd.getName().equalsIgnoreCase("packhere"))
		{ 
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			} 
			else 
			{
				Player player = (Player) sender;
				return processPackHereCommand(player);
			}			
		}
		if(cmd.getName().equalsIgnoreCase("whosepack"))
		{ 
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			} 
			else 
			{
				Player player = (Player) sender;
				return processWhosePackCommand(player, args);
			}			
		}		
		return false; 
	}
}


