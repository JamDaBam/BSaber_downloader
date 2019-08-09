package bsaber.tools.bsaber_scrapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

public class BSaberSongScrapper {

	private static final Logger cvLogger = LogManager.getLogger(BSaberSongScrapper.class);

	private static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(Constants.CORE_SIZE);

	public static void main(String[] args) throws ParseException, InterruptedException {
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
			path = Tools.checkPath(cmd.getOptionValue(Constants.PARAMETER_PATH));
		} else {
			throw new MissingOptionException("Missing required option: " + Constants.PARAMETER_PATH);
		}

		// Process range of pages
		if (isPageRangeOption(cmd)) {
			String[] pageRange = cmd.getOptionValues(Constants.PARAMETER_PAGERANGE);

			Integer pageStart = null;
			Integer pageEnd = null;

			pageStart = Integer.parseInt(pageRange[0]);
			pageEnd = Integer.parseInt(pageRange[1]);

			downloadPages(path, pageStart, pageEnd);
		}
		// Process pages
		else if (isPageOption(cmd)) {
			String[] pageNumbersString = cmd.getOptionValues(Constants.PARAMETER_PAGE);
			int pageNumbersLength = pageNumbersString.length;
			int[] pageNumbersInt = new int[pageNumbersLength];

			for (int i = 0; i < pageNumbersLength; i++) {
				pageNumbersInt[i] = Integer.parseInt(pageNumbersString[i]);
			}

			downloadPages(path, pageNumbersInt);
		}
		// Procces songids
		else if (isSongIdOption(cmd)) {
			String[] songIds = cmd.getOptionValues(Constants.PARAMETER_SONGID);
			downloadSongs(path, songIds);
		} else {
			throw new IllegalArgumentException("False parameters " + Arrays.deepToString(args));
		}

		EXECUTOR.shutdown();
		EXECUTOR.awaitTermination(10, TimeUnit.DAYS);
		BSaberEntry.printNewSongs();
		BSaberEntry.printSongsWithErrors();
		System.exit(1);
	}

	public static void downloadPages(String aPath, int aPageStart, int aPageEnd) {
		if (aPageStart == 0) {
			throw new IllegalArgumentException("aFrom has to be greater than 0");
		}
		if (aPageStart > aPageEnd) {
			throw new IllegalArgumentException("aFrom <= aTo ----- " + aPageStart + " <= " + aPageEnd);
		}

		int maxPoolSize = aPageEnd - aPageStart + Constants.CORE_SIZE;
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

			String urlString = aPageNumber == 1 ? Constants.BSABER_BASE_SONGS_URL
					: Constants.BSABER_SONGS_PAGE_URL + aPageNumber;

			URL url;
			try {

				url = new URL(urlString);

				Document doc = Jsoup.parse(url, 30000);
				Elements songEntries = doc.select(Constants.QUERY_SONG_ENTRIES);

				List<BSaberEntry> downloadEntries = new ArrayList<>();

				for (Element songEntryElement : songEntries) {
					downloadEntries.add(new BSaberEntryRawData(songEntryElement).parse());
				}

				int downloaded = 0;

				cvLogger.debug(urlString + " downloadEntries --> " + downloadEntries.size());

				for (BSaberEntry bSaberEntry : downloadEntries) {
					if (bSaberEntry.download(aPath)) {
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

	public static void downloadSongs(String aPath, String... aSongIds) {
		if (aSongIds != null) {
			for (String aSongId : aSongIds) {
				downloadSong(aPath, aSongId);
			}
		}
	}

	private static void downloadSong(String aPath, String aSongId) {
		EXECUTOR.submit(() -> {
			long start = System.currentTimeMillis();

			String urlString = Constants.BSABER_BASE_SONGS_URL + aSongId;

			URL url;
			try {

				url = new URL(urlString);

				Document doc = Jsoup.parse(url, 30000);
				Elements songEntry = doc.select(Constants.QUERY_SONG_ENTRY);

				int downloaded = 0;

				for (Element songEntryElement : songEntry) {
					if (new BSaberEntryRawData(songEntryElement).parse().download(aPath)) {
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

	private static Options generateOptions() {
		Options options = new Options();

		Option optionHelp = new Option(Constants.PARAMETER_HELP, false, "Help");
		options.addOption(optionHelp);

		Option optionPageRange = new Option(Constants.PARAMETER_PAGERANGE, true,
				"Defines a range of pages. The startpage must be greater than zero and less than or equal to endpage.");
		optionPageRange.setOptionalArg(false);
		optionPageRange.setArgs(2);
		optionPageRange.setArgName("PAGESTART PAGEEND");
		options.addOption(optionPageRange);

		Option optionPage = new Option(Constants.PARAMETER_PAGE, true, "Defines pages to download.");
		optionPage.setArgs(Option.UNLIMITED_VALUES);
		optionPage.setArgName("PAGENUMBERS");
		optionPage.setOptionalArg(false);
		options.addOption(optionPage);

		Option optionSongId = new Option(Constants.PARAMETER_SONGID, true, "Defines songids to download");
		optionSongId.setArgs(Option.UNLIMITED_VALUES);
		optionSongId.setArgName("SONGIDS");
		optionSongId.setOptionalArg(false);
		options.addOption(optionSongId);

		Option optionPath = new Option(Constants.PARAMETER_PATH, true,
				"Defines the downloadfolder. If not set an absolute path then the tool creates the downloadfolder beside the executionpath.");
		optionPath.setArgName("DOWNLOADPATH [REQUIRED]");
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
		return hasOption(cmd, Constants.PARAMETER_SONGID)
				&& hasNotOption(cmd, Constants.PARAMETER_PAGE, Constants.PARAMETER_PAGERANGE, Constants.PARAMETER_HELP);
	}

	private static boolean isPageOption(CommandLine cmd) {
		return hasOption(cmd, Constants.PARAMETER_PAGE) && hasNotOption(cmd, Constants.PARAMETER_PAGERANGE,
				Constants.PARAMETER_SONGID, Constants.PARAMETER_HELP);
	}

	private static boolean isPageRangeOption(CommandLine cmd) {
		return hasOption(cmd, Constants.PARAMETER_PAGERANGE)
				&& hasNotOption(cmd, Constants.PARAMETER_PAGE, Constants.PARAMETER_SONGID, Constants.PARAMETER_HELP);
	}

	private static boolean isPathOption(CommandLine cmd) {
		return cmd.hasOption(Constants.PARAMETER_PATH);
	}

	private static boolean isHelpOption(CommandLine cmd) {
		return hasOption(cmd, Constants.PARAMETER_HELP) && hasNotOption(cmd, Constants.PARAMETER_PAGE,
				Constants.PARAMETER_PAGERANGE, Constants.PARAMETER_PATH, Constants.PARAMETER_SONGID);
	}
}
