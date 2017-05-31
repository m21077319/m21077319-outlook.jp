package jp.co.exacorp.matchingapp.logic;

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
//	private static final String SELECT_ITEM_EXPLANATION = "おすすめのプラン";
	private static final String SELECT_PLAN_EXPLANATION = "おすすめのプラン";
	private static final String SELECT_PLAN1_EXPLANATION = "備前焼体験";
	private static final String SELECT_NEWS1_EXPLANATION = "観光ニュース";
	private static final String SELECT_NEWS2_EXPLANATION = "観光ニュースつづき";
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

		// テキスト取得
		String text = eventMap.get(LINEConstants.TEXT);

		// おすすめプラン押下
		if (text.equals(SELECT_PLAN_EXPLANATION)) {
			mesJab.add(MessageAPIUtil.giveMessage("お好みのプランを選んでください"));

			// カルーセルでプラン表示
			mesJab.add(makeCarousel5AItem());

		} else if (text.equals(SELECT_PLAN1_EXPLANATION)) {
		 	mesJab.add(MessageAPIUtil.giveMessage("シーンを選んでください"));

			// カルーセルでスポット表示
			mesJab.add(makeCarousel5BItem());
		} else if (text.equals(SELECT_NEWS1_EXPLANATION)) {
			mesJab.add(MessageAPIUtil.giveMessage("気になるニュースを選んでください"));

			// カルーセルでニュース表示
			mesJab.add(makeCarousel5CItem());
		} else if (text.equals(SELECT_NEWS2_EXPLANATION)) {
			mesJab.add(MessageAPIUtil.giveMessage("気になるニュースを選んでください"));

			// カルーセルでニュース表示
			mesJab.add(makeCarousel2AItem());
		} else {

			// 質問
			// NLCにテキスト投げて返ってきた結果を表示させる
			String cid = dflii.getCID(qMap.get(ITEM));

			String[] nlcResults = nlcc.getNLCResult(text, cid);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < nlcResults.length; i++) {
			if (i == nlcResults.length - 1) {
				sb.append(nlcResults[i]);
			} else {
			if ("0000001".equals(nlcResults[i]) &
       	 	  !text.equals(SELECT_PLAN_EXPLANATION) &
			  !text.equals(SELECT_PLAN1_EXPLANATION)) {
				mesJab.add(makeCarousel1Item());
       			}
			}
			}
		mesJab.add(MessageAPIUtil.giveMessage(sb.toString()));

		}

		// デバッグ用に状態を出力
		System.out.println("---------- Logic End. ----------");

		return MessageAPIUtil.makeRequestJson(replyToken, mesJab);
	}
