package it.unibz.inf.pp.clash.model.snapshot.units.impl;

import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;

public class Unicorn extends AbstractMobileUnit implements MobileUnit {

    public Unicorn(UnitColor color) {
        super(3, color);
    }

    public Unicorn createBigVersion() {
        Unicorn big = new Unicorn(this.getColor());
        big.isBigUnit = true;
        big.setHealth(this.getHealth() * 3);
        big.setAttackCountdown(0);
        return big;
    }


}
