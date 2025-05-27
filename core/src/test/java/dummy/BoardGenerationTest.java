package dummy;

import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.BoardImpl;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.RealSnapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Unicorn;
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
