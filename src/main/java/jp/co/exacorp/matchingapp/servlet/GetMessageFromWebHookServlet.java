package jp.co.exacorp.matchingapp.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import jp.co.exacorp.matchingapp.logic.DFLItem;
import jp.co.exacorp.matchingapp.logic.DFLRecommendItem;
import jp.co.exacorp.matchingapp.logic.MatchMakeTalk;
import jp.co.exacorp.matchingapp.logic.RightActor;
import jp.co.exacorp.matchingapp.logic.api.LineReplyAPIContact;
import jp.co.exacorp.matchingapp.util.Constants;
import jp.co.exacorp.matchingapp.util.LINEConstants;

@WebServlet("/TEST01APP")
public class GetMessageFromWebHookServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@EJB
	LineReplyAPIContact lrac;

	@EJB
	RightActor ra;

	@EJB
	MatchMakeTalk mmt;

	@EJB
	DFLRecommendItem dflri;

	@EJB
	DFLItem dfli;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String imgDir = this.getServletContext().getRealPath(
				Constants.IMG_URL);
		String body = getRequestBody(req);

		// 署名検証 LINEからのものではなければ処理終了
		if (!verifySignature(req.getHeader(LINEConstants.LINE_HEADER_SIGNATURE),
				body)) {
			resp.setStatus(HttpServletResponse.SC_OK);
			System.out.println("It is not from Line");
			return;
		}
		System.out.println("It is from Line");
		System.out.println("imgDir : " + imgDir);
		Map<String, String> eventMap = makeEventMap(body);
		eventMap.put("imageDir", imgDir);
		String message = "";

		// 個人メッセの時
		if (eventMap.get("sourceType").equals("user")) {
			// ロジックを変えたいときはここを書き換える
			System.out.println("logicに突入");
			message = dflri.teachItem(eventMap, imgDir);

			// グループの時
		} else {
			// ロジックを変えたいときはここを書き換える
			message = dflri.teachItem(eventMap, imgDir);
		}
		if (!message.isEmpty()) {
			lrac.sendMessageToReplyAPI(message);
		}
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * リクエストのbodyを取得する。
	 *
	 * */
	private String getRequestBody(HttpServletRequest req) {
		BufferedReader br = null;
		try {
			br = req.getReader();
			String line = br.readLine();
			return line;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
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

	/**
	 * 署名検証
	 *
	 * */
	private boolean verifySignature(String signature, String reqBody) {
		SecretKeySpec sks = new SecretKeySpec(
				LINEConstants.LINE_CHANNEL_SECRET.getBytes(), Constants.HMAC_SHA256);
		try {
			Mac mac = Mac.getInstance(Constants.HMAC_SHA256);
			mac.init(sks);
			byte[] source = reqBody.getBytes(Constants.CHARSET);
			byte[] createdSignature = Base64.encodeBase64(mac.doFinal(source));
			return signature.equals(new String(createdSignature));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private Map<String, String> makeEventMap(String body) {
		Map<String, String> eventMap = new TreeMap<String, String>();
		JsonReader jr = Json.createReader(new StringReader(body));
		if (jr != null) {
			JsonObject jo = jr.readObject();
			JsonObject event = jo.getJsonArray("events").getJsonObject(0);
			eventMap.put("replyToken", event.getString("replyToken"));
			;
			eventMap.put("type", event.getString("type"));
			JsonObject message = event.getJsonObject("message");
			if (message != null) {
				String messageType = message.getString("type");
				eventMap.put("messageType", messageType);
				if (messageType != null) {
					if (messageType.equals("text")) {
						eventMap.put("text", message.getString("text"));
					}
					if (messageType.equals("image")) {
						eventMap.put("id", message.getString("id"));
					}
					if (messageType.equals("audio")) {
						eventMap.put("id", message.getString("id"));
					}
					if (messageType.equals("location")) {
						eventMap.put("title", message.getString("title"));
						eventMap.put("address", message.getString("address"));
						eventMap.put("latitude", message.getString("latitude"));
						eventMap.put("longitude",
								message.getString("longitude"));
					}
					if (messageType.equals("sticker")) {
						eventMap.put("packageId",
								message.getString("packageId"));
						eventMap.put("stickerId",
								message.getString("stickerId"));
					}
				}
			}

			JsonObject source = event.getJsonObject("source");
			String sourceType = source.getString("type");
			eventMap.put("sourceType", sourceType);
			if (sourceType != null) {
				if (sourceType.equals("user")) {
					eventMap.put("userId", source.getString("userId"));
				}
				if (sourceType.equals("group")) {
					eventMap.put("groupId", source.getString("groupId"));
				}
				if (sourceType.equals("room")) {
					eventMap.put("roomId", source.getString("roomId"));
				}
			}

		}
		return eventMap;
	}
}
