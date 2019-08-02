package bsaber.tools.bsaber_scrapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BSaberSongScrapper {

	private static final Logger cvLogger = LogManager.getLogger(BSaberSongScrapper.class);

	private static final String BSABER_BASE_SONGS_URL = "https://bsaber.com/songs/";
	private static final String BSABER_BASE_DOWNLOAD_URL = "https://beatsaver.com/api/download/key/";
	private static final String BSABER_SONGS_PAGE_URL = "https://bsaber.com/songs/page/";

	private static final String QUERY_SONG_ENTRY = "header.post-title > h1";
	private static final String QUERY_SONG_ENTRIES = "h4 > a";
	private static final String QUERY_LINKS = "a.-download-zip";
	private static final String TAG_LINK = "href";

	private static final int CORE_SIZE = 3;
	private static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(CORE_SIZE);

	public static void main(String[] args) throws IOException, InterruptedException {
		BasicConfigurator.configure();

		downloadPages(0, 5);

		EXECUTOR.shutdown();
		EXECUTOR.awaitTermination(10, TimeUnit.DAYS);
		BSaberEntry.printNewSongs();
		BSaberEntry.printSongsWithErrors();
	}

	private static void downloadPages(int aFrom, int aTo) throws IOException, InterruptedException {
		if (aFrom > aTo) {
			throw new IllegalArgumentException("aFrom <= aTo " + aFrom + " <= " + aTo);
		}

		int maxPoolSize = aTo - aFrom + CORE_SIZE;
		EXECUTOR.setMaximumPoolSize(maxPoolSize);

		IntStream.range(aFrom, aTo + 1).forEach(aInt -> {
			downloadPage(aInt);
		});
	}

	private static void downloadPage(int aPageNumber) {
		EXECUTOR.submit(() -> {
			long start = System.currentTimeMillis();

			Map<String, String> songIdToName = new HashMap<>();
			Map<String, String> songIdToLink = new HashMap<>();

			String urlString = aPageNumber == 1 ? BSABER_BASE_SONGS_URL : BSABER_SONGS_PAGE_URL + aPageNumber;

			URL url;
			try {

				url = new URL(urlString);

				Document doc = Jsoup.parse(url, 30000);
				Elements songEntries = doc.select(QUERY_SONG_ENTRIES);
				Elements links = doc.select(QUERY_LINKS);

				// Map songID to songname
				for (Element element : songEntries) {
					String link = element.attr(TAG_LINK);
					if (link.contains(BSABER_BASE_SONGS_URL)) {
						String songId = extractID(link, BSABER_BASE_SONGS_URL);
						songIdToName.put(songId, element.text());
					}
				}

				// Map songId to downloadlink
				for (Element element : links) {
					String link = element.attr(TAG_LINK);
					String songId = extractID(link, BSABER_BASE_DOWNLOAD_URL);
					songIdToLink.put(songId, link);
				}

				int downloaded = 0;

				List<BSaberEntry> downloadEntries = getBSaberEntries(songIdToName, songIdToLink);

				cvLogger.debug(urlString + " downloadEntries --> " + downloadEntries.size());

				for (BSaberEntry bSaberEntry : downloadEntries) {
					if (bSaberEntry.download()) {
						downloaded++;
					}
				}

				long end = System.currentTimeMillis();
				cvLogger.debug("DONE " + urlString + " --> " + downloaded + " / " + "( " + BSaberEntry.getNewDownloads()
						+ "/" + BSaberEntry.getAlreadyDownloads() + "/" + BSaberEntry.getDownloadedTotal() + " )" + " ("
						+ (end - start) + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private static void downloadSongs(String... aSongIDs) {
		if (aSongIDs != null) {
			for (String aSongID : aSongIDs) {
				downloadSong(aSongID);
			}
		}
	}

	private static void downloadSong(String aSongID) {
		EXECUTOR.submit(() -> {
			long start = System.currentTimeMillis();

			Map<String, String> songIdToName = new HashMap<>();
			Map<String, String> songIdToLink = new HashMap<>();

			String urlString = BSABER_BASE_SONGS_URL + aSongID;

			URL url;
			try {

				url = new URL(urlString);

				Document doc = Jsoup.parse(url, 30000);
				Elements songEntries = doc.select(QUERY_SONG_ENTRY);
				Elements links = doc.select(QUERY_LINKS);

				// Map songID to songname
				for (Element element : songEntries) {
					songIdToName.put(aSongID, element.text());
				}

				// Map songId to downloadlink
				for (Element element : links) {
					String link = element.attr(TAG_LINK);
					String songId = extractID(link, BSABER_BASE_DOWNLOAD_URL);
					songIdToLink.put(songId, link);
				}

				int downloaded = 0;
				List<BSaberEntry> downloadEntries = getBSaberEntries(songIdToName, songIdToLink);
				cvLogger.debug(urlString + " downloadEntries --> " + downloadEntries.size());

				for (BSaberEntry bSaberEntry : downloadEntries) {
					if (bSaberEntry.download()) {
						downloaded++;
					}
				}

				long end = System.currentTimeMillis();
				cvLogger.debug("DONE " + urlString + " --> " + downloaded + " / " + "( " + BSaberEntry.getNewDownloads()
						+ "/" + BSaberEntry.getAlreadyDownloads() + "/" + BSaberEntry.getDownloadedTotal() + " )" + " ("
						+ (end - start) + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	private static List<BSaberEntry> getBSaberEntries(Map<String, String> songIdToName,
			Map<String, String> songIdToLink) {
		List<BSaberEntry> downloadEntries = new ArrayList<>();

		for (Entry<String, String> songEntry : songIdToName.entrySet()) {
			String songId = songEntry.getKey();

			String downloadLink = songIdToLink.get(songId);
			if (downloadLink != null) {
				String songName = songEntry.getValue();

				downloadEntries.add(new BSaberEntry(songId, songName, downloadLink));
			}
		}
		return downloadEntries;
	}

	private static String extractID(String aHref, String aCutString) {
		return aHref.replace(aCutString, "").replace("/", "");
	}
}
