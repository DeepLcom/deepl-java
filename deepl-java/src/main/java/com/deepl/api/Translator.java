// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.deepl.api.http.HttpResponse;
import com.deepl.api.http.HttpResponseStream;
import com.deepl.api.parsing.Parser;
import com.deepl.api.utils.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.*;
import org.jetbrains.annotations.Nullable;

/**
 * Client for the DeepL API. To use the DeepL API, initialize an instance of this class using your
 * DeepL Authentication Key as found in your <a href="https://www.deepl.com/pro-account/">DeepL
 * account</a>.
 */
public class Translator {
  /** Base URL for DeepL API Free accounts. */
  private static final String DEEPL_SERVER_URL_FREE = "https://api-free.deepl.com";
  /** Base URL for DeepL API Pro accounts */
  private static final String DEEPL_SERVER_URL_PRO = "https://api.deepl.com";

  private final Parser jsonParser = new Parser();
  private final HttpClientWrapper httpClientWrapper;

  /**
   * Initializes a new Translator object using your Authentication Key.
   *
   * <p>Note: This function does not establish a connection to the DeepL API. To check connectivity,
   * use {@link Translator#getUsage()}.
   *
   * @param authKey DeepL Authentication Key as found in your <a
   *     href="https://www.deepl.com/pro-account/">DeepL account</a>.
   * @param options Additional options controlling Translator behaviour.
   * @throws IllegalArgumentException If authKey is invalid.
   */
  public Translator(String authKey, TranslatorOptions options) throws IllegalArgumentException {
    if (authKey == null || authKey.length() == 0) {
      throw new IllegalArgumentException("authKey must be a non-empty string");
    }
    String serverUrl =
        (options.getServerUrl() != null)
            ? options.getServerUrl()
            : (isFreeAccountAuthKey(authKey) ? DEEPL_SERVER_URL_FREE : DEEPL_SERVER_URL_PRO);

    Map<String, String> headers = new HashMap<>();
    if (options.getHeaders() != null) {
      headers.putAll(options.getHeaders());
    }
    headers.putIfAbsent("Authorization", "DeepL-Auth-Key " + authKey);
    headers.putIfAbsent("User-Agent", "deepl-java/0.1.0");

    this.httpClientWrapper =
        new HttpClientWrapper(
            serverUrl, headers, options.getTimeout(), options.getProxy(), options.getMaxRetries());
  }

  /**
   * Initializes a new Translator object using your Authentication Key.
   *
   * <p>Note: This function does not establish a connection to the DeepL API. To check connectivity,
   * use {@link Translator#getUsage()}.
   *
   * @param authKey DeepL Authentication Key as found in your <a
   *     href="https://www.deepl.com/pro-account/">DeepL account</a>.
   * @throws IllegalArgumentException If authKey is invalid.
   */
  public Translator(String authKey) throws IllegalArgumentException {
    this(authKey, new TranslatorOptions());
  }

  /**
   * Determines if the given DeepL Authentication Key belongs to an API Free account.
   *
   * @param authKey DeepL Authentication Key as found in your <a
   *     href="https://www.deepl.com/pro-account/">DeepL account</a>.
   * @return <code>true</code> if the Authentication Key belongs to an API Free account, otherwise
   *     <code>false</code>.
   */
  public static boolean isFreeAccountAuthKey(String authKey) {
    return authKey != null && authKey.endsWith(":fx");
  }

  /**
   * Translate specified text from source language into target language.
   *
   * @param text Text to translate; must not be empty.
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @param options Options influencing translation.
   * @return Text translated into specified target language, and detected source language.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public TextResult translateText(
      String text,
      @Nullable String sourceLang,
      String targetLang,
      @Nullable TextTranslationOptions options)
      throws InterruptedException, DeepLException {
    ArrayList<String> texts = new ArrayList<>();
    texts.add(text);
    return translateText(texts, sourceLang, targetLang, options).get(0);
  }

  /**
   * Functions the same as {@link Translator#translateText(String, String, String,
   * TextTranslationOptions)} but with default options.
   *
   * @see Translator#translateText(String, String, String, TextTranslationOptions)
   */
  public TextResult translateText(String text, @Nullable String sourceLang, String targetLang)
      throws DeepLException, InterruptedException {
    return translateText(text, sourceLang, targetLang, null);
  }

