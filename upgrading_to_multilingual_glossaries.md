# Migration Documentation for Newest Glossary Functionality

## 1. Overview of Changes

The newest version of the Glossary APIs is the `/v3` endpoints, which introduce enhanced functionality:

- **Support for Multilingual Glossaries**: The v3 endpoints allow for the creation of glossaries with multiple language
  pairs, enhancing flexibility and usability.
- **Editing Capabilities**: Users can now edit existing glossaries.

To support these new v3 APIs, we have created new methods to interact with these new multilingual glossaries. Users are
encouraged to transition to the new to take full advantage of these new features. However, for those who prefer to
continue using the existing functionality, the `v2` methods for monolingual glossaries (e.g., `createGlossary()`,
`getGlossary()`, etc.) remain available.

## 2. Endpoint Changes

| Monolingual glossary methods   | Multilingual glossary methods                | Changes Summary                                                                                                                                                                      |
|--------------------------------|----------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `createGlossary()`        | `createMultilingualGlossary()`          | Accepts a list of `MultilingualGlossaryDictionaryEntries` for multi-lingual support and now returns a `MultilingualGlossaryInfo` object.                                             |
| `createGlossaryFromCsv()` | `createMultilingualGlossaryFromCsv()`   | Similar functionality, but now returns a `MultilingualGlossaryInfo` object                                                                                                           |
| `getGlossary()`           | `getMultilingualGlossary()`             | Similar functionality, but now returns `MultilingualGlossaryInfo`. Also can accept a `MultilingualGlossaryInfo` object as the glossary parameter instead of a `GlossaryInfo` object. |
| `listGlossaries()`        | `listMultilingualGlossaries()`          | Similar functionality, but now returns a list of `MultilingualGlossaryInfo` objects.                                                                                                 |
| `getGlossaryEntries()`    | `getMultilingualGlossaryDictionaryEntries()` | Requires specifying source and target languages. Also returns a `MultilingualGlossaryDictionaryEntriesResponse` object as the response.                                              |
| `deleteGlossary()`        | `deleteMultilingualGlossary()`          | Similar functionality, but now can accept a `MultilingualGlossaryInfo` object instead of a `GlossaryInfo` object when specifying the glossary.                                       |

## 3. Model Changes

V2 glossaries are monolingual and the previous glossary objects could only have entries for one language pair (
`SourceLanguageCode` and `TargetLanguageCode`). Now we introduce the concept of "glossary dictionaries", where a
glossary dictionary specifies its own `SourceLanguageCode`, `TargetLanguageCode`, and has its own entries.

- **Glossary Information**:
  - **v2**: `GlossaryInfo` supports only mono-lingual glossaries, containing fields such as `SourceLanguageCode`,
    `TargetLanguageCode`, and `EntryCount`.
  - **v3**: `MultilingualGlossaryInfo` supports multi-lingual glossaries and includes a list of
    `MultilingualGlossaryDictionaryInfo`, which provides details about each glossary dictionary, each with its own
    `SourceLanguageCode`, `TargetLanguageCode`, and `EntryCount`.

- **Glossary Entries**:
  - **v3**: Introduces `MultilingualGlossaryDictionaryEntries`, which encapsulates a glossary dictionary with source and
    target languages along with its entries.

## 4. Code Examples

### Create a glossary

```java
class Example {
  // monolingual glossary example
  public void createGlossaryExample() throws Exception {
    GlossaryEntries entries = new GlossaryEntries() {{
              put("hello", "hallo");
    }};
    GlossaryInfo glossaryInfo = client.createGlossary("My Glossary", "EN", "DE", entries);
  }
  
  // multilingual glossary example
  public void createMultilingualGlossaryExample() throws Exception {
    GlossaryEntries entries = new GlossaryEntries() {{
      put("hello", "hallo");
    }};
    List<MultilingualGlossaryDictionaryEntries> glossaryDicts =
            Arrays.asList(new MultilingualGlossaryDictionaryEntries("EN", "DE", entries));
    MultilingualGlossaryInfo glossaryInfo = client.createMultilingualGlossary("My Glossary", glossaryDicts);
  }
}
```

### Get a glossary

