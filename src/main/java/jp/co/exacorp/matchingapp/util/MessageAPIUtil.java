package jp.co.exacorp.matchingapp.util;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class MessageAPIUtil {
	/**
	 * imageを返す
	 *
	 * @param url
	 *            画像URL
	 * @param preUrl
	 *            サムネ画像URL
	 * @return これ返せばメッセージになる
	 */
	public static JsonObjectBuilder giveImg(String url, String preUrl) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add(LINEConstants.TYPE, LINEConstants.IMAGE);
		actJob.add(LINEConstants.ORG_CONTENT_URL, url);
		actJob.add(LINEConstants.PRE_IMG_URL, preUrl);
		return actJob;
	}

	/**
	 * ボタン作成 imageurlとtitleは必須でないため、ブランクの場合には未指定とする
	 *
	 * @param imageurl
	 *            画像URL
	 * @param title
	 *            タイトル
	 * @param text
	 *            テキスト画像もタイトルも指定しない場合：160文字以内、画像またはタイトルを指定する場合：60文字以内
	 * @param action
	 *            各ボタンのアクション
	 * @return ボタン
	 */
	public static JsonObjectBuilder giveButton(String altText, String imageurl,
			String title, String text, JsonArrayBuilder action) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add(LINEConstants.TYPE, LINEConstants.TEMPLATE);
		actJob.add(LINEConstants.ALTTEXT, altText);

		JsonObjectBuilder template = Json.createObjectBuilder();
		template.add(LINEConstants.TYPE, LINEConstants.BUTTON);
		if (!imageurl.isEmpty()) {
			template.add(LINEConstants.THUMB_URL, imageurl);
		}
		if (!title.isEmpty()) {
			template.add(LINEConstants.TITLE, title);
		}
		template.add(LINEConstants.TEXT, text);
		template.add(LINEConstants.ACTIONS, action);

		actJob.add(LINEConstants.TEMPLATE, template);
		return actJob;
	}

	/**
	 * confirm作成
	 *
	 * @param altText
	 *            代替文字列
	 * @param text
	 *            上に書く文字列
	 * @param action
	 *            makeMessage4Action, makeURI4Actionどちらかかな
	 * @return JOB
	 */
	public static JsonObjectBuilder giveConfirm(String altText, String text,
			JsonArrayBuilder action) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add(LINEConstants.TYPE, LINEConstants.TEMPLATE);
		actJob.add(LINEConstants.ALTTEXT, altText);

		JsonObjectBuilder template = Json.createObjectBuilder();
		template.add(LINEConstants.TYPE, LINEConstants.CONFIRM);
		template.add(LINEConstants.TEXT, text);
		template.add(LINEConstants.ACTIONS, action);

		actJob.add(LINEConstants.TEMPLATE, template);
		return actJob;
	}

// 追加 START
	/**
	 * アクション用メッセージ
	 *
	 * @param label
	 *            ボタン表示ラベル
	 * @param text
	 *            送信するテキスト
	 * @return アクション
	 */
	public static JsonObjectBuilder makeMessage1Action(String label, String text) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add(LINEConstants.TYPE, LINEConstants.MESSAGE);
		actJob.add(LINEConstants.LABEL, label);
		actJob.add(LINEConstants.TEXT, text);
		return actJob;
	}
