package com.splitscreen;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Player;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;

import java.util.UUID;

public class VirtualPlayer {

    private final Player base;
    private final int index;
    private ServerPlayer nmsPlayer;

    public VirtualPlayer(Player base, int index) {
        this.base = base;
        this.index = index;
    }

    public void spawn() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel world = ((CraftWorld) base.getWorld()).getHandle();

        GameProfile profile = new GameProfile(UUID.randomUUID(), base.getName() + "#" + index);
        nmsPlayer = new ServerPlayer(server, world, profile);

        Location loc = base.getLocation().add(index * 1.5, 0, 0);
        nmsPlayer.setPos(loc.getX(), loc.getY(), loc.getZ());

        server.getPlayerList().placeNewPlayer(new ServerGamePacketListenerImpl(server, null, nmsPlayer), nmsPlayer);

        Bukkit.getLogger().info("[Splitscreen] Steuerbarer Sub-Account: " + nmsPlayer.getName().getString());
    }

    public void applyMovement(PlayerAuthInputPacket packet) {
        float x = packet.getMoveX();
        float y = packet.getMoveY();

        double speed = 0.2;
        double dx = x * speed;
        double dz = y * speed;

        nmsPlayer.setPos(nmsPlayer.getX() + dx, nmsPlayer.getY(), nmsPlayer.getZ() + dz);
    }

    public ServerPlayer getNmsPlayer() {
        return nmsPlayer;
    }
}
