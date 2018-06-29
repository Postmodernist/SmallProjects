package battle.di;

import battle.services.Allies;
import battle.services.IronBank;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class ServicesModule {

  private IronBank bank;
  private Allies allies;

  public ServicesModule(IronBank bank, Allies allies) {
    this.bank = bank;
    this.allies = allies;
  }

  @Provides
  @Singleton
  IronBank provideBank() {
    return bank;
  }

  @Provides
  @Singleton
  Allies provideAllies() {
    return allies;
  }
}
