package com.mividstudios.cordova;

import java.io.File;
import android.content.Intent;
import android.net.Uri;
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
        // TBD: Get device address and place in deviceAddress local var
    }

    @Override
    public boolean execute(
        String action,
        JSONArray args,
        CallbackContext callbackContext)
    throws JSONException {
        if("opp".equals(action)) {
            String imagePath = args.getString(0);
            this.opp(imagePath, callbackContext);
            return true;
        }
        return false;
    }

    private void opp(String imagePath, CallbackContext callbackContext) {
        if(imagePath != null && imagePath.length() > 0) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.setPackage("com.android.bluetooth");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
            cordova.getActivity().getApplicationContext().startActivity(Intent.createChooser(intent, "Choose printer"));
            callbackContext.success(imagePath);
        }
        else
            callbackContext.error("Expected one non-empty string argument.");
    }
}

