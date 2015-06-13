package com.example.classes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class ImageFilePath {

	
	 public static String getPath(final Context context, final Uri uri) {

         // check here to KITKAT or new version
         final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

         // DocumentProvider
         if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					if ("primary".equalsIgnoreCase(type)) {
						return Environment.getExternalStorageDirectory() + "/"
								+ split[1];
					}
				}
				// DownloadsProvider
				else if (isDownloadsDocument(uri)) {

					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"),
							Long.valueOf(id));

					return getDataColumn(context, contentUri, null, null);
				}
				// MediaProvider
				else if (isMediaDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					final String selection = "_id=?";
					final String[] selectionArgs = new String[] { split[1] };

					return getDataColumn(context, contentUri, selection,
							selectionArgs);
				}
         }
         // MediaStore (and general)
         else if ("content".equalsIgnoreCase(uri.getScheme())) {

             // Return the remote address
             if (isGooglePhotosUri(uri))
                 return uri.getLastPathSegment();

             return getDataColumn(context, uri, null, null);
         }
         // File
         else if ("file".equalsIgnoreCase(uri.getScheme())) {
             return uri.getPath();
         }

         return null;
     }
	 
	 /**
      * Get the value of the data column for this Uri. This is useful for
      * MediaStore Uris, and other file-based ContentProviders.
      * 
      * @param context
      *            The context.
      * @param uri
      *            The Uri to query.
      * @param selection
      *            (Optional) Filter used in the query.
      * @param selectionArgs
      *            (Optional) Selection arguments used in the query.
      * @return The value of the _data column, which is typically a file path.
      */
     public static String getDataColumn(Context context, Uri uri,
             String selection, String[] selectionArgs) {

         Cursor cursor = null;
         final String column = "_data";
         final String[] projection = { column };

         try {
             cursor = context.getContentResolver().query(uri, projection,
                     selection, selectionArgs, null);
             if (cursor != null && cursor.moveToFirst()) {
                 final int index = cursor.getColumnIndexOrThrow(column);
                 return cursor.getString(index);
             }
         } finally {
             if (cursor != null)
                 cursor.close();
         }
         return null;
     }

     /**
      * @param uri
      *            The Uri to check.
      * @return Whether the Uri authority is ExternalStorageProvider.
      */
     public static boolean isExternalStorageDocument(Uri uri) {
         return "com.android.externalstorage.documents".equals(uri
                 .getAuthority());
     }

     /**
      * @param uri
      *            The Uri to check.
      * @return Whether the Uri authority is DownloadsProvider.
      */
     public static boolean isDownloadsDocument(Uri uri) {
         return "com.android.providers.downloads.documents".equals(uri
                 .getAuthority());
     }

     /**
      * @param uri
      *            The Uri to check.
      * @return Whether the Uri authority is MediaProvider.
      */
     public static boolean isMediaDocument(Uri uri) {
         return "com.android.providers.media.documents".equals(uri
                 .getAuthority());
     }

     /**
      * @param uri
      *            The Uri to check.
      * @return Whether the Uri authority is Google Photos.
      */
     public static boolean isGooglePhotosUri(Uri uri) {
         return "com.google.android.apps.photos.content".equals(uri.getAuthority());
     }
     
     public static Bitmap loadBitmap(File file)
     {
         Bitmap bm = null;
         BufferedInputStream bis = null;
         try 
         {
             bm = ImageFilePath.decodeSampledBitmapFromResource(file.getPath(), 400, 400);
             
             	int w=400,h=400;
				if(bm.getWidth()>bm.getHeight()){
					double f = (double)400/bm.getWidth();
					h = (int) (f*bm.getHeight());
					w = 400;
				}else{
					double f = (double)400/bm.getHeight();
					w = (int) (f*bm.getWidth());
					h = 400;
				}
				bm = Bitmap.createScaledBitmap(bm, w, h, true);
				
				ExifInterface ei = new ExifInterface(file.getPath());
	            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				
	             switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					bm= rotateImage(bm, 90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					bm=rotateImage(bm, 180);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					bm=rotateImage(bm, 270);
					break;
					
				}
             
         }
         catch (Exception e) 
         {
             e.printStackTrace();
         }
         return bm;
     }
     
	private static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromResource(String path,int reqWidth, int reqHeight) {
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(path, options);
	}
     
	public static Bitmap rotateImage(Bitmap bm,float degree){
		
		Matrix matrix = new Matrix();

		matrix.postRotate(degree);

		Bitmap rotatedBitmap = Bitmap.createBitmap(bm , 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		bm.recycle();
		
		return rotatedBitmap;
		
	}
	
	public static Bitmap loadBitmapCut(File file)
    {
        Bitmap bm = null;
        BufferedInputStream bis = null;
        try 
        {
            bm = ImageFilePath.decodeSampledBitmapFromResource(file.getPath(), 75, 75);
            
            	int w=75,h=75;
				if(bm.getWidth()>bm.getHeight()){
					double f = (double)75/bm.getWidth();
					h = (int) (f*bm.getHeight());
					w = 75;
				}else{
					double f = (double)75/bm.getHeight();
					w = (int) (f*bm.getWidth());
					h = 75;
				}
				bm = Bitmap.createScaledBitmap(bm, w, h, true);
				
				ExifInterface ei = new ExifInterface(file.getPath());
	            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				
	             switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					bm= rotateImage(bm, 90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					bm=rotateImage(bm, 180);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					bm=rotateImage(bm, 270);
					break;
					
				}
            
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return bm;
    }
	
 }
