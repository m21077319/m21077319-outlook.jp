package jp.co.exacorp.matchingapp.logic;

import java.util.Map;

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
import jp.co.exacorp.matchingapp.util.Constants;
import jp.co.exacorp.matchingapp.util.DFLConstants;
import jp.co.exacorp.matchingapp.util.LINEConstants;
import jp.co.exacorp.matchingapp.util.MessageAPIUtil;

@Stateless
public class TeachDFLItem {
	private static final String START = "start";
	private static final String SELECTED = "selected";
	private static final String TRUE = "TRUE";
	private static final String STARTMES = "開始";
	private static final String THAT_Q_TXT = "この商品のあの図を見る";
	private static final String THAT_TXT = "のアレ見せて";

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

	public String teachItem(Map<String, String> eventMap, String imgDir) {
		String type = eventMap.get("messageType");
		String id = eventMap.get("id");
		String replyToken = eventMap.get("replyToken");
		JsonArrayBuilder mesJab = Json.createArrayBuilder();

		// テキストに限定
		if (!type.equals(LINEConstants.TEXT)) {
			// 状態取得
			String userId = eventMap.get(LINEConstants.USERID);
			Map<String, String> qMap = qmb.getQueryMap(userId);
			String state = qMap.get(LINEConstants.STATE);
			if (state.equals(START)) {
				if (qMap.containsKey(LINEConstants.COUNT)) {
					// 2回変なの来たら状態をフリダシに戻る
					mesJab.add(giveMessage("申し訳ありません。\n最初からやり直してください。"));
					// ステータス削除
					qmb.removeMapEntry(userId);
				} else {
					// 1回目の変なのはフラグを立てて終わり
					qMap.put(LINEConstants.COUNT, TRUE);
					mesJab.add(giveMessage("画像内をタップしてください"));
				}
			} else {
				// メッセージ
				mesJab.add(giveMessage("このbotはテキストメッセージのみに対応しております。\n申し訳ございません。"));
			}
		} else {
			//
			String text = eventMap.get(LINEConstants.TEXT);
			String userId = eventMap.get(LINEConstants.USERID);
			Map<String, String> qMap = qmb.getQueryMap(userId);
			String state = qMap.get(LINEConstants.STATE);

			// ここの文言はボタンで一旦言わせる
			if (text.equals(STARTMES)) {
				// ステータスに状態を保持
				qMap.put(LINEConstants.STATE, START);
				mesJab.add(giveMessage("どの種類の商品についての紹介を見ますか？\n画像内をタップしてください。"));
				// ------------------------------------------------------------------------------
				// imagemapを作成
				// ------------------------------------------------------------------------------
				JsonObjectBuilder imagemap = Json.createObjectBuilder();
				imagemap.add(LINEConstants.TYPE, LINEConstants.IMAGEMAP);
				imagemap.add(LINEConstants.BASEURL, Constants.IMG_URL
						+ "temp_imagemap");
				imagemap.add("altText", "イメージマップだよ");
				JsonObjectBuilder baseSize = Json.createObjectBuilder();
				baseSize.add(LINEConstants.WIDTH, 1040);
				baseSize.add(LINEConstants.HEIGHT, 1040);
				imagemap.add("baseSize", baseSize);
				JsonArrayBuilder actions = Json.createArrayBuilder();
				// 左下
				actions.add(giveMes4IM(DFLConstants.FIXED_ANNUITY_INSURANCE, 0,
						520, 520, 520));
				// 右下
				actions.add(giveMes4IM(
						DFLConstants.STRAIGHT_LINE_LIFE_INSURANCE, 520, 520,
						520, 520));
				// 左上
				actions.add(giveMes4IM(DFLConstants.VARIABLE_ANNUITY_INSURANCE,
						0, 0, 520, 520));
				// 右上
				actions.add(giveMes4IM(DFLConstants.VARIABLE_LIFE_INSURANCE,
						520, 0, 520, 520));
				imagemap.add(LINEConstants.ACTIONS, actions);
				mesJab.add(imagemap);
				// ------------------------------------------------------------------------------
			} else {
				switch (state) {
				case START:
					// 変なのかどうか判定、正常ならif内へ
					if (text.equals(DFLConstants.VARIABLE_ANNUITY_INSURANCE)
							|| text.equals(DFLConstants.VARIABLE_LIFE_INSURANCE)
							|| text.equals(DFLConstants.FIXED_ANNUITY_INSURANCE)
							|| text.equals(DFLConstants.STRAIGHT_LINE_LIFE_INSURANCE)) {
						// 状態によって分岐
						mesJab.add(giveMessage(text + "には以下の商品がございます。"));
						// ステータスに状態を保持
						qMap.put(LINEConstants.STATE, SELECTED);

						// 箱用意
						JsonArrayBuilder columns = Json.createArrayBuilder();
						JsonArrayBuilder actionA = Json.createArrayBuilder();
						JsonArrayBuilder actionB = Json.createArrayBuilder();
						JsonArrayBuilder actionC = Json.createArrayBuilder();
						JsonObjectBuilder carousel = Json.createObjectBuilder();

						switch (text) {
						// 変額年金保険
						case DFLConstants.VARIABLE_ANNUITY_INSURANCE:

							actionA.add(giveUri4Actions("商品ページへ",
									DFLConstants.PREMIERE_JUMP3_URL));
							// TODO
							actionA.add(giveMes4Actions(THAT_Q_TXT,
									DFLConstants.PREMIERE_JUMP3 + "のアレ見せて"));
							columns.add(giveColumns4Carousel(Constants.IMG_URL
									+ DFLConstants.PREMIERE_JUMP3_IMG,
									DFLConstants.PREMIERE_JUMP3,
									DFLConstants.PREMIERE_JUMP3_TXT, actionA));

							actionB.add(giveUri4Actions("商品ページへ",
									DFLConstants.PREMIRE_STEP_GLB2_URL));
							actionB.add(giveMes4Actions(THAT_Q_TXT,
									DFLConstants.PREMIRE_STEP_GLB2 + "のアレ見せて"));
							columns.add(giveColumns4Carousel(Constants.IMG_URL
									+ DFLConstants.PREMIRE_STEP_GLB2_IMG,
									DFLConstants.PREMIRE_STEP_GLB2,
									DFLConstants.PREMIRE_STEP_GLB2_TXT, actionB));

							carousel = giveCarousel("商品一覧カルーセル", columns);

							mesJab.add(carousel);
							break;

						// 変額終身保険
						case DFLConstants.VARIABLE_LIFE_INSURANCE:
							actionA.add(giveUri4Actions("商品ページへ",
									DFLConstants.PREMIRE_JUMP2_JPN_URL));
							columns.add(giveColumns4Carousel(Constants.IMG_URL
									+ DFLConstants.PREMIRE_JUMP2_JPN_IMG,
									DFLConstants.PREMIRE_JUMP2_JPN,
									DFLConstants.PREMIRE_JUMP2_JPN_TXT, actionA));

							actionB.add(giveUri4Actions("商品ページへ",
									DFLConstants.PREMIRE_JUMP2_GLB_URL));
							columns.add(giveColumns4Carousel(Constants.IMG_URL
									+ DFLConstants.PREMIRE_JUMP2_GLB_IMG,
									DFLConstants.PREMIRE_JUMP2_GLB,
									DFLConstants.PREMIRE_JUMP2_GLB_TXT, actionB));

							carousel = giveCarousel("商品一覧カルーセル", columns);
							mesJab.add(carousel);
							break;

						// 定額年金保険
						case DFLConstants.FIXED_ANNUITY_INSURANCE:
							actionA.add(giveUri4Actions("商品ページへ",
									DFLConstants.PREMIRE_STORY_URL));
							columns.add(giveColumns4Carousel(Constants.IMG_URL
									+ DFLConstants.PREMIRE_STORY_IMG,
									DFLConstants.PREMIRE_STORY,
									DFLConstants.PREMIRE_STORY_TXT, actionA));

							actionB.add(giveUri4Actions("商品ページへ",
									DFLConstants.PREMIRE_CURRENCY_PLUS2_URL));
							columns.add(giveColumns4Carousel(Constants.IMG_URL
									+ DFLConstants.PREMIRE_CURRENCY_PLUS2_IMG,
									DFLConstants.PREMIRE_CURRENCY_PLUS2,
									DFLConstants.PREMIRE_CURRENCY_PLUS2_TXT,
									actionB));

							carousel = giveCarousel("商品一覧カルーセル", columns);
							mesJab.add(carousel);
							break;

						// 定額終身保険
						case DFLConstants.STRAIGHT_LINE_LIFE_INSURANCE:
							actionA.add(giveUri4Actions("商品ページへ",
									DFLConstants.PREMIRE_RECEIVE_JPN_URL));
							columns.add(giveColumns4Carousel(Constants.IMG_URL
									+ DFLConstants.PREMIRE_RECEIVE_JPN_IMG,
									DFLConstants.PREMIRE_RECEIVE_JPN,
									DFLConstants.PREMIRE_RECEIVE_JPN_TXT,
									actionA));

							actionB.add(giveUri4Actions("商品ページへ",
									DFLConstants.PREMIRE_RECEIVE_GLB_URL));
							columns.add(giveColumns4Carousel(Constants.IMG_URL
									+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG,
									DFLConstants.PREMIRE_RECEIVE_GLB,
									DFLConstants.PREMIRE_RECEIVE_GLB_TXT,
									actionB));

							actionC.add(giveUri4Actions("商品ページへ",
									DFLConstants.PREMIRE_GIFT2_AUS_URL));
							columns.add(giveColumns4Carousel(Constants.IMG_URL
									+ DFLConstants.PREMIRE_GIFT2_AUS_IMG,
									DFLConstants.PREMIRE_GIFT2_AUS,
									DFLConstants.PREMIRE_GIFT2_AUS_TXT, actionC));

							carousel = giveCarousel("商品一覧カルーセル", columns);
							mesJab.add(carousel);
							break;
						}
						// TODO できたらボタン表示させて状態戻してもっかいimagemapだしたい
						mesJab.add(giveMessage("他の商品の一覧を見たい場合はもう一度画面下部「開始」を押してください。"));
					} else {
						// 2回変なの来たら状態をフリダシに戻る
						if (qMap.containsKey(LINEConstants.COUNT)) {
							mesJab.add(giveMessage("申し訳ありません。\n最初からやり直してください。"));
							// ステータス削除
							qmb.removeMapEntry(userId);
						} else {
							// 1回目の変なのはフラグを立てて終わり
							qMap.put(LINEConstants.COUNT, TRUE);
							mesJab.add(giveMessage("画像内をタップしてください"));
						}
					}
					break;

				// 選択後
				case SELECTED:
					// TODO ここにアレ聞かれたときの挙動
					if (text.endsWith(THAT_TXT)) {
						mesJab.add(giveMessage("「アレ」ですね？ﾆｬ"));

						// 商品ごとに分岐
						switch (text.replaceAll(THAT_TXT, Constants.BRANK)) {
						case DFLConstants.PREMIERE_JUMP3:
							mesJab.add(MessageAPIUtil
									.giveImg(
											Constants.IMG_URL
													+ DFLConstants.PREMIERE_JUMP3_THAT_IMG,
											Constants.IMG_URL
													+ DFLConstants.PREMIERE_JUMP3_THAT_PRE_IMG));
							break;
						case DFLConstants.PREMIRE_STEP_GLB2:
							mesJab.add(MessageAPIUtil
									.giveImg(
											Constants.IMG_URL
													+ DFLConstants.PREMIRE_STEP_GLB2_THAT_IMG,
											Constants.IMG_URL
													+ DFLConstants.PREMIRE_STEP_GLB2_THAT_PRE_IMG));
							break;
						}
						mesJab.add(giveMessage("他に知りたいことがあったら最初から動作を行ってください。"));
						qmb.removeMapEntry(userId);
					}
					break;
				}
			}
		}
		return makeRequestJson(replyToken, mesJab);
	}

	private JsonObjectBuilder giveMessage(String message) {
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
