package me.TheTealViper.chatbubbles.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.TheTealViper.chatbubbles.ChatBubbles;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.ai.speech.event.NPCSpeechEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class ChatBubbleTrait extends Trait {
	
	ChatBubbles plugin = null;
	
	public ChatBubbleTrait() {
		super("chatbubble");
		plugin = JavaPlugin.getPlugin(ChatBubbles.class);
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled = true)
	public void onNPCSpeech(NPCSpeechEvent event) {
		if (this.npc != event.getNPC()) return;
	    if ((event.getNPC() != null) && (event.getNPC().isSpawned())) {
	    	NPC talker = event.getNPC();
	    	if ((talker.getEntity() instanceof LivingEntity))
	    	{
	    		SpeechContext sp = event.getContext();
	    		String msg = sp.getMessage();
	    		LivingEntity p = (LivingEntity)talker.getEntity();
	    		createBubble(p, msg);
	    		if(plugin.chatBubbleOverridesNPCChat)
	    			event.setCancelled(true);
	    	}
	    }
	}
	
	@Override
	public void onAttach() {
		plugin.getServer().getLogger().info(npc.getName() + " has been assigned trait ChatBubble!");
	}
	
	public void createBubble(LivingEntity p, String msg) {
		if(plugin.existingHolograms.containsKey(p.getUniqueId())) {
			for(Hologram h : plugin.existingHolograms.get(p.getUniqueId())) {
				if(!h.isDeleted())
					h.delete();
			}
		}
		final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, plugin.bubbleOffset, 0.0));
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		plugin.existingHolograms.put(p.getUniqueId(), hList);
		hologram.getVisibilityManager().setVisibleByDefault(false);
		for(Player oP : Bukkit.getOnlinePlayers()){
			if(oP.getWorld().getName().equals(p.getWorld().getName()) 
					&& oP.getLocation().distance(p.getLocation()) <= plugin.distance);
				hologram.getVisibilityManager().showTo(oP);
		}
		int lines = plugin.formatHologramLines(p, hologram, msg);

		new BukkitRunnable() {
			int ticksRun = 0;
			@Override
			public void run() {
				ticksRun++;
				if(!hologram.isDeleted())
					hologram.teleport(p.getLocation().add(0.0, plugin.bubbleOffset + .25 * lines, 0.0));
				if (ticksRun > plugin.life) {
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