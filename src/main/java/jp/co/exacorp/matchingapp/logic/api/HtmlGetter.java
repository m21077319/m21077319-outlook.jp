package jp.co.exacorp.matchingapp.logic.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.Stateless;

@Stateless
public class HtmlGetter {

	/**
	 * URL先のページのHTMLを文字列として返す
	 *
	 * @param urlString
	 *            URL文字列
	 * @return HTMLをStringとして返す
	 * @throws IOException
	 */
	public String getHtmlString(String urlString, String startString,
			String endString) throws IOException {
		String htmlString = "";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		InputStream in = con.getInputStream();
		BufferedReader r = new BufferedReader(new InputStreamReader(in,
				"EUC-JP"));
		boolean flag = false;

		for (;;) {
			String line = r.readLine();

			// 行がなくなったら終了
			if (line == null) {
				break;
			}
			if (line.indexOf(startString) != -1) {
				flag = true;
			} else if (line.indexOf(endString) != -1) {
				flag = false;
			}
			if (flag) {
				htmlString += line;
			}
		}
		return htmlString;
	}
}
