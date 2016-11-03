package ch.sebooom.domain.stockexchange.simulator;

import ch.sebooom.domain.stockexchange.matierespremieres.MatierePremiere;
import ch.sebooom.domain.stockexchange.prix.Operation;
import ch.sebooom.domain.stockexchange.prix.Prix;
import ch.sebooom.domain.stockexchange.prix.Variation;
import com.google.common.base.Preconditions;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StockExchangeSimulator {

	//Entite a traiter
	private List<SimulatorItemWrapper> wrappers;

	//nbre d'occurence pour une operation
	private int nbreOperationSeries;
	//L'opération active
	private Operation operation;
	//Etat du simulator
	private Boolean running = Boolean.FALSE;
	
	//Ecart max extremes
	private static final double MIN_FACTOR = 0.5;
	private static final double MAX_FACTOR = 1.6;

	//pourcentage max de variation par rapport au prix initial
	private static final int MAX_VARIATION_FACTOR = 5;
	//Nombre d'occurence d'une opération, nbre de fois une opération
	private static final int MAX_VARIATION_SERIES = 5;
	private static final int MIN_SLEEP_MS = 20;
	private static final int MAX_SLEEP_MS = 500;
	
	
	public StockExchangeSimulator(SimulatorItemWrapper wrapper){
		Preconditions.checkNotNull(wrapper);
		Preconditions.checkNotNull(wrapper.getEntity());
		this.wrappers = Arrays.asList(wrapper);

	}

	public StockExchangeSimulator(List<SimulatorItemWrapper> wrappers){
		Preconditions.checkNotNull(wrappers);
		this.wrappers = new ArrayList<>(wrappers);

	}


	
	public static void main(String[] args) {
		MatierePremiere mat = new MatierePremiere("Pétrole","",Prix.from(10.99));

		SimulatorItemWrapper w = SimulatorItemWrapper.from(mat);
		StockExchangeSimulator sim = new StockExchangeSimulator(w);
		
		sim.start()
				//.flatMap(observable -> Observable.just(observable))
				.flatMap(observable -> Observable.just(observable))
				.subscribe(next -> System.out.println(next),
				error -> error.printStackTrace(),
				()-> System.out.println("Completeh"));
	}
	
	public Observable<Observable> start(){
		
		//init 
		nbreOperationSeries = SimulatorUtil.getRandomIntBeetween(0, MAX_VARIATION_SERIES);
		operation = SimulatorUtil.getRandomOperation();
		running = Boolean.TRUE;

		List<Observable> obs = wrappers.stream()
				.map(wrapper -> {
					return Observable.create(observer -> {


						ExecutorService service = Executors.newSingleThreadExecutor();

						service.submit(() -> {

							try {
								while(running){
									TimeUnit.MILLISECONDS.sleep(SimulatorUtil.getRandomIntBeetween(MIN_SLEEP_MS, MAX_SLEEP_MS));

									double variationPrix = variation();
									double newPrix = next(wrapper);


									wrapper.getEntity().updatePrix(newPrix);
									wrapper.updateVariation(Variation.from(operation,variationPrix));
									observer.onNext(wrapper);
								}

							} catch (InterruptedException e) {
								observer.onError(e);
							}
						});
					});
				})
				.collect(Collectors.toList());


			
		return Observable.from(obs);//.flatMap(observable -> Observable.just(observable).flatMap(obsa -> Observable.just(obsa)));
		
	} 
	
	private static double variation () {
		return SimulatorUtil.getRandomDoubleBeetween(1, MAX_VARIATION_FACTOR)/100d;
	}
	
	private double next(SimulatorItemWrapper wrapper){
		
		double newPrix = 0;
		double variation = variation();
		
	
		if(nbreOperationSeries == 0){
			nbreOperationSeries = SimulatorUtil.getRandomIntBeetween(0, MAX_VARIATION_SERIES);
		}
		
		if(checkIfEcartFromInitialValueOutBound(wrapper)){
			operation = SimulatorUtil.inverse(operation);
			
		}else{
			if(nbreOperationSeries == 0){
				operation = SimulatorUtil.getRandomOperation();
			}
		}
		
		
		double initPrix = wrapper.prixInitial().valeur().doubleValue();
		double lastPrix = wrapper.getEntity().prix().valeur().doubleValue();
		
		
		switch(operation){
			case ADD:
				newPrix = lastPrix + (initPrix * variation);
				System.out.println("ADD:");
			break;
				
			case SUBSTRACT: 
				newPrix = lastPrix - (initPrix * variation);
				System.out.println("SUBTRACT:");
			break;
			
			default: throw new IllegalArgumentException();
		}
		
		nbreOperationSeries --;
		
		
		return newPrix;
	
	}
	
	
	private static boolean checkIfEcartFromInitialValueOutBound (SimulatorItemWrapper wrapper) {
		double lastValue = wrapper.getEntity().prix().valeur().doubleValue();
		double initValue = wrapper.prixInitial().valeur().doubleValue();
		double ecartFromInitial = lastValue/initValue;
		
		return ecartFromInitial > MAX_FACTOR || ecartFromInitial < MIN_FACTOR;
	}

}
