package com.dmdemo.module;

import android.os.Environment;
import android.widget.Toast;

import java.util.Map;
import java.util.HashMap;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

public class DMModule extends ReactContextBaseJavaModule {

  private static final String DIR_DOWNLOAD_KEY = "DIR_DOWNLOAD";

  public DMModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "MyDownload";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(DIR_DOWNLOAD_KEY, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
    return constants;
  }

  @ReactMethod
  public void download(final ReadableMap params) {
    Toast.makeText(getReactApplicationContext(), params.toString(), Toast.LENGTH_SHORT).show();
  }
}