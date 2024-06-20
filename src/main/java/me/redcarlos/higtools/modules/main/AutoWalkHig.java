/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 * Enhanced by RedCarlos26
 */

package me.redcarlos.higtools.modules.main;

import me.redcarlos.higtools.HIGTools;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Items;

public class AutoWalkHig extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> pauseOnLag = sgGeneral.add(new BoolSetting.Builder()
        .name("pause-on-lag")
        .description("Pauses walking while the server stops responding.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> resumeTPS = sgGeneral.add(new IntSetting.Builder()
        .name("resume-tps")
        .description("Server tick speed at which to resume walking.")
        .defaultValue(18)
        .range(1, 19)
        .sliderRange(1, 19)
        .visible(pauseOnLag::get)
        .build()
    );

    private final Setting<Boolean> keepY = sgGeneral.add(new BoolSetting.Builder()
        .name("y-value-toggle")
        .description("Toggles itself when you fall below your original height.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> pickToggle = sgGeneral.add(new BoolSetting.Builder()
        .name("pickaxe-toggle")
        .description("Toggles itself when you run out of pickaxes.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> gapToggle = sgGeneral.add(new BoolSetting.Builder()
        .name("gap-toggle")
        .description("Toggles itself when you run out of enchanted golden apples.")
        .defaultValue(true)
        .build()
    );

    private double originY;
    private boolean sentLagMessage;

    public AutoWalkHig() {
        super(HIGTools.MAIN, "autoWalkHIG", "Automatically walks forward (optimized for highway digging).");
    }

    @Override
    public void onActivate() {
        if (mc.player == null) return;

        originY = Math.abs(mc.player.getY());
        sentLagMessage = false;
    }

    @Override
    public void onDeactivate() {
        unpress();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (pauseOnLag.get()) {
            if (TickRate.INSTANCE.getTimeSinceLastTick() > 1.4f) {
                if (!sentLagMessage) {
                    error("Server isn't responding, pausing.");
                }
                sentLagMessage = true;
                unpress();
                return;
            }

            if (sentLagMessage) {
                if (TickRate.INSTANCE.getTickRate() > resumeTPS.get()) {
                    sentLagMessage = false;
                } else return;
            }
        }

        setPressed(mc.options.forwardKey, true);

        if (keepY.get()) {
            if (mc.player == null) return;
            // -0.125 is so players can still walk on soul sand and similar blocks while digging
            if (mc.player.getY() < originY - 0.125) {
                info("Fell below original height, disabling.");
                toggle();
                return;
            }
        }

        if (pickToggle.get()) {
            FindItemResult pickaxe = InvUtils.find(itemStack -> itemStack.getItem() == Items.DIAMOND_PICKAXE || itemStack.getItem() == Items.NETHERITE_PICKAXE);
            if (!pickaxe.found()) {
                error("No pickaxe found, disabling.");
                toggle();
                return;
            }
        }

        if (gapToggle.get()) {
            FindItemResult gapple = InvUtils.find(itemStack -> itemStack.getItem() == Items.ENCHANTED_GOLDEN_APPLE);
            if (!gapple.found()) {
                error("No gap found, disabling.");
                toggle();
            }
        }
    }

    private void unpress() {
        setPressed(mc.options.forwardKey, false);
    }

    private void setPressed(KeyBinding key, boolean pressed) {
        key.setPressed(pressed);
        Input.setKeyState(key, pressed);
    }
}
