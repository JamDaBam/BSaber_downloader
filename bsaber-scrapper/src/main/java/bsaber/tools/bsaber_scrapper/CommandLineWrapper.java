package bsaber.tools.bsaber_scrapper;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CommandLineWrapper {
	private static final Logger cvLogger = LogManager.getLogger(CommandLineWrapper.class);

	// Parameters
	private static final String PARAMETER_HELP = "h";
	private static final String PARAMETER_PAGERANGE = "pagerange";
	private static final String PARAMETER_PAGE = "page";
	private static final String PARAMETER_SONGID = "songid";
	private static final String PARAMETER_PATH = "path";
	private static final String PARAMETER_RATIO = "ratio";

	private String ivPath;
	private Float ivRatio;
	private int[] ivPages;
	private String[] ivSongKeys;

	public CommandLineWrapper(String[] aArgs) {
		try {
			Options options = generateOptions();
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, aArgs);

			// Process help
			if (aArgs.length == 0 || isHelpOption(cmd)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("Parameters", options);

				System.exit(1);
			}

			// Process path
			processPath(cmd);

			// Process ratio
			processRatio(cmd);

			// Process pages
			processPages(cmd);

			// Proces songkeys
			processSongKeys(cmd);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void processSongKeys(CommandLine cmd) {
		ivSongKeys = cmd.getOptionValues(PARAMETER_SONGID);
	}

	private void processPages(CommandLine cmd) {
		Set<Integer> pages = new TreeSet<>();

		String[] pageRange = cmd.getOptionValues(PARAMETER_PAGERANGE);

		if (pageRange != null) {
			Integer pageStart = null;
			Integer pageEnd = null;

			pageStart = Integer.parseInt(pageRange[0]);
			pageEnd = Integer.parseInt(pageRange[1]);

			pages.addAll(processPageRange(pageStart, pageEnd));
		}

		String[] pageNumbersStrings = cmd.getOptionValues(PARAMETER_PAGE);

		if (pageNumbersStrings != null) {
			for (String pageNumberString : pageNumbersStrings) {
				pages.add(Integer.parseInt(pageNumberString));
			}
		}

		ivPages = pages.stream().mapToInt(Number::intValue).toArray();
	}

	private void processRatio(CommandLine cmd) {
		if (isRatioOption(cmd)) {
			ivRatio = Float.parseFloat(cmd.getOptionValue(PARAMETER_RATIO));
		}
	}

	private void processPath(CommandLine cmd) {
		Config config = Config.getInstance();
		String path = config.getProperty(Config.PROPERTY_PATH);
		if (isPathOption(cmd)) {
			path = cmd.getOptionValue(PARAMETER_PATH);
		}
		ivPath = checkPath(path);
	}

	private List<Integer> processPageRange(Integer aPageStart, Integer aPageEnd) {
		if (aPageStart == 0) {
			throw new IllegalArgumentException("aPageStart has to be greater than 0");
		}
		if (aPageStart > aPageEnd) {
			throw new IllegalArgumentException("aPageStart <= aPageEnd ----- " + aPageStart + " <= " + aPageEnd);
		}

		int maxPoolSize = aPageEnd - aPageStart + Constants.CORE_SIZE;
		Constants.EXECUTOR.setMaximumPoolSize(maxPoolSize);

		return IntStream.range(aPageStart, aPageEnd + 1).boxed().collect(Collectors.toList());
	}

	private Options generateOptions() {
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
				"Defines the downloadfolder. If not set an absolute path then the tool creates the downloadfolder beside the executionpath. Default: path from config.ini");
		optionPath.setArgName("DOWNLOADPATH");
		optionPath.setOptionalArg(false);
		optionPath.setArgs(1);
		options.addOption(optionPath);

		Option optionRatio = new Option(PARAMETER_RATIO, "Defines a upvote ratio between 0.0 to 1.0.");
		optionRatio.setArgName("RATIO");
		optionRatio.setOptionalArg(false);
		optionRatio.setArgs(1);
		options.addOption(optionRatio);

		return options;
	}

	private boolean hasOption(CommandLine aCmd, String... aOptions) {
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

	private boolean hasNotOption(CommandLine aCmd, String... aOptions) {
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

	private boolean isPathOption(CommandLine aCmd) {
		return aCmd.hasOption(PARAMETER_PATH);
	}

	private boolean isHelpOption(CommandLine aCmd) {
		return hasOption(aCmd, PARAMETER_HELP)
				&& hasNotOption(aCmd, PARAMETER_PAGE, PARAMETER_PAGERANGE, PARAMETER_PATH, PARAMETER_SONGID);
	}

	private boolean isRatioOption(CommandLine aCmd) {
		return aCmd.hasOption(PARAMETER_RATIO);
	}

	private String checkPath(String aPath) {
		String path = aPath;

		char lastChar = path.charAt(path.length() - 1);
		if (lastChar != '\\') {
			path = path + "\\";
		}

		return path;
	}

	public int[] getPages() {
		return ivPages;
	}

	public String getPath() {
		return ivPath;
	}

	public Float getRatio() {
		return ivRatio;
	}

	public String[] getSongKeys() {
		return ivSongKeys;
	}
}
