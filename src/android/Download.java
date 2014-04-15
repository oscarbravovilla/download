package com.vodafone.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

public class Download extends CordovaPlugin {
	
	public static final String OPERATOR_NAME = "downLoadFile";

   @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	
        if (OPERATOR_NAME.equals(action)){
		
            try
			{
				
				PluginResult pluginResult 	=  downloadUrl(args.getString(0), args.getString(1), args.getString(2), args.getString(3), args.getString(4));
				pluginResult.setKeepCallback(true);
				// resultado del plugin
				callbackContext.sendPluginResult(pluginResult);
				return true;
			}
			catch (Exception e)
			{
				System.err.println("Exception: " + e.getMessage());
				callbackContext.error(e.getMessage());
				return false;
			}
            
        }
        return false;
    }

 	private PluginResult downloadUrl(String fileUrl, String dirName, String fileName, String overwrite, String idSesion) {
		try {

			Log.d("DownloaderPlugin", "DIRECTORY CALLED " + dirName + " created");

			if(dirName == null || dirName.trim().length() == 0){				
				if(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
					String downloadName = "Download";
					if (Build.VERSION.SDK_INT >= 8) {
						downloadName = getDirectoryDownload8();
					} 
					dirName = Environment.getExternalStorageDirectory().getPath() + "/" + downloadName;
				}
				else{
					return new PluginResult(PluginResult.Status.ERROR, "ERROR_SD_UNAVAILABLE");
				}
			}
						
			File dir = new File(dirName);

			if (!dir.exists()) {
				Log.d("DownloaderPlugin", "directory " + dirName + " created");
				dir.mkdirs();
			}
			
			File file = new File(dirName + "/" +fileName);

			if (overwrite.equals("false") && file.exists()) {
				Log.d("DownloaderPlugin", "File already exist");
				return new PluginResult(PluginResult.Status.OK, "FILE_EXIST");
			}
			
			//Descargamos el archivo
			InputStream is = null;

			try {
						
				URL url = new URL(fileUrl);
				HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
				ucon.setRequestMethod("GET");
				ucon.setRequestProperty("Cookie", "JSESSIONID="+idSesion);
				ucon.setDoOutput(true);
				ucon.connect();
				
				Log.d("DownloaderPlugin", "download begining url: " + url);
	
				is = ucon.getInputStream();
			
			}catch (IOException e) {

				Log.d("DownloaderPlugin", "Error: " + e);
				return new PluginResult(PluginResult.Status.ERROR, "ERROR_DOWNLOADING");
			}
			
			byte[] buffer = new byte[1024];

			int len1 = 0;

			FileOutputStream fos = new FileOutputStream(file);

			while ( (len1 = is.read(buffer)) > 0 ) {
				fos.write(buffer,0, len1);
			}

			fos.close();

			Log.d("DownloaderPlugin", "Download complete in" + fileName);

		} catch (Exception e) {

			Log.d("DownloaderPlugin", "Error: " + e);
			return new PluginResult(PluginResult.Status.ERROR, "ERROR_GENERIC");

		}

		return new PluginResult(PluginResult.Status.OK, dirName + "/" +fileName);
	}
	
	@TargetApi(8)
	private String getDirectoryDownload8(){
		return Environment.DIRECTORY_DOWNLOADS;
	}
}
