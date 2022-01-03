package com.agendadigital.core.shared.infrastructure.utils;

import android.os.Environment;
import android.util.Log;

import com.agendadigital.core.modules.messages.domain.MessageEntity;

import java.io.File;
import java.io.FileNotFoundException;

public class DirectoryManager {
    private static final String TAG = "DirectoryManager";
    private static final String MAIN_DIRECTORY = "AgendaAizama";
    private static final String IMAGES_DIRECTORY = MAIN_DIRECTORY + File.separator + "Imagenes";
    private static final String IMAGES_SENT_DIRECTORY = MAIN_DIRECTORY + File.separator + "Imagenes" + File.separator + "Enviadas";
    private static final String VIDEOS_DIRECTORY = MAIN_DIRECTORY + File.separator + "Videos";
    private static final String VIDEOS_SENT_DIRECTORY = MAIN_DIRECTORY + File.separator + "Videos" + File.separator + "Enviados";
    private static final String DOCUMENTS_DIRECTORY = MAIN_DIRECTORY + File.separator + "Documentos";
    private static final String DOCUMENTS_SENT_DIRECTORY = MAIN_DIRECTORY + File.separator + "Documentos" + File.separator + "Enviados";


    public static String getPathToSave(MessageEntity.MessageType messageType, boolean sent) {
        String path = Environment.getExternalStorageDirectory() + File.separator;
        switch (messageType) {
            case Image:
                if (sent) {
                    path += IMAGES_SENT_DIRECTORY + File.separator;
                }else {
                    path += IMAGES_DIRECTORY + File.separator;
                }
                break;
            case Video:
                if (sent) {
                    path += VIDEOS_SENT_DIRECTORY + File.separator;
                } else {
                    path += VIDEOS_DIRECTORY + File.separator;
                }
                break;
            case Document:
                if (sent) {
                    path += DOCUMENTS_SENT_DIRECTORY + File.separator;
                } else {
                    path += DOCUMENTS_DIRECTORY + File.separator;
                }
                break;
        }
        return path;
    }

    private static boolean createDirectory(String directoryPath) {
        boolean result = false;
        try {
            File root = new File(Environment.getExternalStorageDirectory(), directoryPath);
            if (!root.exists()) {
                result = root.mkdirs();
            } else {
                result = true;
            }
            Log.d(TAG, "createDirectory: " + root.getPath() + ":" + result);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean createDirectories() {
        return createDirectory(MAIN_DIRECTORY)
                && createDirectory(IMAGES_DIRECTORY)
                && createDirectory(IMAGES_SENT_DIRECTORY)
                && createDirectory(VIDEOS_DIRECTORY)
                && createDirectory(VIDEOS_SENT_DIRECTORY)
                && createDirectory(DOCUMENTS_DIRECTORY)
                && createDirectory(DOCUMENTS_SENT_DIRECTORY);
    }
}
