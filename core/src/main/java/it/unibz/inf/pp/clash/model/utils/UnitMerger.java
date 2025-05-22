package it.unibz.inf.pp.clash.model.utils;

import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier;
import it.unibz.inf.pp.clash.model.snapshot.Board;
import it.unibz.inf.pp.clash.model.snapshot.Snapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.AbstractMobileUnit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Butterfly;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Fairy;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Unicorn;

import java.util.Optional;

/*
funzione hasThreeInARow(board):
    N = numero di righe della board
            M = numero di colonne della board

    // Controlla righe
    per ogni riga da 0 a N-1:
    per ogni col da 0 a M-3:
    val = board[riga][col]
    se val ≠ null e val == board[riga][col+1] e val == board[riga][col+2]:
    ritorna true

    // Controlla colonne
    per ogni col da 0 a M-1:
    per ogni riga da 0 a N-3:
    val = board[riga][col]
    se val ≠ null e val == board[riga+1][col] e val == board[riga+2][col]:
    ritorna true

    // Controlla diagonali principali \
    per ogni riga da 0 a N-3:
    per ogni col da 0 a M-3:
    val = board[riga][col]
    se val ≠ null e val == board[riga+1][col+1] e val == board[riga+2][col+2]:
    ritorna true

    // Controlla diagonali secondarie /
    per ogni riga da 0 a N-3:
    per ogni col da 2 a M-1:
    val = board[riga][col]
    se val ≠ null e val == board[riga+1][col-1] e val == board[riga+2][col-2]:
    ritorna true

    ritorna false
*/

public class UnitMerger {
    public static void merge(Board board) {


        int maxColumnIndex = board.getMaxColumnIndex();
        int maxRowIndex = board.getMaxRowIndex();
        for (int i = 0; i <= maxColumnIndex; i++) {
            for (int j = 0; j <= maxRowIndex - 2; j++) {

                Optional<Unit> opt1 = board.getUnit(j, i);
                Optional<Unit> opt2 = board.getUnit(j+1, i);
                Optional<Unit> opt3 = board.getUnit(j+2, i);

                if (opt1.isPresent() && opt2.isPresent() && opt3.isPresent()) {
                    Unit unit1 = opt1.get();
                    Unit unit2 = opt2.get();
                    Unit unit3 = opt3.get();
                    if (unit1 instanceof AbstractMobileUnit && unit2 instanceof AbstractMobileUnit && unit3 instanceof AbstractMobileUnit) {
                        AbstractMobileUnit au1 = (AbstractMobileUnit) opt1.get();
                        AbstractMobileUnit au2 = (AbstractMobileUnit) opt2.get();
                        AbstractMobileUnit au3 = (AbstractMobileUnit) opt3.get();
                        if (au1.equals(au2) && au1.equals(au3) && !au1.isBigUnit && !au2.isBigUnit && !au3.isBigUnit) {
                            System.out.println(au1);
                            System.out.println(au2);
                            System.out.println(au3);

                            board.removeUnit(j,i);
                            board.removeUnit(j+1, i);
                            board.removeUnit(j+2, i);

                            if (au1 instanceof Butterfly){
                                Butterfly bigButterfly = new Butterfly(au1.getColor());
                                bigButterfly.isBigUnit = true;
                                bigButterfly.setHealth(au1.getHealth()*3);
                                bigButterfly.setAttackCountdown(2);
                                board.addUnit(j,i,bigButterfly);
                                board.addUnit(j+1,i,bigButterfly);
                                board.addUnit(j+2,i,bigButterfly);
                            }

                            if (au1 instanceof Fairy){
                                Fairy bigFairy = new Fairy(au1.getColor());
                                bigFairy.isBigUnit = true;

                                bigFairy.setHealth(au1.getHealth()*3);
                                bigFairy.setAttackCountdown(1);
                                board.addUnit(j,i,bigFairy);
                                board.addUnit(j+1,i,bigFairy);
                                board.addUnit(j+2,i,bigFairy);

                            }

                            if (au1 instanceof Unicorn){
                                Unicorn bigUnicorn = new Unicorn(au1.getColor());
                                bigUnicorn.isBigUnit = true;

                                bigUnicorn.setHealth(au1.getHealth()*3);
                                bigUnicorn.setAttackCountdown(0);
                                board.addUnit(j,i,bigUnicorn);
                                board.addUnit(j+1,i,bigUnicorn);
                                board.addUnit(j+2,i,bigUnicorn);

                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i <= maxRowIndex; i++) {
            for (int j = 0; j <= maxColumnIndex - 2; j++) {

                Optional<Unit> opt1 = board.getUnit(i, j);
                Optional<Unit> opt2 = board.getUnit(i, j + 1);
                Optional<Unit> opt3 = board.getUnit(i, j + 2);

                if (opt1.isPresent() && opt2.isPresent() && opt3.isPresent()) {
                    Unit unit1 = opt1.get();
                    Unit unit2 = opt2.get();
                    Unit unit3 = opt3.get();
                    if (unit1 instanceof AbstractMobileUnit && unit2 instanceof AbstractMobileUnit && unit3 instanceof AbstractMobileUnit) {
                        AbstractMobileUnit au1 = (AbstractMobileUnit) opt1.get();
                        AbstractMobileUnit au2 = (AbstractMobileUnit) opt2.get();
                        AbstractMobileUnit au3 = (AbstractMobileUnit) opt3.get();
                        if (au1.equals(au2) && au1.equals(au3) && !au1.isBigUnit && !au2.isBigUnit && !au3.isBigUnit) {
                            System.out.println(au1);
                            System.out.println(au2);
                            System.out.println(au3);
                            board.removeUnit(i, j);
                            board.removeUnit(i, j+1);
                            board.removeUnit(i, j+2);

                            if (au1 instanceof Butterfly){
                                Butterfly bigButterfly = new Butterfly(au1.getColor());
                                bigButterfly.isBigUnit = true;
                                bigButterfly.setHealth(au1.getHealth()*3);
                                bigButterfly.setAttackCountdown(2);
                                board.addUnit(i,j, bigButterfly);
                                board.addUnit(i,j+1,bigButterfly);
                                board.addUnit(i,j+2,bigButterfly);
                            }

                            if (au1 instanceof Fairy){
                                Fairy bigFairy = new Fairy(au1.getColor());
                                bigFairy.isBigUnit = true;

                                bigFairy.setHealth(au1.getHealth()*3);
                                bigFairy.setAttackCountdown(1);
                                board.addUnit(i,j, bigFairy);
                                board.addUnit(i,j+1,bigFairy);
                                board.addUnit(i,j+2,bigFairy);

                            }

                            if (au1 instanceof Unicorn){
                                Unicorn bigUnicorn = new Unicorn(au1.getColor());
                                bigUnicorn.isBigUnit = true;

                                bigUnicorn.setHealth(au1.getHealth()*3);
                                bigUnicorn.setAttackCountdown(0);
                                board.addUnit(i,j, bigUnicorn);
                                board.addUnit(i,j+1,bigUnicorn);
                                board.addUnit(i,j+2,bigUnicorn);

                            }
                        }
                    }
                }
            }
        }
    }
}
