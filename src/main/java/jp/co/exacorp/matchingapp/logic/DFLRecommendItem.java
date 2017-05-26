package jp.co.exacorp.matchingapp.logic;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import jp.co.exacorp.matchingapp.beans.DFLItemInfo;
import jp.co.exacorp.matchingapp.beans.PsychologicalTest;
import jp.co.exacorp.matchingapp.beans.QueryMapBean;
import jp.co.exacorp.matchingapp.logic.api.NLCContact;
import jp.co.exacorp.matchingapp.logic.api.PersonalityInsightsContact;
import jp.co.exacorp.matchingapp.util.Constants;
import jp.co.exacorp.matchingapp.util.DFLConstants;
import jp.co.exacorp.matchingapp.util.LINEConstants;
import jp.co.exacorp.matchingapp.util.MessageAPIUtil;
import jp.co.exacorp.matchingapp.util.MessageAPIforDFLUtil;

@Stateless
public class DFLRecommendItem {
	private static final String ITEM = "item";
	private static final String ITEM_SELECT_BEFORE = "itemSelectBefore";
	private static final String ITEM_SELECT = "itemSelect";
	private static final String SELECT_ITEM_EXPLANATION = "プラン説明";
	private static final String ITEM_EXPLANATION = "itemExplanation";
	private static final String END = "もういいや";
	private static final String UNRELATED = "unrelated";
	private static final String NO_TEXT = "noText";
	private static final String OTHERS_ITEM = "他のプランをおすすめして！";
	private static final String RECOMMEND = "recommend";
	private static final String RECOMMEND_BEFORE = "recommendBefore";
	private static final String RECOMMEND_OK = "recommendOk";
	private static final String RECOMMEND_END = "recommendEnd";
	private static final String PI_OK = "piOk";
	private static final String SELECT_RECOMMEND = "レコメンド";
	private static final String Q_NUM = "qNum";
	private static final String Q = "question";
	private static final String A1 = "answer1";
	private static final String A2 = "answer2";
	private static final String A3 = "answer3";
	private static final String T1 = "text1";
	private static final String T2 = "text2";
	private static final String T3 = "text3";
	// 質問数を入れる
	private static final String Q_NUM_MAX = "2";
	private static final String OK = "OK！";
	private static final String NG = "うーん";
	private static final String ITEM_OK_OR_NG = "itemOKorNG";

	@EJB
	QueryMapBean qmb;
	@EJB
	NLCContact nlcc;
	@EJB
	PersonalityInsightsContact pic;
	@EJB
	MatchMaker mm;
	@EJB
	DFLItemInfo dflii;
	@EJB
	PsychologicalTest pt;

