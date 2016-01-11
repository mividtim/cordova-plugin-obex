package com.mividstudios.cordova;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OBEX extends CordovaPlugin {

    private String deviceAddress;

    @Override
    public void initialize(CordovaInterface cordova, WebView webView) {
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
            String message = args.getString(0);
            this.opp(message, callbackContext);
            return true;
        }
        return false;
    }

    private void opp(String imagePath, CallbackContext callbackContext) {
        if(message != null && message.length() > 0) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.setPackage("com.android.bluetooth");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
            startActivity(Intent.createChooser(intent, "Choose printer"));
            callbackContext.success(message);
        }
        else
            callbackContext.error("Expected one non-empty string argument.");
    }
}

