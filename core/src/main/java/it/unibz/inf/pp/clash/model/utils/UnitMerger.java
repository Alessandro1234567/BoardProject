package it.unibz.inf.pp.clash.model.utils;

import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UnitMerger {

    public static void baordHandle(Board board) {
        List<Coordinate> vertMatches = findVertMatch(board);
        List<Coordinate> horMatches = findHorMatch(board);

        System.out.println("Vert matches: ");
        for (Coordinate coord : vertMatches) {
            mergeToBigUnit(board, coord.x, coord.y);
            System.out.println(coord);
        }

        System.out.println("Hor matches: ");
        for (Coordinate coord : horMatches) {
            mergeToWall(board, coord.x, coord.y);
            System.out.println(coord);
        }
        collapse(board);
    }

    private static boolean areValidMatchingUnits(AbstractMobileUnit au1, AbstractMobileUnit au2, AbstractMobileUnit au3) {
        return au1.equals(au2) && au1.equals(au3) &&
                !au1.isBigUnit && !au2.isBigUnit && !au3.isBigUnit;
    }

    public static List<Coordinate> findVertMatch(Board board) {

        List<Coordinate> vertMatches = new ArrayList<>();
        int maxColumnIndex = board.getMaxColumnIndex();
        int maxRowIndex = board.getMaxRowIndex();

        //check if on the same ver line
        for (int i = 0; i <= maxColumnIndex; i++) {
            for (int j = 0; j <= maxRowIndex - 2; j++) {

                Optional<Unit> opt1 = board.getUnit(j, i);
                Optional<Unit> opt2 = board.getUnit(j + 1, i);
                Optional<Unit> opt3 = board.getUnit(j + 2, i);

                AbstractMobileUnit au1 = optToAbUnit(opt1);
                AbstractMobileUnit au2 = optToAbUnit(opt2);
                AbstractMobileUnit au3 = optToAbUnit(opt3);

                if (au1 == null || au2 == null || au3 == null) continue;

                if (areValidMatchingUnits(au1, au2, au3)) {
                    vertMatches.add(new Coordinate(i, j));
                }
            }
        }
        return vertMatches;
    }

    public static List<Coordinate> findHorMatch(Board board) {

        List<Coordinate> vertMatches = new ArrayList<>();
        int maxColumnIndex = board.getMaxColumnIndex();
        int maxRowIndex = board.getMaxRowIndex();

        //check if on the same hor line
        for (int i = 0; i <= maxRowIndex; i++) {
            for (int j = 0; j <= maxColumnIndex - 2; j++) {

                Optional<Unit> opt1 = board.getUnit(i, j);
                Optional<Unit> opt2 = board.getUnit(i, j + 1);
                Optional<Unit> opt3 = board.getUnit(i, j + 2);

                AbstractMobileUnit au1 = optToAbUnit(opt1);
                AbstractMobileUnit au2 = optToAbUnit(opt2);
                AbstractMobileUnit au3 = optToAbUnit(opt3);

                if (au1 == null || au2 == null || au3 == null) continue;

                if (areValidMatchingUnits(au1, au2, au3)) {
                    vertMatches.add(new Coordinate(i, j));
                }
            }
        }
        return vertMatches;
    }

    public static AbstractMobileUnit optToAbUnit(Optional<Unit> opt) {
        if (opt.isEmpty()) {
            return null;
        }
        Unit unit = opt.get();

        if (unit instanceof AbstractMobileUnit) {
            AbstractMobileUnit au = (AbstractMobileUnit) unit;
            return au;
        }
        return null;
    }

    public static Unit optToUnit(Optional<Unit> opt){
        if (opt.isEmpty()) {
            return null;
        }
        Unit unit = opt.get();

        return (Unit) unit;
    }

    public static void collapse(Board board) {

        int maxColIndex = board.getMaxColumnIndex();
        int maxRowIndex = board.getMaxRowIndex()/2;

        List<AbstractMobileUnit> smallUnits = new ArrayList<>();
        List<AbstractMobileUnit> bigUnits = new ArrayList<>();
        List<Wall> walls = new ArrayList<>();

        for (int col = 0; col <= maxColIndex; col++) {
            for (int row = 0; row <= maxRowIndex; row++) {
                Unit unit = optToUnit(board.getUnit(row, col));
                if (unit == null) continue;
                if (unit instanceof Wall){
                    walls.add((Wall) unit);
                }
                if (unit instanceof AbstractMobileUnit && ((AbstractMobileUnit) unit).getAttackCountdown() != -1){
                    bigUnits.add((AbstractMobileUnit) unit);
                } else if (unit instanceof AbstractMobileUnit){
                    smallUnits.add((AbstractMobileUnit) unit);
                }
                board.removeUnit(row, col);
            }
        }

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

    public static void mergeToBigUnit(Board board, int col, int row) {
        Optional<Unit> opt1 = board.getUnit(row, col);
        AbstractMobileUnit au1 = optToAbUnit(opt1);

        board.removeUnit(row, col);
        board.removeUnit(row + 1, col);
        board.removeUnit(row + 2, col);

        assert au1 != null;
        AbstractMobileUnit bigUnit = au1.createBigVersion();

        board.addUnit(row, col, bigUnit);
        board.addUnit(row + 1, col, bigUnit);
        board.addUnit(row + 2, col, bigUnit);
    }
}

