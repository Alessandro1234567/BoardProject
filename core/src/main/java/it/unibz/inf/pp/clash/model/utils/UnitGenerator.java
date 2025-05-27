package it.unibz.inf.pp.clash.model.utils;

import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Butterfly;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Fairy;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Unicorn;

import java.util.ArrayList;
import java.util.Random;

public class UnitGenerator {
    static private Unit generateUnit(){
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

    static private ArrayList<Unit> generateRandomUnits(int min, int max){
        ArrayList <Unit> temp = new ArrayList<>();
        int numberOfUnits = new Random().nextInt(max - min + 1) + min;
        System.out.println(numberOfUnits);

        for(int i = 0; i < numberOfUnits; i++){ temp.add(generateUnit()); }


        return temp;
    }

    public static void populateTiles(Snapshot.Player player, Board board, int min, int max) {
        System.out.println(player.name());
        int increment = player.equals(Snapshot.Player.FIRST) ? 1 : -1;
        final int rows = board.getMaxRowIndex() / 2 + (player.equals(Snapshot.Player.FIRST) ? 1 : 0);
        ArrayList<Unit> units = generateRandomUnits(min,max);

        for (Unit unit : units) {
            int row = rows;
            int col = new Random().nextInt(board.getMaxColumnIndex() + 1);

            while (board.getUnit(row, col).isPresent()) {
                if(row == 0 || row == board.getMaxRowIndex()){
                    col = new Random().nextInt(board.getMaxColumnIndex() + 1);
                    row = rows;
                }else{
                    row += increment;
                }
            }

            board.addUnit(row, col, unit);
        }

    }
}
