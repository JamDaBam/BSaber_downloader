# A tool to download songs from bsaber
You have to choose between **-pagerange**, **-page** or **-songid**.  
For now it's not possible to do more options at once.

## Parameters
| Parameter | Notes |
| - | - |
| &#8209;h | Help |
| &#8209;page <PAGENUMBERS> | Defines pages to download. |
| &#8209;pagerange <PAGESTART PAGEEND> | Defines a range of pages. The startpage must be greater than zero and less than or equal to endpage. |
| &#8209;path <DOWNLOADPATH> | Defines the downloadfolder. If not set an absolute path then the tool creates the downloadfolder beside the executionpath. |
| &#8209;songid <SONGIDS> | Defines songids to download |

## Example
* java -jar BSaberSongScrapper.jar -path c:\Downloads\ -pagerange 1 5


# Planned features
* Download ranked songs
* Download songs by thumbs up ratio
* Download newest songs of mapper/s
* Mapper and difficulty in filename