//}


	/** 営業職員さんが扱える三種類のカルーセルを返す */
	private JsonObjectBuilder makeCarousel1Item() {
		JsonArrayBuilder columns = Json.createArrayBuilder();
		JsonArrayBuilder action1 = Json.createArrayBuilder();
		JsonObjectBuilder carousel = Json.createObjectBuilder();

		action1.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_FIS));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_FIS_IMG,
				DFLConstants.PREMIRE_RECEIVE_FIS,
				DFLConstants.PREMIRE_RECEIVE_FIS_TXT, action1));

		carousel = MessageAPIUtil.giveCarousel("プラン一覧カルーセル", columns);
		return carousel;
	}
	/** おすすめプランのカルーセルを返す */
	private JsonObjectBuilder makeCarousel5AItem() {
		JsonArrayBuilder columns = Json.createArrayBuilder();
		JsonArrayBuilder action1 = Json.createArrayBuilder();
		JsonArrayBuilder action2 = Json.createArrayBuilder();
		JsonArrayBuilder action3 = Json.createArrayBuilder();
		JsonArrayBuilder action4 = Json.createArrayBuilder();
		JsonArrayBuilder action5 = Json.createArrayBuilder();
		JsonObjectBuilder carousel = Json.createObjectBuilder();

		action1.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_PL1));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_PL1_IMG,
				DFLConstants.PREMIRE_RECEIVE_PL1,
				DFLConstants.PREMIRE_RECEIVE_PL1_TXT, action1));

		action2.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_PL2));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_PL2_IMG,
				DFLConstants.PREMIRE_RECEIVE_PL2,
				DFLConstants.PREMIRE_RECEIVE_PL2_TXT, action2));

		action3.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_PL3));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_PL3_IMG,
				DFLConstants.PREMIRE_RECEIVE_PL3,
				DFLConstants.PREMIRE_RECEIVE_PL3_TXT, action3));

		action4.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_PL4));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_PL4_IMG,
				DFLConstants.PREMIRE_RECEIVE_PL4,
				DFLConstants.PREMIRE_RECEIVE_PL4_TXT, action4));

		action5.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_PL5));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_PL5_IMG,
				DFLConstants.PREMIRE_RECEIVE_PL5,
				DFLConstants.PREMIRE_RECEIVE_PL5_TXT, action5));

		carousel = MessageAPIUtil.giveCarousel("プラン一覧カルーセル", columns);
		return carousel;
		}

	/** おすすめプラン備前焼体験のカルーセルを返す */
	private JsonObjectBuilder makeCarousel5BItem() {
		JsonArrayBuilder columns = Json.createArrayBuilder();
		JsonArrayBuilder action1 = Json.createArrayBuilder();
		JsonArrayBuilder action2 = Json.createArrayBuilder();
		JsonArrayBuilder action3 = Json.createArrayBuilder();
		JsonArrayBuilder action4 = Json.createArrayBuilder();
		JsonArrayBuilder action5 = Json.createArrayBuilder();
		JsonObjectBuilder carousel = Json.createObjectBuilder();

		action1.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_PL6));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_PL6_IMG,
				DFLConstants.PREMIRE_RECEIVE_PL6,
				DFLConstants.PREMIRE_RECEIVE_PL6_TXT, action1));

		action2.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_PL7));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_PL7_IMG,
				DFLConstants.PREMIRE_RECEIVE_PL7,
				DFLConstants.PREMIRE_RECEIVE_PL7_TXT, action2));

		action3.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_PL8));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_PL8_IMG,
				DFLConstants.PREMIRE_RECEIVE_PL8,
				DFLConstants.PREMIRE_RECEIVE_PL8_TXT, action3));

		action4.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_PL9));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_PL9_IMG,
				DFLConstants.PREMIRE_RECEIVE_PL9,
				DFLConstants.PREMIRE_RECEIVE_PL9_TXT, action4));

		action5.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_P10));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_P10_IMG,
				DFLConstants.PREMIRE_RECEIVE_P10,
				DFLConstants.PREMIRE_RECEIVE_P10_TXT, action5));

		carousel = MessageAPIUtil.giveCarousel("プラン一覧カルーセル", columns);
		return carousel;
		}

	/** ニュース１のカルーセルを返す */
	private JsonObjectBuilder makeCarousel5CItem() {
		JsonArrayBuilder columns = Json.createArrayBuilder();
		JsonArrayBuilder action1 = Json.createArrayBuilder();
		JsonArrayBuilder action2 = Json.createArrayBuilder();
		JsonArrayBuilder action3 = Json.createArrayBuilder();
		JsonArrayBuilder action4 = Json.createArrayBuilder();
		JsonArrayBuilder action5 = Json.createArrayBuilder();
		JsonObjectBuilder carousel = Json.createObjectBuilder();

		action1.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_NEWS_001));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_NEWS_001_IMG,
				DFLConstants.PREMIRE_NEWS_001,
				DFLConstants.PREMIRE_NEWS_001_TXT, action1));

		action2.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_NEWS_002));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_NEWS_002_IMG,
				DFLConstants.PREMIRE_NEWS_002,
				DFLConstants.PREMIRE_NEWS_002_TXT, action2));

		action3.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_NEWS_003));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_NEWS_003_IMG,
				DFLConstants.PREMIRE_NEWS_003,
				DFLConstants.PREMIRE_NEWS_003_TXT, action3));

		action4.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_NEWS_004));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_NEWS_004_IMG,
				DFLConstants.PREMIRE_NEWS_004,
				DFLConstants.PREMIRE_NEWS_004_TXT, action4));

		action5.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_NEWS_005));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_NEWS_005_IMG,
				DFLConstants.PREMIRE_NEWS_005,
				DFLConstants.PREMIRE_NEWS_005_TXT, action5));

		carousel = MessageAPIUtil.giveCarousel("プラン一覧カルーセル", columns);
		return carousel;
		}

	/** ニュース２のカルーセルを返す */
	private JsonObjectBuilder makeCarousel2AItem() {
		JsonArrayBuilder columns = Json.createArrayBuilder();
		JsonArrayBuilder action1 = Json.createArrayBuilder();
		JsonArrayBuilder action2 = Json.createArrayBuilder();
		JsonObjectBuilder carousel = Json.createObjectBuilder();

		action1.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_NEWS_006));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_NEWS_006_IMG,
				DFLConstants.PREMIRE_NEWS_006,
				DFLConstants.PREMIRE_NEWS_006_TXT, action1));

		action2.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_NEWS_007));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_NEWS_007_IMG,
				DFLConstants.PREMIRE_NEWS_007,
				DFLConstants.PREMIRE_NEWS_007_TXT, action2));

		carousel = MessageAPIUtil.giveCarousel("プラン一覧カルーセル", columns);
		return carousel;
		}

	/** 営業職員さんが扱える三種類のカルーセルを返す */
	private JsonObjectBuilder makeCarousel5Item() {
		JsonArrayBuilder columns = Json.createArrayBuilder();
		JsonArrayBuilder action1 = Json.createArrayBuilder();
		JsonArrayBuilder action2 = Json.createArrayBuilder();
		JsonArrayBuilder action3 = Json.createArrayBuilder();
		JsonArrayBuilder action4 = Json.createArrayBuilder();
		JsonArrayBuilder action5 = Json.createArrayBuilder();
		JsonObjectBuilder carousel = Json.createObjectBuilder();

		action1.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_GLB));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG,
				DFLConstants.PREMIRE_RECEIVE_GLB,
				DFLConstants.PREMIRE_RECEIVE_GLB_TXT, action1));

		action2.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_GLB));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG,
				DFLConstants.PREMIRE_RECEIVE_GLB,
				DFLConstants.PREMIRE_RECEIVE_GLB_TXT, action2));

		action3.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_GLB));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG,
				DFLConstants.PREMIRE_RECEIVE_GLB,
				DFLConstants.PREMIRE_RECEIVE_GLB_TXT, action3));

		action4.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_GLB));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG,
				DFLConstants.PREMIRE_RECEIVE_GLB,
				DFLConstants.PREMIRE_RECEIVE_GLB_TXT, action4));

		action5.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
				DFLConstants.PREMIRE_RECEIVE_GLB));
		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
				+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG,
				DFLConstants.PREMIRE_RECEIVE_GLB,
				DFLConstants.PREMIRE_RECEIVE_GLB_TXT, action5));

		carousel = MessageAPIUtil.giveCarousel("プラン一覧カルーセル", columns);
		return carousel;
		}
}

