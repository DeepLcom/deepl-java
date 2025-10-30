// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public class TranslateDocumentTest extends TestBase {
  @Test
  void testTranslateDocument() throws Exception {
    Translator translator = createTranslator();

    File inputFile = createInputFile();
    File outputFile = createOutputFile();

    translator.translateDocument(inputFile, outputFile, "en", "de");
    Assertions.assertEquals(exampleOutput, readFromFile(outputFile));

    // Test with output path occupied
    Assertions.assertThrows(
        IOException.class,
        () -> {
          translator.translateDocument(inputFile, outputFile, "en", "de");
        });
  }

  @Test
  void testTranslateDocumentFailsWithOutputOccupied() throws Exception {
    Translator translator = createTranslator();

    File inputFile = createInputFile();
    File outputFile = createOutputFile();
    outputFile.createNewFile();

    // Test with output path occupied
    Assertions.assertThrows(
        IOException.class,
        () -> {
          translator.translateDocument(inputFile, outputFile, "en", "de");
        });
  }

  @Test
  void testTranslateDocumentWithRetry() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    Translator translator =
        createTranslator(
            new SessionOptions().setNoResponse(1),
            new TranslatorOptions().setTimeout(Duration.ofSeconds(1)));

    File outputFile = createOutputFile();
    translator.translateDocument(createInputFile(), outputFile, "en", "de");
    Assertions.assertEquals(exampleOutput, readFromFile(outputFile));
  }

  @Test
  void testTranslateDocumentWithWaiting() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    Translator translator =
        createTranslator(
            new SessionOptions()
                .setDocumentTranslateTime(Duration.ofSeconds(2))
                .setDocumentQueueTime(Duration.ofSeconds(2)));
    File outputFile = createOutputFile();
    translator.translateDocument(createInputFile(), outputFile, "en", "de");
    Assertions.assertEquals(exampleOutput, readFromFile(outputFile));
  }

  @Test
  void testTranslateLargeDocument() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    Translator translator = createTranslator();
    File inputFile = createInputFile(exampleLargeInput);
    File outputFile = createOutputFile();
    translator.translateDocument(inputFile, outputFile, "en", "de");
    Assertions.assertEquals(exampleLargeOutput, readFromFile(outputFile));
  }

  @Test
  void testTranslateDocumentFormality() throws Exception {
    Translator translator = createTranslator();
    File inputFile = createInputFile("How are you?");
    File outputFile = createOutputFile();
    translator.translateDocument(
        inputFile,
        outputFile,
        "en",
        "de",
        new DocumentTranslationOptions().setFormality(Formality.More));
    if (!isMockServer) {
      Assertions.assertTrue(readFromFile(outputFile).contains("Ihnen"));
    }

    outputFile.delete();

    translator.translateDocument(
        inputFile,
        outputFile,
        "en",
        "de",
        new DocumentTranslationOptions().setFormality(Formality.Less));
    if (!isMockServer) {
      Assertions.assertTrue(readFromFile(outputFile).contains("dir"));
    }
  }

  @Test
  void testTranslateDocumentFailureDuringTranslation() throws Exception {
    Translator translator = createTranslator();

    // Translating text from DE to DE will trigger error
    File inputFile = createInputFile(exampleText.get("de"));
    File outputFile = createOutputFile();

    DocumentTranslationException exception =
        Assertions.assertThrows(
            DocumentTranslationException.class,
            () -> {
              translator.translateDocument(inputFile, outputFile, null, "de");
            });
    Assertions.assertTrue(exception.getMessage().contains("Source and target language"));
  }

  @Test
  void testInvalidDocument() throws Exception {
    Translator translator = createTranslator();
    File inputFile = new File(tempDir + "/document.xyz");
    writeToFile(inputFile, exampleText.get("en"));
    File outputFile = new File(tempDir + "/output_document.xyz");
    outputFile.delete();

    DocumentTranslationException exception =
        Assertions.assertThrows(
            DocumentTranslationException.class,
            () -> {
              translator.translateDocument(inputFile, outputFile, "en", "de");
            });
    Assertions.assertNull(exception.getHandle());
  }

  @Test
  void testTranslateDocumentLowLevel() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    // Set a small document queue time to attempt downloading a queued document
    Translator translator =
        createTranslator(new SessionOptions().setDocumentQueueTime(Duration.ofMillis(100)));

    File inputFile = createInputFile();
    File outputFile = createOutputFile();
    final DocumentHandle handle = translator.translateDocumentUpload(inputFile, "en", "de");

    DocumentStatus status = translator.translateDocumentStatus(handle);
    Assertions.assertEquals(handle.getDocumentId(), status.getDocumentId());
    Assertions.assertTrue(status.ok());
    Assertions.assertFalse(status.done());

    // Test recreating a document handle from id & key
    String documentId = handle.getDocumentId();
    String documentKey = handle.getDocumentKey();
    DocumentHandle recreatedHandle = new DocumentHandle(documentId, documentKey);
    status = translator.translateDocumentStatus(recreatedHandle);
    Assertions.assertTrue(status.ok());

    while (status.ok() && !status.done()) {
      Thread.sleep(200);
      status = translator.translateDocumentStatus(recreatedHandle);
    }

    Assertions.assertTrue(status.ok() && status.done());
    translator.translateDocumentDownload(recreatedHandle, outputFile);
    Assertions.assertEquals(exampleOutput, readFromFile(outputFile));
  }

  @Test
  void testTranslateDocumentRequestFields() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    Translator translator =
        createTranslator(
            new SessionOptions()
                .setDocumentTranslateTime(Duration.ofSeconds(2))
                .setDocumentQueueTime(Duration.ofSeconds(2)));
    File inputFile = createInputFile();
    File outputFile = createOutputFile();

    long timeBefore = new Date().getTime();
    DocumentHandle handle = translator.translateDocumentUpload(inputFile, "en", "de");
    DocumentStatus status = translator.translateDocumentStatus(handle);
    Assertions.assertTrue(status.ok());
    Assertions.assertTrue(
        status.getSecondsRemaining() == null || status.getSecondsRemaining() >= 0);
    status = translator.translateDocumentWaitUntilDone(handle);
    translator.translateDocumentDownload(handle, outputFile);
    long timeAfter = new Date().getTime();

    Assertions.assertEquals(exampleInput.length(), status.getBilledCharacters());
    Assertions.assertTrue(timeAfter - timeBefore > 4000);
    Assertions.assertEquals(exampleOutput, readFromFile(outputFile));
  }

  @Test
  void testRecreateDocumentHandleInvalid() {
    Translator translator = createTranslator();
    String documentId = repeatString("12AB", 8);
    String documentKey = repeatString("CD34", 16);
    DocumentHandle handle = new DocumentHandle(documentId, documentKey);
    Assertions.assertThrows(
        NotFoundException.class,
        () -> {
          translator.translateDocumentStatus(handle);
        });
  }
}