```java
class Example {
  // monolingual glossary example
  public void getGlossaryExample() throws Exception {
    GlossaryEntries entries = new GlossaryEntries() {{
      put("hello", "hallo");
    }};
    GlossaryInfo createdGlossary = client.createGlossary("My Glossary", "EN", "DE", entries);
    GlossaryInfo glossaryInfo = client.getGlossary(createdGlossary); // GlossaryInfo object
  }
  
  // multilingual glossary example
  public void getMultilingualGlossaryExample() throws Exception {
    GlossaryEntries entries = new GlossaryEntries() {{
      put("hello", "hallo");
    }};
    List<MultilingualGlossaryDictionaryEntries> glossaryDicts =
            Arrays.asList(new MultilingualGlossaryDictionaryEntries("EN", "DE", entries));
    MultilingualGlossaryInfo createdGlossary = client.createMultilingualGlossary("My Glossary", glossaryDicts);
    GlossaryInfo glossaryInfo = client.getGlossary(createdGlossary); // MultilingualGlossaryInfo object
  }
}
```

### Get glossary entries

```java
class Example {
  // monolingual glossary example
  public void getGlossaryEntriesExample() throws Exception {
    GlossaryEntries entries = new GlossaryEntries() {{
      put("hello", "hallo");
    }};
    GlossaryInfo createdGlossary = client.createGlossary("My Glossary", "EN", "DE", entries);
    GlossaryEntries entries = client.getGlossaryEntries(createdGlossary);
    System.out.println(entries.toTsv()); // 'hello\thallo'
  }  
  
  // mutlilingual glossary example
  public void getMultilingualGlossaryEntriesExample() throws Exception {
    GlossaryEntries entries = new GlossaryEntries() {{
      put("hello", "hallo");
    }};
    List<MultilingualGlossaryDictionaryEntries> glossaryDicts =
            Arrays.asList(new MultilingualGlossaryDictionaryEntries("EN", "DE", entries));
    MultilingualGlossaryInfo createdGlossary = client.createMultilingualGlossary("My Glossary", glossaryDicts);
    MultilingualGlossaryInfo dictEntries = client.getMultilingualGlossaryDictionaryEntries(createdGlossary, "EN", "DE");
    System.out.println(dictEntries.getDictionaries()[0].getEntries().ToTsv()); // 'hello\thallo'
  }
}
```

### List and delete glossaries

```java
class Example {
  // monolingual glossary example
  public void getListDeleteGlossaryExamples() throws Exception {
    List<GlossaryInfo> glossaries = client.listGlossaries();
    for (GlossaryInfo glossary : glossaries) {
      if (glossary.getName() == "Old glossary") {
        client.deleteGlossary(glossary);
      }
    }
  }
  
  // multilingual glossary example
  public void getListDeleteMultilingualGlossaryExamples() throws Exception {
    List<MultilingualGlossaryInfo> glossaries = client.listMultilingualGlossaries();
    for (MultilingualGlossaryInfo glossary : glossaries) {
      if (glossary.getName() == "Old glossary") {
        client.deleteMultilingualGlossary(glossary);
      }
    }
  }
}
```

## 5. New Multilingual Glossary Methods

In addition to introducing multilingual glossaries, we introduce several new methods that enhance the functionality for
managing glossaries. Below are the details for each new method:

### Update Multilingual Glossary Dictionary

- **Method Overloads**:
  - `MultilingualGlossaryInfo updateMultilingualGlossaryDictionary(String glossaryId, String
  sourceLanguageCode, String targetLanguageCode, GlossaryEntries entries)`
  - `MultilingualGlossaryInfo updateMultilingualGlossaryDictionary(MultilingualGlossaryInfo glossary, String
  sourceLanguageCode, String targetLanguageCode, GlossaryEntries entries)`
  - `MultilingualGlossaryInfo updateMultilingualGlossaryDictionary(String glossaryId,
  MultilingualGlossaryDictionaryEntries glossaryDict)`
  - `MultilingualGlossaryInfo updateMultilingualGlossaryDictionary(MultilingualGlossaryInfo glossary,
  MultilingualGlossaryDictionaryEntries glossaryDict)`
- **Description**: Updates a glossary dictionary with new entries.
- **Parameters:**
  - `String glossaryId`: ID of the glossary to update.
  - `String sourceLanguageCode`: Source language code for the glossary dictionary.
  - `String targetLanguageCode`: Target language code for the glossary dictionary.
  - `GlossaryEntries entries`: The source-target entry pairs.
  - `MultilingualGlossaryDictionaryEntries glossaryDict`: The glossary dictionary to update.
- **Returns**: `MultilingualGlossaryInfo` containing details about the updated glossary.
- **Exceptions**:
  - `IllegalArgumentException`: Thrown if any argument is invalid.
  - `DeepLException`: Thrown if any error occurs while communicating with the DeepL API.
  - `InterruptedException`: If the thread is interrupted during execution of this function.
- **Example**:

