package websend;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import java.util.LinkedList;
import java.util.Scanner;


public class PListener extends PlayerListener {

		@Override
		public void onPlayerChat(PlayerChatEvent event) { 	
			System.out.println("Websend: Chat Captured");
			System.out.println("Websend (PC): " + event.getMessage());
		}
	
}
