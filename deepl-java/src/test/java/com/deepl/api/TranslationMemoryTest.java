// Copyright 2025 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.*;

public class TranslationMemoryTest extends TestBase {
  private static final String DEFAULT_TM_ID = "a74d88fb-ed2a-4943-a664-a4512398b994";
  private static final String UNKNOWN_ID = "00000000-0000-0000-0000-000000000000";
  private static final String EXAMPLE_TMX =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "<tmx version=\"1.4\"><body>"
          + "<tu><tuv xml:lang=\"de\"><seg>Hallo</seg></tuv>"
          + "<tuv xml:lang=\"en\"><seg>Hello</seg></tuv></tu>"
          + "</body></tmx>\n";

  @Test
  void testListTranslationMemories() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    List<TranslationMemoryInfo> translationMemories = client.listTranslationMemories(0, 10);

    Assertions.assertNotNull(translationMemories);
    Assertions.assertFalse(translationMemories.isEmpty());
    Assertions.assertNotNull(translationMemories.get(0).getTranslationMemoryId());
    Assertions.assertNotNull(translationMemories.get(0).getName());
    Assertions.assertNotNull(translationMemories.get(0).getSourceLanguage());
    Assertions.assertNotNull(translationMemories.get(0).getTargetLanguages());
  }

  @Test
  void testTranslateTextWithTranslationMemoryId() throws Exception {
    // Note: this test may use the mock server that will not translate the text
    // with a translation memory, therefore we do not check the translated result.
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    String text = "Hallo, Welt!";

    TextResult result =
        client.translateText(
            text,
            "de",
            "en-US",
            new TextTranslationOptions().setTranslationMemoryId(DEFAULT_TM_ID));

    Assertions.assertNotNull(result);
  }

  @Test
  void testTranslateTextWithTranslationMemoryIdAndThreshold() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    String text = "Hallo, Welt!";

    TextResult result =
        client.translateText(
            text,
            "de",
            "en-US",
            new TextTranslationOptions()
                .setTranslationMemoryId(DEFAULT_TM_ID)
                .setTranslationMemoryThreshold(80));

    Assertions.assertNotNull(result);
  }

  @Test
  void testTranslateDocumentWithTranslationMemoryId() throws Exception {
    // Note: this test may use the mock server that will not translate the document
    // with a translation memory, therefore we do not check the translated result.
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    File inputFile = createInputFile("Hallo, Welt!");
    File outputFile = createOutputFile();

    client.translateDocument(
        inputFile,
        outputFile,
        "de",
        "en-US",
        new DocumentTranslationOptions().setTranslationMemoryId(DEFAULT_TM_ID));

    Assertions.assertNotNull(readFromFile(outputFile));
  }

  @Test
  void testTranslateDocumentWithTranslationMemoryIdAndThreshold() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    File inputFile = createInputFile("Hallo, Welt!");
    File outputFile = createOutputFile();

    client.translateDocument(
        inputFile,
        outputFile,
        "de",
        "en-US",
        new DocumentTranslationOptions()
            .setTranslationMemoryId(DEFAULT_TM_ID)
            .setTranslationMemoryThreshold(80));

    Assertions.assertNotNull(readFromFile(outputFile));
  }

  @Test
  void testTranslateDocumentWithTranslationMemoryInfo() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    List<TranslationMemoryInfo> translationMemories = client.listTranslationMemories(0, 10);
    TranslationMemoryInfo translationMemory = translationMemories.get(0);
    File inputFile = createInputFile("Hallo, Welt!");
    File outputFile = createOutputFile();

    client.translateDocument(
        inputFile,
        outputFile,
        "de",
        "en-US",
        new DocumentTranslationOptions().setTranslationMemory(translationMemory));

    Assertions.assertNotNull(readFromFile(outputFile));
  }

  @Test
  void testGetTranslationMemory() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();

    TranslationMemoryInfo translationMemory = client.getTranslationMemory(DEFAULT_TM_ID);

    Assertions.assertEquals(DEFAULT_TM_ID, translationMemory.getTranslationMemoryId());
    Assertions.assertNotNull(translationMemory.getName());
    Assertions.assertEquals("de", translationMemory.getSourceLanguage());
    Assertions.assertFalse(translationMemory.getTargetLanguages().isEmpty());
    Assertions.assertTrue(translationMemory.getSegmentCount() > 0);
    Assertions.assertNotNull(translationMemory.getCreationTime());
    Assertions.assertNotNull(translationMemory.getUpdatedTime());
  }

  @Test
  void testGetTranslationMemoryWithTranslationMemoryInfo() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    TranslationMemoryInfo listed = client.listTranslationMemories().get(0);

    TranslationMemoryInfo translationMemory = client.getTranslationMemory(listed);

    Assertions.assertEquals(
        listed.getTranslationMemoryId(), translationMemory.getTranslationMemoryId());
  }

  @Test
  void testGetTranslationMemoryWithUnknownId() {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();

    Assertions.assertThrows(NotFoundException.class, () -> client.getTranslationMemory(UNKNOWN_ID));
  }

  @Test
  void testListTranslationMemorySegments() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();

    TranslationMemorySegments page = client.listTranslationMemorySegments(DEFAULT_TM_ID);

    Assertions.assertFalse(page.getSegments().isEmpty());
    Assertions.assertTrue(page.getSegmentCount() > 0);
    TranslationMemorySegment segment = page.getSegments().get(0);
    Assertions.assertNotNull(segment.getSourceSegmentId());
    Assertions.assertNotNull(segment.getSourceText());
    Assertions.assertFalse(segment.getTargets().isEmpty());
    TranslationMemoryTargetSegment target = segment.getTargets().get(0);
    Assertions.assertNotNull(target.getTargetSegmentId());
    Assertions.assertNotNull(target.getTargetLanguage());
    Assertions.assertNotNull(target.getTargetText());
  }

  @Test
  void testListTranslationMemorySegmentsPagination() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();

    TranslationMemorySegments firstPage =
        client.listTranslationMemorySegments(
            DEFAULT_TM_ID, new TranslationMemorySegmentsOptions().setPageSize(5));

    Assertions.assertEquals(5, firstPage.getSegments().size());
    Assertions.assertNotNull(firstPage.getNextPageCursor());

    TranslationMemorySegments secondPage =
        client.listTranslationMemorySegments(
            DEFAULT_TM_ID,
            new TranslationMemorySegmentsOptions()
                .setPageSize(5)
                .setPageCursor(firstPage.getNextPageCursor()));

    Assertions.assertFalse(secondPage.getSegments().isEmpty());
    for (TranslationMemorySegment segment : secondPage.getSegments()) {
      for (TranslationMemorySegment firstPageSegment : firstPage.getSegments()) {
        Assertions.assertNotEquals(
            firstPageSegment.getSourceSegmentId(), segment.getSourceSegmentId());
      }
    }
  }

  @Test
  void testListTranslationMemorySegmentsFilter() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();

    TranslationMemorySegments unfiltered = client.listTranslationMemorySegments(DEFAULT_TM_ID);
    TranslationMemorySegments filtered =
        client.listTranslationMemorySegments(
            DEFAULT_TM_ID, new TranslationMemorySegmentsOptions().setFilterText("Nummer 7"));

    Assertions.assertTrue(filtered.getSegments().size() < unfiltered.getSegments().size());
    // segmentCount is translation-memory-level metadata and unaffected by the filter
    Assertions.assertEquals(unfiltered.getSegmentCount(), filtered.getSegmentCount());
  }

  @Test
  void testImportTranslationMemoryFromFilepath() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();

    TranslationMemoryJob job =
        client.importTranslationMemoryFromFilepath(createTmxFile(), "Imported TM");

    Assertions.assertEquals(TranslationMemoryJob.Operation.Import, job.getOperation());
    Assertions.assertEquals("translation_memory", job.getProduct());
    Assertions.assertEquals(TranslationMemoryJobResult.Status.Completed, job.getStatus());
    String translationMemoryId = job.getResult().getTranslationMemoryId();
    Assertions.assertNotNull(translationMemoryId);

    TranslationMemoryInfo imported = client.getTranslationMemory(translationMemoryId);
    Assertions.assertEquals("Imported TM", imported.getName());

    client.deleteTranslationMemory(imported);
  }

  @Test
  void testCreateTranslationMemoryImportAwaitsUpload() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();

    TranslationMemoryImport translationMemoryImport =
        client.createTranslationMemoryImport("example.tmx", 1024, null, "Awaiting Upload TM");

    Assertions.assertNotNull(translationMemoryImport.getJobId());
    Assertions.assertNotNull(translationMemoryImport.getUploadUrl());

    TranslationMemoryJob job = client.getTranslationMemoryJob(translationMemoryImport.getJobId());
    Assertions.assertEquals(TranslationMemoryJobResult.Status.AwaitingInput, job.getStatus());
    Assertions.assertNotNull(job.getResult().getRequiredAction());
    // A job whose file is never uploaded does not finish on its own, so waiting for it only
    // stops when the caller's timeout is exceeded
    Assertions.assertThrows(
        DeepLException.class,
        () ->
            client.waitUntilTranslationMemoryJobDone(
                translationMemoryImport.getJobId(), Duration.ofSeconds(1)));
  }

  @Test
  void testWaitUntilTranslationMemoryJobDonePollsThroughAwaitingInput() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    // The job reports its non-terminal status once before completing
    DeepLClient client =
        createDeepLClient(new SessionOptions().setTranslationMemoryJobProcessingPolls(1));
    File tmxFile = createTmxFile();
    byte[] fileContent = Files.readAllBytes(tmxFile.toPath());

    TranslationMemoryImport translationMemoryImport =
        client.createTranslationMemoryImport(
            tmxFile.getName(), fileContent.length, null, "Awaiting Input TM");
    client.uploadTranslationMemoryFile(translationMemoryImport, fileContent);

    // An uploaded import keeps reporting AwaitingInput for a while, because the API detects
    // the upload asynchronously. Waiting must poll through that status instead of throwing.
    long startTimeMillis = System.currentTimeMillis();
    TranslationMemoryJob job =
        client.waitUntilTranslationMemoryJobDone(
            translationMemoryImport.getJobId(), Duration.ofSeconds(60));
    long elapsedMillis = System.currentTimeMillis() - startTimeMillis;

    Assertions.assertEquals(TranslationMemoryJobResult.Status.Completed, job.getStatus());
    // The job was polled at least twice, so the first AwaitingInput response was polled through
    Assertions.assertTrue(elapsedMillis >= 5000);
    Assertions.assertNotNull(job.getResult().getTranslationMemoryId());

    client.deleteTranslationMemory(job.getResult().getTranslationMemoryId());
  }

  @Test
  void testCreateTranslationMemoryImportWithInvalidFile() {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.createTranslationMemoryImport("", 100, null, null));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> client.createTranslationMemoryImport("example.tmx", 0, null, null));
  }

  @Test
  void testExportTranslationMemoryToFilepath() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    String translationMemoryId = importTranslationMemory(client);
    File outputFile = new File(tempDir + "/exported.tmx");

    TranslationMemoryJob job =
        client.exportTranslationMemoryToFilepath(translationMemoryId, outputFile);

    Assertions.assertEquals(TranslationMemoryJob.Operation.Export, job.getOperation());
    Assertions.assertEquals(TranslationMemoryJobResult.Status.Completed, job.getStatus());
    Assertions.assertTrue(readFromFile(outputFile).contains("<tmx"));

    client.deleteTranslationMemory(translationMemoryId);
  }

  @Test
  void testCreateTranslationMemoryExportReusesCompletedJob() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    String translationMemoryId = importTranslationMemory(client);

    TranslationMemoryExport created = client.createTranslationMemoryExport(translationMemoryId);
    Assertions.assertFalse(created.isReusedExisting());
    Assertions.assertEquals(translationMemoryId, created.getTranslationMemoryId());
    client.waitUntilTranslationMemoryJobDone(created.getJobId());

    TranslationMemoryExport reused = client.createTranslationMemoryExport(translationMemoryId);
    Assertions.assertTrue(reused.isReusedExisting());
    Assertions.assertEquals(created.getJobId(), reused.getJobId());

    client.deleteTranslationMemory(translationMemoryId);
  }

  @Test
  void testGetTranslationMemoryJobWithUnknownId() {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();

    Assertions.assertThrows(
        NotFoundException.class, () -> client.getTranslationMemoryJob(UNKNOWN_ID));
  }

  @Test
  void testDeleteTranslationMemory() throws Exception {
    Assumptions.assumeTrue(isMockServer);
    DeepLClient client = createDeepLClient();
    String translationMemoryId = importTranslationMemory(client);

    client.deleteTranslationMemory(translationMemoryId);

    Assertions.assertThrows(
        NotFoundException.class, () -> client.getTranslationMemory(translationMemoryId));
  }

  /** Imports the example TMX file and returns the ID of the resulting translation memory. */
  private String importTranslationMemory(DeepLClient client) throws Exception {
    TranslationMemoryJob job = client.importTranslationMemoryFromFilepath(createTmxFile());
    return job.getResult().getTranslationMemoryId();
  }

  /** Writes the example TMX file into the temporary directory of this test. */
  private File createTmxFile() throws Exception {
    File tmxFile = new File(tempDir + "/example.tmx");
    boolean ignored = tmxFile.delete();
    writeToFile(tmxFile, EXAMPLE_TMX);
    return tmxFile;
  }
}
