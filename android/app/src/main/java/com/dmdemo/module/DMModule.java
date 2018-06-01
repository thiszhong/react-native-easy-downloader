package com.dmdemo.module;

import android.os.Environment;
import android.widget.Toast;
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

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

public class DMModule extends ReactContextBaseJavaModule {

  private static final String DIR_DOWNLOAD_KEY = "DIR_DOWNLOAD";
  private long taskId = 0 ;  
  private DownloadManager downloadManager;
  private ReactApplicationContext contect;

  public DMModule(ReactApplicationContext reactContext) {
    super(reactContext);
    contect = reactContext;
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
  public void download(final String url, final String name, final String description) {
    //创建目录
    // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir() ; 
    // String dirDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    //注册广播接收器
    IntentFilter filter = new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE );   
    contect.registerReceiver( receiver , filter ) ;

    DownloadManager.Request request;
    try {
      request = new DownloadManager.Request(Uri.parse(url));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    request.setTitle(name);
    request.setDescription(description);
    //在通知栏显示下载进度
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      request.allowScanningByMediaScanner();
      request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    }
    //设置保存下载apk保存路径
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "qhztest.apk");
    //设置可以被系统的Downloads应用扫描到并管理
    request.setVisibleInDownloadsUi( true ) ;
    //设置请求的Mime
    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(url));
    //开始下载
    downloadManager = (DownloadManager)contect.getSystemService(Context.DOWNLOAD_SERVICE);
    taskId = downloadManager.enqueue( request );
  }

  /**
   * 广播接受器, 下载完成监听器
   */
  BroadcastReceiver receiver = new BroadcastReceiver() {   
    @Override     
    public void onReceive(Context context, Intent intent) { 
        String action = intent.getAction() ;
        if( action.equals( DownloadManager.ACTION_DOWNLOAD_COMPLETE  )){
            //下载完成了
            //获取当前完成任务的ID
            long  doneDownloadId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID , -1 );
            if(doneDownloadId != taskId) {
              return;
            }
            try {
              downloadManager.openDownloadedFile(doneDownloadId);
            } catch (Exception e) {
              e.printStackTrace();
            }
            //自动安装应用
            Toast.makeText(getReactApplicationContext(), "count", Toast.LENGTH_SHORT).show();
            
        }
    }   
  };

  @ReactMethod
  public void installApk(final String url) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setDataAndType(url, "application/vnd.android.package-archive");
    contect.startActivity(intent);
  }
}