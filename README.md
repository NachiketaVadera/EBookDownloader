# eBooks

[![GitHub license](https://img.shields.io/github/license/NachiketaVadera/EBookDownloader.svg?style=for-the-badge)](https://github.com/NachiketaVadera/EBookDownloader/blob/master/LICENSE) 
[![GitHub stars](https://img.shields.io/github/stars/NachiketaVadera/Ebookdownloader.svg?style=for-the-badge)](https://github.com/NachiketaVadera/Ebookdownloader/stargazers)
[![GitHub issues](https://img.shields.io/github/issues/NachiketaVadera/Ebookdownloader.svg?color=brightgreen&style=for-the-badge)](https://github.com/NachiketaVadera/Ebookdownloader/issues)

## Summary

eBooks is an app that searches the interent for the book you request and gives you options to download the book in multiple formats (pdf, epub, mobi etc).

<a href="https://f-droid.org/packages/android.nachiketa.ebookdownloader/"><img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" height="75"></a>

## Downloads

[eBooks.apk](https://github.com/NachiketaVadera/EBookDownloader/releases/download/v0.5/eBooks-v0.5.apk)

## How to Use

* You can download the app directly from the F-Droid app.
* Give the app permission to access storage of your device (Click allow/yes when the app prompts for permission).
* Enter the name of the book you want to download and the author. Try to spell the names accurately for better results.
* Click on `Search` and you'll be given options to download from.
* While you wait, read some of the quotes I like.
* To download a book, click on the name you want to download. Your selection will turn green and download will start.
* You can find the book in your `Downloads` folder.
* Make sure you have appropriate application to read the file you just downloaded.
* Have fun reading!

## Details

eBooks dose not search books from a formal database. Instead, your queries are fired through Google and the results are parsed for links to trusted sources that can provide download links. Currently, the only source in use is vk.com with more to follow. From the search results page, the app selects a page that has mentioned your query on the pages text. The selected page is parsed again and all the file download links are collected and sent back for you to see and download. 

Another feature that the app provides is ‘Search by Author’ which parses a card view google search page with books by all the authors. While easy, this method is quite inefficient and will be replaced down the line with JSON parsing of Google Books API.

Libraries used: 
* [Jsoup](https://jsoup.org/)
* [PenAndPaper](https://github.com/NachiketaVadera/PenAndPaper) {My own under development library)
* [FancyToast](https://github.com/Shashank02051997/FancyToast-Android/)
* [SnappyDB](https://github.com/nhachicha/SnappyDB/)
* [SweetAlertDialog](https://mvnrepository.com/artifact/com.github.f0ris.sweetalert/library)

## Next Step

Once started, an app can never be completely finished. 

1. Add new sources.
2. Optimize code.
3. Improve user experience.

## Contributers

[IzzySoft](https://github.com/IzzySoft)
[TacoTheDank](https://github.com/TacoTheDank)

## Contributing

Any help, including feedback, is highly appreciated. I have just started out with Android and I’m relatively new to app development.

1. Fork it!
2. Create your feature branch: `git checkout -b new-branch`
3. Commit your changes: `git commit -am 'Make a valuable addition'`
4. Push to the branch: `git push origin new-feature`
5. Submit a pull request :D
