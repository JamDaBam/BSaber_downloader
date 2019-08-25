package bsaber.tools.bsaber_scrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BSaberParser {
	private static final Logger cvLogger = LogManager.getLogger(BSaberParser.class);

	// Html tags
	private static final String TAG_MAPPER = "mapper_id vcard";
	private static final String TAG_TITLE = "entry-title";
	private static final String TAG_LINK = "href";
	private static final String TAG_DIFFICULTY = "post-difficulty";
	private static final String TAG_UPVOTES = "fa-thumbs-up";
	private static final String TAG_DOWNVOTES = "fa-thumbs-down";
	private static final String TAG_DOWNLOADURL = "-download-zip";

	public static List<SongEntry> parse(Elements aSongEntryElements) {
		List<SongEntry> songEntries = new ArrayList<>();

		for (Element songEntryElement : aSongEntryElements) {
			songEntries.add(parse(songEntryElement));
		}

		return songEntries;
	}

	private static SongEntry parse(Element aSongEntryElement) {
		return new SongEntry(parse(Arrays.asList(aSongEntryElement), new SongMetaData()));
	}

	private static SongMetaData parse(List<Element> aElements, SongMetaData aMetaData) {
		if (aElements != null) {
			for (Element element : aElements) {
				if (element.hasClass(TAG_MAPPER)) {
					aMetaData.setLevelAuthorName(element.text());
				}

				if (element.hasClass(TAG_TITLE)) {
					// Pageentries
					if (!Tools.isNullOrEmpty(element.children())) {
						Element child = element.child(0);
						aMetaData.setSongName(child.text());
						aMetaData.setKey(extractKey(child.attr(TAG_LINK), Constants.BSABER_BASE_SONGS_URL));
					}
					// Singleentry
					else {
						aMetaData.setSongName(element.text());
					}
				}

				if (element.hasClass(TAG_DIFFICULTY)) {
					aMetaData.addDifficulty(element.text());
				}

				if (element.hasClass(TAG_UPVOTES)) {
					aMetaData.setUpVotes(Integer.parseInt(element.parent().text()));
				}

				if (element.hasClass(TAG_DOWNVOTES)) {
					aMetaData.setDownVotes(Integer.parseInt(element.parent().text()));
				}

				if (element.hasClass(TAG_DOWNLOADURL)) {
					aMetaData.setDownloadURL(element.attr(TAG_LINK));

					// Singleentry
					if (aMetaData.getKey() == null) {
						aMetaData.setKey(extractKey(aMetaData.getDownloadURL(), Constants.BSABER_BASE_DOWNLOAD_URL));
					}
				}

				aMetaData = parse(element.children(), aMetaData);
			}
		}

		return aMetaData;
	}

	private static String extractKey(String aString, String aCutString) {
		return aString.replace(aCutString, "").replace("/", "");
	}
}
