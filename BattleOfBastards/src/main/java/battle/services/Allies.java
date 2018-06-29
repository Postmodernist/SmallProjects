package battle.services;

import battle.resources.Cash;
import battle.resources.Soldiers;

public class Allies {
  public Soldiers march(Cash cash) {
    return cash == null ? null : new Soldiers();
  }
}
