package net.jaams.weaponry.network;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.util.ModAnimations;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PlayAnimationMessage implements CustomPacketPayload {
    public static final Type<PlayAnimationMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, "play_animation"));
    public static final StreamCodec<FriendlyByteBuf, PlayAnimationMessage> STREAM_CODEC = StreamCodec.of((buffer, msg) -> encode(msg, buffer), PlayAnimationMessage::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private int playerId;
    private String animation;
    private boolean override;
    private boolean firstPerson;
    private boolean hideArms;
    private int duration;
    private float speed;

    public PlayAnimationMessage(int playerId, String animation, boolean override, boolean firstPerson,
            boolean hideArms, int duration, float speed) {
        this.playerId = playerId;
        this.animation = animation;
        this.override = override;
        this.firstPerson = firstPerson;
        this.hideArms = hideArms;
        this.duration = duration;
        this.speed = speed;
    }

    public PlayAnimationMessage(int playerId, String animation, boolean override, boolean firstPerson,
            boolean hideArms) {
        this(playerId, animation, override, firstPerson, hideArms, 0, 1.0f);
    }

    public PlayAnimationMessage(int playerId, String animation, boolean override, boolean firstPerson) {
        this(playerId, animation, override, firstPerson, false, 0, 1.0f);
    }

    public static void encode(PlayAnimationMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.playerId);
        buffer.writeUtf(msg.animation);
        buffer.writeBoolean(msg.override);
        buffer.writeBoolean(msg.firstPerson);
        buffer.writeBoolean(msg.hideArms);
        buffer.writeInt(msg.duration);
        buffer.writeFloat(msg.speed);
    }

    public static PlayAnimationMessage decode(FriendlyByteBuf buffer) {
        return new PlayAnimationMessage(buffer.readInt(), buffer.readUtf(), buffer.readBoolean(), buffer.readBoolean(),
                buffer.readBoolean(), buffer.readInt(), buffer.readFloat());
    }

    public static void handle(PlayAnimationMessage msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level != null) {
                Player player = (Player) mc.level.getEntity(msg.playerId);
                if (player == null)
                    return;
                if (msg.animation.isEmpty()) {
                    ModAnimations.setAnimationReset(player, true);
                    ModAnimations.setFirstPersonAnimation(player, false);
                    ModAnimations.removeCurrentAnimationName(player);
                    ModAnimations.removeAnimationProgress(player);
                    ModAnimations.removeHideArms(player);
                    ModAnimations.removeAnimationDuration(player);
                    ModAnimations.removeAnimationSpeed(player);
                    ModAnimations.removeAnimationElapsedTicks(player);
                    ModAnimations.removePose(player);
                    ModAnimations.clearCombinableAnimations(player);
                    ModAnimations.clearActiveAnimation(player);
                    if (player != mc.player) {
                        player.noCulling = false;
                    }
                } else {
                    ModAnimations.setCurrentAnimationName(player, msg.animation);
                    ModAnimations.setAnimationOverride(player, msg.override);
                    ModAnimations.setFirstPersonAnimation(player, msg.firstPerson);
                    ModAnimations.setHideArms(player, msg.hideArms);
                    ModAnimations.setAnimationDuration(player, msg.duration);
                    ModAnimations.setAnimationSpeed(player, msg.speed);




                    net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation anim =
                        net.jaams.weaponry.animation.AnimationAPI.animations.get(msg.animation);





                    if (anim != null && !anim.combinable) {
                        ModAnimations.clearCombinableAnimations(player);
                    }

                    if (anim != null && anim.isPose) {
                        ModAnimations.setPose(player, msg.animation);
                    }





                    if (anim != null && anim.combinable) {
                        ModAnimations.addCombinableAnimation(player, msg.animation, msg.speed);
                    }
                    if (player != mc.player) {
                        player.noCulling = true;
                    }
                }
            }
        });
    }
}
