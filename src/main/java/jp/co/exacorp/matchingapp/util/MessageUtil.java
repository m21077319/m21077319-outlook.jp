package jp.co.exacorp.matchingapp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;

public class MessageUtil {

	/**
	 * jsonデータを作成する
	 * @param replyToken リプライトークン
	 * @param messageMapList messagesに入れるオブジェクトのリスト
	 * @return 生成したjsonデータ
	 */
	public static String makeJson (String replyToken, List<Map<String, Object>> messageMapList) {

		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("replyToken", replyToken);
		map.put("messages", messageMapList);
		ObjectMapper mapper = new ObjectMapper();
		// Map -> JSON
		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonStr;
	}

	/**
	 * テキストメッセージ作成
	 * @param text 表示するテキスト,2000文字以内
	 * @return messagesにリストとして追加するマップ
	 */
	public static Map<String, Object> makeMessageText (String text) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();

		if(text.length() > 2000){
			text = text.substring(0, 2000);
		}
		lineMessageMap.put("type", "text");
		lineMessageMap.put("text", text);

		return lineMessageMap;
	}

	/**
	 * イメージメッセージ作成
	 * @param originalContentUrl 画像のURL (1000文字以内),HTTPS,JPEG,縦横最大1024px,最大1MB
	 * @param previewImageUrl プレビュー画像のURL (1000文字以内),HTTPS,JPEG,縦横最大240px,最大1MB
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeMessageImage (String originalContentUrl, String previewImageUrl) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "image");
		lineMessageMap.put("originalContentUrl", changeHttps (originalContentUrl));
		lineMessageMap.put("previewImageUrl", changeHttps (previewImageUrl));

		return lineMessageMap;
	}

	/**
	 * ビデオメッセージ作成
	 * @param originalContentUrl 動画ファイルのURL (1000文字以内),HTTPS,mp4,長さ1分以下,最大10MB
	 * @param previewImageUrl プレビュー画像のURL (1000文字以内),HTTPS,JPEG,縦横最大240px,最大1MB
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeMessageVideo (String originalContentUrl, String previewImageUrl) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "video");
		lineMessageMap.put("originalContentUrl", changeHttps (originalContentUrl));
		lineMessageMap.put("previewImageUrl", changeHttps (previewImageUrl));

		return lineMessageMap;
	}

	/**
	 * 音声メッセージ作成
	 * @param originalContentUrl 音声ファイルのURL (1000文字以内),HTTPS,m4a,長さ1分以下,最大10MB
	 * @param duration 音声ファイルの時間長さ(ミリ秒)
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeMessageAudio (String originalContentUrl, String duration) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "audio");
		lineMessageMap.put("originalContentUrl", changeHttps (originalContentUrl));
		lineMessageMap.put("duration", duration);

		return lineMessageMap;
	}

	/**
	 * 位置情報メッセージ作成
	 * @param title タイトル,100文字以内
	 * @param address 住所,100文字以内
	 * @param latitude 緯度
	 * @param longitude 経度
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeMessageLocation (String title, String address, BigDecimal latitude, BigDecimal longitude) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "location");
		if(title.length() > 100){
			title = title.substring(0, 100);
		}
		lineMessageMap.put("title", title);
		if(address.length() > 100){
			address = address.substring(0, 100);
		}
		lineMessageMap.put("address", address);
		lineMessageMap.put("latitude", latitude);
		lineMessageMap.put("longitude", longitude);

		return lineMessageMap;
	}

	/**
	 * スタンプメッセージ作成
	 * @param packageId パッケージ識別子
	 * @param stickerId Sticker識別子
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeMessageSticker (String packageId, String stickerId) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "sticker");
		lineMessageMap.put("packageId", packageId);
		lineMessageMap.put("stickerId", stickerId);

		return lineMessageMap;
	}


	/**
	 * イメージマップメッセージ作成<br/>
	 * Imagemapに使用する画像は、Base URLの末尾に、クライアントが要求する解像度を横幅サイズ(px)で付与したURLでダウンロードできるようにしておく必要があります。
	 * @param baseUrl Imagemapに使用する画像のBase URL (1000文字以内),HTTPS
	 * @param altText 代替テキスト,400文字以内
	 * @param width 基本比率サイズの幅（1040を指定してください）
	 * @param height 基本比率サイズの高さ（幅を1040としたときの高さを指定してください）
	 * @param actions タップ時のアクション,50以内,makeImagemapActionObjectURIまたはmakeImagemapActionObjectMessageで生成
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeMessageImagemapMessage (String baseUrl, String altText, Integer width, Integer height, List<Map<String, Object>> actions) {

		Map<String, Object> baseSize = new LinkedHashMap<String, Object>();
		baseSize.put("width", width);
		baseSize.put("height", height);

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "imagemap");
		lineMessageMap.put("baseUrl", changeHttps (baseUrl));
		if(altText.length() > 400){
			altText = altText.substring(0, 400);
		}
		lineMessageMap.put("altText", altText);
		lineMessageMap.put("baseSize", baseSize);
		lineMessageMap.put("actions", actions);

		return lineMessageMap;
	}

	/**
	 * Imagemapに配置するアクションの内容とタップ領域の位置を指定するオブジェクトです。<br/>
	 * タップされたときに指定のURIを開くuri。
	 * areaはImagemap全体の幅を1040pxとしたときのサイズを指定します。各座標は左上を原点とします。
	 * @param linkUri WebページのURL,1000文字以内
	 * @param x タップ領域の横方向の位置
	 * @param y タップ領域の縦方向の位置
	 * @param width タップ領域の幅
	 * @param height タップ領域の高さ
	 * @return makeMessageImagemapMessageのactionsにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeImagemapActionObjectURI (String linkUri, Integer x, Integer y, Integer width, Integer height) {

		Map<String, Object> area = new LinkedHashMap<String, Object>();
		area.put("x", x);
		area.put("y", y);
		area.put("width", width);
		area.put("height", height);

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "uri");
		lineMessageMap.put("linkUri", linkUri);
		lineMessageMap.put("area", area);

		return lineMessageMap;
	}

	/**
	 * Imagemapに配置するアクションの内容とタップ領域の位置を指定するオブジェクトです。<br/>
	 * 特定のメッセージ送信をおこなうmessage。
	 * areaはImagemap全体の幅を1040pxとしたときのサイズを指定します。各座標は左上を原点とします。
	 * @param text 送信するメッセージ,400文字以内
	 * @param x タップ領域の横方向の位置
	 * @param y タップ領域の縦方向の位置
	 * @param width タップ領域の幅
	 * @param height タップ領域の高さ
	 * @return makeMessageImagemapMessageのactionsにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeImagemapActionObjectMessage (String text, Integer x, Integer y, Integer width, Integer height) {

		Map<String, Object> area = new LinkedHashMap<String, Object>();
		area.put("x", x);
		area.put("y", y);
		area.put("width", width);
		area.put("height", height);

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "message");
		if(text.length() > 400){
			text = text.substring(0, 400);
		}
		lineMessageMap.put("text", text);
		lineMessageMap.put("area", area);

		return lineMessageMap;
	}

	/**
	 * 画像、タイトル、テキストと、複数のアクションボタンを組み合わせたテンプレートメッセージです。<br/>
	 * thumbnailImageUrl、titleは省略可能です。
	 * @param altText 非対応端末で表示される代替テキスト,400文字以内
	 * @param thumbnailImageUrl 画像のURL (1000文字以内),HTTPS,JPEGまたはPNG,縦横比 1:1.51,縦横最大1024px,最大1MB
	 * @param title タイトル,40文字以内
	 * @param text 説明文,画像もタイトルも指定しない場合：160文字以内,画像またはタイトルを指定する場合：60文字以内
	 * @param actions ボタン押下時のアクション,最大4個
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeMessageTemplateMessageButtons (String altText, String thumbnailImageUrl, String title, String text, List<Map<String, Object>> actions) {

		Map<String, Object> template = new LinkedHashMap<String, Object>();
		template.put("type", "buttons");
		if (thumbnailImageUrl != null && !thumbnailImageUrl.isEmpty()){
			template.put("thumbnailImageUrl", changeHttps (thumbnailImageUrl));
		}
		if (title != null && !title.isEmpty()){
			if(title.length() > 40){
				title = title.substring(0, 40);
			}
			template.put("title", title);
		}
		if(thumbnailImageUrl.isEmpty() && title.isEmpty()) {
			if(text.length() > 160){
				text = text.substring(0, 160);
			}
		} else {
			if(text.length() > 60){
				text = text.substring(0, 60);
			}
		}
		template.put("text", text);
		template.put("actions", actions);

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "template");
		lineMessageMap.put("altText", altText);
		lineMessageMap.put("template", template);

		return lineMessageMap;
	}

	/**
	 * ２つのアクションボタンを提示するテンプレートメッセージです。
	 * @param altText 非対応端末で表示される代替テキスト,400文字以内
	 * @param text 説明文,240文字以内
	 * @param actions ボタン押下時のアクション,最大2個
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeMessageTemplateMessageConfirm (String altText, String text, List<Map<String, Object>> actions) {

		Map<String, Object> template = new LinkedHashMap<String, Object>();
		template.put("type", "confirm");
		if(text.length() > 240){
			text = text.substring(0, 240);
		}
		template.put("text", text);
		template.put("actions", actions);

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "template");
		if(altText.length() > 400){
			altText = altText.substring(0, 400);
		}
		lineMessageMap.put("altText", altText);
		lineMessageMap.put("template", template);

		return lineMessageMap;
	}

	/**
	 * 複数の情報を並べて提示できるカルーセル型のテンプレートメッセージです。
	 * @param altText 非対応端末で表示される代替テキスト,400文字以内
	 * @param columns カラムの配列,最大5個
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeMessageTemplateMessageCarousel (String altText, List<Map<String, Object>> columns) {

		Map<String, Object> template = new LinkedHashMap<String, Object>();
		template.put("type", "carousel");
		template.put("columns", columns);

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "template");
		if(altText.length() > 400){
			altText = altText.substring(0, 400);
		}
		lineMessageMap.put("altText", altText);
		lineMessageMap.put("template", template);

		return lineMessageMap;
	}

	/**
	 * カルーセルテンプレートのカラムを作成します。
	 * 各カラムのthumbnailImageUrl、titleは省略可能です。
	 * @param thumbnailImageUrl 画像のURL (1000文字以内),HTTPS,JPEGまたはPNG,縦横比 1:1.51,縦横最大1024px,最大1MB
	 * @param title タイトル,40文字以内
	 * @param text 説明文,画像もタイトルも指定しない場合：120文字以内,画像またはタイトルを指定する場合：60文字以内
	 * @param actions ボタン押下時のアクション,最大3個
	 * @return messagesにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeTemplateCarouselColumn (String thumbnailImageUrl, String title, String text, List<Map<String, Object>> actions) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		if (thumbnailImageUrl != null && !thumbnailImageUrl.isEmpty()){
			lineMessageMap.put("thumbnailImageUrl", changeHttps (thumbnailImageUrl));
		}
		if (title != null && !title.isEmpty()){
			if(title.length() > 40){
				title = title.substring(0, 40);
			}
			lineMessageMap.put("title", title);
		}

		if(title.isEmpty() && thumbnailImageUrl.isEmpty()){
			if(text.length() > 120){
				text = text.substring(0, 120);
			}
		} else {
			if(text.length() > 60){
				text = text.substring(0, 60);
			}
		}
		lineMessageMap.put("text", text);
		lineMessageMap.put("actions", actions);

		return lineMessageMap;
	}

	/**
	 * このアクションをタップすると、dataで指定された文字列がPostback EventとしてWebhookで通知されます。<br/>
	 * textを指定した場合、その内容がユーザの発言として同時に送信されます。<br/>
	 * textは省略可能です。
	 * @param label アクションの表示名,20文字以内
	 * @param data 	WebhookにPostback Eventのpostback.dataプロパティとして送信される文字列データ,300文字以内
	 * @param text アクション実行時に送信されるテキスト,300文字以内
	 * @return TemplateMessageにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeTemplateActionPostback (String label, String data, String text) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "postback");
		if(label.length() > 20){
			label = label.substring(0, 20);
		}
		lineMessageMap.put("label", label);
		if(data.length() > 300){
			data = data.substring(0, 300);
		}
		lineMessageMap.put("data", data);
		if (text != null && !text.isEmpty()){
			if(text.length() > 300){
				text = text.substring(0, 300);
			}
			lineMessageMap.put("text", text);
		}

		return lineMessageMap;
	}

	/**
	 * このアクションをタップすると、uriで指定されたURIを開きます。
	 * @param label アクションの表示名,20文字以内
	 * @param uri アクション実行時に開くURI (1000文字以内),http, https, tel
	 * @return TemplateMessageにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeTemplateActionURI (String label, String uri) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "uri");
		if(label.length() > 20){
			label = label.substring(0, 20);
		}
		lineMessageMap.put("label", label);
		lineMessageMap.put("uri", uri);

		return lineMessageMap;
	}

	/**
	 * このアクションをタップすると、textで指定された文字列がユーザの発言として送信されます。
	 * @param label アクションの表示名,20文字以内
	 * @param text アクション実行時に送信されるテキスト,300文字以内
	 * @return TemplateMessageにリストとして追加するマップ
	 */
	public static  Map<String, Object> makeTemplateActionMessage (String label, String text) {

		Map<String, Object> lineMessageMap = new LinkedHashMap<String, Object>();
		lineMessageMap.put("type", "message");
		if(label.length() > 20){
			label = label.substring(0, 20);
		}
		lineMessageMap.put("label", label);
		lineMessageMap.put("text", text);

		return lineMessageMap;
	}

	/**
	 * URIをhttpsに変更する
	 * @param uri
	 * @return
	 */
	private static  String changeHttps (String uri) {

		String regex = "http://";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(uri);

		return m.replaceFirst("https://");
	}


	/**
	 * 送信したコンテンツのバイト列を返す
	 * @param id コンテンツID
	 * @return
	 */
	public static byte[] getImageBytes (String id)
	{

		String imageURL = "https://api.line.me/v2/bot/message/" + id + "/content?messageId=" + id;
		byte[] imagebytes = null;
		try {
            URL url = new URL(imageURL);

            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + LINEConstants.LINE_CHANNEL_ACCESS_TOKEN);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    imagebytes = readAll(connection.getInputStream());
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return imagebytes;
	}

	/**
	 * InputStreamをバイト列に変換する
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] readAll(InputStream inputStream) throws IOException {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    byte [] buffer = new byte[1024];
	    while(true) {
	        int len = inputStream.read(buffer);
	        if(len < 0) {
	            break;
	        }
	        bout.write(buffer, 0, len);
	    }
	    return bout.toByteArray();
	}
}
