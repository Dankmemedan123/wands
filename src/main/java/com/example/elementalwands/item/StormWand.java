package com.example.elementalwands.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class StormWand extends Item {

    private static final int    COOLDOWN_TICKS = 100;  // 5 seconds
    private static final double MAX_RANGE      = 50.0;

    public StormWand() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            if (level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("§e⚡ Storm Wand is recharging!"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            Vec3 eyePos  = player.getEyePosition();
            Vec3 lookDir = player.getLookAngle();
            Vec3 farEnd  = eyePos.add(lookDir.scale(MAX_RANGE));

            // FIX: Use level.clip() with ClipContext for a proper server-side block raycast.
            // player.pick() can have subtle partial-tick differences between client and server.
            // level.clip() with BLOCK collision and NONE fluid context is the correct server approach.
            BlockHitResult hitResult = level.clip(new ClipContext(
                    eyePos,
                    farEnd,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
            ));

            Vec3 strikePos;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = hitResult.getBlockPos();
                // Strike the top face of the hit block
                strikePos = new Vec3(
                        blockPos.getX() + 0.5,
                        blockPos.getY() + 1.0,
                        blockPos.getZ() + 0.5);
            } else {
                // No block hit — strike at the far end of the ray
                strikePos = farEnd;
            }

            // FIX: Guard the cast — only pass a ServerPlayer to setCause, never null
            // (null is valid per vanilla but causes some mods to NPE)
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            bolt.setPos(strikePos.x, strikePos.y, strikePos.z);
            if (player instanceof ServerPlayer sp) {
                bolt.setCause(sp);
            }
            level.addFreshEntity(bolt);

            // Thunder boom at player's location for dramatic effect
            level.playSound(null, player.blockPosition(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 5.0f, 1.0f);

            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

            player.displayClientMessage(
                    Component.literal("§e⚡ Storm Wand: §6Lightning strikes!"), true);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
