package bsaber.tools.bsaber_scrapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

	private static final String PARAMETER_HELP = "-h";
	private static final String PARAMETER_PAGEFROM = "-pagefrom";
	private static final String PARAMETER_PAGETO = "-pageto";
	private static final String PARAMETER_PAGE = "-page";
	private static final String PARAMETER_SONGID = "-songid";
	private static final String PARAMETER_PATH = "-path";

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

		// TODO Find better solution to check parameters
		// Checking parameters
		int argsLength = args.length;
		if (argsLength > 0) {
			if (PARAMETER_HELP.equalsIgnoreCase(args[0])) {
				StringBuilder sb = new StringBuilder();
				sb.append("Download a range of pages:\t" + PARAMETER_PATH + " [SAVEPATH] " + PARAMETER_PAGEFROM
						+ " [ > 0] " + PARAMETER_PAGETO + " [ <= pagefrom]\n");
				sb.append("Download a page:\t\t" + PARAMETER_PATH + " [SAVEPATH] " + PARAMETER_PAGE + " [ > 0 ]\n");
				sb.append("Download a specific songs:\t" + PARAMETER_PATH + " [SAVEPATH] " + PARAMETER_SONGID + " [ SONGID_1 SONGID_n ]\n");
				System.out.println(sb.toString());
			} else if (!PARAMETER_SONGID.equalsIgnoreCase(args[2]) && argsLength > 6) {
				throw new IllegalArgumentException("To many parameters set");
			} else if (argsLength == 6 && !PARAMETER_SONGID.equalsIgnoreCase(args[2])) {
				Integer pageFrom = null;
				Integer pageTo = null;
				String path = null;

				// Checking and get path parameter
				String pathParameter = args[0];
				if (PARAMETER_PATH.equalsIgnoreCase(pathParameter)) {
					path = checkPath(args[1]);
				} else {
					throw new IllegalArgumentException("First parameter should be -path");
				}

				// Checking and get pagefrom parameter
				String pageFromParameter = args[2];
				if (PARAMETER_PAGEFROM.equalsIgnoreCase(pageFromParameter)) {
					pageFrom = Integer.parseInt(args[3]);
				} else {
					throw new IllegalArgumentException("Second parameter should be -pagefrom");
				}

				// Checking and get pageto parameter
				String pageToParameter = args[4];
				if (PARAMETER_PAGETO.equalsIgnoreCase(pageToParameter)) {
					pageTo = Integer.parseInt(args[5]);
				} else {
					throw new IllegalArgumentException("Third parameter should be -pageto");
				}

				cvLogger.debug("path=" + path + " from=" + pageFrom + " to=" + pageTo);
				downloadPages(pageFrom, pageTo, path);

			} else if (argsLength == 4) {
				Integer page = null;
				String songId = null;
				String path = null;

				// Checking and get path parameter
				String pathParameter = args[0];
				if (PARAMETER_PATH.equalsIgnoreCase(pathParameter)) {
					path = checkPath(args[1]);
				} else {
					throw new IllegalArgumentException("First parameter should be -path");
				}

				// Checking and get pagefrom parameter
				String secondParameter = args[2];
				if (PARAMETER_PAGE.equalsIgnoreCase(secondParameter)) {
					page = Integer.parseInt(args[3]);
				} else if (PARAMETER_SONGID.equalsIgnoreCase(secondParameter)) {
					songId = args[3];
				} else {
					throw new IllegalArgumentException("Second parameter should be -page or -songid");
				}

				if (page == null) {
					cvLogger.debug("path=" + path + " songid=" + songId);
					downloadSong(songId, path);
				} else {
					cvLogger.debug("path=" + path + " page=" + page);
					downloadPage(page, path);
				}
			} else if (argsLength > 4 && PARAMETER_SONGID.equalsIgnoreCase(args[2])) {
				String[] songIds = new String[argsLength - 3];
				String path = null;

				// Checking and get path parameter
				String pathParameter = args[0];
				if (PARAMETER_PATH.equalsIgnoreCase(pathParameter)) {
					path = checkPath(args[1]);
				} else {
					throw new IllegalArgumentException("First parameter should be -path");
				}

				int songIdStart = 3;

				// Checking and get path parameter
				for (int i = songIdStart; i < argsLength; i++) {
					String songId = args[i];

					if (songId.contains("-")) {
						throw new IllegalArgumentException("False parameters");
					} else {
						songIds[i - songIdStart] = songId;
					}
				}

				cvLogger.debug("path=" + path + " songid=" + Arrays.deepToString(songIds));
				downloadSongs(path, songIds);

			} else {
				throw new IllegalArgumentException("False parameters");
			}
		}

		EXECUTOR.shutdown();
		EXECUTOR.awaitTermination(10, TimeUnit.DAYS);
		BSaberEntry.printNewSongs();
		BSaberEntry.printSongsWithErrors();
	}

	private static void downloadPages(int aFrom, int aTo, String aPath) throws IOException, InterruptedException {
		if (aFrom == 0) {
			throw new IllegalArgumentException("aFrom has to be greater than 0");
		}
		if (aFrom > aTo) {
			throw new IllegalArgumentException("aFrom <= aTo ----- " + aFrom + " <= " + aTo);
		}

		int maxPoolSize = aTo - aFrom + CORE_SIZE;
		EXECUTOR.setMaximumPoolSize(maxPoolSize);

		IntStream.range(aFrom, aTo + 1).forEach(aInt -> {
			downloadPage(aInt, aPath);
		});
	}

	private static void downloadPage(int aPageNumber, String aPath) {
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

				List<BSaberEntry> downloadEntries = getBSaberEntries(songIdToName, songIdToLink, aPath);

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

	private static void downloadSongs(String aPath, String... aSongIDs) {
		if (aSongIDs != null) {
			for (String aSongID : aSongIDs) {
				downloadSong(aSongID, aPath);
			}
		}
	}

	private static void downloadSong(String aSongID, String aPath) {
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
				List<BSaberEntry> downloadEntries = getBSaberEntries(songIdToName, songIdToLink, aPath);
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
			Map<String, String> songIdToLink, String aPath) {
		List<BSaberEntry> downloadEntries = new ArrayList<>();

		for (Entry<String, String> songEntry : songIdToName.entrySet()) {
			String songId = songEntry.getKey();

			String downloadLink = songIdToLink.get(songId);
			if (downloadLink != null) {
				String songName = songEntry.getValue();

				downloadEntries.add(new BSaberEntry(songId, songName, downloadLink, aPath));
			}
		}
		return downloadEntries;
	}

	private static String extractID(String aHref, String aCutString) {
		return aHref.replace(aCutString, "").replace("/", "");
	}

	private static String checkPath(String aPath) {
		String path = aPath;

		char lastChar = path.charAt(path.length() - 1);
		if (lastChar != '\\') {
			path = path + "\\";
		}

		return path;
	}

}
