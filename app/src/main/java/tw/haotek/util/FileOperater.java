package tw.haotek.util;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Neo on 2016/1/20 0020.
 */
public class FileOperater {
    //  http://handd.blog.51cto.com/2796632/1173823
    /*
    *
    *
    *
    * File file = new File(".\\test.txt");
System.out.println(file.getPath());
System.out.println(file.getAbsolutePath());
System.out.println(file.getCanonicalPath());
.\test.txt
E:\workspace\Test\.\test.txt
E:\workspace\Test\test.txt
    * */
    private static final String TAG = FileOperater.class.getSimpleName();
    public static ArrayList<File> mDirList = new ArrayList<File>();

    public static ArrayList<File> getDirectory(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    mDirList.add(listFile[i]);
                    getDirectory(listFile[i]);
                }
            }
        }
        return mDirList;
    }

    public static ArrayList<String> getFilePathList(File dir) {
        final ArrayList<String> filelist = new ArrayList<String>();
        final File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getFilePathList(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(".MP4")) {
                        filelist.add(listFile[i].getPath());
                    }
                }
            }
        }
        return filelist;
    }

    public static ArrayList<String> getFileAbsolutePathList(File dir) {
        ArrayList<String> filelist = null;
        final File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getFileAbsolutePathList(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(".MP4")) {
                        if (filelist == null) {
                            filelist = new ArrayList<String>();
                        }
                        filelist.add(listFile[i].getAbsolutePath());
                        Log.d(TAG, "Show FileAbsolutePath  " + listFile[i].getAbsolutePath());
                    }
                }
            }
        }
        if (filelist != null) {
            Log.d(TAG, "Show Size   " + filelist.size());
        }
        return filelist;
    }

    public static ArrayList<String> getFileCanonicalPathList(File dir) {
        final ArrayList<String> filelist = new ArrayList<String>();
        final File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getFileCanonicalPathList(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(".MP4")) {
                        try {
                            filelist.add(listFile[i].getCanonicalPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return filelist;
    }

    public static ArrayList<String> getFileList(File dir) {
        final ArrayList<String> filelist = new ArrayList<String>();
        final File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getFilePathList(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(".MP4")) {
                        filelist.add(listFile[i].getName());
                    }
                }
            }
        }
        return filelist;
    }

//    public static ArrayList<File> getfile(File dir) { //FIXME test
//        final ArrayList<File> fileList = new ArrayList<File>();
//        File listFile[] = dir.listFiles();
//        if (listFile != null && listFile.length > 0) {
//            for (int i = 0; i < listFile.length; i++) {
//                if (listFile[i].isDirectory()) {
//                    fileList.add(listFile[i]);
//                    getfile(listFile[i]);
//                } else {
//                    if (listFile[i].getName().endsWith(".MP4")) {
//                        fileList.add(listFile[i]);
//                    }
//                }
//
//            }
//        }
//        return fileList;
//    }
}
