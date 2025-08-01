package com.splitscreen;

import org.geysermc.geyser.api.event.subscribe.GeyserEventSubscribe;
import org.geysermc.geyser.api.event.connection.packet.GeyserBedrockPacketReceiveEvent;
import org.geysermc.geyser.api.network.session.GeyserSession;
import com.nukkitx.protocol.bedrock.packet.PlayerAuthInputPacket;

public class ControllerInputListener {

    public void register() {
        org.geysermc.geyser.api.GeyserApi.api().eventRegistrar().subscribe(this);
    }

    @GeyserEventSubscribe
    public void onPacket(GeyserBedrockPacketReceiveEvent event) {
        if (!(event.getPacket() instanceof PlayerAuthInputPacket packet)) return;

        GeyserSession session = event.getSession();
        int controllerId = packet.getInputMode().ordinal();
        SplitscreenPlugin.getInstance().routeControllerInput(session, controllerId, packet);
    }
}
