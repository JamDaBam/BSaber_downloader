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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//TODO Das muss refactored werden
public class BSaberSongScrapper {

	private static final Logger cvLogger = LogManager.getLogger(BSaberSongScrapper.class);

	private static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(BSaberScrapperConstants.CORE_SIZE);

	public static void main(String[] args) throws IOException, InterruptedException, ParseException {
		BasicConfigurator.configure();

		Options options = generateOptions();

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		// Process help
		if (args.length == 0 || isHelpOption(cmd)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Parameters", options);
			System.exit(1);
		}

		// Process path
		String path;
		if (isPathOption(cmd)) {
			path = checkPath(cmd.getOptionValue(BSaberScrapperConstants.PARAMETER_PATH));
		} else {
			throw new MissingOptionException("Missing required option: " + BSaberScrapperConstants.PARAMETER_PATH);
		}

		// Process range of pages
		if (isPageRangeOption(cmd)) {
			String[] pageRange = cmd.getOptionValues(BSaberScrapperConstants.PARAMETER_PAGERANGE);

			Integer pageStart = null;
			Integer pageEnd = null;

			pageStart = Integer.parseInt(pageRange[0]);
			pageEnd = Integer.parseInt(pageRange[1]);

			downloadPages(path, pageStart, pageEnd);
		}
		// Process pages
		else if (isPageOption(cmd)) {
			String[] pageNumbersString = cmd.getOptionValues(BSaberScrapperConstants.PARAMETER_PAGE);
			int pageNumbersLength = pageNumbersString.length;
			int[] pageNumbersInt = new int[pageNumbersLength];

			for (int i = 0; i < pageNumbersLength; i++) {
				pageNumbersInt[i] = Integer.parseInt(pageNumbersString[i]);
			}

			downloadPages(path, pageNumbersInt);
		}
		// Procces songids
		else if (isSongIdOption(cmd)) {
			String[] songIds = cmd.getOptionValues(BSaberScrapperConstants.PARAMETER_SONGID);
			downloadSongs(path, songIds);
		} else {
			throw new IllegalArgumentException("Tow many parameters set " + Arrays.deepToString(args));
		}

		EXECUTOR.shutdown();
		EXECUTOR.awaitTermination(10, TimeUnit.DAYS);
		BSaberEntry.printNewSongs();
		BSaberEntry.printSongsWithErrors();
		System.exit(1);
	}

	public static void downloadPages(String aPath, int aPageStart, int aPageEnd)
			throws IOException, InterruptedException {
		if (aPageStart == 0) {
			throw new IllegalArgumentException("aFrom has to be greater than 0");
		}
		if (aPageStart > aPageEnd) {
			throw new IllegalArgumentException("aFrom <= aTo ----- " + aPageStart + " <= " + aPageEnd);
		}

		int maxPoolSize = aPageEnd - aPageStart + BSaberScrapperConstants.CORE_SIZE;
		EXECUTOR.setMaximumPoolSize(maxPoolSize);

		IntStream.range(aPageStart, aPageEnd + 1).forEach(aPageNumber -> {
			downloadPage(aPath, aPageNumber);
		});
	}

	public static void downloadPages(String aPath, int... aPageNumbers) {
		for (int pageNumber : aPageNumbers) {
			downloadPage(aPath, pageNumber);
		}
	}

