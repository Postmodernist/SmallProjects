package battle.war;

import battle.houses.Boltons;
import battle.houses.Starks;

import javax.inject.Inject;

public class War {

  private Starks starks;
  private Boltons boltons;

  @Inject
  public War(Starks starks, Boltons bolton){
    this.starks = starks;
    this.boltons = bolton;
  }

  public void prepare(){
    starks.prepareForWar();
    boltons.prepareForWar();
  }
}
