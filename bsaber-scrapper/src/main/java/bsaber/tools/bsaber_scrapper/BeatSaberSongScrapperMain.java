package bsaber.tools.bsaber_scrapper;

import java.util.Arrays;
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

public class BeatSaberSongScrapperMain {
	// Parameters
	private static final String PARAMETER_HELP = "h";
	private static final String PARAMETER_PAGERANGE = "pagerange";
	private static final String PARAMETER_PAGE = "page";
	private static final String PARAMETER_SONGID = "songid";
	private static final String PARAMETER_PATH = "path";
	private static final String PARAMETER_RATIO = "ratio";

	public static void main(String[] args) throws ParseException, InterruptedException {
		BasicConfigurator.configure();

		Options options = generateOptions();

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		SongScrapper songScrapper = new BeatSaverSongScrapper();

		// Process help
		if (args.length == 0 || isHelpOption(cmd)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Parameters", options);
			System.exit(1);
		}

		// Process path
		String path;
		if (isPathOption(cmd)) {
			path = checkPath(cmd.getOptionValue(PARAMETER_PATH));
		} else {
			throw new MissingOptionException("Missing required option: " + PARAMETER_PATH);
		}

		// Process ratio
		Float ratio = null;
		if (isRatioOption(cmd)) {
			ratio = Float.parseFloat(cmd.getOptionValue(PARAMETER_RATIO));
		}

		// Process range of pages
		if (isPageRangeOption(cmd)) {
			String[] pageRange = cmd.getOptionValues(PARAMETER_PAGERANGE);

			Integer pageStart = null;
			Integer pageEnd = null;

			pageStart = Integer.parseInt(pageRange[0]);
			pageEnd = Integer.parseInt(pageRange[1]);

			int[] pages = processPageRange(pageStart, pageEnd);
			songScrapper.downloadPages(path, ratio, pages);
		}
		// Process pages
		else if (isPageOption(cmd)) {
			String[] pageNumbersString = cmd.getOptionValues(PARAMETER_PAGE);
			int pageNumbersLength = pageNumbersString.length;
			int[] pageNumbersInt = new int[pageNumbersLength];

			for (int i = 0; i < pageNumbersLength; i++) {
				pageNumbersInt[i] = Integer.parseInt(pageNumbersString[i]);
			}

			songScrapper.downloadPages(path, ratio, pageNumbersInt);
		}
		// Procces songids
		else if (isSongIdOption(cmd)) {
			String[] songIds = cmd.getOptionValues(PARAMETER_SONGID);
			songScrapper.downloadSongs(path, ratio, songIds);
		} else {
			throw new IllegalArgumentException("False parameters " + Arrays.deepToString(args));
		}

		Constants.EXECUTOR.shutdown();
		Constants.EXECUTOR.awaitTermination(10, TimeUnit.DAYS);
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
		Constants.EXECUTOR.setMaximumPoolSize(maxPoolSize);

		return IntStream.range(aPageStart, aPageEnd + 1).toArray();
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

		Option optionHelp = new Option(PARAMETER_HELP, false, "Help");
		options.addOption(optionHelp);

		Option optionPageRange = new Option(PARAMETER_PAGERANGE, true,
				"Defines a range of pages. The startpage must be greater than zero and less than or equal to endpage.");
		optionPageRange.setArgName("PAGESTART PAGEEND");
		optionPageRange.setOptionalArg(false);
		optionPageRange.setArgs(2);
		options.addOption(optionPageRange);

		Option optionPage = new Option(PARAMETER_PAGE, true, "Defines pages to download.");
		optionPage.setArgName("PAGENUMBERS");
		optionPage.setOptionalArg(false);
		optionPage.setArgs(Option.UNLIMITED_VALUES);
		options.addOption(optionPage);

		Option optionSongId = new Option(PARAMETER_SONGID, true, "Defines songids to download");
		optionSongId.setArgName("SONGIDS");
		optionSongId.setOptionalArg(false);
		optionSongId.setArgs(Option.UNLIMITED_VALUES);
		options.addOption(optionSongId);

		Option optionPath = new Option(PARAMETER_PATH, true,
				"Defines the downloadfolder. If not set an absolute path then the tool creates the downloadfolder beside the executionpath.");
		optionPath.setArgName("DOWNLOADPATH [REQUIRED]");
		optionPath.setOptionalArg(false);
		optionPath.setArgs(1);
		options.addOption(optionPath);

		Option optionRatio = new Option(PARAMETER_RATIO, "Defines a thumbs up ratio between 0.0 to 1.0.");
		optionRatio.setArgName("RATIO");
		optionRatio.setOptionalArg(false);
		optionRatio.setArgs(1);
		options.addOption(optionRatio);

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

	private static boolean isSongIdOption(CommandLine aCmd) {
		return hasOption(aCmd, PARAMETER_SONGID)
				&& hasNotOption(aCmd, PARAMETER_PAGE, PARAMETER_PAGERANGE, PARAMETER_HELP);
	}

	private static boolean isPageOption(CommandLine aCmd) {
		return hasOption(aCmd, PARAMETER_PAGE)
				&& hasNotOption(aCmd, PARAMETER_PAGERANGE, PARAMETER_SONGID, PARAMETER_HELP);
	}

	private static boolean isPageRangeOption(CommandLine aCmd) {
		return hasOption(aCmd, PARAMETER_PAGERANGE)
				&& hasNotOption(aCmd, PARAMETER_PAGE, PARAMETER_SONGID, PARAMETER_HELP);
	}

	private static boolean isPathOption(CommandLine aCmd) {
		return aCmd.hasOption(PARAMETER_PATH);
	}

	private static boolean isHelpOption(CommandLine aCmd) {
		return hasOption(aCmd, PARAMETER_HELP)
				&& hasNotOption(aCmd, PARAMETER_PAGE, PARAMETER_PAGERANGE, PARAMETER_PATH, PARAMETER_SONGID);
	}

	private static boolean isRatioOption(CommandLine aCmd) {
		return aCmd.hasOption(PARAMETER_RATIO);
	}
}
