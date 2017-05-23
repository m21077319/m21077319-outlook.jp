package jp.co.exacorp.matchingapp.logic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import jp.co.exacorp.matchingapp.beans.QueryMapBean;
import jp.co.exacorp.matchingapp.logic.api.ContentContact;
import jp.co.exacorp.matchingapp.logic.api.FortuneContact;
import jp.co.exacorp.matchingapp.logic.api.HtmlGetter;
import jp.co.exacorp.matchingapp.logic.api.NLCContact;
import jp.co.exacorp.matchingapp.logic.api.TwitterContact;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Stateless
public class FortuneTelling {

	@EJB
	QueryMapBean qmb;

	@EJB
	MatchMaker mm;

	@EJB
	NLCContact nlcc;

	@EJB
	TwitterContact tc;

	@EJB
	FortuneContact fc;

	@EJB
	ContentContact cc;

	@EJB
	HtmlGetter hg;

	public String actAsLight(Map<String, String> eventMap, String imgDir) {

		String type = eventMap.get("messageType");
		String id = eventMap.get("id");
		String replyToken = eventMap.get("replyToken");
		JsonArrayBuilder mesJab = Json.createArrayBuilder();

		// メッセージタイプ：ボイスメッセージ
		// if (type.equals("image")) {
		// mesJab.add(giveSimpleJob("画像"));
		//
		// // 入力された画像バイナリ取得
		// byte[] imgByte = cc.getImage(id);
		//
		// // 一旦ファイルに書き出してみる
		// try {
		// ByteArrayInputStream input = new ByteArrayInputStream(imgByte);
		// BufferedOutputStream output = new BufferedOutputStream(new
		// FileOutputStream(
		// Constants.IMG_DIR_PATH + id + ".jpg"));
		// System.out.println(Constants.IMG_DIR_PATH + id + ".jpg");
		//
		// byte buf[] = new byte[1024];
		// int len;
		// while ((len = input.read(buf)) != -1) {
		// output.write(buf, 0, len);
		// }
		//
		// output.flush();
		// output.close();
		// input.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// System.out.println("ファイル書き出し時エラー");
		// }
		// // 画像object送信
		// mesJab.add(giveImgJob(Constants.IMG_URL + id + ".jpg",
		// Constants.IMG_URL + id + ".jpg"));
		//
		// mesJab.add(giveSimpleJob("ごめんね、メッセージ以外を送られても何もできないんだ・・・"));
		//
		// // メッセージタイプ：ボイス・テキスト以外
		// } else
		if (!type.equals("text")) {
			mesJab.add(giveSimpleJob("そんなことより、占いとかしたいなぁ・・・"));

			// メッセージタイプ：テキスト
		} else {
			String text = eventMap.get("text");
			String userId = eventMap.get("userId");
			Map<String, String> qMap = qmb.getQueryMap(userId);
			String state = qMap.get("state");

			if (text.equals("今日の運勢")) {
				qmb.initMap(userId);

				// ステータスに状態を保持
				qMap.put("state", "Start");
				mesJab.add(giveSimpleJob("今日の運勢を占うよ！"));
				mesJab.add(giveSimpleJob("君は何座？"));
			} else {
				int rankApi;
				int topRank = 0;
				String topSite = null;
				String topMes = null;
				String luckey = null;
				String topUrl = null;

				switch (state) {
				case "Start":
					String seiza = null;
					int seizaNum = 0;
					String seizaUrl = null;

					if (text.indexOf("おひつじ") != -1 || text.indexOf("牡羊") != -1) {
						seizaNum = 0;
						seiza = "おひつじ";
						seizaUrl = "aries";
					} else if (text.indexOf("おうし") != -1
							|| text.indexOf("牡牛") != -1) {
						seizaNum = 1;
						seiza = "おうし";
						seizaUrl = "taurus";
					} else if (text.indexOf("ふたご") != -1
							|| text.indexOf("双子") != -1) {
						seizaNum = 2;
						seiza = "ふたご";
						seizaUrl = "gemini";
					} else if (text.indexOf("かに") != -1
							|| text.indexOf("蟹") != -1) {
						seizaNum = 3;
						seiza = "かに";
						seizaUrl = "cancer";
					} else if (text.indexOf("しし") != -1
							|| text.indexOf("獅子") != -1) {
						seizaNum = 4;
						seiza = "しし";
						seizaUrl = "leo";
					} else if (text.indexOf("おとめ") != -1
							|| text.indexOf("乙女") != -1) {
						seizaNum = 5;
						seiza = "おとめ";
						seizaUrl = "virgo";
					} else if (text.indexOf("てんびん") != -1
							|| text.indexOf("天秤") != -1) {
						seizaNum = 6;
						seiza = "てんびん";
						seizaUrl = "libra";
					} else if (text.indexOf("さそり") != -1
							|| text.indexOf("蠍") != -1) {
						seizaNum = 7;
						seiza = "さそり";
						seizaUrl = "scorpio";
					} else if (text.indexOf("いて") != -1
							|| text.indexOf("射手") != -1) {
						seizaNum = 8;
						seiza = "いて";
						seizaUrl = "sagittarius";
					} else if (text.indexOf("やぎ") != -1
							|| text.indexOf("山羊") != -1) {
						seizaNum = 9;
						seiza = "やぎ";
						seizaUrl = "capricorn";
					} else if (text.indexOf("みずがめ") != -1
							|| text.indexOf("水瓶") != -1) {
						seizaNum = 10;
						seiza = "みずがめ";
						seizaUrl = "aquarius";
					} else if (text.indexOf("うお") != -1
							|| text.indexOf("魚") != -1) {
						seizaNum = 11;
						seiza = "うお";
						seizaUrl = "pisces";
					}

					// Seizaがnullなら再入力を求める(1回まで)
					if (seiza == null) {
						// counterに値が入っていたらもう聞かない
						if (qMap.containsKey("flagKikikaeshi")) {
							mesJab.add(giveSimpleJob("やっぱり分からなかったからもう一回最初からやってくれる？\nごめんね；；"));

							// ステータス削除
							qmb.removeMapEntry(userId);
							qMap.remove("flagKikikaeshi");
							break;
						}
						mesJab.add(giveSimpleJob("ごめん分からなかった！\nもう一回教えてくれる？"));
						qMap.put("flagKikikaeshi", "true");
						break;
					}

					mesJab.add(giveSimpleJob(seiza + "座なんだね！\n" + seiza
							+ "座の人の今日の運勢はこちら！"));

					// 今日の日付取得
					Date today = new Date(System.currentTimeMillis() + 1000
							* 60 * 60 * 9);
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd");
					String todayForm = dateFormat.format(today);

					// 占いAPI呼び出し
					String fortuneJsonString = fc.getFortune(todayForm);

					// Jsonからデータ取得
					JsonObject jsonObject = new Gson().fromJson(
							fortuneJsonString, JsonObject.class);

					// API順位取得
					rankApi = jsonObject.get("horoscope").getAsJsonObject()
							.get(todayForm).getAsJsonArray().get(seizaNum)
							.getAsJsonObject().get("rank").getAsInt();

					// yahooHTML取得
					String yahooHtmlString = null;
					try {
						System.out
								.println("Connect to : https://fortune.yahoo.co.jp/12astro/"
										+ seizaUrl);
						yahooHtmlString = hg.getHtmlString(
								"https://fortune.yahoo.co.jp/12astro/"
										+ seizaUrl, "<html>", "summary=\"恋愛相性\">");
						System.out.println("yahooHtmlStringの長さ："
								+ yahooHtmlString.length());
					} catch (IOException e) {
						e.printStackTrace();
					}

					// yahoo!HTMLから情報だけ抜き出し
					int indexYahoo = yahooHtmlString.indexOf("</strong></td>");
					System.out.println("indexYahooの位置：" + indexYahoo);

					// yahoo順位取得
					int rankYahoo = Integer.parseInt(yahooHtmlString.substring(
							indexYahoo - 10, indexYahoo).replaceAll("[^0-9]",
							""));

					// gooのhtml取得
					String gooHtmlString = null;
					try {
						gooHtmlString = hg.getHtmlString(
								"http://fortune.goo.ne.jp/sp/destiny/"
										+ seizaUrl + ".html", "<h5>本日の運勢ランキング",
								"<h5>学校・習い事</h5>");
					} catch (IOException e) {
						e.printStackTrace();
					}

					// goo順位取得
					int indexGoo = gooHtmlString.indexOf("本日の運勢ランキング");
					int rankGoo = Integer.parseInt(gooHtmlString.substring(
							indexGoo, indexGoo + 30).replaceAll("[^0-9]", ""));

					// ここで取得してきた順位のうちランキングトップのものを判別
					// 以降の処理をトップのもののみ行うようにする
					// くそだせえ書き方
					// サイトが追加されるたびに処理を追加しなきゃだめ
					if (rankYahoo <= rankApi && rankYahoo <= rankGoo) {
						topSite = "yahoo";
						topRank = rankYahoo;
						topUrl = "https://fortune.yahoo.co.jp/12astro/"
								+ seizaUrl;
					} else if (rankGoo <= rankYahoo && rankGoo <= rankApi) {
						topSite = "goo";
						topRank = rankGoo;
						topUrl = "http://fortune.goo.ne.jp/sp/destiny/"
								+ seizaUrl + ".html";
					} else {
						topRank = rankApi;
						topSite = "API";
					}

					// switch文で分岐
					switch (topSite) {
					case "API":
						// APIメッセージ取得
						topMes = jsonObject.get("horoscope").getAsJsonObject()
								.get(todayForm).getAsJsonArray().get(seizaNum)
								.getAsJsonObject().get("content").getAsString();

						// APIラッキーアイテム取得
						luckey = jsonObject.get("horoscope").getAsJsonObject()
								.get(todayForm).getAsJsonArray().get(seizaNum)
								.getAsJsonObject().get("item").getAsString();
						break;
					case "yahoo":
						// yahooメッセージ取得
						indexYahoo = yahooHtmlString
								.indexOf("<meta property=\"og:description\" content=");
						int endMesIndex = yahooHtmlString
								.indexOf("<meta property=\"og:site_name\" content=");
						topMes = yahooHtmlString.substring(indexYahoo + 41,
								endMesIndex - 2);

						// yahoo開運のおまじない取得
						indexYahoo = yahooHtmlString
								.indexOf("<dt><img src=\"https://s.yimg.jp/images/fortune/images/12astro/yftn12a_md48_t01.gif\" alt=\"開運おまじない\"></dt>");
						endMesIndex = yahooHtmlString
								.lastIndexOf("<!-- /yftn12a-md48 -->");
						luckey = yahooHtmlString
								.substring(
										indexYahoo
												+ "<dt><img src=\"https://s.yimg.jp/images/fortune/images/12astro/yftn12a_md48_t01.gif\" alt=\"開運おまじない\"></dt>"
														.length() + 8,
										endMesIndex - 38);
						break;

					case "goo":
						// gooメッセージ取得
						indexGoo = gooHtmlString.indexOf("<!--総合運-->");
						int endGooIndex = gooHtmlString.indexOf("<!--/総合運-->");
						String temp = gooHtmlString.substring(indexGoo,
								endGooIndex);
						indexGoo = temp.indexOf("<h6>");
						endGooIndex = temp.indexOf("</h6>");

						topMes = temp.substring(indexGoo + 4, endGooIndex);

						// goo 現在の行動アドバイス取得
						indexGoo = gooHtmlString.indexOf("<h5>プライベート</h5>");
						endGooIndex = gooHtmlString
								.lastIndexOf("class=\"study\">");
						luckey = gooHtmlString.substring(indexGoo + 18,
								endGooIndex - 13);
						break;
					}

					mesJab.add(giveSimpleJob("今日の順位は" + topRank + "位！\n\n"
							+ topMes));

					// APIならラッキーアイテム
					if (topSite.equals("API")) {
						mesJab.add(giveSimpleJob("今日のラッキーアイテムは" + luckey + "！"));
						mesJab.add(giveSimpleJob("powerd by JugemKey\nhttp://jugemkey.jp/api/\n【PR】原宿占い館 塔里木\nhttp://www.tarim.co.jp/"));

						// yahooなら開運のおまじない
					} else if (topSite.equals("yahoo")) {
						mesJab.add(giveSimpleJob("～開運のおまじない～\n" + luckey));
						mesJab.add(giveSimpleJob("Yahoo!占い\n" + topUrl));

						// gooなら行動アドバイスを出力
					} else if (topSite.equals("goo")) {
						mesJab.add(giveSimpleJob("～現在の行動アドバイス～\n" + luckey));
						mesJab.add(giveSimpleJob("goo占い\n" + topUrl));
					}

					mesJab.add(giveTemplateJob("続けて占う？", "占う", "もういいや", ""));
					qMap.put("state", "continue");

					break;
				case "continue":
					if (text.equals("占う")) {
						mesJab.add(giveSimpleJob("オッケー！\n次は何座を占う？"));
						qMap.put("state", "Start");
					} else if (text.equals("もういいや")) {
						mesJab.add(giveSimpleJob("また占いたくなったらいつでも呼んでね！\n良い一日を！"));
						// ステータス削除
						qmb.removeMapEntry(userId);
					}
					break;
				default:
					// Randomクラスのインスタンス化
					Random rnd = new Random();
					int ran = rnd.nextInt(5);

					switch (ran) {
					case 0:
						mesJab.add(giveSimpleJob("そういえば僕占いとかできるんだよね！下のボタンから占えるよ！"));
						break;
					case 1:
						mesJab.add(giveSimpleJob("占いとか興味ない？下のボタン押すと占えるんだけどどう？"));
						break;
					case 2:
						mesJab.add(giveSimpleJob("占いとかしてみない？下のボタン押してみない？"));
						break;
					case 3:
						mesJab.add(giveSimpleJob("全然関係ないんだけど12星座占いとか得意だよ！下のボタンから占えるんだよ！"));
						break;
					case 4:
						mesJab.add(giveSimpleJob("占いとかしたい気分だな～下のボタンを押してくれたら占えるんだけどな～"));
						break;
					}
					break;
				}
			}
		}
		return makeRequestJson(replyToken, mesJab);
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

	private JsonObjectBuilder giveTemplateJob(String text, String yesMes,
			String noMes, String prefix) {

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

	private JsonObjectBuilder giveTemplateJobForMaki(
			List<Map<String, String>> resultList) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("type", "carousel");
		job.add("columns", giveColumnJab(resultList));
		return job;
	}

	private JsonArrayBuilder giveColumnJab(List<Map<String, String>> resultList) {
		JsonArrayBuilder jab = Json.createArrayBuilder();
		for (Map<String, String> result : resultList) {
			JsonObjectBuilder job = Json.createObjectBuilder();
			job.add("thumbnailImageUrl", result.get("img"));
			job.add("title", result.get("name"));
			job.add("text",
					result.get("type") + "\nきみとのそっくり度：" + result.get("sim")
							+ "％");
			JsonArrayBuilder actJab = Json.createArrayBuilder();
			actJab.add(giveActUriJob("詳細を見る", result.get("info")));
			actJab.add(giveActUriJob("相談する", result.get("bff")));
			job.add("actions", actJab);
			jab.add(job);
		}
		return jab;
	}

//	public static void main(String args[]) {
//		HtmlGetter hg = new HtmlGetter();
//		try {
//			String html = hg.getHtmlString(
//					"http://fortune.yahoo.co.jp/12astro/aries", "<html>",
//					"</html>");
//			System.out.println(html);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private JsonObjectBuilder giveActUriJob(String label, String uri) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("type", "uri");
		job.add("label", label);
		job.add("uri", uri);
		return job;
	}
}
