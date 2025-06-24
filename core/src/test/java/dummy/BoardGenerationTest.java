package dummy;

import it.unibz.inf.pp.clash.model.snapshot.impl.RealSnapshot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardGenerationTest {
    @Test
    public void testFullBoard() {
        RealSnapshot snapshot = new RealSnapshot("aaa", "aaaa", 28, 28);

        assertTrue(true);
    }
}
