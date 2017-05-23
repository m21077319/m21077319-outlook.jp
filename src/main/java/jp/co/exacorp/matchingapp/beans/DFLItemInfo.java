package jp.co.exacorp.matchingapp.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import jp.co.exacorp.matchingapp.util.Constants;
import jp.co.exacorp.matchingapp.util.DFLConstants;

@Singleton
@Startup
public class DFLItemInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String NAME = "name";
	private static final String IMG = "image";
	private static final String INFO = "info";
	private static final String CID = "ClassifierID";
	private static final String AUDIO = "audio";

	private Map<String, Map<String, String>> allIdInfoMap = new TreeMap<String, Map<String, String>>();

	@PostConstruct
	public void init() {
		Map<String, String> infoMap1 = new TreeMap<String, String>();
		Map<String, String> infoMap2 = new TreeMap<String, String>();
		Map<String, String> infoMap3 = new TreeMap<String, String>();

		allIdInfoMap.put("001", infoMap1);
		allIdInfoMap.put("002", infoMap2);
		allIdInfoMap.put("003", infoMap3);

		// 名前
		infoMap1.put(NAME, DFLConstants.PREMIRE_RECEIVE_GLB);
		infoMap2.put(NAME, DFLConstants.PREMIRE_CURRENCY_PLUS2);
		infoMap3.put(NAME, DFLConstants.PREMIRE_STORY);

		// 顔画像
		infoMap1.put(IMG, Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG);
		infoMap2.put(IMG, Constants.IMG_URL
				+ DFLConstants.PREMIRE_CURRENCY_PLUS2_IMG);
		infoMap3.put(IMG, Constants.IMG_URL + DFLConstants.PREMIRE_STORY_IMG);

		String yahoo = Constants.IMG_URL + "takeiwa.png";

		// 自己紹介HTML
		infoMap1.put(INFO, yahoo);
		infoMap2.put(INFO, yahoo);
		infoMap3.put(INFO, yahoo);

		// NLC Classifier ID
		infoMap1.put(CID, DFLConstants.PREMIER_RECIEVE_GLB_NLC_CLASSIFIER_ID);
		infoMap2.put(CID, DFLConstants.PREMIRE_CURRENCY_PLUS2_NLC_CLASSIFIER_ID);
		infoMap3.put(CID, DFLConstants.PREMIRE_STORY_NLC_CLASSIFIER_ID);

		// audio
		infoMap1.put(AUDIO, DFLConstants.PREMIRE_RECEIVE_GLB_AUDIO);
		infoMap2.put(AUDIO, DFLConstants.PREMIRE_CURRENCY_PLUS2_AUDIO);
		infoMap3.put(AUDIO, DFLConstants.PREMIRE_STORY_AUDIO);
	}

	public DFLItemInfo() {

	}

	public Map<String, String> getDFLItemInfo(String userId) {
		return allIdInfoMap.get(userId);
	}

	/**
	 * 商品名からClassifier ID を返す
	 *
	 * @param name
	 *            名前
	 * @return cid
	 */
	public String getCID(String name) {
		String cid = "";

		for (Map.Entry<String, Map<String, String>> e : getAllItemInfo()
				.entrySet()) {
			if (e.getValue().get(NAME).equals(name)) {
				cid = e.getValue().get(CID);
			}
		}

		return cid;
	}

	/**
	 * 商品名からdataMapを返す
	 *
	 * @param name
	 *            名前
	 * @return data
	 */
	public Map<String, String> getInfo(String name) {
		Map<String, String> data = null;

		for (Map.Entry<String, Map<String, String>> e : getAllItemInfo()
				.entrySet()) {
			if (e.getValue().get(NAME).equals(String.valueOf(name))) {
				data = e.getValue();
			}
		}
		return data;
	}

	public Map<String, Map<String, String>> getAllItemInfo() {
		return allIdInfoMap;
	}
}
