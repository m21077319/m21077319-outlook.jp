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
public class DFLItemPersonalityInsights implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Map<String, Double>> allIdPiMap = new TreeMap<String, Map<String, Double>>();

	@PostConstruct
	public void init() {
		Map<String, Double> piMap1 = new TreeMap<String, Double>();
		Map<String, Double> piMap2 = new TreeMap<String, Double>();
		Map<String, Double> piMap3 = new TreeMap<String, Double>();

		allIdPiMap.put("001", piMap1);
		allIdPiMap.put("002", piMap2);
		allIdPiMap.put("003", piMap3);

		piMap1.put("Openness", 0.6);
		piMap1.put("Conscientiousness", 0.7);
		piMap1.put("Extraversion", 1.0);
		piMap1.put("Agreeableness", 1.0);
		piMap1.put("Neuroticism", 0.65);

		piMap2.put("Openness", 1.0);
		piMap2.put("Conscientiousness", 0.7);
		piMap2.put("Extraversion", 0.3);
		piMap2.put("Agreeableness", 0.5);
		piMap2.put("Neuroticism", 1.0);

		piMap3.put("Openness", 0.4);
		piMap3.put("Conscientiousness", 0.7);
		piMap3.put("Extraversion", 0.7);
		piMap3.put("Agreeableness", 1.0);
		piMap3.put("Neuroticism", 0.3);
	}

	public DFLItemPersonalityInsights() {

	}

	public Map<String, Double> getDFLItemPIMap(String userId) {
		return allIdPiMap.get(userId);
	}

	public Map<String, Map<String, Double>> getAllItemPI() {
		return allIdPiMap;
	}

	public Set<String> getKeySet() {
		return allIdPiMap.keySet();
	}
}
