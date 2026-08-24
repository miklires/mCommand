package io.github.miklires.mcommand.update;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UpdateCheckerTest {
    @Test void comparesSemanticVersions() {
        assertTrue(UpdateChecker.isNewer("1.1.0", "1.0.9"));
        assertTrue(UpdateChecker.isNewer("1.0.0", "1.0.0-beta.1"));
        assertFalse(UpdateChecker.isNewer("1.0.0-beta.1", "1.0.0"));
        assertFalse(UpdateChecker.isNewer("not-a-version", "1.0.0"));
    }
}
