# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [1.0.1] - 2023-01-02
### Fixed
* Always send SentenceSplittingMode option in requests.
  * [#3](https://github.com/DeepLcom/deepl-java/issues/3) thanks to 
    [nicStuff](https://github.com/nicStuff)


## [1.0.0] - 2022-12-15
### Added
* Add support for glossary management functions.
### Changed
* `parsing.ErrorResponse` fields `message` and `detail` are now private,
  encapsulated with getters.


## [0.2.1] - 2022-10-19
### Fixed
* Handle case where HTTP response is not valid JSON.


## [0.2.0] - 2022-09-26
### Added
* Add new `Formality` options: `PreferLess` and `PreferMore`.
### Changed
* Requests resulting in `503 Service Unavailable` errors are now retried.
  Attempting to download a document before translation is completed will now
  wait and retry (up to 5 times by default), rather than throwing an exception.
### Fixed
* Use `Locale.ENGLISH` when changing string case.
  * Thanks to [seratch](https://github.com/seratch).
* Avoid cases in `HttpContent` and `StreamUtils` where temporary objects might
  not be closed.
  * Thanks to [seratch](https://github.com/seratch).


## [0.1.3] - 2022-09-09
### Fixed
* Fixed examples in readme.
* `Usage.Detail` `count` and `limit` properties type changed from `int` to `long`.


## [0.1.2] - 2022-09-08
### Fixed
* Fix publishing to Maven Central by including sourcesJar and javadocJar.


## [0.1.1] - 2022-09-08
### Fixed
* Fix CI publishing step.


## [0.1.0] - 2022-09-08
Initial version.


[1.0.1]: https://github.com/DeepLcom/deepl-java/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/DeepLcom/deepl-java/compare/v0.2.1...v1.0.0
[0.2.1]: https://github.com/DeepLcom/deepl-java/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/DeepLcom/deepl-java/compare/v0.1.3...v0.2.0
[0.1.3]: https://github.com/DeepLcom/deepl-java/compare/v0.1.2...v0.1.3
[0.1.2]: https://github.com/DeepLcom/deepl-java/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/DeepLcom/deepl-java/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/DeepLcom/deepl-java/releases/tag/v0.1.0
