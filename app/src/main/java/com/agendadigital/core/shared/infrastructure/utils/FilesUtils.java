package com.agendadigital.core.shared.infrastructure.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import androidx.annotation.RequiresApi;

public class FilesUtils {

    public static String copyFile(String from, String fileName, String pathToSave) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                int end = from.toString().lastIndexOf("/");
                String str1 = from.toString().substring(0, end);
                String str2 = from.toString().substring(end+1, from.length());
                File source = new File(str1, str2);
                File destination= new File(pathToSave, str2);
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            return pathToSave + fileName + ".jpg";
        } catch (Exception e) {
            return "";
        }
    }

    public static String saveImageJPEG(Context context, File file, String fileName, String pathToSave) throws IOException {
        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file));
        FileOutputStream out = new FileOutputStream(new File(pathToSave, fileName + ".jpg"));
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
        return pathToSave + fileName + ".jpg";
    }

    public static String saveDocument(Context context, File file, String fileName, String pathToSave) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(pathToSave, fileName), true);
        byte[] buf = new byte[5 * 1024];
        int len;

        while ((len = inputStream.read(buf)) > 0) {
            fileOutputStream.write(buf, 0, len);
        }

        fileOutputStream.flush();
        inputStream.close();
        fileOutputStream.close();
        return pathToSave + fileName;
    }

    public static String saveDocumentFromUri(Context context, Uri uri, String fileName, String pathToSave) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(pathToSave, fileName), true);
        byte[] buf = new byte[5 * 1024];
        int len;

        while ((len = inputStream.read(buf)) > 0) {
            fileOutputStream.write(buf, 0, len);
        }

        fileOutputStream.flush();
        inputStream.close();
        fileOutputStream.close();
        return pathToSave + fileName;
    }

    public static String saveVideoMP4FromFile(Context context, File file, String fileName, String pathToSave) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        FileOutputStream out = new FileOutputStream(new File(pathToSave, fileName));
        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.flush();
        inputStream.close();
        out.close();
        return pathToSave + fileName;
    }

    public static String saveVideoMP4FromUri(Context context, Uri uri, String fileName, String pathToSave) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        FileOutputStream out = new FileOutputStream(new File(pathToSave, fileName + ".mp4"));
        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.flush();
        inputStream.close();
        out.close();
        return pathToSave + fileName + ".mp4";
    }

    public static File bitmapToFile(Context context, Bitmap bitmap, String filename) throws IOException {
        File file = new File(context.getCacheDir(), filename);
        if (file.createNewFile()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }
        return file;
    }
}
