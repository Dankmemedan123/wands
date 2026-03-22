package com.example.elementalwands.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public class GlacialWand extends Item {

    private static final int    COOLDOWN_TICKS  = 160;  // 8 seconds
    private static final double RADIUS          = 8.0;
    private static final int    EFFECT_DURATION = 100;  // 5 seconds
    private static final int    SLOWNESS_AMP    = 3;    // Slowness IV
    private static final int    FATIGUE_AMP     = 1;    // Mining Fatigue II

    // FIX: 300 is getMinFreezeDamageTicks() — the threshold for full visual freeze
    // Previously used 140 which only gave a mild tint; 300 gives the complete frozen look
    private static final int FULL_FREEZE_TICKS  = 300;

    public GlacialWand() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            if (level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("§b❄ Glacial Wand is recharging!"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            AABB searchBox = player.getBoundingBox().inflate(RADIUS);
            UUID playerUUID = player.getUUID();

            List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class, searchBox, e -> {
                        if (e == player) return false;
                        // FIX: Don't freeze the player's own tamed pets
                        if (e instanceof OwnableEntity ownable) {
                            UUID ownerUUID = ownable.getOwnerUUID();
                            if (ownerUUID != null && ownerUUID.equals(playerUUID)) return false;
                        }
                        return true;
                    });

            int hit = 0;
            for (LivingEntity target : targets) {
                target.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN, EFFECT_DURATION, SLOWNESS_AMP, false, true));
                target.addEffect(new MobEffectInstance(
                        MobEffects.DIG_SLOWDOWN, EFFECT_DURATION, FATIGUE_AMP, false, true));
                // FIX: Use Math.max to not reset an entity that is already MORE frozen
                // Set to 300 (FULL_FREEZE_TICKS) so the full blue-tinted frozen look appears
                target.setTicksFrozen(Math.max(target.getTicksFrozen(), FULL_FREEZE_TICKS));
                hit++;
            }

            level.playSound(null, player.blockPosition(),
                    SoundEvents.POWDER_SNOW_PLACE, SoundSource.PLAYERS, 1.5f, 0.6f);

            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

            String msg = hit > 0
                    ? "§b❄ Glacial Wand: §3Froze §b" + hit + " §3target" + (hit == 1 ? "!" : "s!")
                    : "§b❄ Glacial Wand: §3No targets in range!";
            player.displayClientMessage(Component.literal(msg), true);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
