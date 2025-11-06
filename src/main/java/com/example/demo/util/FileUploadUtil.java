package com.example.demo.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 檔案上傳工具類別
 * 
 * 功能說明：
 * 1. 處理檔案上傳
 * 2. 驗證檔案類型
 * 3. 產生唯一檔名
 * 4. 儲存檔案到指定目錄
 */
public class FileUploadUtil {
    
    //允許的圖片格式
    private static final String[] ALLOWED_EXTENSIONS = {
        "jpg", "jpeg", "png", "gif", "webp", "bmp"
    };
    
    //最大檔案大小（5MB）
      private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    /**
     * 儲存上傳的圖片檔案
     * 
     * @param file 上傳的檔案
     * @param uploadPath 上傳目錄的絕對路徑
     * @return 儲存後的檔名
     * @throws IOException 檔案操作失敗
     * @throws IllegalArgumentException 檔案驗證失敗
     */
    public static String saveFile(MultipartFile file, String uploadPath) throws IOException {
        System.out.println("========== 開始處理檔案上傳 ==========");
        
        // 驗證檔案是否為空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("檔案不能為空");
        }
        
        System.out.println("✓ 原始檔名：" + file.getOriginalFilename());
        System.out.println("✓ 檔案大小：" + file.getSize() + " bytes");
        System.out.println("✓ 內容類型：" + file.getContentType());
        
        // 驗證檔案大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("檔案大小超過限制（最大 5MB）");
        }
        
        // 取得原始檔名和副檔名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("無法取得檔案名稱");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        
        // 驗證檔案類型
        if (!isAllowedExtension(fileExtension)) {
            throw new IllegalArgumentException(
                "不支援的檔案格式。允許的格式：jpg, jpeg, png, gif, webp, bmp"
            );
        }
        
        System.out.println("✓ 副檔名驗證通過：" + fileExtension);
        
        // 產生唯一檔名
        String newFilename = generateUniqueFilename(originalFilename);
        System.out.println("✓ 新檔名：" + newFilename);
        
        // 建立上傳目錄（如果不存在）
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                System.out.println("✓ 建立上傳目錄：" + uploadPath);
            }
        }
        
        // 儲存檔案
        Path filePath = Paths.get(uploadPath, newFilename);
        Files.write(filePath, file.getBytes());
        
        System.out.println("✓ 檔案儲存成功：" + filePath);
        System.out.println("====================================");
        
        return newFilename;
    }
    
    /**
     * 刪除舊的圖片檔案
     * 
     * @param filename 要刪除的檔名
     * @param uploadPath 檔案所在目錄
     */
    public static void deleteFile(String filename, String uploadPath) {
        if (filename == null || filename.isEmpty()) {
            return;
        }
        
        try {
            Path filePath = Paths.get(uploadPath, filename);
            Files.deleteIfExists(filePath);
            System.out.println("✓ 已刪除舊檔案：" + filename);
        } catch (IOException e) {
            System.err.println("❌ 刪除檔案失敗：" + e.getMessage());
        }
    }
    
    //取得檔案副檔名

    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    //檢查副檔名是否在允許的清單中

    private static boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    //產生唯一的檔名
    private static String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        long timestamp = System.currentTimeMillis();
        
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        baseName = baseName.replaceAll("[^a-zA-Z0-9]", "_");
        
        if (baseName.length() > 20) {
            baseName = baseName.substring(0, 20);
        }
        
        return uuid + "_" + timestamp + "_" + baseName + "." + extension;
    }
    
    //驗證檔案是否為圖片
    public static boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}