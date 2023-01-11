// Copyright 2023 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.deepltestapp;

import com.deepl.api.*;
import com.deepl.deepltestapp.*;
import com.deepl.deepltestapp.annotation.IntegrationTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert.*;
import org.junit.experimental.categories.Category;

/**
 * Internal DeepL Integration Test
 */
@Category(IntegrationTest.class)
public class DeepLIntegrationTest extends TestCase {
    public DeepLIntegrationTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(DeepLIntegrationTest.class);
    }

    /**
     * Runs the hello world example. Requires a DeepL auth key via the DEEPL_AUTH_KEY
     * environment variable.
     */
    public void testApp() throws InterruptedException, DeepLException {
        String result = App.translateHelloWorld();
        String[] wordsToCheck = {"Hello", "World"};
        for (String wordToCheck : wordsToCheck) {
            assertFalse(String.format("Expected translation to no longer contain the english %s, received %s", 
                wordToCheck, result), result.contains(wordToCheck)
            );
        }
    }
}
