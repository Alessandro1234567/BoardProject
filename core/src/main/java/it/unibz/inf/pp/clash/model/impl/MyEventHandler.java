package it.unibz.inf.pp.clash.model.impl;

import it.unibz.inf.pp.clash.model.EventHandler;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.DummySnapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.AbstractUnit;
import it.unibz.inf.pp.clash.view.DisplayManager;
import it.unibz.inf.pp.clash.view.exceptions.NoGameOnScreenException;

import java.util.Optional;

public class MyEventHandler implements EventHandler {

    private final DisplayManager displayManager;
    private DummySnapshot snapshot;

    public MyEventHandler(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }

    @Override
    public void newGame(String firstHero, String secondHero) {
        displayManager.drawSnapshot(
                this.snapshot =  new DummySnapshot(firstHero, secondHero),
                "Game has started."
        );
    }

    @Override
    public void exitGame() {
        displayManager.drawHomeScreen();
    }

    @Override
    public void skipTurn() {
        snapshot.setActivePlayer((snapshot.getActivePlayer() == Snapshot.Player.FIRST) ? Snapshot.Player.SECOND : Snapshot.Player.FIRST);
        snapshot.setActionsRemaining(3);
        displayManager.drawSnapshot(
                (snapshot),
                "Player skipped turn."
        );

    }

    @Override
    public void callReinforcement() {

    }

//    @Override
//    public void requestInformation(int rowIndex, int columnIndex) {
//        Optional<Unit> unitOpt = snapshot.getBoard().getUnit(rowIndex, columnIndex);
//        unitOpt.ifPresent(unit -> {
//            try {
//                displayManager.updateMessage(
//                        String.format("UNIT: (%d), (%s)", unit.getHealth(), unit.getClass().getSimpleName())
//                );
//            } catch (NoGameOnScreenException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }

    @Override
    public void requestInformation(int rowIndex, int columnIndex) {
        Optional<Unit> unitOpt = snapshot.getBoard().getUnit(rowIndex, columnIndex);
        String messaggio = unitOpt.map(unit -> String.format("UNIT: %s\nHealth: %d", unit.getClass().getSimpleName(),unit.getHealth())).orElse("");
        try {
            displayManager.updateMessage(messaggio);
        } catch (NoGameOnScreenException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void selectTile(int rowIndex, int columnIndex) {

    }

    @Override
    public void deleteUnit(int rowIndex, int columnIndex) {

    }
}
