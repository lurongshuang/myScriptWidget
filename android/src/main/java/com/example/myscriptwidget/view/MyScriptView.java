package com.example.myscriptwidget.view;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myscriptwidget.R;
import com.example.myscriptwidget.bean.EditorViewBean;
import com.example.myscriptwidget.bean.JSONData;
import com.example.myscriptwidget.engine.MyScriptEngine;
import com.google.gson.Gson;
import com.myscript.iink.ContentBlock;
import com.myscript.iink.ContentPart;
import com.myscript.iink.Editor;
import com.myscript.iink.EditorError;
import com.myscript.iink.IEditorListener;
import com.myscript.iink.MimeType;

import java.io.IOException;
import java.util.Map;

import io.flutter.Log;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.platform.PlatformView;

public class MyScriptView implements PlatformView {
    private final Context context;
    private BinaryMessenger messenger;
    private final int viewId;
    private Object args;

    private View myView;

    private final String TAG = "MyScriptView";

    private TextUpdate textUpdate;

    public MyScriptView(Context context, BinaryMessenger messenger, int viewId, Object args, TextUpdate textUpdate) {
        this.context = context;
        this.messenger = messenger;
        this.viewId = viewId;
        this.args = args;
        this.textUpdate = textUpdate;
    }

    @Nullable
    @Override
    public View getView() {
        ///返回原生视图对象。当Flutter需要显示PlatformView时，会调用此方法来获取原生视图，然后将其嵌入到Flutter界面中。
        if (myView != null) {
            return myView;
        }
        if (MyScriptEngine.getEngine() == null) {
            TextView textView = new TextView(context);
            textView.setText("初始化失败 engine is null");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(context.getColor(R.color.control_gray));
            }
            textView.setGravity(Gravity.CENTER);
            return textView;
        }
        EditorViewBean editorViewBean = MyScriptEngine.getEditorView(context, viewId, (Map<String, Object>) args);
        myView = editorViewBean.editorViewInflate;
        Editor editor = editorViewBean.editor;
        editor.addListener(new IEditorListener() {
            @Override
            public void partChanging(@NonNull Editor editor, ContentPart oldPart, ContentPart newPart) {
                // no-op
                android.util.Log.e("MyScriptView", "partChanging");
            }

            @Override
            public void partChanged(@NonNull Editor editor) {
                android.util.Log.e("MyScriptView", "partChanged");
            }

            @Override
            public void contentChanged(@NonNull Editor editor, @NonNull String[] blockIds) {
                for (String blockId : blockIds) {
                    String label = "";
                    ContentBlock contentBlock = editor.getBlockById(blockId);
                    try {
                        String jiixString = editor.export_(contentBlock, MimeType.JIIX, MyScriptEngine.getEditorViewBean(viewId).exportParams);
                        Gson gson = new Gson();
                        JSONData jsonElement = gson.fromJson(jiixString, JSONData.class);
                        label = jsonElement.getLabel();
                        android.util.Log.w(TAG, label);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (textUpdate != null) {
                        textUpdate.onEvent(label);
                    }
                    Log.w("editor", label);
                }


            }

            @Override
            public void onError(@NonNull Editor editor, @NonNull String blockId, @NonNull EditorError error, @NonNull String message) {
                android.util.Log.e(TAG, "Failed to edit block \"" + blockId + "\"" + message);
            }

            @Override
            public void selectionChanged(@NonNull Editor editor) {
                // no-op
                android.util.Log.e("MyScriptView", "selectionChanged");
            }

            @Override
            public void activeBlockChanged(@NonNull Editor editor, @NonNull String blockId) {
                // no-op
                android.util.Log.e("MyScriptView", "activeBlockChanged");
            }
        });

        return myView;
    }

    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {
        ///当PlatformView被附加到Flutter视图层级时，即将显示在Flutter界面上时，
        // 会调用此方法。您可以在此方法中执行一些初始化操作，或者监听与Flutter视图的交互事件。
        PlatformView.super.onFlutterViewAttached(flutterView);
    }

    @Override
    public void onFlutterViewDetached() {
        //当PlatformView从Flutter视图层级中分离（不再显示）时，
        //会调用此方法。您可以在此方法中执行一些清理操作，停止监听与Flutter视图的交互事件。
        PlatformView.super.onFlutterViewDetached();
    }

    @Override
    public void dispose() {
        ///当PlatformView需要被销毁时，会调用此方法。
        // 您应该在这里释放与PlatformView相关的资源，确保不会出现内存泄漏
        Log.e("MyScriptView", "dispose");
        MyScriptEngine.disposeView();
    }

    @Override
    public void onInputConnectionLocked() {
        //当Flutter视图需要锁定输入连接时，会调用此方法。
        // 这可以发生在需要处理输入事件时，例如文本输入。您可以在此方法中实现处理输入事件所需的逻辑。
        PlatformView.super.onInputConnectionLocked();
    }

    @Override
    public void onInputConnectionUnlocked() {
        ///当Flutter视图不再需要锁定输入连接时，会调用此方法。您可以在此方法中实现停止处理输入事件所需的逻辑。
        //这些方法允许您与Flutter视图进行交互并管理PlatformView的生命周期。您可以根据需要在这些方法中添加逻辑，
        // 以确保PlatformView在Flutter中正确地显示和交互。
        PlatformView.super.onInputConnectionUnlocked();
    }
}