////追加 END
//
//	/** 営業職員さんが扱える三種類のカルーセルを返す */
//	private JsonObjectBuilder makeCarousel3Item() {
//		JsonArrayBuilder columns = Json.createArrayBuilder();
//		JsonArrayBuilder actionA = Json.createArrayBuilder();
//		JsonArrayBuilder actionB = Json.createArrayBuilder();
//		JsonArrayBuilder actionC = Json.createArrayBuilder();
//		JsonObjectBuilder carousel = Json.createObjectBuilder();
//
//		actionA.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
//				DFLConstants.PREMIRE_RECEIVE_GLB));
//		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
//				+ DFLConstants.PREMIRE_RECEIVE_GLB_IMG,
//				DFLConstants.PREMIRE_RECEIVE_GLB,
//				DFLConstants.PREMIRE_RECEIVE_GLB_TXT, actionA));
//
//		actionB.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
//				DFLConstants.PREMIRE_CURRENCY_PLUS2));
//		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
//				+ DFLConstants.PREMIRE_CURRENCY_PLUS2_IMG,
//				DFLConstants.PREMIRE_CURRENCY_PLUS2,
//				DFLConstants.PREMIRE_CURRENCY_PLUS2_TXT, actionB));
//
//		actionC.add(MessageAPIforDFLUtil.giveMes4Actions("これにする",
//				DFLConstants.PREMIRE_STORY));
//		columns.add(MessageAPIUtil.giveColumns4Carousel(Constants.IMG_URL
//				+ DFLConstants.PREMIRE_STORY_IMG, DFLConstants.PREMIRE_STORY,
//				DFLConstants.PREMIRE_STORY_TXT, actionC));
//
//		carousel = MessageAPIUtil.giveCarousel("プラン一覧カルーセル", columns);
//
//		return carousel;
//	}
//
//	public static void callMatchMaker(String userId, String query,
//			String imgDir, JsonArrayBuilder mesJab) {
//		List<Map<String, String>> resultList = MessageAPIforDFLUtil.mm
//				.actMatchMaker(userId, query, imgDir);
//		mesJab.add(MessageAPIUtil.giveMessage("君におすすめのプランはコレ！"));
//		mesJab.add(MessageAPIUtil.giveMessage("「相談する」をタップすると君と私と選んだアドバイザーさんの"
//				+ "LINEグループを作成するよ！"));
//		mesJab.add(MessageAPIforDFLUtil.giveCarouselRecommend(resultList));
//	}
//}
