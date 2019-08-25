package bsaber.tools.bsaber_scrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SongMetaData {
	private String ivKey;
	private String ivSongName;
	private String ivSongSubName;
	private String ivLevelAuthorName;
	private String ivSongAuthorName;
	private String ivDownloadURL;
	private Integer ivBpm;
	private Integer ivDownVotes;
	private Integer ivUpVotes;
	private Float ivHeat;
	private Float ivRating;
	private List<String> ivDifficulties;

	public SongMetaData() {
	}

	public String getKey() {
		return ivKey;
	}

	public void setKey(String aKey) {
		ivKey = aKey;
	}

	public String getSongName() {
		return ivSongName;
	}

	public void setSongName(String aSongName) {
		ivSongName = aSongName;
	}

	public String getSongSubName() {
		return ivSongSubName;
	}

	public void setSongSubName(String aSongSubName) {
		ivSongSubName = aSongSubName;
	}

	public String getLevelAuthorName() {
		return ivLevelAuthorName;
	}

	public void setLevelAuthorName(String aLevelAuthorName) {
		ivLevelAuthorName = aLevelAuthorName;
	}

	public String getSongAuthorName() {
		return ivSongAuthorName;
	}

	public void setSongAuthorName(String aSongAuthorName) {
		ivSongAuthorName = aSongAuthorName;
	}

	public String getDownloadURL() {
		return ivDownloadURL;
	}

	public void setDownloadURL(String aDownloadURL) {
		ivDownloadURL = aDownloadURL;
	}

	public Integer getBpm() {
		return ivBpm;
	}

	public void setBpm(Integer aBpm) {
		ivBpm = aBpm;
	}

	public Integer getDownVotes() {
		return ivDownVotes;
	}

	public void setDownVotes(Integer aDownVotes) {
		ivDownVotes = aDownVotes;
	}

	public Integer getUpVotes() {
		return ivUpVotes;
	}

	public void setUpVotes(Integer aUpVotes) {
		ivUpVotes = aUpVotes;
	}

	public Float getHeat() {
		return ivHeat;
	}

	public void setHeat(Float aHeat) {
		ivHeat = aHeat;
	}

	public Float getRating() {
		return ivRating;
	}

	public void setRating(Float aRating) {
		ivRating = aRating;
	}

	public void addDifficulty(String aDifficulty) {
		if (ivDifficulties == null) {
			ivDifficulties = new ArrayList<>();
		}

		ivDifficulties.add(aDifficulty);
	}

	public String getDifficultiesAsString() {
		String res = "";

		if (!Tools.isNullOrEmpty(ivDifficulties)) {
			for (Iterator<String> iterator = ivDifficulties.iterator(); iterator.hasNext();) {
				String difficulty = iterator.next();

				res += difficulty;

				if (iterator.hasNext()) {
					res += ", ";
				}
			}
		}

		return res;
	}

	@Override
	public String toString() {
		return "Key: " + ivKey + " LevelAuthorName: " + ivLevelAuthorName + " SongAuthorName: " + ivSongAuthorName
				+ " SongName: " + ivSongName + " SongSubName: " + ivSongSubName + " Difficulties: "
				+ getDifficultiesAsString() + " Upvotes: " + ivUpVotes + " Downvotes: " + ivDownVotes + " Heat: "
				+ ivHeat + " Rating: " + ivRating + " DownloadURL: " + ivDownloadURL;
	}
}
