// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.

package com.deepl.api;

import com.deepl.api.http.HttpResponse;
import com.deepl.api.utils.KeyValuePair;
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
}
