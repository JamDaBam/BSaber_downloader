package bsaber.tools.bsaber_scrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

public class ParserTest {
	@Before
	public void initLog4j() {
		BasicConfigurator.configure();
	}

	@Test
	public void BSaberParserTest() {
		String singlePageEntry = "<html lang=\"en-US\">\r\n" + "<body>"
				+ "<article itemscope itemtype=\"http://schema.org/Article\" class=\"post style2 post-1173611 type-post status-publish format-standard has-post-thumbnail hentry category-uncategorized tag-majorpickle\">\r\n"
				+ "<div class=\"row\">\r\n" + "<div class=\"small-12 medium-4 columns\">\r\n"
				+ "<figure class=\"post-gallery\">\r\n"
				+ "<a href=\"https://bsaber.com/songs/5ddd/\" rel=\"bookmark\" title=\"Vize &#8211; Glad You Came\">\r\n"
				+ "<img width=\"300\" height=\"300\" src=\"data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7\" data-original=\"https://bsaber.com/wp-content/uploads/2019/08/1bd7d1a21d4cec69a4d49725e8696b6babd617fc-300x300.jpg\" class=\"attachment-medium size-medium wp-post-image\" alt=\"\" srcset=\"data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7 370w\" data-original-set=\"https://bsaber.com/wp-content/uploads/2019/08/1bd7d1a21d4cec69a4d49725e8696b6babd617fc-300x300.jpg 300w, https://bsaber.com/wp-content/uploads/2019/08/1bd7d1a21d4cec69a4d49725e8696b6babd617fc-150x150.jpg 150w, https://bsaber.com/wp-content/uploads/2019/08/1bd7d1a21d4cec69a4d49725e8696b6babd617fc.jpg 695w\" sizes=\"(max-width: 300px) 100vw, 300px\" data-attachment-id=\"1173612\" data-permalink=\"https://bsaber.com/songs/5ddd/1bd7d1a21d4cec69a4d49725e8696b6babd617fc/\" data-orig-file=\"https://bsaber.com/wp-content/uploads/2019/08/1bd7d1a21d4cec69a4d49725e8696b6babd617fc.jpg\" data-orig-size=\"695,695\" data-comments-opened=\"1\" data-image-meta=\"{&quot;aperture&quot;:&quot;0&quot;,&quot;credit&quot;:&quot;&quot;,&quot;camera&quot;:&quot;&quot;,&quot;caption&quot;:&quot;&quot;,&quot;created_timestamp&quot;:&quot;0&quot;,&quot;copyright&quot;:&quot;&quot;,&quot;focal_length&quot;:&quot;0&quot;,&quot;iso&quot;:&quot;0&quot;,&quot;shutter_speed&quot;:&quot;0&quot;,&quot;title&quot;:&quot;&quot;,&quot;orientation&quot;:&quot;0&quot;}\" data-image-title=\"1bd7d1a21d4cec69a4d49725e8696b6babd617fc\" data-image-description=\"\" data-medium-file=\"https://bsaber.com/wp-content/uploads/2019/08/1bd7d1a21d4cec69a4d49725e8696b6babd617fc-300x300.jpg\" data-large-file=\"https://bsaber.com/wp-content/uploads/2019/08/1bd7d1a21d4cec69a4d49725e8696b6babd617fc.jpg\" /> <div class='easin_review_circle_list'> <figure class=\"circle_rating rwp_user_score_styles small\">\r\n"
				+ "<span>4.8</span>\r\n"
				+ "<svg width=\"50\" height=\"50\" viewBox=\"0 0 50 50\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\r\n"
				+ "<circle class=\"circle_base\" r=\"23\" cx=\"25\" cy=\"25\" fill=\"transparent\" stroke-dasharray=\"144.44\" stroke-dashoffset=\"0\"></circle>\r\n"
				+ "<circle class=\"circle_perc\" r=\"23\" cx=\"25\" cy=\"25\" fill=\"transparent\" stroke-dasharray=\"144.44\" stroke-dashoffset=\"5.7776\" data-dashoffset=\"5.7776\"></circle>\r\n"
				+ "</svg>\r\n" + "</figure>\r\n" + "</div> </a>\r\n" + "</figure>\r\n" + "</div>\r\n"
				+ "<div class=\"small-12 medium-8 columns\">\r\n"
				+ "<div class=\"post-bottom-meta post-mapper-id-meta\">\r\n"
				+ "<img alt='' src='https://bsaber.com/wp-content/uploads/avatars/587/5c2833fa08bc9-bpfull.jpg' srcset='https://bsaber.com/wp-content/uploads/avatars/587/5c2833fa08bc9-bpfull.jpg 2x' class='avatar avatar-72 photo' height='72' width='72' /> <strong class=\"mapper_id vcard\">\r\n"
				+ "<a href=\"https://bsaber.com/members/majorpickle/\">\r\n" + "Majorpickle </a>\r\n" + "</strong>\r\n"
				+ "<time class=\"date published time\" datetime=\"2019-08-19T00:40:25-04:00\" itemprop=\"datePublished\" content=\"2019-08-19T00:40:25-04:00\">\r\n"
				+ "6 days ago </time>\r\n" + "<div class='post-recommended bsaber-tooltip -recommended'>\r\n"
				+ "<i class='fa fa-check' aria-hidden='true'></i>\r\n" + "</div>\r\n"
				+ "<span class='bsaber-categories'>\r\n" + "</span>\r\n" + "</div>\r\n"
				+ "<header class=\"post-title entry-header\">\r\n"
				+ "<h4 class=\"entry-title\" itemprop=\"name headline\">\r\n"
				+ "<a href=\"https://bsaber.com/songs/5ddd/\" title=\"Vize &#8211; Glad You Came\">\r\n"
				+ "Vize &#8211; Glad You Came </a>\r\n" + "</h4>\r\n" + "</header>\r\n" + "<div class='post-row'>\r\n"
				+ "<span class='post-difficulties post-stat'>Difficulties</span>\r\n"
				+ "<a class='post-difficulty' href='/songs/?difficulty=hard'>Hard</a>\r\n" + "</div>\r\n"
				+ "<div class='post-row'>\r\n" + "<span class='post-stat'>\r\n"
				+ "<i class='fa fa-thumbs-up fa-fw' aria-hidden='true'></i>\r\n" + "89 </span>\r\n"
				+ "<span class='post-stat'>\r\n" + "<i class='fa fa-thumbs-down fa-fw' aria-hidden='true'></i>\r\n"
				+ "1 </span>\r\n" + "</div>\r\n" + "<div class='post-row'>\r\n"
				+ "<a href=\"#\" class=\"js-bookmark action post-icon bsaber-tooltip -bookmark\" data-id=\"1173611\" data-type=\"add_bookmark\">\r\n"
				+ "<i class=\"far fa-bookmark fa-fw\"></i>\r\n" + "</a>\r\n"
				+ "<a class=\"js-twitch-request action post-icon bsaber-tooltip -twitch\" data-request-code=\"!bsr 5ddd\">\r\n"
				+ "<i class=\"fab fa-twitch fa-fw\"></i>\r\n" + "</a>\r\n"
				+ "<a class=\"action post-icon bsaber-tooltip -download-zip\" href=\"https://beatsaver.com/api/download/key/5ddd\">\r\n"
				+ "</a>\r\n" + "<a class=\"action post-icon bsaber-tooltip -one-click\" href=\"beatsaver://5ddd\">\r\n"
				+ "</a>\r\n"
				+ "<a class=\"js-listen action post-icon has-text-weight-bold bsaber-tooltip -listen\" onclick=\"previewSong(this, 'https://beatsaver.com/cdn/5ddd/1bd7d1a21d4cec69a4d49725e8696b6babd617fc.zip')\">\r\n"
				+ "<span class='listen-icon'></span>\r\n" + "</a>\r\n"
				+ "<a class=\"js-beatsaver-preview action post-icon bsaber-tooltip -beatsaver-viewer\" href=\"https://supermedium.com/beatsaver-viewer/?id=5ddd\">\r\n"
				+ "</a>\r\n" + "</div>\r\n" + "<div class=\"post-content entry-content small\">\r\n"
				+ "<p>Mapper: majorpickle&hellip;</p>\r\n" + "</div>\r\n" + "</div>\r\n" + "</div>\r\n" + "</article>"
				+ "</body></html>";
		Document doc = Jsoup.parseBodyFragment(singlePageEntry);
		Elements songEntryElements = doc.select("article");
		List<SongEntry> songEntries = BSaberParser.parse(songEntryElements);

		assertEquals(1, songEntries.size());

		SongEntry songEntry = songEntries.get(0);
		String resString = "Key: 5ddd LevelAuthorName: Majorpickle SongAuthorName: null SongName: Vize – Glad You Came SongSubName: null Difficulties: Hard Upvotes: 89 Downvotes: 1 Heat: null Rating: null DownloadURL: https://beatsaver.com/api/download/key/5ddd";
		assertEquals(resString, songEntry.toString());
	}

