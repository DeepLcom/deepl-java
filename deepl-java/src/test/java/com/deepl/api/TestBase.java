// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import com.deepl.api.utils.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestBase {
  protected static final boolean isMockServer;
  protected static final boolean isMockProxyServer;
  protected static final String authKey;
  protected static final String serverUrl;
  protected static final String proxyUrl;

  protected static final Map<String, String> exampleText;

  private static final String tempDirBase;

  final String exampleInput = exampleText.get("en");
  final String exampleLargeInput = repeatString(exampleText.get("en") + "\n", 1000);
  final String exampleOutput = exampleText.get("de");
  final String exampleLargeOutput = repeatString(exampleText.get("de") + "\n", 1000);
  final String tempDir;

  static {
    isMockServer = System.getenv("DEEPL_MOCK_SERVER_PORT") != null;
    serverUrl = System.getenv("DEEPL_SERVER_URL");
    proxyUrl = System.getenv("DEEPL_PROXY_URL");
    isMockProxyServer = proxyUrl != null;
    if (isMockServer) {
      authKey = "mock_server";
      if (serverUrl == null) {
        System.err.println(
            "DEEPL_SERVER_URL environment variable must be set when using mock server.");
        System.exit(1);
      }
    } else {
      authKey = System.getenv("DEEPL_AUTH_KEY");
      if (authKey == null) {
        System.err.println(
            "DEEPL_AUTH_KEY environment variable must be set unless using mock server.");
        System.exit(1);
      }
    }

    exampleText = new HashMap<>();
    exampleText.put("ar", "شعاع البروتون");
    exampleText.put("bg", "протонен лъч");
    exampleText.put("cs", "protonový paprsek");
    exampleText.put("da", "protonstråle");
    exampleText.put("de", "Protonenstrahl");
    exampleText.put("el", "δέσμη πρωτονίων");
    exampleText.put("en", "proton beam");
    exampleText.put("en-US", "proton beam");
    exampleText.put("en-GB", "proton beam");
    exampleText.put("es", "haz de protones");
    exampleText.put("et", "prootonikiirgus");
    exampleText.put("fi", "protonisäde");
    exampleText.put("fr", "faisceau de protons");
    exampleText.put("hu", "protonnyaláb");
    exampleText.put("id", "berkas proton");
    exampleText.put("it", "fascio di protoni");
    exampleText.put("ja", "陽子ビーム");
    exampleText.put("ko", "양성자 빔");
    exampleText.put("lt", "protonų spindulys");
    exampleText.put("lv", "protonu staru kūlis");
    exampleText.put("nb", "protonstråle");
    exampleText.put("nl", "protonenbundel");
    exampleText.put("pl", "wiązka protonów");
    exampleText.put("pt", "feixe de prótons");
    exampleText.put("pt-BR", "feixe de prótons");
    exampleText.put("pt-PT", "feixe de prótons");
    exampleText.put("ro", "fascicul de protoni");
    exampleText.put("ru", "протонный луч");
    exampleText.put("sk", "protónový lúč");
    exampleText.put("sl", "protonski žarek");
    exampleText.put("sv", "protonstråle");
    exampleText.put("tr", "proton ışını");
    exampleText.put("zh", "质子束");

    String tmpdir = System.getProperty("java.io.tmpdir");
    tempDirBase = tmpdir.endsWith("/") ? tmpdir : tmpdir + "/";
  }

  protected TestBase() {
    tempDir = createTempDir();
  }

  // TODO: Delete `createTranslator` methods, replace with `createDeepLClient`
  protected Translator createTranslator() {
    SessionOptions sessionOptions = new SessionOptions();
    return createTranslator(sessionOptions);
  }

  protected Translator createTranslator(SessionOptions sessionOptions) {
    TranslatorOptions translatorOptions = new TranslatorOptions();
    return createTranslator(sessionOptions, translatorOptions);
  }

  protected Translator createTranslator(
      SessionOptions sessionOptions, TranslatorOptions translatorOptions) {
    Map<String, String> headers = sessionOptions.createSessionHeaders();

    if (translatorOptions.getServerUrl() == null) {
      translatorOptions.setServerUrl(serverUrl);
    }

    if (translatorOptions.getHeaders() != null) {
      headers.putAll(translatorOptions.getHeaders());
    }
    translatorOptions.setHeaders(headers);

    String authKey = sessionOptions.randomAuthKey ? UUID.randomUUID().toString() : TestBase.authKey;

    try {
      return new Translator(authKey, translatorOptions);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  protected DeepLClient createDeepLClient() {
    SessionOptions sessionOptions = new SessionOptions();
    return createDeepLClient(sessionOptions);
  }

  protected DeepLClient createDeepLClient(SessionOptions sessionOptions) {
    TranslatorOptions translatorOptions = new TranslatorOptions();
    return createDeepLClient(sessionOptions, translatorOptions);
  }

  protected DeepLClient createDeepLClient(
      SessionOptions sessionOptions, TranslatorOptions translatorOptions) {
    Map<String, String> headers = sessionOptions.createSessionHeaders();

    if (translatorOptions.getServerUrl() == null) {
      translatorOptions.setServerUrl(serverUrl);
    }

    if (translatorOptions.getHeaders() != null) {
      headers.putAll(translatorOptions.getHeaders());
    }
    translatorOptions.setHeaders(headers);

    String authKey = sessionOptions.randomAuthKey ? UUID.randomUUID().toString() : TestBase.authKey;

    try {
      return new DeepLClient(authKey, translatorOptions);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  protected String createTempDir() {
    String newTempDir = tempDirBase + UUID.randomUUID();
    boolean created = new File(newTempDir).mkdirs();
    return newTempDir;
  }

  protected void writeToFile(File file, String content) throws IOException {
    Boolean justCreated = file.createNewFile();
    FileWriter writer = new FileWriter(file);
    writer.write(content);
    writer.flush();
    writer.close();
  }

  protected String readFromFile(File file) throws IOException {
    if (!file.exists()) return "";
    return StreamUtil.readStream(new FileInputStream(file));
  }

  /**
   * Returns a string containing the input string repeated given number of times. Note:
   * String.repeat() was added in Java 11.
   *
   * @param input Input string to be repeated.
   * @param number Number of times to repeat string.
   * @return Input string repeated given number of times.
   */
  protected static String repeatString(String input, int number) {
    StringBuilder sb = new StringBuilder(input.length() * number);
    for (int i = 0; i < number; i++) {
      sb.append(input);
    }
    return sb.toString();
  }

  protected File createInputFile() throws IOException {
    return createInputFile(exampleInput);
  }

  protected File createInputFile(String content) throws IOException {
    File inputFile = new File(tempDir + "/example_document.txt");
    boolean ignored = inputFile.delete();
    ignored = inputFile.createNewFile();
    writeToFile(inputFile, content);
    return inputFile;
  }

  protected File createOutputFile() {
    File outputFile = new File(tempDir + "/output/example_document.txt");
    boolean ignored = new File(outputFile.getParent()).mkdir();
    ignored = outputFile.delete();
    return outputFile;
  }
}
