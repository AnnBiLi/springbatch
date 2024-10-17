package org.example.controller;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.example.service.OcrService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/extract-text")
    public String extractText(@RequestParam("file") MultipartFile file) {
        try {
            // 将 MultipartFile 保存为临时文件
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            // 使用 OCR 服务提取文本
            String extractedText = ocrService.extractTextFromImage(tempFile);

            // 删除临时文件
            tempFile.delete();

            return extractedText;
        } catch (IOException e) {
            throw new RuntimeException("File processing failed", e);
        }
    }

    @PostMapping("/tessdata")
    public String tessdata(@RequestParam("file") MultipartFile file,@RequestParam("language") String language) throws TesseractException, IOException {

//        String dosoc = ocrService.tessdata(file,language);
//        String dosoc = ocrService.dosoc(file);

        String dosoc = ocrService.bigImage(file, language);

        return dosoc;
    }


}
