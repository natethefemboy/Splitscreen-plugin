package com.splitscreen;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.geysermc.floodgate.api.FloodgateApi;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;
import org.geysermc.geyser.api.network.session.GeyserSession;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SplitscreenPlugin extends JavaPlugin implements TabExecutor {

    private final Map<UUID, List<VirtualPlayer>> sessionPlayers = new HashMap<>();
    private static SplitscreenPlugin instance;
    private int defaultExtraPlayers;
    private boolean debug;
    private File playersFile;
    private FileConfiguration playersConfig;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadConfigValues();
        loadPlayersFile();

        getCommand("splitscreen").setExecutor(this);
        getServer().getPluginManager().registerEvents(new ControllerHandler(this), this);
        new ControllerInputListener().register();

        getLogger().info("Splitscreen Handler gestartet!");
    }

    @Override
    public void onDisable() {
        sessionPlayers.clear();
        savePlayersFile();
    }

    public static SplitscreenPlugin getInstance() {
        return instance;
    }

    public void loadConfigValues() {
        FileConfiguration config = getConfig();
        defaultExtraPlayers = config.getInt("extra-players", 2);
        debug = config.getBoolean("debug", false);
    }

    private void loadPlayersFile() {
        playersFile = new File(getDataFolder(), "players.yml");
        if (!playersFile.exists()) {
            try {
                playersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    public void savePlayersFile() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSplitscreen(Player p) {
        if (!FloodgateApi.getInstance().isFloodgatePlayer(p.getUniqueId())) return false;
        String device = FloodgateApi.getInstance().getPlayer(p.getUniqueId()).getDeviceOs().toString();
        return device.equalsIgnoreCase("XBOX") || device.equalsIgnoreCase("SWITCH");
    }

    public int getPlayerExtraAccounts(Player p) {
        String path = "players." + p.getUniqueId() + ".subAccounts";
        return playersConfig.getInt(path, defaultExtraPlayers);
    }

    public void savePlayerData(Player p, String device) {
        String base = "players." + p.getUniqueId();
        playersConfig.set(base + ".name", p.getName());
        playersConfig.set(base + ".device", device);
        if (!playersConfig.contains(base + ".subAccounts")) {
            playersConfig.set(base + ".subAccounts", defaultExtraPlayers);
        }
        savePlayersFile();
    }

    public void createSubPlayers(Player main) {
        int extraPlayers = getPlayerExtraAccounts(main);
        List<VirtualPlayer> virtuals = new ArrayList<>();
        for (int i = 1; i <= extraPlayers; i++) {
            VirtualPlayer vp = new VirtualPlayer(main, i);
            vp.spawn();
            virtuals.add(vp);
        }
        sessionPlayers.put(main.getUniqueId(), virtuals);

        if (debug) getLogger().info("Sub-Accounts erstellt: " + extraPlayers + " für " + main.getName());
    }

    public Map<UUID, List<VirtualPlayer>> getSessionPlayers() {
        return sessionPlayers;
    }

    public void routeControllerInput(GeyserSession session, int controllerId, PlayerAuthInputPacket packet) {
        UUID mainUuid = session.getPlayerEntity().getUuid();

        if (!sessionPlayers.containsKey(mainUuid)) return;
        List<VirtualPlayer> subs = sessionPlayers.get(mainUuid);

        if (controllerId == 0) return; // Hauptspieler
        int subIndex = controllerId - 1;

        if (subIndex >= 0 && subIndex < subs.size()) {
            VirtualPlayer vp = subs.get(subIndex);
            vp.applyMovement(packet);
            if (debug) getLogger().info("Controller " + controllerId + " -> " + vp.getNmsPlayer().getName().getString());
        }
    }

    // -------- Command Handler --------
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            loadConfigValues();
            loadPlayersFile();
            sender.sendMessage("§aSplitscreen Konfiguration neu geladen.");
            return true;
        }
        sender.sendMessage("§eBenutzung: /splitscreen reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("reload");
        return Collections.emptyList();
    }
}
