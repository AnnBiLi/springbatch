package org.example.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class OcrService {

    private final ITesseract tesseract;

    public OcrService() {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("D:\\software\\tessercatOCR\\tessdata");

    }

    public String extractTextFromImage(File imageFile){
        try {
            tesseract.setLanguage("eng");
            return tesseract.doOCR(imageFile);
        }catch (Exception e){
            throw new RuntimeException("Error extracting text from image",e);
        }
    }


    public String tessdata(MultipartFile file,String language){
        try {
            // 转换
            InputStream sbs = new ByteArrayInputStream(file.getBytes());
            BufferedImage bufferedImage = ImageIO.read(sbs);
            // 设置为中文简体
            tesseract.setLanguage(language);

            // 对图片进行文字识别
            return tesseract.doOCR(bufferedImage);
        }catch (Exception e){
            throw new RuntimeException("Error extracting text from image",e);
        }

    }


    public String bigImage(MultipartFile file,String language)   {
        // 读取图像
        try {
            InputStream sbs = new ByteArrayInputStream(file.getBytes());
            BufferedImage originalImage = ImageIO.read(sbs);

            // 设置新的尺寸
            int newWidth = 600; // 调整为所需的宽度
            int newHeight = 800; // 调整为所需的高度

            // 创建调整大小后的图像
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
            resizedImage.getGraphics().drawImage(originalImage.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH), 0, 0, null);

            // 保存调整大小后的图像
            File outputFile = new File("D:\\path\\output.png");
            ImageIO.write(resizedImage, "png", outputFile);

            tesseract.setLanguage(language);
            return tesseract.doOCR(outputFile);
        }catch (Exception e){
            throw new RuntimeException("Error extracting text from image",e);

        }


    }


}
