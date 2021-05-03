package example.com.bus2.service;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;


/**
 * Created by Arkady Gorodischer on 01/07/19.
 *
 * logic is divided to 3 parts
 * 1) copy file from assets (if needed)
 * 2) read file and return it's string
 * 3) parse the file to json, fill the data
 */
public class SettingsManager {

    //data
    public static final int OK = 0, ERROR_COPY_SETTINGS = -1, ERROR_READ_SETTINGS = -2, ERROR_PARSING_SETTINGS = -3;

    private static final String folderPath = BleScanService.SETTINGS_FOLDER;
    private static final String fileName = "settings.json";

    private Context ctx;

    private TagsContainer bles = null;


    //constructor
    public SettingsManager(Context ctx) {
        this.ctx = ctx;
    }

    //functions

    /**
     * this function do:
     * 1) copy settings file to sd card if needed
     * 2) read the settings file from sd
     * 3) parse the settings file
     *
     * @return error code
     */


    public int init() {

        int errorCode = 0;
        String fileText;

        errorCode = makeSettingsFileAvailable();
        if (errorCode != 0) {
            //return can't make settings file error
            return ERROR_COPY_SETTINGS;
        }

        fileText = readSettingsFile();
        if (fileText == null) {
            //return can't read settings file error
            return ERROR_READ_SETTINGS;
        }

        errorCode = parseSettings(fileText);
        if (errorCode != 0) {
            //return can't parse settings file error
            return ERROR_PARSING_SETTINGS;
        }

        return OK;

    }


    ///////////// getters ////////////////

    public TagsContainer getBles(){
        if(bles == null) {
            bles = new TagsContainer();
        }
        return bles;
    }

    ///////////// private functions ///////////////////

    /**
     * this functions check if the setting file is exist. if not, it will copy it from assets
     *
     * @return error code
     */

    private int makeSettingsFileAvailable() {
        File file = new File(Environment.getExternalStorageDirectory() + folderPath, fileName);
        if (file.exists()) {
            return OK;
        } else {
            return copySettings();
        }
    }


    /**
     * this function should get text from settings file stored on disk
     **/
    private String readSettingsFile() {

        String result = null;

        try {
            File file = new File(Environment.getExternalStorageDirectory() + folderPath, fileName);
            FileInputStream stream = new FileInputStream(file);

            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            stream.close();

            result = Charset.defaultCharset().decode(bb).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * this function parse json text to data
     */

    private int parseSettings(String data) {
        try {

            JSONObject jsonObj = new JSONObject(data);
            //get Macs array
            bles = new TagsContainer(jsonObj);

        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }

        return OK;
    }



    /**
     * this function copy recordingSettings file from assets to sdcard
     *
     * @return error code
     */
    private int copySettings() {
        try {

            // Path to the just created empty db
            String outFileName = Environment.getExternalStorageDirectory() + folderPath + fileName;

            //Create a dir for the file
            File dir = new File(Environment.getExternalStorageDirectory() + folderPath);
            if (dir.isDirectory() == false && dir.mkdirs() == false) {
                return -1;
            }

            // Open your local db as the input stream
            InputStream myInput = ctx.getAssets().open("default_settings.json");

            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return OK;
    }


}
