package com.foodfusion.foodfusion.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.*;

/**
 * Created by Rameez on 5/4/2018.
 */

public final class NetworkManager {

    /**
     * Log or request TAG
     */
    public static final String TAG = "VolleyPatterns";

    private static NetworkManager mInstance;
    private RequestQueue mRequestQueue;
    private com.android.volley.toolbox.ImageLoader mImageLoader;
    private static Context mCtx;

    private NetworkManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new com.android.volley.toolbox.ImageLoader(mRequestQueue, new LruBitmapCache(
                LruBitmapCache.getCacheSize(mCtx)));
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }


    public com.android.volley.toolbox.ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     *
     * @param error
     * @param context
     * @return
     */
//    public static String getMessage(Object error, Context context) {
//        if (error instanceof TimeoutError) {
//            return context.getResources().getString(R.string.generic_server_down);
//        } else if (isServerProblem(error)) {
//            return handleServerError(error, context);
//        } else if (isNetworkProblem(error)) {
//            return context.getResources().getString(R.string.no_internet);
//        }
//        return context.getResources().getString(R.string.generic_error);
//    }

    /**
     * Determines whether the error is related to network
     *
     * @param error
     * @return
     */
    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }


    /**
     * Determines whether the error is related to server
     *
     * @param error
     * @return
     */
    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }

    /**
     * Handles the server error, tries to determine whether to show a stock message or to
     * show a message retrieved from the server.
     *
     * @param err
     * @param context
     * @return
     */
//    private static String handleServerError(Object err, Context context) {
//        VolleyError error = (VolleyError) err;
//
//        NetworkResponse response = error.networkResponse;
//
//        if (response != null) {
//            switch (response.statusCode) {
//                case 404:
//                    return context.getResources().getString(R.string.wrong_endpoint);
//                case 422:
//                case 401:
//                    try {
//                        // server might return error like this { "error": "Some error occurred" }
//                        JSONObject jsonObject = new JSONObject(new String(response.data));
//                        return jsonObject.getString("error");
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    // Invalid request
//                    return error.getMessage();
//
//                default:
//                    return context.getResources().getString(R.string.generic_server_down);
//            }
//        }
//        return context.getResources().getString(R.string.generic_error);
//    }
}

class LruBitmapCache extends LruCache<String, Bitmap>
        implements com.android.volley.toolbox.ImageLoader.ImageCache {

    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    public LruBitmapCache(Context ctx) {
        this(getCacheSize(ctx));
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    // Returns a cache size equal to approximately three screens worth of images.
    public static int getCacheSize(Context ctx) {
        final DisplayMetrics displayMetrics = ctx.getResources().
                getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        final int screenBytes = screenWidth * screenHeight * 4;

        return screenBytes * 3;
    }
}