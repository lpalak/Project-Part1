package com.example.shareimage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.example.classes.Action;
import com.example.classes.CustomGallery;
import com.example.classes.GalleryAdapter;
import com.example.classes.ImageFilePath;
import com.example.classes.Shared;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.graphics.Bitmap;


public class SelectMultiple extends Activity {
	
	private ViewSwitcher viewSwitcher;
	private ImageLoader imageLoader;
	private GridView gridGallery;
	private GalleryAdapter adapter;
    private	String[] all_path;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_multiple);
		
		initImageLoader();
		
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);
		gridGallery.setAdapter(adapter);

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		viewSwitcher.setDisplayedChild(1);
		all_path = this.getIntent().getStringArrayExtra("all_path");

	}
		
		@Override
		protected void onResume() {
			super.onResume();
			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

			for (String string : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;

				dataT.add(item);
			}
			viewSwitcher.setDisplayedChild(0);
			adapter.addAll(dataT);
		}
		
		public void share (View v){
			if (all_path.length >10) {
				confirmDialog(SelectMultiple.this); 
			}else {
	     		multiShare();
			}
		}

		private void multiShare() {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND_MULTIPLE);
			intent.putExtra(Intent.EXTRA_SUBJECT, "");
			intent.setType("image/jpeg");
			
//				   if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SEND_MULTIPLE)) {
			        ArrayList<Uri> uris = new ArrayList<Uri>(all_path.length);
			        for (int i = 0; i < all_path.length; i++) {
//				            Uri uri = Uri.parse(all_path[i]);// uris.get(i);
			            String path = all_path[i];
			            File imageFile = new File(path);
			            Uri   uri = getImageContentUri(imageFile);
			            uris.add(uri);
			        }
			        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			        startActivityForResult(intent, 200);
		}
		
	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
				new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}
	


	private Uri getImageContentUri(File imageFile) {
	    String filePath = imageFile.getAbsolutePath();
	    Cursor cursor = getApplicationContext().getContentResolver().query(
	            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	            new String[] { MediaStore.Images.Media._ID },
	            MediaStore.Images.Media.DATA + "=? ",
	            new String[] { filePath }, null);
	    if (cursor != null && cursor.moveToFirst()) {
	        int id = cursor.getInt(cursor
	                .getColumnIndex(MediaStore.MediaColumns._ID));
	        Uri baseUri = Uri.parse("content://media/external/images/media");
	        return Uri.withAppendedPath(baseUri, "" + id);
	    } else {
	        if (imageFile.exists()) {
	            ContentValues values = new ContentValues();
	            values.put(MediaStore.Images.Media.DATA, filePath);
	            return getApplicationContext().getContentResolver().insert(
	                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	        } else {
	            return null;
	        }
	    }
	}
	public void confirmDialog(final Activity activity){
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	builder.setTitle("Alert!");
    	builder.setMessage("This many images may not support selected Application .\nAre you sure you want Share?");
    	builder.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				multiShare();
			}
		});
    	builder.setNegativeButton("No", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				onBackPressed();
			}
		});
    	builder.show();
    }
}
