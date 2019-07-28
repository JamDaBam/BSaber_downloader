package bsaber.tools.bsaber_scrapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BSaberLinkScrapper {

	private static Logger cvLogger = LogManager.getLogger(BSaberLinkScrapper.class);

	private static final String BSABER_BASE_URL = "https://bsaber.com/songs/";
	private static final String BSABER_BASE_DOWNLOAD_URL = "https://beatsaver.com/api/download/key/";
	private static final String BSABER_URL = "https://bsaber.com/songs/page/";

	public static void main(String[] args) {
		BasicConfigurator.configure();

		try {
			BSaberLinkScrapper bs = new BSaberLinkScrapper(10);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BSaberLinkScrapper(int aAmountOfPages) throws IOException {
		List<BSaberEntry> bsaberEntries = new ArrayList<>();

		for (int i = 1; i <= aAmountOfPages; i++) {
			long start = System.currentTimeMillis();

			String urlString = i == 1 ? BSABER_BASE_URL : BSABER_URL + i;
			URL url = new URL(urlString);
			cvLogger.debug("process " + urlString);

			Document doc = Jsoup.parse(url, 10000);
			Elements songEntries = doc.select("h4 > a");
			Elements links = doc.select("a.-download-zip");

			Map<String, String> songIdToName = new HashMap<>();
			Map<String, String> songIdToLink = new HashMap<>();

			// Map songID to songname
			for (Element element : songEntries) {
				String href = element.attr("href");
				if (href.contains(BSABER_BASE_URL)) {
					String songId = href.replace(BSABER_BASE_URL, "").replace("/", "");
					songIdToName.put(songId, element.text());
				}
			}

			// Map songId to downloadlink
			for (Element linkElement : links) {
				String link = linkElement.attr("href");
				String songId = link.replace(BSABER_BASE_DOWNLOAD_URL, "").replace("/", "");
				songIdToLink.put(songId, link);
			}

			for (Entry<String, String> songEntry : songIdToName.entrySet()) {
				String songId = songEntry.getKey();

				String downloadLink = songIdToLink.get(songId);
				if (downloadLink != null) {
					String songName = songEntry.getValue();

					bsaberEntries.add(new BSaberEntry(songId, songName, downloadLink));
				}
			}

			long end = System.currentTimeMillis();
			cvLogger.debug(end - start);
		}

		cvLogger.debug("");
		cvLogger.debug("");
		cvLogger.debug("BSaberEntries");
		for (BSaberEntry bSaberEntry : bsaberEntries) {
			cvLogger.debug(bSaberEntry.toString());
		}
		cvLogger.debug("Scrapped " + bsaberEntries.size() + " entries");
	}
}
