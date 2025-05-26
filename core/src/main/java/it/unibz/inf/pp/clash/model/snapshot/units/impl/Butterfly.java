package it.unibz.inf.pp.clash.model.snapshot.units.impl;

import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;

public class Butterfly extends AbstractMobileUnit implements MobileUnit {

    public Butterfly(MobileUnit.UnitColor color) {
        super(5, color);
    }

    public Butterfly createBigVersion() {
        Butterfly big = new Butterfly(this.getColor());
        big.isBigUnit = true;
        big.setHealth(this.getHealth() * 3);
        big.setAttackCountdown(2);
        return big;
    }
}
