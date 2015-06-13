package com.example.shareimage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.classes.Action;
import com.example.classes.CustomGallery;
import com.example.classes.ImageFilePath;
import com.example.classes.Shared;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button btnCamera,btnGallery,btnShareApp,btnQuit,btnMulti,btnChat,btnVideoChat;
	private File f;
	private Uri selectedImageUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		btnCamera = (Button) findViewById(R.id.btnCamera);
		btnGallery = (Button) findViewById(R.id.btnGallery);
		btnShareApp = (Button) findViewById(R.id.btnShareApp);
		btnQuit = (Button) findViewById(R.id.btnQuit);
		btnMulti= (Button) findViewById(R.id.btnMulti);
		btnChat= (Button) findViewById(R.id.btnChat);
		btnVideoChat= (Button) findViewById(R.id.btnVideoChat);
		
		btnCamera.setOnClickListener(this);
		btnGallery.setOnClickListener(this);
		btnMulti.setOnClickListener(this);
		btnShareApp.setOnClickListener(this);
		btnQuit.setOnClickListener(this);
		btnChat.setOnClickListener(this);
		btnVideoChat.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		
		if(v == btnCamera){
			 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
             File folder = new File(Environment.getExternalStorageDirectory() + "/ShareImage/");
             if(!folder.exists())
             {
                 folder.mkdirs();
             }         
             final Calendar c = Calendar.getInstance();
             String new_Date = c.get(Calendar.DAY_OF_MONTH)+"-"+((c.get(Calendar.MONTH))+1)   +"-"+c.get(Calendar.YEAR) +" " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE)+ "-"+ c.get(Calendar.SECOND);
             String path=String.format(Environment.getExternalStorageDirectory() +"/ShareImage/%s.jpg","Share("+new_Date+")");
             f = new File(path); 
             selectedImageUri =Uri.fromFile(f);
             intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri); 
             startActivityForResult(intent, 1);
		}else if(v == btnGallery) {
			 Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
             startActivityForResult(intent, 2);
			
		}else if(v == btnMulti) {
			Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
			startActivityForResult(i, 200);
			
		}else if(v == btnShareApp) {
			String urlMonarchApp = "https://sites.google.com/site/ee545finalresearch/ee442_shareimage-apk";
		Intent	i = new Intent();
			i.setAction(Intent.ACTION_SEND);
			i.putExtra(Intent.EXTRA_TEXT,
					"Download Image Sharing Mobile Application"
					+ "\nfor your ANDROID Smartphone. Download it from:\n"
					+ urlMonarchApp
					+"\nHelpline:\n626-677-1964-Dashang");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setType("text/plain");
			startActivity(Intent.createChooser(i, "Select an Action"));
		}else if(v == btnChat) {
			Intent i4=new Intent(Intent.ACTION_MAIN);

			PackageManager manager = getPackageManager();

			i4 = manager.getLaunchIntentForPackage("com.quickblox.sample.chat");//apk name

			i4.addCategory(Intent.CATEGORY_LAUNCHER);

			startActivity(i4);

		}
		else if(v == btnVideoChat) {
			Intent i4=new Intent(Intent.ACTION_MAIN);

			PackageManager manager = getPackageManager();

			i4 = manager.getLaunchIntentForPackage("com.quickblox.sample.videochatwebrtcnew");//apk name

			i4.addCategory(Intent.CATEGORY_LAUNCHER);

			startActivity(i4);

		}else if(v == btnQuit) {
			finish();
		}
	}  
	   @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
		   if (resultCode == RESULT_OK) {
	            if (requestCode == 1 && data != null) {
				try {
		            Bitmap bitmap;
		            bitmap  = ImageFilePath.decodeSampledBitmapFromResource(f.getPath(), 1000, 1000);
		            OutputStream outFile = null;
					outFile = new FileOutputStream(f);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
					bitmap = Shared.rotateImage(getApplicationContext(),Uri.fromFile(f), bitmap);

					outFile.flush();
					outFile.close();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }  else if (requestCode == 2) {
	 
	                Uri selectedImage = data.getData();
	                if (selectedImage != null) {
	                	selectedImageUri =selectedImage;
		                String[] filePath = { MediaStore.Images.Media.DATA };
		                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
		                c.moveToFirst();
		                int columnIndex = c.getColumnIndex(filePath[0]);
		                String picturePath = c.getString(columnIndex);
		                c.close();
//		                Bitmap thumbnail = ImageFilePath.decodeSampledBitmapFromResource(picturePath, 80, 70); 
//		                		(BitmapFactory.decodeFile(picturePath));
		                Log.w("path of image from gallery......******************.........", picturePath+"");
					}else{
						Toast.makeText(getApplicationContext(), "No Image Found", Toast.LENGTH_LONG).show();
						
					}
	            }	 if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
	    			String[] all_path = data.getStringArrayExtra("all_path");

	    			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

	    			for (String string : all_path) {
	    				CustomGallery item = new CustomGallery();
	    				item.sdcardPath = string;

	    				dataT.add(item);
	    			}
//	    			viewSwitcher.setDisplayedChild(0);
//	    			adapter.addAll(dataT);
	    		}
	            
	            if (selectedImageUri.toString() != null && !selectedImageUri.toString().equals("")) {
	            	Intent i  =  new  Intent(MainActivity.this, ShowImage.class);
		            i.putExtra("uri", selectedImageUri.toString());
		            startActivity(i);
				}
	            
	        }
	    }
}