	@Test
	public void BeatSaverParserMultipleEntriesTest() {
		String jsonString = "{\"docs\":[{\"metadata\":{\"difficulties\":{\"easy\":false,\"expert\":false,\"expertPlus\":true,\"hard\":false,\"normal\":false},\"characteristics\":[{\"difficulties\":{\"easy\":null,\"expert\":null,\"expertPlus\":{\"duration\":962.0474853515625,\"length\":268,\"njs\":12,\"njsOffset\":0,\"bombs\":145,\"notes\":1104,\"obstacles\":54},\"hard\":null,\"normal\":null},\"name\":\"OneSaber\"}],\"levelAuthorName\":\"mrSmile\",\"songAuthorName\":\"Team Grimoire\",\"songName\":\"G1ll35 d3 R415 (No Arrows)\",\"songSubName\":\"\",\"bpm\":215},\"stats\":{\"downloads\":4,\"plays\":0,\"downVotes\":0,\"upVotes\":0,\"heat\":924.1785392,\"rating\":0},\"description\":\"Single Saber +No Arrows \\nExpert+ Only (Expert And Hard Coming Soon)\\n------------------------------------------------------\\nMade By mrSmile\",\"deletedAt\":null,\"_id\":\"5d624342b8ed570006d79477\",\"key\":\"5f02\",\"name\":\"G1ll35 d3 R415 (Gilles De Rais) - Team Grimoire (No Arrows)\",\"uploader\":{\"_id\":\"5cff0b7798cc5a672c856253\",\"username\":\"mrsmile\"},\"hash\":\"60c9b691c5de0b1b5ad72be8fc82bfd0f8b3108b\",\"uploaded\":\"2019-08-25T08:13:54.266Z\",\"directDownload\":\"/cdn/5f02/60c9b691c5de0b1b5ad72be8fc82bfd0f8b3108b.zip\",\"downloadURL\":\"/api/download/key/5f02\",\"coverURL\":\"/cdn/5f02/60c9b691c5de0b1b5ad72be8fc82bfd0f8b3108b.jpg\"},{\"metadata\":{\"difficulties\":{\"easy\":false,\"expert\":true,\"expertPlus\":true,\"hard\":true,\"normal\":false},\"characteristics\":[{\"difficulties\":{\"easy\":null,\"expert\":{\"duration\":652.9929809570312,\"length\":217,\"njs\":16,\"njsOffset\":0,\"bombs\":100,\"notes\":1083,\"obstacles\":42},\"expertPlus\":{\"duration\":657.4929809570312,\"length\":219,\"njs\":18,\"njsOffset\":0,\"bombs\":52,\"notes\":1256,\"obstacles\":54},\"hard\":{\"duration\":652.9929809570312,\"length\":217,\"njs\":13,\"njsOffset\":0,\"bombs\":84,\"notes\":602,\"obstacles\":63},\"normal\":null},\"name\":\"Standard\"}],\"levelAuthorName\":\"HaiXuZ\",\"songAuthorName\":\"Nanawoakari/Nayutan Seijin\",\"songName\":\"Dadadada Angel - Minato Aqua\",\"songSubName\":\"\",\"bpm\":180},\"stats\":{\"downloads\":28,\"plays\":0,\"downVotes\":0,\"upVotes\":0,\"heat\":924.1529944,\"rating\":0},\"description\":\"！！！！！！！！！！！！！！！！！\",\"deletedAt\":null,\"_id\":\"5d623ec42c316f00068bca1d\",\"key\":\"5f01\",\"name\":\"Dadadada Angel - Minato Aqua\",\"uploader\":{\"_id\":\"5cff0b7298cc5a672c84e9a3\",\"username\":\"haixuz\"},\"hash\":\"9e40c0b043c28c9caf9f53462f6811ab69448eb2\",\"uploaded\":\"2019-08-25T07:54:44.749Z\",\"directDownload\":\"/cdn/5f01/9e40c0b043c28c9caf9f53462f6811ab69448eb2.zip\",\"downloadURL\":\"/api/download/key/5f01\",\"coverURL\":\"/cdn/5f01/9e40c0b043c28c9caf9f53462f6811ab69448eb2.jpg\"},{\"metadata\":{\"difficulties\":{\"easy\":false,\"expert\":true,\"expertPlus\":true,\"hard\":true,\"normal\":false},\"characteristics\":[{\"difficulties\":{\"easy\":null,\"expert\":{\"duration\":172.3333282470703,\"length\":51,\"njs\":16,\"njsOffset\":0,\"bombs\":0,\"notes\":335,\"obstacles\":30},\"expertPlus\":{\"duration\":172.3333282470703,\"length\":51,\"njs\":20,\"njsOffset\":0,\"bombs\":0,\"notes\":565,\"obstacles\":30},\"hard\":{\"duration\":172.3333282470703,\"length\":51,\"njs\":12,\"njsOffset\":0,\"bombs\":0,\"notes\":174,\"obstacles\":30},\"normal\":null},\"name\":\"Standard\"}],\"levelAuthorName\":\"Pug\",\"songAuthorName\":\"saradisk\",\"songName\":\"166 - Suzuya Homerarete Nobiru Type Nandesu.\",\"songSubName\":\"\",\"bpm\":200},\"stats\":{\"downloads\":36,\"plays\":0,\"downVotes\":0,\"upVotes\":0,\"heat\":924.1123848,\"rating\":0},\"description\":\"yuh v5\",\"deletedAt\":null,\"_id\":\"5d6237a1b8ed570006d78e56\",\"key\":\"5f00\",\"name\":\"166 - Suzuya Homerarete Nobiru Type Nandesu. - saradisk\",\"uploader\":{\"_id\":\"5cff0b7398cc5a672c84f09f\",\"username\":\"pug\"},\"hash\":\"839245d64719d018e40e2f11654b7c49230ca126\",\"uploaded\":\"2019-08-25T07:24:17.314Z\",\"directDownload\":\"/cdn/5f00/839245d64719d018e40e2f11654b7c49230ca126.zip\",\"downloadURL\":\"/api/download/key/5f00\",\"coverURL\":\"/cdn/5f00/839245d64719d018e40e2f11654b7c49230ca126.jpg\"}]}";
		List<SongEntry> songEntries = BeatSaverParser.parse(jsonString);

		assertEquals(3, songEntries.size());
		SongEntry songEntry = songEntries.get(0);
		SongEntry songEntry2 = songEntries.get(1);
		SongEntry songEntry3 = songEntries.get(2);

		assertEquals(songEntry.getMetaData().getKey(), "5f02");
		assertEquals(songEntry.getMetaData().getLevelAuthorName(), "mrSmile");
		assertEquals(songEntry.getMetaData().getSongAuthorName(), "Team Grimoire");
		assertEquals(songEntry.getMetaData().getSongName(), "G1ll35 d3 R415 (No Arrows)");
		assertNull(songEntry.getMetaData().getSongSubName());
		assertEquals(songEntry.getMetaData().getDifficultiesAsString(), "easy, expert, expertPlus, hard, normal");
		assertEquals(songEntry.getMetaData().getUpVotes(), 0, 0);
		assertEquals(songEntry.getMetaData().getDownVotes(), 0, 0);
		assertEquals(songEntry.getMetaData().getHeat(), 924.1785f, 0f);
		assertEquals(songEntry.getMetaData().getRating(), 0.0f, 0f);
		assertEquals(songEntry.getMetaData().getDownloadURL(), "https://beatsaver.com/api/download/key/5f02");

		assertEquals(songEntry2.getMetaData().getKey(), "5f01");
		assertEquals(songEntry2.getMetaData().getLevelAuthorName(), "HaiXuZ");
		assertEquals(songEntry2.getMetaData().getSongAuthorName(), "Nanawoakari/Nayutan Seijin");
		assertEquals(songEntry2.getMetaData().getSongName(), "Dadadada Angel - Minato Aqua");
		assertNull(songEntry2.getMetaData().getSongSubName());
		assertEquals(songEntry2.getMetaData().getDifficultiesAsString(), "easy, expert, expertPlus, hard, normal");
		assertEquals(songEntry2.getMetaData().getUpVotes(), 0, 0);
		assertEquals(songEntry2.getMetaData().getDownVotes(), 0, 0);
		assertEquals(songEntry2.getMetaData().getHeat(), 924.153f, 0f);
		assertEquals(songEntry2.getMetaData().getRating(), 0.0f, 0f);
		assertEquals(songEntry2.getMetaData().getDownloadURL(), "https://beatsaver.com/api/download/key/5f01");

		assertEquals(songEntry3.getMetaData().getKey(), "5f00");
		assertEquals(songEntry3.getMetaData().getLevelAuthorName(), "Pug");
		assertEquals(songEntry3.getMetaData().getSongAuthorName(), "saradisk");
		assertEquals(songEntry3.getMetaData().getSongName(), "166 - Suzuya Homerarete Nobiru Type Nandesu.");
		assertNull(songEntry3.getMetaData().getSongSubName());
		assertEquals(songEntry3.getMetaData().getDifficultiesAsString(), "easy, expert, expertPlus, hard, normal");
		assertEquals(songEntry3.getMetaData().getUpVotes(), 0, 0);
		assertEquals(songEntry3.getMetaData().getDownVotes(), 0, 0);
		assertEquals(songEntry3.getMetaData().getHeat(), 924.11237f, 0f);
		assertEquals(songEntry3.getMetaData().getRating(), 0.0f, 0f);
		assertEquals(songEntry3.getMetaData().getDownloadURL(), "https://beatsaver.com/api/download/key/5f00");
	}

