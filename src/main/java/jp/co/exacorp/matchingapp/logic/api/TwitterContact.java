package jp.co.exacorp.matchingapp.logic.api;

import java.util.List;

import javax.ejb.Stateless;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

@Stateless
public class TwitterContact {

	public String getRecentTweet (String user){

		StringBuilder sb = new StringBuilder();

		// gets Twitter instance with default credentials
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			for (int i = 1; i <= 2 ; i++) {
				Paging paging = new Paging(i, 100);
				List<Status> statuses = twitter.getUserTimeline(user, paging);
				for (Status status : statuses) {
					String text = status.getText();
					sb.append(text);
					System.out.println("get tweet : " + text);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
        }
		return sb.toString();
	}

}
