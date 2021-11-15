package me.TheTealViper.chatbubbles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.massivecraft.factions.entity.MPlayer;

public class handleThree {
	public static void run(ChatBubbles cb, String message, Player p){
		boolean sendOriginal = cb.getConfig().getBoolean("ChatBubble_Send_Original_Message");
		boolean requirePerm = cb.getConfig().getBoolean("ConfigOne_Require_Permissions");
		String usePerm = cb.getConfig().getString("ConfigOne_Use_Permission");
		new BukkitRunnable() {
			@Override
			public void run() {
				if(requirePerm && !p.hasPermission(usePerm))
					return;
				if(cb.existingHolograms.containsKey(p.getUniqueId())) {
					for(Hologram h : cb.existingHolograms.get(p.getUniqueId())) {
						if(!h.isDeleted())
							h.delete();
					}
				}
				MPlayer mPlayer = MPlayer.get(p);
				String faction = mPlayer.getFactionName();
				final Hologram hologram = HologramsAPI.createHologram(cb, p.getLocation().add(0.0, cb.bubbleOffset, 0.0));
				List<Hologram> hList = new ArrayList<Hologram>();
				hList.add(hologram);
				cb.existingHolograms.put(p.getUniqueId(), hList);
				hologram.getVisibilityManager().setVisibleByDefault(false);
				for(Player oP : Bukkit.getOnlinePlayers()){
					if(((cb.seeOwnBubble) || (!cb.seeOwnBubble && oP.getName() != p.getName())) 
							&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
							&& oP.getLocation().distance(p.getLocation()) <= cb.distance) 
							&& (MPlayer.get(oP).getFactionName().equals(faction))
							&& oP.canSee(p))
						hologram.getVisibilityManager().showTo(oP);
				}
				int lines = cb.formatHologramLines(p, hologram, message);
				if(sendOriginal)
					p.chat(message);

				new BukkitRunnable() {
					int ticksRun = 0;
					@Override
					public void run() {
						ticksRun++;
						if(!hologram.isDeleted())
							hologram.teleport(p.getLocation().add(0.0, cb.bubbleOffset + .25 * lines, 0.0));
						if (ticksRun > cb.life) {
							hologram.delete();
							cancel();
						}
				}}.runTaskTimer(cb, 1L, 1L);
				
				if(cb.getConfig().getBoolean("ChatBubble_Play_Sound")) {
					String sound = cb.getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
					float volume = (float) cb.getConfig().getDouble("ChatBubble_Sound_Volume");
					if(!sound.equals("")) {
						try {
							p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
						}catch(Exception e) {
							cb.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
							cb.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
						}
					}
				}
	    }}.runTask(cb);
	}
}
