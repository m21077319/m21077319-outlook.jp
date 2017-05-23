package jp.co.exacorp.matchingapp.logic.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.Stateless;

import jp.co.exacorp.matchingapp.util.Constants;

@Stateless
public class FortuneContact {

	public String getFortune(String date) {

		StringBuilder sb = new StringBuilder();

		HttpURLConnection con = null;
		PrintWriter pw = null;
		BufferedReader br = null;

		// get Fotune-Telling
		try {
			// URLの作成
			URL url = new URL(Constants.FORTUNE_URL + date);
			System.out.println(url);

			// 接続用HttpURLConnectionオブジェクト作成
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("GET");
			con.setConnectTimeout(10000);

			// 接続
			con.connect();

			br = new BufferedReader(new InputStreamReader(con.getInputStream(),
					Constants.CHARSET));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}

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
		return sb.toString();
	}
}
