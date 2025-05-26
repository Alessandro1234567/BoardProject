package it.unibz.inf.pp.clash.model.snapshot.units.impl;

import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;

public class Fairy extends AbstractMobileUnit implements MobileUnit {


    public Fairy(UnitColor color) {
        super(2,  color);
    }

    public Fairy createBigVersion() {
        Fairy big = new Fairy(this.getColor());
        big.isBigUnit = true;
        big.setHealth(this.getHealth() * 3);
        big.setAttackCountdown(1);
        return big;
    }
}
