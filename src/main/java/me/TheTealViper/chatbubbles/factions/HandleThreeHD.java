package me.TheTealViper.chatbubbles.factions;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.massivecraft.factions.entity.MPlayer;

import me.TheTealViper.chatbubbles.ChatBubbles;

public class HandleThreeHD {
	public static void handleThree(ChatBubbles plugin, String message, Player p){
		boolean sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message");
		boolean requirePerm = plugin.getConfig().getBoolean("ConfigOne_Require_Permissions");
		String usePerm = plugin.getConfig().getString("ConfigOne_Use_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(plugin.hdHandler.existingHolograms.containsKey(p.getUniqueId())) {
					for(Hologram h : plugin.hdHandler.existingHolograms.get(p.getUniqueId())) {
						if(!h.isDeleted())
							h.delete();
					}
				}
				MPlayer mPlayer = MPlayer.get(p);
				String faction = mPlayer.getFactionName();
				final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, plugin.getCBConfig().bubbleOffset, 0.0));
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				plugin.hdHandler.existingHolograms.put(p.getUniqueId(), hList);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for(Player oP : Bukkit.getOnlinePlayers()){
					if(((plugin.getCBConfig().seeOwnBubble) || (!plugin.getCBConfig().seeOwnBubble && oP.getName() != p.getName())) 
							&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
							&& oP.getLocation().distance(p.getLocation()) <= plugin.getCBConfig().distance) 
							&& (MPlayer.get(oP).getFactionName().equals(faction))
							&& oP.canSee(p))
						hologram.getVisibilityManager().showTo(oP);
				}
				int lines = plugin.hdHandler.formatHologramLines(p, hologram, message);
				if(sendOriginal)
					p.chat(message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(!hologram.isDeleted())
							hologram.teleport(p.getLocation().add(0.0, plugin.getCBConfig().bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > plugin.getCBConfig().life) {
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
							plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}
	    }}.runTask(plugin);
	}
}
    