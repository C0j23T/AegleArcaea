package moe.hepta.arcaea.utils;

import java.io.*;

public class ImageBase64Utils {
    public static String bytes2Base64(byte[] bytes) {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
    }

    public static void base64ToImage(String base64, String path) {
        base64ToImage(base64, new File(path));
    }

    public static void base64ToImage(String base64, File path) {
        try {
            OutputStream outputStream = new FileOutputStream(path);
            base64ToImageOutput(base64, outputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void base64ToImageOutput(String base64, OutputStream out) {
        if (base64 == null) {
            return;
        }
        try {
            byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(base64);
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] < 0) bytes[i] += 256;
            }
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}