	@Test
	public void BeatSaverParserSingleEntryTest() {
		String jsonString = "{\"metadata\":{\"difficulties\":{\"easy\":false,\"expert\":true,\"expertPlus\":false,\"hard\":false,\"normal\":false},\"characteristics\":[{\"difficulties\":{\"easy\":null,\"expert\":{\"duration\":675.875,\"length\":411,\"njs\":10,\"njsOffset\":0,\"bombs\":234,\"notes\":813,\"obstacles\":23},\"expertPlus\":null,\"hard\":null,\"normal\":null},\"name\":\"Standard\"}],\"levelAuthorName\":\"Yuki Yumeno\",\"songAuthorName\":\"NUKITASHI\",\"songName\":\"BWLAUTE BEIRRD\",\"songSubName\":\"\",\"bpm\":98.51000213623047},\"stats\":{\"downloads\":405,\"plays\":0,\"downVotes\":6,\"upVotes\":3,\"heat\":879.7282217,\"rating\":0.41666666666666663},\"description\":\"this is the my first Music score creation\\nJapan Eroge\\\"NUKITASHI 2\\\"\",\"deletedAt\":null,\"_id\":\"5d4411983abf3d000655fdaa\",\"key\":\"5a43\",\"name\":\"BWLAUTE BEIRRD [NUKITASHI2]\",\"uploader\":{\"_id\":\"5d4408b3130e0e0006937d97\",\"username\":\"kageki_usiromiya\"},\"hash\":\"40093b4bf1e79fe72e63766901affa9ebfbfb8fb\",\"uploaded\":\"2019-08-02T10:34:00.432Z\",\"directDownload\":\"/cdn/5a43/40093b4bf1e79fe72e63766901affa9ebfbfb8fb.zip\",\"downloadURL\":\"/api/download/key/5a43\",\"coverURL\":\"/cdn/5a43/40093b4bf1e79fe72e63766901affa9ebfbfb8fb.jpg\"}";
		List<SongEntry> songEntries = BeatSaverParser.parse(jsonString);

		assertEquals(1, songEntries.size());
		SongEntry songEntry = songEntries.get(0);

		assertEquals(songEntry.getMetaData().getKey(), "5a43");
		assertEquals(songEntry.getMetaData().getLevelAuthorName(), "Yuki Yumeno");
		assertEquals(songEntry.getMetaData().getSongAuthorName(), "NUKITASHI");
		assertEquals(songEntry.getMetaData().getSongName(), "BWLAUTE BEIRRD");
		assertNull(songEntry.getMetaData().getSongSubName());
		assertEquals(songEntry.getMetaData().getDifficultiesAsString(), "easy, expert, expertPlus, hard, normal");
		assertEquals(songEntry.getMetaData().getUpVotes(), 3, 0);
		assertEquals(songEntry.getMetaData().getDownVotes(), 6, 0);
		assertEquals(songEntry.getMetaData().getHeat(), 879.7282f, 0f);
		assertEquals(songEntry.getMetaData().getRating(), 0.41666666f, 0f);
		assertEquals(songEntry.getMetaData().getDownloadURL(), "https://beatsaver.com/api/download/key/5a43");
	}
}
