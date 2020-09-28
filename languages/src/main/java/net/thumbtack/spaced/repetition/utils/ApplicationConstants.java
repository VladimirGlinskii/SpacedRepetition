package net.thumbtack.spaced.repetition.utils;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Set;

public class ApplicationConstants {

    public static final TypeReference<Set<String>> SET_TYPE = new TypeReference<Set<String>>() {
    };

    public static final String PRONUNCIATION_BASIC_URL =
            "https://www.oxfordlearnersdictionaries.com/definition/english/";

    public static final String MP3_ATTRIBUTE = "data-src-mp3";
}
