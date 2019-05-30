package com.foodfusion.foodfusion.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.SigninActivity;

import java.io.InputStream;
import java.io.OutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

//import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Rameez on 2/26/2018.
 */

public class Utility {
    public enum ShowMore {TRENDING, MAYALSOLIKE, MYFAVOURITES};
    public static String root = "http://stage.foodfusion.com/services/";
    public static SweetAlertDialog pDialog;

    public static Toast ShowToastMessage(Context context, String message){
        return Toast.makeText(context,message, Toast.LENGTH_LONG);
    }

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;

        try {
            byte[] bytes = new byte[buffer_size];
            while(true) {
                int count = is.read(bytes, 0, buffer_size);
                if(count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showMessage(Context context, String msg, String header) {
        try {
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(header)
                    .setContentText(msg)
                    .show();
        }
        catch (Exception ex){

        }
    }
    public static void showLoginLikeMessage(final Context context) {
        try {
            final Context context2=context;
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Not Logged In")
                    .setContentText("Please login to use this feature!")
                    .setCancelText("No, Later")
                    .setConfirmText("Login")

                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                            Intent intent=new Intent(context2, SigninActivity.class);
                            intent.putExtra("context",context2.toString());
                            context2.startActivity(intent);
                        }
                    })
                    .show();
        }
        catch (Exception ex){
Toast.makeText(context,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public static synchronized SweetAlertDialog loader(final Context context){
        try {
            if (pDialog == null) {
                pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE, "#01ffffff");
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#2196F3"));
                pDialog.setCancelable(false);

                pDialog.setTitleText("");
            }
        }
        catch (Exception ex){
String abc = ex.getMessage();
        }
        return pDialog;
    }

    public static SweetAlertDialog GetLoader(final Context context){
        SweetAlertDialog sweetDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetDialog.getProgressHelper().setBarColor(Color.parseColor("#2196F3"));
        sweetDialog.setTitleText("Loading...");
        //sweetDialog.setCancelable(false);
        return sweetDialog;
    }

    public static boolean IsInternetAvailable(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void showLoader(final Context context){
        try {
            loader(context).show();
        }
        catch (Exception ex){
        }
    }

    public static void hideLoader(final Context context){
        try{
            loader(context).hide();
            pDialog = null;
        }
        catch (Exception ex){
        }
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public static void StartActivity(Context context, Intent intent){
        try { context.startActivity(intent); }
        catch (Exception e) { Toast.makeText(context, "An error occured " + e.toString(), Toast.LENGTH_SHORT).show(); }
    }

    public static String fontHeading(){
        return "fonts/poppins_light.ttf";
    }

    public static Typeface SetTextFontRegular(Context context){
        return Typeface.createFromAsset(context.getAssets(), Utility.fontHeading());
    }

    public static void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(SetTextFontRegular(context));
            }
            else if (v instanceof TextView) {
                ((TextView) v).setTypeface(SetTextFontRegular(context));
            }
            else if (v instanceof Button) {
                ((Button) v).setTypeface(SetTextFontRegular(context));
            }

        } catch (Exception e) {
        }
    }
}

