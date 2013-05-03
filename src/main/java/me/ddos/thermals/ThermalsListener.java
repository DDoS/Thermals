package me.ddos.thermals;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

/**
 *
 * @author DDoS
 */
public class ThermalsListener implements Listener {
	private final ThermalsPlugin plugin;

	public ThermalsListener(ThermalsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRedstoneChange(BlockRedstoneEvent event) {
		final Block block = event.getBlock();
		plugin.getHeatManager().queueHeatIncrement(block.getX(), block.getZ());
	}
}
