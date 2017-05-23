package jp.co.exacorp.matchingapp.logic.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.Stateless;

import jp.co.exacorp.matchingapp.util.Constants;

/**
 * @author Akaiwa
 *
 *         音声関連API呼び出したい
 */
@Stateless
public class RecaiusContact {

	/**
	 * 音声合成する
	 *
	 * @param mes
	 *            音声合成したいメッセージ
	 * @return リクエストURL文字列
	 */
	public String makeTTSURL(String mes) {
		HttpURLConnection con = null;
		PrintWriter pw = null;
		BufferedReader br = null;

		// get Fotune-Telling
		try {
			// URLの作成
			URL url = new URL(Constants.VOICE_URL);
			System.out.println(url);

			// 接続用HttpURLConnectionオブジェクト作成
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("GET");
			con.setConnectTimeout(10000);

		} catch (Exception e) {
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
		return "";
	}
}
