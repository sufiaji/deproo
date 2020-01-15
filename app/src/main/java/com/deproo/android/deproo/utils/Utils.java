package com.deproo.android.deproo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.util.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public final class Utils {

    public final static String image_upload_max = "Silahkan mengunggah maksimum "
            + Integer.toString(Constants.MAX_UPLOAD_IMAGE)
            + " gambar.";

    public final static String image_upload_max_result = "Hanya bisa mengunggah gambar maksimum "
            + Integer.toString(Constants.MAX_UPLOAD_IMAGE)
            + " gambar.";

    public final static String video_upload_max = "Silahkan mengunggah maksimum "
            + Integer.toString(Constants.MAX_UPLOAD_VIDEO)
            + " video.";

    public final static String video_upload_max_result = "Hanya bisa mengunggah video maksimum "
            + Integer.toString(Constants.MAX_UPLOAD_VIDEO)
            + " video.";

    private Utils() {}

    public static boolean ValidateEmail(String email) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if(email.matches(emailPattern)) return true;
        else return false;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void CommonAlertOkDisplayer(final Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public static void CommonAlertOkDisplayerDestroy(final Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ((Activity) context).finish();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public static void CommonAlertOkCancelDisplayerResult(final Context context,
                                                          String title,
                                                          String message,
                                                          String OkText,
                                                          String CancelText,
                                                          final Intent intent){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(OkText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ((Activity) context).setResult(Activity.RESULT_OK, intent);
                        ((Activity) context).finish();
                    }
                })
                .setNegativeButton(CancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                        //((Activity) context).finish();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public static void CommonToastDisplayerShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void CommonToastDisplayerLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, float maxWidth, float maxHeight) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        if (width > maxWidth) {
            height = (maxWidth / width) * height;
            width = maxWidth;
        }
        if (height > maxHeight) {
            width = (maxHeight / height) * width;
            height = maxHeight;
        }
        return Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
    }

    public static Bitmap decodeBitmapFromUriResize(Context c, Uri data, int maxWidth, int maxHeight) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = c.getContentResolver().query(data, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap b = null;

        // Get the dimensions of the original bitmap
        BitmapFactory.Options bmOptions= new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds= true;
        BitmapFactory.decodeFile(picturePath, bmOptions);
        int photoW= bmOptions.outWidth;
        int photoH= bmOptions.outHeight;

        // Determine how much to scale down the image.
        int scaleFactor= (int) Math.max(1.0, Math.min((double) photoW / (double)maxWidth, (double)photoH / (double)maxHeight));    //1, 2, 3, 4, 5, 6, ...
        scaleFactor= (int) Math.pow(2.0, Math.floor(Math.log((double) scaleFactor) / Math.log(2.0)));               //1, 2, 4, 8, ...

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds= false;
        bmOptions.inSampleSize= scaleFactor;
        bmOptions.inPurgeable= true;

        do
        {
            try
            {
                Log.d("tag", "scaleFactor: " + scaleFactor);
                scaleFactor*= 2;
                b= BitmapFactory.decodeFile(picturePath, bmOptions);
            }
            catch(OutOfMemoryError e)
            {
                bmOptions.inSampleSize= scaleFactor;
                Log.d("tag", "OutOfMemoryError: " + e.toString());
            }
        }
        while(b == null && scaleFactor <= 256);
        return b;
    }

    public static Bitmap decodeBitmapFromPathResize(String picturePath, int maxWidth, int maxHeight) {
        Bitmap b = null;

        // Get the dimensions of the original bitmap
        BitmapFactory.Options bmOptions= new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds= true;
        BitmapFactory.decodeFile(picturePath, bmOptions);
        int photoW= bmOptions.outWidth;
        int photoH= bmOptions.outHeight;

        // Determine how much to scale down the image.
        int scaleFactor= (int) Math.max(1.0, Math.min((double) photoW / (double)maxWidth, (double)photoH / (double)maxHeight));    //1, 2, 3, 4, 5, 6, ...
        scaleFactor= (int) Math.pow(2.0, Math.floor(Math.log((double) scaleFactor) / Math.log(2.0)));               //1, 2, 4, 8, ...

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds= false;
        bmOptions.inSampleSize= scaleFactor;
        bmOptions.inPurgeable= true;

        do
        {
            try
            {
                Log.d("tag", "scaleFactor: " + scaleFactor);
                scaleFactor*= 2;
                b= BitmapFactory.decodeFile(picturePath, bmOptions);
            }
            catch(OutOfMemoryError e)
            {
                bmOptions.inSampleSize= scaleFactor;
                Log.d("tag", "OutOfMemoryError: " + e.toString());
            }
        }
        while(b == null && scaleFactor <= 256);
        return b;
    }

    public static byte[] bitmapPathToParseByteArray(String path) {
        Bitmap bitmap = decodeBitmapFromPathResize(path,Constants.DEFAULT_IMAGE_SIZE, Constants.DEFAULT_IMAGE_SIZE);
        ByteArrayOutputStream streamImg = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streamImg);
        byte[] imageByte = streamImg.toByteArray();
        return imageByte;
    }

    public static byte[]    videoPathToParseByteArray(String path) {
        byte[] bytes = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            bytes = IOUtils.toByteArray(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] videoPathToThumbnailByteArray(String path) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        ByteArrayOutputStream streamImg = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streamImg);
        byte[] imageByte = streamImg.toByteArray();
        return imageByte;
    }

    public static byte[] imagePathToThumbnailByteArray(String path) {
        Bitmap bitmap = decodeBitmapFromPathResize(path,Constants.DEFAULT_IMAGE_THUMBSIZE, Constants.DEFAULT_IMAGE_THUMBSIZE);
        ByteArrayOutputStream streamImg = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streamImg);
        byte[] imageByte = streamImg.toByteArray();
        return imageByte;
    }

    public static String formatIDR(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return (formatRupiah.format(number) + ",-");
    }

    public static String formatDouble(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

    public static String getDateFormattedShort(Date date) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(date);
    }

    public static int getResIdOfComponent(Context context, String name) {
        return context.getResources().getIdentifier(name, "id", context.getPackageName());
    }

    /**
     * for newsfeed
     */

    public static ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(Color.parseColor("#ffeead")),
                    new ColorDrawable(Color.parseColor("#93cfb3")),
                    new ColorDrawable(Color.parseColor("#fd7a7a")),
                    new ColorDrawable(Color.parseColor("#faca5f")),
                    new ColorDrawable(Color.parseColor("#1ba798")),
                    new ColorDrawable(Color.parseColor("#6aa9ae")),
                    new ColorDrawable(Color.parseColor("#ffbf27")),
                    new ColorDrawable(Color.parseColor("#d93947"))
            };

    public static ColorDrawable getRandomDrawbleColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }

    public static String DateToTimeFormat(String oldstringDate){
        PrettyTime p = new PrettyTime(new Locale(getCountry()));
        String isTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.ENGLISH);
            Date date = sdf.parse(oldstringDate);
            isTime = p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isTime;
    }

    public static String DateFormat(String oldstringDate){
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry()));
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }

    public static String getCountry(){
        Locale locale = Locale.getDefault();
        String country = String.valueOf(locale.getCountry());
        return country.toLowerCase();
    }

}
