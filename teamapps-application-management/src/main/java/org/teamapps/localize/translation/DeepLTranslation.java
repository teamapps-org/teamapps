/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.localize.translation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class DeepLTranslation implements TranslationService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DeepLTranslation.class);
    public static final Set<String> SUPPORTED_LANGUAGES = new HashSet<>(Arrays.asList("en", "de", "fr", "es", "pt", "nl", "it", "pl", "ru", "ja", "zh")) ;

    private String authKey;
    private long translatedCharacters = 0;
    private final ObjectMapper mapper;

    public DeepLTranslation(String authKey) {

        this.authKey = authKey;

        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Set<String> getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    @Override
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        try {
            URL url = new URL("https://api.deepl.com/v2/translate");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            StringJoiner joiner = new StringJoiner("&");
            joiner.add(URLEncoder.encode("auth_key", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(authKey, StandardCharsets.UTF_8));
            joiner.add(URLEncoder.encode("text", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(text, StandardCharsets.UTF_8));
            joiner.add(URLEncoder.encode("source_lang", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(sourceLanguage, StandardCharsets.UTF_8));
            joiner.add(URLEncoder.encode("target_lang", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(targetLanguage, StandardCharsets.UTF_8));
            //joiner.add(URLEncoder.encode("split_sentences", StandardCharsets.UTF_8) + "=0");
            joiner.add(URLEncoder.encode("split_sentences", StandardCharsets.UTF_8) + "=nonewlines");
            joiner.add(URLEncoder.encode("tag_handling", StandardCharsets.UTF_8) + "=xml");


            byte[] out = joiner.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
            String json;
            int responseCode = http.getResponseCode();
            String responseMessage = http.getResponseMessage();

            if (responseCode != 200) {
                LOGGER.warn("DeepL cannot translate from " + sourceLanguage + " to " + targetLanguage + ", result:" + responseCode + ":" + responseMessage + ", text:" + text);
                return null;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8))) {
                json =  br.lines().collect(Collectors.joining(System.lineSeparator()));
            }

            JsonNode rootNode = mapper.readTree(json);
            JsonNode node = rootNode.elements().next();
            String translation =  node.findValue("text").asText();
            if (translation != null) {
                translatedCharacters += translation.length();
            }
            return translation;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getTranslatedCharacters() {
        return translatedCharacters;
    }

}
