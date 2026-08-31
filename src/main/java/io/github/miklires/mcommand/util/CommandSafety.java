package io.github.miklires.mcommand.util;

public final class CommandSafety {
    private CommandSafety() { }

    public static boolean isSingleCommand(String command) {
        return command != null && !command.isBlank() && command.indexOf('\n') < 0 && command.indexOf('\r') < 0;
    }

    public static boolean isLengthAllowed(String value, int maximum) {
        return value != null && maximum > 0 && value.codePointCount(0, value.length()) <= maximum;
    }

    public static boolean areCoordinatesAllowed(double x, double y, double z, double maxHorizontal,
                                                double minHeight, double maxHeight) {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)
                && Math.abs(x) <= maxHorizontal && Math.abs(z) <= maxHorizontal
                && y >= minHeight && y <= maxHeight;
    }
}
