package bsaber.tools.bsaber_scrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

public class BSaberEntryRawData {
	private static final Logger cvLogger = LogManager.getLogger(BSaberEntryRawData.class);

	private Element ivElement;
	private String ivMapper;
	private String ivTitle;
	private String ivSongId;
	private String ivDownloadUrl;

	private int ivThumbs = 0;
	private int ivThumbsUp = 0;
	private int ivThumbsDown = 0;
	private float ivRatio = 0;

	private List<String> ivDifficulties = new ArrayList<>();

	public BSaberEntryRawData(Element aSongEntryElement) {
		ivElement = aSongEntryElement;
	}

	public BSaberEntry parse() {
		parse(Arrays.asList(ivElement));
		cvLogger.debug(toString());
		return getBSaberEntry();
	}

	private BSaberEntry getBSaberEntry() {
		BSaberEntry entry = new BSaberEntry(this);
		entry.setMapper(ivMapper);
		entry.setTitle(ivTitle);
		entry.setSongId(ivSongId);
		entry.setDownloadUrl(ivDownloadUrl);
		entry.setThumbsDown(ivThumbsDown);
		entry.setThumbsUp(ivThumbsUp);
		entry.setThumbs(ivThumbs);
		entry.setRatio(ivRatio);
		entry.setDifficulties(ivDifficulties);
		return entry;
	}

	private void parse(List<Element> aElements) {
		if (aElements != null) {
			for (Element element : aElements) {
				if (element.hasClass("mapper_id vcard")) {
					ivMapper = element.text();
				}

				if (element.hasClass("entry-title")) {
					// Pageentries
					if (!Tools.isNullOrEmpty(element.children())) {
						Element child = element.child(0);
						ivTitle = child.text();
						ivSongId = extractID(child.attr("href"), Constants.BSABER_BASE_SONGS_URL);
					}
					// Singleentry
					else {
						ivTitle = element.text();
					}
				}

				if (element.hasClass("post-difficulty")) {
					ivDifficulties.add(element.text());
				}

				if (element.hasClass("fa-thumbs-up")) {
					ivThumbsUp = Integer.parseInt(element.parent().text());
					ivThumbs += ivThumbsUp;
					calcRatio();
				}

				if (element.hasClass("fa-thumbs-down")) {
					ivThumbsDown = Integer.parseInt(element.parent().text());
					ivThumbs += ivThumbsDown;
					calcRatio();
				}

				if (element.hasClass("-download-zip")) {
					ivDownloadUrl = element.attr("href");

					// Singleentry
					if (ivSongId == null) {
						ivSongId = extractID(ivDownloadUrl, Constants.BSABER_BASE_DOWNLOAD_URL);
					}
				}

				parse(element.children());
			}
		}
	}

	private String extractID(String aHref, String aCutString) {
		return aHref.replace(aCutString, "").replace("/", "");
	}

	private void calcRatio() {
		if (ivThumbs > 0) {
			ivRatio = (float) ivThumbsUp / (float) ivThumbs;
		}
	}

	@Override
	public String toString() {
		return ivSongId + " - " + ivMapper + " - " + ivTitle + Tools.difficultiesToString(ivDifficulties) + " Upvote: "
				+ ivThumbsUp + " Downvote: " + ivThumbsDown + " Votes: " + ivThumbs + " Ratio: " + ivRatio + " --> "
				+ ivDownloadUrl;
	}
}
