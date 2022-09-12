package com.nft.platform.mapper;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class Base64ToMultipartFileMapper implements MultipartFile {

    private final byte[] image;
    private final String fileName;

    @Override
    public String getName() {
        return "picture";
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return MediaType.MULTIPART_FORM_DATA_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return image.length;
    }

    @Override
    public byte[] getBytes() {
        return image;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(image);
    }

    @Override
    public void transferTo(@NonNull File file) throws IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(image);
        } catch(IOException ex) {
            log.error("Exception occurred while zipping file",ex);
        }
    }

    public static MultipartFile compressUploadedImage(@NonNull MultipartFile initialMultipartFile) {

        String imageName = initialMultipartFile.getOriginalFilename();
        String imageExtension = null;

        if (imageName != null) {
            imageExtension = Objects.requireNonNull(initialMultipartFile.getContentType()).split("/")[1];
        }

        ImageWriter imageWriter = null;

        if (imageExtension != null) {
            imageWriter = ImageIO.getImageWritersByFormatName(imageExtension).next();
        }

        ImageWriteParam imageWriteParam = null;

        if (imageWriter != null) {
            imageWriteParam = imageWriter.getDefaultWriteParam();
        }

        if (imageWriteParam != null) {
            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            configureCompressionQuality(imageWriteParam, initialMultipartFile.getSize());
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(byteArrayOutputStream);

        if (imageWriter != null) {
            imageWriter.setOutput(imageOutputStream);
        }

        BufferedImage originalImage;

        try (InputStream inputStream = initialMultipartFile.getInputStream()) {
            originalImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            String info = String.format("CompressImage - bufferedImage (file %s)- IOException - message: %s ", imageName, e.getMessage());
            log.error(info);
            return new Base64ToMultipartFileMapper(byteArrayOutputStream.toByteArray(), imageName);
        }

        IIOImage image = new IIOImage(originalImage, null, null);

        try {
            if (imageWriter != null) {
                imageWriter.write(null, image, imageWriteParam);
            }
        } catch (IOException e) {
            String info = String.format("CompressImage - imageWriter (file %s)- IOException - message: %s ", imageName, e.getMessage());
            log.error(info);
        } finally {
            if (imageWriter != null) {
                imageWriter.dispose();
            }
        }

        return new Base64ToMultipartFileMapper(byteArrayOutputStream.toByteArray(), imageName);

    }

    private static void configureCompressionQuality(ImageWriteParam imageWriteParam, long uploadedFileSize) {
        if (uploadedFileSize > 4000000 && uploadedFileSize < 5000000) {
            imageWriteParam.setCompressionQuality(0.2f);
        } else if (uploadedFileSize > 3000000 && uploadedFileSize < 4000000) {
            imageWriteParam.setCompressionQuality(0.85f);
        } else if (uploadedFileSize > 2000000 && uploadedFileSize < 3000000) {
            imageWriteParam.setCompressionQuality(0.01f);
        } else if (uploadedFileSize > 1000000 && uploadedFileSize < 2000000){
            imageWriteParam.setCompressionQuality(0.050f);
        }
    }

}