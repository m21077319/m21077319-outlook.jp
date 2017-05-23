package jp.co.exacorp.matchingapp.logic;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.ejb.Stateless;

import jp.co.exacorp.matchingapp.logic.api.VRContact;
import jp.co.exacorp.matchingapp.util.Constants;
import jp.co.exacorp.matchingapp.util.MessageUtil;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

@Stateless
public class MatchMakeTalk {

	static Map<String, Map<String, String>> status;

	/**
	 * 個人トーク時の対応
	 * @param replyToken
	 * @param eventMap
	 * @return
	 */
	public String makeMessageJson (Map<String, String> eventMap) {
		if (status == null) {
			status = new TreeMap<String, Map<String,String>>();
		}
		String replyToken = eventMap.get("replyToken");
		String userID = eventMap.get("userId");
		if (userID == null) {
			userID = eventMap.get("groupId");
			if (userID == null) {
				userID = eventMap.get("roomId");
			}
		}

		List<Map<String, Object>> messageMapList = new ArrayList<Map<String, Object>> ();
		// メッセージに対応
		if (eventMap.get("type").equals("message")) {

			if (eventMap.get("messageType").equals("text")) {

				String usertext = eventMap.get("text").trim();
				if (usertext.equals("今年の運勢を占う")){

					messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + "exakun01.png", Constants.IMG_URL + "exakun01_mini.png"));
					messageMapList.add(MessageUtil.makeMessageText("<エクサくん>\nあけましておめでとう！"));
					List<Map<String, Object>> actions = new ArrayList<Map<String,Object>>();
					actions.add(MessageUtil.makeTemplateActionMessage("はい", "はい"));
					actions.add(MessageUtil.makeTemplateActionMessage("今はいいや", "今はいいや"));
					messageMapList.add(MessageUtil.makeMessageTemplateMessageConfirm("今年の運勢を占ってあげようか？","今年の運勢を占ってあげようか？",actions));

					Map<String, String> stage = new TreeMap<String, String>();
					stage.put("stage", "stage01");
					status.put(userID, stage);

					return MessageUtil.makeJson (replyToken, messageMapList);
				}
				else if (status.get(userID) != null && status.get(userID).get("stage") != null ) {
					String stageString = status.get(userID).get("stage");
					if (stageString.equals("stage01")) {
						if (usertext.equals("はい")){

							messageMapList.add(MessageUtil.makeMessageText("じゃあいくつか質問するよ！"));
							messageMapList.add(MessageUtil.makeMessageText("お名前は？"));

							status.get(userID).put("stage", "stage02");

						} else if (usertext.equals("今はいいや")){

							messageMapList.add(MessageUtil.makeMessageText("そうか―、じゃあまた占いたくなったら言ってね"));
							status.remove(userID);

						}
						return MessageUtil.makeJson (replyToken, messageMapList);
					}
					else if (stageString.equals("stage02")) {

						messageMapList.add(MessageUtil.makeMessageText("生年月日は？"));

						status.get(userID).put("name", usertext);
						status.get(userID).put("stage", "stage03");

						return MessageUtil.makeJson (replyToken, messageMapList);

					}
					else if (stageString.equals("stage03")) {

						status.get(userID).put("birthday", usertext);


						int randomNum = 0;
						int randomMax = 20;

						try {
							Integer hashCode = status.get(userID).get("birthday").hashCode();
							long seedBirth = hashCode.longValue();

							long seedName = hashCode.longValue();

							long seedRandom = seedBirth + seedName;

							Random r = new Random(seedRandom);
							randomNum = r.nextInt(randomMax);
						} catch (Exception e) {

							e.printStackTrace() ;
							messageMapList.add(MessageUtil.makeMessageText("Exception:" + e.getStackTrace()));
							Random r = new Random();
							randomNum = r.nextInt(randomMax);
						}


						messageMapList.add(MessageUtil.makeMessageText(status.get(userID).get("name") + "さん、あなたの今年の運勢は"));

						List<Map<String, Object>> actions = new ArrayList<Map<String,Object>>();
						switch (randomNum) {
						case 0:
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
						case 8:
						case 9:
						case 10:
						case 11:
							actions.add(MessageUtil.makeTemplateActionURI("詳しく見る", Constants.IMG_URL + "daikichi.png"));
							actions.add(MessageUtil.makeTemplateActionMessage("納得", "納得"));
							actions.add(MessageUtil.makeTemplateActionMessage("もう一度", "もう一度"));
							messageMapList.add(MessageUtil.makeMessageTemplateMessageButtons("あなたの運勢",Constants.IMG_URL + "daikichi_mini.png","","大吉です！！",actions));
							break;
						case 12:
						case 13:
						case 14:
						case 15:
						case 16:
						case 17:
						case 18:
							actions.add(MessageUtil.makeTemplateActionURI("詳しく見る", Constants.IMG_URL + "kichi.png"));
							actions.add(MessageUtil.makeTemplateActionMessage("納得", "納得"));
							actions.add(MessageUtil.makeTemplateActionMessage("もう一度", "もう一度"));
							messageMapList.add(MessageUtil.makeMessageTemplateMessageButtons("あなたの運勢",Constants.IMG_URL + "kichi_mini.png","","吉です！",actions));
							break;
						default:
							actions.add(MessageUtil.makeTemplateActionURI("詳しく見る", Constants.IMG_URL + "kyou.png"));
							actions.add(MessageUtil.makeTemplateActionMessage("納得", "納得"));
							actions.add(MessageUtil.makeTemplateActionMessage("もう一度", "もう一度"));
							messageMapList.add(MessageUtil.makeMessageTemplateMessageButtons("あなたの運勢",Constants.IMG_URL + "kyou_mini.png","","凶です",actions));
							break;
						}


						status.get(userID).put("stage", "stage04");

						return MessageUtil.makeJson (replyToken, messageMapList);
					}
					else if (stageString.equals("stage04")) {
						if (usertext.equals("納得")){

							messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + "wazanome.png", Constants.IMG_URL + "wazanome_mini.png"));
							messageMapList.add(MessageUtil.makeMessageText("<技の芽ちゃん>\nねえ、アナタの絵を描こうかと思うんだけどどうかな？"));

							List<Map<String, Object>> actions = new ArrayList<Map<String,Object>>();
							actions.add(MessageUtil.makeTemplateActionMessage("はい", "はい"));
							actions.add(MessageUtil.makeTemplateActionMessage("今はいいや", "今はいいや"));
							messageMapList.add(MessageUtil.makeMessageTemplateMessageConfirm("絵を描いてもらう？","絵を描いてもらう？",actions));

							status.get(userID).put("stage", "stage05");

						} else if (usertext.equals("もう一度")){

							messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + "exakun01.png", Constants.IMG_URL + "exakun01_mini.png"));
							messageMapList.add(MessageUtil.makeMessageText("じゃあいくつか質問するよ！"));
							messageMapList.add(MessageUtil.makeMessageText("お名前は？"));

