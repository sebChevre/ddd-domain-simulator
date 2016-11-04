package ch.sebooom.domain.stockexchange.simulator;

import ch.sebooom.domain.stockexchange.StockExchangeEntity;
import ch.sebooom.domain.stockexchange.matierespremieres.MatierePremiere;
import ch.sebooom.domain.stockexchange.prix.Prix;
import com.google.common.base.Preconditions;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class StockExchangeSimulator {

	//Entites a traiter
	private List<SimulatorItem> entities;

	//Etat du simulator
	private Boolean running = Boolean.TRUE;
	
	//max et min sleep entre chaque emission
	private static final int MIN_SLEEP_MS = 20;
	private static final int MAX_SLEEP_MS = 500;
	
	
	public StockExchangeSimulator(SimulatorItem wrapper){
		Preconditions.checkNotNull(wrapper);
		Preconditions.checkNotNull(wrapper.getEntity());
		this.entities = Arrays.asList(wrapper);

	}

	public StockExchangeSimulator(List<SimulatorItem> wrappers){
		Preconditions.checkNotNull(wrappers);
	    this.entities = new ArrayList<>(wrappers);

	}


	
	public static void main(String[] args) {
		MatierePremiere mat = new MatierePremiere("Pétrole","",Prix.from(10.99));
		MatierePremiere m2 = new MatierePremiere("Test","",Prix.from(101.10));

		SimulatorItem w = SimulatorItem.from(new MatierePremiere("Pétrole","",Prix.from(10.99)));
		SimulatorItem w1 = SimulatorItem.from(new MatierePremiere("Test","",Prix.from(101.10)));

		StockExchangeSimulator sim = new StockExchangeSimulator(Arrays.asList(w,w1));
		
		sim.start()
			.subscribe(
				next -> {System.out.println(next);},
				error -> {},
				()->{}
			);
	}

	public Observable<SimulatorItem> start () {

		List<Observable<SimulatorItem>> itemObservables = entities.stream()

			.map(itemSimulator -> {

				return Observable.<SimulatorItem>create(observer->{

					StockExchangeEntity entity = itemSimulator.getEntity();

					ExecutorService service = Executors.newSingleThreadExecutor();

						service.submit(() -> {

							while(running){

								try {

									randomSleep();

									observer.onNext(itemSimulator);

									itemSimulator.updatePrix();

								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						});
					});
				})
				.collect(toList());

		return Observable.merge(itemObservables);
	}

	private void randomSleep() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(SimulatorUtil.getRandomIntBeetween(MIN_SLEEP_MS, MAX_SLEEP_MS));
	}



	


}
