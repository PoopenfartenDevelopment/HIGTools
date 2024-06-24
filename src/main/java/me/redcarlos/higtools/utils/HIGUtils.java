package me.redcarlos.higtools.utils;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.util.math.BlockPos;

import java.util.stream.IntStream;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class HIGUtils {
    private HIGUtils() {}

    /**
     * Packets
     */
    private static final Int2IntMap packetToClient = new Int2IntOpenHashMap();
    private static final Int2IntMap clientToPacket = new Int2IntOpenHashMap();

    static {
        packetToClient.put(5, 39);
        clientToPacket.put(39, 5);
        packetToClient.put(6, 38);
        clientToPacket.put(38, 6);
        packetToClient.put(7, 37);
        clientToPacket.put(37, 7);
        packetToClient.put(8, 36);
        clientToPacket.put(36, 8);
        packetToClient.put(45, 40);
        clientToPacket.put(40, 45);
        IntStream.rangeClosed(9, 35).forEach(i -> {
            packetToClient.put(i, i);
            clientToPacket.put(i, i);
        });
        IntStream.rangeClosed(0, 8).forEach(i -> {
            packetToClient.put(i + 36, i);
            clientToPacket.put(i, i + 36);
        });
    }

    public static int csToPs(int clientSlot) {
        return clientToPacket.getOrDefault(clientSlot, -1);
    }

    /**
     * Block pos
     */
    public static BlockPos forward(BlockPos pos, int distance) {
        return switch (mc.player.getHorizontalFacing()) {
            case SOUTH -> pos.south(distance);
            case NORTH -> pos.north(distance);
            case WEST -> pos.west(distance);
            default -> pos.east(distance);
        };
    }

    public static BlockPos backward(BlockPos pos, int distance) {
        return switch (mc.player.getHorizontalFacing()) {
            case SOUTH -> pos.north(distance);
            case NORTH -> pos.south(distance);
            case WEST -> pos.east(distance);
            default -> pos.west(distance);
        };
    }

    public static BlockPos left(BlockPos pos, int distance) {
        return switch (mc.player.getHorizontalFacing()) {
            case SOUTH -> pos.east(distance);
            case NORTH -> pos.west(distance);
            case WEST -> pos.south(distance);
            default -> pos.north(distance);
        };
    }

    public static BlockPos right(BlockPos pos, int distance) {
        return switch (mc.player.getHorizontalFacing()) {
            case SOUTH -> pos.west(distance);
            case NORTH -> pos.east(distance);
            case WEST -> pos.north(distance);
            default -> pos.south(distance);
        };
    }

    /**
     * Highway Axis
     */
    public static int getHighway() {
        double playerZ = mc.player.getZ();
        double playerX = mc.player.getX();
        boolean x = Math.abs(playerZ) < 5;
        boolean z = Math.abs(playerX) < 5;
        boolean xp = Math.signum(playerX) == 1.0;
        boolean zp = Math.signum(playerZ) == 1.0;
        boolean diag = Math.abs(Math.abs(playerX) - Math.abs(playerZ)) < 5;

        if (x && xp) return 1;
        if (x) return 2;
        if (z && zp) return 3;
        if (z) return 4;
        if (diag && xp && zp) return 5;
        if (diag && !xp && zp) return 6;
        if (diag && xp) return 7;
        if (diag) return 8;
        return -1;
    }

    /**
     * Desktop notifications (soon:tm:)
     */

    /*
     * This section of the file is part of Baritone
     * Thanks to the Baritone team for the source code.
     * Download Baritone here : https://github.com/cabaletta/baritonefile
     */


    /**
     * Send a message as a desktop notification
     * @param message The message to display in the notification
     *
    private void logNotification(String message) {
        logNotification(message, false);
    }
    */
    /**
     * Send a message as a desktop notification
     * @param message The message to display in the notification
     * @param error   Whether to log as an error
     */
    /*
    private void logNotification(String message, boolean error) {
        if (-1) {
            logNotificationDirect(message, error);
        }
    }

    /**
     * Send a message as a desktop notification regardless of desktopNotifications
     * (should only be used for critically important messages)
     * @param message The message to display in the notification
     */
        /*
    private void logNotificationDirect(String message) {
        logNotificationDirect(message, false);
    }

    /**
     * Send a message as a desktop notification regardless of desktopNotifications
     * (should only be used for critically important messages)
     * @param message The message to display in the notification
     * @param error   Whether to log as an error
     */
        /*
    private void logNotificationDirect(String message, boolean error) {
        MinecraftClient.getInstance().execute(() -> BaritoneAPI.getSettings().notifier.value.accept(message, error));
    }
    */
}
