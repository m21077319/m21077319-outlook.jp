package jp.co.exacorp.matchingapp.logic.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import jp.co.exacorp.matchingapp.util.Constants;
import jp.co.exacorp.matchingapp.util.WatsonConstants;

import org.apache.commons.codec.binary.Base64;

/**
 * Personality Insights APIを取り扱うクラス
 *
 * @author Shota Suzuki
 *
 * */
@Stateless
public class PersonalityInsightsContact {

	/**
	 * 引数で与えた分析対象の文章を元にPersonality Insights結果取扱クラスの インスタンスを生成する。
	 *
	 * @param query
	 *            分析対象の文章
	 *
	 * */
	public Map<String, Double> getPersonalityInsight(String query) {
		String apiResponce = callApi(query);
		return genaratePersonalityInsightByJson(apiResponce);
	}

	/**
	 * Personality Insights APIを呼出し、レスポンス（JSON形式）を返却する。
	 *
	 * @param query
	 *            分析対象の文章
	 *
	 * @return APIレスポンス（JSON形式）
	 *
	 * */
	private String callApi(String query) {
		System.out.println("query is : " + query);
		HttpsURLConnection con = null;
		PrintWriter pw = null;
		BufferedReader br = null;

		StringBuffer sb = new StringBuffer();

		byte[] b64data = Base64
				.encodeBase64((WatsonConstants.PI_USER + ":" + WatsonConstants.PI_PASS)
						.getBytes());

		try {
			con = (HttpsURLConnection) new URL(WatsonConstants.PI_URL)
					.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setConnectTimeout(10000);
			con.setRequestProperty("Authorization", "Basic "
					+ new String(b64data));
			con.setRequestProperty("Content-Type", "text/plain");
			con.setRequestProperty("Content-Language", "ja");

			pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream(),
					Constants.CHARSET));
			pw.print(query);
			pw.flush();

			br = new BufferedReader(new InputStreamReader(con.getInputStream(),
					Constants.CHARSET));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			// System.out.println(sb.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	private Map<String, Double> genaratePersonalityInsightByJson(
			String jsonResponce) {
		Map<String, Double> userPi = new TreeMap<String, Double>();
		JsonReader jr = Json.createReader(new StringReader(jsonResponce));
		if (jr != null) {
			JsonArray piArr = jr.readObject().getJsonObject("tree")
					.getJsonArray("children");
			for (int i = 0; i < piArr.size(); i++) {
				JsonObject pi = piArr.getJsonObject(i);
				if (pi.getString("name").equals("Big 5")) {
					JsonArray big5 = pi.getJsonArray("children")
							.getJsonObject(0).getJsonArray("children");
					for (int j = 0; j < big5.size(); j++) {
						JsonObject big5Pi = big5.getJsonObject(j);
						String id = big5Pi.getString("id");
						double percentage = big5Pi.getJsonNumber("percentage")
								.doubleValue();
						// System.out.println("id : " + id + ", parcentage:" +
						// percentage);
						userPi.put(id, percentage);
					}
					break;
				}
			}
		}
		return userPi;
	}
}
