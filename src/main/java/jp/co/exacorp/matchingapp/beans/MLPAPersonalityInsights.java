package jp.co.exacorp.matchingapp.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class MLPAPersonalityInsights implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Map<String, Double>>allIdPiMap = new TreeMap<String, Map<String, Double>>();

	@PostConstruct
	public void init() {
		Map<String, Double> piMap1 = new TreeMap<String, Double>();
		Map<String, Double> piMap2 = new TreeMap<String, Double>();
		Map<String, Double> piMap3 = new TreeMap<String, Double>();
		Map<String, Double> piMap4 = new TreeMap<String, Double>();
		Map<String, Double> piMap5 = new TreeMap<String, Double>();
		Map<String, Double> piMap6 = new TreeMap<String, Double>();
		Map<String, Double> piMap7 = new TreeMap<String, Double>();
		Map<String, Double> piMap8 = new TreeMap<String, Double>();
		Map<String, Double> piMap9 = new TreeMap<String, Double>();

		allIdPiMap.put("001", piMap1);
		allIdPiMap.put("002", piMap2);
		allIdPiMap.put("003", piMap3);
		allIdPiMap.put("004", piMap4);
		allIdPiMap.put("005", piMap5);
		allIdPiMap.put("006", piMap6);
		allIdPiMap.put("007", piMap7);
		allIdPiMap.put("008", piMap8);
		allIdPiMap.put("009", piMap9);

		piMap1.put("Openness", 1.0);
		piMap1.put("Conscientiousness", 1.0);
		piMap1.put("Extraversion", 1.0);
		piMap1.put("Agreeableness", 1.0);
		piMap1.put("Neuroticism", 1.0);

		piMap2.put("Openness", 0.1);
		piMap2.put("Conscientiousness", 0.1);
		piMap2.put("Extraversion", 0.1);
		piMap2.put("Agreeableness", 0.1);
		piMap2.put("Neuroticism", 0.1);

		piMap3.put("Openness", 0.9);
		piMap3.put("Conscientiousness", 0.3);
		piMap3.put("Extraversion", 0.5);
		piMap3.put("Agreeableness", 0.7);
		piMap3.put("Neuroticism", 0.6);

		piMap4.put("Openness", 0.8);
		piMap4.put("Conscientiousness", 0.4);
		piMap4.put("Extraversion", 0.6);
		piMap4.put("Agreeableness", 0.8);
		piMap4.put("Neuroticism", 0.7);

		piMap5.put("Openness", 0.7);
		piMap5.put("Conscientiousness", 0.5);
		piMap5.put("Extraversion", 0.7);
		piMap5.put("Agreeableness", 0.9);
		piMap5.put("Neuroticism", 0.5);

		piMap6.put("Openness", 0.6);
		piMap6.put("Conscientiousness", 0.6);
		piMap6.put("Extraversion", 0.8);
		piMap6.put("Agreeableness", 0.3);
		piMap6.put("Neuroticism", 0.8);

		piMap7.put("Openness", 0.5);
		piMap7.put("Conscientiousness", 0.7);
		piMap7.put("Extraversion", 0.9);
		piMap7.put("Agreeableness", 0.4);
		piMap7.put("Neuroticism", 0.3);

		piMap8.put("Openness", 0.4);
		piMap8.put("Conscientiousness", 0.8);
		piMap8.put("Extraversion", 0.3);
		piMap8.put("Agreeableness", 0.5);
		piMap8.put("Neuroticism", 0.9);

		piMap9.put("Openness", 0.3);
		piMap9.put("Conscientiousness", 0.9);
		piMap9.put("Extraversion", 0.4);
		piMap9.put("Agreeableness", 0.6);
		piMap9.put("Neuroticism", 0.4);

	}

	public MLPAPersonalityInsights() {
	}

	public Map<String, Double> getPiMap (String userId) {
		return allIdPiMap.get(userId);
	}

	public Set<String> getKeySet(){
		return allIdPiMap.keySet();
	}

}
