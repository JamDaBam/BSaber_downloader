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

		Options options = Tools.CommandLineTools.generateOptions();

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		// Process help
		if (args.length == 0 || Tools.CommandLineTools.isHelpOption(cmd)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Parameters", options);
			System.exit(1);
		}

		// Process path
		String path;
		if (Tools.CommandLineTools.isPathOption(cmd)) {
			path = Tools.checkPath(cmd.getOptionValue(Constants.PARAMETER_PATH));
		} else {
			throw new MissingOptionException("Missing required option: " + Constants.PARAMETER_PATH);
		}

		// Process ratio
		Float ratio = null;
		if (Tools.CommandLineTools.isRatioOption(cmd)) {
			ratio = Float.parseFloat(cmd.getOptionValue(Constants.PARAMETER_RATIO));
		}

		// Process range of pages
		if (Tools.CommandLineTools.isPageRangeOption(cmd)) {
			String[] pageRange = cmd.getOptionValues(Constants.PARAMETER_PAGERANGE);

			Integer pageStart = null;
			Integer pageEnd = null;

			pageStart = Integer.parseInt(pageRange[0]);
			pageEnd = Integer.parseInt(pageRange[1]);

			int[] pages = processPageRange(pageStart, pageEnd);
			downloadPages(path, ratio, pages);
		}
		// Process pages
		else if (Tools.CommandLineTools.isPageOption(cmd)) {
			String[] pageNumbersString = cmd.getOptionValues(Constants.PARAMETER_PAGE);
			int pageNumbersLength = pageNumbersString.length;
			int[] pageNumbersInt = new int[pageNumbersLength];

			for (int i = 0; i < pageNumbersLength; i++) {
				pageNumbersInt[i] = Integer.parseInt(pageNumbersString[i]);
			}

			downloadPages(path, ratio, pageNumbersInt);
		}
		// Procces songids
		else if (Tools.CommandLineTools.isSongIdOption(cmd)) {
			String[] songIds = cmd.getOptionValues(Constants.PARAMETER_SONGID);
			downloadSongs(path, ratio, songIds);
		} else {
			throw new IllegalArgumentException("False parameters " + Arrays.deepToString(args));
		}

		EXECUTOR.shutdown();
		EXECUTOR.awaitTermination(10, TimeUnit.DAYS);
		BSaberEntry.printNewSongs();
		BSaberEntry.printSongsWithErrors();
		System.exit(1);
	}

	private static int[] processPageRange(Integer aPageStart, Integer aPageEnd) {
		if (aPageStart == 0) {
			throw new IllegalArgumentException("aFrom has to be greater than 0");
		}
		if (aPageStart > aPageEnd) {
			throw new IllegalArgumentException("aFrom <= aTo ----- " + aPageStart + " <= " + aPageEnd);
		}

		int maxPoolSize = aPageEnd - aPageStart + Constants.CORE_SIZE;
		EXECUTOR.setMaximumPoolSize(maxPoolSize);

		return IntStream.range(aPageStart, aPageEnd + 1).toArray();
	}

	public static void downloadPages(String aPath, Float aRatio, int... aPageNumbers) {
		for (int pageNumber : aPageNumbers) {
			downloadPage(aPath, aRatio, pageNumber);
		}
	}

	public static void downloadPage(String aPath, Float aRatio, int aPageNumber) {
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
					BSaberEntry bSaberEntry = new BSaberEntryRawData(songEntryElement).parse();

					if (checkBSaberRatio(aRatio, bSaberEntry)) {
						downloadEntries.add(bSaberEntry);
					} else {
						cvLogger.debug("Skip by ratio: " + aRatio + " > " + bSaberEntry.getRatio() + " - "
								+ bSaberEntry.getSongId() + " - " + bSaberEntry.getTitle());
					}
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

	public static void downloadSongs(String aPath, Float aRatio, String... aSongIds) {
		if (aSongIds != null) {
			for (String aSongId : aSongIds) {
				downloadSong(aPath, aRatio, aSongId);
			}
		}
	}

	private static void downloadSong(String aPath, Float aRatio, String aSongId) {
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
					BSaberEntry bSaberEntry = new BSaberEntryRawData(songEntryElement).parse();

					if (checkBSaberRatio(aRatio, bSaberEntry)) {
						if (bSaberEntry.download(aPath)) {
							downloaded++;
						}
					} else {
						cvLogger.debug("Skip by ratio: " + aRatio + " > " + bSaberEntry.getRatio() + " - "
								+ bSaberEntry.getSongId() + " - " + bSaberEntry.getTitle());
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

	private static boolean checkBSaberRatio(Float aRatio, BSaberEntry aBSaberEntry) {
		return aRatio == null || aRatio <= aBSaberEntry.getRatio();
	}
}