// 追加 END
	/**
	 * アクション用メッセージ
	 *
	 * @param label
	 *            ボタン表示ラベル
	 * @param text
	 *            送信するテキスト
	 * @return アクション
	 */
	public static JsonObjectBuilder makeMessage4Action(String label, String text) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add(LINEConstants.TYPE, LINEConstants.MESSAGE);
		actJob.add(LINEConstants.LABEL, label);
		actJob.add(LINEConstants.TEXT, text);
		return actJob;
	}

	/**
	 * アクション用URL
	 *
	 * @param label
	 *            ボタン表示ラベル
	 * @param text
	 *            送信するテキスト
	 * @return アクション
	 */
	public static JsonObjectBuilder makeURI4Action(String label, String uri) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add(LINEConstants.TYPE, LINEConstants.URI);
		actJob.add(LINEConstants.LABEL, label);
		actJob.add(LINEConstants.URI, uri);
		return actJob;
	}

	/**
	 * 普通のメッセージ
	 *
	 * @param message
	 *            文章
	 * @return メッセ
	 */
	public static JsonObjectBuilder giveMessage(String message) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("type", "text");
		job.add("text", message);
		return job;
	}

	/**
	 * @param url
	 *            元のURL
	 * @param duration
	 *            長さ
	 * @return audioメッセ
	 */
	public static JsonObjectBuilder giveAudio(String url, String duration) {
		JsonObjectBuilder actJob = Json.createObjectBuilder();
		actJob.add(LINEConstants.TYPE, LINEConstants.AUDIO);
		actJob.add(LINEConstants.ORG_CONTENT_URL, url);
		actJob.add(LINEConstants.DURATION, duration);
		return actJob;
	}

	public static String makeRequestJson(String replyToken,
			JsonArrayBuilder mesJab) {
		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("replyToken", replyToken);
		job.add("messages", mesJab);
		return job.build().toString();
	}

// 追加 START
	/**
	 * カルーセル用のカラム作成
	 *
	 * @param thumbUrl
	 * @param title
	 * @param text
	 * @param actions
	 * @return
	 */
	public static JsonObjectBuilder giveColumns1Carousel(String thumbUrl,
//	public static JsonObjectBuilder giveColumns1Carousel(String thumbUrl
//			 ) {
			String title, String text, JsonArrayBuilder actions) {
		JsonObjectBuilder column = Json.createObjectBuilder();
		column.add(LINEConstants.THUMB_URL, thumbUrl);
		column.add(LINEConstants.TITLE, title);
		column.add(LINEConstants.TEXT, text);
		column.add(LINEConstants.ACTIONS, actions);
		return column;
	}
// 追加 END
	/**
	 * カルーセル用のカラム作成
	 *
	 * @param thumbUrl
	 * @param title
	 * @param text
	 * @param actions
	 * @return
	 */
	public static JsonObjectBuilder giveColumns4Carousel(String thumbUrl,
			String title, String text, JsonArrayBuilder actions) {
		JsonObjectBuilder column = Json.createObjectBuilder();
		column.add(LINEConstants.THUMB_URL, thumbUrl);
		column.add(LINEConstants.TITLE, title);
		column.add(LINEConstants.TEXT, text);
		column.add(LINEConstants.ACTIONS, actions);
		return column;
	}

// 追加 START
//	/**
//	 * @param altText
//	 * @param columns
//	 *            JsonArrayBuilder
//	 * @return
//	 */
//	public static JsonObjectBuilder giveCarousel1(String altText,
//			JsonArrayBuilder columns) {
//		JsonObjectBuilder carousel = Json.createObjectBuilder();
//		carousel.add(LINEConstants.TYPE, LINEConstants.TEMPLATE);
//		carousel.add(LINEConstants.ALTTEXT, altText);
//		JsonObjectBuilder template = Json.createObjectBuilder();
//		template.add(LINEConstants.TYPE, LINEConstants.CAROUSEL);
//		template.add(LINEConstants.COLUMNS, columns);
//		carousel.add(LINEConstants.TEMPLATE, template);
//		return carousel;
//	}
// 追加 END
	/**
	 * @param altText
	 * @param columns
	 *            JsonArrayBuilder
	 * @return
	 */
	public static JsonObjectBuilder giveCarousel(String altText,
			JsonArrayBuilder columns) {
		JsonObjectBuilder carousel = Json.createObjectBuilder();
		carousel.add(LINEConstants.TYPE, LINEConstants.TEMPLATE);
		carousel.add(LINEConstants.ALTTEXT, altText);
		JsonObjectBuilder template = Json.createObjectBuilder();
		template.add(LINEConstants.TYPE, LINEConstants.CAROUSEL);
		template.add(LINEConstants.COLUMNS, columns);
		carousel.add(LINEConstants.TEMPLATE, template);
		return carousel;
	}
}
