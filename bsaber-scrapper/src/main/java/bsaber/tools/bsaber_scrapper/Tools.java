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

public class Tools {
	private Tools() {
	}

	public static String extractID(String aHref, String aCutString) {
		return aHref.replace(aCutString, "").replace("/", "");
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
