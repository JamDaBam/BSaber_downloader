package bsaber.tools.bsaber_scrapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BSaberSongScrapper {

	private static Logger cvLogger = LogManager.getLogger(BSaberSongScrapper.class);

	private static final String BSABER_BASE_URL = "https://bsaber.com/songs/";
	private static final String BSABER_BASE_DOWNLOAD_URL = "https://beatsaver.com/api/download/key/";
	private static final String BSABER_URL = "https://bsaber.com/songs/page/";

	public static void main(String[] args) {
		BasicConfigurator.configure();

		try {
			long start = System.currentTimeMillis();
			List<BSaberEntry> bsaberEntries = BSaberSongScrapper.scrapePages(780);
			long end = System.currentTimeMillis();
			bsaberEntries.parallelStream().forEach(BSaberEntry::download);
			cvLogger.debug("DONE (" + (end - start) + ")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<BSaberEntry> scrapePages(int aAmountOfPages) throws IOException {
		List<BSaberEntry> bsaberEntries = new ArrayList<>();
		Map<String, String> songIdToName = new HashMap<>();
		Map<String, String> songIdToLink = new HashMap<>();

		IntStream.range(0, aAmountOfPages + 1).parallel().forEach(aInt -> {
			long start = System.currentTimeMillis();

			String urlString = aInt == 1 ? BSABER_BASE_URL : BSABER_URL + aInt;
			URL url;
			try {
				url = new URL(urlString);
				cvLogger.debug("PROCESS " + urlString);

				Document doc = Jsoup.parse(url, 30000);
				Elements songEntries = doc.select("h4 > a");
				Elements links = doc.select("a.-download-zip");

				// Map songID to songname
				for (Element element : songEntries) {
					String link = element.attr("href");
					if (link.contains(BSABER_BASE_URL)) {
						String songId = extractID(link, BSABER_BASE_URL);
						songIdToName.put(songId, element.text());
					}
				}

				// Map songId to downloadlink
				for (Element element : links) {
					String link = element.attr("href");
					String songId = extractID(link, BSABER_BASE_DOWNLOAD_URL);
					songIdToLink.put(songId, link);
				}

				long end = System.currentTimeMillis();
				cvLogger.debug("DONE " + urlString + " (" + (end - start) + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		for (Entry<String, String> songEntry : songIdToName.entrySet()) {
			String songId = songEntry.getKey();

			String downloadLink = songIdToLink.get(songId);
			if (downloadLink != null) {
				String songName = songEntry.getValue();

				bsaberEntries.add(new BSaberEntry(songId, songName, downloadLink));
			}
		}

		cvLogger.debug("Scrapped " + bsaberEntries.size() + " entries");

		return bsaberEntries;
	}

	private static String extractID(String aHref, String aCutString) {
		return aHref.replace(aCutString, "").replace("/", "");
	}
}
