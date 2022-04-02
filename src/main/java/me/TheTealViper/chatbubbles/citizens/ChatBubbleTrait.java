package me.TheTealViper.chatbubbles.citizens;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import me.TheTealViper.chatbubbles.ChatBubbles;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.ai.speech.event.NPCSpeechEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class ChatBubbleTrait extends Trait {
	
	ChatBubbles plugin = null;
	CitizensHDChatbubble hdHandler;
	CitizensDHChatbubble dhHandler;
	
	public ChatBubbleTrait() {
		super("chatbubble");
		plugin = JavaPlugin.getPlugin(ChatBubbles.class);
		switch(plugin.type) {
	        case HD:
	        	hdHandler = new CitizensHDChatbubble(plugin);
	        case DecentHolograms:
	        	dhHandler = new CitizensDHChatbubble(plugin);
	}
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
	    		//Check which Hologram plugin is being used, then call correct method
	    		switch(plugin.type) {
	    		    case HD:
	    		    	hdHandler.createBubbleHD(p, msg);
	    		    case DecentHolograms:
	    		    	dhHandler.createBubbleHD(p, msg);
	    		}
	    		
	    		if(plugin.getCBConfig().chatBubbleOverridesNPCChat)
	    			event.setCancelled(true);
	    	}
	    }
	}
	
	@Override
	public void onAttach() {
		plugin.getServer().getLogger().info("[ChatBubbles] " + npc.getName() + " has been assigned trait ChatBubble!");
	}

}