  /**
   * Functions the same as {@link Translator#translateText(String, String, String,
   * TextTranslationOptions)} but accepts {@link Language} objects for source and target languages,
   * and uses default options.
   *
   * @see Translator#translateText(String, String, String, TextTranslationOptions)
   */
  public TextResult translateText(String text, @Nullable Language sourceLang, Language targetLang)
      throws DeepLException, InterruptedException {
    return translateText(
        text, (sourceLang != null) ? sourceLang.getCode() : null, targetLang.getCode(), null);
  }

  /**
   * Functions the same as {@link Translator#translateText(String, String, String,
   * TextTranslationOptions)} but accepts {@link Language} objects for source and target languages.
   *
   * @see Translator#translateText(String, String, String, TextTranslationOptions)
   */
  public TextResult translateText(
      String text,
      @Nullable Language sourceLang,
      Language targetLang,
      @Nullable TextTranslationOptions options)
      throws DeepLException, InterruptedException {
    return translateText(
        text, (sourceLang != null) ? sourceLang.getCode() : null, targetLang.getCode(), options);
  }

  /**
   * Translate specified texts from source language into target language.
   *
   * @param texts List of texts to translate; each text must not be empty.
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @param options Options influencing translation.
   * @return List of texts translated into specified target language, and detected source language.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public List<TextResult> translateText(
      List<String> texts,
      @Nullable String sourceLang,
      String targetLang,
      @Nullable TextTranslationOptions options)
      throws DeepLException, InterruptedException {
    Iterable<KeyValuePair<String, String>> params =
        createHttpParams(texts, sourceLang, targetLang, options);
    HttpResponse response = httpClientWrapper.sendRequestWithBackoff("/v2/translate", params);
    checkResponse(response, false);
    return jsonParser.parseTextResult(response.getBody());
  }

  /**
   * Functions the same as {@link Translator#translateText(List, String, String,
   * TextTranslationOptions)} but accepts {@link Language} objects for source and target languages,
   * and uses default options.
   *
   * @see Translator#translateText(List, String, String, TextTranslationOptions)
   */
  public List<TextResult> translateText(
      List<String> texts, @Nullable Language sourceLang, Language targetLang)
      throws DeepLException, InterruptedException {
    return translateText(
        texts, (sourceLang != null) ? sourceLang.getCode() : null, targetLang.getCode(), null);
  }

  /**
   * Functions the same as {@link Translator#translateText(List, String, String,
   * TextTranslationOptions)} but accepts {@link Language} objects for source and target languages.
   *
   * @see Translator#translateText(List, String, String, TextTranslationOptions)
   */
  public List<TextResult> translateText(
      List<String> texts,
      @Nullable Language sourceLang,
      Language targetLang,
      @Nullable TextTranslationOptions options)
      throws DeepLException, InterruptedException {
    return translateText(
        texts, (sourceLang != null) ? sourceLang.getCode() : null, targetLang.getCode(), options);
  }

  /**
   * Functions the same as {@link Translator#translateText(List, String, String,
   * TextTranslationOptions)} but uses default options.
   *
   * @see Translator#translateText(List, String, String, TextTranslationOptions)
   */
  public List<TextResult> translateText(
      List<String> texts, @Nullable String sourceLang, String targetLang)
      throws DeepLException, InterruptedException {
    return translateText(texts, sourceLang, targetLang, null);
  }

