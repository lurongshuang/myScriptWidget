package com.example.myscriptwidget.engine;

import static io.flutter.util.PathUtils.getCacheDirectory;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.myscriptwidget.R;
import com.example.myscriptwidget.bean.EditorViewBean;
import com.myscript.iink.Configuration;
import com.myscript.iink.ContentPackage;
import com.myscript.iink.ContentPart;
import com.myscript.iink.Editor;
import com.myscript.iink.Engine;
import com.myscript.iink.ParameterSet;
import com.myscript.iink.PointerTool;
import com.myscript.iink.Renderer;
import com.myscript.iink.uireferenceimplementation.EditorBinding;
import com.myscript.iink.uireferenceimplementation.EditorData;
import com.myscript.iink.uireferenceimplementation.EditorView;
import com.myscript.iink.uireferenceimplementation.FontUtils;
import com.myscript.iink.uireferenceimplementation.InputController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MyScriptEngine {
    private static final String TAG = "MyScriptEngine";

    private static Engine engine;

    private static final Map<Integer, EditorViewBean> viewMap = new HashMap<>();

    public static byte[] myCertificate = {};
    public static String configPath = "";

    public static EditorViewBean getEditorView(Context applicationContext, int viewId, Map<String, Object> args) {
        initEditorView(applicationContext, viewId, args);
        return viewMap.get(viewId);
    }


    public static Engine getEngine() {

        return engine;
    }

    /**
     * 初始化Engine
     */
    public static void initEngine() {
        if (engine == null || engine.isClosed()) {
            initCreateEngine();
        }
    }

    private static synchronized void initCreateEngine() {
//        engine = Engine.create(MyCertificate.getBytes());
        try {
            engine = Engine.create(myCertificate);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }


    public static EditorViewBean getEditorViewBean(int id) {
        return viewMap.get(id);

    }

    private static View initEditorView(Context applicationContext, int viewId, Map<String, Object> args) {
        Editor editor;
        View editorViewInflate;
        EditorView editorView;
        ContentPackage contentPackage = null;
        EditorData editorData;
        ContentPart contentPart = null;
        ParameterSet exportParams;
        FrameLayout flView;

        // configure recognition
        Configuration conf = engine.getConfiguration();
//        String confDir = "zip://" + applicationContext.getPackageCodePath() + "!/assets/conf";
        String confDir = configPath;
        conf.setStringArray("configuration-manager.search-path", new String[]{confDir});
        String tempDir = getCacheDirectory(applicationContext) + File.separator + "tmp";
        conf.setString("content-package.temp-folder", tempDir);

        LayoutInflater inflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editorViewInflate = inflater.inflate(R.layout.activity_main, null);
        editorView = editorViewInflate.findViewById(R.id.editor_view);
        flView = editorViewInflate.findViewById(R.id.flView);
        // load fonts
        AssetManager assetManager = applicationContext.getAssets();
        Map<String, Typeface> typefaceMap = FontUtils.loadFontsFromAssets(assetManager);
        editorView.setTypefaces(typefaceMap);
        EditorBinding editorBinding = new EditorBinding(engine, typefaceMap);
        editorData = editorBinding.openEditor(editorView);
        editor = editorData.getEditor();
        assert editor != null;
        setMargins(editor, applicationContext);

        assert editorData.getInputController() != null;
        editorData.getInputController().setInputMode(InputController.INPUT_MODE_FORCE_PEN);


        String packageName = "File1_" + viewId + ".iink";
        File file = new File(applicationContext.getFilesDir(), packageName);
        try {
            contentPackage = engine.createPackage(file);
            // Choose type of content (possible values are: "Text Document", "Text", "Diagram", "Math", "Drawing" and "Raw Content")
            contentPart = contentPackage.createPart("Text");
        } catch (IOException | IllegalArgumentException e) {
            Log.e(TAG, "Failed to open package \"" + packageName + "\"", e);
        }


        // wait for view size initialization before setting part
        editorView.post(() -> {
            Renderer renderer = editorView.getRenderer();
            if (renderer != null) {
                renderer.setViewOffset(0, 0);
                editorView.getRenderer().setViewScale(1);
                editorView.setVisibility(View.VISIBLE);
                editor.setPart(getEditorViewBean(viewId).contentPart);
            }
        });
        exportParams = engine.createParameterSet();
        exportParams.setBoolean("export.jiix.text.words", true);

        viewMap.put(viewId, new EditorViewBean(editor, editorViewInflate, editorView, contentPackage, editorData, contentPart, exportParams));
        try {
            String bgColor = (String) args.get("bgColor");
            editorView.setBackgroundColor(Color.parseColor(bgColor));
            flView.setBackgroundColor(Color.parseColor(bgColor));
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        try {
            String penColor = (String) args.get("penColor");
            editor.getToolController().setToolStyle(PointerTool.PEN, "color: " + penColor + ";");
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return editorViewInflate;
    }


    public static void disposeView() {
        for (Map.Entry<Integer, EditorViewBean> editorViewBeanEntry : viewMap.entrySet()) {
            EditorViewBean editorViewBean = editorViewBeanEntry.getValue();

            EditorView editorView = editorViewBean.editorView;
            if (editorView != null) {
                editorView.setEditor(null);
            }


            ContentPart contentPart = editorViewBean.contentPart;
            if (contentPart != null && !contentPart.isClosed()) {
                contentPart.getPackage().close();
                contentPart.close();
            }

            Editor editor = editorViewBean.editor;
            if (editor != null && !editor.isClosed()) {
                editor.getRenderer().close();
                editor.close();
            }

            ContentPackage contentPackage = editorViewBean.contentPackage;
            if (contentPackage != null && !contentPackage.isClosed()) {
                contentPackage.close();
            }
        }
        viewMap.clear();
//        engine.close();
    }

    private static void setMargins(Editor editor, Context applicationContext) {
        DisplayMetrics displayMetrics = applicationContext.getResources().getDisplayMetrics();
        Configuration conf = editor.getConfiguration();
        float verticalMarginPX = 2;
        float verticalMarginMM = 25.4f * verticalMarginPX / displayMetrics.ydpi;
        float horizontalMarginPX = 10;
        float horizontalMarginMM = 25.4f * horizontalMarginPX / displayMetrics.xdpi;
        conf.setNumber("text.margin.top", verticalMarginMM);
        conf.setNumber("text.margin.left", horizontalMarginMM);
        conf.setNumber("text.margin.right", horizontalMarginMM);

        conf.setNumber("math.margin.top", verticalMarginMM);
        conf.setNumber("math.margin.bottom", verticalMarginMM);
        conf.setNumber("math.margin.left", horizontalMarginMM);
        conf.setNumber("math.margin.right", horizontalMarginMM);
        conf.setBoolean("text.guides.enable", false);
    }
}