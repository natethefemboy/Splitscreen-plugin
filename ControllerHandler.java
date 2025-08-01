package com.splitscreen;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class ControllerHandler implements Listener {

    private final SplitscreenPlugin plugin;

    public ControllerHandler(SplitscreenPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (plugin.isSplitscreen(p)) {
            String device = FloodgateApi.getInstance().getPlayer(p.getUniqueId()).getDeviceOs().toString();
            plugin.savePlayerData(p, device);
            plugin.getLogger().info("Splitscreen erkannt für: " + p.getName() + " (" + device + ")");
            plugin.createSubPlayers(p);
        }
    }
}