  /**
   * Retrieves the usage in the current billing period for this DeepL account. This function can
   * also be used to check connectivity with the DeepL API and that the account has access.
   *
   * @return {@link Usage} object containing account usage information.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public Usage getUsage() throws DeepLException, InterruptedException {
    HttpResponse response = httpClientWrapper.sendRequestWithBackoff("/v2/usage");
    checkResponse(response, false);
    return jsonParser.parseUsage(response.getBody());
  }

  /**
   * Retrieves the list of supported translation source languages.
   *
   * @return List of {@link Language} objects representing the available translation source
   *     languages.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public List<Language> getSourceLanguages() throws DeepLException, InterruptedException {
    return getLanguages(LanguageType.Source);
  }

  /**
   * Retrieves the list of supported translation target languages.
   *
   * @return List of {@link Language} objects representing the available translation target
   *     languages.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public List<Language> getTargetLanguages() throws DeepLException, InterruptedException {
    return getLanguages(LanguageType.Target);
  }

  /**
   * Retrieves the list of supported translation source or target languages.
   *
   * @param languageType The type of languages to retrieve, source or target.
   * @return List of {@link Language} objects representing the available translation source or
   *     target languages.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public List<Language> getLanguages(LanguageType languageType)
      throws DeepLException, InterruptedException {
    ArrayList<KeyValuePair<String, String>> params = new ArrayList<>();
    if (languageType == LanguageType.Target) {
      params.add(new KeyValuePair<>("type", "target"));
    }
    HttpResponse response = httpClientWrapper.sendRequestWithBackoff("/v2/languages", params);
    checkResponse(response, false);
    return jsonParser.parseLanguages(response.getBody());
  }

  /**
   * Translate specified document content from source language to target language and store the
   * translated document content to specified stream.
   *
   * @param inputFile File to upload to be translated.
   * @param outputFile File to download translated document to.
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @param options Options influencing translation.
   * @return Status when document translation completed, this allows the number of billed characters
   *     to be queried.
   * @throws IOException If the output path is occupied or the input file does not exist.
   * @throws DocumentTranslationException If any error occurs while communicating with the DeepL
   *     API, or if the thread is interrupted during execution of this function. The exception
   *     includes the document handle that may be used to retrieve the document.
   */
  public DocumentStatus translateDocument(
      File inputFile,
      File outputFile,
      @Nullable String sourceLang,
      String targetLang,
      @Nullable DocumentTranslationOptions options)
      throws DocumentTranslationException, IOException {
    try {
      if (outputFile.exists()) {
        throw new IOException("File already exists at output path");
      }
      try (InputStream inputStream = new FileInputStream(inputFile);
          OutputStream outputStream = new FileOutputStream(outputFile)) {
        return translateDocument(
            inputStream, inputFile.getName(), outputStream, sourceLang, targetLang, options);
      }
    } catch (Exception exception) {
      outputFile.delete();
      throw exception;
    }
  }

  /**
   * Functions the same as {@link Translator#translateDocument(File, File, String, String,
   * DocumentTranslationOptions)} but uses default options.
   *
   * @see Translator#translateDocument(File, File, String, String, DocumentTranslationOptions)
   */
  public DocumentStatus translateDocument(
      File inputFile, File outputFile, @Nullable String sourceLang, String targetLang)
      throws DocumentTranslationException, IOException {
    return translateDocument(inputFile, outputFile, sourceLang, targetLang, null);
  }

  /**
   * Translate specified document content from source language to target language and store the
   * translated document content to specified stream. On return, input stream will be at end of
   * stream and neither stream will be closed.
   *
   * @param inputStream Stream containing file to upload to be translated.
   * @param fileName Name of the input file. The file extension is used to determine file type.
   * @param outputStream Stream to download translated document to.
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @param options Options influencing translation.
   * @return Status when document translation completed, this allows the number of billed characters
   *     to be queried.
   * @throws DocumentTranslationException If any error occurs while communicating with the DeepL
   *     API, or if the thread is interrupted during execution of this function. The exception
   *     includes the document handle that may be used to retrieve the document.
   */
  public DocumentStatus translateDocument(
      InputStream inputStream,
      String fileName,
      OutputStream outputStream,
      @Nullable String sourceLang,
      String targetLang,
      @Nullable DocumentTranslationOptions options)
      throws DocumentTranslationException {
    DocumentHandle handle = null;
    try {
      handle = translateDocumentUpload(inputStream, fileName, sourceLang, targetLang, options);
      DocumentStatus status = translateDocumentWaitUntilDone(handle);
      translateDocumentDownload(handle, outputStream);
      return status;
    } catch (Exception exception) {
      throw new DocumentTranslationException(
          "Error occurred during document translation: " + exception.getMessage(),
          exception,
          handle);
    }
  }

