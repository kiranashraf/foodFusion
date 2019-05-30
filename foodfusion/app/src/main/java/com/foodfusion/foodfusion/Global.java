package com.foodfusion.foodfusion;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.ImageCache.ImageCache;
import com.foodfusion.foodfusion.ImageCache.ImageFetcherNew;
import com.foodfusion.foodfusion.Util.NetworkManager;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Rameez on 2/26/2018.
 */

public class Global extends Application {

    private static Context context;
    public static Picasso picassoWithCache;
    public static Context getContext() {
        return context;
    }

    private static Global singleton;

    private static final String IMAGE_CACHE_DIR = "thumbs";
    private ImageFetcherNew mImageFetcher;
    private ImageCache.ImageCacheParams cacheParams;
    private ImageCache.ImageCacheParams homeCacheParams;
    private int mImageThumbSize_height;

    public static Global getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {
        try {
            super.onCreate();
//        IntentFilter intentFilter = new IntentFilter();
//        IntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(new NetworkChangeReceiver(), intentFilter);
            OneSignal.startInit(this)
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                    .init();

            File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
            Cache cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);
            picassoWithCache = new Picasso.Builder(this).downloader(new OkHttp3Downloader(okHttpClientBuilder.build())).build();
            picassoWithCache.setIndicatorsEnabled(false);
            picassoWithCache.setLoggingEnabled(true);
            Picasso.setSingletonInstance(picassoWithCache);

            mImageThumbSize_height = getResources().getDimensionPixelSize(
                    R.dimen.image_thumbnail_size_height);
            cacheParams = new ImageCache.ImageCacheParams(
                    this, IMAGE_CACHE_DIR);
            cacheParams.setMemCacheSizePercent(0.25f);// Set memory cache to 25% of
            // app memory
            // The ImageFetcher takes care of loading images into our ImageView
            // children asynchronously
            mImageFetcher = new ImageFetcherNew(this, getResources()
                    .getDisplayMetrics().widthPixels / 2, mImageThumbSize_height);
            mImageFetcher.setLoadingImage(R.mipmap.imagefetcher);

            MySharedPreference mMySharedPreference = new MySharedPreference(this);
            mMySharedPreference.setCurrentRadio("all");
        }
        catch (Exception ex){

        }
    }
    public ImageFetcherNew getImageFetcher(Activity context){
        mImageFetcher.addImageCache(context, cacheParams);
        return mImageFetcher;
    }
    public static void loadImageView(Context mContext, String imgPath, ImageView imageView) {
        ImageLoader imageLoader = NetworkManager.getInstance(mContext).getImageLoader();
        imageLoader.get(imgPath, ImageLoader.getImageListener(imageView,
                R.mipmap.imagefetcher, R.mipmap.imagefetcher));
    }
    public void setImageFetcher(Activity context){

        mImageFetcher.addImageCache(context, cacheParams);

    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        mImageFetcher.clearCache();
        mImageFetcher.closeCache();
    }
    public void resetCache() {
        mImageFetcher.clearCache();
    }
    public void closeCache() {
        mImageFetcher.clearCache();
    }

}