package com.agendadigital.core.shared.infrastructure.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.agendadigital.core.modules.messages.domain.MessageEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.RequiresApi;

public class FilesUtils {

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

    public static String saveFileFromUri(Context context, Uri uri, String fileName, String pathToSave) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File file = new File(pathToSave, fileName);
        if (!file.exists()) {
            FileOutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
            inputStream.close();
            out.close();
        }
        return pathToSave + fileName;
    }

    public static File bitmapToFile(Context context, Bitmap bitmap, String filename) throws IOException {
        File file = new File(context.getCacheDir(), filename.concat(".jpeg"));
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

    public static File createImageTempFile(Context context) throws IOException {
            // Create an image file name
            String timeStamp = DateFormatter.formatToDate(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            return image;
    }

    public static boolean validateExtension(String extension, MessageEntity.MessageType messageType) {
        boolean result;
        switch (messageType) {
            case Image:
                result = extension.equalsIgnoreCase(".jpeg") || extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".png");
                break;
            case Video:
                result = extension.equalsIgnoreCase(".mp4") || extension.equalsIgnoreCase(".mpg");
                break;
            case Document:
                result = extension.equalsIgnoreCase(".txt")
                        || extension.equalsIgnoreCase(".doc")
                        || extension.equalsIgnoreCase(".docx")
                        || extension.equalsIgnoreCase(".xls")
                        || extension.equalsIgnoreCase(".xlsx")
                        || extension.equalsIgnoreCase(".ppt")
                        || extension.equalsIgnoreCase(".pptx")
                        || extension.equalsIgnoreCase(".pdf");
                break;
            default:
                result = true;
        }
        return result;
    }
}