  /**
   * Functions the same as {@link Translator#translateDocument(InputStream, String, OutputStream,
   * String, String, DocumentTranslationOptions)} but uses default options.
   *
   * @see Translator#translateDocument(InputStream, String, OutputStream, String, String,
   *     DocumentTranslationOptions)
   */
  public DocumentStatus translateDocument(
      InputStream inputFile,
      String fileName,
      OutputStream outputFile,
      @Nullable String sourceLang,
      String targetLang)
      throws DocumentTranslationException {
    return translateDocument(inputFile, fileName, outputFile, sourceLang, targetLang, null);
  }

  /**
   * Upload document at specified input path for translation from source language to target
   * language. See the <a href= "https://www.deepl.com/docs-api/translating-documents/">DeepL API
   * documentation</a> for the currently supported document types.
   *
   * @param inputFile File containing document to be translated.
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @param options Options influencing translation.
   * @return Handle associated with the in-progress document translation.
   * @throws IOException If the input file does not exist.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public DocumentHandle translateDocumentUpload(
      File inputFile,
      @Nullable String sourceLang,
      String targetLang,
      @Nullable DocumentTranslationOptions options)
      throws DeepLException, IOException, InterruptedException {
    Iterable<KeyValuePair<String, String>> params =
        createHttpParams(sourceLang, targetLang, options);
    try (FileInputStream inputStream = new FileInputStream(inputFile)) {
      HttpResponse response =
          httpClientWrapper.uploadWithBackoff(
              "/v2/document/", params, inputFile.getName(), inputStream);
      checkResponse(response, false);
      return jsonParser.parseDocumentHandle(response.getBody());
    }
  }

  /**
   * Functions the same as {@link Translator#translateDocumentUpload(File, String, String,
   * DocumentTranslationOptions)} but uses default options.
   *
   * @see Translator#translateDocumentUpload(File, String, String, DocumentTranslationOptions)
   */
  public DocumentHandle translateDocumentUpload(
      File inputFile, @Nullable String sourceLang, String targetLang)
      throws DeepLException, IOException, InterruptedException {
    return translateDocumentUpload(inputFile, sourceLang, targetLang, null);
  }

  /**
   * Upload document at specified input path for translation from source language to target
   * language. See the <a href= "https://www.deepl.com/docs-api/translating-documents/">DeepL API
   * documentation</a> for the currently supported document types.
   *
   * @param inputStream Stream containing document to be translated. On return, input stream will be
   *     at end of stream and will not be closed.
   * @param fileName Name of the input file. The file extension is used to determine file type.
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @param options Options influencing translation.
   * @return Handle associated with the in-progress document translation.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public DocumentHandle translateDocumentUpload(
      InputStream inputStream,
      String fileName,
      @Nullable String sourceLang,
      String targetLang,
      @Nullable DocumentTranslationOptions options)
      throws DeepLException, InterruptedException {
    Iterable<KeyValuePair<String, String>> params =
        createHttpParams(sourceLang, targetLang, options);
    HttpResponse response =
        httpClientWrapper.uploadWithBackoff("/v2/document/", params, fileName, inputStream);
    checkResponse(response, false);
    return jsonParser.parseDocumentHandle(response.getBody());
  }

  /**
   * Functions the same as {@link Translator#translateDocumentUpload(InputStream, String, String,
   * String, DocumentTranslationOptions)} but uses default options.
   *
   * @see Translator#translateDocumentUpload(InputStream, String, String, String,
   *     DocumentTranslationOptions)
   */
  public DocumentHandle translateDocumentUpload(
      InputStream inputStream, String fileName, @Nullable String sourceLang, String targetLang)
      throws DeepLException, InterruptedException {
    return translateDocumentUpload(inputStream, fileName, sourceLang, targetLang, null);
  }