	public String teachItem(Map<String, String> eventMap, String imgDir) {
		String type = eventMap.get("messageType");
		String replyToken = eventMap.get("replyToken");
		JsonArrayBuilder mesJab = Json.createArrayBuilder();
		boolean flag = false;

		System.out.println("---------- Logic Start. ----------");

		// ユーザーID取得
		String userId = eventMap.get(LINEConstants.USERID);
		// qMapにいろいろ持たせちゃう
		Map<String, String> qMap = qmb.getQueryMap(userId);

		// テキストに限定
		if (!type.equals(LINEConstants.TEXT)) {
			if (!qMap.containsKey(NO_TEXT)) {
				mesJab.add(MessageAPIUtil.giveMessage("申し訳あらんが、テキストを入力してつかあさい。"));
				qMap.put(NO_TEXT, NO_TEXT);
			} else {
				mesJab.add(MessageAPIUtil.giveMessage("すみませんが最初からやり直してつかあさい。"));
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
			// サブ状態取得
			String stateSub = qMap.get(LINEConstants.STATE_SUB);

			// デバッグ用に来たテキスト、状態を出力
			System.out.println("---------- State at Start. ----------");
			System.out.println("text : " + text);
			for (Map.Entry<String, String> e : qMap.entrySet()) {
				System.out.println(e.getKey() + " -> " + e.getValue());
			}

			// プラン説明押下
			if (text.equals(SELECT_ITEM_EXPLANATION)) {
				mesJab.add(MessageAPIUtil.giveMessage("お好みのプランでーじゃろうか？"));

//追加 START
				for (int i = 0; i < 3 ; i++) {
//追加 END

				// カルーセルでプラン表示
//				mesJab.add(makeCarousel3Item());
				mesJab.add(makeCarousel1Item());
// 追加 START
				}
// 追加 END


				qMap.put(LINEConstants.STATE, ITEM_EXPLANATION);

				// サブ状態にプラン選択前を設定
				qMap.put(LINEConstants.STATE_SUB, ITEM_SELECT_BEFORE);

				// レコメンド押下
			} else if (text.equals(SELECT_RECOMMEND)) {
				// confirm作成
				JsonArrayBuilder confirmAct = Json.createArrayBuilder();
				confirmAct.add(MessageAPIUtil.makeMessage4Action("OK！", OK));
				confirmAct.add(MessageAPIUtil.makeMessage4Action("いいよ！", OK));
				mesJab.add(MessageAPIUtil.giveConfirm("あなたのことについて少し教えて？",
						"そしたら、いくつか質問するから答えてほしい。\nいいか？", confirmAct));

				qMap.put(LINEConstants.STATE, RECOMMEND);

				qMap.put(LINEConstants.STATE_SUB, RECOMMEND_BEFORE);

				// 下ボタン以外
			} else {
				// 状態無（初期状態でなんか言ってきたとき）
				if (state.isEmpty()) {
					mesJab.add(MessageAPIUtil.giveMessage("画面下のボタンを押してつかあさい。"));

					// プラン説明
				} else if (state.equalsIgnoreCase(ITEM_EXPLANATION)) {
					switch (stateSub) {
					// プラン選択をしてもらった時
					case ITEM_SELECT_BEFORE:
						switch (text) {
						// 送られてきた文言がどうか
						case DFLConstants.PREMIRE_RECEIVE_GLB:
							qMap.put(ITEM, DFLConstants.PREMIRE_RECEIVE_GLB);
							mesJab.add(MessageAPIUtil.giveMessage(text
									+ "についてならなんでも聞いつかあさい。"));
							qMap.put(LINEConstants.STATE_SUB, ITEM_SELECT);
							break;

						// ボタン押さずに変なもの送ってきたとき
						// 2回目やってきたら初期状態にしたい
						default:
							if (qMap.containsKey(UNRELATED)) {
								mesJab.add(MessageAPIUtil
										.giveMessage("すみませんが最初からやり直してつかあさい。"));
								qmb.removeMapEntry(userId);
							} else {
								mesJab.add(MessageAPIUtil
										.giveMessage("ボタンを押してつかあさい。"));
								qMap.put(UNRELATED, UNRELATED);
							}
							flag = true;
							break;
						}
						break;

					// プラン選択
					case ITEM_SELECT:
						switch (text) {
						// もういいや
						case END:
							mesJab.add(MessageAPIUtil.giveMessage("ほんならの。"));
							qmb.removeMapEntry(userId);
							break;

						// 他のプラン見せて
						case OTHERS_ITEM:
							// レコメンド終わってる時
							if (qMap.containsKey(RECOMMEND_END)) {
								mesJab.add(MessageAPIUtil
										.giveMessage("でーがお好みのプランじゃろうか？"));

								// カルーセルでプラン表示
								mesJab.add(makeCarousel3Item());

								qMap.put(LINEConstants.STATE, ITEM_EXPLANATION);

								// サブ状態にプラン選択前を設定
								qMap.put(LINEConstants.STATE_SUB,
										ITEM_SELECT_BEFORE);

								// レコメンドまだの時
							} else {
								mesJab.add(MessageAPIUtil
										.giveMessage("質問にいただけたんじゃら、ぴったりなプランをおすすめするんじゃ。"));

								// confirm作成
								JsonArrayBuilder confirmAct = Json
										.createArrayBuilder();
								confirmAct.add(MessageAPIUtil
										.makeMessage4Action("OK！", OK));
								confirmAct.add(MessageAPIUtil
										.makeMessage4Action("いいよ！", OK));
								mesJab.add(MessageAPIUtil.giveConfirm(
										"あなたのことについてちーと教えて？", "答えとっただけますか？",
										confirmAct));

								qMap.put(LINEConstants.STATE, RECOMMEND);
								qMap.put(LINEConstants.STATE_SUB,
										RECOMMEND_BEFORE);
							}
							break;

						// 質問
						default:
// 削除 START
//							mesJab.add(MessageAPIUtil
//									.giveMessage("この回答を見てほしい！"));
// 削除 END

							// NLCにテキスト投げて返ってきた結果を表示させる
							String cid = dflii.getCID(qMap.get(ITEM));

							String[] nlcResults = nlcc.getNLCResult(text, cid);
							StringBuilder sb = new StringBuilder();
							for (int i = 0; i < nlcResults.length; i++) {
								if (i == nlcResults.length - 1) {
									sb.append(nlcResults[i]);
								} else {
									sb.append(nlcResults[i]).append("\n");
								}
							}
							mesJab.add(MessageAPIUtil.giveMessage(sb.toString()));

// 削除 START
							// ここで終わりにするかどうかとかの処理
							// レコメンドを既に行った後であればレコメンドしないような流れにしたい
							// 他のプランをおすすめされたい時用ボタン
//							JsonArrayBuilder buttonAct = Json
//									.createArrayBuilder();
//							buttonAct.add(MessageAPIUtil.makeMessage4Action(
//									"他のプラン", OTHERS_ITEM));
//							buttonAct.add(MessageAPIUtil.makeMessage4Action(
//									"もういいや", END));
//							mesJab.add(MessageAPIUtil
//									.giveButton("他の質問がある場合は再度入力してください。",
//											Constants.BRANK, Constants.BRANK,
//											"他の質問がある場合はもう一度入力してくれ！\nボタンから"
//													+ qMap.get(ITEM)
//													+ "以外のプランをおすすめすることもできるぞ！",
//											buttonAct));
// 削除 END
							break;
						}
						break;
					}

					// レコメンド
				} else if (state.equalsIgnoreCase(RECOMMEND)) {
					switch (stateSub) {
					// 教えてくれる時
					case RECOMMEND_BEFORE:
						// OKって言ってもらったらスタート
						if (text.equals(OK)) {
							mesJab.add(MessageAPIUtil
									.giveMessage("性格診断のために質問をするぞ！"));

							qMap.put(Q_NUM, "1");

							String head = "A" + 1 + ":";

							// 初回質問
							Map<String, String> question = pt.getDataFromID(1);

							// ボタンで答えさせる
							JsonArrayBuilder act4Q = Json.createArrayBuilder();
							act4Q.add(MessageAPIUtil.makeMessage4Action(
									question.get(A1), head + question.get(A1)));
							act4Q.add(MessageAPIUtil.makeMessage4Action(
									question.get(A2), head + question.get(A2)));
							act4Q.add(MessageAPIUtil.makeMessage4Action(
									question.get(A3), head + question.get(A3)));

							// TODO
							mesJab.add(MessageAPIUtil.giveButton("テスト1",
									Constants.BRANK, Constants.BRANK,
									question.get(Q), act4Q));

							// mesJab.add(MessageAPIforDFLUtil.giveQuestion(
							// Constants.QUESTIONS[0], "はい", "いいえ", "A1"));
							qMap.put(LINEConstants.PI_STATE,
									LINEConstants.PI_STATE);

							// 2個目の質問以降
						} else if (text.startsWith("A")) {
							// 質問番号取得
							int qNum = Integer.parseInt(qMap.get(Q_NUM)) + 1;

							// 回答を解答番号と回答に分割、マップに保持
							String[] ans = text.split(":");
							qMap.put(ans[0], ans[1]);

							String query = "";
							if (qMap.containsKey("query")) {
								query = qMap.get("query");
								query = query + pt.getQueryFromAnswer(text);
							} else {
								query = pt.getQueryFromAnswer(text);
							}
							qMap.put("query", query);

							// 最後の質問以外
							if (qNum <= Integer.parseInt(Q_NUM_MAX)) {
								Map<String, String> question = pt
										.getDataFromID(qNum);
								String head = "A" + qNum + ":";

								// ボタンで答えさせる
								JsonArrayBuilder act4Q = Json
										.createArrayBuilder();
								act4Q.add(MessageAPIUtil.makeMessage4Action(
										question.get(A1),
										head + question.get(A1)));
								act4Q.add(MessageAPIUtil.makeMessage4Action(
										question.get(A2),
										head + question.get(A2)));
								act4Q.add(MessageAPIUtil.makeMessage4Action(
										question.get(A3),
										head + question.get(A3)));

								// TODO
								mesJab.add(MessageAPIUtil.giveButton("テスト1",
										Constants.BRANK, Constants.BRANK,
										question.get(Q), act4Q));

								// mesJab.add(MessageAPIforDFLUtil.giveQuestion(
								// Constants.QUESTIONS[qNum - 1], "はい",
								// "いいえ", "A" + qNum));

								qMap.put(Q_NUM, String.valueOf(qNum));

								// 最後の質問終わったとき
							} else {
								// TODO ここでPIに投げるクエリを作成
								String piIn = qMap.get("query");

								Map<String, Double> resultPI = pic
										.getPersonalityInsight(piIn);

								StringBuilder pi = new StringBuilder();
								for (Map.Entry<String, Double> e : resultPI
										.entrySet()) {
									pi.append(
											e.getKey() + " -> " + e.getValue())
											.append("\n");
								}

								// ここで相性診断
								Map<String, String> recommendItem = mm
										.makeMatchingDFLItem(resultPI);

								mesJab.add(MessageAPIUtil
										.giveMessage("君におすすめなのはこれだ！！！\n間違いない！！！"));
								qMap.put(ITEM, recommendItem.get("name"));

								// itemDataにプラン情報持たせる
								Map<String, String> itemData = dflii
										.getInfo(recommendItem.get("name"));

								// 音声でレコメンド
								mesJab.add(MessageAPIUtil.giveAudio(
										Constants.AUDIO_URL
												+ itemData
														.get(LINEConstants.AUDIO),
										"5000"));

								// TODO 画像いらない気がするからコメントアウトしとくけどいるなら戻す
								// mesJab.add(MessageAPIUtil.giveImg(
								// itemData.get(LINEConstants.IMAGE),
								// itemData.get(LINEConstants.IMAGE)));

								// TODO 理由をちゃんと書けるようにね
								mesJab.add(MessageAPIUtil
										.giveMessage("君の性格を数値にするとこんな感じだ！\n"
												+ pi.toString()
												+ "この性格の人は今俺が言ったプランに合ってるぜ！"));
								// TODO ここにプランの情報出すのが良い気がする

								// confirm作成
								JsonArrayBuilder confirmAct = Json
										.createArrayBuilder();
								confirmAct.add(MessageAPIUtil
										.makeMessage4Action("これイイネ", OK));
								confirmAct.add(MessageAPIUtil
										.makeMessage4Action("うーーーん", NG));
								mesJab.add(MessageAPIUtil.giveConfirm(
										"このプランどうかな？", "このプラン、どうだ？", confirmAct));

								qMap.put(LINEConstants.STATE_SUB, ITEM_OK_OR_NG);
							}

							// それ以外の入力があった場合
						} else {
							if (qMap.containsKey(UNRELATED)) {
								mesJab.add(MessageAPIUtil
										.giveMessage("申し訳あらんが、最初からやり直してつかあさい。"));
								qmb.removeMapEntry(userId);
							} else {
								mesJab.add(MessageAPIUtil
										.giveMessage("ボタンを押してつかあさい。"));
								qMap.put(UNRELATED, UNRELATED);
							}
							flag = true;
						}
						break;

					// プランが気に入ったか気に入ってないかの選択後
					case ITEM_OK_OR_NG:
						// レコメンドは終わった状態にする
						qMap.put(RECOMMEND_END, RECOMMEND_END);

						switch (text) {
						// OKの時
						case OK:
							// TODO レコメンドしたプランの簡単な情報を出す
							// そのプランについての質問に答える状態に遷移
							qMap.put(ITEM, DFLConstants.PREMIRE_RECEIVE_GLB);
							mesJab.add(MessageAPIUtil.giveMessage(qMap
									.get(ITEM) + "についてならなんでも聞いつかあさい。"));
							qMap.put(LINEConstants.STATE, ITEM_EXPLANATION);
							qMap.put(LINEConstants.STATE_SUB, ITEM_SELECT);
							break;

						// NGの時
						case NG:
							// TODO 全プラン（3種類）のメリットをパッと出す
							mesJab.add(MessageAPIUtil
									.giveMessage(DFLConstants.PREMIRE_RECEIVE_GLB
											+ "\n一定の金額を毎年確実に受け取りながら、減らさずに残せる外貨建の一時払終身保険だ！\n\n"
											+ DFLConstants.PREMIRE_CURRENCY_PLUS2
											+ "\n目標値に到達したら、自動的に運用成果を確保できる外貨建の個人年金保険だ！\n\n"
											+ DFLConstants.PREMIRE_STORY
											+ "\n高金利で確実に増やしながら、１年後から年金を受け取れる外貨建の年金保険だ！！"));
							mesJab.add(MessageAPIUtil
									.giveMessage("でーが気になるんじゃか？"));

							// カルーセルでプラン表示
							mesJab.add(makeCarousel3Item());

							// プラン説明を選択した後に3種類選ばせる状態に遷移
							qMap.put(LINEConstants.STATE, ITEM_EXPLANATION);
							qMap.put(LINEConstants.STATE_SUB,
									ITEM_SELECT_BEFORE);

							// TODO [確認]
							// 全種類のメリットとプラン選んだあとの簡単な説明が一緒なら表示しないようにしたい
							break;
						}
						break;
					// なんか関係ないやつ送ってきたとき
					default:
						if (qMap.containsKey(UNRELATED)) {
							mesJab.add(MessageAPIUtil
									.giveMessage("はあしわけあらん！最初からやり直してつかあさい！"));
							qmb.removeMapEntry(userId);
						} else {
							mesJab.add(MessageAPIUtil.giveMessage("ボタンを押してつかさい"));
							qMap.put(UNRELATED, UNRELATED);
						}
						flag = true;
						break;
					}
				}
			}
		}
		if (!flag) {
			qMap.remove(UNRELATED);
		}

		// デバッグ用に状態を出力
		System.out.println("---------- Logic End. ----------");

		return MessageAPIUtil.makeRequestJson(replyToken, mesJab);
	}

//追加 START
	/** 営業職員さんが扱える三種類のカルーセルを返す */
	private JsonObjectBuilder makeCarousel1Item() {
		JsonArrayBuilder columns = Json.createArrayBuilder();
		JsonArrayBuilder actionA = Json.createArrayBuilder();
		JsonObjectBuilder carousel = Json.createObjectBuilder();

		actionA.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_GLB));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG,
				DFLConstants.PREMIRE_RECEIVE_GLB,
				DFLConstants.PREMIRE_RECEIVE_GLB_TXT, actionA));

		carousel = MessageAPIUtil.giveCarousel("プラン一覧カルーセル", columns);

		return carousel;
	}
