// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;

class GeneralTest extends TestBase {

  @Test
  void testEmptyAuthKey() {
    IllegalArgumentException thrown =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {
              Translator translator = new Translator("");
            });
  }

  @Test
  void testInvalidAuthKey() {
    String authKey = "invalid";
    Translator translator = new Translator(authKey);
    Assertions.assertThrows(AuthorizationException.class, translator::getUsage);
  }

  @Test
  void testExampleTranslation() throws DeepLException, InterruptedException {
    Translator translator = createTranslator();

    for (Map.Entry<String, String> entry : exampleText.entrySet()) {
      String inputText = entry.getValue();
      String sourceLang = LanguageCode.removeRegionalVariant(entry.getKey());
      TextResult result = translator.translateText(inputText, sourceLang, "en-US");
      Assertions.assertTrue(result.getText().toLowerCase(Locale.ENGLISH).contains("proton"));
    }
  }

  @Test
  void testInvalidServerUrl() {
    Assertions.assertThrows(
        DeepLException.class,
        () -> {
          Translator translator =
              new Translator(authKey, new TranslatorOptions().setServerUrl("http:/api.deepl.com"));
          translator.getUsage();
        });
  }

  @Test
  void testUsage() throws DeepLException, InterruptedException {
    Translator translator = createTranslator();
    Usage usage = translator.getUsage();
    Assertions.assertTrue(usage.toString().contains("Usage this billing period"));
  }

  @Test
  void testGetSourceAndTargetLanguages() throws DeepLException, InterruptedException {
    Translator translator = createTranslator();
    List<Language> sourceLanguages = translator.getSourceLanguages();
    List<Language> targetLanguages = translator.getTargetLanguages();

    for (Language language : sourceLanguages) {
      if (Objects.equals(language.getCode(), "en")) {
        Assertions.assertEquals("English", language.getName());
      }
      Assertions.assertNull(language.getSupportsFormality());
    }
    Assertions.assertTrue(sourceLanguages.size() > 20);

    for (Language language : targetLanguages) {
      Assertions.assertNotNull(language.getSupportsFormality());
      if (Objects.equals(language.getCode(), "de")) {
        Assertions.assertTrue(language.getSupportsFormality());
        Assertions.assertEquals("German", language.getName());
      }
    }
    Assertions.assertTrue(targetLanguages.size() > 20);
  }

  @Test
  void testAuthKeyIsFreeAccount() {
    Assertions.assertTrue(
        Translator.isFreeAccountAuthKey("b493b8ef-0176-215d-82fe-e28f182c9544:fx"));
    Assertions.assertFalse(Translator.isFreeAccountAuthKey("b493b8ef-0176-215d-82fe-e28f182c9544"));
  }

  @Test
  void testProxyUsage() throws DeepLException, InterruptedException, MalformedURLException {
    Assumptions.assumeTrue(isMockProxyServer);
    SessionOptions sessionOptions = new SessionOptions();
    sessionOptions.expectProxy = true;
    Map<String, String> headers = sessionOptions.createSessionHeaders();

    URL proxyUrl = new URL(TestBase.proxyUrl);
    TranslatorOptions options =
        new TranslatorOptions()
            .setProxy(
                new Proxy(
                    Proxy.Type.HTTP, new InetSocketAddress(proxyUrl.getHost(), proxyUrl.getPort())))
            .setHeaders(headers)
            .setServerUrl(serverUrl);
    Translator translator = new Translator(authKey, options);
    translator.getUsage();
  }

  @Test
  void testUsageNoResponse() {
    Assumptions.assumeTrue(isMockServer);
    // Lower the retry count and timeout for this test
    Translator translator =
        createTranslator(
            new SessionOptions().setNoResponse(2),
            new TranslatorOptions().setMaxRetries(0).setTimeout(Duration.ofMillis(1)));

    Assertions.assertThrows(ConnectionException.class, translator::getUsage);
  }

  @Test
  void testTranslateTooManyRequests() {
    Assumptions.assumeTrue(isMockServer);
    // Lower the retry count and timeout for this test
    Translator translator =
        createTranslator(
            new SessionOptions().setRespondWith429(2), new TranslatorOptions().setMaxRetries(0));

    Assertions.assertThrows(
        TooManyRequestsException.class,
        () -> translator.translateText(exampleText.get("en"), null, "DE"));
  }

  @Test
  void testUsageOverrun() throws DeepLException, InterruptedException, IOException {
    Assumptions.assumeTrue(isMockServer);
    int characterLimit = 20;
    int documentLimit = 1;
    // Lower the retry count and timeout for this test
    Translator translator =
        createTranslator(
            new SessionOptions()
                .setInitCharacterLimit(characterLimit)
                .setInitDocumentLimit(documentLimit)
                .withRandomAuthKey(),
            new TranslatorOptions().setMaxRetries(0).setTimeout(Duration.ofMillis(1)));

    Usage usage = translator.getUsage();
    Assertions.assertNotNull(usage.getCharacter());
    Assertions.assertNotNull(usage.getDocument());
    Assertions.assertNull(usage.getTeamDocument());
    Assertions.assertEquals(0, usage.getCharacter().getCount());
    Assertions.assertEquals(0, usage.getDocument().getCount());
    Assertions.assertEquals(characterLimit, usage.getCharacter().getLimit());
    Assertions.assertEquals(documentLimit, usage.getDocument().getLimit());
    Assertions.assertTrue(usage.toString().contains("Characters: 0 of 20"));
    Assertions.assertTrue(usage.toString().contains("Documents: 0 of 1"));

    File inputFile = createInputFile();
    writeToFile(inputFile, repeatString("a", characterLimit));
    File outputFile = createOutputFile();

    translator.translateDocument(inputFile, outputFile, null, "de");

    usage = translator.getUsage();
    Assertions.assertTrue(usage.anyLimitReached());
    Assertions.assertNotNull(usage.getCharacter());
    Assertions.assertNotNull(usage.getDocument());
    Assertions.assertTrue(usage.getDocument().limitReached());
    Assertions.assertTrue(usage.getCharacter().limitReached());

    Assertions.assertThrows(
        IOException.class,
        () -> {
          translator.translateDocument(inputFile, outputFile, null, "de");
        });
    outputFile.delete();

    DocumentTranslationException thrownDeepLException =
        Assertions.assertThrows(
            DocumentTranslationException.class,
            () -> {
              translator.translateDocument(inputFile, outputFile, null, "de");
            });
    Assertions.assertNull(thrownDeepLException.getHandle());
    Assertions.assertEquals(
        QuotaExceededException.class, thrownDeepLException.getCause().getClass());

    Assertions.assertThrows(
        QuotaExceededException.class,
        () -> {
          translator.translateText(exampleText.get("en"), null, "de");
        });
  }

  @Test
  void testUsageTeamDocumentLimit() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    int teamDocumentLimit = 1;
    Translator translator =
        createTranslator(
            new SessionOptions()
                .setInitCharacterLimit(0)
                .setInitDocumentLimit(0)
                .setInitTeamDocumentLimit(teamDocumentLimit)
                .withRandomAuthKey());

    Usage usage = translator.getUsage();
    Assertions.assertNull(usage.getCharacter());
    Assertions.assertNull(usage.getDocument());
    Assertions.assertNotNull(usage.getTeamDocument());
    Assertions.assertEquals(0, usage.getTeamDocument().getCount());
    Assertions.assertEquals(teamDocumentLimit, usage.getTeamDocument().getLimit());
    Assertions.assertTrue(usage.toString().contains("Team documents: 0 of 1"));

    File inputFile = createInputFile();
    writeToFile(inputFile, "a");
    File outputFile = createOutputFile();

    translator.translateDocument(inputFile, outputFile, null, "de");

    usage = translator.getUsage();
    Assertions.assertTrue(usage.anyLimitReached());
    Assertions.assertNotNull(usage.getTeamDocument());
    Assertions.assertTrue(usage.getTeamDocument().limitReached());
  }
}
