package me.TheTealViper.chatbubbles.citizens;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.TheTealViper.chatbubbles.ChatBubbles;
import me.TheTealViper.chatbubbles.utils.FormatUtils;

public class CitizensDHChatbubble {
	
	private ChatBubbles plugin;
	
	public CitizensDHChatbubble(ChatBubbles main) {
		this.plugin = main;		
	}
	
	public void createBubbleHD(LivingEntity p, String msg) {
		boolean requirePerm = plugin.getConfig().getBoolean("Citizens_Bubbles_Require_See_Permission");
		String seePerm = plugin.getConfig().getString("Citizens_Bubbles_See_Permission");
		if(plugin.dhHandler.existingHolograms.containsKey(p.getUniqueId())) {
			for(Hologram h : plugin.dhHandler.existingHolograms.get(p.getUniqueId())) {
				if(!h.isEnabled())
					h.delete();
			}
		}
		final Hologram hologram = DHAPI.createHologram(FormatUtils.getRandomString(), p.getLocation().add(0.0, plugin.getCBConfig().bubbleOffset, 0.0), false);
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		plugin.dhHandler.existingHolograms.put(p.getUniqueId(), hList);
		if(requirePerm) {
			hologram.setPermission(seePerm);
		}
		int lines = plugin.dhHandler.formatHologramLines(p, hologram, msg);

		new BukkitRunnable() {
			int ticksRun = 0;
			@Override
			public void run() {
				ticksRun++;
				if(hologram.isEnabled())
					DHAPI.moveHologram(hologram, p.getLocation().add(0.0, plugin.getCBConfig().bubbleOffset + .25 * lines, 0.0));
				if (ticksRun > plugin.getCBConfig().life) {
					hologram.disable();
					hologram.delete();
					cancel();
				}
		}}.runTaskTimer(plugin, 1L, 1L);
		
		if(plugin.getConfig().getBoolean("ChatBubble_Play_Sound")) {
			String sound = plugin.getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
			float volume = (float) plugin.getConfig().getDouble("ChatBubble_Sound_Volume");
			if(!sound.equals("")) {
				try {
					p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
				}catch(Exception e) {
					Bukkit.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					Bukkit.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
	}
}
    