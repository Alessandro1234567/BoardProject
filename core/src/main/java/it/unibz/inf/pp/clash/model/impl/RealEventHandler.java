package it.unibz.inf.pp.clash.model.impl;

import it.unibz.inf.pp.clash.model.EventHandler;
import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.Hero;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.HeroImpl;
import it.unibz.inf.pp.clash.model.snapshot.impl.RealSnapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.utils.UnitGenerator;
import it.unibz.inf.pp.clash.model.utils.UnitMerger;
import it.unibz.inf.pp.clash.view.DisplayManager;
import it.unibz.inf.pp.clash.view.exceptions.NoGameOnScreenException;

import java.util.Optional;

public class RealEventHandler implements EventHandler {

    private final DisplayManager displayManager;
    private RealSnapshot snapshot;

    public RealEventHandler(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }

    @Override
    public void newGame(String firstHero, String secondHero) {
        snapshot = new RealSnapshot(firstHero, secondHero, 7, 10);
        UnitMerger.boardHandler(snapshot.getBoard());
        displayManager.drawSnapshot(
                snapshot,
                "Game has started."
        );
    }

    @Override
    public void exitGame() {
        displayManager.drawHomeScreen();
    }

    /**
     * Executes the attack phase for the active player.

     * This method iterates through each column of the board and processes attacking units
     * located in the active player's half of the field. For each unit:
     * <ul>
     *   <li>If it is not a {@link MobileUnit}, the scan continues to the next row.</li>
     *   <li>If its attack countdown is negative, it cannot attack and is skipped.</li>
     *   <li>If the countdown reaches zero:
     *     <ul>
     *       <li>The unit attacks, triggering a column segment clear, merging, and applying damage to a target.</li>
     *       <li>{@link #doAttack(MobileUnit, int)} is invoked to resolve the attack logic.</li>
     *     </ul>
     *   </li>
     *   <li>If the countdown is greater than zero:
     *     <ul>
     *       <li>The countdown is reduced by one.</li>
     *       <li>The unit's health is increased by 40% as a "charge-up" effect.</li>
     *       <li>The loop skips 3 rows ahead to avoid multiple activations in the same turn.</li>
     *     </ul>
     *   </li>
     * </ul>
     * After all columns have been processed, reinforcements are added.
     */
    public void attack() {
        Snapshot.Player active = snapshot.getActivePlayer();
        Board board = snapshot.getBoard();
        int maxRow = board.getMaxRowIndex();
        int midRow = maxRow / 2;

        boolean isFirst = active == Snapshot.Player.FIRST;
        int startRow = isFirst ? midRow + 1 : midRow;
        int direction = isFirst ? 1 : -1;
        int maxCol = board.getMaxColumnIndex();

        for (int col = 0; col <= maxCol; col++) {
            int row = startRow;

            while (row >= 0 && row <= maxRow) {
                Optional<Unit> opt = board.getUnit(row, col);
                if (opt.isEmpty()) {
                    break;
                }

                Unit unit = opt.get();
                if (!(unit instanceof MobileUnit mobile)) {
                    row += direction;
                    continue;
                }

                int countdown = mobile.getAttackCountdown();
                if (countdown < 0) {
                    break;
                }

                if (countdown == 0) {
                    clearColumnSegment(board, col, row, direction, 3);
                    mergeColumn(board, active, col);
                    doAttack(mobile, col);
                } else {
                    mobile.setAttackCountdown(countdown - 1);
                    mobile.setHealth((int)(mobile.getHealth() * 1.4));
                    row += direction * 3;
                }
            }
        }
        addReinforcementsMax((snapshot.getActivePlayer()==Snapshot.Player.FIRST) ? Snapshot.Player.SECOND: Snapshot.Player.FIRST, 1);
    }

    private void doAttack(MobileUnit attacker, int col) {
        Board board = snapshot.getBoard();
        Snapshot.Player attackerSide = snapshot.getActivePlayer();
        Snapshot.Player defenderSide = attackerSide == Snapshot.Player.FIRST
                ? Snapshot.Player.SECOND
                : Snapshot.Player.FIRST;
        Hero defenderHero = snapshot.getHero(defenderSide);

        int maxRow = board.getMaxRowIndex();
        int midRow = maxRow / 2;
        int targetRow = attackerSide == Snapshot.Player.FIRST ? midRow : midRow + 1;

        Optional<Unit> optDef = board.getUnit(targetRow, col);
        int damage = attacker.getHealth();

        if (optDef.isEmpty()) {
            defenderHero.setHealth(defenderHero.getHealth() - damage);
        } else {
            Unit def = optDef.get();

            if (def instanceof MobileUnit defending) {
                if (defending.getAttackCountdown() >= 0 && defending.getHealth() <= damage) {
                    clearColumnSegment(board, col, targetRow,
                            attackerSide == Snapshot.Player.FIRST ? 1 : -1, 3);
                }
            }

            int remainingHealth = def.getHealth() - damage;
            if (remainingHealth <= 0) {
                board.removeUnit(targetRow, col);
            } else {
                def.setHealth(remainingHealth);
            }

            mergeColumn(board, defenderSide, col);
        }
    }

