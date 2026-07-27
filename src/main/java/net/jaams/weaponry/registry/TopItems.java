package net.jaams.weaponry.registry;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.jaams.weaponry.item.TessenItem;
import net.jaams.weaponry.item.TessenFoldedItem;
import net.jaams.weaponry.item.StakeItem;
import net.jaams.weaponry.item.StakeCrossbowItem;
import net.jaams.weaponry.item.ShurikenItem;
import net.jaams.weaponry.item.ShortBowItem;
import net.jaams.weaponry.item.SharpStoneItem;
import net.jaams.weaponry.item.SharpStoneBladeItem;
import net.jaams.weaponry.item.RusticWhipItem;
import net.jaams.weaponry.item.ProngedKunaiItem;
import net.jaams.weaponry.item.NunchakuItem;
import net.jaams.weaponry.item.KunaiItem;
import net.jaams.weaponry.item.HuntersCrossbowItem;
import net.jaams.weaponry.item.HuntersBowItem;
import net.jaams.weaponry.item.HuntersBoomerangItem;
import net.jaams.weaponry.item.GreatCrossbowItem;
import net.jaams.weaponry.item.GiantShurikenItem;
import net.jaams.weaponry.item.GauntletItem;
import net.jaams.weaponry.item.FlintHammerItem;
import net.jaams.weaponry.item.FlatBowItem;
import net.jaams.weaponry.item.CompoundBowItem;
import net.jaams.weaponry.JaamsWeaponryMod;

public class TopItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM,
            JaamsWeaponryMod.MODID);

    public static final DeferredHolder<Item, Item> GAUNTLET = REGISTRY.register("gauntlet", () -> new GauntletItem());
    public static final DeferredHolder<Item, Item> SHARP_STONE_BLADE = REGISTRY.register("sharp_stone_blade",
            () -> new SharpStoneBladeItem());
    public static final DeferredHolder<Item, Item> RUSTIC_WHIP = REGISTRY.register("rustic_whip", () -> new RusticWhipItem());
    public static final DeferredHolder<Item, Item> HUNTERS_BOOMERANG = REGISTRY.register("hunters_boomerang",
            () -> new HuntersBoomerangItem());
    public static final DeferredHolder<Item, Item> FLINT_HAMMER = REGISTRY.register("flint_hammer",
            () -> new FlintHammerItem());
    public static final DeferredHolder<Item, Item> SHORT_BOW = REGISTRY.register("short_bow", () -> new ShortBowItem());
    public static final DeferredHolder<Item, Item> NUNCHAKU = REGISTRY.register("nunchaku", () -> new NunchakuItem());
    public static final DeferredHolder<Item, Item> FLAT_BOW = REGISTRY.register("flat_bow", () -> new FlatBowItem());
    public static final DeferredHolder<Item, Item> TESSEN = REGISTRY.register("tessen", () -> new TessenItem());
    public static final DeferredHolder<Item, Item> HUNTERS_CROSSBOW = REGISTRY.register("hunters_crossbow",
            () -> new HuntersCrossbowItem());
    public static final DeferredHolder<Item, Item> SHURIKEN = REGISTRY.register("shuriken", () -> new ShurikenItem());
    public static final DeferredHolder<Item, Item> HUNTERS_BOW = REGISTRY.register("hunters_bow", () -> new HuntersBowItem());
    public static final DeferredHolder<Item, Item> GIANT_SHURIKEN = REGISTRY.register("giant_shuriken",
            () -> new GiantShurikenItem());
    public static final DeferredHolder<Item, Item> GREAT_CROSSBOW = REGISTRY.register("great_crossbow",
            () -> new GreatCrossbowItem());
    public static final DeferredHolder<Item, Item> PRONGED_KUNAI = REGISTRY.register("pronged_kunai",
            () -> new ProngedKunaiItem());
    public static final DeferredHolder<Item, Item> COMPOUND_BOW = REGISTRY.register("compound_bow",
            () -> new CompoundBowItem());
    public static final DeferredHolder<Item, Item> KUNAI = REGISTRY.register("kunai", () -> new KunaiItem());
    public static final DeferredHolder<Item, Item> STAKE_CROSSBOW = REGISTRY.register("stake_crossbow",
            () -> new StakeCrossbowItem());
    public static final DeferredHolder<Item, Item> SHARP_STONE = REGISTRY.register("sharp_stone", () -> new SharpStoneItem());
    public static final DeferredHolder<Item, Item> STAKE = REGISTRY.register("stake", () -> new StakeItem());
    public static final DeferredHolder<Item, Item> TESSEN_FOLDED = REGISTRY.register("tessen_folded",
            () -> new TessenFoldedItem());
}
