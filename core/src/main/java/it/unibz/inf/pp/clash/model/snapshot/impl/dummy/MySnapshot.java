package it.unibz.inf.pp.clash.model.snapshot.impl.dummy;

import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.Hero;
import it.unibz.inf.pp.clash.model.snapshot.impl.AbstractSnapshot;

public class MySnapshot extends AbstractSnapshot {

    protected MySnapshot(Hero firstHero, Hero secondHero, Board board, Player activeplayer, int actionsRemaining, Board.TileCoordinates ongoingMove) {
        super(firstHero, secondHero, board, activeplayer, actionsRemaining, ongoingMove);
    }

    @Override
    public int getSizeOfReinforcement(Player player) {
        return 0;
    }

}