  /**
   * Retrieve the status of in-progress document translation associated with specified handle.
   *
   * @param handle Handle associated with document translation to check.
   * @return Status of the document translation.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public DocumentStatus translateDocumentStatus(DocumentHandle handle)
      throws DeepLException, InterruptedException {
    ArrayList<KeyValuePair<String, String>> params = new ArrayList<>();
    params.add(new KeyValuePair<>("document_key", handle.getDocumentKey()));
    String relativeUrl = String.format("/v2/document/%s", handle.getDocumentId());
    HttpResponse response = httpClientWrapper.sendRequestWithBackoff(relativeUrl, params);
    checkResponse(response, false);
    return jsonParser.parseDocumentStatus(response.getBody());
  }

  /**
   * Checks document translation status and waits until document translation is complete or fails
   * due to an error.
   *
   * @param handle Handle associated with document translation to wait for.
   * @return Status when document translation completed, this allows the number of billed characters
   *     to be queried.
   * @throws InterruptedException If the thread is interrupted while waiting for the document
   *     translation to complete.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public DocumentStatus translateDocumentWaitUntilDone(DocumentHandle handle)
      throws InterruptedException, DeepLException {
    DocumentStatus status = translateDocumentStatus(handle);
    while (status.ok() && !status.done()) {
      Thread.sleep(calculateDocumentWaitTimeMillis(status.getSecondsRemaining()));
      status = translateDocumentStatus(handle);
    }

    if (!status.ok()) {
      String message =
          (status.getErrorMessage() != null) ? status.getErrorMessage() : "Unknown error";
      throw new DeepLException(message);
    }
    return status;
  }

  /**
   * Downloads the resulting translated document associated with specified handle to the specified
   * output file. The document translation must be complete i.e. {@link DocumentStatus#done()} for
   * the document status must be <code>true</code>.
   *
   * @param handle Handle associated with document translation to download.
   * @param outputFile File to download translated document to.
   * @throws IOException If the output path is occupied.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public void translateDocumentDownload(DocumentHandle handle, File outputFile)
      throws DeepLException, IOException, InterruptedException {
    try {
      if (outputFile.exists()) {
        throw new IOException("File already exists at output path");
      }
      try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
        translateDocumentDownload(handle, outputStream);
      }
    } catch (Exception exception) {
      outputFile.delete();
      throw exception;
    }
  }

  /**
   * Downloads the resulting translated document associated with specified handle to the specified
   * output stream. The document translation must be complete i.e. {@link DocumentStatus#done()} for
   * the document status must be <code>true</code>. The output stream is not closed.
   *
   * @param handle Handle associated with document translation to download.
   * @param outputStream Stream to download translated document to.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the thread is interrupted during execution of this function.
   * @throws DeepLException If any error occurs while communicating with the DeepL API.
   */
  public void translateDocumentDownload(DocumentHandle handle, OutputStream outputStream)
      throws DeepLException, IOException, InterruptedException {
    ArrayList<KeyValuePair<String, String>> params = new ArrayList<>();
    params.add(new KeyValuePair<>("document_key", handle.getDocumentKey()));
    String relativeUrl = String.format("/v2/document/%s/result", handle.getDocumentId());
    try (HttpResponseStream response = httpClientWrapper.downloadWithBackoff(relativeUrl, params)) {
      checkResponse(response);
      assert response.getBody() != null;
      StreamUtil.transferTo(response.getBody(), outputStream);
    }
  }

