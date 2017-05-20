package com.compiler.controller;


import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.compiler.compiler.CompilerComponent;
import com.compiler.model.AbstractSyntaxTree;
import com.compiler.parser.ParserComponent;
import com.compiler.scaner.ScannerComponent;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
public class FileController {

    @RequestMapping("/get_file")
    public ResponseEntity getFileContent() {
        try {
            String fileContent = this.getFileData();
            return ResponseEntity.status(HttpStatus.OK).body(fileContent);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/compile", method=RequestMethod.POST)
    public @ResponseBody ResponseEntity compileFile(@RequestBody String newFileData) {

        newFileData = newFileData.replaceAll("(\r\n)", "\n");
        String oldFileData = "";
        try {
            oldFileData = this.getFileData();
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        try {
            this.writeFileData(newFileData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        ScannerComponent scanner = new ScannerComponent("public/file.txt");
        ParserComponent parser = new ParserComponent(scanner);

        parser.parse();
        scanner.closeFile();

        try {
            this.writeFileData(oldFileData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        if (parser.getErrorMessages().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(parser.getTree());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(parser.getErrorMessages());
        }
    }

    private void writeFileData(String fileData) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL fileUri = classLoader.getResource("public/file.txt");

        assert fileUri != null;

        File file = new File(fileUri.getPath());
        FileWriter fooWriter = new FileWriter(file, false);

        fooWriter.write(fileData);
        fooWriter.close();
    }

    private String getFileData() throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL fileUri = classLoader.getResource("public/file.txt");

        assert fileUri != null;
        FileInputStream fileInputStream = new FileInputStream(fileUri.getPath());
        return readStream(fileInputStream);
    }

    private static String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder(512);
        try {
            Reader r = new InputStreamReader(is, "UTF-8");
            int c = 0;
            while ((c = r.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }
}