package net.jaams.weaponry.dispenser;

import net.jaams.weaponry.component.projectile.BaseBulletProjectileEntity;
import net.jaams.weaponry.entity.BulletProjectileEntity;
import net.jaams.weaponry.entity.DynamiteProjectileEntity;
import net.jaams.weaponry.entity.EchoBulletProjectileEntity;
import net.jaams.weaponry.entity.FireBulletProjectileEntity;
import net.jaams.weaponry.entity.GlowingBulletProjectileEntity;
import net.jaams.weaponry.entity.HeavyBulletProjectileEntity;
import net.jaams.weaponry.entity.KunaiProjectileEntity;
import net.jaams.weaponry.entity.SharpBulletProjectileEntity;
import net.jaams.weaponry.entity.SharpStoneProjectileEntity;
import net.jaams.weaponry.entity.ShurikenProjectileEntity;
import net.jaams.weaponry.entity.StakeProjectileEntity;
import net.jaams.weaponry.init.ModEntities;
import net.jaams.weaponry.registry.BottomItems;
import net.jaams.weaponry.util.ModTags;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.jaams.weaponry.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ProjectileDispenser {

    private static final float PROJECTILE_SPEED = 1.5F;
    private static final float SHOTSHELL_INACCURACY = 6.0F;
    private static final int SHOTSHELL_MIN_COUNT = 1;
    private static final int SHOTSHELL_MAX_COUNT = 3;
    private static final float SHOTSHELL_SPREAD_ANGLE = 10.0F;

    public static boolean canDispense(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.is(ModTags.BULLETS) || stack.is(ModTags.SHOTSHELLS) || stack.is(ModTags.STAKES)
                || stack.is(ModTags.SHARP_STONES) || stack.is(ModTags.DYNAMITES) || stack.is(ModTags.KUNAIS)
                || stack.is(ModTags.SHURIKENS);
    }

    public static ItemStack dispense(BlockSource source, ItemStack stack) {
        Level level = source.level();
        if (level.isClientSide()) {
            return new DefaultDispenseItemBehavior().dispense(source, stack);
        }

        Direction direction = source.state().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(source);

        if (stack.is(ModTags.SHOTSHELLS)) {
            dispenseShotshell(stack, level, direction, position);
            stack.shrink(1);
            return stack;
        }

        Entity entity = createProjectileEntity(stack, level, position.x(), position.y(), position.z());
        if (entity == null) {
            return new DefaultDispenseItemBehavior().dispense(source, stack);
        }

        if (entity instanceof AbstractArrow arrow) {
            arrow.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(),
                    PROJECTILE_SPEED, 0.0F);
        }

        level.addFreshEntity(entity);
        playShootSound(level, position);
        stack.shrink(1);
        return stack;
    }

    private static void dispenseShotshell(ItemStack stack, Level level, Direction direction, Position position) {
        Vec3 baseDir = Vec3.atLowerCornerOf(direction.getNormal()).normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = baseDir.cross(up).normalize();

        int count = level.random.nextInt(SHOTSHELL_MAX_COUNT - SHOTSHELL_MIN_COUNT + 1) + SHOTSHELL_MIN_COUNT;

        for (int i = 0; i < count; i++) {
            float rotation = count > 1
                    ? (SHOTSHELL_SPREAD_ANGLE / (count - 1)) * (i - (count - 1) / 2.0F)
                    : 0.0F;
            Quaternionf quat = new Quaternionf().setAngleAxis((float) Math.toRadians(rotation),
                    (float) right.x, (float) right.y, (float) right.z);
            Vector3f rotatedDir = baseDir.toVector3f().rotate(quat);

            Entity entity = createBulletEntity(stack, level, position.x(), position.y(), position.z());
            if (entity instanceof AbstractArrow arrow) {
                arrow.shoot(rotatedDir.x(), rotatedDir.y(), rotatedDir.z(),
                        PROJECTILE_SPEED, SHOTSHELL_INACCURACY);
                level.addFreshEntity(entity);
            }
        }
        playShootSound(level, position);
    }

    private static Entity createProjectileEntity(ItemStack stack, Level level, double x, double y, double z) {
        
        if (stack.is(ModTags.KUNAIS)) {
            KunaiProjectileEntity entity = new KunaiProjectileEntity(
                    ModEntities.KUNAI_PROJECTILE.get(), x, y, z, level);
            entity.initializeWeaponItem(stack);
            entity.initializeProjectileStats(stack);
            return entity;
        }
        if (stack.is(ModTags.SHURIKENS)) {
            ShurikenProjectileEntity entity = new ShurikenProjectileEntity(
                    ModEntities.SHURIKEN_PROJECTILE.get(), x, y, z, level);
            entity.initializeWeaponItem(stack);
            entity.initializeProjectileStats(stack);
            return entity;
        }
        if (stack.is(ModTags.STAKES)) {
            StakeProjectileEntity entity = new StakeProjectileEntity(
                    ModEntities.STAKE_PROJECTILE.get(), x, y, z, level);
            entity.initializeWeaponItem(stack);
            entity.initializeProjectileStats(stack);
            return entity;
        }
        if (stack.is(ModTags.SHARP_STONES)) {
            SharpStoneProjectileEntity entity = new SharpStoneProjectileEntity(
                    ModEntities.SHARP_STONE_PROJECTILE.get(), x, y, z, level);
            entity.initializeWeaponItem(stack);
            entity.initializeProjectileStats(stack);
            return entity;
        }
        if (stack.is(ModTags.DYNAMITES)) {
            DynamiteProjectileEntity entity = new DynamiteProjectileEntity(
                    ModEntities.DYNAMITE_PROJECTILE.get(), x, y, z, level);
            entity.initializeWeaponItem(stack);
            entity.initializeProjectileStats(stack);
            return entity;
        }

        
        if (stack.is(ModTags.BULLETS)) {
            return createBulletEntity(stack, level, x, y, z);
        }

        return null;
    }

    private static Entity createBulletEntity(ItemStack stack, Level level, double x, double y, double z) {
        BaseBulletProjectileEntity entity;

        Item item = stack.getItem();
        boolean isFire = item == BottomItems.FIRE_BULLET.get() || item == BottomItems.FIRE_SHOTSHELL.get();
        boolean isHeavy = item == BottomItems.HEAVY_BULLET.get() || item == BottomItems.HEAVY_SHOTSHELL.get();
        boolean isGlowing = item == BottomItems.GLOWING_BULLET.get() || item == BottomItems.GLOWING_SHOTSHELL.get();
        boolean isSharp = item == BottomItems.SHARP_BULLET.get() || item == BottomItems.SHARP_SHOTSHELL.get();
        boolean isEcho = item == BottomItems.ECHO_BULLET.get() || item == BottomItems.ECHO_SHOTSHELL.get();

        if (isFire) {
            entity = new FireBulletProjectileEntity(
                    ModEntities.FIRE_BULLET_PROJECTILE.get(), x, y, z, level);
        } else if (isHeavy) {
            entity = new HeavyBulletProjectileEntity(
                    ModEntities.HEAVY_BULLET_PROJECTILE.get(), x, y, z, level);
        } else if (isGlowing) {
            entity = new GlowingBulletProjectileEntity(
                    ModEntities.GLOWING_BULLET_PROJECTILE.get(), x, y, z, level);
        } else if (isSharp) {
            entity = new SharpBulletProjectileEntity(
                    ModEntities.SHARP_BULLET_PROJECTILE.get(), x, y, z, level);
        } else if (isEcho) {
            entity = new EchoBulletProjectileEntity(
                    ModEntities.ECHO_BULLET_PROJECTILE.get(), x, y, z, level);
        } else {
            entity = new BulletProjectileEntity(
                    ModEntities.BULLET_PROJECTILE.get(), x, y, z, level);
        }

        entity.setBulletItem(stack);
        return entity;
    }

    private static void playShootSound(Level level, Position position) {
        level.playSound(null, position.x(), position.y(), position.z(),
                ModSounds.PISTOL_SHOOT.get(), SoundSource.BLOCKS, 1.0F, 1.2F);
    }
}
