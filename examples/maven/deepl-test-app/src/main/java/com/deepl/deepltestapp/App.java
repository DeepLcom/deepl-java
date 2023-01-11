// Copyright 2023 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.deepltestapp;
import com.deepl.api.*;
import java.lang.System.*;

/**
 * Hello world translation example
 *
 */
public class App {
    /**
     * Hello world example - insert your API key to test the library.
     */
    public static void main( String[] args ) throws InterruptedException, DeepLException {
        String authKey = "f63c02c5-f056-...";  // Replace with your key
        Translator translator = new Translator(authKey);
        TextResult result =
                translator.translateText("Hello, world!", null, "fr");
        System.out.println(result.getText()); // "Bonjour, le monde !"
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /// These methods are for a test using DeepLs CI pipeline, ignore.

    public static String getEnvironmentVariableValue(String envVar) {
        return System.getenv(envVar);
    }

    public static String getAuthKeyFromEnvironmentVariables() {
        return getEnvironmentVariableValue("DEEPL_AUTH_KEY");
    }

    public static String getServerUrlFromEnvironmentVariables() {
        return getEnvironmentVariableValue("DEEPL_SERVER_URL");
    }

     public static String translateHelloWorld() throws InterruptedException, DeepLException {
        Translator translator = new Translator(getAuthKeyFromEnvironmentVariables(), 
            (new TranslatorOptions()).setServerUrl(getServerUrlFromEnvironmentVariables()));
        TextResult result =
                translator.translateText("Hello, world!", null, "fr");
        String translatedText = result.getText();
        return translatedText;
    }
}
