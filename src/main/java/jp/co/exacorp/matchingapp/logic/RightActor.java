package jp.co.exacorp.matchingapp.logic;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import jp.co.exacorp.matchingapp.beans.QueryMapBean;
import jp.co.exacorp.matchingapp.logic.api.NLCContact;
import jp.co.exacorp.matchingapp.logic.api.TwitterContact;
import jp.co.exacorp.matchingapp.util.Constants;

@Stateless
public class RightActor {

	@EJB
	QueryMapBean qmb;

	@EJB
	MatchMaker mm;

	@EJB
	NLCContact nlcc;

	@EJB
	TwitterContact tc;

	public String actAsLight(Map<String, String> eventMap, String imgDir) {

		String type = eventMap.get("messageType");
		String replyToken = eventMap.get("replyToken");
		JsonArrayBuilder mesJab = Json.createArrayBuilder();

		if (!type.equals("text")) {
			mesJab.add(giveSimpleJob("ごめんね、メッセージじゃないのを送られても何もできないんだ・・・"));
		} else {
			String text = eventMap.get("text");
			String userId = eventMap.get("userId");
			Map<String, String> qMap = qmb.getQueryMap(userId);
			String state = qMap.get("state");

			if (text.equals("技の芽ちゃん！")) {
				qmb.initMap(userId);
				qMap.put("state", "S");
				mesJab.add(giveSimpleJob("こんにちは！！\n私技の芽ちゃんです。"));
				mesJab.add(giveTemplateJob("業務で何か悩んでいることとかあるかな？","ある","ない","悩み"));
			} else {
				switch (state) {
				case "S":
					if (text.equals("悩み:ある")) {
						mesJab.add(giveSimpleJob("どんな悩みなのか聞かせてよ！できることなら力になるよ！"));
						qMap.put("state", "N");
					} else if (text.equals("悩み:ない")) {
						mesJab.add(giveSimpleJob("悩んでないのなら大丈夫だね！\n安心したよ！"));
						qmb.removeMapEntry(userId);
					}
					break;
				case "N":
					String[] nlcResults = nlcc.getNLCResult(text);
					for (String nlcResult: nlcResults) {
						mesJab.add(giveSimpleJob(nlcResult));
					}
					mesJab.add(giveTemplateJob("よかったら君にピッタリのビジネスプランアドバイザーを紹介させて"
							+ "ほしいんだけど、どうかな？","紹介して！", "今はいいや", ""));
					qMap.put("bog", text);
					qMap.put("state", "I");
					break;
				case "I":
					if (text.equals("紹介して！")) {
						mesJab.add(giveSimpleJob("そしたら今からいくつか質問をするから、それに答えてね！\n"
								+ "相性バッチリ！のビジネスプランアドバイザーを見つけるよ！"));
						mesJab.add(giveSimpleJob("何県に住んでるの？"));
						qMap.put("state", "F1");
					} else if (text.equals("今はいいや")) {
						mesJab.add(giveSimpleJob("そっかぁ・・・\nまた今度困ったことがあったら呼んでね！"));
						qmb.removeMapEntry(userId);
					}
					break;
				case "F1":
					qMap.put("bog", qMap.get("bog") + " " + text);
					mesJab.add(giveSimpleJob("お誕生日はいつ？"));
					qMap.put("state", "F2");
					break;
				case "F2":
					qMap.put("bog", qMap.get("bog") + " " + text);
					mesJab.add(giveTemplateJob("結婚してるの？","してるよ","してないよ","結婚"));
					qMap.put("state", "F3");
					break;
				case "F3":
					qMap.put("bog", qMap.get("bog") + " " + text);
					mesJab.add(giveTemplateJob("ついったーのアカウントって持ってる？","持ってるよ！","持ってないよ！",""));
					qMap.put("state", "W");
					break;
				case "W":
					if (text.equals("持ってるよ！")) {
						mesJab.add(giveTemplateJob("アカウント名が分かると、きみの今までしたツイートを使って"
								+ "性格診断できるんだけど、よかったら教えてほしいな♪","いいよ！","うーん・・・",""));
						qMap.put("state", "C");
					} else if (text.equals("持ってないよ！")) {
						startYNQuestions(qMap, mesJab);
					}
					break;
				case "C":
					if (text.equals("いいよ！")) {
						mesJab.add(giveSimpleJob("ありがとー！\n"
								+ "それじゃあ、\"@\"で始まるついったーのアカウント名を教えてね！"));
						qMap.put("state", "T");
					} else if (text.equals("うーん・・・")) {
						startYNQuestions(qMap, mesJab);
					}
					break;
				case "T":
					if (text.startsWith("@")) {
						String twiResult = tc.getRecentTweet(text);
						if (twiResult.isEmpty()) {
							mesJab.add(giveSimpleJob("うまくツイートがとれなかったよ・・・\n"
									+ "性格診断のためにもう少しだけ質問をするから答えてね！"));
							mesJab.add(giveTemplateJob(Constants.QUESTIONS[0], "はい", "いいえ", "A1"));
							qMap.put("state", "A");
						} else {
							callMatchMaker(userId, twiResult + qMap.get("bog"), imgDir, mesJab);
						}
					} else {
						mesJab.add(giveSimpleJob("\"@\"で始まるついったーのアカウント名を教えてね！"));
					}
					break;
				case "A":
					if (text.startsWith("A")) {
						String[] token = text.split(":");
						qMap.put(token[0], token[1]);
						int qNum = qmb.getNextQuestionNumber(userId);
						if (qNum == 0) {
							callMatchMaker(userId, generateQuery(qmb.getYesMen(userId, "はい"), qMap.get("bog")), imgDir, mesJab);
						} else {
							mesJab.add(giveTemplateJob(Constants.QUESTIONS[qNum - 1], "はい", "いいえ", "A" + qNum));
						}
					}
					break;
				default:
					return "";
				}
			}
		}
		return makeRequestJson(replyToken, mesJab);
	}

