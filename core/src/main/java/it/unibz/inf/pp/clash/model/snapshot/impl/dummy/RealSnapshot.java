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
        populateTiles(Player.FIRST);
        populateTiles(Player.SECOND);
    }

    private Unit generateUnit(){
        int type = new Random().nextInt(3);
        MobileUnit.UnitColor[] values = MobileUnit.UnitColor.values();
        MobileUnit.UnitColor randomColor = values[new Random().nextInt(values.length)];

        return switch (type) {
            case 0 -> new Butterfly(randomColor);
            case 1 -> new Fairy(randomColor);
            case 2 -> new Unicorn(randomColor);
            default -> null;
        };
    }

    private ArrayList<Unit> generateRandomUnits(int min, int max){
        ArrayList <Unit> temp = new ArrayList<>();
        int numberOfUnits = new Random().nextInt(max - min + 1) + min;
        System.out.println(numberOfUnits);

        for(int i = 0; i < numberOfUnits; i++){ temp.add(generateUnit()); }


        return temp;
    }

    private void populateTiles(Player player) {
        System.out.println(player.name());
        int increment = player.equals(Player.FIRST) ? 1 : -1;
        final int rows = board.getMaxRowIndex() / 2 + (player.equals(Player.FIRST) ? 1 : 0);
        ArrayList<Unit> units = generateRandomUnits(7, 11);

        for (Unit unit : units) {
            int row = rows;
            int col = new Random().nextInt(board.getMaxColumnIndex() + 1);

            while (board.getUnit(row, col).isPresent()) {
                row += increment;
            }

            board.addUnit(row, col, unit);
        }
    }

    @Override
    public int getSizeOfReinforcement(Player player) {

        if (player == Player.FIRST) {
            return 3;
        }
        return 2;
    }
}
