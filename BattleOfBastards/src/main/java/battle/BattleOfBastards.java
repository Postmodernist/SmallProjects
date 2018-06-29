package battle;

import battle.di.BattleComponent;
import battle.di.DaggerBattleComponent;
import battle.di.ServicesModule;
import battle.services.Allies;
import battle.services.IronBank;
import battle.war.War;

public class BattleOfBastards {
  public static void main(String[] args) {

    BattleComponent battleComponent = DaggerBattleComponent.builder()
        .servicesModule(new ServicesModule(new IronBank(), new Allies()))
        .build();

    War war = battleComponent.getWar();
    war.prepare();
  }
}
