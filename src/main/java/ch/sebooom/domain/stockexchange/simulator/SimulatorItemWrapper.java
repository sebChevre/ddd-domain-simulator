package ch.sebooom.domain.stockexchange.simulator;

import ch.sebooom.domain.stockexchange.StockExchangeEntity;
import ch.sebooom.domain.stockexchange.prix.Prix;
import ch.sebooom.domain.stockexchange.prix.Variation;
import com.google.common.base.Preconditions;

/**
 * Created by seb on 01.11.16.
 */
public class SimulatorItemWrapper {

    //Entite simule
    private StockExchangeEntity entity;
    //Taux et sens de la variation de la valeur de l'entite
    private Variation variation;
    //prix initial de l'entite
    private Prix prixInitial;

    private SimulatorItemWrapper(StockExchangeEntity entity) {
        Preconditions.checkNotNull(entity);
        this.entity = entity;
    }

    public Prix prixInitial(){
        return prixInitial;
    }

    public static SimulatorItemWrapper from(StockExchangeEntity entity){
        return new SimulatorItemWrapper(entity);
    }

    public StockExchangeEntity getEntity(){
        return entity;
    }

    public void updateVariation(Variation from) {
        this.variation = from;

    }

    @Override
    public String toString() {
        return "SimulatorItemWrapper{" +
                "entity=" + entity +
                ", variation=" + variation +
                '}';
    }
}
