package Utils;

import android.content.Context;
import android.os.Environment;

import org.json.JSONException;

import java.io.File;

import saves.MainSaver;

/**
 * Created by yossibarel on 12/06/16.
 */
public class FileManager {
    private static final boolean IS_EXTERNAL = true;
    private static File CHANNEL_SAVE_DIR;
    private static File SAVE_DIR;

    public static void saveFile(String fileName, TypeFile typeFile, MainSaver.SaveListener listener) throws JSONException {

        switch (typeFile) {
            case CHANNEL:
                String filePath = CHANNEL_SAVE_DIR + "/" + fileName + ".ch";
                MainSaver.save(filePath, listener);
                break;
        }

    }

    public static void loadFile(File file, MainSaver.LoadListener listener) {
        MainSaver.load(file.getPath(), listener);
    }

    public enum TypeFile {
        CHANNEL
    }

    public static File getSaveDir() {
        return SAVE_DIR;
    }

    public static File getChannelSaveDir() {
        return CHANNEL_SAVE_DIR;
    }

    public static void create(Context context) {
        if (IS_EXTERNAL) {
            SAVE_DIR = new File(Environment.getExternalStorageDirectory() + "/Save");
        } else
            SAVE_DIR = new File(context.getFilesDir() + "/Save");
        if (!SAVE_DIR.exists())
            SAVE_DIR.mkdir();
        CHANNEL_SAVE_DIR = new File(SAVE_DIR + "/channel");
        if (!CHANNEL_SAVE_DIR.exists())
            CHANNEL_SAVE_DIR.mkdir();
    }


    public static boolean isFileNameExist(String fileName, TypeFile typeFile) {
        boolean retVal = false;
        switch (typeFile) {
            case CHANNEL:
                for (int i = 0; !retVal && i < CHANNEL_SAVE_DIR.listFiles().length; i++) {
                    if (fileName.equals(CHANNEL_SAVE_DIR.listFiles()[i].getName()))
                        retVal = true;
                }
                break;
        }
        return retVal;
    }
}