```java
class Example { 
    public void updateGlossaryEntriesExample() throws Exception {
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

        for (Map.Entry<String, String> entry : entriesResponse.getDictionaries()[0].getEntries().entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        // prints:
        //   artist:Maler
        //   hello:hallo
        //   prize:Gewinn
    }
}
```

### Update Multilingual Glossary Dictionary from CSV

- **Method**:
  - `MultilingualGlossaryInfo updateMultilingualGlossaryDictionaryFromCsv(String glossaryId, String
  sourceLanguageCode, String targetLanguageCode, File csvFile)`
  - `MultilingualGlossaryInfo updateMultilingualGlossaryDictionaryFromCsv(MultilingualGlossaryInfo glossary,
  String sourceLanguageCode, String targetLanguageCode, File csvFile)`
- **Description**: This method allows you to update or create a glossary dictionary using entries in CSV format.
- **Parameters**:
  - `String glossaryId`: The ID of the glossary to update.
  - `MultilingualGlossaryInfo glossary`: The `MultilingualGlossaryInfo` object representing the glossary to update
  - `String sourceLanguageCode`: Language of source entries.
  - `String targetLanguageCode`: Language of target entries.
  - `File csvFile`: The CSV data containing glossary entries as a stream.
- **Returns**: `MultilingualGlossaryInfo` containing information about the updated glossary.
- **Exceptions**:
  - `IllegalArgumentException`: Thrown if any argument is invalid.
  - `DeepLException`: Thrown if any error occurs while communicating with the DeepL API.
  - `InterruptedException`: If the thread is interrupted during execution of this function.
  - `IOException`: If an I/O error occurs.
- **Example**:
  ```java
  class Example {
    public void updateGlossaryEntriesFromCsvExample() throws Exception {
        File csvFile = new File("/path/to/glossary_file.csv");
        String glossaryId = "559192ed-8e23-...";
        MultilingualGlossaryInfo myGlossary = 
            client.updateMultilingualGlossaryDictionaryFromCsv(glossaryId, "en", "de", csvFile);
    }
  }
  ```

### Update Multilingual Glossary Name

- **Method**:
  `MultilingualGlossaryInfo updateMultilingualGlossaryName(String glossaryId, String name)`
- **Description**: This method allows you to update the name of an existing glossary.
- **Parameters**:
  - `String glossary`: The ID of the glossary to update.
  - `String name`: The new name for the glossary.
- **Returns**: `MultilingualGlossaryInfo` containing information about the updated glossary.
- **Exceptions**:
  - `IllegalArgumentException`: Thrown if any argument is invalid.
  - `DeepLException`: Thrown if any error occurs while communicating with the DeepL API.
- **Example**:
  ```java
  class Example {
    public void updateGlossaryNameExample() throws Exception {
        String glossaryId = "559192ed-8e23-...";
        MultilingualGlossaryInfo myGlossary = client.updateMultilingualGlossaryName(glossaryId, "My new glossary name");
        System.out.println(myGlossary.getName()); // 'My new glossary name'
    }
  }
  ```

### Replace a Multilingual Glossary Dictionary

- **Method**:
  - `MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionary(String glossaryId, String
  sourceLanguageCode, String targetLanguageCode, GlossaryEntries entries)`
  - `MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionary(MultilingualGlossaryInfo
  glossary, String sourceLanguageCode, String targetLanguageCode, GlossaryEntries entries)`
  - `MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionary(String glossaryId,
  MultilingualGlossaryDictionaryEntries glossaryDict)`
  - `MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionary(MultilingualGlossaryInfo
  glossary, MultilingualGlossaryDictionaryEntries glossaryDict)`
- **Description**: This method replaces the existing glossary dictionary with a new set of entries.
- **Parameters**:
  - `String glossaryId`: ID of the glossary whose dictionary will be replaced.
  - `String sourceLanguageCode`: Source language code for the glossary dictionary.
  - `String targetLanguageCode`: Target language code for the glossary dictionary.
  - `GlossaryEntries entries`: The source-target entries that will replace any existing ones for that language pair.
  - `MultilingualGlossaryDictionaryEntries glossaryDict`: The glossary dictionary to update.
- **Returns**: `MultilingualGlossaryDictionaryInfo` containing information about the replaced glossary dictionary.
- **Exceptions**:
  - `IllegalArgumentException`: Thrown if any argument is invalid.
  - `DeepLException`: Thrown if any error occurs while communicating with the DeepL API.
  - `InterruptedException`: If the thread is interrupted during execution of this function.
- **Note**: Ensure that the new dictionary entries are complete and valid, as this method will completely overwrite the
  existing entries. It will also create a new glossary dictionary if one did not exist for the given language pair.
