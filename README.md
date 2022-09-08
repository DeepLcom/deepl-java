# DeepL Java Library

[![License: MIT](https://img.shields.io/badge/license-MIT-blueviolet.svg)](https://github.com/DeepLcom/deepl-java/blob/main/LICENSE)

The [DeepL API][api-docs] is a language translation API that allows other
computer programs to send texts and documents to DeepL's servers and receive
high-quality translations. This opens a whole universe of opportunities for
developers: any translation product you can imagine can now be built on top of
DeepL's best-in-class translation technology.

The DeepL Java library offers a convenient way for applications written in Java
to interact with the DeepL API. Currently, the library only supports text and
document translation; we intend to add support for glossary management soon.

## Getting an authentication key

To use the DeepL Java Library, you'll need an API authentication key. To get a
key, [please create an account here][create-account]. With a DeepL API Free
account you can translate up to 500,000 characters/month for free.

## Requirements

Java 1.8 or later.

## Installation

### Gradle users

Add this dependency to your project's build file:

```
implementation "com.deepl.api:deepl-java:0.1.2"
```

### Maven users

Add this dependency to your project's POM:

```
<dependency>
  <groupId>com.deepl.api</groupId>
  <artifactId>deepl-java</artifactId>
  <version>0.1.2</version>
</dependency>
```

## Usage

Import the package and construct a `Translator`. The first argument is a string
containing your API authentication key as found in your
[DeepL Pro Account][pro-account].

Be careful not to expose your key, for example when sharing source code.

```java
import com.deepl.api.*;

class Example {
    public String basicTranslationExample() throws Exception {
        String authKey = "f63c02c5-f056-...";  // Replace with your key
        Translator translator = new Translator(authKey);
        TextResult result =
                translator.translateText("Hello, world!", null, "fr");
        return result.getText(); // "Bonjour, le monde !"
    }
}
```

This example is for demonstration purposes only. In production code, the
authentication key should not be hard-coded, but instead fetched from a
configuration file or environment variable.

`Translator` accepts additional options, see [Configuration](#configuration)
for more information.

### Translating text

To translate text, call `translateText()`. The first argument is a string
containing the text you want to translate, or an iterable of strings if you want
to translate multiple texts.

`sourceLang` and `targetLang` specify the source and target language codes
respectively. The `sourceLang` is optional, if it is `null` the source language
will be auto-detected.

Language codes are **case-insensitive** strings according to ISO 639-1, for
example `'de'`, `'fr'`, `'ja''`. Some target languages also include the regional
variant according to ISO 3166-1, for example `'en-US'`, or `'pt-BR'`. The full
list of supported languages is in the [API documentation][api-docs-lang-list].

There are additional optional arguments to control translation, see
[Text translation options](#text-translation-options) below.

`translateText()` returns a `TextResult`, or a List of `TextResult`s
corresponding to your input text(s). `TextResult` has two accessors: `getText()`
returns the translated text, and `getDetectedSourceLanguage()` returns the
detected source language code.

```java
class Example {
    public void textTranslationExamples() throws Exception {
        // Translate text into a target language, in this case, French:
        TextResult result =
                translator.translateText("Hello, world!", null, "fr");
        System.out.println(result.getText()); // "Bonjour, le monde !"

        // Translate multiple texts into British English
        List<TextResult> results =
                translator.translateText(List.of("お元気ですか？", "¿Cómo estás"),
                                         null,
                                         "en-GB");
        System.out.println(results.get(0).getText()); // "How are you?"
        System.out.println(results.get(0).getDetectedSourceLanguage()); // "ja" the language code for Japanese
        System.out.println(results.get(1).getText()); // "How are you?"
        System.out.println(results.get(1).getDetectedSourceLanguage()); // "es" the language code for Spanish

        // Translate into German with less and more Formality:
        System.out.println(translator.translateText("How are you?",
                                                    null,
                                                    "de",
                                                    new TextTranslationOptions().setFormality(
                                                            Formality.Less)).getText());  // 'Wie geht es dir?'
        System.out.println(translator.translateText("How are you?",
                                                    null,
                                                    "de",
                                                    new TextTranslationOptions().setFormality(
                                                            Formality.More)).getText());  // 'Wie geht es Ihnen?'
    }
}
```

#### Text translation options

In addition to the input text(s) argument, a `translateText()` overload accepts
a `TextTranslationOptions`, with the following setters:

- `setSentenceSplittingMode()`: specify how input text should be split into
  sentences, default: `'on'`.
    - `SentenceSplittingMode.All`: input text will be split into sentences using
      both newlines and punctuation.
    - `SentenceSplittingMode.Off`: input text will not be split into sentences.
      Use this for applications where each input text contains only one
      sentence.
    - `SentenceSplittingMode.NoNewlines`: input text will be split into
      sentences using punctuation but not newlines.
- `setPreserveFormatting()`: controls automatic-formatting-correction. Set to
  `True` to prevent automatic-correction of formatting, default: `false`.
- `setFormality()`: controls whether translations should lean toward informal or
  formal language. This option is only available for some target languages, see
  [Listing available languages](#listing-available-languages).
    - `Formality.Less`: use informal language.
    - `Formality.More`: use formal, more polite language.
- `setGlossaryId()`: specifies a glossary to use with translation, as a string
  containing the glossary ID.
- `setTagHandling()`: type of tags to parse before translation, options are
  `"html"` and `"xml"`.

The following options are only used if `setTagHandling()` is set to `'xml'`:

- `setOutlineDetection()`: specify `false` to disable automatic tag detection,
  default is `true`.
- `setSplittingTags()`: list of XML tags that should be used to split text into
  sentences. Tags may be specified as an array of strings (`['tag1', 'tag2']`),
  or a comma-separated list of strings (`'tag1,tag2'`). The default is an empty
  list.
- `setNonSplittingTags()`: list of XML tags that should not be used to split
  text into sentences. Format and default are the same as for splitting tags.
- `setIgnoreTags()`: list of XML tags that containing content that should not be
  translated. Format and default are the same as for splitting tags.

For a detailed explanation of the XML handling options, see the
[API documentation][api-docs-xml-handling].

### Translating documents

To translate documents, call `translateDocument()` File objects. The first and
second arguments correspond to the input and output files respectively.

Just as for the `translateText()` function, the `sourceLang` and
`targetLang` arguments specify the source and target language codes.

There are additional optional arguments to control translation, see
[Document translation options](#document-translation-options) below.

```java
class Example {
    public void documentTranslationExamples() throws Exception {
        // Translate a formal document from English to German
        File inputFile = new File("/path/to/Instruction Manual.docx");
        File outputFile = new File("/path/to/Bedienungsanleitung.docx");
        try {
            translator.translateDocument(inputFile, outputFile, "en", "de");
        } catch (DocumentTranslationException exception) {
            // If an error occurs during document translation after the document was
            // already uploaded, a DocumentTranslationException is thrown. The
            // document_handle property contains the document handle that may be used to
            // later retrieve the document from the server, or contact DeepL support.
            DocumentHandle handle = exception.getHandle();
            System.out.write(String.format(
                    "Error after uploading %s, document handle: id: %s key: %s",
                    exception.getMessage(),
                    handle.getDocumentId(),
                    handle.getDocumentKey()));
        }
    }
}
```

`translateDocument()` is a convenience function that wraps multiple API calls:
uploading, polling status until the translation is complete, and downloading. If
your application needs to execute these steps individually, you can instead use
the following functions directly:

- `translateDocumentUpload()`,
- `translateDocumentGetStatus()` (or
  `translateDocumentWaitUntilDone()`), and
- `translateDocumentDownload()`

#### Document translation options

In addition to the input file, output file, `sourceLang` and `targetLang`
arguments, the available `translateDocument()` setters are:

- `setFormality()`: same as in [Text translation options](#text-translation-options).
- `setGlossaryId()`: same as in [Text translation options](#text-translation-options).

### Checking account usage

To check account usage, use the `getUsage()` function.

The returned `Usage` object contains three usage subtypes: `character`,
`document` and `teamDocument`. Depending on your account type, some usage
subtypes may be `null`. For API accounts:

- `usage.character` is non-`null`,
- `usage.document` and `usage.teamDocument` are `null`.

Each usage subtype (if valid) has `count` and `limit` properties giving the
amount used and maximum amount respectively, and the `limit_reached` property
that checks if the usage has reached the limit. The top level `Usage` object has
the `any_limit_reached` property to check all usage subtypes.

```java
class Example {
    public void getUsageExample() throws Exception {
        Usage usage = translator.getUsage();
        if (usage.anyLimitReached()) {
            System.out.println("Translation limit reached.");
        }
        if (usage.character != null) {
            System.out.println(String.format("Character usage: %d of %d",
                                             usage.character.count,
                                             usage.character.count));
        }
        if (usage.document != null) {
            System.out.println(String.format("Document usage: %d of %d",
                                             usage.document.count,
                                             usage.document.limit));
        }
    }
}
```

### Listing available languages

You can request the list of languages supported by DeepL for text and documents
using the `getSourceLanguages()` and `getTargetLanguages()` functions. They both
return a list of `Language` objects.

The `name` property gives the name of the language in English, and the `code`
property gives the language code. The `supportsFormality` property only appears
for target languages, and indicates whether the target language supports the
optional `formality` parameter.

```java
class Example {
  public void getLanguagesExample() throws Exception {
        Language[] sourceLanguages = translator.getSourceLanguages();
        Language[] targetLanguages = translator.getTargetLanguages();
        System.out.println("Source languages:");
        for (Language language : sourceLanguages) {
            System.out.println(String.format("%s (%s)",
                                             language.name,
                                             language.code)); // Example: "German (de)"

        }

        System.out.println("Target languages:");
        for (Language language : targetLanguages) {
            if (language.supportsFormality) {
                System.out.println(String.format("%s (%s) supports formality",
                                                 language.name,
                                                 language.code)); // Example: "Italian (it) supports formality"

            } else {
                System.out.println(String.format("%s (%s)",
                                                 language.name,
                                                 language.code)); // Example: "Lithuanian (lt)"
            }
        }
    }
}
```

### Exceptions

All module functions may raise `DeepLException` or one of its subclasses. If
invalid arguments are provided, they may raise the standard exceptions
`IllegalArgumentException`.

### Configuration

The `Translator` constructor accepts `TranslatorOptions` as a second argument,
for example:

```java
class Example {
    public void configurationExample() throws Exception {
        TranslatorOptions options =
                new TranslatorOptions().setMaxRetries(1).setTimeout(Duration.ofSeconds(
                        1));
        Translator translator = new Translator(authKey, options);
    }
}
```

The available options setters are:

- `setMaxRetries()`: maximum number of failed HTTP requests to retry, the
  default is 5. Note: only failures due to transient conditions are retried e.g.
  timeouts or temporary server overload.
- `setTimeout()`: connection timeout for each HTTP request. 
- `setProxy()`: provide details about a proxy to use for all HTTP requests to
  DeepL. 
- `setHeaders()`: additional HTTP headers to attach to all requests.
- `setServerUrl()`: base URL for DeepL API, may be overridden for testing
  purposes. By default, the correct DeepL API (Free or Pro) is automatically
  selected.

## Issues

If you experience problems using the library, or would like to request a new
feature, please open an [issue][issues].

## Development

We welcome Pull Requests, please read the
[contributing guidelines](CONTRIBUTING.md).

### Tests

Execute the tests using `./gradlew test`. The tests communicate with the DeepL
API using the auth key defined by the `DEEPL_AUTH_KEY` environment variable.

Be aware that the tests make DeepL API requests that contribute toward your API
usage.

The test suite may instead be configured to communicate with the mock-server
provided by [deepl-mock][deepl-mock]. Although most test cases work for either,
some test cases work only with the DeepL API or the mock-server and will be
otherwise skipped. The test cases that require the mock-server trigger server
errors and test the client error-handling. To execute the tests using
deepl-mock, run it in another terminal while executing the tests. Execute the
tests using `./gradlew test` with the `DEEPL_MOCK_SERVER_PORT` and
`DEEPL_SERVER_URL` environment variables defined referring to the mock-server.

[api-docs]: https://www.deepl.com/docs-api?utm_source=github&utm_medium=github-java-readme

[api-docs-xml-handling]: https://www.deepl.com/docs-api/handling-xml/?utm_source=github&utm_medium=github-java-readme

[api-docs-lang-list]: https://www.deepl.com/docs-api/translating-text/?utm_source=github&utm_medium=github-java-readme

[api-docs-glossary-lang-list]: https://www.deepl.com/docs-api/managing-glossaries/?utm_source=github&utm_medium=github-java-readme

[create-account]: https://www.deepl.com/pro?utm_source=github&utm_medium=github-java-readme#developer

[deepl-mock]: https://www.github.com/DeepLcom/deepl-mock

[issues]: https://www.github.com/DeepLcom/deepl-java/issues

[pro-account]: https://www.deepl.com/pro-account/?utm_source=github&utm_medium=github-java-readme
