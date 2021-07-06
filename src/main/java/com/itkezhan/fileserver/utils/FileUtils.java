package com.itkezhan.fileserver.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 文件相关工具类：
 */
public class FileUtils {
    // 文件名正则校验
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 保存文件
     *
     * @param file
     * @param filePath
     */
    public static void saveFile(MultipartFile file, String filePath) throws IOException {
        File desc = new File(filePath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        file.transferTo(desc);
    }

    /**
     * 文件名校验
     *
     * @param fileName
     * @return
     */
    public static boolean isValidName(String fileName) {
        return fileName.matches(FILENAME_PATTERN);
    }

    /**
     * 将文件转换为文件流
     *
     * @param filePath
     * @param os
     */
    public static void transToStream(String filePath, OutputStream os) {
        FileInputStream fi = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            fi = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fi.read(bytes)) > 0) {
                os.write(bytes, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fi!=null){
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
