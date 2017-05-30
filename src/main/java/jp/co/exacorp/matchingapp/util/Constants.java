package jp.co.exacorp.matchingapp.util;

public class Constants {

	/** HMAC_SHA256 */
	public static final String HMAC_SHA256 = "HmacSHA256";

	/** 質問数 */
	public static final int NUM_OF_QUESTION = 9;

	/** リクエスト区切り文字 */
	public static final String REQ_DELIM = ",";

	public static final String IMG_DIR_PATH = "images/";

	public static final String IMG_URL = "https://TEST01APP.eu-gb.mybluemix.net/images/";
	public static final String AUDIO_URL = "https://dflife.mybluemix.net/audio/";

	/** Web Audio API URL */
	public static final String VOICE_URL = "https://api.recaius.jp/tts/v2/plaintext2speechwave";
	/** Web Audio API KEY */
	public static final String VOICE_KEY = "";

	public static final String FORTUNE_URL = "http://api.jugemkey.jp/api/horoscope/free/";

	/** 文字コード */
	public static final String CHARSET = "UTF-8";

	/** 質問にYESと答えた場合のクエリ */
	public static final String[] YES_QUERIES = { "人付き合いが好き。",
			"客観的、論理的に考えるのが自分に合っている。", "机、本棚などは整理整頓されているのが良い。",
			"新しいクラスやグループに入った時、周りの人と比較的早く馴染める。", "ある課題を終わらせてから、次の事をやる。",
			"余暇は友人や同僚と会う事が多い。", "きちんと段階を踏んで考えて話す。", "他人の悩みに共感しやすい。",
			"物事は基本的に白黒ハッキリつけたい派だ。" };
	/** 質問にNOと答えた場合のクエリ */
	public static final String[] NO_QUERIES = { "人付き合いは面倒、または苦手。",
			"主観的、感情的に考えるのが自分に合っている。", "机、本棚などはごちゃごちゃでも使えれば問題なし。",
			"新しいクラスやグループに入った時、周囲となかなか馴染めない。", "いろいろな課題を並行してやる。",
			"余暇は一人でのんびりしている事が多い。", "次々と話題を変える。", "他人に悩み事には鈍感な方だ。",
			"あまりはっきり決めない方が柔軟に対処できると思う。" };

	public static final String[] QUESTIONS = { "人付き合いは好き？", "物事を客観的に考えるタイプ？",
			"机とか本棚は整理整頓されている方が好き？", "新しいグループに早く馴染める方？",
			"ひとつひとつ物事を終わらせてから次の事をするタイプ？", "休みの日は友達や同僚と過ごすことが多い？",
			"きちんと段階を踏んで話すタイプ？", "他人の悩みに共感しやすい？", "物事は基本的に白黒はっきりつけたい派？" };

	public static final String TYPE_RESULT_MAX = "きみと似ているタイプ";
	public static final String TYPE_RESULT_MID = "中立的なタイプ";
	public static final String TYPE_RESULT_MIN = "補い合えるタイプ";
	public static final String BRANK = "";

}
