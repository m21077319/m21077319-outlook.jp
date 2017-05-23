package jp.co.exacorp.matchingapp.logic;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import jp.co.exacorp.matchingapp.beans.QueryMapBean;
import jp.co.exacorp.matchingapp.logic.api.NLCContact;
import jp.co.exacorp.matchingapp.logic.api.PersonalityInsightsContact;
import jp.co.exacorp.matchingapp.util.Constants;
import jp.co.exacorp.matchingapp.util.DFLConstants;
import jp.co.exacorp.matchingapp.util.LINEConstants;
import jp.co.exacorp.matchingapp.util.MessageAPIUtil;

@Stateless
public class DFLItem {
	private static final String ITEM = "item";
	private static final String NEW_ITEM = "newItem";
	private static final String QUESTION = "question";
	private static final String FINAL = "final";
	private static final String END = "もういいや";
	private static final String FINAL_NO_BUTTON = "ボタン押さない";
	private static final String NO_TEXT = "noText";
	private static final String TRUE = "true";
	private static final String OTHERS_ITEM = "他の商品をおすすめして！";
	private static final String RECOMMEND = "recommend";
	private static final String OK = "OK！";
	private static final String NEW_ITEM_SELECT = "この商品イイネ！";
	private static final String OLD_ITEM_SELECT = "やっぱりさっきの商品のこともう少し教えて";

	@EJB
	QueryMapBean qmb;
	@EJB
	NLCContact nlcc;
	@EJB
	PersonalityInsightsContact pic;

