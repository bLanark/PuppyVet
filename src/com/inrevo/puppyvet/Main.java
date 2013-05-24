package com.inrevo.puppyvet;


import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
		getLogger().info("Looking for your pack...");
		
		final Location loc = player.getLocation();
		getLogger().info("b");
		final World world = player.getWorld();
		getLogger().info("c");
		int countMoved=0;
		
		getLogger().info("0");
		for (Chunk chunk : world.getLoadedChunks())
		{
			getLogger().info("1");
			for (Entity entity : chunk.getEntities())
			{
				getLogger().info("2");
				if (entity instanceof Wolf)
				{
					getLogger().info("3");
					Wolf wolf = (Wolf)entity;
					if (wolf.isTamed())
					{

						getLogger().info("4");
						if (wolf.getTarget() == player)
						{
							getLogger().info("5");
							wolf.teleport(loc);
							countMoved++;
						}
					}
				}
			}
		}
		String summary = String.format("Found %d of your pack", countMoved);
		player.sendMessage(summary);
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
		int realRadius = radius * radius;
		int countAll=0;
		int countCalmed=0;

		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity entity : chunk.getEntities())
			{
				if (loc != null)
				{
					if (loc.distanceSquared(entity.getLocation()) > realRadius)
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
				player.sendMessage("-1");
				return processPackHereCommand(player);
			}			
		}
		return false; 
	}
}


