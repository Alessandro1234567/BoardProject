package it.unibz.inf.pp.clash.model.snapshot.impl.dummy;

import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.AbstractSnapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.BoardImpl;
import it.unibz.inf.pp.clash.model.snapshot.impl.HeroImpl;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Butterfly;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Fairy;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Unicorn;
import it.unibz.inf.pp.clash.model.utils.UnitGenerator;

import java.util.ArrayList;
import java.util.Random;


public class RealSnapshot extends AbstractSnapshot implements Snapshot {

    public RealSnapshot(String firstHeroName, String secondHeroName) {
        super(
                new HeroImpl(firstHeroName, 20),
                new HeroImpl(secondHeroName, 10),
                BoardImpl.createEmptyBoard(7, 6),
                Player.FIRST,
                2,
                null
        );
//        this.ongoingMove = new TileCoordinates(6, 1);
        UnitGenerator.populateTiles(Player.FIRST, getBoard(), 7, 11);
        UnitGenerator.populateTiles(Player.SECOND, getBoard(), 7, 11);
    }

    @Override
    public int getSizeOfReinforcement(Player player) {

        if (player == Player.FIRST) {
            return 3;
        }
        return 2;
    }
}