  /**
   * Checks the specified texts, languages and options are valid, and returns an iterable of
   * containing the parameters to include in HTTP request.
   *
   * @param texts Iterable of texts to translate.
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @param options Options influencing translation.
   * @return Iterable of parameters for HTTP request.
   */
  private static ArrayList<KeyValuePair<String, String>> createHttpParams(
      List<String> texts,
      @Nullable String sourceLang,
      String targetLang,
      @Nullable TextTranslationOptions options) {
    ArrayList<KeyValuePair<String, String>> params =
        createHttpParamsCommon(
            sourceLang,
            targetLang,
            options != null ? options.getFormality() : null,
            options != null ? options.getGlossaryId() : null);
    texts.forEach(
        (text) -> {
          if (text.isEmpty()) throw new IllegalArgumentException("text must not be empty");
          params.add(new KeyValuePair<>("text", text));
        });

    if (options != null) {
      // Note: formality and glossaryId are added above
      if (options.getSentenceSplittingMode() != null
          && options.getSentenceSplittingMode() != SentenceSplittingMode.All) {
        switch (options.getSentenceSplittingMode()) {
          case Off:
            params.add(new KeyValuePair<>("split_sentences", "0"));
            break;
          case NoNewlines:
            params.add(new KeyValuePair<>("split_sentences", "nonewlines"));
            break;
          default:
            break;
        }
      }
      if (options.isPreserveFormatting()) {
        params.add(new KeyValuePair<>("preserve_formatting", "1"));
      }
      if (options.getTagHandling() != null) {
        params.add(new KeyValuePair<>("tag_handling", options.getTagHandling()));
      }
      if (!options.isOutlineDetection()) {
        params.add(new KeyValuePair<>("outline_detection", "0"));
      }
      if (options.getSplittingTags() != null) {
        params.add(new KeyValuePair<>("splitting_tags", joinTags(options.getSplittingTags())));
      }
      if (options.getNonSplittingTags() != null) {
        params.add(
            new KeyValuePair<>("non_splitting_tags", joinTags(options.getNonSplittingTags())));
      }
      if (options.getIgnoreTags() != null) {
        params.add(new KeyValuePair<>("ignore_tags", joinTags(options.getIgnoreTags())));
      }
    }
    return params;
  }

  /**
   * Checks the specified languages and document translation options are valid, and returns an
   * iterable of containing the parameters to include in HTTP request.
   *
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @param options Options influencing translation.
   * @return Iterable of parameters for HTTP request.
   */
  private static ArrayList<KeyValuePair<String, String>> createHttpParams(
      String sourceLang, String targetLang, DocumentTranslationOptions options) {
    return createHttpParamsCommon(
        sourceLang,
        targetLang,
        options != null ? options.getFormality() : null,
        options != null ? options.getGlossaryId() : null);
  }

  /**
   * Checks the specified parameters common to both text and document translation are valid, and
   * returns an iterable of containing the parameters to include in HTTP request.
   *
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @param formality Formality option for translation.
   * @param glossaryId ID of glossary to use for translation.
   * @return Iterable of parameters for HTTP request.
   */
  private static ArrayList<KeyValuePair<String, String>> createHttpParamsCommon(
      @Nullable String sourceLang,
      String targetLang,
      @Nullable Formality formality,
      @Nullable String glossaryId) {
    targetLang = LanguageCode.standardize(targetLang);
    sourceLang = sourceLang == null ? null : LanguageCode.standardize(sourceLang);
    checkValidLanguages(sourceLang, targetLang);

    ArrayList<KeyValuePair<String, String>> params = new ArrayList<>();
    if (sourceLang != null) {
      params.add(new KeyValuePair<>("source_lang", sourceLang));
    }
    params.add(new KeyValuePair<>("target_lang", targetLang));

    if (formality != null && formality != Formality.Default) {
      switch (formality) {
        case More:
          params.add(new KeyValuePair<>("formality", "more"));
          break;
        case Less:
          params.add(new KeyValuePair<>("formality", "less"));
          break;
        default:
          break;
      }
    }

    if (glossaryId != null) {
      params.add(new KeyValuePair<>("glossary_id", glossaryId));
    }

    return params;
  }

