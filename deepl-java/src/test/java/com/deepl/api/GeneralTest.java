// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

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
  void testNullAuthKey() {
    IllegalArgumentException thrown =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {
              Translator translator = new Translator(null);
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
    DeepLClient client = createDeepLClient();

    for (Map.Entry<String, String> entry : exampleText.entrySet()) {
      String inputText = entry.getValue();
      String sourceLang = LanguageCode.removeRegionalVariant(entry.getKey());
      TextResult result = client.translateText(inputText, sourceLang, "en-US");
      Assertions.assertTrue(result.getText().toLowerCase(Locale.ENGLISH).contains("proton"));
      Assertions.assertEquals(inputText.length(), result.getBilledCharacters());
    }
  }

  @ParameterizedTest
  @CsvSource({
    "quality_optimized,quality_optimized",
    "prefer_quality_optimized,quality_optimized",
    "latency_optimized,latency_optimized"
  })
  void testModelType(String modelTypeArg, String expectedModelType)
      throws DeepLException, InterruptedException {
    DeepLClient client = createDeepLClient();
    String sourceLang = "de";
    TextResult result =
        client.translateText(
            exampleText.get(sourceLang),
            sourceLang,
            "en-US",
            new TextTranslationOptions().setModelType(modelTypeArg));
    Assertions.assertEquals(expectedModelType, result.getModelTypeUsed());
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
  void testMixedDirectionText() throws DeepLException, InterruptedException {
    Assumptions.assumeFalse(isMockServer);
    DeepLClient client = createDeepLClient();
    TextTranslationOptions options =
        new TextTranslationOptions().setTagHandling("xml").setIgnoreTags(Arrays.asList("xml"));
    String arIgnorePart = "<ignore>يجب تجاهل هذا الجزء.</ignore>";
    String enSentenceWithArIgnorePart =
        "<p>This is a <b>short</b> <i>sentence</i>. " + arIgnorePart + " This is another sentence.";
    String enIgnorePart = "<ignore>This part should be ignored.</ignore>";
    String arSentenceWithEnIgnorePart =
        "<p>هذه <i>جملة</i> <b>قصيرة</b>. " + enIgnorePart + "هذه جملة أخرى.</p>";

    TextResult enResult = client.translateText(enSentenceWithArIgnorePart, null, "en-US", options);
    Assertions.assertTrue(enResult.getText().contains(arIgnorePart));
    TextResult arResult = client.translateText(arSentenceWithEnIgnorePart, null, "ar", options);
    Assertions.assertTrue(arResult.getText().contains(enIgnorePart));
  }

  @Test
  void testUsage() throws DeepLException, InterruptedException {
    DeepLClient client = createDeepLClient();
    Usage usage = client.getUsage();
    Assertions.assertTrue(usage.toString().contains("Usage this billing period"));
  }

  @Test
  void testUsageLarge() throws DeepLException, InterruptedException {
    Assumptions.assumeTrue(isMockServer);
    SessionOptions sessionOptions = new SessionOptions();
    sessionOptions.initCharacterLimit = 1000000000000L;
    Map<String, String> headers = sessionOptions.createSessionHeaders();

    TranslatorOptions options = new TranslatorOptions().setHeaders(headers).setServerUrl(serverUrl);
    String authKeyWithUuid = authKey + "/" + UUID.randomUUID().toString();
    Translator translator = new Translator(authKeyWithUuid, options);
    Usage usage = translator.getUsage();
    Assertions.assertNotNull(usage.getCharacter());
    Assertions.assertEquals(sessionOptions.initCharacterLimit, usage.getCharacter().getLimit());
  }

  @Test
  void testGetSourceAndTargetLanguages() throws DeepLException, InterruptedException {
    DeepLClient client = createDeepLClient();
    List<Language> sourceLanguages = client.getSourceLanguages();
    List<Language> targetLanguages = client.getTargetLanguages();

    for (Language language : sourceLanguages) {
      if (Objects.equals(language.getCode(), "en")) {
        Assertions.assertEquals("English", language.getName());
      }
      Assertions.assertNull(language.getSupportsFormality());
    }
    Assertions.assertTrue(sourceLanguages.size() >= 29);

    for (Language language : targetLanguages) {
      Assertions.assertNotNull(language.getSupportsFormality());
      if (Objects.equals(language.getCode(), "de")) {
        Assertions.assertTrue(language.getSupportsFormality());
        Assertions.assertEquals("German", language.getName());
      }
    }
    Assertions.assertTrue(targetLanguages.size() >= 31);
  }

  @Test
  void testGetGlossaryLanguages() throws DeepLException, InterruptedException {
    DeepLClient client = createDeepLClient();
    List<GlossaryLanguagePair> glossaryLanguagePairs = client.getGlossaryLanguages();
    Assertions.assertTrue(glossaryLanguagePairs.size() > 0);
    for (GlossaryLanguagePair glossaryLanguagePair : glossaryLanguagePairs) {
      Assertions.assertTrue(glossaryLanguagePair.getSourceLanguage().length() > 0);
      Assertions.assertTrue(glossaryLanguagePair.getTargetLanguage().length() > 0);
    }
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
    DeepLClient client =
        createDeepLClient(
            new SessionOptions().setNoResponse(2),
            new TranslatorOptions().setMaxRetries(0).setTimeout(Duration.ofMillis(1)));

    Assertions.assertThrows(ConnectionException.class, client::getUsage);
  }

  @Test
  void testTranslateTooManyRequests() {
    Assumptions.assumeTrue(isMockServer);
    // Lower the retry count and timeout for this test
    DeepLClient client =
        createDeepLClient(
            new SessionOptions().setRespondWith429(2), new TranslatorOptions().setMaxRetries(0));

    Assertions.assertThrows(
        TooManyRequestsException.class,
        () -> client.translateText(exampleText.get("en"), null, "DE"));
  }

  @Test
  void testUsageOverrun() throws DeepLException, InterruptedException, IOException {
    Assumptions.assumeTrue(isMockServer);
    int characterLimit = 20;
    int documentLimit = 1;
    // Lower the retry count and timeout for this test
    DeepLClient client =
        createDeepLClient(
            new SessionOptions()
                .setInitCharacterLimit(characterLimit)
                .setInitDocumentLimit(documentLimit)
                .withRandomAuthKey(),
            new TranslatorOptions().setMaxRetries(0).setTimeout(Duration.ofMillis(1)));

    Usage usage = client.getUsage();
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

    client.translateDocument(inputFile, outputFile, null, "de");

    usage = client.getUsage();
    Assertions.assertTrue(usage.anyLimitReached());
    Assertions.assertNotNull(usage.getCharacter());
    Assertions.assertNotNull(usage.getDocument());
    Assertions.assertTrue(usage.getDocument().limitReached());
    Assertions.assertTrue(usage.getCharacter().limitReached());

    Assertions.assertThrows(
        IOException.class,
        () -> {
          client.translateDocument(inputFile, outputFile, null, "de");
        });
    outputFile.delete();

    DocumentTranslationException thrownDeepLException =
        Assertions.assertThrows(
            DocumentTranslationException.class,
            () -> {
              client.translateDocument(inputFile, outputFile, null, "de");
            });
    Assertions.assertNull(thrownDeepLException.getHandle());
    Assertions.assertEquals(
        QuotaExceededException.class, thrownDeepLException.getCause().getClass());

    Assertions.assertThrows(
        QuotaExceededException.class,
        () -> {
          client.translateText(exampleText.get("en"), null, "de");
        });
  }

  @Test
  void testUsageTeamDocumentLimit() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    int teamDocumentLimit = 1;
    DeepLClient client =
        createDeepLClient(
            new SessionOptions()
                .setInitCharacterLimit(0)
                .setInitDocumentLimit(0)
                .setInitTeamDocumentLimit(teamDocumentLimit)
                .withRandomAuthKey());

    Usage usage = client.getUsage();
    Assertions.assertNull(usage.getCharacter());
    Assertions.assertNull(usage.getDocument());
    Assertions.assertNotNull(usage.getTeamDocument());
    Assertions.assertEquals(0, usage.getTeamDocument().getCount());
    Assertions.assertEquals(teamDocumentLimit, usage.getTeamDocument().getLimit());
    Assertions.assertTrue(usage.toString().contains("Team documents: 0 of 1"));

    File inputFile = createInputFile();
    writeToFile(inputFile, "a");
    File outputFile = createOutputFile();

    client.translateDocument(inputFile, outputFile, null, "de");

    usage = client.getUsage();
    Assertions.assertTrue(usage.anyLimitReached());
    Assertions.assertNotNull(usage.getTeamDocument());
    Assertions.assertTrue(usage.getTeamDocument().limitReached());
  }

  @ParameterizedTest
  @MethodSource("provideUserAgentTestData")
  void testUserAgent(
      SessionOptions sessionOptions,
      TranslatorOptions translatorOptions,
      Iterable<String> requiredStrings,
      Iterable<String> blocklistedStrings)
      throws Exception {
    Map<String, String> headers = new HashMap<>();
    HttpURLConnection con = Mockito.mock(HttpURLConnection.class);
    Mockito.doAnswer(
            invocation -> {
              String key = (String) invocation.getArgument(0);
              String value = (String) invocation.getArgument(1);
              headers.put(key, value);
              return null;
            })
        .when(con)
        .setRequestProperty(Mockito.any(String.class), Mockito.any(String.class));
    Mockito.when(con.getResponseCode()).thenReturn(200);
    try (MockedConstruction<URL> mockUrl =
        Mockito.mockConstruction(
            URL.class,
            (mock, context) -> {
              Mockito.when(mock.openConnection()).thenReturn(con);
            })) {
      DeepLClient client = createDeepLClient(sessionOptions, translatorOptions);
      Usage usage = client.getUsage();
      String userAgentHeader = headers.get("User-Agent");
      for (String s : requiredStrings) {
        Assertions.assertTrue(
            userAgentHeader.contains(s),
            String.format(
                "Expected User-Agent header to contain %s\nActual:\n%s", s, userAgentHeader));
      }
      for (String n : blocklistedStrings) {
        Assertions.assertFalse(
            userAgentHeader.contains(n),
            String.format(
                "Expected User-Agent header not to contain %s\nActual:\n%s", n, userAgentHeader));
      }
    }
  }

  @Test
  @EnabledIf("runV1ApiTests")
  void testV1Api() throws DeepLException, InterruptedException {
    SessionOptions sessionOptions = new SessionOptions();
    DeepLClientOptions clientOptions =
        (new DeepLClientOptions()).setApiVersion(DeepLApiVersion.VERSION_1);
    DeepLClient client = createDeepLClient(sessionOptions, clientOptions);

    for (Map.Entry<String, String> entry : exampleText.entrySet()) {
      String inputText = entry.getValue();
      String sourceLang = LanguageCode.removeRegionalVariant(entry.getKey());
      TextResult result = client.translateText(inputText, sourceLang, "en-US");
      Assertions.assertTrue(result.getText().toLowerCase(Locale.ENGLISH).contains("proton"));
      Assertions.assertEquals(inputText.length(), result.getBilledCharacters());
    }
    Usage usage = client.getUsage();
    Assertions.assertTrue(usage.toString().contains("Usage this billing period"));

    List<Language> sourceLanguages = client.getSourceLanguages();
    List<Language> targetLanguages = client.getTargetLanguages();
    Assertions.assertTrue(sourceLanguages.size() > 20);
    Assertions.assertTrue(targetLanguages.size() > 20);
    Assertions.assertTrue(targetLanguages.size() >= sourceLanguages.size());
  }

  // Session options & Translator options: Used to construct the `Translator`
  // Next arg: List of Strings that must be contained in the user agent header
  // Last arg: List of Strings that must not be contained in the user agent header
  private static Stream<? extends Arguments> provideUserAgentTestData() {
    Map<String, String> testHeaders = new HashMap<>();
    testHeaders.put("User-Agent", "my custom user agent");
    Iterable<String> lightPlatformInfo = Arrays.asList("deepl-java/");
    Iterable<String> lightPlatformInfoWithAppInfo =
        Arrays.asList("deepl", "my-java-translation-plugin/1.2.3");
    Iterable<String> detailedPlatformInfo = Arrays.asList(" java/", "(");
    Iterable<String> detailedPlatformInfoWithAppInfo =
        Arrays.asList(" java/", "(", "my-java-translation-plugin/1.2.3");
    Iterable<String> customUserAgent = Arrays.asList("my custom user agent");
    Iterable<String> noStrings = new ArrayList<String>();
    return Stream.of(
        Arguments.of(
            new SessionOptions(), new TranslatorOptions(), detailedPlatformInfo, noStrings),
        Arguments.of(
            new SessionOptions(),
            new TranslatorOptions().setSendPlatformInfo(false),
            lightPlatformInfo,
            detailedPlatformInfo),
        Arguments.of(
            new SessionOptions(),
            new TranslatorOptions().setHeaders(testHeaders),
            customUserAgent,
            detailedPlatformInfo),
        Arguments.of(
            new SessionOptions(),
            new TranslatorOptions().setAppInfo("my-java-translation-plugin", "1.2.3"),
            detailedPlatformInfoWithAppInfo,
            noStrings),
        Arguments.of(
            new SessionOptions(),
            new TranslatorOptions()
                .setSendPlatformInfo(false)
                .setAppInfo("my-java-translation-plugin", "1.2.3"),
            lightPlatformInfoWithAppInfo,
            detailedPlatformInfo),
        Arguments.of(
            new SessionOptions(),
            new TranslatorOptions()
                .setHeaders(testHeaders)
                .setAppInfo("my-java-translation-plugin", "1.2.3"),
            customUserAgent,
            detailedPlatformInfoWithAppInfo));
  }

  boolean runV1ApiTests() {
    return Boolean.getBoolean("runV1ApiTests");
  }
}
