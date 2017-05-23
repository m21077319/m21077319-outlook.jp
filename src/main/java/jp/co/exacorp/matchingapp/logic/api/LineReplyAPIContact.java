package jp.co.exacorp.matchingapp.logic.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.Stateless;
import javax.net.ssl.HttpsURLConnection;

import jp.co.exacorp.matchingapp.util.Constants;
import jp.co.exacorp.matchingapp.util.LINEConstants;

@Stateless
public class LineReplyAPIContact {

	public void sendMessageToReplyAPI (String mesJson) {
		HttpURLConnection con = null;
		PrintWriter pw = null;
		BufferedReader br = null;

		System.out.println(mesJson);

		try {
			con = (HttpsURLConnection) new URL(LINEConstants.LINE_REPLY_API).openConnection();
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setConnectTimeout(10000);
			con.setRequestProperty("Content-Type", "application/json" );
			con.setRequestProperty("Authorization",  "Bearer " + LINEConstants.LINE_CHANNEL_ACCESS_TOKEN);
			pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), Constants.CHARSET));
			pw.print(mesJson);
			pw.close();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				System.out.println("Reply API OK");
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), Constants.CHARSET));
				br.readLine();
				br.close();
			} else {
				System.out.println("HTTP response is " + con.getResponseCode());
				br = new BufferedReader(new InputStreamReader(con.getErrorStream(), Constants.CHARSET));
				System.out.println(br.readLine());
				br.close();

			}
		} catch (Exception e ){
			e.printStackTrace();
		}
		return;
	}
}
