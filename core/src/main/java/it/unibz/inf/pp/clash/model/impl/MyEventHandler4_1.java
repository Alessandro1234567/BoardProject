package it.unibz.inf.pp.clash.model.impl;

import it.unibz.inf.pp.clash.model.EventHandler;
import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.Hero;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.DummySnapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.RealSnapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.utils.UnitGenerator;
import it.unibz.inf.pp.clash.model.utils.UnitMerger;
import it.unibz.inf.pp.clash.view.DisplayManager;
import it.unibz.inf.pp.clash.view.exceptions.NoGameOnScreenException;

import java.util.Optional;

public class MyEventHandler4_1 implements EventHandler {

    private final DisplayManager displayManager;
    private RealSnapshot snapshot;

    public MyEventHandler4_1(DisplayManager displayManager) {
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
    }



    @Override
    public void skipTurn() {
        attack();
        snapshot.setActivePlayer((snapshot.getActivePlayer() == Snapshot.Player.FIRST) ? Snapshot.Player.SECOND : Snapshot.Player.FIRST);
        snapshot.setActionsRemaining(3);
        displayManager.drawSnapshot(
                (snapshot),
                "Player skipped turn."
        );
    }

    @Override
    public void callReinforcement() {
        Snapshot.Player player = snapshot.getActivePlayer();
        UnitGenerator.populateTiles(
                player,
                snapshot.getBoard(),
                snapshot.getSizeOfReinforcement(player),
                snapshot.getSizeOfReinforcement(player)
        );
        UnitMerger.boardHandler(snapshot.getBoard());
        displayManager.drawSnapshot(
                snapshot,
                "This is another dummy game snapshot, to test animations."
        );
        snapshot.setActionsRemaining(snapshot.getActionsRemaining() - 1);
    }

    @Override
    public void requestInformation(int rowIndex, int columnIndex) {
        Optional<Unit> unitOpt = snapshot.getBoard().getUnit(rowIndex, columnIndex);
        String messaggio = unitOpt.map(unit -> String.format("UNIT: %s\nHealth: %d", unit.getClass().getSimpleName(), unit.getHealth())).orElse("");
        try {
            displayManager.updateMessage(messaggio);
        } catch (NoGameOnScreenException e) {
            throw new RuntimeException(e);
        }
    }

    //metodo principale per selezionare e in seguito muovere pedina
    @Override
    public void selectTile(int rowIndex, int columnIndex) {
        Board.TileCoordinates clickedTile = new Board.TileCoordinates(rowIndex, columnIndex);
        //verifico che non abbia già selezionato una casella precedentemente
        if (snapshot.getOngoingMove().isEmpty()) {
            Optional<Unit> unitOpt = snapshot.getBoard().getUnit(rowIndex, columnIndex);
            //verifico la presenza di un unità
            if (unitOpt.isPresent()) {
                //controllo se è mobile 
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
        //snip di codice per deselezionare cliccando la stessa casella
        if (snapshot.getOngoingMove().get().equals(clickedTile)) {
            snapshot.setOngoingMove(null);
            displayManager.drawSnapshot(snapshot, "Unit deselected!");
            return;
        }
        //movimento e reset
        moveUnit(snapshot.getOngoingMove().get(), clickedTile);
        snapshot.setOngoingMove(null);
        
        //Verifica se usare metodo move forward (sfortunatamente c'era un bug che se si selezionava direttamente la prima casella libera
        //mi spostava la pediana su quella successiva (da migliorare)
        int direction = snapshot.getActivePlayer() == Snapshot.Player.FIRST ? -1 : 1;
        int prevRow = clickedTile.rowIndex() + direction;
        
        // Verifica se la casella antecedente è vuota e non territorio del giocatore
        if (isOwned(snapshot.getActivePlayer(), prevRow) &&
            snapshot.getBoard().getUnit(prevRow, clickedTile.columnIndex()).isEmpty()) {

            // sposta verso il centro solo se la casella è nel territorio del giocatore
            if (isOwned(snapshot.getActivePlayer(), clickedTile.rowIndex())) {
                Optional<Unit> movedUnit = snapshot.getBoard().getUnit(clickedTile.rowIndex(), clickedTile.columnIndex());
                movedUnit.ifPresent(unit -> moveForward(unit, clickedTile));
            }
        }
    }
    //metodo ausiliario per verificare appartenenza board
    private boolean isOwned(Snapshot.Player activePlayer, int rowIndex) {
        if (activePlayer == Snapshot.Player.FIRST) {
            return rowIndex > 5;
        } else if (activePlayer == Snapshot.Player.SECOND) {
            return rowIndex <= 5;
        }
        return false;
    }

    private void moveUnit(Board.TileCoordinates from, Board.TileCoordinates to) {
        // controllo se ci sono azioni rimanenti
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

        // controllo se è il campo avversario
        if (!isOwned(snapshot.getActivePlayer(), to.rowIndex())) {
            displayManager.drawSnapshot(snapshot, "Cannot move unit to opponent's territory!");
            return;
        }

        // controllo se casella occupata
        if (destUnitOpt.isPresent()) {
            try {
                displayManager.updateMessage("Cannot move unit: destination tile is occupied.");
            } catch (NoGameOnScreenException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try {
            // movimento
            snapshot.getBoard().addUnit(to.rowIndex(), to.columnIndex(), sourceUnit);
            snapshot.getBoard().removeUnit(from.rowIndex(), from.columnIndex());
            UnitMerger.boardHandler(snapshot.getBoard());  //TODO danial pls look at this
            // diminuisco azioni
            snapshot.setActionsRemaining(snapshot.getActionsRemaining() - 1);
            
            displayManager.drawSnapshot(snapshot, "Unit moved successfully. Actions remaining: " + snapshot.getActionsRemaining());
        } catch (Exception e) {
            try {
                displayManager.updateMessage("Error during movement: " + e.getMessage());
            } catch (NoGameOnScreenException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    //metodo ausiliare per spostare in avanti unità
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

    //Metodo per eliminare pedina con tasto destro
    @Override
    public void deleteUnit(int rowIndex, int columnIndex) {
        Optional<Unit> unitOpt = snapshot.getBoard().getUnit(rowIndex, columnIndex);
        if (unitOpt.isPresent()) {
            try {
                snapshot.getBoard().removeUnit(rowIndex, columnIndex);
                displayManager.drawSnapshot(snapshot, "Unit deleted at (" + rowIndex + ", " + columnIndex + ")");
            } catch (Exception e) {
                try {
                    displayManager.updateMessage("Error during deletion: " + e.getMessage());
                } catch (NoGameOnScreenException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            try {
                displayManager.updateMessage("No unit to delete at (" + rowIndex + ", " + columnIndex + ")");
            } catch (NoGameOnScreenException e) {
                throw new RuntimeException(e);
            }
        }
    }
} 