    private void clearColumnSegment(Board board, int col, int startRow,
                                    int direction, int length) {
        int maxRow = board.getMaxRowIndex();
        for (int offset = 0; offset < length; offset++) {
            int r = startRow + direction * offset;
            if (r >= 0 && r <= maxRow) {
                board.removeUnit(r, col);
            }
        }
    }

    private void mergeColumn(Board board, Snapshot.Player player, int col) {
        if (player == Snapshot.Player.FIRST) {
            UnitMerger.columnManagerP1(board, col);
        } else {
            UnitMerger.columnManagerP2(board, col);
        }
        addReinforcementsMax(snapshot.getActivePlayer(),2);
    }


    @Override
    public void skipTurn() {
        if(arePlayersDead()){
            isGameOver();
            return;
        }
        attack();
        snapshot.setActivePlayer((snapshot.getActivePlayer() == Snapshot.Player.FIRST) ? Snapshot.Player.SECOND : Snapshot.Player.FIRST);
        snapshot.setActionsRemaining(3);
        displayManager.drawSnapshot(
                (snapshot),
                "It's the turn of "+ (snapshot.getHero(snapshot.getActivePlayer())).getName()
        );
        if(arePlayersDead()){
            isGameOver();
        }
    }

    /**
     * Adds a random number of reinforcements to the specified player, up to a given maximum value.
     * The method will not add reinforcements if the player's current reinforcements are already 10 or more.
     *
     * @param player the player to whom reinforcements will be added
     * @param value the upper bound (exclusive) for the random number of reinforcements to add
     */
    private void addReinforcementsMax(Snapshot.Player player, int value) {
        if (((HeroImpl)snapshot.getHero(player)).getReinforcements() >= 5) {
        return; 
     }
        ((HeroImpl)snapshot.getHero(player)).setReinforcements(
                ((HeroImpl)snapshot.getHero(player)).getReinforcements()
                        + ((int)(Math.random() * value) + 1));
    }


