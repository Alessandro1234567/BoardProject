package it.unibz.inf.pp.clash.model.snapshot.impl;

import it.unibz.inf.pp.clash.model.snapshot.Hero;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.utils.UnitGenerator;


public class RealSnapshot extends AbstractSnapshot implements Snapshot {

    private Hero hero1;

    public RealSnapshot(String firstHeroName, String secondHeroName, int min, int max) {
        super(
                new HeroImpl(firstHeroName, 20),
                new HeroImpl(secondHeroName, 20),
                BoardImpl.createEmptyBoard(11, 7),
                Player.FIRST,
                3,
                null
        );
//        this.ongoingMove = new TileCoordinates(6, 1);
        UnitGenerator.populateTiles(Player.FIRST, getBoard(), min, max);
        UnitGenerator.populateTiles(Player.SECOND, getBoard(), min, max);
    }

    @Override
    public int getSizeOfReinforcement(Player player) {
        if (player == Player.FIRST) {
            return ((HeroImpl)getHero(player)).getReinforcements();
        }
        return ((HeroImpl)getHero(player)).getReinforcements();
    }
}
