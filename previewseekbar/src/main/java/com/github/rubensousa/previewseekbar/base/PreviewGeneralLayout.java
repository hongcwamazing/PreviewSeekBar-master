package com.github.rubensousa.previewseekbar.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.github.rubensousa.previewseekbar.R;

public abstract class PreviewGeneralLayout extends RelativeLayout implements PreviewLayout,
        PreviewView.OnPreviewChangeListener {

    private PreviewDelegate delegate;
    private View morphView;
    private View frameView;
    private boolean firstLayout = true;
    private int tintColor;
    private PreviewLoader loader;

    public PreviewGeneralLayout(Context context) {
        super(context);
        init(context, null);
    }

    public PreviewGeneralLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PreviewGeneralLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public abstract boolean checkChilds();

    public abstract void setupMargins();

    public abstract PreviewView getPreviewView();

    private void init(Context context, AttributeSet attrs) {
        TypedValue outValue = new TypedValue();

        getContext().getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
        tintColor = ContextCompat.getColor(context, outValue.resourceId);

        // Create morph view
        morphView = new View(getContext());
        morphView.setBackgroundResource(R.drawable.previewseekbar_morph);

        // Create frame view for the circular reveal
        frameView = new View(getContext());
        delegate = new PreviewDelegate(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        } else if (firstLayout) {

            // Check if we have the proper views
            if (!checkChilds()) {
                throw new IllegalStateException("You need to add a view that implements PreviewView" +
                        "and a FrameLayout as direct childs");
            }

            // Set proper seek bar margins
            setupMargins();

            // Setup colors for the morph view and frame view
            int color = getPreviewView().getDefaultColor();
            if (color != 0) {
                setTintColor(color);
            } else {
                setTintColor(tintColor);
            }

            delegate.setup();
            getPreviewView().addOnPreviewChangeListener(this);

            // Setup morph view
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(0, 0);
            layoutParams.width = getResources()
                    .getDimensionPixelSize(R.dimen.previewseekbar_indicator_width);
            layoutParams.height = layoutParams.width;
            addView(morphView, layoutParams);

            // Add frame view to the preview layout
            FrameLayout.LayoutParams frameLayoutParams
                    = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayoutParams.gravity = Gravity.CENTER;
            (getPreviewFrameLayout()).addView(frameView, frameLayoutParams);
            firstLayout = false;
        }
    }

    public void setup(PreviewLoader loader) {
        this.loader = loader;
    }

    public boolean isShowingPreview() {
        return delegate.isShowing();
    }

    public void showPreview() {
        delegate.show();
    }

    public void hidePreview() {
        delegate.hide();
    }

    @Override
    public View getFrameView() {
        return frameView;
    }

    @Override
    public View getMorphView() {
        return morphView;
    }

    public void setTintColor(@ColorInt int color) {
        Drawable drawable = DrawableCompat.wrap(morphView.getBackground());
        DrawableCompat.setTint(drawable, color);
        morphView.setBackground(drawable);
        frameView.setBackgroundColor(color);
    }

    public void setTintColorResource(@ColorRes int color) {
        setTintColor(ContextCompat.getColor(getContext(), color));
    }


    @Override
    public void onStartPreview(PreviewView previewView) {

    }

    @Override
    public void onStopPreview(PreviewView previewView) {
        hidePreview();
    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {
        if (fromUser) {
            if (loader != null) {
                loader.loadPreview(progress, previewView.getMax());
            }
            showPreview();
        }
    }
}
