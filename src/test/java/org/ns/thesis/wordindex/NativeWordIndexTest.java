package org.ns.thesis.wordindex;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NativeWordIndexTest {

    public static final String TEST_FILE = "src/test/resources/bible.txt";
    private final String searchWord = "god";
    private final int occurrences = 4472;

    @Test
    void testGetWords() throws Exception {
        final WordIndex.ContextBytes ctx = WordIndex.ContextBytes.SMALL_CONTEXT;

        try (WordIndex index = new NativeWordIndex(TEST_FILE,
                1 << 8,
                8192, 4096, true)) {
            dumpToFile(index, ctx);

            long count = index.getWords(searchWord, ctx)
                    .size();
            assertEquals(occurrences, count);
        }
    }

    @Test
    void testIterateWords() throws Exception {
        final WordIndex.ContextBytes ctx = WordIndex.ContextBytes.SMALL_CONTEXT;

        try (WordIndex index = new NativeWordIndex(TEST_FILE,
                1 << 8,
                8192, 4096, true)) {
            try (WordContextIterator iterator =
                         index.iterateWords(searchWord, ctx)) {
                long count = iterator.stream().count();
                assertEquals(89440, count);
            }
        }
    }

    private static void dumpToFile(WordIndex index, WordIndex.ContextBytes ctx)
            throws IOException {
        File f = new File("build/results-native");
        try (var writer = new FileWriter(f)) {
            index.iterateWords("god", ctx).stream()
                    .map(str -> str.replaceAll("\n", " "))
                    .forEach(str -> {
                        try {
                            writer.write(str);
                            writer.write("\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}