	public static void downloadPage(String aPath, int aPageNumber) {
		EXECUTOR.submit(() -> {
			long start = System.currentTimeMillis();

			Map<String, String> songIdToName = new HashMap<>();
			Map<String, String> songIdToLink = new HashMap<>();

			String urlString = aPageNumber == 1 ? BSaberScrapperConstants.BSABER_BASE_SONGS_URL
					: BSaberScrapperConstants.BSABER_SONGS_PAGE_URL + aPageNumber;

			URL url;
			try {

				url = new URL(urlString);

				Document doc = Jsoup.parse(url, 30000);
				Elements songEntries = doc.select(BSaberScrapperConstants.QUERY_SONG_ENTRIES);
				Elements links = doc.select(BSaberScrapperConstants.QUERY_LINKS);

				// Map songID to songname
				for (Element element : songEntries) {
					String link = element.attr(BSaberScrapperConstants.TAG_LINK);
					if (link.contains(BSaberScrapperConstants.BSABER_BASE_SONGS_URL)) {
						String songId = extractID(link, BSaberScrapperConstants.BSABER_BASE_SONGS_URL);
						songIdToName.put(songId, element.text());
					}
				}

				// Map songId to downloadlink
				for (Element element : links) {
					String link = element.attr(BSaberScrapperConstants.TAG_LINK);
					String songId = extractID(link, BSaberScrapperConstants.BSABER_BASE_DOWNLOAD_URL);
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

	public static void downloadSongs(String aPath, String... aSongIDs) {
		if (aSongIDs != null) {
			for (String aSongID : aSongIDs) {
				downloadSong(aPath, aSongID);
			}
		}
	}

	private static void downloadSong(String aPath, String aSongID) {
		EXECUTOR.submit(() -> {
			long start = System.currentTimeMillis();

			Map<String, String> songIdToName = new HashMap<>();
			Map<String, String> songIdToLink = new HashMap<>();

			String urlString = BSaberScrapperConstants.BSABER_BASE_SONGS_URL + aSongID;

			URL url;
			try {

				url = new URL(urlString);

				Document doc = Jsoup.parse(url, 30000);
				Elements songEntries = doc.select(BSaberScrapperConstants.QUERY_SONG_ENTRY);
				Elements links = doc.select(BSaberScrapperConstants.QUERY_LINKS);

				// Map songID to songname
				for (Element element : songEntries) {
					songIdToName.put(aSongID, element.text());
				}

				// Map songId to downloadlink
				for (Element element : links) {
					String link = element.attr(BSaberScrapperConstants.TAG_LINK);
					String songId = extractID(link, BSaberScrapperConstants.BSABER_BASE_DOWNLOAD_URL);
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

	private static String extractID(String aHref, String aCutString) {
		return aHref.replace(aCutString, "").replace("/", "");
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

	private static String checkPath(String aPath) {
		String path = aPath;

		char lastChar = path.charAt(path.length() - 1);
		if (lastChar != '\\') {
			path = path + "\\";
		}

		return path;
	}

	private static Options generateOptions() {
		Options options = new Options();

		Option optionHelp = new Option(BSaberScrapperConstants.PARAMETER_HELP, false, "Help");
		options.addOption(optionHelp);

		Option optionPageRange = new Option(BSaberScrapperConstants.PARAMETER_PAGERANGE, true,
				"Defines a range of pages. The startpage must be greater than zero and less than or equal to endpage.");
		optionPageRange.setOptionalArg(false);
		optionPageRange.setArgs(2);
		optionPageRange.setArgName("PAGESTART PAGEEND");
		options.addOption(optionPageRange);

		Option optionPage = new Option(BSaberScrapperConstants.PARAMETER_PAGE, true, "Defines pages to download.");
		optionPage.setArgs(Option.UNLIMITED_VALUES);
		optionPage.setArgName("PAGENUMBERS");
		optionPage.setOptionalArg(false);
		options.addOption(optionPage);

		Option optionSongId = new Option(BSaberScrapperConstants.PARAMETER_SONGID, true, "Defines songids to download");
		optionSongId.setArgs(Option.UNLIMITED_VALUES);
		optionSongId.setArgName("SONGIDS");
		optionSongId.setOptionalArg(false);
		options.addOption(optionSongId);

		Option optionPath = new Option(BSaberScrapperConstants.PARAMETER_PATH, true,
				"Defines the downloadfolder. If not set an absolute path then the tool creates the downloadfolder beside the executionpath.");
		optionPath.setArgName("DOWNLOADPATH");
		optionPath.setOptionalArg(false);
		options.addOption(optionPath);

		return options;
	}

	private static boolean hasOption(CommandLine aCmd, String... aOptions) {
		boolean res = true;

		for (String option : aOptions) {
			if (res) {
				res = aCmd.hasOption(option);
			} else {
				break;
			}
		}

		return res;
	}

	private static boolean hasNotOption(CommandLine aCmd, String... aOptions) {
		boolean res = true;

		for (String option : aOptions) {
			if (res) {
				res = !aCmd.hasOption(option);
			} else {
				break;
			}
		}

		return res;
	}

	private static boolean isSongIdOption(CommandLine cmd) {
		return hasOption(cmd, BSaberScrapperConstants.PARAMETER_SONGID)
				&& hasNotOption(cmd, BSaberScrapperConstants.PARAMETER_PAGE,
						BSaberScrapperConstants.PARAMETER_PAGERANGE, BSaberScrapperConstants.PARAMETER_HELP);
	}

	private static boolean isPageOption(CommandLine cmd) {
		return hasOption(cmd, BSaberScrapperConstants.PARAMETER_PAGE)
				&& hasNotOption(cmd, BSaberScrapperConstants.PARAMETER_PAGERANGE,
						BSaberScrapperConstants.PARAMETER_SONGID, BSaberScrapperConstants.PARAMETER_HELP);
	}

	private static boolean isPageRangeOption(CommandLine cmd) {
		return hasOption(cmd, BSaberScrapperConstants.PARAMETER_PAGERANGE)
				&& hasNotOption(cmd, BSaberScrapperConstants.PARAMETER_PAGE, BSaberScrapperConstants.PARAMETER_SONGID,
						BSaberScrapperConstants.PARAMETER_HELP);
	}

	private static boolean isPathOption(CommandLine cmd) {
		return cmd.hasOption(BSaberScrapperConstants.PARAMETER_PATH);
	}

	private static boolean isHelpOption(CommandLine cmd) {
		return hasOption(cmd, BSaberScrapperConstants.PARAMETER_HELP) && hasNotOption(cmd,
				BSaberScrapperConstants.PARAMETER_PAGE, BSaberScrapperConstants.PARAMETER_PAGERANGE,
				BSaberScrapperConstants.PARAMETER_PATH, BSaberScrapperConstants.PARAMETER_SONGID);
	}
}
