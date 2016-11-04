package ch.sebooom.domain.stockexchange.simulator;

import ch.sebooom.domain.stockexchange.StockExchangeEntity;
import ch.sebooom.domain.stockexchange.prix.Operation;
import ch.sebooom.domain.stockexchange.prix.Prix;
import ch.sebooom.domain.stockexchange.prix.Variation;
import com.google.common.base.Preconditions;

/**
 * Created by seb on 01.11.16.
 */
public class SimulatorItem {

    //Entite simule
    private StockExchangeEntity entity;
    //Taux et sens de la tauxVariation de la valeur de l'entite
    private Variation variation;
    //prix initial de l'entite
    private Prix prixInitial;
    //nbre d'occurence pour une operation
    private int nbreOperationSeries;
    //L'opération active
    private Operation operation;
    //Nombre d'occurence d'une opération, nbre de fois une opération
    private static final int MAX_VARIATION_SERIES = 5;
    //Ecart max extremes
    private static final double MIN_FACTOR = 0.5;
    private static final double MAX_FACTOR = 1.6;
    //pourcentage max de variation par rapport au prix initial
    private static final int MAX_VARIATION_FACTOR = 5;

    private SimulatorItem(StockExchangeEntity entity) {
        Preconditions.checkNotNull(entity);
        this.entity = entity;
        this.prixInitial = entity.prix();
        operation = SimulatorUtil.getRandomOperation();
        nbreOperationSeries = SimulatorUtil.getRandomIntBeetween(0, MAX_VARIATION_SERIES);
        variation = Variation.from(operation,0d);
    }

    public Prix prixInitial(){
        return prixInitial;
    }

    public Operation operation () {
        return this.operation;

    }

    public int nbreOperationsSeries () {
        return this.nbreOperationSeries;
    }

    public void defineNbreOperationSeries (){
        this.nbreOperationSeries = SimulatorUtil.getRandomIntBeetween(0, MAX_VARIATION_SERIES);
    }

    public void inverseOperation(){
        this.operation = SimulatorUtil.inverse(operation);
    }

    public static SimulatorItem from(StockExchangeEntity entity){
        return new SimulatorItem(entity);
    }

    public StockExchangeEntity getEntity(){
        return entity;
    }



    public void updatePrix(){

        double tauxVariation = tauxVariation();

        double newPrix = nextPrix(tauxVariation);
        this.getEntity().updatePrix(newPrix);

        this.variation = Variation.from(this.operation(),tauxVariation);
    }

    private static double tauxVariation() {
        return SimulatorUtil.getRandomDoubleBeetween(1, MAX_VARIATION_FACTOR)/100d;
    }

    private double nextPrix(double tauxVariation){

        double newPrix = 0;


        //si compteur de serie à zero, réinit
        if(this.nbreOperationsSeries() == 0){
            this.defineNbreOperationSeries();
        }

        //si limite variation atteinte, redéfintion operation
        if(checkIfEcartFromInitialValueOutBound()){
            this.inverseOperation();

        }else{
            if(this.nbreOperationsSeries() == 0){
                this.defineNbreOperationSeries();
            }
        }


        double lastPrix = this.getEntity().prix().valeur().doubleValue();


        switch(this.operation()){
            case ADD:
                newPrix = lastPrix + (lastPrix * tauxVariation);
                System.out.println("ADD:");
                break;

            case SUBSTRACT:
                newPrix = lastPrix - (lastPrix * tauxVariation);
                System.out.println("SUBTRACT:");
                break;

            default: throw new IllegalArgumentException();
        }

        this.decrementOperatiom();

        return newPrix;

    }

    private boolean checkIfEcartFromInitialValueOutBound() {
        double lastValue = this.getEntity().prix().valeur().doubleValue();
        double initValue = this.prixInitial().valeur().doubleValue();
        double ecartFromInitial = lastValue/initValue;

        return ecartFromInitial > MAX_FACTOR || ecartFromInitial < MIN_FACTOR;
    }

    @Override
    public String toString() {
        return "SimulatorItemWrapper{" +
                "entity=" + entity +
                ", " + variation +
                '}';
    }

    public void decrementOperatiom() {
       this.nbreOperationSeries --;
    }
}