  /** Combine XML tags with comma-delimiter to be included in HTTP request parameters. */
  private static String joinTags(Iterable<String> tags) {
    return String.join(",", tags);
  }

  /**
   * Checks the specified source and target language are valid.
   *
   * @param sourceLang Language code of the input language, or <code>null</code> to use
   *     auto-detection.
   * @param targetLang Language code of the desired output language.
   * @throws IllegalArgumentException If either language code is invalid.
   */
  private static void checkValidLanguages(@Nullable String sourceLang, String targetLang)
      throws IllegalArgumentException {
    if (sourceLang != null && sourceLang.isEmpty()) {
      throw new IllegalArgumentException("sourceLang must be null or non-empty");
    }
    if (targetLang.isEmpty()) {
      throw new IllegalArgumentException("targetLang must not be empty");
    }
    switch (targetLang) {
      case "en":
        throw new IllegalArgumentException(
            "targetLang=\"en\" is not allowed, please use \"en-GB\" or \"en-US\" instead");
      case "pt":
        throw new IllegalArgumentException(
            "targetLang=\"pt\" is not allowed, please use \"pt-PT\" or \"pt-BR\" instead");
      default:
        break;
    }
  }

  /**
   * Functions the same as {@link Translator#checkResponse(HttpResponse, boolean)} but accepts
   * response stream for document downloads. If the HTTP status code represents failure, the
   * response stream is converted to a String response to throw the appropriate exception.
   *
   * @see Translator#checkResponse(HttpResponse, boolean)
   */
  private void checkResponse(HttpResponseStream response) throws DeepLException {
    if (response.getCode() >= HttpURLConnection.HTTP_OK
        && response.getCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
      return;
    }
    if (response.getBody() == null) {
      throw new DeepLException("response stream is empty");
    }
    checkResponse(response.toStringResponse(), true);
  }

  /**
   * Checks the response HTTP status is OK, otherwise throws corresponding exception.
   *
   * @param response Response received from DeepL API.
   * @throws DeepLException Throws {@link DeepLException} or a derived exception depending on the
   *     type of error.
   */
  private void checkResponse(HttpResponse response, boolean inDocumentDownload)
      throws DeepLException {
    if (response.getCode() >= 200 && response.getCode() < 300) {
      return;
    }

    String messageSuffix = jsonParser.parseErrorMessage(response.getBody());
    if (!messageSuffix.isEmpty()) {
      messageSuffix = ", " + messageSuffix;
    }
    switch (response.getCode()) {
      case HttpURLConnection.HTTP_BAD_REQUEST:
        throw new DeepLException("Bad request" + messageSuffix);
      case HttpURLConnection.HTTP_FORBIDDEN:
        throw new AuthorizationException("Authorization failure, check auth_key" + messageSuffix);
      case HttpURLConnection.HTTP_NOT_FOUND:
        throw new NotFoundException("Not found, check serverUrl" + messageSuffix);
      case 429:
        throw new TooManyRequestsException(
            "Too many requests, DeepL servers are currently experiencing high load"
                + messageSuffix);
      case 456:
        throw new QuotaExceededException(
            "Quota for this billing period has been exceeded" + messageSuffix);
      case HttpURLConnection.HTTP_UNAVAILABLE:
        {
          if (inDocumentDownload) {
            throw new DocumentNotReadyException("Document not ready" + messageSuffix);
          } else {
            throw new DeepLException("Service unavailable" + messageSuffix);
          }
        }
      default:
        throw new DeepLException("Unknown error" + messageSuffix);
    }
  }

  private int calculateDocumentWaitTimeMillis(Long secondsRemaining) {
    if (secondsRemaining != null) {
      double secs = ((double) secondsRemaining) / 2.0 + 1.0;
      secs = max(1.0, min(secs, 60.0));
      return (int) (secs * 1000);
    }
    return 1000;
  }
}
