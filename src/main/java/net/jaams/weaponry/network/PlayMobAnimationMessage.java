package net.jaams.weaponry.network;

import net.minecraftforge.network.NetworkEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.FriendlyByteBuf;

import net.jaams.weaponry.animation.AnimationHelper;

import java.util.function.Supplier;

public class PlayMobAnimationMessage {
    private int entityId;
    private String animation;
    private boolean stop;
    private boolean override;
    private int duration;
    private float speed;

    public PlayMobAnimationMessage(int entityId, String animation, boolean stop, boolean override, int duration,
            float speed) {
        this.entityId = entityId;
        this.animation = animation;
        this.stop = stop;
        this.override = override;
        this.duration = duration;
        this.speed = speed;
    }

    public PlayMobAnimationMessage(int entityId, String animation, boolean stop) {
        this(entityId, animation, stop, false, 0, 1.0f);
    }

    public PlayMobAnimationMessage(int entityId, String animation) {
        this(entityId, animation, false, false, 0, 1.0f);
    }

    public PlayMobAnimationMessage(int entityId, String animation, boolean override, int duration, float speed) {
        this(entityId, animation, false, override, duration, speed);
    }

    public static void encode(PlayMobAnimationMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeUtf(msg.animation);
        buffer.writeBoolean(msg.stop);
        buffer.writeBoolean(msg.override);
        buffer.writeInt(msg.duration);
        buffer.writeFloat(msg.speed);
    }

    public static PlayMobAnimationMessage decode(FriendlyByteBuf buffer) {
        int entityId = buffer.readInt();
        String animation = buffer.readUtf();
        boolean stop = buffer.readBoolean();
        boolean override = buffer.readBoolean();
        int duration = buffer.readInt();
        float speed = buffer.readFloat();
        return new PlayMobAnimationMessage(entityId, animation, stop, override, duration, speed);
    }

    public static void handle(PlayMobAnimationMessage msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().setPacketHandled(true);
            return;
        }
        ctx.get().enqueueWork(() -> {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level != null) {
                net.minecraft.world.entity.Entity entity = mc.level.getEntity(msg.entityId);
                if (entity instanceof LivingEntity living) {
                    if (msg.stop || msg.animation.isEmpty()) {
                        AnimationHelper.stopAnimation(living);
                    } else {
                        AnimationHelper.startAnimation(living, msg.animation, msg.override, msg.speed, msg.duration);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
