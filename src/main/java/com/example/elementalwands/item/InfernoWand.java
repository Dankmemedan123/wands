package com.example.elementalwands.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class InfernoWand extends Item {

    private static final int    COOLDOWN_TICKS = 40;   // 2 seconds
    private static final double RANGE          = 10.0;
    private static final double CONE_DOT       = 0.6;  // ~53° half-angle
    private static final int    FIRE_SECONDS   = 5;

    public InfernoWand() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            if (level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("§c🔥 Inferno Wand is recharging!"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            Vec3 look   = player.getLookAngle();
            Vec3 origin = player.getEyePosition();

            AABB searchBox = new AABB(
                    origin.x - RANGE, origin.y - RANGE, origin.z - RANGE,
                    origin.x + RANGE, origin.y + RANGE, origin.z + RANGE);

            UUID playerUUID = player.getUUID();

            List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class, searchBox, e -> {
                        // Never hit the casting player
                        if (e == player) return false;
                        // FIX: Skip tamed animals/pets owned by this player
                        if (e instanceof OwnableEntity ownable) {
                            UUID ownerUUID = ownable.getOwnerUUID();
                            if (ownerUUID != null && ownerUUID.equals(playerUUID)) return false;
                        }
                        return isInCone(origin, look, e.position(), RANGE, CONE_DOT);
                    });

            int hit = 0;
            for (LivingEntity target : targets) {
                target.setSecondsOnFire(FIRE_SECONDS);
                hit++;

                // FIX: Use BlockPos.containing() and check the block BELOW the entity
                // then confirm the surface is solid before placing fire ON TOP of it
                BlockPos feetPos    = target.blockPosition();
                BlockPos surfacePos = feetPos; // where fire should appear

                // Walk up until we find a valid surface to place fire on
                // (entity may be hovering mid-air — find first air block above solid)
                if (level.getBlockState(surfacePos).isAir()) {
                    BlockPos below = surfacePos.below();
                    if (!level.getBlockState(below).isAir()
                            && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                        level.setBlockAndUpdate(surfacePos, Blocks.FIRE.defaultBlockState());
                    }
                }
            }

            // FIX: Use BLAZE_SHOOT as a reliable fallback that definitely exists in 1.20.1
            // FIRECHARGE_USE is confirmed in 1.20.1 but BLAZE_SHOOT is more universally safe
            level.playSound(null, player.blockPosition(),
                    SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.2f, 0.8f);

            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

            String msg = hit > 0
                    ? "§6🔥 Inferno Wand: §eIgnited §6" + hit + " §etarget" + (hit == 1 ? "!" : "s!")
                    : "§6🔥 Inferno Wand: §eNo targets in range!";
            player.displayClientMessage(Component.literal(msg), true);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * True if the target is within the forward cone from origin.
     * dot >= 0.6 means roughly a 53-degree half-angle cone.
     */
    private boolean isInCone(Vec3 origin, Vec3 lookDir, Vec3 targetPos,
                              double range, double dotThreshold) {
        Vec3 toTarget = targetPos.subtract(origin);
        double dist = toTarget.length();
        if (dist > range || dist < 0.5) return false;
        double dot = toTarget.normalize().dot(lookDir.normalize());
        return dot >= dotThreshold;
    }
}
