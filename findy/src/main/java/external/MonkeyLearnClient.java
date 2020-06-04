package external;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

public class MonkeyLearnClient {
	private static final String API_KEY = "476e37545ad5e2838508b39e39b0da102a4a28ee";

	public static void main(String[] args) {

		String[] textList = {
				"Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.", "meow meow"};
		List<List<String>> words = extractKeywords(textList);
		for (List<String> ws : words) {
			for (String w : ws) {
				System.out.println(w);
			}
			System.out.println("--");
		}
	}

	// Extract the keywords from the text
	public static List<List<String>> extractKeywords(String[] text) {

		if (text == null || text.length == 0) {
			return new ArrayList<>();
		}

		MonkeyLearn ml = new MonkeyLearn(API_KEY);

		ExtraParam[] extraParams = { new ExtraParam("max_keywords", "3") };
		MonkeyLearnResponse response;

		try {
			response = ml.extractors.extract("ex_YCya9nrn", text, extraParams);
			JSONArray resultArray = response.arrayResult;
			// To get the keywords from the result JSONArray
			return getKeywords(resultArray);
		} catch (MonkeyLearnException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	private static List<List<String>> getKeywords(JSONArray mlResultArray) {
		// The final keywords list
		List<List<String>> topKeywords = new ArrayList<>();

		for (int i = 0; i < mlResultArray.size(); i++) {
			List<String> keywords = new ArrayList<>();

			// Each item in the result array is a JSON Array
			JSONArray keywordsArray = (JSONArray) mlResultArray.get(i);

			for (int j = 0; j < keywordsArray.size(); j++) {
				JSONObject keywordObject = (JSONObject) keywordsArray.get(j);
	
				// Extract the 'keyword' from the small JSON Array
				String keyword = (String) keywordObject.get("keyword");
				keywords.add(keyword);
			}
			System.out.println("keywords: " + keywords);
			topKeywords.add(keywords);
		}

		return topKeywords;
	}

}