    //method that calls reinforcements
    @Override
    public void callReinforcement() {
        if(arePlayersDead()){
            isGameOver();
            return;
        }
        if (snapshot.getActionsRemaining() <= 0) {
            try {
                displayManager.updateMessage("No actions remaining for this turn!");
            } catch (NoGameOnScreenException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (snapshot.getSizeOfReinforcement(snapshot.getActivePlayer()) <= 0) {
            try {
                displayManager.updateMessage("No reinforcements aviable!");
            } catch (NoGameOnScreenException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if(countEmptyTiles(snapshot.getActivePlayer()) < snapshot.getSizeOfReinforcement(snapshot.getActivePlayer())){
            ((HeroImpl)snapshot.getHero(snapshot.getActivePlayer())).setReinforcements(countEmptyTiles(snapshot.getActivePlayer()));
        }
        Snapshot.Player player = snapshot.getActivePlayer();
        UnitGenerator.populateTiles(
                player,
                snapshot.getBoard(),
                snapshot.getSizeOfReinforcement(snapshot.getActivePlayer()),
                snapshot.getSizeOfReinforcement(snapshot.getActivePlayer())
        );
        ((HeroImpl)snapshot.getHero(player)).setReinforcements(0);
        displayManager.drawSnapshot(
                snapshot,
                "Reinforcements!."
        );
        snapshot.setActionsRemaining(snapshot.getActionsRemaining() - 1);
        UnitMerger.boardHandler(snapshot.getBoard());
        if (snapshot.getActionsRemaining() == 0){
            skipTurn();
        }
    }

    //method that gives information by hovering on unit
    @Override
    public void requestInformation(int rowIndex, int columnIndex) {
        if(arePlayersDead()){
            isGameOver();
            return;
        }
        Optional<Unit> unitOpt = snapshot.getBoard().getUnit(rowIndex, columnIndex);
        String messaggio = unitOpt.map(unit -> String.format("UNIT: %s\nHealth: %d", unit.getClass().getSimpleName(), unit.getHealth())).orElse("");
        try {
            displayManager.updateMessage(messaggio);
        } catch (NoGameOnScreenException e) {
            throw new RuntimeException(e);
        }
    }

    //main method to move a unit
    @Override
    public void selectTile(int rowIndex, int columnIndex) {
        if(arePlayersDead()){
            isGameOver();
            return;
        }
        Board.TileCoordinates clickedTile = new Board.TileCoordinates(rowIndex, columnIndex);
        //check if the tile is not previously selected
        if (snapshot.getOngoingMove().isEmpty()) {
            Optional<Unit> unitOpt = snapshot.getBoard().getUnit(rowIndex, columnIndex);
            //check presence of unit
            if (unitOpt.isPresent()) {
                //checks if it's mobile unit
                Unit unit = unitOpt.get();
                if (!(unit instanceof MobileUnit)) {
                    try {
                        displayManager.updateMessage("Cannot select static units!");
                    } catch (NoGameOnScreenException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                if (!isOwned(snapshot.getActivePlayer(), rowIndex)) {
                    try {
                        displayManager.updateMessage("Cannot select opponent's unit!");
                    } catch (NoGameOnScreenException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }

                // Check if it's not between two units
                int direction = snapshot.getActivePlayer() == Snapshot.Player.FIRST ? 1 : -1;
                int nextRow = rowIndex + direction;
                if (nextRow >= 0 && nextRow <= snapshot.getBoard().getMaxRowIndex()) {
                    Optional<Unit> nextUnit = snapshot.getBoard().getUnit(nextRow, columnIndex);
                    if (nextUnit.isPresent()) {
                        try {
                            displayManager.updateMessage("Cannot select unit: there is another unit behind it!");
                        } catch (NoGameOnScreenException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                }

                snapshot.setOngoingMove(clickedTile);
                displayManager.drawSnapshot(snapshot, "Unit selected at (" + rowIndex + ", " + columnIndex + ")");
            } else {
                try {
                    displayManager.updateMessage("No unit to select here!");
                } catch (NoGameOnScreenException e) {
                    throw new RuntimeException(e);
                }
            }
            return;
        }
        //snip of code to deselect the previously selected tile
        if (snapshot.getOngoingMove().get().equals(clickedTile)) {
            snapshot.setOngoingMove(null);
            displayManager.drawSnapshot(snapshot, "Unit deselected!");
            return;
        }
        //movement and reset
        moveUnit(snapshot.getOngoingMove().get(), clickedTile);
        snapshot.setOngoingMove(null);

        int direction = snapshot.getActivePlayer() == Snapshot.Player.FIRST ? -1 : 1;
        int prevRow = clickedTile.rowIndex() + direction;
        
        // Check if the previous tile is empty and not on opponets field
        if (isOwned(snapshot.getActivePlayer(), prevRow) &&
            snapshot.getBoard().getUnit(prevRow, clickedTile.columnIndex()).isEmpty()) {

            // moves to center only it the tile is in current player field
            if (isOwned(snapshot.getActivePlayer(), clickedTile.rowIndex())) {
                Optional<Unit> movedUnit = snapshot.getBoard().getUnit(clickedTile.rowIndex(), clickedTile.columnIndex());
                movedUnit.ifPresent(unit -> moveForward(unit, clickedTile));
            }
        }
    }

    /**
     * Checks if the specified row index corresponds to a field owned by the given player.
     *
     * @param activePlayer the player whose ownership is being checked
     * @param rowIndex the index of the row to check
     * @return true if the row belongs to the active player; false otherwise
     */
    private boolean isOwned(Snapshot.Player activePlayer, int rowIndex) {
        if (activePlayer == Snapshot.Player.FIRST) {
            return rowIndex > 5;
        } else if (activePlayer == Snapshot.Player.SECOND) {
            return rowIndex <= 5;
        }
        return false;
    }

    /**
     * Moves a unit from one tile to another on the board, if the move is valid.
     *
     * If all checks pass, the unit is moved to the destination tile, the source tile is cleared,
     * the board state is updated, and the player's remaining actions are decremented.
     * If no actions remain after the move, the turn is automatically skipped.
     *
     * @param from the coordinates of the tile where the unit currently is
     * @param to the coordinates of the tile where the unit should be moved
     */
    private void moveUnit(Board.TileCoordinates from, Board.TileCoordinates to) {
        // control if I have remaining moves
        if (snapshot.getActionsRemaining() <= 0) {
            try {
                displayManager.updateMessage("No actions remaining for this turn!");
            } catch (NoGameOnScreenException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        Optional<Unit> sourceUnitOpt = snapshot.getBoard().getUnit(from.rowIndex(), from.columnIndex());
        Optional<Unit> destUnitOpt = snapshot.getBoard().getUnit(to.rowIndex(), to.columnIndex());

        Unit sourceUnit = sourceUnitOpt.get();

        // control if it's opponent field
        if (!isOwned(snapshot.getActivePlayer(), to.rowIndex())) {
            displayManager.drawSnapshot(snapshot, "Cannot move unit to opponent's territory!");
            return;
        }

        // control if tile is occupied
        if (destUnitOpt.isPresent()) {
            try {
                displayManager.updateMessage("Cannot move unit: destination tile is occupied.");
            } catch (NoGameOnScreenException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try {
            // move
            snapshot.getBoard().addUnit(to.rowIndex(), to.columnIndex(), sourceUnit);
            snapshot.getBoard().removeUnit(from.rowIndex(), from.columnIndex());
            UnitMerger.boardHandler(snapshot.getBoard());
            // decreasing actions
            snapshot.setActionsRemaining(snapshot.getActionsRemaining() - 1);
            
            displayManager.drawSnapshot(snapshot, "Unit moved successfully. Actions remaining: " + snapshot.getActionsRemaining());
        } catch (Exception e) {
            try {
                displayManager.updateMessage("Error during movement: " + e.getMessage());
            } catch (NoGameOnScreenException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (snapshot.getActionsRemaining() == 0){
            skipTurn();
        }
    }

    /**
     * Moves the specified unit forward along its column towards the center of the map.
     *
     * The method scans along the column from the starting row towards the opponent's side,
     * moving the unit to the first empty tile found.
     *
     * @param unit the unit to be moved forward
     * @param coordinates the current coordinates of the unit
     */
    private void moveForward(Unit unit, Board.TileCoordinates coordinates) {
        int startRow = snapshot.getActivePlayer() == Snapshot.Player.FIRST ? 6 : 5;
        int direction = snapshot.getActivePlayer() == Snapshot.Player.FIRST ? 1 : -1;
        int checkRow = startRow;

        while (checkRow >= 0 && checkRow <= 11) {
            Optional<Unit> checkUnit = snapshot.getBoard().getUnit(checkRow, coordinates.columnIndex());
            if (checkUnit.isEmpty()) {
                snapshot.getBoard().addUnit(checkRow, coordinates.columnIndex(), unit);
                snapshot.getBoard().removeUnit(coordinates.rowIndex(), coordinates.columnIndex());
                return;
            }
            checkRow += direction;
        }
    }

    //Method to eliminate selected unit (right click)
    @Override
    public void deleteUnit(int rowIndex, int columnIndex) {
        if(arePlayersDead()){
            isGameOver();
            return;
        }
        Optional<Unit> unitOpt = snapshot.getBoard().getUnit(rowIndex, columnIndex);
        if (unitOpt.isPresent()) {
            if (!isOwned(snapshot.getActivePlayer(), rowIndex)) {
                try {
                    displayManager.updateMessage("Cannot select opponent's unit!");
                } catch (NoGameOnScreenException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            snapshot.getBoard().removeUnit(rowIndex, columnIndex);
            UnitMerger.collapse(snapshot.getBoard());
            displayManager.drawSnapshot(snapshot, "Unit deleted at (" + rowIndex + ", " + columnIndex + ")");
            snapshot.setActionsRemaining(snapshot.getActionsRemaining() - 1);
            addReinforcementsMax(snapshot.getActivePlayer(),1);
            UnitMerger.boardHandler(snapshot.getBoard());
            if (snapshot.getActionsRemaining() == 0){
                skipTurn();
            }
            UnitMerger.boardHandler(snapshot.getBoard());
        }
    }

    /**
     * Counts the number of empty tiles on the board within the territory of the specified player.
     *
     * This method is typically used as an auxiliary helper for reinforcement calculations.
     *
     * @param player the player whose territory's empty tiles are to be counted
     * @return the total number of empty tiles in the player's territory
     */
    private int countEmptyTiles(Snapshot.Player player) {
        Board board = snapshot.getBoard();
        int maxRow = board.getMaxRowIndex();
        int maxCol = board.getMaxColumnIndex();
        int emptyCount = 0;
        int startRow, endRow;
        if (player == Snapshot.Player.FIRST) {
            startRow = (maxRow / 2) + 1;
            endRow = maxRow;
        } else {
            startRow = 0;
            endRow = maxRow / 2;
        }
        for (int row = startRow; row <= endRow; row++) {
            for (int col = 0; col <= maxCol; col++) {
                if (board.getUnit(row, col).isEmpty()) {
                    emptyCount++;
                }
            }
        }
        return emptyCount;
    }

    /**
     * Auxiliary method to check if the game has terminated
     * @return true if one of the players is dead, false otherwise
     */
    private boolean arePlayersDead(){
         return snapshot.getHero(Snapshot.Player.FIRST).getHealth() <= 0 || snapshot.getHero(Snapshot.Player.SECOND).getHealth() <= 0;
    }

    /**
     * Auxiliary method to terminate the game
     */
    private void isGameOver(){
        try {
            displayManager.updateMessage("GAME OVER!");
        } catch (NoGameOnScreenException e) {
            throw new RuntimeException(e);
        }
    }
} 