//追加 END

	/** 営業職員さんが扱える三種類のカルーセルを返す */
	private JsonObjectBuilder makeCarousel3Item() {
		JsonArrayBuilder columns = Json.createArrayBuilder();
		JsonArrayBuilder actionA = Json.createArrayBuilder();
		JsonArrayBuilder actionB = Json.createArrayBuilder();
		JsonArrayBuilder actionC = Json.createArrayBuilder();
		JsonObjectBuilder carousel = Json.createObjectBuilder();

		actionA.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_GLB));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG,
				DFLConstants.PREMIRE_RECEIVE_GLB,
				DFLConstants.PREMIRE_RECEIVE_GLB_TXT, actionA));

		actionB.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_CURRENCY_PLUS2));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_CURRENCY_PLUS2_IMG,
				DFLConstants.PREMIRE_CURRENCY_PLUS2,
				DFLConstants.PREMIRE_CURRENCY_PLUS2_TXT, actionB));

		actionC.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_STORY));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_STORY_IMG, DFLConstants.PREMIRE_STORY,
				DFLConstants.PREMIRE_STORY_TXT, actionC));

		carousel = MessageAPIUtil.giveCarousel("プラン一覧カルーセル", columns);

		return carousel;
	}

	public static void callMatchMaker(String userId, String query,
			String imgDir, JsonArrayBuilder mesJab) {
		List<Map<String, String>> resultList = MessageAPIforDFLUtil.mm
				.actMatchMaker(userId, query, imgDir);
		mesJab.add(MessageAPIUtil.giveMessage("君におすすめのプランはコレ！"));
		mesJab.add(MessageAPIUtil.giveMessage("「相談する」をタップすると君と私と選んだアドバイザーさんの"
				+ "LINEグループを作成するよ！"));
		mesJab.add(MessageAPIforDFLUtil.giveCarouselRecommend(resultList));
	}
}
