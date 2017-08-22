package com.daimajia.slider.library.SliderTypes;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.daimajia.slider.library.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * When you want to make your own slider view, you must extends from this class.
 * BaseSliderView provides some useful methods.
 * I provide two example: {@link com.daimajia.slider.library.SliderTypes.DefaultSliderView} and
 * {@link com.daimajia.slider.library.SliderTypes.TextSliderView}
 * if you want to show progressbar, you just need to set a progressbar id as @+id/loading_bar.
 */
public abstract class BaseSliderView {

    private static final int MIN_WIDTH = 10;
    private static final int MIN_HEIGHT = 10;
    protected Context mContext;

    private Bundle mBundle;

    /**
     * Error place holder image.
     */
    private int mErrorPlaceHolderRes;

    /**
     * Empty imageView placeholder.
     */
    private int mEmptyPlaceHolderRes;

    private String mUrl;
    private File mFile;
    private int mRes;

    protected OnSliderClickListener mOnSliderClickListener;

    private boolean mErrorDisappear;

    private ImageLoadListener mLoadListener;

    private String mDescription;

    private Picasso mPicasso;

    /**
     * Scale type of the image.
     */
    private ScaleType mScaleType = ScaleType.Fit;

    public enum ScaleType {
        CenterCrop, CenterInside, Fit, FitCenterCrop
    }

    GenericDraweeHierarchyBuilder mHierarchyBuilder;
    PipelineDraweeControllerBuilder mControllerBuilder;
    ResizeOptions mResizeOptions;

    protected BaseSliderView(Context context) {
        init(context, -1, -1);
    }
    protected BaseSliderView(Context context, int w, int h) {
        init(context, w, h);
    }

    private void init(Context context, int w, int h) {
        mContext = context;
        mHierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(mContext.getResources());

        mControllerBuilder = Fresco.newDraweeControllerBuilder();
        w = w <= 0 ? MIN_WIDTH : w;
        h = h <= 0 ? MIN_HEIGHT : h;
        mResizeOptions = new ResizeOptions(w, h);
    }

    /**
     * the placeholder image when loading image from url or file.
     *
     * @param resId Image resource id
     * @return
     */
    public BaseSliderView empty(int resId) {
        mEmptyPlaceHolderRes = resId;
        return this;
    }

    /**
     * determine whether remove the image which failed to download or load from file
     *
     * @param disappear
     * @return
     */
    public BaseSliderView errorDisappear(boolean disappear) {
        mErrorDisappear = disappear;
        return this;
    }

    /**
     * if you set errorDisappear false, this will set a error placeholder image.
     *
     * @param resId image resource id
     * @return
     */
    public BaseSliderView error(int resId) {
        mErrorPlaceHolderRes = resId;
        return this;
    }

    /**
     * the description of a slider image.
     *
     * @param description
     * @return
     */
    public BaseSliderView description(String description) {
        mDescription = description;
        return this;
    }

    /**
     * set a url as a image that preparing to load
     *
     * @param url
     * @return
     */
    public BaseSliderView image(String url) {
        if (mFile != null || mRes != 0) {
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mUrl = url;
        return this;
    }

    /**
     * set a file as a image that will to load
     *
     * @param file
     * @return
     */
    public BaseSliderView image(File file) {
        if (mUrl != null || mRes != 0) {
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mFile = file;
        return this;
    }

    public BaseSliderView image(int res) {
        if (mUrl != null || mFile != null) {
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mRes = res;
        return this;
    }

    /**
     * lets users add a bundle of additional information
     *
     * @param bundle
     * @return
     */
    public BaseSliderView bundle(Bundle bundle) {
        mBundle = bundle;
        return this;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isErrorDisappear() {
        return mErrorDisappear;
    }

    public int getEmpty() {
        return mEmptyPlaceHolderRes;
    }

    public int getError() {
        return mErrorPlaceHolderRes;
    }

    public String getDescription() {
        return mDescription;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * set a slider image click listener
     *
     * @param l
     * @return
     */
    public BaseSliderView setOnSliderClickListener(OnSliderClickListener l) {
        mOnSliderClickListener = l;
        return this;
    }

    /**
     * When you want to implement your own slider view, please call this method in the end in `getView()` method
     *
     * @param v               the whole view
     * @param targetImageView where to place image
     */
    protected void bindEventAndShow(final View v, SimpleDraweeView targetImageView) {
        final BaseSliderView me = this;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSliderClickListener != null) {
                    mOnSliderClickListener.onSliderClick(me);
                }
            }
        });

        if (targetImageView == null)
            return;

        if (mLoadListener != null) {
            mLoadListener.onStart(me);
        }

        if (getEmpty() != 0) {
            mHierarchyBuilder.setPlaceholderImage(getEmpty());
        }

        if (getError() != 0) {
            mHierarchyBuilder.setFailureImage(getError());
        }

        switch (mScaleType) {
            case Fit:
                mHierarchyBuilder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
                break;
            case CenterCrop:
                mHierarchyBuilder.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                break;
            case CenterInside:
                mHierarchyBuilder.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
                break;
        }

        ImageRequestBuilder imageRequestBuilder;
        if (mUrl != null) {
            imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(mUrl));
        } else if (mRes != 0) {
            imageRequestBuilder = ImageRequestBuilder.newBuilderWithResourceId(mRes);
        } else {
            return;
        }

        ImageRequest request = imageRequestBuilder
                .setResizeOptions(mResizeOptions)
                .build();

        DraweeController controller = mControllerBuilder
                .setOldController(targetImageView.getController())
                .setImageRequest(request)
                .build();

        targetImageView.setController(controller);
        targetImageView.setHierarchy(mHierarchyBuilder.build());

        if (v.findViewById(R.id.loading_bar) != null) {
            v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
        }
    }


    public BaseSliderView setScaleType(ScaleType type) {
        mScaleType = type;
        return this;
    }

    public ScaleType getScaleType() {
        return mScaleType;
    }

    /**
     * the extended class have to implement getView(), which is called by the adapter,
     * every extended class response to render their own view.
     *
     * @return
     */
    public abstract View getView();

    /**
     * set a listener to get a message , if load error.
     *
     * @param l
     */
    public void setOnImageLoadListener(ImageLoadListener l) {
        mLoadListener = l;
    }

    public interface OnSliderClickListener {
        public void onSliderClick(BaseSliderView slider);
    }

    /**
     * when you have some extra information, please put it in this bundle.
     *
     * @return
     */
    public Bundle getBundle() {
        return mBundle;
    }

    public interface ImageLoadListener {
        public void onStart(BaseSliderView target);

        public void onEnd(boolean result, BaseSliderView target);
    }

    /**
     * Get the last instance set via setPicasso(), or null if no user provided instance was set
     *
     * @return The current user-provided Picasso instance, or null if none
     */
    public Picasso getPicasso() {
        return mPicasso;
    }

    /**
     * Provide a Picasso instance to use when loading pictures, this is useful if you have a
     * particular HTTP cache you would like to share.
     *
     * @param picasso The Picasso instance to use, may be null to let the system use the default
     *                instance
     */
    public void setPicasso(Picasso picasso) {
        mPicasso = picasso;
    }
}
