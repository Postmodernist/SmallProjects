package battle.di;

import battle.war.War;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ServicesModule.class)
public interface BattleComponent {
  War getWar();
}
