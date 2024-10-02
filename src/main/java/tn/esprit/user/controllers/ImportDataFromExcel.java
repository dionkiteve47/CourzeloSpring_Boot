package tn.esprit.user.controllers;

import tn.esprit.user.services.Implementations.LoadFromExcelToDB;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Slf4j
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/data")
@AllArgsConstructor
public class ImportDataFromExcel {
    private LoadFromExcelToDB loadFromExcelToDB;
    @PostMapping("/import")
    public Boolean importData(@RequestParam("file") MultipartFile file) {
        try {
            File convertedFile = convertMultipartFileToFile(file);
            return loadFromExcelToDB.PutDataToDb(convertedFile.getPath());
        } catch (IOException e) {
            return false;
        }
    }
    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File file = new File(tempDir + "/" + Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (var inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return file;
    }
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadGeneratedExcelFile() throws IOException {
        return loadFromExcelToDB.downloadGeneratedExcelFile();
    }
}
