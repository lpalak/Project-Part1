package com.example.shareimage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class ShowImage extends Activity {
	private ImageView showImage;
	private String intentUri;
	private Uri uri;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show__image);
		sharedPreferences = getSharedPreferences("com.imageshareing", Context.MODE_PRIVATE);
		showImage = (ImageView) findViewById(R.id.imgShowImage);
		intentUri = this.getIntent().getStringExtra("uri") == null?"" : this.getIntent().getStringExtra("uri");
		
		if (!intentUri.isEmpty()) {
			uri = Uri.parse(intentUri);
			showImage.setImageURI(uri);
//			sharedPreferences.edit().putString("lastimg", intentUri).commit();
		}
	}
	public void share (View v){
//	    File filePath = getFileStreamPath("shareimage.jpg");  //optional //internal storage
	     Intent shareIntent = new Intent();
	     shareIntent.setAction(Intent.ACTION_SEND);
	     shareIntent.putExtra(Intent.EXTRA_TEXT, "");
//	     shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(filePath)));  //optional//use this when you want to send an image
	     shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
	     shareIntent.setType("image/jpeg");
	     shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	     startActivity(Intent.createChooser(shareIntent, "send"));
	}
/*	File f = new  File(uri.getPath());
	if (f != null && f.exists()) {
		Bitmap thumbnail = ImageFilePath.decodeSampledBitmapFromResource(f.getAbsolutePath(), 250, 300); 
		if (thumbnail != null)
		showImage.setImageBitmap(thumbnail);*/
}
