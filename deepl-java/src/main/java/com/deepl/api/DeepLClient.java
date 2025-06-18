// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.

package com.deepl.api;

import com.deepl.api.http.HttpResponse;
import com.deepl.api.utils.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class DeepLClient extends Translator {

  /**
   * Initializes a new DeepLClient object using your Authentication Key.
   *
   * <p>Note: This function does not establish a connection to the DeepL API. To check connectivity,
   * use {@link DeepLClient#getUsage()}.
   *
   * @param authKey DeepL Authentication Key as found in your <a
   *     href="https://www.deepl.com/pro-account/">DeepL account</a>.
   * @throws IllegalArgumentException If authKey is invalid.
   */
  public DeepLClient(String authKey) throws IllegalArgumentException {
    this(authKey, new DeepLClientOptions());
  }

  /**
   * Initializes a new DeepLClient object using your Authentication Key.
   *
   * <p>Note: This function does not establish a connection to the DeepL API. To check connectivity,
   * use {@link DeepLClient#getUsage()}.
   *
   * @param authKey DeepL Authentication Key as found in your <a
   *     href="https://www.deepl.com/pro-account/">DeepL account</a>.
   * @param options Additional options controlling Client behaviour.
   * @throws IllegalArgumentException If authKey is invalid.
   * @deprecated Use the constructor that takes {@link DeepLClientOptions} instead of {@link
   *     TranslatorOptions}
   */
  @Deprecated
  public DeepLClient(String authKey, TranslatorOptions options) throws IllegalArgumentException {
    super(authKey, options);
  }

  /**
   * Initializes a new DeepLClient object using your Authentication Key.
   *
   * <p>Note: This function does not establish a connection to the DeepL API. To check connectivity,
   * use {@link DeepLClient#getUsage()}.
   *
   * @param authKey DeepL Authentication Key as found in your <a
   *     href="https://www.deepl.com/pro-account/">DeepL account</a>.
   * @param options Additional options controlling Client behaviour.
   * @throws IllegalArgumentException If authKey is invalid.
   */
  @SuppressWarnings("deprecation")
  public DeepLClient(String authKey, DeepLClientOptions options) throws IllegalArgumentException {
    super(authKey, options);
  }

  public WriteResult rephraseText(
      String text, @Nullable String targetLang, @Nullable TextRephraseOptions options)
      throws InterruptedException, DeepLException {
    ArrayList<String> texts = new ArrayList<>();
    texts.add(text);
    return this.rephraseText(texts, targetLang, options).get(0);
  }

  public List<WriteResult> rephraseText(
      List<String> texts, @Nullable String targetLang, @Nullable TextRephraseOptions options)
      throws InterruptedException, DeepLException {
    Iterable<KeyValuePair<String, String>> params =
        createWriteHttpParams(texts, targetLang, options);
    HttpResponse response =
        httpClientWrapper.sendRequestWithBackoff(
            String.format("/%s/write/rephrase", apiVersion), params);
    checkResponse(response, false, false);
    return jsonParser.parseWriteResult(response.getBody());
  }

  /**
   * Creates a glossary in your DeepL account with the specified details and returns a {@link
   * MultilingualGlossaryInfo} object with details about the newly created glossary. The glossary
   * will contain the glossary dictionaries specified in <paramref name="glossaryDicts" /> each with
   * their own source language, target language and entries.The glossary can be used in translations
   * to override translations for specific terms (words). The glossary must contain a glossary
   * dictionary that matches the languages of translations for which it will be used.
   *
   * @param name User-defined name to assign to the glossary; must not be empty.
   * @param glossaryDicts {@link MultilingualGlossaryDictionaryInfo} The dictionaries of the
   *     glossary
   * @return {@link MultilingualGlossaryInfo} object with details about the newly created glossary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryInfo createMultilingualGlossary(
      String name, List<MultilingualGlossaryDictionaryEntries> glossaryDicts)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    validateParameter("name", name);
    if (glossaryDicts.isEmpty()) {
      throw new IllegalArgumentException("Parameter dictionaries must not be empty");
    }
    ArrayList<KeyValuePair<String, String>> bodyParams =
        createGlossaryHttpParams(name, glossaryDicts);
    HttpResponse response = httpClientWrapper.sendRequestWithBackoff("/v3/glossaries", bodyParams);
    checkResponse(response, false, false);
    return jsonParser.parseMultilingualGlossaryInfo(response.getBody());
  }

  /**
   * Creates a glossary in your DeepL account with the specified details and returns a {@link
   * MultilingualGlossaryInfo} object with details about the newly created glossary. The glossary
   * will contain a glossary dictionary with the source and target language codes specified and
   * entries created from the <paramref name="csvFile" />. The glossary can be used in translations
   * to override translations for specific terms (words). The glossary must contain a glossary
   * dictionary that matches the languages of translations for which it will be used.
   *
   * @param name User-defined name to assign to the glossary; must not be empty.
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param csvFile String containing CSV content.
   * @return {@link MultilingualGlossaryInfo} object with details about the newly created glossary.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryInfo createMultilingualGlossaryFromCsv(
      String name, String sourceLanguageCode, String targetLanguageCode, String csvFile)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return createGlossaryFromCsvInternal(name, sourceLanguageCode, targetLanguageCode, csvFile);
  }
  /**
   * Creates a glossary in your DeepL account with the specified details and returns a {@link
   * MultilingualGlossaryInfo} object with details about the newly created glossary. The glossary
   * will contain a glossary dictionary with the source and target language codes specified and
   * entries created from the <paramref name="csvFile" />. The glossary can be used in translations
   * to override translations for specific terms (words). The glossary must contain a glossary
   * dictionary that matches the languages of translations for which it will be used.
   *
   * @param name User-defined name to assign to the glossary; must not be empty.
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param csvFile String containing CSV content.
   * @return {@link MultilingualGlossaryInfo} object with details about the newly created glossary.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   * @throws IOException If an I/O error occurs.
   */
  public MultilingualGlossaryInfo createMultilingualGlossaryFromCsv(
      String name, String sourceLanguageCode, String targetLanguageCode, File csvFile)
      throws DeepLException, IllegalArgumentException, InterruptedException, IOException {
    try (FileInputStream stream = new FileInputStream(csvFile)) {
      String csvContent = StreamUtil.readStream(stream);
      return createGlossaryFromCsvInternal(
          name, sourceLanguageCode, targetLanguageCode, csvContent);
    }
  }

  /**
   * Retrieves information about the glossary with the specified ID and returns a {@link
   * MultilingualGlossaryInfo} object containing details. This does not retrieve the glossary
   * entries; to retrieve entries use {@link
   * DeepLClient#getMultilingualGlossaryDictionaryEntries(String, String, String)}
   *
   * @param glossaryId ID of glossary to retrieve.
   * @return {@link MultilingualGlossaryInfo} object with details about the specified glossary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public MultilingualGlossaryInfo getMultilingualGlossary(String glossaryId)
      throws DeepLException, InterruptedException {
    String relativeUrl = String.format("/v3/glossaries/%s", glossaryId);
    HttpResponse response = httpClientWrapper.sendGetRequestWithBackoff(relativeUrl);
    checkResponse(response, false, true);
    return jsonParser.parseMultilingualGlossaryInfo(response.getBody());
  }

  /**
   * Retrieves information about all glossaries and returns an array of {@link
   * MultilingualGlossaryInfo} objects containing details. This does not retrieve the glossary
   * entries; to retrieve entries use {@link
   * DeepLClient#getMultilingualGlossaryDictionaryEntries(String, String, String)}
   *
   * @return List of {@link MultilingualGlossaryInfo} objects with details about each glossary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public List<MultilingualGlossaryInfo> listMultilingualGlossaries()
      throws DeepLException, InterruptedException {
    HttpResponse response = httpClientWrapper.sendGetRequestWithBackoff("/v3/glossaries");
    checkResponse(response, false, false);
    return jsonParser.parseMultilingualGlossaryInfoList(response.getBody());
  }

  /**
   * For the glossary with the specified ID, retrieves the glossary dictionary with its entries for
   * the given source and target language code pair.
   *
   * @param glossaryId ID of glossary for which to retrieve entries.
   * @param sourceLanguageCode Source language code for the requested glossary dictionary.
   * @param targetLanguageCode Target language code of the requested glossary dictionary.
   * @return {@link MultilingualGlossaryDictionaryEntries} object containing a glossary dictionary
   *     with entries.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryDictionaryEntries getMultilingualGlossaryDictionaryEntries(
      String glossaryId, String sourceLanguageCode, String targetLanguageCode)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    validateParameter("glossaryId", glossaryId);
    String queryString = createLanguageQueryParams(sourceLanguageCode, targetLanguageCode);
    String relativeUrl = String.format("/v3/glossaries/%s/entries%s", glossaryId, queryString);
    HttpResponse response = httpClientWrapper.sendGetRequestWithBackoff(relativeUrl);
    checkResponse(response, false, true);
    return jsonParser
        .parseMultilingualGlossaryDictionaryListResponse(response.getBody())
        .getDictionaries()
        .get(0)
        .getDictionaryEntries();
  }

  /**
   * For the glossary with the specified ID, retrieves the glossary dictionary with its entries for
   * the given {@link MultilingualGlossaryDictionaryInfo} glossary dictionary.
   *
   * @param glossaryId ID of glossary for which to retrieve entries.
   * @param glossaryDict The requested glossary dictionary.
   * @return {@link MultilingualGlossaryDictionaryEntries} object containing a glossary dictionary
   *     with entries.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryDictionaryEntries getMultilingualGlossaryDictionaryEntries(
      String glossaryId, MultilingualGlossaryDictionaryInfo glossaryDict)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return getMultilingualGlossaryDictionaryEntries(
        glossaryId, glossaryDict.getSourceLanguageCode(), glossaryDict.getTargetLanguageCode());
  }

  /**
   * For the specified glossary, retrieves the glossary dictionary with its entries for the given
   * source and target language code pair.
   *
   * @param glossary The glossary for which to retrieve entries.
   * @param sourceLanguageCode Source language code for the requested glossary dictionary.
   * @param targetLanguageCode Target language code of the requested glossary dictionary.
   * @return {@link MultilingualGlossaryDictionaryEntries} object containing a glossary dictionary
   *     with entries.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryDictionaryEntries getMultilingualGlossaryDictionaryEntries(
      MultilingualGlossaryInfo glossary, String sourceLanguageCode, String targetLanguageCode)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return getMultilingualGlossaryDictionaryEntries(
        glossary.getGlossaryId(), sourceLanguageCode, targetLanguageCode);
  }

  /**
   * For the specified glossary, retrieves the glossary dictionary with its entries for the given
   * {@link MultilingualGlossaryDictionaryInfo} glossary dictionary.
   *
   * @param glossary The glossary for which to retrieve entries.
   * @param glossaryDict The requested glossary dictionary.
   * @return {@link MultilingualGlossaryDictionaryEntries} object containing a glossary dictionary
   *     with entries.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryDictionaryEntries getMultilingualGlossaryDictionaryEntries(
      MultilingualGlossaryInfo glossary, MultilingualGlossaryDictionaryInfo glossaryDict)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return getMultilingualGlossaryDictionaryEntries(
        glossary.getGlossaryId(),
        glossaryDict.getSourceLanguageCode(),
        glossaryDict.getTargetLanguageCode());
  }

  /**
   * Replaces a glossary dictionary with given entries for the source and target language codes. If
   * no such glossary dictionary exists for that language pair, a new glossary dictionary will be
   * created for that language pair and entries.
   *
   * @param glossaryId The specified ID of the glossary that contains the dictionary to be
   *     replaced/created
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param entries The source-target entry pairs in the new glossary dictionary.
   * @return {@link MultilingualGlossaryDictionaryInfo} object with details about the newly replaced
   *     glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionary(
      String glossaryId,
      String sourceLanguageCode,
      String targetLanguageCode,
      GlossaryEntries entries)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return replaceGlossaryDictionaryInternal(
        glossaryId, sourceLanguageCode, targetLanguageCode, entries.toTsv(), "tsv");
  }

  /**
   * Replaces a glossary dictionary with given entries for the source and target language codes. If
   * no such glossary dictionary exists for that language pair, a new glossary dictionary will be
   * created for that language pair and entries.
   *
   * @param glossaryId The specified ID of the glossary that contains the dictionary to be
   *     replaced/created
   * @param glossaryDict The glossary dictionary to replace the existing glossary dictionary for
   *     that source/target language code pair or to be newly created if no such glossary dictionary
   *     exists.
   * @return {@link MultilingualGlossaryDictionaryInfo} object with details about the newly replaced
   *     glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionary(
      String glossaryId, MultilingualGlossaryDictionaryEntries glossaryDict)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return replaceGlossaryDictionaryInternal(
        glossaryId,
        glossaryDict.getSourceLanguageCode(),
        glossaryDict.getTargetLanguageCode(),
        glossaryDict.getEntries().toTsv(),
        "tsv");
  }

  /**
   * Replaces a glossary dictionary with given entries for given glossary dictionary. If no such
   * glossary dictionary exists for that language pair, a new glossary dictionary will be created
   * for that language pair and entries.
   *
   * @param glossary The specified glossary that contains the dictionary to be replaced/created
   * @param glossaryDict The glossary dictionary to replace the existing glossary dictionary for
   *     that source/target language code pair or to be newly created if no such glossary dictionary
   *     exists.
   * @return {@link MultilingualGlossaryDictionaryInfo} object with details about the newly replaced
   *     glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionary(
      MultilingualGlossaryInfo glossary, MultilingualGlossaryDictionaryEntries glossaryDict)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return replaceGlossaryDictionaryInternal(
        glossary.getGlossaryId(),
        glossaryDict.getSourceLanguageCode(),
        glossaryDict.getTargetLanguageCode(),
        glossaryDict.getEntries().toTsv(),
        "tsv");
  }

  /**
   * Replaces a glossary dictionary with given entries for the source and target language codes. If
   * no such glossary dictionary exists for that language pair, a new glossary dictionary will be
   * created for that language pair and entries.
   *
   * @param glossary The specified glossary that contains the dictionary to be replaced/created
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param entries The source-target entry pairs in the new glossary dictionary.
   * @return {@link MultilingualGlossaryDictionaryInfo} object with details about the newly replaced
   *     glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionary(
      MultilingualGlossaryInfo glossary,
      String sourceLanguageCode,
      String targetLanguageCode,
      GlossaryEntries entries)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return replaceGlossaryDictionaryInternal(
        glossary.getGlossaryId(), sourceLanguageCode, targetLanguageCode, entries.toTsv(), "tsv");
  }

  /**
   * Replaces a glossary dictionary with given entries for the source and target language codes. If
   * no such glossary dictionary exists for that language pair, a new glossary dictionary will be
   * created for that language pair and entries specified in the {@code csvFile}.
   *
   * @param glossaryId The specified Id of the glossary that contains the dictionary to be
   *     replaced/created
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param csvFile File containing CSV content.
   * @return {@link MultilingualGlossaryDictionaryInfo} object with details about the newly replaced
   *     glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   * @throws IOException If an I/O error occurs.
   */
  public MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionaryFromCsv(
      String glossaryId, String sourceLanguageCode, String targetLanguageCode, File csvFile)
      throws DeepLException, IllegalArgumentException, InterruptedException, IOException {
    try (FileInputStream stream = new FileInputStream(csvFile)) {
      String csvContent = StreamUtil.readStream(stream);
      return replaceGlossaryDictionaryInternal(
          glossaryId, sourceLanguageCode, targetLanguageCode, csvContent, "csv");
    }
  }

  /**
   * Replaces a glossary dictionary with given entries for the source and target language codes. If
   * no such glossary dictionary exists for that language pair, a new glossary dictionary will be
   * created for that language pair and entries specified in the {@code csvContent}.
   *
   * @param glossaryId The specified ID of the glossary that contains the dictionary to be
   *     replaced/created
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param csvContent String containing CSV content.
   * @return {@link MultilingualGlossaryDictionaryInfo} object with details about the newly replaced
   *     glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryDictionaryInfo replaceMultilingualGlossaryDictionaryFromCsv(
      String glossaryId, String sourceLanguageCode, String targetLanguageCode, String csvContent)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return replaceGlossaryDictionaryInternal(
        glossaryId, sourceLanguageCode, targetLanguageCode, csvContent, "csv");
  }

  /**
   * Updates a glossary dictionary with given entries for the source and target language codes. The
   * glossary dictionary must belong to the glossary with the ID specified in <paramref
   * name="glossaryId" />. If a dictionary for the provided language pair already exists, the
   * dictionary entries are merged.
   *
   * @param glossaryId The specified ID of the glossary that contains the dictionary to be
   *     updated/created
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param entries The source-target entry pairs in the new glossary dictionary.
   * @return {@link MultilingualGlossaryInfo} object with details about the glossary with the newly
   *     updated glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryInfo updateMultilingualGlossaryDictionary(
      String glossaryId,
      String sourceLanguageCode,
      String targetLanguageCode,
      GlossaryEntries entries)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return updateGlossaryDictionaryInternal(
        glossaryId, sourceLanguageCode, targetLanguageCode, entries.toTsv(), "tsv");
  }

  /**
   * Updates a glossary dictionary with given entries for the source and target language codes. The
   * glossary dictionary must belong to the glossary specified in <paramref name="glossary" />. If a
   * dictionary for the provided language pair already exists, the dictionary entries are merged.
   *
   * @param glossary The specified ID for the glossary that contains the dictionary to be
   *     updated/created
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param entries The source-target entry pairs in the new glossary dictionary.
   * @return {@link MultilingualGlossaryInfo} object with details about the glossary with the newly
   *     updated glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryInfo updateMultilingualGlossaryDictionary(
      MultilingualGlossaryInfo glossary,
      String sourceLanguageCode,
      String targetLanguageCode,
      GlossaryEntries entries)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return updateGlossaryDictionaryInternal(
        glossary.getGlossaryId(), sourceLanguageCode, targetLanguageCode, entries.toTsv(), "tsv");
  }

  /**
   * Updates a glossary dictionary with given glossary dictionary specified in <paramref
   * name="glossaryDict" />. The glossary dictionary must belong to the glossary with the ID
   * specified in <paramref name="glossaryId" />. If a dictionary for the provided language pair
   * already exists, the dictionary entries are merged.
   *
   * @param glossaryId The specified ID of the glossary that contains the dictionary to be
   *     updated/created
   * @param glossaryDict The glossary dictionary to be created/updated
   * @return {@link MultilingualGlossaryInfo} object with details about the glossary with the newly
   *     updated glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryInfo updateMultilingualGlossaryDictionary(
      String glossaryId, MultilingualGlossaryDictionaryEntries glossaryDict)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return updateGlossaryDictionaryInternal(
        glossaryId,
        glossaryDict.getSourceLanguageCode(),
        glossaryDict.getTargetLanguageCode(),
        glossaryDict.getEntries().toTsv(),
        "tsv");
  }

  /**
   * Updates a glossary dictionary with given entries for the source and target language codes. If a
   * dictionary for the provided language pair already exists, the dictionary entries are merged.
   *
   * @param glossary The specified glossary that contains the dictionary to be updated/created
   * @param glossaryDict The glossary dictionary to be created/updated
   * @return {@link MultilingualGlossaryInfo} object with details about the glossary with the newly
   *     updated glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryInfo updateMultilingualGlossaryDictionary(
      MultilingualGlossaryInfo glossary, MultilingualGlossaryDictionaryEntries glossaryDict)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return updateGlossaryDictionaryInternal(
        glossary.getGlossaryId(),
        glossaryDict.getSourceLanguageCode(),
        glossaryDict.getTargetLanguageCode(),
        glossaryDict.getEntries().toTsv(),
        "tsv");
  }

  /**
   * Updates a glossary's name with the provided parameter
   *
   * @param glossaryId The specified ID of the glossary whose name will be updated
   * @param name The new name of the glossary
   * @return {@link MultilingualGlossaryInfo} object with details about the glossary with the newly
   *     updated glossary dictionary.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryInfo updateMultilingualGlossaryName(String glossaryId, String name)
      throws DeepLException, IllegalArgumentException {

    ArrayList<KeyValuePair<String, String>> bodyParams = new ArrayList<>();
    bodyParams.add(new KeyValuePair<>("name", name));
    String relativeUrl = String.format("/v3/glossaries/%s", glossaryId);
    HttpResponse response = httpClientWrapper.sendPatchRequestWithBackoff(relativeUrl, bodyParams);
    checkResponse(response, false, true);
    return jsonParser.parseMultilingualGlossaryInfo(response.getBody());
  }

  /**
   * Updates a glossary dictionary correlating to the specified ID with given entries in the {@code
   * csvFile} for the source and target language codes. If a dictionary for the provided language
   * pair already exists, the dictionary entries are merged.
   *
   * @param glossaryId The specified ID of the glossary that contains the dictionary to be
   *     updated/created
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param csvFile {@link File} containing CSV content.
   * @return {@link MultilingualGlossaryInfo} object with details about the glossary with the newly
   *     updated glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   * @throws IOException If an I/O error occurs.
   */
  public MultilingualGlossaryInfo updateMultilingualGlossaryDictionaryFromCsv(
      String glossaryId, String sourceLanguageCode, String targetLanguageCode, File csvFile)
      throws DeepLException, IllegalArgumentException, InterruptedException, IOException {
    try (FileInputStream stream = new FileInputStream(csvFile)) {
      String csvContent = StreamUtil.readStream(stream);
      return updateGlossaryDictionaryInternal(
          glossaryId, sourceLanguageCode, targetLanguageCode, csvContent, "csv");
    }
  }

  /**
   * Updates a glossary dictionary with given entries in the {@code csvFile} for the source and
   * target language codes. If a dictionary for the provided language pair already exists, the
   * dictionary entries are merged.
   *
   * @param glossaryId The specified ID of the glossary that contains the dictionary to be
   *     updated/created
   * @param sourceLanguageCode Language code of the source terms language.
   * @param targetLanguageCode Language code of the target terms language.
   * @param csvContent String containing CSV content.
   * @return {@link MultilingualGlossaryInfo} object with details about the glossary with the newly
   *     updated glossary dictionary.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public MultilingualGlossaryInfo updateMultilingualGlossaryDictionaryFromCsv(
      String glossaryId, String sourceLanguageCode, String targetLanguageCode, String csvContent)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    return updateGlossaryDictionaryInternal(
        glossaryId, sourceLanguageCode, targetLanguageCode, csvContent, "csv");
  }

  /**
   * Deletes the glossary with the specified ID.
   *
   * @param glossaryId ID of glossary to delete.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public void deleteMultilingualGlossary(String glossaryId) throws DeepLException, InterruptedException {
    String relativeUrl = String.format("/v3/glossaries/%s", glossaryId);
    HttpResponse response = httpClientWrapper.sendDeleteRequestWithBackoff(relativeUrl);
    this.checkResponse(response, false, true);
  }

  /**
   * Deletes the specified glossary.
   *
   * @param glossary {@link MultilingualGlossaryInfo} object corresponding to glossary to delete.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public void deleteMultilingualGlossary(MultilingualGlossaryInfo glossary)
      throws DeepLException, InterruptedException {
    this.deleteMultilingualGlossary(glossary.getGlossaryId());
  }

  /**
   * Deletes the glossary dictionary with the source and target language codes specified in the
   * glossary with the specified ID.
   *
   * @param glossaryId ID of glossary that contains the glossary dictionary to delete.
   * @param sourceLanguageCode Source language code of the glossary dictionary to be deleted.
   * @param targetLanguageCode Target language code of the glossary dictionary to be deleted.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws IllegalArgumentException If any argument is invalid.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public void deleteMultilingualGlossaryDictionary(
      String glossaryId, String sourceLanguageCode, String targetLanguageCode)
      throws DeepLException, InterruptedException, IllegalArgumentException {
    String queryString = createLanguageQueryParams(sourceLanguageCode, targetLanguageCode);
    String relativeUrl = String.format("/v3/glossaries/%s/dictionaries%s", glossaryId, queryString);
    HttpResponse response = httpClientWrapper.sendDeleteRequestWithBackoff(relativeUrl);
    this.checkResponse(response, false, true);
  }

  /**
   * Deletes the specified glossary dictionary in the glossary with the specified ID.
   *
   * @param glossaryId ID of glossary that contains the glossary dictionary to delete.
   * @param glossaryDict {@link MultilingualGlossaryDictionaryInfo} object corresponding to glossary
   *     dictionary to delete.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public void deleteMultilingualGlossaryDictionary(
      String glossaryId, MultilingualGlossaryDictionaryInfo glossaryDict)
      throws DeepLException, InterruptedException, IllegalArgumentException {
    deleteMultilingualGlossaryDictionary(
        glossaryId, glossaryDict.getSourceLanguageCode(), glossaryDict.getTargetLanguageCode());
  }

  /**
   * Deletes the specified glossary dictionary in the glossary in the specified glossary.
   *
   * @param glossary The glossary that contains the glossary dictionary to delete.
   * @param sourceLanguageCode Source language code of the glossary dictionary to be deleted.
   * @param targetLanguageCode Target language code of the glossary dictionary to be deleted.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public void deleteMultilingualGlossaryDictionary(
      MultilingualGlossaryInfo glossary, String sourceLanguageCode, String targetLanguageCode)
      throws DeepLException, InterruptedException, IllegalArgumentException {
    deleteMultilingualGlossaryDictionary(
        glossary.getGlossaryId(), sourceLanguageCode, targetLanguageCode);
  }

  /**
   * Deletes the specified glossary dictionary in the glossary in the specified glossary.
   *
   * @param glossary The glossary that contains the glossary dictionary to delete.
   * @param glossaryDict {@link MultilingualGlossaryDictionaryInfo} object corresponding to glossary
   *     dictionary to delete.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API, a {@link
   *     DeepLException} or a derived class will be thrown.
   */
  public void deleteMultilingualGlossaryDictionary(
      MultilingualGlossaryInfo glossary, MultilingualGlossaryDictionaryInfo glossaryDict)
      throws DeepLException, InterruptedException, IllegalArgumentException {
    deleteMultilingualGlossaryDictionary(
        glossary.getGlossaryId(),
        glossaryDict.getSourceLanguageCode(),
        glossaryDict.getTargetLanguageCode());
  }

  /** Creates a glossary with given details. */
  private MultilingualGlossaryInfo createGlossaryFromCsvInternal(
      String name, String sourceLanguageCode, String targetLanguageCode, String entries)
      throws DeepLException, InterruptedException {
    ArrayList<KeyValuePair<String, String>> params =
        createGlossaryDictionariesHttpParams(
            sourceLanguageCode, targetLanguageCode, entries, "csv");
    params.add(new KeyValuePair<>("name", name));
    HttpResponse response = httpClientWrapper.sendRequestWithBackoff("/v3/glossaries", params);
    checkResponse(response, false, false);
    return jsonParser.parseMultilingualGlossaryInfo(response.getBody());
  }

  /**
   * Gets the entries in the glossary with the specified ID for the given source and target
   * languages
   */
  private MultilingualGlossaryDictionaryInfo replaceGlossaryDictionaryInternal(
      String glossaryId,
      String sourceLanguageCode,
      String targetLanguageCode,
      String entries,
      String entriesFormat)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    validateParameter("glossaryId", glossaryId);
    validateParameter("sourceLanguageCode", sourceLanguageCode);
    validateParameter("targetLanguageCode", targetLanguageCode);
    validateParameter("entries", entries);

    ArrayList<KeyValuePair<String, String>> bodyParams = new ArrayList<>();
    bodyParams.add(new KeyValuePair<>("source_lang", sourceLanguageCode));
    bodyParams.add(new KeyValuePair<>("target_lang", targetLanguageCode));
    bodyParams.add(new KeyValuePair<>("entries", entries));
    bodyParams.add(new KeyValuePair<>("entries_format", entriesFormat));

    String relativeUrl = String.format("/v3/glossaries/%s/dictionaries", glossaryId);
    HttpResponse response = httpClientWrapper.sendPutRequestWithBackoff(relativeUrl, bodyParams);
    checkResponse(response, false, true);
    return jsonParser.parseMultilingualGlossaryDictionaryInfo(response.getBody());
  }

  /**
   * Gets the entries in the glossary with the specified ID for the given source and target
   * languages
   */
  private MultilingualGlossaryInfo updateGlossaryDictionaryInternal(
      String glossaryId,
      String sourceLanguageCode,
      String targetLanguageCode,
      String entries,
      String entriesFormat)
      throws DeepLException, IllegalArgumentException, InterruptedException {
    validateParameter("glossaryId", glossaryId);
    validateParameter("sourceLanguageCode", sourceLanguageCode);
    validateParameter("targetLanguageCode", targetLanguageCode);
    validateParameter("entries", entries);

    ArrayList<KeyValuePair<String, String>> bodyParams =
        createGlossaryDictionariesHttpParams(
            sourceLanguageCode, targetLanguageCode, entries, entriesFormat);
    String relativeUrl = String.format("/v3/glossaries/%s", glossaryId);
    HttpResponse response = httpClientWrapper.sendPatchRequestWithBackoff(relativeUrl, bodyParams);
    checkResponse(response, false, true);
    return jsonParser.parseMultilingualGlossaryInfo(response.getBody());
  }

  /** Creates query string for the source and target languages */
  private String createLanguageQueryParams(String sourceLanguageCode, String targetLanguageCode)
      throws IllegalArgumentException, DeepLException {
    validateParameter("sourceLanguageCode", sourceLanguageCode);
    validateParameter("targetLanguageCode", targetLanguageCode);
    try {
      return "?"
          + String.join(
              "&",
              String.format(
                  "source_lang=%s",
                  URLEncoder.encode(sourceLanguageCode, StandardCharsets.UTF_8.name())),
              String.format(
                  "target_lang=%s",
                  URLEncoder.encode(targetLanguageCode, StandardCharsets.UTF_8.name())));
    } catch (UnsupportedEncodingException exception) {
      throw new DeepLException("Error while URL-encoding request", exception);
    }
  }

  private void validateParameter(String paramName, String value) throws IllegalArgumentException {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("Parameter %s must not be empty", paramName));
    }
  }

  protected static ArrayList<KeyValuePair<String, String>> createWriteHttpParams(
      List<String> texts, @Nullable String targetLang, @Nullable TextRephraseOptions options) {
    targetLang = LanguageCode.standardize(targetLang);
    checkValidLanguages(null, targetLang);

    ArrayList<KeyValuePair<String, String>> params = new ArrayList<>();
    if (targetLang != null) {
      params.add(new KeyValuePair<>("target_lang", targetLang));
    }
    if (options != null && options.getWritingStyle() != null) {
      params.add(new KeyValuePair<>("writing_style", options.getWritingStyle()));
    }
    if (options != null && options.getTone() != null) {
      params.add(new KeyValuePair<>("tone", options.getTone()));
    }

    texts.forEach(
        (text) -> {
          if (text.isEmpty()) throw new IllegalArgumentException("text must not be empty");
          params.add(new KeyValuePair<>("text", text));
        });

    return params;
  }

  protected static ArrayList<KeyValuePair<String, String>> createGlossaryHttpParams(
      String name, List<MultilingualGlossaryDictionaryEntries> glossaryDicts) {
    ArrayList<KeyValuePair<String, String>> bodyParams = new ArrayList<>();
    bodyParams.add(new KeyValuePair<>("name", name));
    for (int i = 0; i < glossaryDicts.size(); i++) {
      bodyParams.add(
          new KeyValuePair<>(
              String.format("dictionaries[%d].source_lang", i),
              glossaryDicts.get(i).getSourceLanguageCode()));
      bodyParams.add(
          new KeyValuePair<>(
              String.format("dictionaries[%d].target_lang", i),
              glossaryDicts.get(i).getTargetLanguageCode()));
      bodyParams.add(
          new KeyValuePair<>(
              String.format("dictionaries[%d].entries", i),
              glossaryDicts.get(i).getEntries().toTsv()));
      bodyParams.add(
          new KeyValuePair<>(String.format("dictionaries[%d].entries_format", i), "tsv"));
    }
    return bodyParams;
  }

  protected static ArrayList<KeyValuePair<String, String>> createGlossaryDictionariesHttpParams(
      String sourceLanguageCode, String targetLanguageCode, String entries, String entriesFormat) {
    ArrayList<KeyValuePair<String, String>> bodyParams = new ArrayList<>();
    bodyParams.add(new KeyValuePair<>("dictionaries[0].source_lang", sourceLanguageCode));
    bodyParams.add(new KeyValuePair<>("dictionaries[0].target_lang", targetLanguageCode));
    bodyParams.add(new KeyValuePair<>("dictionaries[0].entries", entries));
    bodyParams.add(new KeyValuePair<>("dictionaries[0].entries_format", entriesFormat));

    return bodyParams;
  }
}
