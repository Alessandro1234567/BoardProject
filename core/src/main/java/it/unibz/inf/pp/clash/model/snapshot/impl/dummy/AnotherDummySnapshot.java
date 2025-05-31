package it.unibz.inf.pp.clash.model.snapshot.impl.dummy;

import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.BoardImpl;
import it.unibz.inf.pp.clash.model.snapshot.impl.HeroImpl;
import it.unibz.inf.pp.clash.model.snapshot.impl.AbstractSnapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Butterfly;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Fairy;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Unicorn;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Wall;
import it.unibz.inf.pp.clash.model.utils.UnitMerger;


import static it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit.UnitColor.*;

/**
 * This class is a dummy implementation, for demonstration purposes.
 * It should not appear in the final project.
 */
public class AnotherDummySnapshot extends AbstractSnapshot implements Snapshot {


    public AnotherDummySnapshot(String firstHeroName, String secondHeroName) {
        super(
                new HeroImpl(firstHeroName, 20),
                new HeroImpl(secondHeroName, 10),
                BoardImpl.createEmptyBoard(11, 7),
                Player.FIRST,
                2,
                null
        );
//        this.ongoingMove = new TileCoordinates(6, 1);
        populateTiles();
    }

    private void populateTiles() {

        Unicorn bigUnicorn = new Unicorn(THREE);
        bigUnicorn.setAttackCountdown(2);
        bigUnicorn.setHealth(10);
        bigUnicorn.isBigUnit = true;
        Fairy bigFairy = new Fairy(TWO);
        bigFairy.setAttackCountdown(3);
        bigFairy.setHealth(15);
        bigFairy.isBigUnit = true;

        //Player 2
//        board.addUnit(0,0, new Fairy(THREE));
//        board.addUnit(3,0, new Fairy(THREE));
        board.addUnit(4, 0, new Fairy(THREE));
        board.addUnit(5, 0, new Wall());
        board.addUnit(4, 2, new Unicorn(ONE));
        board.addUnit(5, 2, new Unicorn(THREE));
        board.addUnit(2, 3, new Butterfly(THREE));
        board.addUnit(3, 3, new Butterfly(THREE));
        board.addUnit(4, 3, new Butterfly(THREE));
        board.addUnit(2, 7, new Unicorn(ONE));
        board.addUnit(3, 7, new Unicorn(ONE));
        board.addUnit(4, 7, new Unicorn(ONE));
        //board.addUnit(3, 5, new Unicorn(ONE));
        board.addUnit(4, 5, new Unicorn(ONE));
        board.addUnit(3, 6, new Butterfly(TWO));
        board.addUnit(4, 6, new Butterfly(TWO));
        board.addUnit(5, 6, new Butterfly(TWO));

        board.addUnit(5, 3, new Wall());
        board.addUnit(5, 4, new Wall());
        board.addUnit(5, 7, new Butterfly(THREE));
        board.addUnit(1,0, new Fairy(ONE));
        board.addUnit(1,1, new Fairy(ONE));
        board.addUnit(1,2, new Fairy(ONE));
        board.addUnit(5,5, new Unicorn(ONE));

        //Player 1
        board.addUnit(6, 1, new Butterfly(THREE));
        board.addUnit(6, 2, new Butterfly(ONE));
        board.addUnit(7, 2, new Butterfly(TWO));
        board.addUnit(8, 2, new Fairy(ONE));
        board.addUnit(6, 4, new Wall());
        //board.addUnit(6, 5, new Unicorn(ONE));
        //board.addUnit(7, 5, new Unicorn(ONE));
        board.addUnit(8, 5, new Unicorn(ONE));
        board.addUnit(9, 5, new Fairy(THREE));
        board.addUnit(6, 7, new Butterfly(ONE));
        board.addUnit(7, 7, new Butterfly(ONE));

        board.addUnit(4,1, new Butterfly(ONE));
        board.addUnit(5,1, new Butterfly(ONE));
        board.addUnit(6,1, new Butterfly(ONE));

        UnitMerger.boardHandler(board);
    }

    @Override
    public int getSizeOfReinforcement(Player player) {

        if (player == Player.FIRST) {
            return 3;
        }
        return 2;
    }
}
