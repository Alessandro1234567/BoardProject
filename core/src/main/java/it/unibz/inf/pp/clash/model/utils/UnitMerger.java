package it.unibz.inf.pp.clash.model.utils;

import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.*;

import java.util.Optional;


public class UnitMerger {

    private static boolean areValidMatchingUnits(AbstractMobileUnit au1, AbstractMobileUnit au2, AbstractMobileUnit au3) {
        return au1.equals(au2) && au1.equals(au3) &&
                !au1.isBigUnit && !au2.isBigUnit && !au3.isBigUnit;
    }

    public static void baordHandle(Board board) {

        int maxColumnIndex = board.getMaxColumnIndex();
        int maxRowIndex = board.getMaxRowIndex();

        //check if on the same ver line
        for (int i = 0; i <= maxColumnIndex; i++) {
            for (int j = 0; j <= maxRowIndex - 2; j++) {

                Optional<Unit> opt1 = board.getUnit(j, i);
                Optional<Unit> opt2 = board.getUnit(j + 1, i);
                Optional<Unit> opt3 = board.getUnit(j + 2, i);

                if (opt1.isEmpty() || opt2.isEmpty() || opt3.isEmpty()) continue;

                Unit unit1 = opt1.get();
                Unit unit2 = opt2.get();
                Unit unit3 = opt3.get();

                if (!(unit1 instanceof AbstractMobileUnit au1) ||
                        !(unit2 instanceof AbstractMobileUnit au2) ||
                        !(unit3 instanceof AbstractMobileUnit au3)) continue;

                if (areValidMatchingUnits(au1, au2, au3)) {

                    mergeToBigUnit(board, j, i, au1);
                }
            }
        }

        //check if on the same hor line
        for (int i = 0; i <= maxRowIndex; i++) {
            for (int j = 0; j <= maxColumnIndex - 2; j++) {

                Optional<Unit> opt1 = board.getUnit(i, j);
                Optional<Unit> opt2 = board.getUnit(i, j + 1);
                Optional<Unit> opt3 = board.getUnit(i, j + 2);

                if (opt1.isEmpty() || opt2.isEmpty() || opt3.isEmpty()) continue;

                Unit unit1 = opt1.get();
                Unit unit2 = opt2.get();
                Unit unit3 = opt3.get();

                if (!(unit1 instanceof AbstractMobileUnit au1) ||
                        !(unit2 instanceof AbstractMobileUnit au2) ||
                        !(unit3 instanceof AbstractMobileUnit au3)) continue;

                if (areValidMatchingUnits(au1, au2, au3)) {
                    mergeToWall(board, i, j);
                }
            }
        }
    }

    public static void collapse(Board board) {

    }

    public static void mergeToWall(Board board, int row, int col) {
        board.removeUnit(row, col);
        board.removeUnit(row, col + 1);
        board.removeUnit(row, col + 2);

        Wall wall = new Wall();

        board.addUnit(row, col, wall);
        board.addUnit(row, col + 1, wall);
        board.addUnit(row, col + 2, wall);
    }

    public static void mergeToBigUnit(Board board, int j, int i, AbstractMobileUnit au1) {
        board.removeUnit(j, i);
        board.removeUnit(j + 1, i);
        board.removeUnit(j + 2, i);

        AbstractMobileUnit bigUnit = au1.createBigVersion();

        board.addUnit(j, i, bigUnit);
        board.addUnit(j + 1, i, bigUnit);
        board.addUnit(j + 2, i, bigUnit);
    }

}
