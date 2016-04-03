//** Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.**
//**This file is originally apart of Folio for Facebook.**
//**Copyright notice must remain here if you're using any part of this code.**
//**Some code taken from Tinfoil for Facebook**
//**Some code taken from FaceSlim**
//**Some code taken from Metal for Facebook & Twitter**
//**Some code taken from FBwrapper**

package com.creativtrendz.folio.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.Toast;

import com.creativetrends.folio.app.R;
import com.creativtrendz.folio.utils.Miscellany;

import java.io.File;
import java.io.IOException;



public class GoogleActivity extends AppCompatActivity {
	
	
    public Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private static final int REQUEST_STORAGE = 1;   
    private WebView webView;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    
        
          
     
         
    private static final String TAG = GoogleActivity.class.getSimpleName();
	
    
    @SuppressWarnings({ "unused", "deprecation" })
	@Override
    @SuppressLint({ "setJavaScriptEnabled", "CutPasteId", "ClickableViewAccessibility" })
    protected void onCreate(Bundle savedInstanceState) {    
            super.onCreate(savedInstanceState);
            setContentView(R.layout.google_plus);


        NavigationView navigationView = (NavigationView) findViewById(R.id.google_drawer);
        
            
     
        
          
        String webViewUrl = "https://plus.google.com";
        
               
     
        webView = (WebView) findViewById(R.id.webViewG);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setGeolocationEnabled(true);  
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true); 
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);		
        webView.setVerticalScrollBarEnabled(false);        
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);                
        webView.getSettings().setPluginState(PluginState.ON);
        webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); 
        webView.setOnTouchListener(new View.OnTouchListener() 
		{ 
		       
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
			           switch (event.getAction()) 
			           { 
			               case MotionEvent.ACTION_DOWN:
			            	   
			            	 
			            	   
			               case MotionEvent.ACTION_UP:
			            	   
			            	 
			            	   
			            	   
			               	            	   
			            	   
			           
				              					       		     
			                   if (!v.hasFocus()) 
			                	   
			                	   
			                   { 
			                       v.requestFocus(); 
			                   }  
			                   break; 
			           } 
			           return false; 
			        }		
			});
	            
        
        
                    
        
       

     	        
               
        webView.loadUrl(webViewUrl);
        webView.setWebViewClient(new GoogleWebView(){
        	
        	
        	 @Override
				public void onPageStarted(WebView view, String url, Bitmap favicon)
              {
        		
        		 
        		        		 
              }
        	
        	
        	 @Override
            	public void onPageFinished(WebView view, String url){
        		 super.onPageFinished(view, url);
        		 
        		            		 
             		
            	 }});
        
           	 
    	
        	   
        
        webView.setWebChromeClient(new WebChromeClient(){ 
        	
        	        	
        	        	        	
        	@Override
    		public void onGeolocationPermissionsShowPrompt(String origin,
    		Callback callback) {
    		
    		super.onGeolocationPermissionsShowPrompt(origin, callback);
    		callback.invoke(origin, true, false);
    		}

    	
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    // create the file where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }

                    // continue only if the file was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                return true;
            }

            // creating image files (Lollipop only)
            private File createImageFile() throws IOException {

                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Folio");

                if (!imageStorageDir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    imageStorageDir.mkdirs();
                }

                // create an image file name
                imageStorageDir  = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;

                try {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Folio");

                    if (!imageStorageDir.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        imageStorageDir.mkdirs();
                    }

                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                    mCapturedImageURI = Uri.fromFile(file); // save to the private variable

                    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(i, getString(R.string.image_chooser));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                } catch (Exception e) {
                    Snackbar.make( drawerLayout, "Camera Exception:" + e, Snackbar.LENGTH_SHORT).show();
                }

            }
            
            

            // not needed but let's make it overloaded just in case
            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // openFileChooser for other Android versions
            /** may not work on KitKat due to lack of implementation of openFileChooser() or onShowFileChooser()
             *  https://code.google.com/p/android/issues/detail?id=62220
             *  however newer versions of KitKat fixed it on some devices */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

        });
        
       
                 
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                drawerLayout.closeDrawers();


                switch (menuItem.getItemId()) {

                    case R.id.folio:
                        Intent folio = new Intent(GoogleActivity.this, MainActivity.class);
                        startActivity(folio);
                        return true;

                    case R.id.bugs:
                        Intent bugIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "bugs@creativetrendsapps.comcreativetrendz85@gmail.com", null));
                        bugIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Bug");
                        bugIntent.putExtra(Intent.EXTRA_TEXT, "I found a bug in Folio \n\n--" + Miscellany.getDeviceInfo(GoogleActivity.this));
                        startActivity(Intent.createChooser(bugIntent, getString(R.string.choose_email_client)));
                        return true;

                    case R.id.helpfeedback:
                        Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "feedback@creativetrendsapps.com", null));
                        feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Feedback");
                        feedbackIntent.putExtra(Intent.EXTRA_TEXT, "Here is some awesome feedback for " + getString(R.string.app_name));
                        startActivity(Intent.createChooser(feedbackIntent, getString(R.string.choose_email_client)));
                        return true;


                    default:

                        return true;
                }
            }
        });

        
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_google);
        
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

        	
            
        
        	
            @Override
            public void onDrawerClosed(View drawerView) {
                
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                

                super.onDrawerOpened(drawerView);
                
                super.onDrawerSlide(drawerView, 0);
            }
        };

       
        
        
	


       
        actionBarDrawerToggle.syncState();

        }
        
  
      
    
        
    
        
    



    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dummy_menu, menu);
        return true;
    }
    
 
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       
  
            return super.onOptionsItemSelected(item);
    }


    
    
    
   
    @SuppressWarnings("unused")
	private boolean requestStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(this, storagePermission);
        String[] permissions = new String[] { storagePermission };
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
           
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
            return false;
        } else {
           
            return true;
        }
    }

    @SuppressLint("NewApi")
	@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   
                    
                } else {
                   
                    Snackbar noPermission = Snackbar .make( drawerLayout, getString(R.string.permission_not_granted, Snackbar.LENGTH_SHORT), Snackbar.LENGTH_LONG);
                    noPermission.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    
    
       
    
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
         
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                if (requestCode == FILECHOOSER_RESULTCODE) {
                    if (null == this.mUploadMessage)
                        return;

                    Uri result = null;

                    try {
                        if (resultCode != RESULT_OK)
                            result = null;
                        else {
                            // retrieve from the private variable if the intent is null
                            result = data == null ? mCapturedImageURI : data.getData();
                        }
                    }
                    catch(Exception e) {
                        Toast.makeText(getApplicationContext(), "activity :"+e, Toast.LENGTH_LONG).show();
                    }

                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }

            } // end of code for all versions except of Lollipop

            // start of code for Lollipop only
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                }

                Uri[] results = null;

                // check that the response is a good one
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null || data.getData() == null) {
                        // if there is not data, then we may have taken a photo
                        if (mCameraPhotoPath != null) {
                            results = new Uri[] {Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[] {Uri.parse(dataString)};
                        }
                    }
                }

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;

            }
            
    	}

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);


        String webViewUrl = getIntent().getDataString();

        String sharedSubject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
        String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);


        if (sharedUrl != null) {
            if (!sharedUrl.equals("")) {

                if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {

                    int startUrlIndex = sharedUrl.indexOf("http:");
                    if (startUrlIndex > 0) {

                        sharedUrl = sharedUrl.substring(startUrlIndex);
                    }
                }

                webViewUrl = String.format("https://plus.google.com/share?&u=%s&t=%s", sharedUrl, sharedSubject);

                webViewUrl = Uri.parse(webViewUrl).toString();

            }
        }

        try {
            if (getIntent().getExtras().getString("start_url") != null)
                webViewUrl = getIntent().getExtras().getString("start_url");

        } catch (Exception ignored) {
        }

            webView.loadUrl(webViewUrl);


        }


       
   
    
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
               
        }


    @Override
    protected void onPause() {
        super.onPause();
        
       
    }


    @Override
    public void onDestroy() {    
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();  
       
    }


}