package jp.co.exacorp.matchingapp.logic.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.Stateless;

import jp.co.exacorp.matchingapp.util.LINEConstants;

@Stateless
public class ContentContact {
	/* この2行はConstantsクラスに記述する */
	public static final String CONTENT_URL_HEAD = "https://api.line.me/v2/bot/message/";
	public static final String CONTENT_URL_FOOT = "/content";

	public byte[] getImage(String id) {

		// get inputstream
		HttpURLConnection con = null;
		InputStream in = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		try {
			URL url = new URL(CONTENT_URL_HEAD + id + CONTENT_URL_FOOT);

			con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", "Bearer "
					+ LINEConstants.LINE_CHANNEL_ACCESS_TOKEN);

			con.connect();

			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				in = con.getInputStream();
			}

			byte[] buffer = new byte[1024];
			while (true) {
				int len = in.read(buffer);
				if (len < 0) {
					break;
				}
				bout.write(buffer, 0, len);
			}

			System.out.println("これでたぶん画像の取得はできてるんじゃないかな・・・・");

			// String result = new String(bout.toByteArray(), "UTF-8");
			// System.out.println("byte配列からStringに変換した結果の最初の50文字 ： "
			// + result.substring(0, 50));

		} catch (Exception e) {
			System.out.println("content接続時エラー");
			// e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("close時エラー");
			}
		}
		return bout.toByteArray();
	}
}
