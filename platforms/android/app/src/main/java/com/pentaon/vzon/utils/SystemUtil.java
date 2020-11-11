package com.pentaon.vzon.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pentaon.vzon.common.ApplicationContext;
import com.pentaon.vzon.common.Config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Pentaon on 14,5월,2018
 */
public class SystemUtil {

  private static String TAG = "SystemUtil";

  public static int IS_GOOGLE_PLAY_SERVICES_AVAILABLE = 0;
  public static String GOOGLE_PLAY_SERVICES_PACKAGE = GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE; // "com.google.android.gms";
  //    public static int GOOGLE_PLAY_SERVICES_VERSION_CODE = GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE;
  public static String ANDROID_OS_BUILD_VERSION_RELEASE = android.os.Build.VERSION.RELEASE;
  public static String ANDROID_OS_BUILD_MODEL = android.os.Build.MODEL;
  public static String DEVICE_SERIAL_NUMBER = "";
  public static String APK_PACKAGE_NAME = "";
  public static String APK_VERSION_NAME = "";
  public static int GOOGLE_PLAY_SERVICES_APP_VERSION_CODE = 0;
  public static String GOOGLE_PLAY_SERVICES_APP_VERSION_NAME = "";
  public static ArrayList<HashMap<String, Size>> mArrCameraSizeInfo = new ArrayList<>();


  public static String getVersion() {
    String version = "";
    try {
      final String packageName = ApplicationContext.getInstance().getPackageName();
      final PackageInfo i = ApplicationContext.getInstance().getPackageManager()
          .getPackageInfo(packageName, 0);
      version = i.versionName;
    } catch (Exception e) {
      Log.e(TAG, "##### getVersion() : " + e.toString());
    }
    if (Config.DEBUG) {
      Log.d(TAG, "##### getVersion() : version[" + version + "]");
    }
    return version;
  }

  /**
   * android.permission.READ_PHONE_STATE
   */
  public static String getUsimCode(Context context) {
    return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
        .getSimSerialNumber();
  }

