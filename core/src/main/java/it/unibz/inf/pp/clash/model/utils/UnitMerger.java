package it.unibz.inf.pp.clash.model.utils;

import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class that provides methods to handle unit merging on the board.
 * The code is not perfectly factorized, I did not know how to do it without breaking anything
 */

public class UnitMerger {

    /**
     * Handles the merging process on the board. It looks for horizontal and vertical
     * matches and merges them until no more matches are found. Rearranges the units in the following order (going from center to the other extreme vertically):
     * - walls
     * - big units
     * - small units
     *
     * @param board the board on which merging operations are applied
     */
    public static void boardHandler(Board board) {
        List<Coordinate> vertMatches = findVertMatch(board);
        List<Coordinate> horMatches = findHorMatch(board);

        do {
            System.out.println("Vertical matches: ");
            for (Coordinate cord : vertMatches) {
                mergeToBigUnit(board, cord.x, cord.y);
                System.out.println(cord);
            }

            System.out.println("Horizontal matches: ");
            for (Coordinate cord : horMatches) {
                mergeToWall(board, cord.x, cord.y);
                System.out.println(cord);
            }
            collapse(board);

            vertMatches = findVertMatch(board);
            horMatches = findHorMatch(board);
        } while (!vertMatches.isEmpty() || !horMatches.isEmpty());
    }

    /**
     * Checks whether the three provided units are the same type and not already merged.
     *
     * @param au1 first unit
     * @param au2 second unit
     * @param au3 third unit
     * @return true if units are of the same type and not already big units
     */
    private static boolean areValidMatchingUnits(AbstractMobileUnit au1, AbstractMobileUnit au2, AbstractMobileUnit au3) {

        return au1.equals(au2) && au1.equals(au3) &&
                !au1.isBigUnit && !au2.isBigUnit && !au3.isBigUnit;
    }

    /**
     * Finds vertically aligned matching units.
     * Only the first coordinate (topmost) of each trio is stored.
     *
     * @param board the game board
     * @return list of coordinates where vertical matches start
     */
    public static List<Coordinate> findVertMatch(Board board) {

        List<Coordinate> vertMatches = new ArrayList<>();
        int maxColumnIndex = board.getMaxColumnIndex();
        int maxRowIndex = board.getMaxRowIndex();

        //check if on the same ver line
        for (int i = 0; i <= maxColumnIndex; i++) {
            for (int j = 0; j <= maxRowIndex / 2 - 2; j++) {

                findVertMatchHelp(board, vertMatches, i, j);
            }
            for (int j = maxRowIndex / 2 + 1; j <= maxRowIndex - 2; j++) {

                findVertMatchHelp(board, vertMatches, i, j);
            }
        }
        return vertMatches;
    }

    /**
     * Helper method to find and collect vertical matches.
     *
     * @param board the game board
     * @param vertMatches list where matches are stored
     * @param i column index
     * @param j row index
     */
    private static void findVertMatchHelp(Board board, List<Coordinate> vertMatches, int i, int j) {
        Optional<Unit> opt1 = board.getUnit(j, i);
        Optional<Unit> opt2 = board.getUnit(j + 1, i);
        Optional<Unit> opt3 = board.getUnit(j + 2, i);

        AbstractMobileUnit au1 = optToAbUnit(opt1);
        AbstractMobileUnit au2 = optToAbUnit(opt2);
        AbstractMobileUnit au3 = optToAbUnit(opt3);

        if (au1 == null || au2 == null || au3 == null) return;

        if (areValidMatchingUnits(au1, au2, au3)) {
            vertMatches.add(new Coordinate(i, j));
        }
    }

