package com.github.alexthe666.rats.server.message;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSwingArm {

    public MessageSwingArm() {

    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageSwingArm message, Supplier<NetworkEvent.Context> context) {
            ((NetworkEvent.Context)context.get()).setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(player != null) {
                //TODO: Wait for tinkers construct update
                //TinkersCompatBridge.onPlayerSwing(player, player.getHeldItem(EnumHand.MAIN_HAND));
            }
        }
    }


    public static MessageSwingArm read(PacketBuffer buf) {
        return new MessageSwingArm();
    }

    public static void write(MessageSwingArm message, PacketBuffer buf) {
    }

}