  /**
   * USIM 존재유무 체크
   */
  public static String getUSIM() {
    final TelephonyManager tm = (TelephonyManager) ApplicationContext.getInstance()
        .getSystemService(Context.TELEPHONY_SERVICE);
    if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
      // 유심이 존재하는 경우
      return "1";
    } else {
      // 유심이 없는 경우
      return "0";
    }
  }


  /**
   * 전화번호 가져오기
   */
  public static String getPhoneNumber(Context context) {
    String phoneNumber = "";
    final TelephonyManager telManager = (TelephonyManager) ApplicationContext.getInstance()
        .getSystemService(Context.TELEPHONY_SERVICE);
    if (telManager != null) {
      phoneNumber = telManager.getLine1Number();
    }
    if (phoneNumber != null && phoneNumber.equalsIgnoreCase("") == false) {
      phoneNumber = phoneNumber.replace("+82", "0");
    } else {
      if (Config.WIFI_MODE) {
        String deviceId = getDeviceId(context);
        int hashCode = 0;

        if (deviceId != null && deviceId.equalsIgnoreCase("") == false) {
          hashCode = Math.abs(deviceId.hashCode());
        }
        phoneNumber = "0" + String.valueOf(hashCode);
      }
    }
    return phoneNumber;
  }

  public static void makeDirFromFullPath(String fullpath) {

    File file = new File(fullpath);
    String str = file.getParent();
    File dir = new File(str);
    if (dir.exists()) {
      return;
    }
    dir.mkdir();

  }

  public static void savePhoto(Bitmap bitmap, String filename) {
    File saveDir = new File(filename);
    // saveDir.mkdir();
    FileOutputStream fos = null;
    try {

      fos = new FileOutputStream(saveDir);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

    } catch (FileNotFoundException e) {

      Log.w(TAG, e);

    } finally {

      if (fos != null) {
        try {
          fos.flush();
          fos.close();

        } catch (IOException e) {

          // Do nothing

        }

      }


    }

  }


  public static int saveFile(byte[] a_sData, String a_sParentPath,
      String a_sFileName) {
    int nSaveState = 1; // FILE SAVE FAILED

    if (a_sData != null && a_sData.length > 0) {
      File oDatabFolder = new File(a_sParentPath);
      if (oDatabFolder != null) {
        if (oDatabFolder.exists() == false) {
          oDatabFolder.mkdirs();
        }

        if (oDatabFolder.exists() == true) {
          String sFile = a_sParentPath + a_sFileName;

          try {
            FileOutputStream oOutputStream = new FileOutputStream(
                sFile);
            oOutputStream.write(a_sData);
            oOutputStream.close();

            nSaveState = 2; // FILE SAVE SUCESS
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } else {
      nSaveState = 0; // FILE SAVE NOTDATA
    }

    return nSaveState;
  }

  public static byte[] readFile(String a_sParentPath, String a_sFileName) {
    byte[] bArData = null;
    if (a_sParentPath != null && a_sParentPath.length() > 0) {
      File oDatabFolder = new File(a_sParentPath);
      if (oDatabFolder != null && oDatabFolder.exists() == true
          && oDatabFolder.isDirectory() == true) {
        String sFile = a_sParentPath + a_sFileName;

        try {
          FileInputStream oInputStream = new FileInputStream(sFile);
          int nCount = oInputStream.available();
          if (nCount > 0) {
            bArData = new byte[nCount];
            oInputStream.read(bArData);
          }

          if (oInputStream != null) {
            oInputStream.close();
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return bArData;
  }


  public static byte[] readFile(String a_sFileName) {
    byte[] bArData = null;
    String sFile = a_sFileName;

    try {
      FileInputStream oInputStream = new FileInputStream(sFile);
      int nCount = oInputStream.available();
      if (nCount > 0) {
        bArData = new byte[nCount];
        oInputStream.read(bArData);
      }

      if (oInputStream != null) {
        oInputStream.close();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bArData;
  }

  /**
   * public static ArrayList<String> unZip(String path, String fileName) throws IOException,
   * ArchiveException { File zipFile = new File(path, fileName);
   *
   * InputStream is = new FileInputStream(zipFile); ArchiveInputStream in = null; try { in = new
   * ArchiveStreamFactory().createArchiveInputStream("zip", is); } catch (ArchiveException e) {
   * e.printStackTrace(); }
   *
   * ZipArchiveEntry entry = null;
   *
   * ArrayList<String> entryFiles = new ArrayList<String>();
   *
   * while ((entry = (ZipArchiveEntry) in.getNextEntry()) != null) {
   *
   * File outfile = new File(path, entry.getName()); outfile.getParentFile().mkdirs(); OutputStream
   * out = new FileOutputStream(outfile); IOUtils.copy(in, out); entryFiles.add(entry.getName());
   * out.close();
   *
   * }
   *
   * in.close();
   *
   * return entryFiles; }
   *
   * public static ArrayList<String> unZip2(String path, String fileName) throws IOException,
   * ArchiveException { File zipFile = new File(path, fileName);
   *
   * InputStream is = new FileInputStream(zipFile); ArchiveInputStream in = null; try { in = new
   * ArchiveStreamFactory().createArchiveInputStream("zip", is); } catch (ArchiveException e) {
   * e.printStackTrace(); }
   *
   * ZipArchiveEntry entry = null;
   *
   * ArrayList<String> entryFiles = new ArrayList<String>(); String entryName = null; while ((entry
   * = (ZipArchiveEntry) in.getNextEntry()) != null) { entryName = entry.getName();
   *
   * File outfile = new File(path, entryName); outfile.getParentFile().mkdirs(); OutputStream out =
   * new FileOutputStream(outfile); IOUtils.copy(in, out); entryFiles.add(entryName); out.close();
   * }
   *
   * in.close();
   *
   * return entryFiles; }
   **/
  public static void zipWrite(List<String> inFile, String inPathe, String outFile, String outPath,
      boolean isDel) {
    try {
      File f = new File(outPath);

      if (!f.exists()) {
        f.mkdirs();
      }

      final String outFilename = outPath + outFile;

      // Create the ZIP file
      final ZipOutputStream zos = new ZipOutputStream(
          new FileOutputStream(outFilename));

      byte[] buf = new byte[1024];

      for (String filename : inFile) {
        final String infiles = inPathe + filename;
        File iFile = new File(infiles);

        if (Config.DEBUG) {
          Log.d(TAG, "IN PDF FILE [" + infiles + "]");
          Log.d(TAG, "iFile[" + iFile + "] IN file [" + filename + "]");
        }
        FileInputStream fis = new FileInputStream(iFile); // 파일입력
        zos.putNextEntry(new ZipEntry(filename)); // 파일만 (경로제거)
        int len;
        while ((len = fis.read(buf)) > 0) {
          zos.write(buf, 0, len);
        }
        zos.closeEntry();
        fis.close();
      }
      zos.flush();
      zos.close();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {

    }
  }

  public static boolean deleteFile(String path, String fileName) {
    return deleteFile(path + fileName);
  }

  public static boolean deleteFile(String fileName) {
    if (fileName == null) {
      return false;
    }
    return deleteFile(new File(fileName));
  }

  public static boolean deleteJpgFile(String fileName) {
    return deleteJpgFile(new File(fileName));
  }

  public static boolean deleteAllFile(String fileName) {
    return deleteAllFile(new File(fileName));
  }

  /**
   * 폴더에 있는 모든파일 삭제
   */
  public static boolean deleteFile(File fileOrDirectory) {
    if (fileOrDirectory.isDirectory()) {
      for (File child : fileOrDirectory.listFiles()) {
        deleteFile(child);
      }
    } else if (fileOrDirectory.isFile()) {
      return fileOrDirectory.delete();
    }
    return true;
  }

  /**
   * 폴더에 있는 모든 JPG 파일 삭제
   */
  public static boolean deleteJpgFile(File fileOrDirectory) {
    if (fileOrDirectory.isDirectory()) {
      for (File child : fileOrDirectory.listFiles()) {
        if (child.getName().endsWith(".jpg") || child.getName().endsWith(".jpeg")) {
          deleteFile(child);
        }
      }
    } else if (fileOrDirectory.isFile()) {
      return fileOrDirectory.delete();
    }

    return true;
  }

  /**
   * 폴더에 있는 모든 파일 삭제
   */
  public static boolean deleteAllFile(File fileOrDirectory) {
    if (fileOrDirectory.isDirectory()) {
      for (File child : fileOrDirectory.listFiles()) {
        deleteFile(child);
      }
    } else if (fileOrDirectory.isFile()) {
      return fileOrDirectory.delete();
    }
    return true;
  }

  /**
   * 폴더에 있는 모든 파일/디렉토리 삭제
   */
  public static boolean deleteFileDir(String fileOrDirectory) {
    boolean result = false;

    if (Config.DEBUG) {
      Log.d(TAG, "deleteFileDir() : fileOrDirectory[" + fileOrDirectory + "]");
    }

    try {
      File parent = new File(fileOrDirectory);
      if (parent != null && parent.isDirectory()) {
        for (File child : parent.listFiles()) {
          if (child != null) {
            deleteFileDir(child.getPath());
          }
        }
      }

      if (parent != null) {
        if (Config.DEBUG) {
          Log.d(TAG, "deleteFileDir() : delete [" + parent.getPath() + "]");
        }
        parent.delete();
      }
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  public static boolean existFile(String path, String fileName) {
    File file = new File(path, fileName);
    return file.exists();
  }

  public static boolean existFile(String fileName) {
    if (fileName == null) {
      return false;
    }
    File file = new File(fileName);
    return file.exists();
  }

  public static boolean copyFile(String fileName, String save_file) {
    boolean result;
    File file = new File(fileName);
    File savedFile = new File(save_file);
    File parentSavedFile = savedFile.getParentFile();

    if (!parentSavedFile.exists()) {
      parentSavedFile.mkdirs();
    }

    if (!savedFile.exists()) {
      try {
        savedFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (file != null && file.exists()) {
      try {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream newfos = new FileOutputStream(savedFile);
        int readcount = 0;
        byte[] buffer = new byte[1024];
        while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
          newfos.write(buffer, 0, readcount);
        }
        newfos.close();
        fis.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      result = true;
    } else {
      result = false;
    }
    return result;
  }

  public static void copyFile(File fromFile, File targetFile) {

    FileInputStream fis = null;
    FileOutputStream fos = null;
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    if (fromFile != null && fromFile.exists()) {
      try {
        fis = new FileInputStream(fromFile);
        fos = new FileOutputStream(targetFile);
        bis = new BufferedInputStream(fis);
        bos = new BufferedOutputStream(fos);
        int len = -1;
        byte[] buffer = new byte[(int) fromFile.length()];

        while ((len = bis.read(buffer)) > 0) {
          bos.write(buffer, 0, len);
        }

      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          fis.close();
          fos.close();
          bis.close();
          bos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static boolean moveFile(String fileName, String save_file) {
    boolean result;
    File file = new File(fileName);

    if (file != null && file.exists()) {
      try {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream newfos = new FileOutputStream(save_file);
        int readcount = 0;
        byte[] buffer = new byte[1024];
        while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
          newfos.write(buffer, 0, readcount);
        }
        newfos.close();
        fis.close();
        file.delete();
      } catch (Exception e) {
        e.printStackTrace();
      }
      result = true;
    } else {
      result = false;
    }
    return result;
  }

  /**
   * 현재일로 부터 이전(agoDay) 날자 만큼의 날자를 가져온다.
   *
   * @return yyyyMMdd
   */
  public static Date getDayAgoFromToday(int agoDay) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -agoDay);
    return cal.getTime();
  }


/*	public static void makeCheckFile(String path) {
		final File pathFile = new File(path);
		final File checkFile = new File(pathFile, "fileint.lst");

		try {

			if (!checkFile.exists()) {
				pathFile.mkdirs();
				checkFile.createNewFile();
			}
			final OutputStreamWriter osw = new FileWriter(checkFile);
			osw.write(path + "/vcav.db\n");
			osw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/

//	public static void makeFile(String fileName) {
//
//		final File checkFile = new File(fileName);
//
//		try {
//			if (!checkFile.exists()) {
//				checkFile.mkdirs();
//				checkFile.createNewFile();
//			}
//
//			final OutputStreamWriter osw = new FileWriter(checkFile);
//			osw.write(fileName);
//			osw.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}

  /**
   * 폴더 안에 암호화 파일 개수 구하는 함수 (Enc 파일)
   *
   * @return 해당 폴더안의 Enc 파일 개수 리턴
   * @author shlee1219
   */
  public static int getFolderEncFileCnt(String folderName) {
    try {
      int i;

      FilenameFilter fileFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.endsWith(".enc");
        }
      };

      File file = new File(folderName);
      File[] files = file.listFiles(fileFilter);
      String[] titleList = new String[files.length];

      for (i = 0; i < files.length; i++) {
        titleList[i] = files[i].getName();
      }
      return i;

    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * 폴더 안에 이미지 파일 개수 구하는 함수
   *
   * @return 해당 폴더안의 이미지 파일 개수 리턴
   * @author shlee1219
   */
  public static int getFolderImgFileCnt(String folderName) {
    int i = 0;

    try {
      FilenameFilter fileFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.endsWith(".jpg") || name.endsWith(".tif");
        }
      };

      File file = new File(folderName);
      File[] files = file.listFiles(fileFilter);

      for (; files != null && i < files.length; i++) {
        if (Config.DEBUG) {
          Log.d(TAG, "getFolderImgFileCnt() : files[" + i
              + "].getName() = " + files[i].getName());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return i;
  }

  /**
   * 폴더 안에 이미지 파일을 구하는 함수
   *
   * @return 해당 폴더안의 이미지 파일 개수 리턴
   * @author shlee1219
   */
  public static File[] getFolderImgFile(String folderName) {
    File file = null;
    File[] files = null;

    try {
      int i;

      FilenameFilter fileFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          Log.d(TAG, "accept: File : " + dir.getAbsolutePath() + " , name: " + name);
//                    return name.endsWith(".jpg") || name.endsWith(".tif");
          return name.endsWith(".jpg") || name.endsWith(".tif");
        }
      };

      file = new File(folderName);
      files = file.listFiles(fileFilter);

      for (i = 0; files != null && i < files.length; i++) {
        if (Config.DEBUG) {
          Log.d(TAG, "getFolderImgFile() : files[" + i
              + "].getName() = " + files[i].getName());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return files;
  }

  /**
   * 파일복사
   *
   * @author dohun
   */
  public static boolean copyFile(File file, String save_file) {
    boolean result;
    if (file != null && file.exists()) {
      try {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream newfos = new FileOutputStream(save_file);
        int readCount = 0;
        byte[] buffer = new byte[1024];
        while ((readCount = fis.read(buffer, 0, 1024)) != -1) {
          newfos.write(buffer, 0, readCount);
        }
        newfos.close();
        fis.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      result = true;
    } else {
      result = false;
    }
    return result;
  }

  /**
   * 파일 읽어 오기
   *
   * @author dohun
   */
  public static void readFile(File file) {
    int readcount = 0;
    if (file != null && file.exists()) {
      try {
        FileInputStream fis = new FileInputStream(file);
        readcount = (int) file.length();
        byte[] buffer = new byte[readcount];
        fis.read(buffer);
        for (int i = 0; i < file.length(); i++) {
          if (Config.DEBUG) {
            Log.d(TAG, "readFile >> " + buffer[i]);
          }
        }
        fis.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 파일에 내용 쓰기
   *
   * @author dohun
   */
  public static boolean writeFile(File file, byte[] file_content) {
    boolean result;
    FileOutputStream fos;
    if (file != null && file.exists() && file_content != null) {
      try {
        fos = new FileOutputStream(file);
        try {
          fos.write(file_content);
          fos.flush();
          fos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      result = true;
    } else {
      result = false;
    }
    return result;
  }

  /**
   * 파일 존재 여부 확인 하기
   *
   * @author dohun
   */
  public static boolean isFileExist(File file) {
    boolean result;
    result = file != null && file.exists();
    return result;
  }


  public static String modifyFileName(int cnt, String newFileName) {
    cnt = cnt + 1;
    if (cnt < 10) {
      newFileName = newFileName + "_" + "0" + cnt;
    } else {
      newFileName = newFileName + "_" + cnt;
    }
    return newFileName;
  }


  /**
   * @param dirPath 폴더를 만들 경로
   * @author dohun
   */
  public static File makeDirectory(String dirPath) {
    File dir = new File(dirPath);
    if (dir.exists() == false) // dir_path 경로에
    {
      boolean result = dir.mkdirs();
      if (Config.DEBUG) {
        Log.d(TAG, "makeDirectory() : result[" + result + "] dirPath >> " + dirPath);
      }
    }
    return dir;
  }

  /**
   * 폴더 내부의 모든 파일명 추출하는 함수
   *
   * @return 폴더 내부의 모든 파일명 리턴
   * @author shlee1219
   */
  public static File[] getFileName(String path) {
    File file = new File(path);
    FileFilter fileFilter = new FileFilter() {
      public boolean accept(File f) {
        return f.isFile();
      }
    };
    File[] files = file.listFiles(fileFilter);

    if (files == null) {
      return null;
    } else {
      if (Config.DEBUG) {
        Log.d(TAG, "getFileName() : files.length[" + files.length + "] path[" + path + "]");
      }
      for (int i = 0; i < files.length; i++) {
        if (Config.DEBUG) {
          Log.d(TAG, "getFileName() : files[" + i + "].getName() = " + files[i].getName());
        }
      }
    }

    sortNameFileList(files);
//        sortNameForIndex(files);

    return files;
  }

  /**
   * 파일명에서 확장자 추출하는 함수
   *
   * @return 확장자 리턴
   * @author shlee1219
   */
  public static String getExtName(String fileName) {
    fileName.toLowerCase();
    String[] arrFileName = fileName.split("\\.");
    return arrFileName[arrFileName.length - 1];
  }

  public static String[] getSplitFilePath(String path) {
    path.toLowerCase();
    String[] splitPath = path.split("\\.");
    StringBuffer stringBuffer = new StringBuffer();
    int length = splitPath.length;
    for (int i = 0; i < length; i++) {
      if (i == length - 1) {
        break;
      }
      stringBuffer.append(splitPath[i]);
    }
    return new String[]{stringBuffer.toString(), splitPath[length - 1]};
  }

  /**
   * @param deleteFile = 삭제할 파일
   * @param arrayListFileImage = 전제 이미지 리스트
   * @param currentPosition = 삭제할 이미지 번호 0~9
   * @author dohun
   */
  public static void setChangeFileNameNew(String deleteFile, ArrayList<String> arrayListFileImage,
      int currentPosition) {

    // 기존 메소드는 deleteFile이 "/data/data/com.pentaon.bizfast/cache/6/A_1231231231_AA_01.jpg"일 경우에 앱이 죽는 문제가 있었음
    // 즉, "A_"로 split를 하는 방식으로 코딩해서 생긴 오류임. 해당 오류는 "/"를 substring하는 방식으로 변경하여 해결함.

    File[] temp;
    String tempRootPath;
    String tempFilePath;

    if (Config.DEBUG) {
      Log.d(TAG, "setChangeFileNameNew() : currentPosition[" + currentPosition + "] deleteFile >> "
          + deleteFile);
    }

    tempRootPath = deleteFile.substring(0, deleteFile.lastIndexOf("/") + 1);
    tempFilePath = deleteFile.substring(deleteFile.lastIndexOf("/") + 1);

    if (Config.DEBUG) {
      Log.d(TAG,
          "setChangeFileNameNew() : currentPosition[" + currentPosition + "] tempRootPath >> "
              + tempRootPath);
      Log.d(TAG,
          "setChangeFileNameNew() : currentPosition[" + currentPosition + "] tempFilePath >> "
              + tempFilePath);
    }

    temp = getFileName(tempRootPath);

    sortNameFileList(temp);

    if (Config.DEBUG) {
      Log.d(TAG, "setChangeFileNameNew() : arrayListFileImage.get(" + currentPosition + ") >> "
          + arrayListFileImage.get(currentPosition));
    }

    // 지정한 파일 삭제
    deleteFile(arrayListFileImage.get(currentPosition));

    ArrayList<String> fileExtension = new ArrayList<String>();  // 파일 확장자만 따로 리스트에 저장
    String tempPath = null;
    String tempFileName = null;  // A_ 기준으로 자른변수
    String tempFileExtention = null;  // . 기준으로 자른 변수

    if (Config.DEBUG) {
      Log.d(TAG,
          "setChangeFileNameNew() : arrayListFileImage.size() >> " + arrayListFileImage.size());
    }

    // 파일 확장자만 추출
    for (int j = 0; j < arrayListFileImage.size(); j++) {
      tempPath = arrayListFileImage.get(j).toString();
      if (Config.DEBUG) {
        Log.d(TAG, "setChangeFileNameNew() : " + j + " : tempPath >> " + tempPath);
      }
      tempFileName = tempPath.substring(tempPath.lastIndexOf("/") + 1);
      if (tempFileName != null) {
        if (Config.DEBUG) {
          Log.d(TAG, "setChangeFileNameNew() : " + j + " : tempFileName >> " + tempFileName);
        }
        tempFileExtention = tempFileName.substring(tempFileName.lastIndexOf(".") + 1);
      }
      if (tempFileExtention != null) {
        if (Config.DEBUG) {
          Log.d(TAG,
              "setChangeFileNameNew() : " + j + " : tempFileExtention >> " + tempFileExtention);
        }
        fileExtension.add(j, tempFileExtention);
      }
    }
    //---------------------------------------------------------
    // PictureListAct의 mListImagePath 중복 삭제해서 주석 2019-03-25 _ PC-jhKim
    //---------------------------------------------------------
//        arrayListFileImage.remove(currentPosition);
//        fileExtension.remove(currentPosition);
        
/*
        // currentPosition으로 가지고 온 위치에 있는 이미지 삭제 후에 이미지파일 뒤에 붙는 번호랑 확장자 생성 (fileExtention)에 저장
        for (int i = 0; i < arrayListFileImage.size(); i++) {
            String fileNum;
            if (i < 9) {
                fileNum = "0" + String.valueOf(i + 1);
                if (Config.DEBUG) {
                    Log.d(TAG, "setChangeFileNameNew() : FIRST fileNum >> " + fileNum);
                }
            } else {
                fileNum = String.valueOf(i + 1);
                if (Config.DEBUG) {
                    Log.d(TAG, "setChangeFileNameNew() : SECOND fileNum >> " + fileNum);
                }
            }
            fileExtension.set(i, fileNum + "." + fileExtension.get(i));
            if (Config.DEBUG) {
                Log.d(TAG, "setChangeFileNameNew() : FINAL fileExtension.get >> " + fileExtension.get(i).toString());
            }
        }


        String basicDirectory = fileDirectorySave(deleteFile);
        //  파일 번호, 확장자를 차례대로 붙여주기
        for (int i = 0; i < arrayListFileImage.size(); i++) {
            arrayListFileImage.set(i, basicDirectory + fileExtension.get(i));
            if (Config.DEBUG) {
                Log.d(TAG, "setChangeFileNameNew() : arrayListFileImage.get >> " + arrayListFileImage.get(i).toString());
            }
        }

        File[] tempFolderFileName;

        tempFolderFileName = getFileName(tempRootPath); // 실제 저장되어있는 파일 리스트를 가져옴
        if (tempFolderFileName == null)
            return;

        for (int j = currentPosition; j < arrayListFileImage.size(); j++) {
            if (Config.DEBUG) {
                Log.d(TAG, "setChangeFileNameNew() : j  >> " + j);
                Log.d(TAG, "setChangeFileNameNew() : copy from file >> " + temp[j + 1].toString()); //j+1
                Log.d(TAG, "setChangeFileNameNew() : copy to file >> " + arrayListFileImage.get(j).toString());
                Log.d(TAG, "setChangeFileNameNew() : tempRootPath >> " + tempRootPath);
                Log.d(TAG, "setChangeFileNameNew() : deletefile >> " + tempRootPath + temp[j + 1].toString());
            }
            copyFile(temp[j + 1].toString(), arrayListFileImage.get(j).toString());
            deleteFile(temp[j + 1].toString());
        }*/
  }

  public static void setCameraSizeInfo(String key, Size value) {
    if (mArrCameraSizeInfo.size() > 0) {
      mArrCameraSizeInfo.clear();
    }
    HashMap<String, Size> sizeMap = new HashMap<>();
    sizeMap.put(key, value);
    mArrCameraSizeInfo.add(sizeMap);
  }

  public static ArrayList<HashMap<String, Size>> getCameraSizeInfo() {
    return mArrCameraSizeInfo;
  }

  /**
   * 기본 경로 저장
   */
  public static String fileDirectorySave(String fileDirectorySave) {
    // 이미지파일 경로 저장(newAdress)
    String[] cut;
    String basicDirectory = "";
    cut = fileDirectorySave.split("-");
    if (cut.length < 2) {
      cut = fileDirectorySave.split("_");
    }
    Log.d(TAG,
        "fileDirectorySave: cut[0]: " + cut[0] + " ,cut[1]: " + cut[1] + " ,cut[0] = " + cut[2]);
    basicDirectory = cut[0] + "_" + cut[1] + "_" + cut[2] + "_";

    return basicDirectory;
  }


  public static void sortNameFileList(File[] files) {
    Arrays.sort(files, new Comparator() {
      public int compare(Object object1, Object object2) {
        File file1 = (File) object1;
        File file2 = (File) object2;
        return file1.getName().compareToIgnoreCase(file2.getName());
      }
    });
  }

  private static void sortNameForIndex(File[] files) {
    Arrays.sort(files, (o1, o2) -> {
      String pathO1 = o1.getAbsolutePath();
      String pathO2 = o2.getAbsolutePath();
      String partO1 = pathO1.substring(pathO1.length() - 6, pathO1.length() - 4);
      String partO2 = pathO2.substring(pathO2.length() - 6, pathO2.length() - 4);
      int idxO1 = Integer.parseInt(partO1);
      int idxO2 = Integer.parseInt(partO2);
      return Integer.compare(idxO1, idxO2);
    });
  }

  public static void saveTestFile(Bitmap bitmap) {
    String absPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/_testVzon";
    File dirAbsPath = new File(absPath);

    if (!dirAbsPath.exists()) {
      dirAbsPath.mkdirs();
    }

    String currentTime = System.currentTimeMillis() + "";
    File file = new File(absPath + "/file_" + currentTime + ".jpg");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      FileOutputStream fos = new FileOutputStream(file);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      bitmap.compress(CompressFormat.JPEG, 100, bos);
      bos.close();
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e1) {
      e1.printStackTrace();
    }


  }

  public static Bitmap cropBitmap(Bitmap source, Point Lt, Point Rb) {
    int width = Rb.x - Lt.x;
    int height = Rb.y - Lt.y;
    return Bitmap.createBitmap(source, Lt.x, Lt.y, width, height);
  }

  public void CopyAssets(Context nContext) {
    AssetManager assetManager = nContext.getAssets();
    String[] files = null;
    String pdfInstancePath = ApplicationContext.getInstance().getSharedInfo().getCachePath();
    try {
      files = assetManager.list("");
    } catch (IOException e) {
      if (Config.DEBUG) {
        Log.e("tag", e.getMessage());
      }
    }
    for (int i = 0; i < files.length; i++) {
      InputStream in = null;
      OutputStream out = null;
      try {
        in = assetManager.open(files[i]);
        out = new FileOutputStream(pdfInstancePath + files[i]);
        copyFile(in, out);
        in.close();
        in = null;
        out.flush();
        out.close();
        out = null;
      } catch (Exception e) {
        if (Config.DEBUG) {
          Log.e(TAG, e.getMessage());
        }
      }
    }
  }

  public void copyFile(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }
  }

  public static String getDeviceId(Context mContext) {
/*		String tempUuid = "";

		final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;

		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		tempUuid = deviceUuid.toString();

		return tempUuid;*/

    String deviceId = "";

    TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    if (tm != null) {
      deviceId = tm.getDeviceId(); // IMEI 저장
    }

    // 현재 서버에서 MacAddress 값 처리 못함 ( : 값 처리 못함)
/*		if(deviceId == null || deviceId.trim().equals("")) {
			WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			if (wm != null) {
				deviceId = wm.getConnectionInfo().getMacAddress();
			}
		}*/

    if (deviceId == null || deviceId.trim().equals("") || deviceId.trim()
        .equals("000000000000000")) {
      deviceId = android.provider.Settings.Secure
          .getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    return deviceId;
  }

  public static String formattingDate(long milliseconds, String template) {
    Date date = new Date(milliseconds);
    SimpleDateFormat format = new SimpleDateFormat(template,
        Locale.KOREA); // "yyyy-MM-dd HH:mm:ss.SSS"
    return format.format(date);
  }

  public static float mmtoPixel(Context context, float mm) { //1 mm -> px 로 변경
//        float tPixel  =(float)(mm /25.4*getDensityDpi(context));
    float tPixel = TypedValue
        .applyDimension(TypedValue.COMPLEX_UNIT_MM, mm, context.getResources().getDisplayMetrics());
    return tPixel;
  }

  /**
   * Desity DPI 값
   */
  public static int getDensityDpi(Context mContext) {
    WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);
    Log.d(TAG, "getDensityDpi: " + metrics.densityDpi);
    return metrics.densityDpi;
  }

  /**
   * Desity DPI 배율값
   */
  public static float getDensityRate(Context mContext) {
    Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE))
        .getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);

    return metrics.density;
  }

  // TODO: 2018-09-10 하단에 soft buttons이 있는 device같은 경우 density 값이 다르게 나올 수 있음

  /**
   * Desity 값
   */
  public static String getDensity(int densityDpi) {
    String density = "";

    if (densityDpi <= 120) {
      density = "ldpi";
    } else if (densityDpi <= 160) {
      density = "mdpi";
    } else if (densityDpi <= 240) {
      density = "hdpi";
    } else if (densityDpi <= 320) {
      density = "xhdpi";
    } else if (densityDpi <= 480) {
      density = "xxhdpi";
    } else if (densityDpi <= 640) {
      density = "xxxhdpi";
    } else {
      density = "nodpi";
    }

    return density;
  }

//	/**
//	 * mDPI인지 여부
//	 */
//	public static boolean isDENSITY_MEDIUM() {
//		return getDensityDpi(ApplicationContext.getInstance().getBaseContext()) == DisplayMetrics.DENSITY_MEDIUM;
//	}

  /**
   * Screen density가 High density 이상 인지 여부 (hdpi, xhdpi, xxhdpi, xxxhdpi...)
   */
  public static boolean isHighExtraHighDensity() {
    return getDensityDpi(ApplicationContext.getInstance().getBaseContext())
        >= DisplayMetrics.DENSITY_HIGH;
  }

  /**
   * displayMetrics.widthPixels, displayMetrics.heightPixels
   */
  public static int[] getDisplayMetricsWidthPixelsHeightPixels() {
    Display display = ((WindowManager) ApplicationContext.getInstance().getBaseContext()
        .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    display.getMetrics(displayMetrics);
    int widthPixels = displayMetrics.widthPixels;
    int heightPixels = displayMetrics.heightPixels;
    int widthPixelsHeightPixels[] = {widthPixels, heightPixels};

    if (Config.DEBUG) {
      Log.d(TAG, "##### getDisplayMetricsWidthPixels : widthPixels = " + widthPixels);
      Log.d(TAG, "##### getDisplayMetricsHeightPixels : heightPixels = " + heightPixels);
    }

    return widthPixelsHeightPixels;
  }

  /**
   * displayMetrics.widthPixels
   */
  public static int getDisplayMetricsWidthPixels() {
    Display display = ((WindowManager) ApplicationContext.getInstance().getBaseContext()
        .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    display.getMetrics(displayMetrics);
    int widthPixels = displayMetrics.widthPixels;

    if (Config.DEBUG) {
      Log.d(TAG, "##### getDisplayMetricsWidthPixels : widthPixels = " + widthPixels);
    }

    return widthPixels;
  }

  /**
   * displayMetrics.heightPixels
   */
  public static int getDisplayMetricsHeightPixels() {
    Display display = ((WindowManager) ApplicationContext.getInstance().getBaseContext()
        .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    display.getMetrics(displayMetrics);
    int heightPixels = displayMetrics.heightPixels;

    if (Config.DEBUG) {
      Log.d(TAG, "##### getDisplayMetricsHeightPixels : heightPixels = " + heightPixels);
    }

    return heightPixels;
  }

  /**
   * 전달된 값(param)과 WidthPixels 값의 비율을 구함
   *
   * @param targetWidth : 비교하고자 하는 width 값
   * @return 비율
   */
  public static float getRatioByMetricsWidth(int targetWidth) {
    int width = getDisplayMetricsWidthPixels();
    return (float) width / targetWidth;
  }

  public static float getRatioByMetricsHeight(int targetHeight) {
    int height = getDisplayMetricsHeightPixels();
    return (float) height / targetHeight;
  }


  /**
   * Display Aspect Ration 4:3 (Graphics Display Resolution : 1024x768 XGA, 2048x1536 QXGA) 예를 들어
   * 갤럭시 탭 A with S Pen (SM-P555S, SM-P555K, SM-P555L : XGA), 갤럭시 탭 S3 (SM-T825N0 : QXGA)가 해당됨.
   */
  public static boolean isDisplayAspectRatio4to3() {
    Display display = ((WindowManager) ApplicationContext.getInstance().getBaseContext()
        .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    display.getMetrics(displayMetrics);
    int widthPixels = displayMetrics.widthPixels;
    int heightPixels = displayMetrics.heightPixels;

    float displayAspectRatio = 0.0f;

    if ((float) widthPixels >= (float) heightPixels) {
      displayAspectRatio = (float) widthPixels / (float) heightPixels;
    } else {
      displayAspectRatio = (float) heightPixels / (float) widthPixels;
    }

    float displayAspectRatio4to3 = 4.0f / 3.0f;

    boolean isDisplayAspectRatio4to3 =
        (displayAspectRatio == displayAspectRatio4to3) ? true : false;

    if (Config.DEBUG) {
      Log.d(TAG, "##### isDisplayAspectRatio4to3 : widthPixels = " + widthPixels);
      Log.d(TAG, "##### isDisplayAspectRatio4to3 : heightPixels = " + heightPixels);
      Log.d(TAG, "##### isDisplayAspectRatio4to3 : displayAspectRatio = " + displayAspectRatio);
      Log.d(TAG,
          "##### isDisplayAspectRatio4to3 : displayAspectRatio4to3 = " + displayAspectRatio4to3);
      Log.d(TAG, "##### isDisplayAspectRatio4to3 : isDisplayAspectRatio4to3 = "
          + isDisplayAspectRatio4to3);
    }

    return isDisplayAspectRatio4to3;
  }

  /**
   * Graphics Display Resolution 1024x768 XGA 예를 들어 갤럭시 탭 A with S Pen (SM-P555S, SM-P555K,
   * SM-P555L)가 해당됨.
   */
  public static boolean isGraphicsDisplayResolution1024x768() {
    Display display = ((WindowManager) ApplicationContext.getInstance().getBaseContext()
        .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    display.getMetrics(displayMetrics);
    int widthPixels = 0;
    int heightPixels = 0;

    if (displayMetrics.widthPixels >= displayMetrics.heightPixels) {
      widthPixels = displayMetrics.widthPixels;
      heightPixels = displayMetrics.heightPixels;
    } else {
      widthPixels = displayMetrics.heightPixels;
      heightPixels = displayMetrics.widthPixels;
    }

    boolean isGraphicsDisplayResolution1024x768 =
        ((widthPixels == 1024) && (heightPixels == 768)) ? true : false;

    if (Config.DEBUG) {
      Log.d(TAG, "##### isGraphicsDisplayResolution1024x768 : widthPixels = " + widthPixels);
      Log.d(TAG, "##### isGraphicsDisplayResolution1024x768 : heightPixels = " + heightPixels);
      Log.d(TAG,
          "##### isGraphicsDisplayResolution1024x768 : isGraphicsDisplayResolution1024x768 = "
              + isGraphicsDisplayResolution1024x768);
    }

    return isGraphicsDisplayResolution1024x768;
  }

  /**
   * Graphics Display Resolution 2048x1536 QXGA 예를 들어 갤럭시 탭 S3 (SM-T825N0)가 해당됨.
   */
  public static boolean isGraphicsDisplayResolution2048x1536(Context context) {
    //Display display = ((WindowManager) ApplicationContext.getInstance().getBaseContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
        .getDefaultDisplay();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    display.getMetrics(displayMetrics);
    int widthPixels = 0;
    int heightPixels = 0;

    if (displayMetrics.widthPixels >= displayMetrics.heightPixels) {
      widthPixels = displayMetrics.widthPixels;
      heightPixels = displayMetrics.heightPixels;
    } else {
      widthPixels = displayMetrics.heightPixels;
      heightPixels = displayMetrics.widthPixels;
    }

    boolean bGraphicsDisplayResolution2048x1536 =
        ((widthPixels == 2048) && (heightPixels == 1536)) ? true : false;

    if (Config.DEBUG) {
      Log.d(TAG, "##### isGraphicsDisplayResolution2048x1536 : widthPixels = " + widthPixels);
      Log.d(TAG, "##### isGraphicsDisplayResolution2048x1536 : heightPixels = " + heightPixels);
      Log.d(TAG,
          "##### isGraphicsDisplayResolution2048x1536 : isGraphicsDisplayResolution2048x1536 = "
              + bGraphicsDisplayResolution2048x1536);
    }

    return bGraphicsDisplayResolution2048x1536;
  }

  /**
   * Density가 Extra-high-density이고 Resolution이 2048x1536 인지 여부 (QXGA, 4:3) 예를 들어 갤럭시 탭 S3
   * (SM-T825N0)가 해당됨.
   */
  public static boolean isExtraHighDensity2048x1536() {
    Context context = ApplicationContext.getInstance().getBaseContext();
    String density = getDensity(getDensityDpi(context));
    boolean isxhdpi = "xhdpi".equals(density);
    boolean is2048x1536 = isGraphicsDisplayResolution2048x1536(context);
    boolean isExtraHighDensity2048x1536 = (isxhdpi && is2048x1536) ? true : false;

    if (Config.DEBUG) {
      Log.d(TAG, "##### isExtraHighDensity2048x1536 : isxhdpi = " + isxhdpi);
      Log.d(TAG, "##### isExtraHighDensity2048x1536 : is2048x1536 = " + is2048x1536);
      Log.d(TAG, "##### isExtraHighDensity2048x1536 : isExtraHighDensity2048x1536 = "
          + isExtraHighDensity2048x1536);
    }
    return isExtraHighDensity2048x1536;
  }


  /**
   * Device Serial Number (삼성 디바이스에서만 값을 가져 올 수 있음)
   */
  public static String getDeviceSerialNumber() {
    String serialNumber = "";
    try {
      Class<?> c = Class.forName("android.os.SystemProperties");
      Method get = c.getMethod("get", String.class, String.class);
      serialNumber = (String) get.invoke(c, "ril.serialnumber", "");
    } catch (Exception ignored) {
    }
    return serialNumber;
  }

//	public static boolean isSupportedDeviceModel(String modelName) { // 디바이스 모델 체크
//		if(Config.DEBUG)
//		{
//			Log.d(TAG,"##### isSupportedDeviceModel() : modelName == " + modelName);
//		}
//
//		if (TextUtils.isEmpty(modelName) == true) {
//			return false;
//		}
//
//		for(String supportedDeviceModel : Config.supportedDeviceModels) {
//			if(Config.DEBUG)
//			{
//				Log.d(TAG,"##### isSupportedDeviceModel() : supportedDeviceModel == " + supportedDeviceModel);
//			}
//
//			if (supportedDeviceModel.equals(modelName.trim())) {
//				return true;
//			}
//		}
//
//		return false;
//	}


  public static void checkGooglePlayServicesAvailable(final Activity activity) {
    IS_GOOGLE_PLAY_SERVICES_AVAILABLE = GooglePlayServicesUtil
        .isGooglePlayServicesAvailable(activity.getApplicationContext());

    if (Config.DEBUG) {
      Log.d(TAG, "##### checkGooglePlayServicesAvailable() : IS_GOOGLE_PLAY_SERVICES_AVAILABLE = "
          + IS_GOOGLE_PLAY_SERVICES_AVAILABLE);
    }

    if (IS_GOOGLE_PLAY_SERVICES_AVAILABLE != ConnectionResult.SUCCESS) {
      boolean isUserRecoverableError = GooglePlayServicesUtil
          .isUserRecoverableError(IS_GOOGLE_PLAY_SERVICES_AVAILABLE);
      // true : ConnectionResult.SERVICE_MISSING 				   : 1
      // true : ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED : 2
      // true : ConnectionResult.SERVICE_DISABLED 			   : 3
      // true : ConnectionResult.SERVICE_INVALID 				   : 9

      if (Config.DEBUG) {
        Log.d(TAG, "##### checkGooglePlayServicesAvailable() : isUserRecoverableError = "
            + isUserRecoverableError);
      }

      if (isUserRecoverableError) {
        Dialog errorDialog = GooglePlayServicesUtil
            .getErrorDialog(IS_GOOGLE_PLAY_SERVICES_AVAILABLE, activity, 0);
        if (errorDialog != null) {
          errorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
              activity.finish();
            }
          });
          errorDialog.show();
        }
      }
    }
  }


  public static void checkGooglePlayServicesInfo(Activity activity) {

    checkGooglePlayServicesAvailable(activity);

    try {
      if ((StringUtil.isEmpty(ANDROID_OS_BUILD_MODEL) == false) && (ANDROID_OS_BUILD_MODEL.length()
          > 3)) {
        ANDROID_OS_BUILD_MODEL = "***" + ANDROID_OS_BUILD_MODEL.substring(3);
      } else {
        ANDROID_OS_BUILD_MODEL = "***";
      }
      DEVICE_SERIAL_NUMBER = SystemUtil.getDeviceSerialNumber();
      if ((StringUtil.isEmpty(DEVICE_SERIAL_NUMBER) == false) && (DEVICE_SERIAL_NUMBER.length()
          > 5)) {
        DEVICE_SERIAL_NUMBER = "*****" + DEVICE_SERIAL_NUMBER.substring(5);
      } else {
        DEVICE_SERIAL_NUMBER = "*****";
      }
      APK_PACKAGE_NAME = ApplicationContext.getInstance().getPackageName();
      APK_VERSION_NAME = ApplicationContext.getInstance().getPackageManager()
          .getPackageInfo(APK_PACKAGE_NAME, 0).versionName;
      GOOGLE_PLAY_SERVICES_APP_VERSION_CODE = ApplicationContext.getInstance().getPackageManager()
          .getPackageInfo(GOOGLE_PLAY_SERVICES_PACKAGE, 0).versionCode;
      GOOGLE_PLAY_SERVICES_APP_VERSION_NAME = ApplicationContext.getInstance().getPackageManager()
          .getPackageInfo(GOOGLE_PLAY_SERVICES_PACKAGE, 0).versionName;
    } catch (Exception e) {
      Log.e(TAG, "##### checkGooglePlayServicesInfo() : " + e.toString());
    }

    if (Config.DEBUG) {
      Log.d(TAG, "##### checkGooglePlayServicesInfo() : APK_VERSION_NAME = " + APK_VERSION_NAME);
      Log.d(TAG, "##### checkGooglePlayServicesInfo() : ANDROID_OS_BUILD_VERSION_RELEASE = "
          + ANDROID_OS_BUILD_VERSION_RELEASE);
//            Log.d(TAG, "##### checkGooglePlayServicesInfo() : GOOGLE_PLAY_SERVICES_VERSION_CODE = " + GOOGLE_PLAY_SERVICES_VERSION_CODE);
      Log.d(TAG, "##### checkGooglePlayServicesInfo() : GOOGLE_PLAY_SERVICES_APP_VERSION_CODE = "
          + GOOGLE_PLAY_SERVICES_APP_VERSION_CODE);
      Log.d(TAG, "##### checkGooglePlayServicesInfo() : GOOGLE_PLAY_SERVICES_APP_VERSION_NAME = "
          + GOOGLE_PLAY_SERVICES_APP_VERSION_NAME);
      Log.d(TAG, "##### checkGooglePlayServicesInfo() : ANDROID_OS_BUILD_MODEL = "
          + ANDROID_OS_BUILD_MODEL);
      Log.d(TAG,
          "##### checkGooglePlayServicesInfo() : DEVICE_SERIAL_NUMBER = " + DEVICE_SERIAL_NUMBER);
    }
  }

  public static boolean copyAssetAll(Context context, String srcPath) {
    AssetManager assetMgr = context.getAssets();
    String assets[] = null;
    try {
      assets = assetMgr.list(srcPath);
      if (assets.length == 0) {
        copyFileAssets(context, srcPath);
      } else {
        //String destPath = context.getFilesDir().getAbsolutePath() + File.separator + srcPath;
        String destPath = "/mnt/sdcard" + File.separator + srcPath;

        File dir = new File(destPath);
        if (!dir.exists()) {
          dir.mkdir();
        }
        for (String element : assets) {
          copyAssetAll(context, srcPath + File.separator + element);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static void copyFileAssets(Context context, String srcFile) {
    AssetManager assetMgr = context.getAssets();

    InputStream is = null;
    OutputStream os = null;
    try {
      String destFile = "/mnt/sdcard" + File.separator + srcFile;

      is = assetMgr.open(srcFile);
      os = new FileOutputStream(destFile);

      byte[] buffer = new byte[1024];
      int read;
      while ((read = is.read(buffer)) != -1) {
        os.write(buffer, 0, read);
      }
      is.close();
      os.flush();
      os.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int getDisplayOrientation(Activity activity) {

    if (Config.DEBUG) {
//			Log.d(TAG,"############################ Device Info ############################");
//			Log.d(TAG,"##### android.os.Build.MANUFACTURER : "+android.os.Build.MANUFACTURER );
//			Log.d(TAG,"##### android.os.Build.MODEL : "+android.os.Build.MODEL );
//			Log.d(TAG,"##### android.os.Build.PRODUCT : "+android.os.Build.PRODUCT );
//			Log.d(TAG,"##### android.os.Build.SERIAL : "+android.os.Build.SERIAL );
//			Log.d(TAG,"##### DeviceId : "+SystemUtil.getDeviceId(activity.getApplicationContext()));
//			Log.d(TAG,"##### DeviceSerialNumber : "+SystemUtil.getDeviceSerialNumber(activity.getApplicationContext()));
//			Log.d(TAG,"##### PhoneNumber = "+SystemUtil.getPhoneNumber(activity.getApplicationContext()));
//			Log.d(TAG,"##### android.os.Build.TIME : "+android.os.Build.TIME );
//			Log.d(TAG,"##### formattingDate(Build.TIME) = " + SystemUtil.formattingDate(Build.TIME, "yyyyMMdd"));
//			Log.d(TAG,"##### AppConstants.DEVICE_OS : "+ AppConstants.DEVICE_OS );
//			Log.d(TAG,"##### android.os.Build.VERSION.RELEASE : "+android.os.Build.VERSION.RELEASE );
//			Log.d(TAG,"##### android.os.Build.VERSION.SDK_INT : "+android.os.Build.VERSION.SDK_INT );
//			Log.d(TAG,"##### DensityDpi : "+SystemUtil.getDensityDpi(activity.getApplicationContext()) );
//			Log.d(TAG,"##### Density : "+SystemUtil.getDensity(SystemUtil.getDensityDpi(activity.getApplicationContext())) );
//			Log.d(TAG,"############################ Device Info ############################");
    }

    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    if (Config.DEBUG) {
      Log.d(TAG, "##### getDisplayOrientation() : rotation = " + rotation);
    }

    int degrees = 0;
    int result = 0;
    switch (rotation) {
      case Surface.ROTATION_0: // 0 (세로형, 기본 상태) desiredRotation = 90
        degrees = 0;
        result = 90;
        break;
      case Surface.ROTATION_90: // 1 (가로형, 180도 회전상태) desiredRotation = 0
        degrees = 90;
        result = 270;
        break;
      case Surface.ROTATION_180: // 2 (세로형, 180 회전 상태) desiredRotation = 90
        degrees = 180;
        result = 270;
        break;
      case Surface.ROTATION_270: // 3 (가로형, 기본 상태) desiredRotation = 0
        degrees = 270;
        result = 90;
        break;
    }
    if (Config.DEBUG) {
      Log.d(TAG, "##### getDisplayOrientation() : degrees = " + degrees);
      Log.d(TAG, "##### getDisplayOrientation() : result = " + result);
    }

    return result;
  }

  /**
   * 앱에 쌓여있는 캐시 데이터 삭제
   */
  public static void clearCache() {
    try {
      File dir = ApplicationContext.getInstance().getCacheDir();
      Log.d(TAG, "clearCache: dir =" + dir);
      deleteDir(dir);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getPathFromUri(Context context, Uri uri) {
    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
    cursor.moveToNext();
    String path = cursor.getString(cursor.getColumnIndex("_data"));
    cursor.close();
    return path;
  }

  /**
   * app이 현재 background 동작하고 있는지 확인
   */
  public static boolean isAppInBackground(Context context) {
    boolean isInBackground = true;
    ActivityManager activityManager = (ActivityManager) context
        .getSystemService(Context.ACTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
      List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager
          .getRunningAppProcesses();
      for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfos) {
        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
          for (String activityProcess : processInfo.pkgList) {
            if (activityProcess.equals(context.getPackageName())) {
              isInBackground = false;
              break;
            }
          }
        }
      }
    } else {
      List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
      ComponentName componentInfo = taskInfo.get(0).topActivity;
      if (componentInfo.getPackageName().equals(context.getPackageName())) {
        isInBackground = false;
      }
    }
    return isInBackground;
  }


  /**
   * 해당 파일(sub 폴더 및 파일 포함) 삭제
   */
  private static boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
      return dir.delete();
    } else if (dir != null && dir.isFile()) {
      return dir.delete();
    } else {
      return false;
    }
  }

}
