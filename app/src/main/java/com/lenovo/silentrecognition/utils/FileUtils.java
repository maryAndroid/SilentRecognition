package com.lenovo.silentrecognition.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.lenovo.silentrecognition.common.GlobalConfig;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mali18 on 2016/9/7.
 */
public class FileUtils {


    public static void copyFile(String oldPath,String newPath){
        if(oldPath==null||newPath==null){
            return;
        }
        File oldFile = new File(oldPath);
        if(oldFile.exists()){
            File newFile = new File(newPath);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(oldFile);
                outputStream = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int len=0;
                while ((len = inputStream.read(buffer))!=-1){
                    outputStream.write(buffer,0,len);
                }
                outputStream.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                close(inputStream);
                close(outputStream);
            }
        }
    }

    public static void copyDbFile(Context context){
        String oldPath = context.getDatabasePath(GlobalConfig.DB_NAME).getAbsolutePath();
        String parentPath = "/mnt/sdcard/SilentRecognition/";
        File dir = new File(parentPath);
        if(dir!=null&&!dir.exists()){
            dir.mkdir();
        }

        String newPath = parentPath+GlobalConfig.DB_NAME;
        copyFile(oldPath,newPath);
    }

    public static void close(Closeable closeable){
        if(closeable==null){
            return;
        }
        try{
            closeable.close();
        }catch (Throwable e){
            Log.e(e.getMessage(),e+"");
        }
    }

    // （创建/获取）子目录
    public static String genAbsoluteFolderPath(String subDir) {
        String sdPath = getSDPath();
//        String  sdPath   =  "/storage/emulated/0";
        if (sdPath == null) {
            return null;
        } else {
            String folderPath = sdPath + "/SilentRecognition/" + subDir+"/";
            File destDir = new File(folderPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            return folderPath;
        }
    }
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }

    /**
     * 拷贝asset目录文件到SD卡
     * @param context
     * @param assetFileName
     * @param outFilePath
     * @throws IOException
     */
    public static void copyAssetToSD(Context context,String assetFileName,String outFilePath){
        if(!isFileExsist(outFilePath)) {
            InputStream myInput;
            OutputStream myOutput = null;
            try {
                myOutput = new FileOutputStream(outFilePath);
                myInput = context.getAssets().open(assetFileName);
                byte[] buffer = new byte[1024];
                int length = myInput.read(buffer);
                while (length > 0) {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }
                myOutput.flush();
                myInput.close();
                myOutput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isFileExsist(String filePAth){
        File file = new File(filePAth);
        if(file.exists()){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
}
