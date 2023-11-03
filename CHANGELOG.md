# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [1.4.0] - 2023-11-03
### Added
* Add optional `context` parameter for text translation, that specifies
  additional context to influence translations, that is not translated itself.
### Fixed
* Remove unused `commons-math` dependency


## [1.3.0] - 2023-06-09 
### Fixed
* Changed document translation to poll the server every 5 seconds. This should greatly reduce observed document translation processing time.
* Fix getUsage request to be a HTTP GET request, not POST.


## [1.2.0] - 2023-03-22
### Added
* Script to check our source code for license headers and a step for them in the CI.
* Added system and java version information to the user-agent string that is sent with API calls, along with an opt-out.
* Added method for applications that use this library to identify themselves in API requests they make.


## [1.1.0] - 2023-01-26
### Added
* Add example maven project using this library.
* New languages available: Korean (`'ko'`) and Norwegian (bokmål) (`'nb'`). Add
  language code constants and tests.

  Note: older library versions also support the new languages, this update only
  adds new code constants.
### Fixed
* Send Formality options in API requests even if it is default.


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


[1.4.0]: https://github.com/DeepLcom/deepl-java/compare/v1.3.0...v1.4.0
[1.3.0]: https://github.com/DeepLcom/deepl-java/compare/v1.2.0...v1.3.0
[1.2.0]: https://github.com/DeepLcom/deepl-java/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/DeepLcom/deepl-java/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/DeepLcom/deepl-java/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/DeepLcom/deepl-java/compare/v0.2.1...v1.0.0
[0.2.1]: https://github.com/DeepLcom/deepl-java/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/DeepLcom/deepl-java/compare/v0.1.3...v0.2.0
[0.1.3]: https://github.com/DeepLcom/deepl-java/compare/v0.1.2...v0.1.3
[0.1.2]: https://github.com/DeepLcom/deepl-java/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/DeepLcom/deepl-java/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/DeepLcom/deepl-java/releases/tag/v0.1.0
