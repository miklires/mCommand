package io.github.miklires.mcommand.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandSafetyTest {
    @Test void acceptsOneCommand() {
        assertTrue(CommandSafety.isSingleCommand("say hello world"));
    }

    @Test void rejectsBlankAndLineBreakInjection() {
        assertFalse(CommandSafety.isSingleCommand("  "));
        assertFalse(CommandSafety.isSingleCommand("say safe\nop someone"));
        assertFalse(CommandSafety.isSingleCommand("say safe\rstop"));
    }

    @Test void acceptsCoordinatesInsideBounds() {
        assertTrue(CommandSafety.areCoordinatesAllowed(10, 64, -20, 1000, -64, 320));
    }

    @Test void rejectsNonFiniteAndOutOfWorldCoordinates() {
        assertFalse(CommandSafety.areCoordinatesAllowed(Double.NaN, 64, 0, 1000, -64, 320));
        assertFalse(CommandSafety.areCoordinatesAllowed(1001, 64, 0, 1000, -64, 320));
        assertFalse(CommandSafety.areCoordinatesAllowed(0, 321, 0, 1000, -64, 320));
    }

    @Test void countsUnicodeCodePointsForInputLimits() {
        assertTrue(CommandSafety.isLengthAllowed("ab😀", 3));
        assertFalse(CommandSafety.isLengthAllowed("ab😀", 2));
        assertFalse(CommandSafety.isLengthAllowed(null, 10));
        assertFalse(CommandSafety.isLengthAllowed("value", 0));
    }
}
