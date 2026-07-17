package net.jaams.weaponry.capability.aberration;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

public class AberrationProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<IAberration> ABERRATION = CapabilityManager.get(new CapabilityToken<>() {
    });
    public final IAberration impl = new AberrationImpl();
    private final LazyOptional<IAberration> optional = LazyOptional.of(() -> impl);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == ABERRATION ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return impl.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        impl.deserializeNBT(nbt);
    }
}
