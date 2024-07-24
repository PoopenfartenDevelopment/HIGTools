package me.redcarlos.higtools.modules.itemrefill;

import me.redcarlos.higtools.HIGTools;
import me.redcarlos.higtools.utils.HIGUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class TestModule extends Module {

    public TestModule() {
        super(HIGTools.MAIN, "shulker-test", "");
    }

    private int timer;
    private boolean placedShulker, openedShulker;
    BlockPos shulkerPos;

    @Override
    public void onActivate() {
        timer = 0;
        placedShulker = false;
        openedShulker = false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre tickEvent) {
        if (mc.player == null) return;
        if (!placedShulker) {
            BlockUtils.place((mc.player.getBlockPos().up(2)), Utils.findShulkerBox(), true, 0, true);
            shulkerPos = mc.player.getBlockPos().up(2);
            info("placed shulker");
            placedShulker = true;
            timer = 0;
            return;
        }

        if (timer < 5) {
            timer++;
            return;
        }

        if (openedShulker) {
            Utils.grabAllItems();
        }

        if (!openedShulker) {
            Vec3d shulkerVec = new Vec3d(shulkerPos.getX(), shulkerPos.getY(), shulkerPos.getZ());
            BlockHitResult table = new BlockHitResult(shulkerVec, Direction.DOWN, shulkerPos, false);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, table);
            info("opened shulker");
            openedShulker = true;
            timer = 0;
        }
    }
}