- **Example**:
  ```java
  class Example {
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

        GlossaryEntries newEntries = new GlossaryEntries() {{put("goodbye", "Auf Weidersehen");}};
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
  }
  ```

### Replace Multilingual Glossary Dictionary from CSV

- **Method**:
  - `MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionaryFromCsv(String glossaryId, String
  sourceLanguageCode, String targetLanguageCode, Stream csvFile)`
  - `MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionaryFromCsv(MultilingualGlossaryInfo
  glossary, String sourceLanguageCode, String targetLanguageCode, Stream csvFile)`
- **Description**: This method allows you to replace or create a glossary dictionary using entries in CSV format.
- **Parameters**:
  - `String glossaryId`: The ID of the glossary whose dictionary will be replaced.
  - `MultilingualGlossaryInfo glossary`: The `MultilingualGlossaryInfo` object representing the glossary whose
    dictionary will be replaced.
  - `String sourceLanguageCode`: Language of source entries.
  - `String targetLanguageCode`: Language of target entries.
  - `Stream csvFile`: The CSV data containing glossary entries as a stream.
- **Returns**: `MultilingualGlossaryDictionaryInfo` containing information about the replaced glossary dictionary.
- **Exceptions**:
  - `IllegalArgumentException`: Thrown if any argument is invalid.
  - `DeepLException`: Thrown if any error occurs while communicating with the DeepL API.
  - `InterruptedException`: If the thread is interrupted during execution of this function.
  - `IOException`: If an I/O error occurs.
- **Example**:
  ```java
  class Example {
    public void replaceGlossaryEntriesFromCsvExample() throws Exception {
        File csvFile = new File("/path/to/glossary_file.csv");
        String glossaryId = "559192ed-8e23-...";
        MultilingualGlossaryInfo myGlossary = 
            client.replaceMultilingualGlossaryDictionaryFromCsv(glossaryId, "en", "de", csvFile);   
        MultilingualGlossaryInfo entriesResponse =
            client.getMultilingualGlossaryDictionaryEntries(myGlossary, "EN", "DE");
    }
  }
  ```

### Delete a Multilingual Glossary Dictionary

- **Method**:
  - `Task deleteMultilingualGlossaryDictionary(MultilingualGlossaryInfo glossary, String sourceLanguageCode, String
  targetLanguageCode)`
  - `Task deleteMultilingualGlossaryDictionary(String glossaryId, String sourceLanguageCode, String
  targetLanguageCode)`
- **Description**: This method deletes a specified glossary dictionary from a given glossary.
- **Parameters**:
  - `String glossaryId`: The ID of the glossary containing the dictionary to delete.
  - `MultilingualGlossaryInfo glossary`: The `MultilingualGlossaryInfo` object of the glossary containing the dictionary
    to delete.
  - `MultilingualGlossaryDictionaryInfo dictionary`: The `MultilingualGlossaryDictionaryInfo` object that specifies the
  - dictionary to delete.
  - `String sourceLanguageCode`: The source language of the glossary dictionary.
  - `String targetLanguageCode`: The target language of the glossary dictionary.
- **Returns**: A Task

- **Migration Note**: Ensure that your application logic correctly identifies the dictionary to delete. If using
  `sourceLanguageCode` and `targetLanguageCode`, both must be provided to specify the dictionary.

- **Example**:
  ```java
  class Example {
    public void deleteGlossaryDictionaryExample() throws Exception {
        GlossaryEntries entriesEnde = new GlossaryEntries() {{put("hello", "hallo");}};
        GlossaryEntries entriesDeen = new GlossaryEntries() {{put("hallo", "hello");}};
        MultilingualGlossaryDictionaryEntries glossaryDictEnde = 
            new MultilingualGlossaryDictionaryEntries("EN", "DE", entriesEnde);
        MultilingualGlossaryDictionaryEntries glossaryDictDeen = 
            new MultilingualGlossaryDictionaryEntries("EN", "DE", entriesDeen);
        List<MultilingualGlossaryDictionaryEntries> glossaryDicts =
            Arrays.asList(new MultilingualGlossaryDictionaryEntries("EN", "DE", entriesEnde),
                          new MultilingualGlossaryDictionaryEntries("DE", "EN", entriesDeen));
        MultilingualGlossaryInfo glossaryInfo = client.createMultilingualGlossary("My Glossary", glossaryDicts);
  
        // Delete via specifying the glossary dictionary
        client.deleteMultilingualGlossaryDictionary(glossaryInfo, glossaryDictEnde);
  
        // Delete via specifying the language pair
        client.deleteMultilingualGlossaryDictionary(glossaryInfo, "DE", "EN");
    }
  }
  ```
