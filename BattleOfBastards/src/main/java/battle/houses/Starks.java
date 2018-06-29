package battle.houses;

import battle.resources.Cash;
import battle.services.Allies;
import battle.services.IronBank;
import battle.resources.Soldiers;

import javax.inject.Inject;

public class Starks implements House {

  private Allies allies;
  private IronBank bank;

  @Inject
  public Starks(Allies allies, IronBank bank) {
    this.allies = allies;
    this.bank = bank;
  }

  @Override
  public void prepareForWar() {
    Cash cash = bank.fund();
    Soldiers soldiers = allies.march(cash);
    if (soldiers != null) {
      System.out.println(this.getClass().getSimpleName() + " are prepared for war");
    } else {
      System.out.println(this.getClass().getSimpleName() + " are NOT prepared for war");
    }
  }
}
