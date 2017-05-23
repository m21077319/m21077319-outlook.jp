package jp.co.exacorp.matchingapp.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import jp.co.exacorp.matchingapp.util.Constants;

@Singleton
@Startup
public class MLPAInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<String, Map<String, String>> allIdInfoMap = new TreeMap<String, Map<String, String>>();

	@PostConstruct
	public void init() {
		Map<String, String> infoMap1 = new TreeMap<String, String>();
		Map<String, String> infoMap2 = new TreeMap<String, String>();
		Map<String, String> infoMap3 = new TreeMap<String, String>();
		Map<String, String> infoMap4 = new TreeMap<String, String>();
		Map<String, String> infoMap5 = new TreeMap<String, String>();
		Map<String, String> infoMap6 = new TreeMap<String, String>();
		Map<String, String> infoMap7 = new TreeMap<String, String>();
		Map<String, String> infoMap8 = new TreeMap<String, String>();
		Map<String, String> infoMap9 = new TreeMap<String, String>();

		allIdInfoMap.put("001", infoMap1);
		allIdInfoMap.put("002", infoMap2);
		allIdInfoMap.put("003", infoMap3);
		allIdInfoMap.put("004", infoMap4);
		allIdInfoMap.put("005", infoMap5);
		allIdInfoMap.put("006", infoMap6);
		allIdInfoMap.put("007", infoMap7);
		allIdInfoMap.put("008", infoMap8);
		allIdInfoMap.put("009", infoMap9);

		// 名前
		infoMap1.put("name", "真皿 聖");
		infoMap2.put("name", "福田 泰造");
		infoMap3.put("name", "佐藤 誠子");
		infoMap4.put("name", "大野寺 にこ");
		infoMap5.put("name", "井本 幸子");
		infoMap6.put("name", "金親 有美");
		infoMap7.put("name", "竹岩 祐子");
		infoMap8.put("name", "明石 真子");
		infoMap9.put("name", "園口 也実");

		// 名前
		infoMap1.put("rome", "Satoshi Masara");
		infoMap2.put("rome", "Taizo Fukuda");
		infoMap3.put("rome", "Seiko Sato");
		infoMap4.put("rome", "Niko Onodera");
		infoMap5.put("rome", "Sachiko Imoto");
		infoMap6.put("rome", "Yumi Kanechika");
		infoMap7.put("rome", "Yuko Takeiwa");
		infoMap8.put("rome", "Mako Akashi");
		infoMap9.put("rome", "Narimi Sonoguchi");

		// 顔画像
		infoMap1.put("img", "001.jpg");
		infoMap2.put("img", "002.jpg");
		infoMap3.put("img", "003.jpg");
		infoMap4.put("img", "004.jpg");
		infoMap5.put("img", "005.jpg");
		infoMap6.put("img", "006.jpg");
		infoMap7.put("img", "007.jpg");
		infoMap8.put("img", "008.jpg");
		infoMap9.put("img", "009.jpg");

		String yahoo = Constants.IMG_URL + "takeiwa.png";

		// 自己紹介HTML
		infoMap1.put("info", yahoo);
		infoMap2.put("info", yahoo);
		infoMap3.put("info", yahoo);
		infoMap4.put("info", yahoo);
		infoMap5.put("info", yahoo);
		infoMap6.put("info", yahoo);
		infoMap7.put("info", yahoo);
		infoMap8.put("info", yahoo);
		infoMap9.put("info", yahoo);

		// 招待HTML
		infoMap1.put("bff", "https://line.me/ti/p/%40vql0786z");
		infoMap2.put("bff", "https://line.me/ti/p/%40xin5319m");
		infoMap3.put("bff", "https://line.me/ti/p/%40mia3276v");
		infoMap4.put("bff", "https://line.me/ti/p/%40qfx5259h");
		infoMap5.put("bff", "https://line.me/ti/p/%40ahl5833i");
		infoMap6.put("bff", "https://line.me/ti/p/%40xzl9781z");
		infoMap7.put("bff", "https://line.me/ti/p/z9_H9NkVlh");
		infoMap8.put("bff", "https://line.me/ti/p/%40mci9915b");
		infoMap9.put("bff", "https://line.me/ti/p/%40krg8182v");

	}

	public MLPAInfo () {
	}

	public Map<String, String> getMLPAInfo (String userId) {
		return allIdInfoMap.get(userId);
	}
}
