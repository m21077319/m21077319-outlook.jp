package jp.co.exacorp.matchingapp.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class QueryMapBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Map<String, String>> allIdQMap = new TreeMap<String, Map<String, String>>();

	@PostConstruct
	public void init() {
	}

	public QueryMapBean () {
	}

	public void initMap(String userId) {
		Map<String, String> qMap = null;
		if (allIdQMap.containsKey(userId)) {
			qMap = allIdQMap.get(userId);
		} else {
			qMap = new TreeMap<String, String>();
			allIdQMap.put(userId, qMap);
		}
		for (int i = 1; i <= 9; i++) {
			qMap.put("A" + i, "");
		}
		qMap.put("state", "");
		qMap.put("bog", "");
	}

	public Map<String, String> getQueryMap(String userId) {
		if (!allIdQMap.containsKey(userId)) {
			initMap(userId);
		}
		return allIdQMap.get(userId);
	}

	public int getNextQuestionNumber(String userId) {
		Map<String, String> qMap = allIdQMap.get(userId);
		for (int i = 1; i <= 9; i++) {
			if (qMap.get("A" + i).equals("")) {
				return i;
			}
		}
		return 0;
	}

	public List<Integer> getYesMen(String userId, String yesMes) {
		Map<String, String> qMap = allIdQMap.get(userId);
		List<Integer> yesMen = new ArrayList<Integer>();
		for (int i = 1; i <= 9; i++) {
			if (qMap.get("A" + i).equals(yesMes)) {
				yesMen.add(i);
			}
		}
		return yesMen;
	}

	public void removeMapEntry(String userId) {
		allIdQMap.remove(userId);
	}
}