							status.get(userID).put("stage", "stage02");
						}
						return MessageUtil.makeJson (replyToken, messageMapList);
					}
					else if (stageString.equals("stage05")) {
						if (usertext.equals("はい")){

							messageMapList.add(MessageUtil.makeMessageText("実は私、人の顔を動物に例えるのがトクイなんだ！"));
							messageMapList.add(MessageUtil.makeMessageText("アナタの写真を送ってね！\nアナタをイメージした絵を描くよ！\n ※顔写真を送ってください"));

							status.get(userID).put("stage", "stage06");

						} else if (usertext.equals("今はいいや")){

							messageMapList.add(MessageUtil.makeMessageText("そうか―、じゃあまた描いてほしくなったら言ってね"));
							status.remove(userID);

						}
						return MessageUtil.makeJson (replyToken, messageMapList);
					}
					else if (stageString.equals("stage06")) {

						messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + "wazanome.png", Constants.IMG_URL + "wazanome_mini.png"));
						messageMapList.add(MessageUtil.makeMessageText("絵を描くからアナタの写真を送ってね"));

						return MessageUtil.makeJson (replyToken, messageMapList);
					}
					else if (stageString.equals("stage07")) {
						if (usertext.equals("Yes")){


							messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + "wazanome.png", Constants.IMG_URL + "wazanome_mini.png"));
							messageMapList.add(MessageUtil.makeMessageText("アナタの顔を動物に例えて絵を描くよ！"));
							messageMapList.add(MessageUtil.makeMessageText("アナタの写真を送ってね！\n※顔写真を送ってください"));

							status.get(userID).put("stage", "stage06");

						} else if (usertext.equals("No")){

							messageMapList.add(MessageUtil.makeMessageText("今年も良い年になりますように！"));
							messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + "exawazanome.png", Constants.IMG_URL + "exawazanome_mini.png"));
							messageMapList.add(MessageUtil.makeMessageText("Bye Bye!"));
							status.remove(userID);

						}

						return MessageUtil.makeJson (replyToken, messageMapList);
					}
				}


				if (usertext.startsWith("技の芽") || usertext.startsWith("わざのめ") || usertext.startsWith("技のめ") || usertext.startsWith("技のめ")){
					int min = usertext.indexOf(" ");
					if (min < 0 || usertext.indexOf(" ") < min) {
						min = usertext.indexOf("　");
					}

					if(0 < min && (min+1) < usertext.length()){
						String freeword = usertext.substring(min+1).trim();

						List<Map<String, Object>> restaurantInfo = getRestaurant(freeword);
						if(restaurantInfo == null || restaurantInfo.isEmpty()){

							messageMapList.add(MessageUtil.makeMessageText("「" + freeword + "」でお店を探してみたけど\nいいお店が見つからなかったよ"));
							return MessageUtil.makeJson (replyToken, messageMapList);
						}
						messageMapList.add(MessageUtil.makeMessageText("「" + freeword + "」でお店を探してみたよ"));
						List<Map<String, Object>> columns = new ArrayList<Map<String,Object>>();
						String restaurantjson = MessageUtil.makeJson (replyToken, restaurantInfo);
						System.out.println("restaurant jsontext : " + restaurantjson);
						for (Map<String, Object> restaurantdata : restaurantInfo) {
							List<Map<String, Object>> actions = new ArrayList<Map<String,Object>>();
							actions.add(MessageUtil.makeTemplateActionURI("詳しい情報を開く", restaurantdata.get("url_mobile").toString()));
							String imageurl = restaurantdata.get("shop_image").toString();
							if(imageurl == null || imageurl.isEmpty()){
								imageurl = Constants.IMG_URL + "api_225_100.png";
							}
							columns.add(MessageUtil.makeTemplateCarouselColumn(imageurl, restaurantdata.get("name").toString(), "提供：ぐるなび\n" + restaurantdata.get("address").toString(), actions));
						}

						messageMapList.add(MessageUtil.makeMessageTemplateMessageCarousel("レストラン情報", columns));
						String json = MessageUtil.makeJson (replyToken, messageMapList);
						System.out.println("jsontext : " + json);
						return json;
					}

					messageMapList.add(MessageUtil.makeMessageText("はい！私技の芽ちゃんです！"));
					messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + "wazanome.png", Constants.IMG_URL + "wazanome_mini.png"));
					return MessageUtil.makeJson (replyToken, messageMapList);
				}

				if (usertext.contains("技") || usertext.contains("わざ") || usertext.contains("芽")) {
					messageMapList.add(MessageUtil.makeMessageText("ん？呼んだ？"));
					return MessageUtil.makeJson (replyToken, messageMapList);
				}
			}
			if (eventMap.get("messageType").equals("image")) {

				if (status.get(userID) != null && status.get(userID).get("stage").equals("stage06")) {
					VRContact vrContact = new VRContact();

					Map<String, String> animalResult = vrContact.getVRResult(MessageUtil.getImageBytes(eventMap.get("id")));

					String animalFace = animalResult.get("class");
					Double score = Double.parseDouble(animalResult.get("score"));

					String resultText = "アナタは";

					String scoreRank = "";

					String imageStr = "";
					if (score > 0.95) {
						scoreRank = "かなりの";
						imageStr = "_high";
					}
					else if (score <= 0.7) {
						scoreRank = "ちょっぴり";
						imageStr = "_low";
					}
					else {
						scoreRank = "まあまあ";
						imageStr = "_mid";
					}

					String afterText = "顔だね！";

					if (animalFace.equals("cat")) {
						resultText = resultText + scoreRank + "ネコ" + afterText;
						//						imageStr = "cat" + imageStr;
						imageStr = "cat";
					}
					else if (animalFace.equals("dog")) {
						resultText = resultText + scoreRank + "イヌ" + afterText;
						//						imageStr = "dog" + imageStr;

						switch (score.hashCode() % 3){
						case 0:
							imageStr = "dog_01";
							break;
						case 1:
							imageStr = "dog_02";
							break;
						default:
							imageStr = "dog";
						}
//						imageStr = "dog";
					}
					else if (animalFace.equals("fox")) {
						resultText = resultText + scoreRank + "キツネ" + afterText;
						//						imageStr = "fox" + imageStr;
						imageStr = "fox";
					}
					else if (animalFace.equals("rabbit")) {
						resultText = resultText + scoreRank + "ウサギ" + afterText;
						//						imageStr = "rabbit" + imageStr;
						imageStr = "rabbit";
					}
					else if (animalFace.equals("saru")) {
						resultText = resultText + scoreRank + "サル" + afterText;
//						imageStr = "saru" + i
						switch (score.hashCode() % 3){
						case 0:
							imageStr = "saru_01";
							break;
						case 1:
							imageStr = "saru_02";
							break;
						default:
							imageStr = "saru";
						}
//						imageStr = "saru";
					}
					else if (animalFace.equals("tanuki")) {
						resultText = resultText + scoreRank + "タヌキ" + afterText;
						//						imageStr = "tanuki" + imageStr;
						imageStr = "tanuki";
					}
					else {
						resultText = resultText + "独特な顔だちをしているね";
						imageStr = "sonota";
					}
					messageMapList.add(MessageUtil.makeMessageText(resultText));
					messageMapList.add(MessageUtil.makeMessageText("アナタの顔をイメージして絵をかいてみたよ"));
					messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + imageStr + ".jpg", Constants.IMG_URL + imageStr + ".jpg"));
					messageMapList.add(MessageUtil.makeMessageText("どう？似てるかな？？"));

					List<Map<String, Object>> actions = new ArrayList<Map<String,Object>>();
					actions.add(MessageUtil.makeTemplateActionMessage("Yes", "Yes"));
					actions.add(MessageUtil.makeTemplateActionMessage("No", "No"));
					messageMapList.add(MessageUtil.makeMessageTemplateMessageButtons("もう一度やる？","","","もう一度やる？",actions));

					status.get(userID).put("stage", "stage07");

					return MessageUtil.makeJson (replyToken, messageMapList);
				}
			}
			if (eventMap.get("messageType").equals("sticker")) {
				String imageStr = "wazanome";
				//randomメソッドで0以上6未満の整数を生成
				switch ((int)(Math.random() * 6)){
				case 0:
					imageStr = "wazanome0";
					break;
				case 1:
					imageStr = "wazanome1";
					break;
				case 2:
					imageStr = "wazanome2";
					break;
				case 3:
					imageStr = "wazanome3";
					break;
				case 4:
					imageStr = "wazanome4";
					break;
				default:
					imageStr = "wazanome";
				}
				messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + imageStr + ".png", Constants.IMG_URL + imageStr + "_mini.png"));
				return MessageUtil.makeJson (replyToken, messageMapList);
			}
		}
		return MessageUtil.makeJson (replyToken, messageMapList);

	}

	/**
	 * グループ、またはルームトーク時の対応
	 * @param replyToken
	 * @return
	 */
	public String makeMessageJsonGroup (Map<String, String> eventMap) {

		String replyToken = eventMap.get("replyToken");

		// 返答メッセージのリストを作成
		List<Map<String, Object>> messageMapList = new ArrayList<Map<String, Object>> ();

		// グループに招待されたとき
		if (eventMap.get("type").equals("join")) {
			messageMapList.add(MessageUtil.makeMessageText("やあ！私技の芽ちゃんです！！\nグループのみんな！よろしくね！"));
			messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + "wazanome.png", Constants.IMG_URL + "wazanome_mini.png"));
			return MessageUtil.makeJson (replyToken, messageMapList);
		}

		// メッセージに対応
		if (eventMap.get("type").equals("message")) {

			if (eventMap.get("messageType").equals("text")) {

				String usertext = eventMap.get("text").trim();
				if (usertext.startsWith("技の芽") || usertext.startsWith("わざのめ") || usertext.startsWith("技のめ") || usertext.startsWith("技のめ")){
					int min = usertext.indexOf(" ");
					if (min < 0 || usertext.indexOf(" ") < min) {
						min = usertext.indexOf("　");
					}

					if(0 < min && (min+1) < usertext.length()){
						String freeword = usertext.substring(min+1).trim();

						List<Map<String, Object>> restaurantInfo = getRestaurant(freeword);
						if(restaurantInfo == null || restaurantInfo.isEmpty()){

							messageMapList.add(MessageUtil.makeMessageText("「" + freeword + "」でお店を探してみたけど\nいいお店が見つからなかったよ"));
							return MessageUtil.makeJson (replyToken, messageMapList);
						}
						messageMapList.add(MessageUtil.makeMessageText("「" + freeword + "」でお店を探してみたよ"));
						List<Map<String, Object>> columns = new ArrayList<Map<String,Object>>();
						String restaurantjson = MessageUtil.makeJson (replyToken, restaurantInfo);
						System.out.println("restaurant jsontext : " + restaurantjson);
						for (Map<String, Object> restaurantdata : restaurantInfo) {
							List<Map<String, Object>> actions = new ArrayList<Map<String,Object>>();
							actions.add(MessageUtil.makeTemplateActionURI("詳しい情報を開く", restaurantdata.get("url_mobile").toString()));
							String imageurl = restaurantdata.get("shop_image").toString();
							if(imageurl == null || imageurl.isEmpty()){
								imageurl = Constants.IMG_URL + "api_225_100.png";
							}
							columns.add(MessageUtil.makeTemplateCarouselColumn(imageurl, restaurantdata.get("name").toString(), "提供：ぐるなび\n" + restaurantdata.get("address").toString(), actions));
						}

						messageMapList.add(MessageUtil.makeMessageTemplateMessageCarousel("レストラン情報", columns));
						String json = MessageUtil.makeJson (replyToken, messageMapList);
						System.out.println("jsontext : " + json);
						return json;
					}

					messageMapList.add(MessageUtil.makeMessageText("はい！私技の芽ちゃんです！"));
					messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + "wazanome.png", Constants.IMG_URL + "wazanome_mini.png"));
					return MessageUtil.makeJson (replyToken, messageMapList);
				}

				if (usertext.contains("技") || usertext.contains("わざ") || usertext.contains("芽")) {
					messageMapList.add(MessageUtil.makeMessageText("ん？呼んだ？"));
					return MessageUtil.makeJson (replyToken, messageMapList);
				}

			}
			if (eventMap.get("messageType").equals("sticker")) {
				String imageStr = "wazanome";
				//randomメソッドで0以上6未満の整数を生成
				switch ((int)(Math.random() * 6)){
				case 0:
					imageStr = "wazanome0";
					break;
				case 1:
					imageStr = "wazanome1";
					break;
				case 2:
					imageStr = "wazanome2";
					break;
				case 3:
					imageStr = "wazanome3";
					break;
				case 4:
					imageStr = "wazanome4";
					break;
				default:
					imageStr = "wazanome";
				}
				messageMapList.add(MessageUtil.makeMessageImage(Constants.IMG_URL + imageStr + ".png", Constants.IMG_URL + imageStr + "_mini.png"));
				return MessageUtil.makeJson (replyToken, messageMapList);
			}
		}


		return null;

	}


	public static List<Map<String, Object>> getRestaurant(String freeword) {
		HttpURLConnection con = null;
		// ぐるなびのキーIDを記載
		String keyid = "489ac0ba36e46bb3fa7ac14c301cbe74";
		try {
			// URLの作成
			URL url = new URL("http://api.gnavi.co.jp/RestSearchAPI/20150630/?keyid=" + keyid + "&format=json&freeword=" + URLEncoder.encode(freeword, "UTF-8"));

			// 接続用HttpURLConnectionオブジェクト作成
			con = (HttpURLConnection)url.openConnection();

			// リクエストメソッドの設定
			con.setRequestMethod("GET");
			// リダイレクトを自動で許可しない設定
			con.setInstanceFollowRedirects(false);
			// ヘッダーの設定(複数設定可能)
			con.setRequestProperty("Accept-Language", "jp");

			// 接続
			con.connect();

			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> restaurantInfo = getMapListFromJson(mapper.readTree(con.getInputStream()));

			return restaurantInfo;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static List<Map<String, Object>> getMapListFromJson(JsonNode nodeList){
		if(nodeList != null){
			//トータルヒット件数
			String hitcount   = "total:" + nodeList.path("total_hit_count").asText();
			System.out.println(hitcount);
			//restのみ取得
			JsonNode restList = nodeList.path("rest");
			Iterator<JsonNode> rest = restList.iterator();
			System.out.println("restList.path(\"id\") : "+restList.path("id"));
			System.out.println("restList.path(\"id\").asText() : "+restList.path("id").asText());
			List<Map<String, Object>> restaurantList = new ArrayList<Map<String, Object>>();
			if (restList.path("id").asText() == null) {

				int count = 0;
				while(rest.hasNext() && count < 5){

					restaurantList.add(getRestaurantData(rest.next()));
					count ++;
				}

			} else {
				//一つのみデータを取得した場合

				restaurantList.add(getRestaurantData(restList));

			}
			return restaurantList;
		}
		return null;
	}

	private static Map<String, Object> getRestaurantData(JsonNode rest){
		//店舗番号、店舗名、最寄の路線、最寄の駅、最寄駅から店までの時間、店舗の小業態を出力
		Map<String, Object> restaurantData = new LinkedHashMap<String, Object>();
		// 店舗番号
		restaurantData.put("id", rest.path("id").asText());

		// 店舗名
		restaurantData.put("name", rest.path("name").asText());

		// 住所
		restaurantData.put("address", rest.path("address").asText());

		// 携帯用URL
		restaurantData.put("url_mobile", rest.path("url_mobile").asText());

		// 店舗画像
		restaurantData.put("shop_image", rest.path("image_url").path("shop_image1").asText());

		// 最寄の路線
		restaurantData.put("line", rest.path("access").path("line").asText());

		// 最寄の駅
		restaurantData.put("station", rest.path("access").path("station").asText());

		// 最寄駅から店までの時間
		restaurantData.put("walk", rest.path("access").path("walk").asText() + "分");

		// 店舗の小業態
		String categorys = "";
		for(JsonNode n : rest.path("code").path("category_name_s")){
			categorys += n.asText();
		}
		restaurantData.put("categorys", categorys);

		return restaurantData;

	}
}
