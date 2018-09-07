package com.mayenjoy.RNDownloadManager;

import android.os.Environment;
import android.database.Cursor;
// import android.widget.Toast;
import android.webkit.MimeTypeMap;
import android.content.IntentFilter;
import android.os.Build;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.app.DownloadManager;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Promise;

public class RNDMModule extends ReactContextBaseJavaModule {

  private long taskId = 0;
  private DownloadManager downloadManager;
  private ReactApplicationContext rContext;
  private Boolean isAutoInstall = false;
  // 支持多任务同时进行
  private Map<Long, Promise> taskPromises = new HashMap<>();
  private Map<Long, Boolean> taskAutoInstalls = new HashMap<>();

  public RNDMModule(ReactApplicationContext reactContext) {
    super(reactContext);
    rContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNDownloadManager";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("DirDownload", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
    constants.put("DirExternalStorage", Environment.getExternalStorageDirectory().getPath());
    return constants;
  }

  @ReactMethod
  public void download(final ReadableMap options, final Promise promise) {
    String url;
    if (options.hasKey("url")) {
      url = options.getString("url");
    } else {
      promise.reject("Error", "Missing the download url");
      return;
    }
    String title = options.hasKey("title") ? options.getString("title") : null;
    String description = options.hasKey("description") ? options.getString("description") : null;
    String savePath = options.hasKey("savePath") ? options.getString("savePath") : null;
    // isAutoInstall = options.hasKey("autoInstall") ? options.getBoolean("autoInstall") : false;
    
    // 注册广播接收器
    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    rContext.registerReceiver(dmReceiver, filter);
      
    DownloadManager.Request request;
    try {
      request = new DownloadManager.Request(Uri.parse(url));
    } catch (Exception e) {
      e.printStackTrace();
      promise.reject("Error", e.getMessage());
      return;
    }
    if(title != null) {
      request.setTitle(title);
    }
    if(description != null) {
      request.setDescription(description);
    }
    // 在通知栏显示下载进度
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      request.allowScanningByMediaScanner();
      request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    }
    // 设置保存下载apk保存路径
    if (savePath != null) {
      request.setDestinationUri(Uri.fromFile(new File(savePath)));
    }
    // 设置可以被系统的Downloads应用扫描到并管理
    request.setVisibleInDownloadsUi(true);
    // 设置请求的Mime
    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(url));
    // 开始下载
    downloadManager = (DownloadManager) rContext.getSystemService(Context.DOWNLOAD_SERVICE);
    taskId = downloadManager.enqueue(request);
    taskPromises.put(taskId, promise);
    if(options.hasKey("autoInstall")) {
      taskAutoInstalls.put(taskId, options.getBoolean("autoInstall"));
    }
  }

  /**
   * 广播接受器, 下载完成监听器
   */
  BroadcastReceiver dmReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
        // 下载完成了
        // 获取当前完成任务的ID
        long doneDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        Promise thisPromise = taskPromises.get(doneDownloadId);
        if(thisPromise == null) {
          return;
        }
        // 自动安装应用
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(doneDownloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
          int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
          // 下载失败也会返回这个广播，所以要判断下是否真的下载成功
          if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
            int fileNameId;
            String filePath;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
              String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
              filePath = Uri.parse(fileUri).getPath();
            } else {
              /**Android 7.0的方式：请求获取写入权限，这一步报错**/
              fileNameId = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
              filePath = cursor.getString(fileNameId);
            }
            
            // 是否安装
            if(taskAutoInstalls.get(doneDownloadId) != null) {
              installApk(filePath, thisPromise);
            }else {
              thisPromise.resolve(filePath);
            }
          }
        }else {
          thisPromise.reject("Error", "Something wrong when getting the file downloaded");
        }
      }
    }
  };

  @ReactMethod
  public void installApk(final String url, final Promise promise) {
    Uri apkuri;
    try {
      File apk = new File(url);
      if (apk.exists()) {
        apkuri = Uri.fromFile(apk);
      } else {
        promise.reject("Error", "File not exists");
        return;
      }
    } catch (Exception e) {
      promise.reject("Error", e.getMessage());
      return;
    }
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setDataAndType(apkuri, "application/vnd.android.package-archive");
    promise.resolve("success");
    rContext.startActivity(intent);
  }

}