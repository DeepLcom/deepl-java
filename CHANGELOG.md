# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
* Added `extraRequestParameters` option to text and document translation methods to pass arbitrary parameters in the request body. This can be used to access beta features or override built-in parameters (such as `target_lang`, `source_lang`, etc.).

## [1.10.3] - 2025-08-22
### Security
* Updated `org.apache.httpcomponents:httpclient` to 4.5.14 due to [CVE-2020-13956](https://nvd.nist.gov/vuln/detail/CVE-2020-13956).
 * Thanks to [warm-tune](https://github.com/warm-tune) for reporting in [#72](https://github.com/DeepLcom/deepl-java/issues/72).


## [1.10.2] - 2025-07-11
### Changed
* Migrate to Sonatype Portal OSSRH Staging API due to legacy OSSRH being sunsetted.
* Whitespace surrounding auth key is now stripped.
  * Thanks to [timazhum](https://github.com/timazhum) for the fix in [#64](https://github.com/DeepLcom/deepl-java/pull/69).


## [1.10.1] - 2025-06-18
### Fixed
* Fixed `DeepLClient::deleteMultilingualGlossary(String glossaryId)` being package private, made it public instead.
  * Thanks to [MTSxoff](https://github.com/MTSxoff) for the report in [#68](https://github.com/DeepLcom/deepl-java/issues/68) and the fix in [#69](https://github.com/DeepLcom/deepl-java/pull/69).


## [1.10.0] - 2025-04-30
### Added
* Added support for the /v3 Multilingual Glossary APIs in the client library
  while providing backwards compatability for the previous /v2 Glossary
  endpoints. Please refer to the README or
  [upgrading_to_multilingual_glossaries.md](upgrading_to_multilingual_glossaries.md)
  for usage instructions.
* Added Ukrainian language code


## [1.9.0] - 2025-02-21
### Added
* Allow specifying the API version to use. This is mostly for users who have an
  API subscription that includes an API key for CAT tool usage, who need to use
  the v1 API.


## [1.8.1] - 2025-02-07
### Fixed
* Added a constructor for `DeepLClient` that only takes an `authKey`, to fix the
  README example and be in line with `Translator`.
* Un-deprecated the `Translator` and `TranslatorOptions` class and moved it to
  their constructors. The functionality in them continues to work and be supported,
  user code should just use `DeepLClient` and `DeepLClientOptions`.

## [1.8.0] - 2025-01-17
### Added
* Added support for the Write API in the client library, the implementation
  can be found in the `DeepLClient` class. Please refer to the README for usage
  instructions.
### Changed
* The main functionality of the library is now also exposed via the `DeepLClient`
  class. Please change your code to use this over the `Translator` class whenever
  convenient.

## [1.7.0] - 2024-11-15
### Added
* Added `modelType` option to `translateText()` to use models with higher
  translation quality (available for some language pairs), or better latency.
  Options are `'quality_optimized'`, `'latency_optimized'`, and  `'prefer_quality_optimized'`
* Added the `modelTypeUsed` field to `translateText()` response, that
  indicates the translation model used when the `modelType` option is
  specified.


## [1.6.0] - 2024-09-17
### Added
* Added `getBilledCharacters()` to text translation response.


## [1.5.1] - 2024-09-05
### Fixed
* Fixed parsing for usage count and limit for large values.
  * Thanks to [lubo-dev](https://github.com/lubo-dev) in [#45](https://github.com/DeepLcom/deepl-java/pull/45). 


## [1.5.0] - 2024-04-10
### Added
* New language available: Arabic (MSA) (`'ar'`). Add language code constants and tests.

  Note: older library versions also support the new language, this update only
  adds new code constants.
### Fixed
* Change document upload to use the path `/v2/document` instead of `/v2/document/` (no trailing `/`).
  Both paths will continue to work in the v2 version of the API, but `/v2/document` is the intended one.


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


[Unreleased]: https://github.com/DeepLcom/deepl-java/compare/v1.10.3...HEAD
[1.10.3]: https://github.com/DeepLcom/deepl-java/compare/v1.10.2...v1.10.3
[1.10.2]: https://github.com/DeepLcom/deepl-java/compare/v1.10.0...v1.10.2
[1.10.1]: https://github.com/DeepLcom/deepl-java/compare/v1.10.0...v1.10.1
[1.10.0]: https://github.com/DeepLcom/deepl-java/compare/v1.9.0...v1.10.0
[1.9.0]: https://github.com/DeepLcom/deepl-java/compare/v1.8.1...v1.9.0
[1.8.1]: https://github.com/DeepLcom/deepl-java/compare/v1.8.0...v1.8.1
[1.8.0]: https://github.com/DeepLcom/deepl-java/compare/v1.7.0...v1.8.0
[1.7.0]: https://github.com/DeepLcom/deepl-java/compare/v1.6.0...v1.7.0
[1.6.0]: https://github.com/DeepLcom/deepl-java/compare/v1.5.1...v1.6.0
[1.5.1]: https://github.com/DeepLcom/deepl-java/compare/v1.5.0...v1.5.1
[1.5.0]: https://github.com/DeepLcom/deepl-java/compare/v1.4.0...v1.5.0
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
