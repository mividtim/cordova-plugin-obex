package com.mividstudios.cordova;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
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
            String imagePath = args.getString(0);
            this.opp(imagePath, callbackContext);
            return true;
        }
        return false;
    }

    public static Intent createChooser(PackageManager packageManager, Intent intent, String title) {
        final List<Intent> filtered = filterShareIntent(packageManager, intent);
        final Intent chooser = Intent.createChooser(filtered.remove(filtered.size() - 1), title);
        return chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, filtered.toArray(new Parcelable[] {}));
    }

    private static List<Intent> filterShareIntent(PackageManager packageManager, Intent intent) {
        final List<Intent> intentList = new ArrayList<Intent>();
        final List<ResolveInfo> possible = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if(possible == null || possible.isEmpty()) {
            intentList.add(intent);
            return intentList;
        }
        for(ResolveInfo resolveInfo : possible) {
            if(resolveInfo.activityInfo.name.equals("com.android.bluetooth.opp.BluetoothOppLauncherActivity"))
                intentList.add(createSameIntent(intent, resolveInfo));
        }
        return intentList;
    }

    private static Intent createSameIntent(Intent source, ResolveInfo resolveInfo) {
        final Intent intent = new Intent(source.getAction());
        intent.setType(source.getType());
        intent.putExtras(source.getExtras());
        intent.setPackage(resolveInfo.activityInfo.packageName);
        intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        return intent;
    }

    private void opp(String imagePath, CallbackContext callbackContext) {
        if(imagePath != null && imagePath.length() > 0) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("image/*");
            sendIntent.setPackage("com.android.bluetooth");
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
            Intent printIntent = createChooser(cordova.getActivity().getPackageManager(), sendIntent, "Choose Printer");
            cordova.startActivityForResult(this, printIntent, 0);
            callbackContext.success(imagePath);
        }
        else
            callbackContext.error("Expected one non-empty string argument.");
    }
}

