package bsaber.tools.bsaber_scrapper;

import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BeatSaberSongScrapperMain {
	private static final Logger cvLogger = LogManager.getLogger(BeatSaberSongScrapperMain.class);

	public static void main(String[] aArgs) throws ParseException, InterruptedException {
		BasicConfigurator.configure();

		CommandLineWrapper cmd = new CommandLineWrapper(aArgs);
		SongScrapper songScrapper = new BeatSaverSongScrapper();
		songScrapper.processCommandLine(cmd);

		Constants.EXECUTOR.shutdown();
		Constants.EXECUTOR.awaitTermination(10, TimeUnit.DAYS);

		System.exit(1);
	}
}
