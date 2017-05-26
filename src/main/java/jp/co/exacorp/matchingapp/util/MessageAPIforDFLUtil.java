package jp.co.exacorp.matchingapp.util;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import jp.co.exacorp.matchingapp.logic.MatchMaker;

public class MessageAPIforDFLUtil {

	@EJB
	public static MatchMaker mm;

	public static JsonArrayBuilder giveColumnJab(
			List<Map<String, String>> resultList) {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for (Map<String, String> result : resultList) {
			JsonObjectBuilder job = Json.createObjectBuilder();
			job.add("thumbnailImageUrl", result.get("img"));
			job.add("title", result.get("name"));
			job.add("text",
					result.get("type") + "\nきみとのそっくり度：" + result.get("sim")
							+ "％");
			JsonArrayBuilder actJab = Json.createArrayBuilder();
			actJab.add(MessageAPIUtil.makeURI4Action("詳細を見る",
					result.get("info")));
			actJab.add(MessageAPIUtil.makeURI4Action("相談する", result.get("bff")));
			job.add("actions", actJab);
			jab.add(job);
		}
		return jab;
	}

	public static JsonObjectBuilder giveCarouselRecommend(
			List<Map<String, String>> resultList) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("type", "template");
		job.add("altText", "君にピッタリのビジネスプランアドバイザーはこの人たちだよ♪\n" + "候補を３人ご案内しています。");
		job.add("template", giveColumnsRecommend(resultList));
		return job;
	}

	public static JsonObjectBuilder giveColumnsRecommend(
			List<Map<String, String>> resultList) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("type", "carousel");
		job.add("columns", giveColumnJab(resultList));
		return job;
	}

	/**
	 * 画面の回答結果からPersonality Insights APIに送るクエリを生成する。
	 *
	 * @param yesMen
	 *            YESと答えた質問
	 * @param bog
	 *            最後の質問
	 *
	 * @return Personality Insights APIに送るクエリ
	 *
	 * */
	public static String generateQuery(List<Integer> yesMen, String bog) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < Constants.NUM_OF_QUESTION; i++) {
			if (yesMen.contains(i + 1)) {
				sb.append(Constants.YES_QUERIES[i]);
			} else {
				sb.append(Constants.NO_QUERIES[i]);
			}
		}
		sb.append(bog);
		return sb.toString();
	}

	public static JsonObjectBuilder giveAction4Question(String mes,
			String prefix) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add("type", "message");
		actJob.add("label", mes);
		if (prefix.isEmpty()) {
			actJob.add("text", mes);
		} else {
			actJob.add("text", prefix + ":" + mes);
		}
		return actJob;
	}

	public static JsonObjectBuilder giveQuestion(String text, String yesMes,
			String noMes, String prefix) {

		JsonObjectBuilder job = Json.createObjectBuilder();
		JsonObjectBuilder tempJob = Json.createObjectBuilder();
		JsonArrayBuilder actJab = Json.createArrayBuilder();

		job.add("type", "template");
		job.add("altText", text);

		actJab.add(giveAction4Question(yesMes, prefix));
		actJab.add(giveAction4Question(noMes, prefix));

		tempJob.add("type", "confirm");
		tempJob.add("text", text);
		tempJob.add("actions", actJab);
		job.add("template", tempJob);

		return job;
	}

// 追加 START
//	public static JsonObjectBuilder giveMes1Actions(String label, String text) {
	//		JsonObjectBuilder actJob = Json.createObjectBuilder();
	//		actJob.add(LINEConstants.TYPE, LINEConstants.MESSAGE);
	//		actJob.add(LINEConstants.LABEL, label);
	//		actJob.add(LINEConstants.TEXT, text);
	//		return actJob;
	//	}
// 追加 END
	public static JsonObjectBuilder giveMes4Actions(String label, String text) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add(LINEConstants.TYPE, LINEConstants.MESSAGE);
		actJob.add(LINEConstants.LABEL, label);
		actJob.add(LINEConstants.TEXT, text);
		return actJob;
	}

}
