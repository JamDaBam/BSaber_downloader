# A tool to download songs from bsaber
You have to choose between **-pagerange**, **-page** or **-songid**.  
For now it's not possible to do more options at once.

Optional you can use **-ratio** to define a ratio between 0.0 to 1.0.<br>
The ratio of songs is measured by (thumbs up / thumbs total)

## Parameters
| Parameter | Notes |
| - | - |
| &#8209;h | Help |
| &#8209;page | Defines pages to download. |
| &#8209;pagerange | Defines a range of pages. The startpage must be greater than zero and less than or equal to endpage. |
| &#8209;path | Defines the downloadfolder. If not set an absolute path then the tool creates the downloadfolder beside the executionpath. |
| &#8209;ratio | Defines a thumbs up ratio between 0.0 to 1.0. |  
| &#8209;songid | Defines songids to download |

## Example
* java -jar BSaberSongScrapper.jar -path c:\Downloads\ -pagerange 1 5

* Outputfile: &lt;SongId&gt; - &lt;Title&gt; (&lt;Mapper&gt; (&lt;Difficulties&gt;))
  + 5b6f - Nicola Fasano _ Miami Rockets _ I like to move it (jessi81_1 (Normal, Hard, Expert))

# Planned features
* Download ranked songs
* Download newest songs of mapper/s

