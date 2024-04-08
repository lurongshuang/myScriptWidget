package com.example.myscriptwidget;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.myscriptwidget.engine.MyScriptEngine;
import com.example.myscriptwidget.view.MyScriptViewFactory;
import com.example.myscriptwidget.view.TextUpdate;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * MyscriptwidgetPlugin
 */
public class MyscriptwidgetPlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, TextUpdate {
    private MethodChannel channel;

    private EventChannel eventChannel;

    private EventChannel.EventSink eventSink;

    private FlutterPluginBinding flutterPluginBinding;

    private MyScriptViewFactory scriptViewFactory;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.example.myScriptWidget_example.myScriptWidgetMethodChannel");
        channel.setMethodCallHandler(this);
        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "com.example.myScriptWidget_example.myScriptWidgetEventChannel");
        eventChannel.setStreamHandler(this);

        scriptViewFactory = new MyScriptViewFactory(flutterPluginBinding.getBinaryMessenger(), this);
        //注册组件
        flutterPluginBinding
                .getPlatformViewRegistry()
                .registerViewFactory(
                        MyScriptViewFactory.viewTypeId,
                        scriptViewFactory);
        this.flutterPluginBinding = flutterPluginBinding;

    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + Build.VERSION.RELEASE);
        } else if (call.method.equals("clear")) {
            scriptViewFactory.clear();
            result.success("success");
        } else if (call.method.equals("initMyScriptWidget")) {
            try {
                MyScriptEngine.configPath = call.argument("configPath");
                List<Integer> myArray = call.argument("myCertificate");
                List<Byte> bytes = new ArrayList<>();
                for (int i = 0; i < myArray.size(); i++) {
                    byte b = Byte.valueOf(myArray.get(i).toString());
                    bytes.add(b);
                }
                MyScriptEngine.myCertificate = listTobyte(bytes);
                MyScriptEngine.initEngine();
                result.success("success");
            } catch (Exception e) {
                Log.e("MyscriptwidgetPlugin", e.toString());
                result.error("-1", e.getMessage(), e.fillInStackTrace());
            }
        } else if (call.method.equals("getChannel")) {
            try {
                ApplicationInfo appInfo = flutterPluginBinding.getApplicationContext().getPackageManager().getApplicationInfo(
                        flutterPluginBinding.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
                String channel = appInfo.metaData.getString("CHANNEL_NAME");
                if (channel == null) {
                    channel = "default";
                }
                result.success(channel);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                result.error(String.valueOf(-1), e.getMessage(), e.fillInStackTrace());
            }

        } else {
            result.notImplemented();
        }
    }

    private byte[] listTobyte(List<Byte> list) {
        if (list == null || list.size() < 0)
            return null;
        byte[] bytes = new byte[list.size()];
        int i = 0;
        Iterator<Byte> iterator = list.iterator();
        while (iterator.hasNext()) {
            bytes[i] = iterator.next();
            i++;
        }
        return bytes;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;
    }

    @Override
    public void onEvent(String text) {
        if (eventSink != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                eventSink.success(text);
            });
        }
    }
}
