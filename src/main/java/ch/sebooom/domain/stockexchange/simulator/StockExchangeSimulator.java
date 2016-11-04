package ch.sebooom.domain.stockexchange.simulator;

import ch.sebooom.domain.simulator.StockExchangeEntity;
import ch.sebooom.domain.stockexchange.matierespremieres.impl.MatierePremieresRepositoryImpl;
import ch.sebooom.domain.stockexchange.matierespremieres.impl.MatierePremieresServiceImpl;
import ch.sebooom.domain.stockexchange.matierespremieres.model.MatierePremiere;
import ch.sebooom.domain.stockexchange.matierespremieres.service.MatierePremiereService;
import rx.Observable;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Simulateur de valeurs boursieres
 * @author sce
 *
 */
public class StockExchangeSimulator {

	//Entites a traiter
	private List<SimulatorItem> entities;

	//Etat du simulator
	private Boolean running = Boolean.TRUE;
	
	//max et min sleep entre chaque emission
	private static final int MIN_SLEEP_MS = 20;
	private static final int MAX_SLEEP_MS = 500;
	//logger
	private static final Logger logger = Logger.getLogger(StockExchangeSimulator.class.getName());
	private static FileHandler fh = null;
	private static ConsoleHandler ch = null;
	
	//service
	MatierePremiereService service;
	
	
	/**
	 * Constructeur par défaut
	 * Charge les données via les données du domaine en mémoire
	 */
	public StockExchangeSimulator(){
		
	    service = new MatierePremieresServiceImpl(new MatierePremieresRepositoryImpl());
	    this.entities = convertToSimulatorItem(service.getAllMatieresPremieres());

	}
	
	/**
	 * Constructeur permettant de passe une liste d'éléments externe
	 * @param entities la liste des éléments à traiter
	 */
	public StockExchangeSimulator(List<StockExchangeEntity> entities){
		
	    this.entities = convertToSimulatorItem(entities);
	}


	/**
	 * Convertir les éléments <code>StockExchangeEntity</code> en éléments pour le simulateur
	 * @param entities les éléments à convertir
	 * @return la liste des éléments simulateur
	 */
	private List<SimulatorItem> convertToSimulatorItem(List<StockExchangeEntity> entities){
		
		return entities.stream()
				.map(matierePremiere -> {
					return SimulatorItem.from(matierePremiere);
				})
				.collect(Collectors.toList());
	}
	
	/**
	 * Initialisation de l'application
	 */
	private static void init(){
		File file = new File("logs");
		if (!file.exists()) {
            if (file.mkdir()) {
                logger.info("Directory logs created: " + file.getAbsolutePath());
            }
        }
		
        SimpleDateFormat format = new SimpleDateFormat("_dd-MM-yyyy_HHmmss");
        try {
            fh = new FileHandler("logs/out_"
                + format.format(Calendar.getInstance().getTime()) + ".log");
            ch = new ConsoleHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fh.setFormatter(new SimulatorLogFormatter());
        ch.setFormatter(new SimulatorLogFormatter());
        logger.addHandler(ch);
        logger.addHandler(fh);
	}
	
	public static void main(String[] args) {
		
		init();
		
		StockExchangeSimulator sim = new StockExchangeSimulator();
		
		sim.start()
			.subscribe(
				next -> {logger.info(next.toString());},
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
