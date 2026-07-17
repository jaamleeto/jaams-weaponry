package net.jaams.weaponry.handler.event;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;

import net.jaams.weaponry.loader.EquipmentModifierLoader;

import java.util.Set;


@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EquipmentModifierHandler {
    private static final Logger LOGGER = LogManager.getLogger(EquipmentModifierHandler.class);

    
    private static final Set<EntityType<?>> MIXIN_HANDLED = Set.of(
            EntityType.ZOMBIE,
            EntityType.HUSK,
            EntityType.DROWNED,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.SKELETON,
            EntityType.STRAY,
            EntityType.WITHER_SKELETON,
            EntityType.PILLAGER,
            EntityType.VINDICATOR,
            EntityType.VEX,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE
    );

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof Player) return;
        if (!(event.getEntity() instanceof Mob mob)) return;

        
        if (MIXIN_HANDLED.contains(mob.getType())) return;

        
        if (EquipmentModifierLoader.INSTANCE.getForEntityType(mob.getType()).isEmpty()) return;

        EquipmentModifierLoader.INSTANCE.applySpawnEquipment(mob, mob.getRandom());
    }
}
