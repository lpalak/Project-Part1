package com.example.classes;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;


public class Shared {
	
	public static Bitmap rotateImage(Context context,Uri u, Bitmap b){
        ExifInterface ei;
        File filechk =  new File(ImageFilePath.getPath(context, u));
		try {
			ei = new ExifInterface(filechk.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			
             switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				b= ImageFilePath.rotateImage(b, 90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				b=ImageFilePath.rotateImage(b, 180);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				b=ImageFilePath.rotateImage(b, 270);
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

}
