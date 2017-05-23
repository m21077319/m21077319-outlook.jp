package jp.co.exacorp.matchingapp.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import jp.co.exacorp.matchingapp.beans.DFLItemInfo;
import jp.co.exacorp.matchingapp.beans.DFLItemPersonalityInsights;
import jp.co.exacorp.matchingapp.beans.MLPAInfo;
import jp.co.exacorp.matchingapp.logic.api.PersonalityInsightsContact;
import jp.co.exacorp.matchingapp.util.Constants;

/**
 * 登録済みのマイライフプランアドバイザーと相性診断を行うクラス
 *
 * @author Shota Suzuki
 * */
@Stateless
public class MatchMaker {

	@EJB
	private DFLItemPersonalityInsights dflPi;

	@EJB
	private DFLItemInfo dfli;

	@EJB
	PersonalityInsightsContact pic;

	@EJB
	SpiderMan sm;

	@EJB
	MLPAInfo info;

	/**
	 * 登録済みのマイライフプランアドバイザーと相性診断を行う。
	 *
	 * @param userId
	 *            ユーザID
	 * @param yesMen
	 *            YESと答えた質問
	 * @param bog
	 *            最後の質問
	 *
	 * */
	public List<Map<String, String>> actMatchMaker(String userId, String query,
			String imgDir) {

		Map<String, Double> userPi = pic.getPersonalityInsight(query);

		Map<String, Integer> mmResult = new TreeMap<String, Integer>();

		for (String dflId : dflPi.getKeySet()) {
			mmResult.put(dflId,
					calcSimilarity(userPi, dflPi.getDFLItemPIMap(dflId)));
		}

		List<String> sortedList = getSortedListOrderBySimilality(mmResult);
		String maxId = sortedList.get(0);
		String midId = sortedList.get((sortedList.size() / 2) - 1);
		String minId = sortedList.get(sortedList.size() - 1);

		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

		try {

			Map<String, String> maxInfo = info.getMLPAInfo(maxId);
			String maxImg = sm.createThumbnail(userPi,
					dflPi.getDFLItemPIMap(maxId), userId, maxInfo.get("rome"),
					"max", maxInfo.get("img"), imgDir);
			resultList.add(makeResultMap(maxInfo, maxImg,
					Constants.TYPE_RESULT_MAX, mmResult.get(maxId)));

			Map<String, String> midInfo = info.getMLPAInfo(midId);
			String midImg = sm.createThumbnail(userPi,
					dflPi.getDFLItemPIMap(midId), userId, midInfo.get("rome"),
					"mid", midInfo.get("img"), imgDir);
			resultList.add(makeResultMap(midInfo, midImg,
					Constants.TYPE_RESULT_MID, mmResult.get(midId)));

			Map<String, String> minInfo = info.getMLPAInfo(minId);
			String minImg = sm.createThumbnail(userPi,
					dflPi.getDFLItemPIMap(minId), userId, minInfo.get("rome"),
					"min", minInfo.get("img"), imgDir);
			resultList.add(makeResultMap(minInfo, minImg,
					Constants.TYPE_RESULT_MIN, mmResult.get(minId)));
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	private Map<String, String> makeResultMap(Map<String, String> info,
			String imgFile, String type, int sim) {
		Map<String, String> resultMap = new TreeMap<String, String>();
		resultMap.put("name", info.get("name"));
		resultMap.put("img", Constants.IMG_URL + imgFile);
		resultMap.put("type", type);
		resultMap.put("info", info.get("info"));
		resultMap.put("bff", info.get("bff"));
		resultMap.put("sim", String.valueOf(sim));
		return resultMap;
	}

	/**
	 * 質問者とマイライフプランアドバイザーの性格診断結果から類似度を計算する。
	 *
	 * @param userPi
	 *            質問者の性格診断結果
	 * @param dflPi
	 *            マイライフプランアドバイザーの性格診断結果
	 *
	 * @param 類似度
	 *
	 * */
	private int calcSimilarity(Map<String, Double> userPi,
			Map<String, Double> dflPi) {
		double quadrance = 0;
		double maxQ = 0;
		for (String key : userPi.keySet()) {
			if (dflPi.containsKey(key)) {
				quadrance += Math.pow(userPi.get(key) - dflPi.get(key), 2);
				maxQ += 1;
			}
		}
		double sim = ((double) 1.0 - Math.sqrt(quadrance) / Math.sqrt(maxQ))
				* (double) 100.0;
		return (int) sim;
	}

	/**
	 * @param userPi
	 *            ユーザーのPI結果
	 * @return 商品情報のMap
	 */
	public Map<String, String> makeMatchingDFLItem(Map<String, Double> userPi) {
		Map<String, Map<String, Double>> allItemPI = dflPi.getAllItemPI();
		String id = "";
		int max = 0;

		for (Map.Entry<String, Map<String, Double>> piMap : allItemPI
				.entrySet()) {
			int si = calcSimilarity(userPi, piMap.getValue());
			System.out
					.println(dfli.getDFLItemInfo(piMap.getKey()) + " : " + si);
			if (max <= si) {
				id = piMap.getKey();
			}
		}

		return dfli.getDFLItemInfo(id);
	}

	/**
	 * 類似度の高い順にソートしたIDのリストを返却する。
	 *
	 * @param mmResult
	 *            相性診断結果
	 *
	 * @return 類似度の高い順にソートしたIDのリスト
	 *
	 * */
	private List<String> getSortedListOrderBySimilality(
			Map<String, Integer> mmResult) {
		List<String> sortedList = new ArrayList<String>();
		for (String mlpaId : mmResult.keySet()) {
			for (String mmrId : sortedList) {
				if (mmResult.get(mlpaId) > mmResult.get(mmrId)) {
					sortedList.add(sortedList.indexOf(mmrId), mlpaId);
					break;
				}
			}
			if (!sortedList.contains(mlpaId)) {
				sortedList.add(mlpaId);
			}
		}
		return sortedList;
	}

}
