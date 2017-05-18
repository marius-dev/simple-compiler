package util;

import java.io.*;
import java.nio.charset.Charset;

public class CharacterHandler {

    public static Reader handleFile(File file, Charset encoding) throws IOException {
        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             Reader buffer = new BufferedReader(reader)) {

            return buffer;
        }
    }
}