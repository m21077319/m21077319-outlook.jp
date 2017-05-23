package jp.co.exacorp.matchingapp.logic.api;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import jp.co.exacorp.matchingapp.util.WatsonConstants;

import org.apache.commons.codec.binary.Base64;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

/**
 * Visual Recognition APIを取り扱うクラス
 *
 * @author Shinya Sonoda
 *
 * */
@Stateless
public class VRContact {


	public Map<String, String> getVRResult(byte[] imageBytes) {
		System.out.println("getVRResult start");
		String result = callApi(imageBytes);
		Map<String, String> eventMap = new TreeMap<String, String>();
		JsonReader jr = Json.createReader(new StringReader(result));
		if (jr != null) {
			JsonObject jo = jr.readObject();
			JsonObject images = jo.getJsonArray("images").getJsonObject(0);
			JsonObject classifires = images.getJsonArray("classifiers").getJsonObject(0);
			JsonArray classes = classifires.getJsonArray("classes");

			// スコア最大の結果を取得
			eventMap.put("class", classes.getJsonObject(0).getString("class"));
			eventMap.put("score", String.valueOf(classes.getJsonObject(0).getJsonNumber("score").doubleValue()));
			for (int i = 0; i < classes.size(); i++){
				double oldscore = Double.parseDouble(eventMap.get("score"));
				double newscore = classes.getJsonObject(i).getJsonNumber("score").doubleValue();
				if(newscore > oldscore) {
					eventMap.put("class", classes.getJsonObject(i).getString("class"));
					eventMap.put("score", String.valueOf(classes.getJsonObject(i).getJsonNumber("score").doubleValue()));
				}
			}
			return eventMap;
		}

		eventMap.put("class", "");
		eventMap.put("score", "0");
		System.out.println("getVRResult end");
		return eventMap;
	}

	/**
	 * Visual Recognition APIを呼出し、レスポンス（JSON形式）を返却する。
	 *
	 * @param imageBytes 分析対象の画像のバイト列
	 *
	 * @return APIレスポンス（JSON形式）
	 *
	 *  */
	private String callApi(byte[] imageBytes) {

		System.out.println("callApi start");
		HttpsURLConnection con = null;
		PrintWriter pw = null;
		BufferedReader br = null;
		URL url = null;

		StringBuffer sb = new StringBuffer();

		byte[] b64data = Base64.encodeBase64((WatsonConstants.NLC_USER + ":" + WatsonConstants.NLC_PASS ).getBytes() );

		VisualRecognition service = new VisualRecognition("2016-05-20");
		service.setApiKey(WatsonConstants.VR_API_KEY);

		System.out.println("Classify an image");

		// コードを使用するためpom.xmlに以下を追加する必要あり
		//   ⇒ワトソンを簡単に利用するためのライブラリ
		//		<dependency>
		//	    	<groupId>com.ibm.watson.developer_cloud</groupId>
		//	    	<artifactId>java-sdk</artifactId>
		//	    	<version>3.5.3</version>
		//		</dependency>
		ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
		    .images(imageBytes,"testimage.png")
		    .classifierIds("animal_1480618148")
		    .build();
		System.out.println("VisualClassification");
		VisualClassification result = service.classify(options).execute();

		String resultJson = result.toString().replaceAll("\\n", "").replaceAll(" ", "");
		System.out.println("resultJson start");
		System.out.println(resultJson);
		System.out.println("resultJson end");

		return resultJson;


	}
}
