package jp.co.exacorp.matchingapp.logic.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;

import jp.co.exacorp.matchingapp.util.Constants;
import jp.co.exacorp.matchingapp.util.WatsonConstants;

/**
 * NaturalLanguageClassifier APIを取り扱うクラス
 *
 * @author Shota Suzuki
 *
 * */
@Stateless
public class NLCContact {

	/**
	 * NLCに質問よぶ
	 *
	 * @param query
	 *            質問文
	 * @return 答えの文字列配列
	 */
	public String[] getNLCResult(String query) {
		String result = callApi(query);
		JsonReader jr = Json.createReader(new StringReader(result));
		if (jr != null) {
			JsonObject jo = jr.readObject();
			// 改行したいところでバーティカルバー入れてるので分割
			return jo.getString("top_class").split("\\|");
		}
		// 答えが空の場合
		return new String[0];
	}

	/**
	 * NLCに質問よぶ
	 *
	 * @param query
	 *            質問文
	 * @param classifierID
	 *            対象NLCのclassifierID
	 * @return 答えの文字列配列
	 */
	public String[] getNLCResult(String query, String classifierID) {
		String url = classifierID;
		String result = callApi(query, url);
		JsonReader jr = Json.createReader(new StringReader(result));
		if (jr != null) {
			JsonObject jo = jr.readObject();
			// 改行したいところでバーティカルバー入れてるので分割
// 修正 START
			return jo.getString("top_class").split("|");
//			return jo.getString("top_class").split("\\|");
// 修正 END
		}
		// 答えが空の場合
		return new String[0];
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

		HttpsURLConnection con = null;
		PrintWriter pw = null;
		BufferedReader br = null;

		StringBuffer sb = new StringBuffer();

		byte[] b64data = Base64
				.encodeBase64((WatsonConstants.NLC_USER + ":" + WatsonConstants.NLC_PASS)
						.getBytes());

		try {
			JsonObjectBuilder job = Json.createObjectBuilder();
			job.add("text", query);

			con = (HttpsURLConnection) new URL(WatsonConstants.NLC_URL)
					.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setConnectTimeout(10000);
			con.setRequestProperty("Authorization", "Basic "
					+ new String(b64data));
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");

			pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream(),
					Constants.CHARSET));
			pw.print(job.build().toString());
			pw.flush();

			br = new BufferedReader(new InputStreamReader(con.getInputStream(),
					Constants.CHARSET));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			System.out.println(sb.toString());
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

	/**
	 * Personality Insights APIを呼出し、レスポンス（JSON形式）を返却する。
	 *
	 * @param query
	 *            分析対象の文章
	 *
	 * @return APIレスポンス（JSON形式）
	 *
	 * */
	private String callApi(String query, String url) {

		HttpsURLConnection con = null;
		PrintWriter pw = null;
		BufferedReader br = null;

		StringBuffer sb = new StringBuffer();

		byte[] b64data = Base64
				.encodeBase64((WatsonConstants.NLC_USER + ":" + WatsonConstants.NLC_PASS)
						.getBytes());

		try {
			JsonObjectBuilder job = Json.createObjectBuilder();
			job.add("text", query);

			con = (HttpsURLConnection) new URL(WatsonConstants.NLC_URL)
					.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setConnectTimeout(10000);
			con.setRequestProperty("Authorization", "Basic "
					+ new String(b64data));
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");

			pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream(),
					Constants.CHARSET));
			pw.print(job.build().toString());
			pw.flush();

			br = new BufferedReader(new InputStreamReader(con.getInputStream(),
					Constants.CHARSET));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			System.out.println(sb.toString());
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
}
