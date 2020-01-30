package net.mcatlas.helpers.geonames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;

import net.mcatlas.helpers.HelpersPlugin;

public class GotoTabCompletion implements Listener {

	private static final int MAX_LIST = 5;

	private Map<Player, List<String>> recents = new HashMap<>();

	@EventHandler
	public void onAsyncTabComplete(AsyncTabCompleteEvent event) {
		if ((!event.isCommand() && !event.getBuffer().startsWith("/")) || event.getBuffer().indexOf(" ") == -1) {
			System.out.println("nope");
			return;
		}

		if (!(event.getSender() instanceof Player)) return;
		Player player = (Player) event.getSender();
		if (recents.containsKey(player)) {
			event.setCompletions(recents.get(player));
			event.setHandled(true);
			return;
		}

		if (!event.getBuffer().startsWith("/goto")) return;
		if (!HelpersPlugin.get().hasStorage()) return;

		List<String> tabCompleters = new ArrayList<>();

		String argsAttached = event.getBuffer().replace("/goto ", "");
		if (argsAttached.length() < 3) return;

		List<Destination> destinations = HelpersPlugin.get().getStorage().getAutoComplete(argsAttached);
		if (destinations == null || destinations.isEmpty()) return;

		// calc amount of good destinations in the list
		List<Destination> goodDestinations = new ArrayList<>(destinations);
		for (int i = 0; i < goodDestinations.size(); i++) {
			Destination good = goodDestinations.get(i);
			if (good.getFormattedName().length() > 80 || good.getFCode().equals("ADM1") || 
					good.getFCode().equals("ADM2") || good.getFCode().equals("ADM3") ||
					good.getFCode().equals("ADM5") || good.getFCode().equals("ADM2") ||
					good.getFCode().equals("ADM4") || good.getFCode().equals("RGN") ||
					good.getFCode().equals("RGNE") || good.getFCode().equals("PCLI")) {
				goodDestinations.remove(good);
				i--;
			}
		}
		int goodDestAmount = goodDestinations.size();

		Collections.sort(destinations);
		int i = 0;
		for (Destination destination : destinations) {
			// 20 is max max list size
			if (i >= 20) break;

			if (!goodDestinations.contains(destination) && goodDestAmount > MAX_LIST) continue;

			String firstArgs = "";
			if (argsAttached.contains(" ")) {
				firstArgs = destination.getFormattedName().substring(0, 
						argsAttached.lastIndexOf(" ") + 1);
			}

			String formatted = destination.getFormattedName();

			// check if theres an admin district or something with the same name, remove for less confusion
			boolean cut = false;
			for (Destination destAgain : destinations) {
				if (destination.equals(destAgain)) break;
				if (formatted.equals(destAgain.getFormattedName())) {
					cut = true;
					break;
				}
			}
			if (cut) continue;

			formatted = formatted.replace(firstArgs, "");

			tabCompleters.add(formatted);

			i++;
		}

		event.setCompletions(tabCompleters);
		event.setHandled(true);

		this.recents.put(player, tabCompleters);
		Bukkit.getScheduler().runTaskLater(HelpersPlugin.get(), () -> {
			this.recents.remove(player);
		}, 20 * 1);
	}

}
