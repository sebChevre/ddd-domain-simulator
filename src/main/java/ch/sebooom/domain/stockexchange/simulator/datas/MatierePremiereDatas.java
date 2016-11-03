package ch.sebooom.domain.stockexchange.simulator.datas;

import ch.sebooom.domain.stockexchange.matierespremieres.MatierePremiere;
import ch.sebooom.domain.stockexchange.prix.Prix;

import java.util.ArrayList;
import java.util.List;

public class MatierePremiereDatas {
	
	public static List<MatierePremiere> datas(){
		
		List<MatierePremiere> datas = new ArrayList<>();
		datas.add(new MatierePremiere("Pétrole", "Cours pétrole", Prix.from(50.36)));
		datas.add(new MatierePremiere("Or", "Cours or", Prix.from(1262.09)));
		datas.add(new MatierePremiere("Blé", "Cours blé", Prix.from(402.80)));
		datas.add(new MatierePremiere("Platine", "Cours platine", Prix.from(936.25)));
		datas.add(new MatierePremiere("Café C", "Cours café", Prix.from(157.73)));
		datas.add(new MatierePremiere("Soja", "Cours soja", Prix.from(997.38)));
		datas.add(new MatierePremiere("Coton", "Cours coton", Prix.from(68.70)));
		datas.add(new MatierePremiere("Brebt oil", "Cours brent", Prix.from(51.34)));
		datas.add(new MatierePremiere("Gaz naturel", "Cours gaz naturel", Prix.from(50.36)));
		return datas;
	}

}
