package com.example.myscriptwidget.bean;

import android.view.View;

import com.myscript.iink.ContentPackage;
import com.myscript.iink.ContentPart;
import com.myscript.iink.Editor;
import com.myscript.iink.ParameterSet;
import com.myscript.iink.uireferenceimplementation.EditorData;
import com.myscript.iink.uireferenceimplementation.EditorView;

public class EditorViewBean {
    public Editor editor;
    public View editorViewInflate;
    public EditorView editorView;

    public ContentPackage contentPackage;
    public EditorData editorData;
    public ContentPart contentPart;
    public ParameterSet exportParams;

    public EditorViewBean(Editor editor, View editorViewInflate, EditorView editorView, ContentPackage contentPackage, EditorData editorData, ContentPart contentPart, ParameterSet exportParams) {
        this.editor = editor;
        this.editorViewInflate = editorViewInflate;
        this.editorView = editorView;
        this.contentPackage = contentPackage;
        this.contentPart = contentPart;
        this.editorData = editorData;
        this.exportParams = exportParams;
    }
}