    /**
     * Finds horizontally aligned matching units.
     * Only the first coordinate (leftmost) of each trio is stored.
     *
     * @param board the game board
     * @return list of coordinates where horizontal matches start
     */
    public static List<Coordinate> findHorMatch(Board board) {

        List<Coordinate> horMatches = new ArrayList<>();
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
                    horMatches.add(new Coordinate(i, j));
                }
            }
        }
        return horMatches;
    }

    /**
     * Converts an Optional<Unit> to an AbstractMobileUnit if present.
     *
     * @param opt optional unit
     * @return the abstract mobile unit or null if not present or not of the correct type
     */
    public static AbstractMobileUnit optToAbUnit(Optional<Unit> opt) {
        if (opt.isEmpty()) {
            return null;
        }
        Unit unit = opt.get();

        if (unit instanceof AbstractMobileUnit) {
            return (AbstractMobileUnit) unit;
        }
        return null;
    }

    /**
     * Converts an Optional<Unit> to a Unit if present.
     *
     * @param opt optional unit
     * @return the unit or null if not present
     */
    public static Unit optToUnit(Optional<Unit> opt) {
        return opt.orElse(null);

    }

    /**
     * Reorders the board vertically by category (walls, big units, units) going from center to the other vertical extreme.
     *
     * @param board the board to be collapsed
     */
    public static void collapse(Board board) {
        int maxColumnIndex = board.getMaxColumnIndex();
        for (int i = 0; i <= maxColumnIndex; i++) {
            columnManagerP2(board, i);
            columnManagerP1(board, i);
        }
    }

    /**
     * Rearranges the upper half of the column (Player 2).
     *
     * @param board the board
     * @param col the column index
     */
    public static void columnManagerP2(Board board, int col) {
        int maxRowIndex = board.getMaxRowIndex() / 2;
        int currentRow = maxRowIndex;
        List<AbstractMobileUnit> smallUnits = new ArrayList<>();
        List<AbstractMobileUnit> bigUnits = new ArrayList<>();
        List<Wall> walls = new ArrayList<>();

        for (int i = maxRowIndex; i >= 0; i--) {
            removeAndStoreUnits(board, col, smallUnits, bigUnits, walls, i);
        }

        for (Wall wall : walls) {
            board.addUnit(currentRow, col, wall);
            currentRow--;
        }

        for (AbstractMobileUnit bigUnit : bigUnits) {
            board.addUnit(currentRow, col, bigUnit);
            currentRow--;
        }

        for (AbstractMobileUnit smallUnit : smallUnits) {
            board.addUnit(currentRow, col, smallUnit);
            currentRow--;
        }
    }

    /**
     * Rearranges the lower half of the column.
     *
     * @param board the board
     * @param col the column index
     */
    public static void columnManagerP1(Board board, int col) {
        int maxRowIndex = board.getMaxRowIndex();
        int currentRow = maxRowIndex / 2 + 1;
        List<AbstractMobileUnit> smallUnits = new ArrayList<>();
        List<AbstractMobileUnit> bigUnits = new ArrayList<>();
        List<Wall> walls = new ArrayList<>();

        for (int i = maxRowIndex / 2 + 1; i <= maxRowIndex; i++) {
            removeAndStoreUnits(board, col, smallUnits, bigUnits, walls, i);
        }

        for (Wall wall : walls) {
            board.addUnit(currentRow, col, wall);
            currentRow++;
        }

        for (AbstractMobileUnit bigUnit : bigUnits) {
            board.addUnit(currentRow, col, bigUnit);
            currentRow++;

        }

        for (AbstractMobileUnit smallUnit : smallUnits) {
            board.addUnit(currentRow, col, smallUnit);
            currentRow++;

        }
    }
    /**
     * Helper method that removes units and saves them in a list, to rearrange them later.
     *
     * @param board the board
     * @param col the column index
     * @param smallUnits list for small units
     * @param bigUnits list for big units
     * @param walls list for walls
     * @param i row index
     */
    private static void removeAndStoreUnits(Board board, int col, List<AbstractMobileUnit> smallUnits, List<AbstractMobileUnit> bigUnits, List<Wall> walls, int i) {
        Unit unit = optToUnit(board.getUnit(i, col));
        if (unit == null) return;
        if (unit instanceof Wall) {
            walls.add((Wall) unit);
        }
        if (unit instanceof AbstractMobileUnit && ((AbstractMobileUnit) unit).getAttackCountdown() != -1) {
            bigUnits.add((AbstractMobileUnit) unit);
        } else if (unit instanceof AbstractMobileUnit) {
            smallUnits.add((AbstractMobileUnit) unit);
        }
        board.removeUnit(i, col);
    }


    /**
     * Merges three horizontally aligned units into a wall.
     *
     * @param board the board
     * @param row row of the first unit
     * @param col column of the first unit
     */

    public static void mergeToWall(Board board, int row, int col) {
        board.removeUnit(row, col);
        board.removeUnit(row, col + 1);
        board.removeUnit(row, col + 2);

        Wall wall = new Wall();

        board.addUnit(row, col, wall);
        board.addUnit(row, col + 1, wall);
        board.addUnit(row, col + 2, wall);
    }

    /**
     * Merges three vertically aligned units into a big unit.
     *
     * @param board the board
     * @param col column of the first unit
     * @param row row of the first unit
     */
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