package it.unibz.inf.pp.clash.model.impl;

import it.unibz.inf.pp.clash.model.EventHandler;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.AnotherDummySnapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.DummySnapshot;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.RealSnapshot;
import it.unibz.inf.pp.clash.model.utils.UnitGenerator;
import it.unibz.inf.pp.clash.model.utils.UnitGenerator;
import it.unibz.inf.pp.clash.view.DisplayManager;
import it.unibz.inf.pp.clash.view.exceptions.NoGameOnScreenException;

/**
 * This is a dummy implementation, for demonstration purposes.
 * It should not appear in the final project.
 */
public class DummyEventHandler implements EventHandler {

<<<<<<< HEAD
    RealSnapshot currentSnapshot;

=======
>>>>>>> Nicola
    private final DisplayManager displayManager;

    public DummyEventHandler(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }

    @Override
    public void selectTile(int rowIndex, int columnIndex) {
        try {
            displayManager.updateMessage(
                    String.format(
                            "Tile (%s,%s) has just been selected",
                            rowIndex,
                            columnIndex
                    ));
        } catch (NoGameOnScreenException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void requestInformation(int rowIndex, int columnIndex) {
        try {
            displayManager.updateMessage(
                    String.format(
                            "Information request for Tile (%s,%s).",
                            rowIndex,
                            columnIndex
                    ));
        } catch (NoGameOnScreenException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUnit(int rowIndex, int columnIndex) {
        try {
            displayManager.updateMessage(
                    String.format(
                            "Unit deletion request for Tile (%s,%s)",
                            rowIndex,
                            columnIndex
                    ));
        } catch (NoGameOnScreenException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void newGame(String firstHero, String secondHero) {
        currentSnapshot = new RealSnapshot(firstHero, secondHero, 7, 11);
        displayManager.drawSnapshot(
<<<<<<< HEAD
                currentSnapshot,
=======
                new AnotherDummySnapshot(
                        firstHero,
                        secondHero
                ),
>>>>>>> Nicola
                "This is a dummy game snapshot, for demonstration purposes."
        );
    }

    @Override
    public void callReinforcement() {
        Snapshot.Player player = currentSnapshot.getActivePlayer();
        UnitGenerator.populateTiles(
                player,
                currentSnapshot.getBoard(),
                currentSnapshot.getSizeOfReinforcement(player),
                currentSnapshot.getSizeOfReinforcement(player)
        );
        displayManager.drawSnapshot(
                currentSnapshot,
                "This is another dummy game snapshot, to test animations."
        );
    }

    @Override
    public void skipTurn() {
        displayManager.drawSnapshot(
                new DummySnapshot(
                        "Alice",
                        "Bob"
                ),
                "This is a dummy game snapshot, for demonstration purposes."
        );
    }

    @Override
    public void exitGame() {
        displayManager.drawHomeScreen();
    }
}
