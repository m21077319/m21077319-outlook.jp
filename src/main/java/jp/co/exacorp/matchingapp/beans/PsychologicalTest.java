package jp.co.exacorp.matchingapp.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class PsychologicalTest implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String ID = "id";
	private static final String Q = "question";
	private static final String A1 = "answer1";
	private static final String A2 = "answer2";
	private static final String A3 = "answer3";
	private static final String T1 = "text1";
	private static final String T2 = "text2";
	private static final String T3 = "text3";

	private static final String Q_N = "今のあなたはピカピカの新入社員。入社して初めての夏に、なんと上司からお中元をもらってしまいました。慌ててお返しをしようと、とりあえず、高級フルーツを送ることにしましたが、あなたなら何を送りますか？";
	private static final String A_N_1 = "メロン";
	private static final String A_N_2 = "さくらんぼ";
	private static final String A_N_3 = "ドリアン";
	private static final String T_N_1 = "高級フルーツの代名詞｢メロン」を選んだ人はかなり気を使っているようですが、メロンが外見(しわだらけ)と中身(上品な甘さ)が違うように、あなたも言葉には出さなくても、心の中ではいろんな事を考えるタイプの人みたい。でも根は悪い人ではなく、基本的には、人を裏切ることができない性分なので、そこのところを理解してくれる人とはとても仲良くなれるでしょう。";
	private static final String T_N_2 = "とてもデリケートな果物「さくらんぼ」を選んだ人は、さくらんぼ同様、デリケートでナイーブな心の持ち主。素直すぎるくらいの反応が周りの人をホッとさせる、いわゆる「天然」とか「癒し系」の人と、逆に、思ったままを口にするあまり周りの人を傷つけてしまう危険なタイプの人がいそうです。";
	private static final String T_N_3 = "果物の王様といわれるものの、強烈なニオイで好き嫌いの分かれる「ドリアン」を選んだ人は、残念ながらちょっと繊細さに欠けているみたい。その反面、あまり細かいことを気にせず、オープンなところがあって、ノリも良かったりするので、あなたの周りには自然と人が集まってきそうです。適度なマイペースを保ちつつ、周りの人にも気を配れる人は、リーダー的存在やみんなの人気者になれるでしょう。";

	private static final String Q_S = "あなたの家の近くにあった空き地にかなりのお金をかけた豪邸が建ちました。それは露骨に豪華に見せてあるようでもありました。その家を見て一言、あなたの気持ちを正直に表している言葉は次の内どれが最も近いでしょう。";
	private static final String A_S_1 = "すごい";
	private static final String A_S_2 = "趣味悪い";
	private static final String A_S_3 = "お友達になりたい";
	private static final String T_S_1 = "少々うさん臭くても、すごい物はすごいと素直に感嘆の気持ちが出てくるあなたは根っからのお気楽さん。人生を気楽にエンジョイしていることでしょう。あまり他人を批判したりせず、率直に認めることが出来るので、人付き合いなども得意そうで社会に出てもうまくやって行けるでしょう。「すごい物」に感心するだけでなく、それに対して自分はどうなのか？どうすれば自分もすごくなれるのか？を考えられる人は、さらにGOOD!気楽でありながらも成功できる素質のある人です。";
	private static final String T_S_2 = "自分より優れている物を自慢げに見せられるとちょっと嫉妬してしまう傾向にあるあなたは気楽さとは最も縁遠い人。残念ながら気楽な人生は送れそうにありません。ただ、その悔しさをエネルギーに変え、気持ちを前向きに切り替えられる人は、勉強や仕事でもすごい力を発揮できそうです。理想が高いところもあるようなので、あまり理想を追求しすぎるとうまく行かないこともあるかもしれませんが、理想と現実を踏まえ、自分の感情をコントロールする事が出来れば、最も伸びるタイプの人とも言えます。";
	private static final String T_S_3 = "自分より優れたモノを目の前にすると素直に負けを認めたり、「長いものには巻かれろ」とばかりに接近を図ろうとするあなたは、気楽そうに見えて、案外気を使うところがあるタイプの人。「気楽な人生を送りたいけど、なかなかそうもいかないな…」なんて思ったりしていませんか？現実の厳しさを知るがゆえにあきらめの早いところもあるようですが、何事も諦めてしまってはその先はありません。自分の力を自分で判断したりせず、努力や挑戦を続けていれば、予想以上の力を発揮したり、思わぬ幸運が転がり込んできたりするかもしれませんよ。";

	private Map<String, Map<String, String>> allIdInfoMap = new TreeMap<String, Map<String, String>>();

	@PostConstruct
	public void init() {
		Map<String, String> infoMap1 = new TreeMap<String, String>();
		Map<String, String> infoMap2 = new TreeMap<String, String>();

		allIdInfoMap.put("001", infoMap1);
		allIdInfoMap.put("002", infoMap2);

		// ID
		infoMap1.put(ID, "1");
		infoMap2.put(ID, "2");

		// Q
		infoMap1.put(Q, Q_N);
		infoMap2.put(Q, Q_S);

		// A1
		infoMap1.put(A1, A_N_1);
		infoMap2.put(A1, A_S_1);

		// A2
		infoMap1.put(A2, A_N_2);
		infoMap2.put(A2, A_S_2);

		// A3
		infoMap1.put(A3, A_N_3);
		infoMap2.put(A3, A_S_3);

		// T1
		infoMap1.put(T1, T_N_1);
		infoMap2.put(T1, T_S_1);

		// T2
		infoMap1.put(T2, T_N_2);
		infoMap2.put(T2, T_S_2);

		// T3
		infoMap1.put(T3, T_N_3);
		infoMap2.put(T3, T_S_3);
	}

	public PsychologicalTest() {

	}

	public Map<String, String> getInfo(String userId) {
		return allIdInfoMap.get(userId);
	}

	/**
	 * 商品名からClassifier ID を返す
	 *
	 * @param name
	 *            名前
	 * @return cid
	 */
	// public String getCID(String name) {
	// String cid = "";
	//
	// for (Map.Entry<String, Map<String, String>> e : getAllItemInfo()
	// .entrySet()) {
	// if (e.getValue().get(NAME).equals(name)) {
	// cid = e.getValue().get(CID);
	// }
	// }
	//
	// return cid;
	// }
	public Map<String, String> getDataFromID(int id) {
		Map<String, String> data = null;

		for (Map.Entry<String, Map<String, String>> e : getAllItemInfo()
				.entrySet()) {
			if (e.getValue().get(ID).equals(String.valueOf(id))) {
				data = e.getValue();
			}
		}

		return data;
	}

	/**
	 * PIに投げる文章を返す 来た回答をそのまま引数にする（例 A1:メロン）
	 *
	 * @param text
	 *            回答できたテキストをそのまま投げてもらう
	 * @return 答えに対応する文章を返す
	 */
	public String getQueryFromAnswer(String text) {
		String query = "";
		String textKey = "";
		// textを分割
		String[] txt = text.split(":");
		// txt[0]から問題番号を取得
		int q_number = Integer.parseInt(txt[0].replaceAll("[^0-9]", ""));
		Map<String, String> data = getDataFromID(q_number);

		int ansNum = 0;

		for (Map.Entry<String, String> e : data.entrySet()) {
			if (e.getValue().equals(txt[1])) {
				ansNum = Integer.parseInt(e.getKey().replaceAll("[^0-9]", ""));
				// キー作成
				textKey = "text" + String.valueOf(ansNum);
			}
		}
		// キーからテキスト取得
		query = data.get(textKey);

		return query;
	}

	public Map<String, Map<String, String>> getAllItemInfo() {
		return allIdInfoMap;
	}
}