	public String teachItem(Map<String, String> eventMap, String imgDir) {
		String type = eventMap.get("messageType");
		String id = eventMap.get("id");
		String replyToken = eventMap.get("replyToken");
		JsonArrayBuilder mesJab = Json.createArrayBuilder();

		// ユーザーID取得
		String userId = eventMap.get(LINEConstants.USERID);
		// qMapにいろいろ持たせちゃう
		Map<String, String> qMap = qmb.getQueryMap(userId);

		// テキストに限定
		if (!type.equals(LINEConstants.TEXT)) {
			if (!qMap.containsKey(NO_TEXT)) {
				mesJab.add(MessageAPIUtil
						.giveMessage("申し訳ありません。\nテキストメッセージ以外へは対応しておりません。"));
				qMap.put(NO_TEXT, NO_TEXT);
			} else {
				mesJab.add(MessageAPIUtil
						.giveMessage("申し訳ありません。\n最初からやり直してください。"));
				qmb.removeMapEntry(userId);
			}
		} else {
			// テキスト以外が一度来ていたというフラグを削除
			if (qMap.containsKey(NO_TEXT)) {
				qMap.remove(NO_TEXT);
			}

			// テキスト取得
			String text = eventMap.get(LINEConstants.TEXT);
			// 状態取得
			String state = qMap.get(LINEConstants.STATE);

			// TODO
			// 開始の文言をボタン入力させて対象商品を選ばせるのか、LINEの下ボタンから対象商品をいきなり選ばせるのか決める
			// 現状とりあえず商品名が送られて来たら状態をSTARTにする（下のボタンがいいかなぁ）
			// ステータスが空なら商品選ぶ段階
			if (state.isEmpty()) {
				switch (text) {
				case DFLConstants.PREMIRE_RECEIVE_GLB:
					// ステータスに状態を保持
					qMap.put(LINEConstants.STATE, QUESTION);
					qMap.put(ITEM, DFLConstants.PREMIRE_RECEIVE_GLB);
					mesJab.add(MessageAPIUtil.giveMessage(qMap.get(ITEM)
							+ "についてならなんでも聞いてください！"));
					break;
				default:
					mesJab.add(MessageAPIUtil.giveMessage("申し訳ありません。\n「" + text
							+ "」は対象外となっております。"));
					break;
				}

				// 商品選択直後
			} else if (state.equals(QUESTION)) {
				// 他の商品を選んだ時
				if (text.equals(OTHERS_ITEM)) {
					// ステータスを更新
					qMap.put(LINEConstants.STATE, RECOMMEND);

					// 質問していいか聞くこんふぁーーーむ
					JsonArrayBuilder confirmAct = Json.createArrayBuilder();
					confirmAct
							.add(MessageAPIUtil.makeMessage4Action("OK！", OK));
					confirmAct.add(MessageAPIUtil
							.makeMessage4Action("いいよ！", OK));
					mesJab.add(MessageAPIUtil.giveConfirm("あなたのことについて少し教えて？",
							"ではあなたのことについて少し教えてほしいのですがよろしいですか？", confirmAct));

					// もういいや
				} else if (text.equals(END)) {
					mesJab.add(MessageAPIUtil
							.giveMessage("ご利用ありがとうございました。\nもう一度使用したい場合は画面下のボタンから始めてください。"));
					qmb.removeMapEntry(userId);
					// 質問の時
				} else {
					mesJab.add(MessageAPIUtil.giveMessage("以下の回答をご覧ください。"));

					// TODO
					// NLCにテキスト投げて返ってきた結果を表示させる
					String[] nlcResults = nlcc.getNLCResult(text);
					for (String nlcResult : nlcResults) {
						mesJab.add(MessageAPIUtil.giveMessage(nlcResult));
					}

					// 他の商品をおすすめされたい時用ボタン
					JsonArrayBuilder buttonAct = Json.createArrayBuilder();
					buttonAct.add(MessageAPIUtil.makeMessage4Action("他の商品",
							OTHERS_ITEM));
					buttonAct.add(MessageAPIUtil.makeMessage4Action("もういいや",
							END));
					mesJab.add(MessageAPIUtil.giveButton(
							"他の質問がある場合は再度入力してください。", Constants.BRANK,
							Constants.BRANK, "他の質問がある場合は再度入力してください。\nまた、ボタンから"
									+ qMap.get(ITEM) + "以外の商品をおすすめすることもできます。",
							buttonAct));
				}
			} else if (state.equals(RECOMMEND)) {
				// OKのとき（しかない）
				if (text.equals(OK)) {
					// TODO
					// パーソナリティインサイツッ！
					mesJab.add(MessageAPIUtil.giveMessage("まだつくってないよーん"));
					Map<String, Double> userPi = pic
							.getPersonalityInsight("ボーカルの川谷絵音（２８）が昨年、当時未成年だった交際中のタレント・ほのかりん（２０）と飲酒デートした問題で活動を休止していたロックバンド「ゲスの極み乙女。」が１０日、Ｚｅｐｐ　Ｔｏｋｙｏで復帰ライブを行った。昨年１２月３日に前回のライブを開催した場所で、約５カ月ぶりに再始動した。(デイリースポーツ)");

					for (Map.Entry<String, Double> e : userPi.entrySet()) {
						System.out.println(e.getKey() + " : " + e.getValue());
					}

					qmb.removeMapEntry(userId);
				}

				if (text.equals("おすすめ商品")) {
					// TODO
					// レコメンドする商品が同じだった時の処理

					// ステータスを更新
					qMap.put(LINEConstants.STATE, FINAL);

					// TODO
					// 急にしゃべるよ（ってやりたいけど一旦普通のテキスト）
					mesJab.add(MessageAPIUtil.giveMessage("以下の商品はいかがでしょうか？"));
					// レコメンド商品
					JsonArrayBuilder buttonAct = Json.createArrayBuilder();
					buttonAct.add(MessageAPIUtil.makeMessage4Action(
							NEW_ITEM_SELECT, NEW_ITEM_SELECT));
					buttonAct.add(MessageAPIUtil.makeMessage4Action(
							OLD_ITEM_SELECT, OLD_ITEM_SELECT));
					// TODO
					// 新しい商品の名前とかを動的にもってこれるように
					mesJab.add(MessageAPIUtil.giveButton("こちらの商品はいかがですか？",
							Constants.BRANK, "商品タイトル", "この商品の説明", buttonAct));
				}

			} else if (state.equals(FINAL)) {
				switch (text) {
				case NEW_ITEM_SELECT:
					// ITEMを更新する
					qMap.put(ITEM, qMap.get(NEW_ITEM));
					qMap.remove(NEW_ITEM);
					mesJab.add("実は"
							+ MessageAPIUtil.giveMessage(qMap.get(ITEM)
									+ "についても詳しいのでなんでも聞いてください！"));
					break;
				case OLD_ITEM_SELECT:
					qMap.remove(NEW_ITEM);
					mesJab.add(MessageAPIUtil.giveMessage(qMap.get(ITEM)
							+ "についてさらに知りたいとはお目が高い！\nなんでも聞いてください！"));
					break;
				default:
					if (!qMap.containsKey(FINAL_NO_BUTTON)) {
						mesJab.add(MessageAPIUtil.giveMessage("ボタンを押してください"));
						qMap.put(FINAL_NO_BUTTON, TRUE);
					} else {
						mesJab.add(MessageAPIUtil
								.giveMessage("申し訳ありませんが最初からやり直してください。"));
						qmb.removeMapEntry(userId);
					}
					break;
				}
			}
		}
		return makeRequestJson(replyToken, mesJab);
	}

