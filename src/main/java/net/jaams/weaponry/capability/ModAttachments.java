package net.jaams.weaponry.capability;

import java.util.function.Supplier;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.capability.aberration.AberrationImpl;
import net.jaams.weaponry.capability.amount.AmountImpl;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, JaamsWeaponryMod.MODID);

    public static final Supplier<AttachmentType<AmountImpl>> AMOUNT = ATTACHMENT_TYPES.register("amount",
            () -> AttachmentType.serializable(AmountImpl::new).copyOnDeath().build());

    public static final Supplier<AttachmentType<AberrationImpl>> ABERRATION = ATTACHMENT_TYPES.register("aberration",
            () -> AttachmentType.serializable(AberrationImpl::new).copyOnDeath().build());
}