	private void startYNQuestions(Map<String, String> qMap, JsonArrayBuilder mesJab) {
		mesJab.add(giveSimpleJob("うん、わかったぁ！\n"
				+ "それじゃあ、もう少しだけ質問をするから、それに答えてね！"));
		mesJab.add(giveTemplateJob(Constants.QUESTIONS[0], "はい", "いいえ", "A1"));
		qMap.put("state", "A");
	}

	private void callMatchMaker (String userId, String query, String imgDir, JsonArrayBuilder mesJab) {
		List<Map<String, String>> resultList = mm.actMatchMaker(userId, query, imgDir);
		mesJab.add(giveSimpleJob("君にピッタリのビジネスプランアドバイザーはこの人たちだよ♪\n"
				+ "候補が３人いるんだけど、どのアドバイザーさんがいいかな？"));
		mesJab.add(giveSimpleJob("「相談する」をタップすると君と私と選んだアドバイザーさんの"
				+ "LINEグループを作成するよ！"));
		mesJab.add(giveMakiJob(resultList));
		qmb.removeMapEntry(userId);
	}

	private JsonObjectBuilder giveSimpleJob(String message) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("type", "text");
		job.add("text", message);
		return job;
	}

	private String makeRequestJson(String replyToken, JsonArrayBuilder mesJab) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("replyToken", replyToken);
		job.add("messages", mesJab);
		return job.build().toString();
	}

	private JsonObjectBuilder giveTemplateJob(String text, String yesMes, String noMes, String prefix) {

		JsonObjectBuilder job = Json.createObjectBuilder();
		JsonObjectBuilder tempJob = Json.createObjectBuilder();
		JsonArrayBuilder actJab = Json.createArrayBuilder();

		job.add("type", "template");
		job.add("altText", text);

		actJab.add(giveActionJob(yesMes, prefix));
		actJab.add(giveActionJob(noMes, prefix));

		tempJob.add("type", "confirm");
		tempJob.add("text", text);
		tempJob.add("actions", actJab);
		job.add("template", tempJob);

		return job;
	}

	private JsonObjectBuilder giveActionJob(String mes, String prefix) {
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

	private JsonObjectBuilder giveMakiJob(List<Map<String, String>> resultList) {
		JsonObjectBuilder job =  Json.createObjectBuilder();
		job.add("type", "template");
		job.add("altText", "君にピッタリのビジネスプランアドバイザーはこの人たちだよ♪\n"
				+ "候補を３人ご案内しています。");
		job.add("template", giveTemplateJobForMaki(resultList));
		return job;
	}

	private JsonObjectBuilder giveTemplateJobForMaki(List<Map<String, String>> resultList) {
		JsonObjectBuilder job =  Json.createObjectBuilder();
		job.add("type", "carousel");
		job.add("columns", giveColumnJab(resultList));
		return job;
	}

	private JsonArrayBuilder giveColumnJab(List<Map<String, String>> resultList) {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for (Map<String, String> result: resultList) {
			JsonObjectBuilder job =  Json.createObjectBuilder();
			job.add("thumbnailImageUrl", result.get("img"));
			job.add("title", result.get("name"));
			job.add("text", result.get("type") + "\nきみとのそっくり度：" + result.get("sim") + "％");
			JsonArrayBuilder actJab = Json.createArrayBuilder();
			actJab.add(giveActUriJob("詳細を見る", result.get("info")));
			actJab.add(giveActUriJob("相談する", result.get("bff")));
			job.add("actions", actJab);
			jab.add(job);
		}
		return jab;
	}

	private JsonObjectBuilder giveActUriJob(String label, String uri) {
		JsonObjectBuilder job =  Json.createObjectBuilder();
		job.add("type", "uri");
		job.add("label", label);
		job.add("uri", uri);
		return job;
	}

	/**
	 * 画面の回答結果からPersonality Insights APIに送るクエリを生成する。
	 *
	 * @param yesMen YESと答えた質問
	 * @param bog 最後の質問
	 *
	 * @return Personality Insights APIに送るクエリ
	 *
	 * */
	private String generateQuery(List<Integer> yesMen, String bog) {
		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < Constants.NUM_OF_QUESTION; i++) {
			if (yesMen.contains(i + 1)) {
				sb.append(Constants.YES_QUERIES[i]);
			} else {
				sb.append(Constants.NO_QUERIES[i]);
			}
		}
		sb.append(bog);
		return sb.toString();
	}

}
