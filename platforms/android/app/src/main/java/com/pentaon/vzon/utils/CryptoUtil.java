package com.pentaon.vzon.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;

import java.util.HashMap;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

  private static final String ALGORITHM = "AES";
  private Key mSecretKey;
  private HashMap<String, Boolean> mEncryptInfoMap = new HashMap<String, Boolean>();

  private CryptoUtil() {
    String keySeed = getRandomChar(16);
    mSecretKey = new SecretKeySpec(keySeed.getBytes(), ALGORITHM);
  }

  public static CryptoUtil getInstance() {
    return LazyHolder.INSTANCE;
  }

  public boolean getEncryptStatus(String path) {
    return (mEncryptInfoMap.get(path) == null) ? false : mEncryptInfoMap.get(path);
  }

  public void setEncryptStatus(String path, boolean encryptInfo) {
    mEncryptInfoMap.put(path, encryptInfo);
  }

  public Key getSecretKey(){
    return mSecretKey;
  }

  public void clearEncryptStatuses() {
    mEncryptInfoMap.clear();
  }


  private static class LazyHolder {
    private static final CryptoUtil INSTANCE = new CryptoUtil();
  }

  /**
   * 암.복호화에 사용되는 대칭키(Symmetric key) 생성을 위해 Random으로 캐릭터 생성
   */
  private String getRandomChar(int numOfChar) {
    String BASE = "abcdefghijklmnopqrstuvwxyz1234567890";
    StringBuilder stringBuilder = new StringBuilder();
    Random rnd = new Random();
    while (stringBuilder.length() < numOfChar) {
      int index = (int) (rnd.nextFloat() * BASE.length());
      stringBuilder.append(BASE.charAt(index));
    }
    return stringBuilder.toString();
  }


  /**
   * encrypts(decrypts) a file
   *
   * @param cipherMode 2 mode(Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE) //   * @param key a
   * symmetric key using when it encrypts, (or decrypt);
   */
  public void encryptDecryptFile(int cipherMode, File input, File out) {
    /*try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(cipherMode, mSecretKey);

      FileInputStream inputStream = new FileInputStream(input);
      BufferedInputStream bis = new BufferedInputStream(inputStream);
      byte[] inputBytes = new byte[(int) input.length()];
      bis.read(inputBytes);

      byte[] outputBytes = cipher.doFinal(inputBytes);

      FileOutputStream outputStream = new FileOutputStream(out);
      BufferedOutputStream bos = new BufferedOutputStream(outputStream);
      bos.write(outputBytes);

      bis.close();
      bos.flush();
      bos.close();
      inputStream.close();
      outputStream.close();

    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  /**
   * 특정 경로(path)에 대상 파일(out)의 복사본 "copy.jpg" 파일을 만들고, 암,복호화 진행(cipherMode)
   *
   * @param cipherMode : 2 mode(Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE) //   * @param key :  a
   * symmetric key using when it encrypts, (or decrypt);
   * @param path : 암복화 진행할 파일의 위치
   */
  public void encryptDecryptFile(int cipherMode, String path, File out) {
    /*try {
      String copyFilePath = path + "copy.jpg";
      File copiedFile = new File(copyFilePath);
      if (!copiedFile.exists()) {
        copiedFile.createNewFile();
      }
      copyFile(out, copiedFile);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(cipherMode, mSecretKey);

      FileInputStream inputStream = new FileInputStream(copiedFile);
      BufferedInputStream bis = new BufferedInputStream(inputStream);
      byte[] inputBytes = new byte[(int) copiedFile.length()];
      bis.read(inputBytes);

      byte[] outputBytes = cipher.doFinal(inputBytes);

      FileOutputStream outputStream = new FileOutputStream(out);
      BufferedOutputStream bos = new BufferedOutputStream(outputStream);
      bos.write(outputBytes);

      bis.close();
//            bos.flush();
      bos.close();
      inputStream.close();
      outputStream.close();
      copiedFile.delete();
      boolean isEncrypted = (cipherMode == Cipher.ENCRYPT_MODE);
      CryptoUtil.getInstance().setEncryptStatus(out.getAbsolutePath(), isEncrypted);

    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  private void copyFile(File fromFile, File targetFile) {
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
}
