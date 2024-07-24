package me.redcarlos.higtools.modules.highwayborers;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AxisBorer extends BorerModule {
    public AxisBorer() {
        super("axis-borer", "Digs axis highways and ring roads.", 4, 4, 0, 0);
    }

    @Override
    public void onActivate() {
        super.onActivate();
        blackList.clear();
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        blackList.clear();
    }

    @Override
    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        // Previous floored block position of player
        BlockPos prevBlockPos = playerPos;
        playerPos = new BlockPos(
            MathHelper.floor(mc.player.getX()),
            keepY.get() != -1 ? keepY.get() : MathHelper.floor(mc.player.getY()),
            MathHelper.floor(mc.player.getZ())
        );

        if (playerPos != prevBlockPos || Util.getMeasuringTimeMs() - lastUpdateTime > 800) {
            getBlacklistedBlockPoses();
            switch (mode.get()) {
                case THIN -> {
                    do2x3(playerPos.add(xOffset.get(), 0, zOffset.get()));
                    if (jumping.get()) {
                        do2x3(playerPos.add(xOffset.get() * -1, 0, zOffset.get() * -1));
                        do2x3(playerPos.add(xOffset.get() * -3, 0, zOffset.get() * -3));
                        do2x3(playerPos.add(xOffset.get() * -7, 0, zOffset.get() * -7));
                    } else do2x3(playerPos.add(xOffset.get() * -3, 0, zOffset.get() * -3));
                }
                case HIGHWAY -> {
                    doHighway4(playerPos.add(xOffset.get(), 0, zOffset.get()));
                    if (jumping.get()) {
                        doHighway4(playerPos.add(xOffset.get() * -1, 0, zOffset.get() * -1));
                        doHighway4(playerPos.add(xOffset.get() * -3, 0, zOffset.get() * -3));
                        doHighway4(playerPos.add(xOffset.get() * -7, 0, zOffset.get() * -7));
                    } else doHighway4(playerPos.add(xOffset.get() * -3, 0, zOffset.get() * -3));
                }
            }
            lastUpdateTime = Util.getMeasuringTimeMs();
        }
        packets = 0;
    }
}
