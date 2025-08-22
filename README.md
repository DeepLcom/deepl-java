# DeepL Java Library

[![Maven Central](https://img.shields.io/maven-central/v/com.deepl.api/deepl-java.svg)](https://mvnrepository.com/artifact/com.deepl.api/deepl-java)
[![License: MIT](https://img.shields.io/badge/license-MIT-blueviolet.svg)](https://github.com/DeepLcom/deepl-java/blob/main/LICENSE)

The [DeepL API][api-docs] is a language AI API that allows other computer programs
to send texts and documents to DeepL's servers and receive high-quality
translations and improvements to the text. This opens a whole universe of
opportunities for developers: any translation product you can imagine can now
be built on top of DeepL's best-in-class translation technology.

The DeepL Java library offers a convenient way for applications written in Java
to interact with the DeepL API. We intend to support all API functions with the
library, though support for new features may be added to the library after
they’re added to the API.

## Getting an authentication key

To use the DeepL Java Library, you'll need an API authentication key. To get a
key, [please create an account here][create-account]. With a DeepL API Free
account you can consume up to 500,000 characters/month for free.

## Requirements

Java 1.8 or later.

## Installation

### Gradle users

Add this dependency to your project's build file:

```
implementation "com.deepl.api:deepl-java:1.10.3"
```

### Maven users

Add this dependency to your project's POM:

```
<dependency>
  <groupId>com.deepl.api</groupId>
  <artifactId>deepl-java</artifactId>
  <version>1.10.3</version>
</dependency>
```

## Usage

Import the package and construct a `DeepLClient`. The first argument is a string
containing your API authentication key as found in your
[DeepL Pro Account][pro-account].

Be careful not to expose your key, for example when sharing source code.

```java
import com.deepl.api.*;

class Example {
    DeepLClient client;

    public Example() throws Exception {
        String authKey = "f63c02c5-f056-...";  // Replace with your key
        client = new DeepLClient(authKey);
        TextResult result =
                client.translateText("Hello, world!", null, "fr");
        System.out.println(result.getText()); // "Bonjour, le monde !"
    }
}
```

This example is for demonstration purposes only. In production code, the
authentication key should not be hard-coded, but instead fetched from a
configuration file or environment variable.

`DeepLClient` accepts additional options, see [Configuration](#configuration)
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
corresponding to your input text(s). `TextResult` has the following accessors:
- `getText()` returns the translated text,
- `getDetectedSourceLanguage()` returns the detected source language code,
- `getBilledCharacters()` returns the number of characters billed for the text, and
- `getModelTypeUsed()` returns the model type used for the translation.

```java
class Example {  // Continuing class Example from above
    public void textTranslationExamples() throws Exception {
        // Translate text into a target language, in this case, French:
        TextResult result =
                client.translateText("Hello, world!", null, "fr");
        System.out.println(result.getText()); // "Bonjour, le monde !"

        // Translate multiple texts into British English
        List<TextResult> results =
                client.translateText(List.of("お元気ですか？", "¿Cómo estás?"),
                                         null,
                                         "en-GB");
        System.out.println(results.get(0).getText()); // "How are you?"
        System.out.println(results.get(0).getDetectedSourceLanguage()); // "ja" the language code for Japanese
        System.out.println(results.get(0).getBilledCharacters()); // 7 - the number of characters in the source text "お元気ですか？"
        System.out.println(results.get(1).getText()); // "How are you?"
        System.out.println(results.get(1).getDetectedSourceLanguage()); // "es" the language code for Spanish
        System.out.println(results.get(1).getBilledCharacters()); // 12 - the number of characters in the source text "¿Cómo estás?"

        // Translate into German with less and more Formality:
        System.out.println(client.translateText("How are you?",
                                                    null,
                                                    "de",
                                                    new TextTranslationOptions().setFormality(
                                                            Formality.Less)).getText());  // 'Wie geht es dir?'
        System.out.println(client.translateText("How are you?",
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
- `setGlossary()`: specifies a glossary to use with translation, as a string
  containing the glossary ID, or a `GlossaryInfo`/`MultilingualGlossaryInfo` 
  object (this object is returned by glossary lookup functions, for example 
  `listGlossaries()` or `listMultilingualGlossaries()`).
    - `setGlossaryId()` is also available for backward-compatibility, accepting
      a string containing the glossary ID.
- `setContext()`: specifies additional context to influence translations, that is not
  translated itself. Characters in the `context` parameter are not counted toward billing.  
  See the [API documentation][api-docs-context-param] for more information and
  example usage.
- `model_type`: specifies the type of translation model to use, options are:
  - `'quality_optimized'`: use a translation model that maximizes translation quality,
    at the cost of response time. This option may be unavailable for some language pairs.
  - `'prefer_quality_optimized'`: use the highest-quality translation model for the given
    language pair.
  - `'latency_optimized'`: use a translation model that minimizes response time, at the
    cost of translation quality.
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

### Improving text (Write API)

You can use the Write API to improve or rephrase text. This is implemented in
the `rephraseText()` method. The first argument is a string containing the text
you want to translate, or a list of strings if you want to translate multiple texts.

`targetLang` optionally specifies the target language, e.g. when you want to change
the variant of a text (for example, you can send an english text to the write API and
use `targetLang` to turn it into British or American English). Please note that the
Write API itself does NOT translate. If you wish to translate and improve a text, you
will need to make multiple calls in a chain.

Language codes are the same as for translating text.

Example call:

```java
WriteResult result = client.rephraseText("A rainbouw has seven colours.", "EN-US", null);
System.out.println(result.getText);
```

Additionally, you can optionally specify a style OR a tone (not both at once) that the
improvement should be in. The following styles are supported (`default` will be used if
nothing is selected):

- `academic`
- `business`
- `casual`
- `default`
- `simple`

The following tones are supported (`default` will be used if nothing is selected):

- `confident`
- `default`
- `diplomatic`
- `enthusiastic`
- `friendly`

You can also prefix any non-default style or tone with `prefer_` (`prefer_academic`, etc.),
in which case the style/tone will only be applied if the language supports it. If you do not
use `prefer_`, requests with `targetLang`s or detected languages that do not support
styles and tones will fail. The current list of supported languages can be found in our
[API documentation][api-docs]. We plan to also expose this information via an API endpoint
in the future.

You can use the predefined constants in the library to use a style:

```java
TextRephraseOptions options = (new TextRephraseOptions()).setWritingStyle(WritingStyle.Business.getValue());
WriteResult result = client.rephraseText("A rainbouw has seven colours.", "EN-US", options);
System.out.println(result.getText);
```

### Translating documents

To translate documents, call `translateDocument()` File objects. The first and
second arguments correspond to the input and output files respectively.

Just as for the `translateText()` function, the `sourceLang` and
`targetLang` arguments specify the source and target language codes.

There are additional optional arguments to control translation, see
[Document translation options](#document-translation-options) below.

```java
class Example {  // Continuing class Example from above
    public void documentTranslationExamples() throws Exception {
        // Translate a formal document from English to German
        File inputFile = new File("/path/to/Instruction Manual.docx");
        File outputFile = new File("/path/to/Bedienungsanleitung.docx");
        try {
            client.translateDocument(inputFile, outputFile, "en", "de");
        } catch (DocumentTranslationException exception) {
            // If an error occurs during document translation after the document was
            // already uploaded, a DocumentTranslationException is thrown. The
            // document_handle property contains the document handle that may be used to
            // later retrieve the document from the server, or contact DeepL support.
            DocumentHandle handle = exception.getHandle();
            System.out.printf(
                    "Error after uploading %s, document handle: id: %s key: %s",
                    exception.getMessage(),
                    handle.getDocumentId(),
                    handle.getDocumentKey());
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
arguments, `translateDocument()` accepts an optional
`DocumentTranslationOptions`, with the following setters:

- `setFormality()`: same as
  in [Text translation options](#text-translation-options).
- `setGlossary()`: same as
  in [Text translation options](#text-translation-options).
- `setGlossaryId()`: same as
  in [Text translation options](#text-translation-options).

### Glossaries

Glossaries allow you to customize your translations using user-defined terms.
Multiple glossaries can be stored with your account, each with a user-specified
name and a uniquely-assigned ID.

### v2 versus v3 glossary APIs

The newest version of the glossary APIs are the `/v3` endpoints, allowing both
editing functionality plus support for multilingual glossaries. New methods and
objects have been created to support interacting with these new glossaries.
Due to this new functionality, users are recommended to utilize these
multilingual glossary methods. However, to continue using the `v2` glossary API
endpoints, please continue to use the existing endpoints in the `translator.java`
(e.g. `createGlossary()`, `getGlossary()`, etc).

To migrate to use the new multilingual glossary methods from the current
monolingual glossary methods, please refer to
[this migration guide](upgrading_to_multilingual_glossaries.md).

The following sections describe how to interact with multilingual glossaries
using the new functionality:

#### Creating a glossary

You can create a glossary with `createMultilingualGlossary()` by passing your
desired glossary name, and a `GlossaryEntries` object specifying the terms to
store in the glossary.

Each glossary contains a list of dictionaries, where each dictionary applies to a single source-target language pair. Note: Glossaries
are only supported for some language pairs, see
[Listing available glossary languages](#listing-available-glossary-languages)
for more information.

If successful, the glossary is created and stored with your DeepL account, and
a `MultilingualGlossaryInfo` object is returned including the ID, name, 
languages and entry count.

```java
class Example {  // Continuing class Example from above
    public void createGlossaryExample() throws Exception {
        // Create an English to German glossary with two terms:
        GlossaryEntries entries = new GlossaryEntries() {{
            put("artist", "Maler");
            put("prize", "Gewinn");
        }};
        MultilingualGlossaryDictionaryEntries myGlossaryDicts = 
                Arrays.asList(new MultilingualGlossaryDictionaryEntries(
                                "en",
                                "de",
                                entries
                              ));
        MultilingualGlossaryInfo myGlossary =
                client.createGlossary("My glossary", myGlossaryDicts);

        System.out.printf("Created '%s' (%s) containing %d dictionary from %s->%s with %d entries\n",
                          myGlossary.getName(),
                          myGlossary.getGlossaryId(),
                          myGlossary.getDictionaries().length,
                          myGlossary.getSourceLang(),
                          myGlossary.getTargetLang(),
                          myGlossary.getEntryCount());
        // Example: Created 'My glossary' (559192ed-8e23-...) containing 1 
        // dictionary from en->de containing 2 entries
    }
}
```

To construct the GlossaryEntries, you can insert entries using typical Map 
functions like `put()`. The `fromTsv()` function allows creating GlossaryEntries
from TSV data.

You can also create a glossary using a glossary downloaded from the DeepL
website by using `createMultilingualGlossaryFromCsv()` with either a CSV file,
or a string containing the CSV data:

```java
class Example {  // Continuing class Example from above
    public createGlossaryFromCsvExample() throws Exception {
        File csvFile = new File("/path/to/glossary_file.csv");
        MultilingualGlossaryInfo myGlossary =
                client.createMultilingualGlossaryFromCsv("My glossary",
                                                         "en",
                                                         "de",
                                                         csvFile);
    }
}
```

The [API documentation][api-docs-csv-format] explains the expected CSV format in
detail.


#### Getting, listing and deleting stored glossaries

Functions to get, list, and delete stored glossaries are also provided:

- `getMultilingualGlossary()` takes a glossary ID and returns a
  `MultilingualGlossaryInfo` object for a stored glossary, or raises an
  exception if no such glossary is found.
- `listMultilingualGlossaries()` returns a list of `MultilingualGlossaryInfo`
  objects corresponding to all of your stored glossaries.
- `deleteMultilingualGlossary()` takes a glossary ID or
  `MultilingualGlossaryInfo` object and deletes the stored glossary from the
  server, or raises an exception if no such glossary is found.
- `deleteMultilingualGlossaryDictionary()` takes a glossary ID or
  `MultilingualGlossaryInfo` object to identify the glossary. Additionally
  takes in a source and target language or a
  `MultilingualGlossaryDictionaryInfo` object and deletes the stored dictionary
  from the server, or raises an exception if no such glossary dictionary is
  found.

```java
class Example {  // Continuing class Example from above
    public getListDeleteGlossaryExamples() throws Exception {
        // Retrieve a stored glossary using the ID
        String glossaryId = "559192ed-8e23-...";
        MultilingualGlossaryInfo myGlossary = 
          client.getMultilingualGlossary(glossaryId);

        client.deleteMultilingualGlossaryDictionary(glossaryId, 
                                                    myGlossary.getDictionaries()[0]);

        // Find and delete glossaries named 'Old glossary'
        List<MultilingualGlossaryInfo> glossaries = 
          client.listMultilingualGlossaries();
        for (MultilingualGlossaryInfo glossary : glossaries) {
            if (glossary.getName() == "Old glossary") {
                client.deleteMultilingualGlossary(glossary);
            }
        }
    }
}
```

#### Listing entries in a stored glossary

The `MultilingualGlossaryDictionaryInfo` object does not contain the glossary
entries, but instead only the number of entries in the `entry_count` property.

To list the entries contained within a stored glossary, use
`getMultilingualGlossaryDictionaryEntries()` providing either the 
`MultilingualGlossaryInfo` object or glossary ID and either a 
`MultilingualGlossaryDictionaryInfo` or source and target language pair:

```java
class Example {  // Continuing class Example from above
  public getGlossaryEntriesExample() throws Exception {
      List<MultilingualGlossaryDictionaryInfo> glossaryDicts = 
        client.getMultilingualGlossaryDictionaryEntries(myGlossary, "en", "de");
      
      for (Map.Entry<String, String> entry : glossaryDicts.getDictionaries()[0].getEntries().entrySet()) {
        System.out.println(entry.getKey() + ":" + entry.getValue());
      }
      // prints:
      //   artist:Maler
      //   prize:Gewinn
  }
}
```

#### Editing a glossary

Functions to edit stored glossaries are also provided:

- `updateMultilingualGlossaryDictionary()` takes a glossary ID or `MultilingualGlossaryInfo`
  object, plus a source language, target language, and a dictionary of entries.
  It will then either update the list of entries for that dictionary (either
  inserting new entires or replacing the target phrase for any existing
  entries) or will insert a new glossary dictionary if that language pair is
  not currently in the stored glossary.
- `replaceMultilingualGlossaryDictionary()` takes a glossary ID or `MultilingualGlossaryInfo`
  object, plus a source language, target language, and a dictionary of entries.
  It will then either set the entries to the parameter value, completely
  replacing any pre-existing entries for that language pair.
- `updateMultilingualGlossaryName()` takes a glossary ID or `MultilingualGlossaryInfo`
  object, plus the new name of the glossary.

```java
// Update glossary dictionary
class Example {  // Continuing class Example from above
  public updateGlossaryEntriesExample() throws Exception {
    GlossaryEntries entries = new GlossaryEntries() {{
              put("artist", "Maler");
              put("hello", "guten tag");
          }};
    List<MultilingualGlossaryDictionaryEntries> dictionaries = 
      Arrays.asList(new MultilingualGlossaryDictionaryEntries("EN", "DE", entries));
    MultilingualGlossaryInfo myGlossary = client.createMultilingualGlossary(
        "My glossary",
        dictionaries
    );

    GlossaryEntries newEntries = new GlossaryEntries() {{
              put("hello", "hallo");
              put("prize", "Gewinn");
          }};

    MultilingualGlossaryDictionaryEntries glossaryDict = 
      new MultilingualGlossaryDictionaryEntries("EN", "DE", newEntries);

    MultilingualGlossaryInfo updatedGlossary = 
      client.updateMultilingualGlossaryDictionary(myGlossary, glossaryDict);

    MultilingualGlossaryInfo entriesResponse = 
      client.getMultilingualGlossaryDictionaryEntries(myGlossary, "EN", "DE");

    for (Map.Entry<String, String> entry : glossaryDicts.getDictionaries()[0].getEntries().entrySet()) {
      System.out.println(entry.getKey() + ":" + entry.getValue());
    }
    
    // prints:
    //   artist:Maler
    //   hello:hallo
    //   prize:Gewinn
  }

  // Update a glossary dictionary from CSV
  public updateGlossaryEntriesFromCsvExample() throws Exception {
    File csvFile = new File("/path/to/glossary_file.csv");
    String glossaryId = "559192ed-8e23-...";
    MultilingualGlossaryInfo myGlossary =
      client.createMultilingualGlossaryDictionaryFromCsv(glossaryId,
                                                        "en",
                                                        "de",
                                                        csvFile);
  }


  // Update a glossary name
  public void updateGlossaryNameExample() throws Exception {
    String glossaryId = "559192ed-8e23-...";
    MultilingualGlossaryInfo myGlossary =
      client.updateMultilingualName(glossaryId, "My new glossary name");
    System.out.println(myGlossary.getName()); // 'My new glossary name'
  }

  // Replace a glossary dictionary
  public void replaceGlossaryEntriesExample() throws Exception {
    GlossaryEntries entries = new GlossaryEntries() {{
              put("artist", "Maler");
              put("hello", "guten tag");
          }};
    List<MultilingualGlossaryDictionaryEntries> dictionaries = 
      Arrays.asList(new MultilingualGlossaryDictionaryEntries("EN", "DE", entries));
    MultilingualGlossaryInfo myGlossary = client.createMultilingualGlossary(
        "My glossary",
        dictionaries
    );

    GlossaryEntries newEntries = new GlossaryEntries() {{
              put("goodbye", "Auf Weidersehen");
          }};

    MultilingualGlossaryDictionaryEntries glossaryDict = 
      new MultilingualGlossaryDictionaryEntries("EN", "DE", newEntries);

    MultilingualGlossaryInfo updatedGlossary = 
      client.replaceMultilingualGlossaryDictionary(myGlossary, glossaryDict);

    MultilingualGlossaryInfo entriesResponse = 
      client.getMultilingualGlossaryDictionaryEntries(myGlossary, "EN", "DE");

    for (Map.Entry<String, String> entry : glossaryDicts.getDictionaries()[0].getEntries().entrySet()) {
      System.out.println(entry.getKey() + ":" + entry.getValue());
    }
    
    // prints:
    //   goodbye:Auf Weidersehen
  }

  // Replace a glossary dictionary from CSV
  public void replaceGlossaryEntriesFromCsvExample() throws Exception {
    File csvFile = new File("/path/to/glossary_file.csv");
    String glossaryId = "559192ed-8e23-...";
    MultilingualGlossaryInfo myGlossary =
      client.replaceMultilingualGlossaryDictionaryFromCsv(glossaryId,
                                                          "en",
                                                          "de",
                                                          csvFile);
    MultilingualGlossaryInfo entriesResponse = 
      client.getMultilingualGlossaryDictionaryEntries(myGlossary, "EN", "DE");
  }
}
```

#### Using a stored glossary

You can use a stored glossary for text translation by setting the `glossary`
argument to either the glossary ID or `MultilingualGlossaryInfo` object. You must also
specify the `source_lang` argument (it is required when using a glossary):

```java
class Example {  // Continuing class Example from above
    public boid usingGlossaryExample() throws Exception {
        String text = "The artist was awarded a prize.";
        TextTranslationOptions options =
                new TextTranslationOptions().setGlossary(my_glossary);
        TextResult resultWithGlossary =
                client.translateText(text, "en", "de", options);
        System.out.println(resultWithGlossary.getText()); // "Der Maler wurde mit einem Gewinn ausgezeichnet."

        // For comparison, the result without a glossary:
        TextResult resultWithoutGlossary =
                client.translateText(text, "en", "de");
        System.out.println(resultWithoutGlossary.getText()); // "Der Künstler wurde mit einem Preis ausgezeichnet."
    }
}
```

Using a stored glossary for document translation is the same: set the `glossary`
argument and specify the `source_lang` argument:

```java
class Example {  // Continuing class Example from above
    public boid getListDeleteGlossaryExamples() throws Exception {
        String glossaryId = "559192ed-8e23-...";
        DocumentTranslationOptions options =
                new DocumentTranslationOptions().setGlossary(glossaryId);

        File inputFile = new File("/path/to/Instruction Manual.docx");
        File outputFile = new File("/path/to/Bedienungsanleitung.docx");
        client.translateDocument(inputFile,
                                     outputFile,
                                     "en",
                                     "de",
                                     options);
    }
}
```

The `translateDocument()` and `translateDocumentUpload()` functions both
support the `glossary` argument.

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
class Example {  // Continuing class Example from above
    public void getUsageExample() throws Exception {
        Usage usage = client.getUsage();
        if (usage.anyLimitReached()) {
            System.out.println("Translation limit reached.");
        }
        if (usage.getCharacter() != null) {
            System.out.printf("Character usage: %d of %d%n",
                              usage.getCharacter().getCount(),
                              usage.getCharacter().getLimit());
        }
        if (usage.getDocument() != null) {
            System.out.printf("Document usage: %d of %d%n",
                              usage.getDocument().getCount(),
                              usage.getDocument().getLimit());
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
class Example {  // Continuing class Example from above
    public void getLanguagesExample() throws Exception {
        List<Language> sourceLanguages = client.getSourceLanguages();
        List<Language> targetLanguages = client.getTargetLanguages();
        System.out.println("Source languages:");
        for (Language language : sourceLanguages) {
            System.out.printf("%s (%s)%n",
                              language.getName(),
                              language.getCode()); // Example: "German (de)"
        }

        System.out.println("Target languages:");
        for (Language language : targetLanguages) {
            if (language.getSupportsFormality()) {
                System.out.printf("%s (%s) supports formality%n",
                                  language.getName(),
                                  language.getCode()); // Example: "Italian (it) supports formality"

            } else {
                System.out.printf("%s (%s)%n",
                                  language.getName(),
                                  language.getCode()); // Example: "Lithuanian (lt)"
            }
        }
    }
}
```

#### Listing available glossary languages

Glossaries are supported for a subset of language pairs. To retrieve those
languages use the `getGlossaryLanguages()` function, which returns an array
of `GlossaryLanguagePair` objects. Use the `getSourceLanguage()` and
`getTargetLanguage()` functions to check the pair of language codes supported.

```java
class Example {  // Continuing class Example from above
    public void getGlossaryLanguagesExample() throws Exception {
        List<GlossaryLanguagePair> glossaryLanguages =
                client.getGlossaryLanguages();
        for (GlossaryLanguagePair glossaryLanguage : glossaryLanguages) {
            System.out.printf("%s to %s\n",
                              glossaryLanguage.getSourceLanguage(),
                              glossaryLanguage.getTargetLanguage());
            // Example: "en to de", "de to en", etc.
        }
    }
}
```

You can also find the list of supported glossary language pairs in the
[API documentation][api-docs-glossary-lang-list].

Note that glossaries work for all target regional-variants: a glossary for the
target language English (`"en"`) supports translations to both American English
(`"en-US"`) and British English (`"en-GB"`).

### Exceptions

All module functions may raise `DeepLException` or one of its subclasses. If
invalid arguments are provided, they may raise the standard exceptions
`IllegalArgumentException`.

### Writing a Plugin

If you use this library in an application, please identify the application with
`DeepLClientOptions.setAppInfo()`, which takes the name and version of the app:

```java
class Example {  // Continuing class Example from above
    public void configurationExample() throws Exception {
        DeepLClientOptions options =
                new DeepLClientOptions().setAppInfo("my-java-translation-plugin", "1.2.3");
        DeepLClient client = new DeepLClient(authKey, options);
    }
}
```

This information is passed along when the library makes calls to the DeepL API.
Both name and version are required. Please note that setting the `User-Agent` header
via `DeepLClientOptions.setHeaders()` will override this setting, if you need to use this,
please manually identify your Application in the `User-Agent` header.

### Configuration

The `DeepLClient` constructor accepts `DeepLClientOptions` as a second argument,
for example:

```java
class Example {  // Continuing class Example from above
    public void configurationExample() throws Exception {
        DeepLClientOptions options =
                new DeepLClientOptions().setMaxRetries(1).setTimeout(Duration.ofSeconds(
                        1));
        DeepLClient client = new DeepLClient(authKey, options);
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
- `setApiVersion()`: Version of the DeepL API, may be overridden to use e.g.
  the v1 API. By default, the most recent API version is automatically selected.
  Please note: The v1 API does not support all features of the API, e.g.
  document translation or rephrase.

#### Anonymous platform information

By default, we send some basic information about the platform the client library is running on with each request, see [here for an explanation](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/User-Agent). This data is completely anonymous and only used to improve our product, not track any individual users. If you do not wish to send this data, you can opt-out when creating your `DeepLClient` object by calling the `setSendPlatformInfo()` setter on the `DeepLClientOptions` like so:

```java
class Example {  // Continuing class Example from above
    public void configurationExample() throws Exception {
        DeepLClientOptions options =
                new DeepLClientOptions().setSendPlatformInfo(false);
        DeepLClient client = new DeepLClient(authKey, options);
    }
}
```

You can also customize the `User-Agent` header by setting its value explicitly in the `DeepLClientOptions` object via the header field. Example:

```java
class Example {  // Continuing class Example from above
    public void configurationExample() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "my custom user agent");
        DeepLClientOptions options =
                new DeepLClientOptions().setHeaders(headers);
        DeepLClient client = new DeepLClient(authKey, options);
    }
}
```

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

[api-docs-context-param]: https://www.deepl.com/docs-api/translating-text/?utm_source=github&utm_medium=github-java-readme

[api-docs-csv-format]: https://www.deepl.com/docs-api/managing-glossaries/supported-glossary-formats/?utm_source=github&utm_medium=github-java-readme

[api-docs-xml-handling]: https://www.deepl.com/docs-api/handling-xml/?utm_source=github&utm_medium=github-java-readme

[api-docs-lang-list]: https://www.deepl.com/docs-api/translating-text/?utm_source=github&utm_medium=github-java-readme

[api-docs-glossary-lang-list]: https://www.deepl.com/docs-api/managing-glossaries/?utm_source=github&utm_medium=github-java-readme

[create-account]: https://www.deepl.com/pro?utm_source=github&utm_medium=github-java-readme#developer

[deepl-mock]: https://www.github.com/DeepLcom/deepl-mock

[issues]: https://www.github.com/DeepLcom/deepl-java/issues

[pro-account]: https://www.deepl.com/pro-account/?utm_source=github&utm_medium=github-java-readme
