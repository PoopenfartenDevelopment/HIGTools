package me.redcarlos.higtools.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Center extends Command {
    public Center() {
        super("center", "Centers yourself on a block.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            double x = MathHelper.floor(mc.player.getX()) + 0.5;
            double z = MathHelper.floor(mc.player.getZ()) + 0.5;
            mc.player.setPosition(x, mc.player.getY(), z);
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));

            return SINGLE_SUCCESS;
        });
    }
}
