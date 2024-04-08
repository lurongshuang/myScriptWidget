package com.example.myscriptwidget.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myscriptwidget.bean.EditorViewBean;
import com.example.myscriptwidget.engine.MyScriptEngine;


import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class MyScriptViewFactory extends PlatformViewFactory {

    private final BinaryMessenger messenger;
    public static String viewTypeId = "com.example.myscriptwidget.view";

    private TextUpdate textUpdate;

    private int viewId;

    public MyScriptViewFactory(BinaryMessenger messenger, TextUpdate textUpdate) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
        this.textUpdate = textUpdate;
    }

    @NonNull
    @Override
    public PlatformView create(Context context, int viewId, @Nullable Object args) {
        MyScriptEngine.initEngine();
        this.viewId = viewId;
        return new MyScriptView(context, messenger, viewId, args, textUpdate);
    }

    public void clear() {
        EditorViewBean editorViewBean = MyScriptEngine.getEditorViewBean(viewId);
        editorViewBean.editor.clear();
    }
}
