package com.mividstudios.cordova;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.os.Environment;
import android.util.Log;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OBEX extends CordovaPlugin {

    private String deviceAddress;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(
        String action,
        JSONArray args,
        CallbackContext callbackContext)
    throws JSONException {
        if("opp".equals(action)) {
            String imageUri = args.getString(0);
            this.opp(imageUri, callbackContext);
            return true;
        }
        return false;
    }

    private static Intent getOppIntent(PackageManager packageManager, Intent intent) {
        final List<ResolveInfo> possible = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo resolveInfo : possible)
            if(resolveInfo.activityInfo.name.equals("com.android.bluetooth.opp.BluetoothOppLauncherActivity"))
                return createSameIntent(intent, resolveInfo);
        return intent;
    }

    private static Intent createSameIntent(Intent source, ResolveInfo resolveInfo) {
        final Intent intent = new Intent(source.getAction());
        intent.setType(source.getType());
        intent.putExtras(source.getExtras());
        intent.setPackage(resolveInfo.activityInfo.packageName);
        intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        return intent;
    }

    private File saveBase64Image(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image.getBytes());
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/DCIM/Camera");
        if(!dir.exists())
            dir.mkdirs();
        String imageFileName = dir.getAbsolutePath() + "/tempImage.jpg";
        File imageFile = new File(imageFileName);
        if(imageFile.exists())
          imageFile.delete();
        try {
          imageFile.createNewFile();
          FileOutputStream ostream = new FileOutputStream(imageFile);
          ostream.write(decodedBytes);
          ostream.close();
          return imageFile;
        }
        catch(IOException ex) {
            return null;
        }
    }

    private void opp(String base64Image, CallbackContext callbackContext) {
        if(base64Image != null && base64Image.length() > 0) {
            File imageFile = saveBase64Image(base64Image);
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("image/*");
            sendIntent.setPackage("com.android.bluetooth");
            Uri uri = Uri.fromFile(imageFile);
            Log.d("obex", uri.toString());
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            Intent printIntent = getOppIntent(cordova.getActivity().getPackageManager(), sendIntent);
            cordova.startActivityForResult(this, printIntent, 0);
            callbackContext.success(imageFile.getAbsolutePath());
        }
        else
            callbackContext.error("Expected one non-empty string argument.");
    }
}

