package bsaber.tools.bsaber_scrapper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class Tools {
	private Tools() {
	}

	public static String difficultiesToString(List<String> aDifficulties) {
		String res = "";

		for (Iterator<String> iterator = aDifficulties.iterator(); iterator.hasNext();) {
			String difficulty = iterator.next();

			if (res.isEmpty()) {
				res += "(";
			}

			if (iterator.hasNext()) {
				res += difficulty + ", ";
			} else {
				res += difficulty + ")";
			}
		}

		return res;
	}

	public static String checkPath(String aPath) {
		String path = aPath;

		char lastChar = path.charAt(path.length() - 1);
		if (lastChar != '\\') {
			path = path + "\\";
		}

		return path;
	}

	public static <T> boolean isNullOrEmpty(Collection<T> aCollection) {
		return aCollection == null || aCollection.isEmpty();
	}

	public static class DownloadTools {
		public static boolean checkSongIdAlreadyDownloaded(String aPath, String aSongId) {
			File dir = new File(aPath);

			// list the files using a anonymous FileFilter
			File[] files = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File file) {
					return file.getName().startsWith(aSongId + " ");
				}
			});

			if (files != null && files.length > 0) {
				return true;
			} else {
				return false;
			}
		}

		public static void saveUrl(final Path aFile, final URL aUrl, int aSecsConnectTimeout, int aSecsReadTimeout)
				throws MalformedURLException, IOException {
			try (InputStream in = streamFromUrl(aUrl, aSecsConnectTimeout, aSecsReadTimeout)) {
				Files.copy(in, aFile);
			}
		}

		public static InputStream streamFromUrl(URL aUrl, int aSecsConnectTimeout, int aSecsReadTimeout)
				throws IOException {
			URLConnection conn = aUrl.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

			if (aSecsConnectTimeout > 0)
				conn.setConnectTimeout(aSecsConnectTimeout * 1000);
			if (aSecsReadTimeout > 0)
				conn.setReadTimeout(aSecsReadTimeout * 1000);
			return conn.getInputStream();
		}
	}

	public static class CommandLineTools {
		public static Options generateOptions() {
			Options options = new Options();

			Option optionHelp = new Option(Constants.PARAMETER_HELP, false, "Help");
			options.addOption(optionHelp);

			Option optionPageRange = new Option(Constants.PARAMETER_PAGERANGE, true,
					"Defines a range of pages. The startpage must be greater than zero and less than or equal to endpage.");
			optionPageRange.setArgName("PAGESTART PAGEEND");
			optionPageRange.setOptionalArg(false);
			optionPageRange.setArgs(2);
			options.addOption(optionPageRange);

			Option optionPage = new Option(Constants.PARAMETER_PAGE, true, "Defines pages to download.");
			optionPage.setArgName("PAGENUMBERS");
			optionPage.setOptionalArg(false);
			optionPage.setArgs(Option.UNLIMITED_VALUES);
			options.addOption(optionPage);

			Option optionSongId = new Option(Constants.PARAMETER_SONGID, true, "Defines songids to download");
			optionSongId.setArgName("SONGIDS");
			optionSongId.setOptionalArg(false);
			optionSongId.setArgs(Option.UNLIMITED_VALUES);
			options.addOption(optionSongId);

			Option optionPath = new Option(Constants.PARAMETER_PATH, true,
					"Defines the downloadfolder. If not set an absolute path then the tool creates the downloadfolder beside the executionpath.");
			optionPath.setArgName("DOWNLOADPATH [REQUIRED]");
			optionPath.setOptionalArg(false);
			optionPath.setArgs(1);
			options.addOption(optionPath);

			Option optionRatio = new Option(Constants.PARAMETER_RATIO, "Defines a thumbs up ratio between 0.0 to 1.0.");
			optionRatio.setArgName("RATIO");
			optionRatio.setOptionalArg(false);
			optionRatio.setArgs(1);
			options.addOption(optionRatio);

			return options;
		}

		public static boolean hasOption(CommandLine aCmd, String... aOptions) {
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

		public static boolean hasNotOption(CommandLine aCmd, String... aOptions) {
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

		public static boolean isSongIdOption(CommandLine aCmd) {
			return hasOption(aCmd, Constants.PARAMETER_SONGID) && hasNotOption(aCmd, Constants.PARAMETER_PAGE,
					Constants.PARAMETER_PAGERANGE, Constants.PARAMETER_HELP);
		}

		public static boolean isPageOption(CommandLine aCmd) {
			return hasOption(aCmd, Constants.PARAMETER_PAGE) && hasNotOption(aCmd, Constants.PARAMETER_PAGERANGE,
					Constants.PARAMETER_SONGID, Constants.PARAMETER_HELP);
		}

		public static boolean isPageRangeOption(CommandLine aCmd) {
			return hasOption(aCmd, Constants.PARAMETER_PAGERANGE) && hasNotOption(aCmd, Constants.PARAMETER_PAGE,
					Constants.PARAMETER_SONGID, Constants.PARAMETER_HELP);
		}

		public static boolean isPathOption(CommandLine aCmd) {
			return aCmd.hasOption(Constants.PARAMETER_PATH);
		}

		public static boolean isHelpOption(CommandLine aCmd) {
			return hasOption(aCmd, Constants.PARAMETER_HELP) && hasNotOption(aCmd, Constants.PARAMETER_PAGE,
					Constants.PARAMETER_PAGERANGE, Constants.PARAMETER_PATH, Constants.PARAMETER_SONGID);
		}

		public static boolean isRatioOption(CommandLine aCmd) {
			return aCmd.hasOption(Constants.PARAMETER_RATIO);
		}
	}
}
