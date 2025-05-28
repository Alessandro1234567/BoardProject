package it.unibz.inf.pp.clash.model.impl;

import it.unibz.inf.pp.clash.model.EventHandler;
import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.HeroImpl;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.DummySnapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.RealSnapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.utils.UnitGenerator;
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
        displayManager.drawSnapshot(
                snapshot,
                "Game has started."
        );
    }

    @Override
    public void exitGame() {
        displayManager.drawHomeScreen();
    }

    private void attack() {
        Snapshot.Player activePlayer = snapshot.getActivePlayer();
        final int cols = snapshot.getBoard().getMaxRowIndex() / 2 + (activePlayer.equals(Snapshot.Player.FIRST) ? 1 : 0);
        int col = cols;

        for (int i = 0; i <= snapshot.getBoard().getMaxColumnIndex(); i++){
            System.out.println(i + " " + col);
            Optional<Unit> optUnit = snapshot.getBoard().getUnit(i,col);
            if (optUnit.isPresent()){
                Unit unit = optUnit.get();
                while (!(unit instanceof MobileUnit) || ((MobileUnit) unit).getAttackCountdown() > -1){

                    if (((MobileUnit) unit).getAttackCountdown() == 0){
                        //attack
                    }

                    if (((MobileUnit) unit).getAttackCountdown() > 0){
                        unit.setHealth((int) (unit.getHealth() * 0.4));
                        col = col + 2;
                    }

                    col++;
                    Optional<Unit> temp = snapshot.getBoard().getUnit(i,col);
                    if (temp.isEmpty()) break;
                    unit = temp.get();
                }
                col = cols;
            }
        }
    }

    @Override
    public void skipTurn() {
        attack();
        ((HeroImpl)snapshot.getHero(snapshot.getActivePlayer())).setReinforcements(3);
        snapshot.setActivePlayer((snapshot.getActivePlayer() == Snapshot.Player.FIRST) ? Snapshot.Player.SECOND : Snapshot.Player.FIRST);
        snapshot.setActionsRemaining(3);
        displayManager.drawSnapshot(
                (snapshot),
                "Player skipped turn."
        );
    }

    @Override
    public void callReinforcement() {
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

                // Controlla che non sia tra due unità
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
            if (!isOwned(snapshot.getActivePlayer(), rowIndex)) {
                try {
                    displayManager.updateMessage("Cannot select opponent's unit!");
                } catch (NoGameOnScreenException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            snapshot.getBoard().removeUnit(rowIndex, columnIndex);
            displayManager.drawSnapshot(snapshot, "Unit deleted at (" + rowIndex + ", " + columnIndex + ")");
            snapshot.setActionsRemaining(snapshot.getActionsRemaining() - 1);
            //((HeroImpl)snapshot.getHero(snapshot.getActivePlayer())).setReinforcements(0);
        }
    }
} 