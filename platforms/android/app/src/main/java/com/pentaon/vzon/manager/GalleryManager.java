package com.pentaon.vzon.manager;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import com.pentaon.vzon.data.PhotoVO;
import java.io.IOException;
import java.util.ArrayList;

public class GalleryManager {

  private static final String TAG = GalleryManager.class.getSimpleName();
  private Context mContext;

  public GalleryManager(Context context) {
    this.mContext = context;
  }

  /**
   * get image infos({@link PhotoVO}) in the Gallery.
   * @return
   */
  public ArrayList<PhotoVO> getAllPhotoList() {
    ArrayList<PhotoVO> photoVOS = new ArrayList<>();
    Uri uri = Media.EXTERNAL_CONTENT_URI;
    String[] projection =
    {
        MediaColumns.DATA, Media.DATE_ADDED, ImageColumns.ORIENTATION
    };

    Cursor cursor = mContext.getContentResolver().query(
        uri, projection,null, null,Media.DATE_ADDED);

    int columnIndexData = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
    int columnIndexOrientation = cursor.getColumnIndexOrThrow(ImageColumns.ORIENTATION);

//    ExifInterface exif = null;

    while(cursor.moveToNext())
    {
      String path = cursor.getString(columnIndexData);
      int orientation = cursor.getInt(columnIndexOrientation);

      /*try{
        exif = new ExifInterface(path);
      }catch (IOException e){
        e.printStackTrace();
      }

      int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
      Log.d(TAG, "getAllPhotoList: exif.orientation = "+orientation
          +" ,cursor.orientation = "+cursor.getInt(columnIndexOrientation)
          +" ,path= "+path
      );*/
      PhotoVO photoVO = new PhotoVO(path,orientation);
//      PhotoVO photoVO = new PhotoVO(path,orientation);
      photoVOS.add(0,photoVO);
    }
    cursor.close();

    return photoVOS;
  }
}
