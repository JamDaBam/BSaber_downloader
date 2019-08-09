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

	private int ivThumbsUp = 0;
	private int ivThumbsDown = 0;

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
						ivSongId = Tools.extractID(child.attr("href"), Constants.BSABER_BASE_SONGS_URL);
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
				}

				if (element.hasClass("fa-thumbs-down")) {
					ivThumbsDown = Integer.parseInt(element.parent().text());
				}

				if (element.hasClass("-download-zip")) {
					ivDownloadUrl = element.attr("href");

					// Singleentry
					if (ivSongId == null) {
						ivSongId = Tools.extractID(ivDownloadUrl, Constants.BSABER_BASE_DOWNLOAD_URL);
					}
				}

				parse(element.children());
			}
		}
	}

	@Override
	public String toString() {
		return ivSongId + " - " + ivMapper + " - " + ivTitle + Tools.difficultiesToString(ivDifficulties) + " Upvote: "
				+ ivThumbsUp + " Downvote: " + ivThumbsDown + " --> " + ivDownloadUrl;
	}
}