	private String makeRequestJson(String replyToken, JsonArrayBuilder mesJab) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("replyToken", replyToken);
		job.add("messages", mesJab);
		return job.build().toString();
	}

	/**
	 * @param label
	 *            ラベル
	 * @param uri
	 *            URL
	 * @return JsonObjectBuilder
	 */
	private JsonObjectBuilder giveUri4Actions(String label, String uri) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("type", "uri");
		job.add("label", label);
		job.add("uri", uri);
		return job;
	}

	/**
	 * imagemapのためのエリア指定
	 *
	 * @param text
	 *            送信テキスト
	 * @param x
	 *            横軸の座標
	 * @param y
	 *            縦軸の座標
	 * @param height
	 *            高さ
	 * @param width
	 *            幅
	 * @return regionオブジェクト
	 */
	private JsonObjectBuilder giveMes4IM(String text, int x, int y, int height,
			int width) {
		JsonObjectBuilder region = Json.createObjectBuilder();
		region.add(LINEConstants.TYPE, LINEConstants.MESSAGE);
		region.add(LINEConstants.TEXT, text);
		JsonObjectBuilder area = Json.createObjectBuilder();
		area.add(LINEConstants.X, x);
		area.add(LINEConstants.Y, y);
		area.add(LINEConstants.HEIGHT, height);
		area.add(LINEConstants.WIDTH, width);
		region.add(LINEConstants.AREA, area);
		return region;
	}

	/**
	 * @param altText
	 * @param columns
	 *            JsonArrayBuilder
	 * @return
	 */
	private JsonObjectBuilder giveCarousel(String altText,
			JsonArrayBuilder columns) {
		JsonObjectBuilder carousel = Json.createObjectBuilder();
		carousel.add(LINEConstants.TYPE, LINEConstants.TEMPLATE);
		carousel.add(LINEConstants.ALTTEXT, altText);
		JsonObjectBuilder template = Json.createObjectBuilder();
		template.add(LINEConstants.TYPE, LINEConstants.CAROUSEL);
		template.add(LINEConstants.COLUMNS, columns);
		carousel.add(LINEConstants.TEMPLATE, template);
		return carousel;
	}

	/**
	 * カルーセル用のカラム作成
	 *
	 * @param thumbUrl
	 * @param title
	 * @param text
	 * @param actions
	 * @return
	 */
	private JsonObjectBuilder giveColumns4Carousel(String thumbUrl,
			String title, String text, JsonArrayBuilder actions) {
		JsonObjectBuilder column = Json.createObjectBuilder();
		column.add(LINEConstants.THUMB_URL, thumbUrl);
		column.add(LINEConstants.TITLE, title);
		column.add(LINEConstants.TEXT, text);
		column.add(LINEConstants.ACTIONS, actions);
		return column;
	}

	private JsonObjectBuilder giveMes4Actions(String label, String text) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add(LINEConstants.TYPE, LINEConstants.MESSAGE);
		actJob.add(LINEConstants.LABEL, label);
		actJob.add(LINEConstants.TEXT, text);
		return actJob;
	}
}
