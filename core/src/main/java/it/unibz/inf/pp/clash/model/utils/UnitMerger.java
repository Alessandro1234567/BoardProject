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

    public static void merge(Board board) {

        int maxColumnIndex = board.getMaxColumnIndex();
        int maxRowIndex = board.getMaxRowIndex();

        for (int i = 0; i <= maxColumnIndex; i++) {
            for (int j = 0; j <= maxRowIndex - 2; j++) {

                Optional<Unit> opt1 = board.getUnit(j, i);
                Optional<Unit> opt2 = board.getUnit(j + 1, i);
                Optional<Unit> opt3 = board.getUnit(j + 2, i);

                if (opt1.isEmpty() || opt2.isEmpty() || opt3.isEmpty()) continue;

                Unit unit1 = opt1.get();
                Unit unit2 = opt2.get();
                Unit unit3 = opt3.get();

                if (!(unit1 instanceof AbstractMobileUnit) ||
                        !(unit2 instanceof AbstractMobileUnit) ||
                        !(unit3 instanceof AbstractMobileUnit)) continue;

                AbstractMobileUnit au1 = (AbstractMobileUnit) opt1.get();
                AbstractMobileUnit au2 = (AbstractMobileUnit) opt2.get();
                AbstractMobileUnit au3 = (AbstractMobileUnit) opt3.get();

                if (areValidMatchingUnits(au1, au2, au3)) {

                    board.removeUnit(j, i);
                    board.removeUnit(j + 1, i);
                    board.removeUnit(j + 2, i);

                    AbstractMobileUnit bigUnit = au1.createBigVersion();

                    board.addUnit(j, i, bigUnit);
                    board.addUnit(j + 1, i, bigUnit);
                    board.addUnit(j + 2, i, bigUnit);

                }
            }
        }

        for (int i = 0; i <= maxRowIndex; i++) {
            for (int j = 0; j <= maxColumnIndex - 2; j++) {

                Optional<Unit> opt1 = board.getUnit(i, j);
                Optional<Unit> opt2 = board.getUnit(i, j + 1);
                Optional<Unit> opt3 = board.getUnit(i, j + 2);

                if (opt1.isEmpty() || opt2.isEmpty() || opt3.isEmpty()) continue;

                Unit unit1 = opt1.get();
                Unit unit2 = opt2.get();
                Unit unit3 = opt3.get();

                if (!(unit1 instanceof AbstractMobileUnit) ||
                        !(unit2 instanceof AbstractMobileUnit) ||
                        !(unit3 instanceof AbstractMobileUnit)) continue;

                AbstractMobileUnit au1 = (AbstractMobileUnit) opt1.get();
                AbstractMobileUnit au2 = (AbstractMobileUnit) opt2.get();
                AbstractMobileUnit au3 = (AbstractMobileUnit) opt3.get();

                if (areValidMatchingUnits(au1, au2, au3)) {
                    board.removeUnit(i, j);
                    board.removeUnit(i, j + 1);
                    board.removeUnit(i, j + 2);

                    Wall wall = new Wall();

                    board.addUnit(i, j, wall);
                    board.addUnit(i, j + 1, wall);
                    board.addUnit(i, j + 2, wall);
                }
            }
        }
    }
}
