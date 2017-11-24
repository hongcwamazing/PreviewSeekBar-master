package com.github.rubensousa.previewseekbar.base;


public interface PreviewView {

    ///接口的时刻记得还是空间的
    int getProgress();

    int getMax();

    int getThumbOffset();

    int getDefaultColor();

    void addOnPreviewChangeListener(OnPreviewChangeListener listener);

    void removeOnPreviewChangeListener(OnPreviewChangeListener listener);

    interface OnPreviewChangeListener {
        void onStartPreview(PreviewView previewView);

        void onStopPreview(PreviewView previewView);

        void onPreview(PreviewView previewView, int progress, boolean fromUser);
    }
}
