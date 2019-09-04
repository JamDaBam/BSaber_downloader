package bsaber.tools.bsaber_scrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class BeatSaverParser {
	private static final Logger cvLogger = LogManager.getLogger(BeatSaverParser.class);

	// Jsonkeys
	private static final String KEY = "key";
	private static final String SONG_NAME = "songName";
	private static final String SONG_SUB_NAME = "songSubname";
	private static final String LEVEL_AUTHOR_NAME = "levelAuthorName";
	private static final String SONG_AUTHOR_NAME = "songAuthorName";
	private static final String DOWNLOAD_URL = "downloadURL";
	private static final String BPM = "bpm";
	private static final String DOWN_VOTES = "downVotes";
	private static final String UP_VOTES = "upVotes";
	private static final String RATING = "rating";
	private static final String HEAT = "heat";
	private static final String DIFFICULTIES = "difficulties";
	private static final String COVER_URL = "coverURL";
	private static final String DIRECT_DOWNLOAD = "directDownload";
	// Exclude
	private static final String CHARACTERISTICS = "characteristics";

	// Json Membernames
	private static final String DOCS = "docs";

	public static List<SongEntry> parse(String aJsonString) {

		List<SongEntry> songEntries = new ArrayList<>();

		Gson gson = new GsonBuilder().create();
		JsonElement fromJson = gson.fromJson(aJsonString, JsonElement.class);
		if (fromJson instanceof JsonObject) {
			JsonObject jsonObject = fromJson.getAsJsonObject();

			// Multiple entries
			if (jsonObject.has(DOCS)) {
				JsonArray asJsonArray = jsonObject.getAsJsonArray(DOCS);
				for (JsonElement jsonElement : asJsonArray) {
					songEntries.add(new SongEntry(parse(jsonElement, new SongMetaData())));
				}
			}
			// Single entry
			else {
				songEntries.add(new SongEntry(parse(fromJson, new SongMetaData())));
			}
		}

		return songEntries;
	}

	private static SongMetaData parse(JsonElement aJsonElement, SongMetaData aMetaData) {
		if (!(aJsonElement instanceof JsonPrimitive) && !(aJsonElement instanceof JsonNull)) {
			if (aJsonElement instanceof JsonArray) {
				for (JsonElement jsonElement : aJsonElement.getAsJsonArray()) {
					aMetaData = parse(jsonElement, aMetaData);
				}
			} else {
				JsonObject jsonObject = (JsonObject) aJsonElement;

				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					String key = entry.getKey();
					JsonElement value = entry.getValue();

					if (!CHARACTERISTICS.equals(key)) {
						if (KEY.equals(key)) {
							aMetaData.setKey(value.getAsString());
						} else if (SONG_NAME.equals(key)) {
							aMetaData.setSongName(value.getAsString());
						} else if (SONG_SUB_NAME.equals(key)) {
							aMetaData.setSongSubName(value.getAsString());
						} else if (LEVEL_AUTHOR_NAME.equals(key)) {
							aMetaData.setLevelAuthorName(value.getAsString());
						} else if (SONG_AUTHOR_NAME.equals(key)) {
							aMetaData.setSongAuthorName(value.getAsString());
						} else if (DOWNLOAD_URL.equals(key)) {
							aMetaData.setDownloadURL(Constants.BEATSAVER_BASE_URL + value.getAsString());
						} else if (BPM.equals(key)) {
							aMetaData.setBpm(value.getAsInt());
						} else if (DOWN_VOTES.equals(key)) {
							aMetaData.setDownVotes(value.getAsInt());
						} else if (UP_VOTES.equals(key)) {
							aMetaData.setUpVotes(value.getAsInt());
						} else if (RATING.equals(key)) {
							aMetaData.setRating(value.getAsFloat());
						} else if (HEAT.equals(key)) {
							aMetaData.setHeat(value.getAsFloat());
						} else if (DIRECT_DOWNLOAD.equals(key)) {
							aMetaData.setDirectDownload(value.getAsString());
						} else if (COVER_URL.equals(key)) {
							aMetaData.setCoverUrl(value.getAsString());
						} else if (DIFFICULTIES.equals(key)) {
							JsonObject difficulties = value.getAsJsonObject();
							for (Entry<String, JsonElement> difficultEntry : difficulties.entrySet()) {
								aMetaData.addDifficulty(difficultEntry.getKey());
							}
						} else {
							aMetaData = parse(value, aMetaData);
						}
					}
				}
			}
		}

		return aMetaData;
	}
}
