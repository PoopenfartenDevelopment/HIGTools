package me.redcarlos.higtools.modules.main;

import me.redcarlos.higtools.HIGTools;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ScaffoldPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> ext = sgGeneral.add(new IntSetting.Builder()
        .name("extend")
        .description("How far to place in front of you.")
        .defaultValue(1)
        .range(0, 5)
        .build()
    );

    private final Setting<Boolean> keepY = sgGeneral.add(new BoolSetting.Builder()
        .name("keepY")
        .description("Places blocks only at a specific Y value.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> height = sgGeneral.add(new IntSetting.Builder()
        .name("height")
        .description("Y value to scaffold at.")
        .defaultValue(119)
        .range(-64, 320)
        .sliderRange(-64, 320)
        .visible(keepY::get)
        .build()
    );

    private final Setting<Boolean> tower = sgGeneral.add(new BoolSetting.Builder()
        .name("tower")
        .description("Makes towering easier.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> towerMulti = sgGeneral.add(new DoubleSetting.Builder()
        .name("multi")
        .description("Makes tower potentially bypass stricter anti-cheats.")
        .defaultValue(0.7454)
        .range(0.0, 2.0)
        .visible(tower::get)
        .build()
    );

    private int slot = -1;
    private boolean worked = false;

    public ScaffoldPlus() {
        super(HIGTools.MAIN, "scaffold+", "Scaffolds blocks under you.");
    }

    @EventHandler
    public void tick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        float f = MathHelper.sin(mc.player.getYaw() * 0.017453292f);
        float g = MathHelper.cos(mc.player.getYaw() * 0.017453292f);
        int prevSlot = mc.player.getInventory().selectedSlot;

        for (int i = 0; i <= (mc.player.getVelocity().x == 0.0 && mc.player.getVelocity().z == 0.0 ? 0 : ext.get()); i++) {
            // Loop body
            Vec3d pos = mc.player.getPos().add(-f * i, -0.85, g * i);
            if (keepY.get()) ((IVec3d) pos).setY(height.get() - 1.0);
            BlockPos bPos = BlockPos.ofFloored(pos);
            if (!mc.world.getBlockState(bPos).isReplaceable()) {
                worked = false;
                continue;
            }
            worked = true;

            boolean offHand = mc.player.getOffHandStack().getItem() instanceof BlockItem;
            boolean mainHand = mc.player.getMainHandStack().getItem() instanceof BlockItem;
            if (!offHand && !mainHand) {
                for (int j = 0; j <= 8; j++) {
                    if (mc.player.getInventory().getStack(j).getItem() instanceof BlockItem) {
                        slot = j;
                        break;
                    }
                }
                if (slot == -1) return;
                mc.player.getInventory().selectedSlot = slot;
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }

            if (tower.get() && mc.options.jumpKey.isPressed() && mc.player.getVelocity().x == 0.0 && mc.player.getVelocity().z == 0.0) {
                if (mc.world.getBlockState(mc.player.getBlockPos().down()).isReplaceable() &&
                    !mc.world.getBlockState(mc.player.getBlockPos().down(2)).isReplaceable() &&
                    mc.player.getVelocity().y > 0) {
                    mc.player.setVelocity(mc.player.getVelocity().x, -0.6, mc.player.getVelocity().z);
                    mc.player.jump();
                    mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y * towerMulti.get(), mc.player.getVelocity().z);
                }
            }

            mc.player.networkHandler.sendPacket(
                new PlayerInteractBlockC2SPacket(offHand ? Hand.OFF_HAND : Hand.MAIN_HAND, new BlockHitResult(pos, Direction.DOWN, bPos, false), 0)
            );
            slot = -1;
        }

        if (mc.player.getInventory().selectedSlot != prevSlot) {
            mc.player.getInventory().selectedSlot = prevSlot;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(prevSlot));
        }
    }

    public boolean hasWorked() {
        return worked;
    }
}
