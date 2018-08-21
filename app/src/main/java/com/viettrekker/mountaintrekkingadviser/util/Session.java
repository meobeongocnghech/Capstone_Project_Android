package com.viettrekker.mountaintrekkingadviser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.erikagtierrez.multiple_media_picker.Gallery;
import com.viettrekker.mountaintrekkingadviser.model.MyGallery;
import com.viettrekker.mountaintrekkingadviser.model.MyMedia;
import com.viettrekker.mountaintrekkingadviser.model.MyMessage;
import com.viettrekker.mountaintrekkingadviser.model.User;
import com.viettrekker.mountaintrekkingadviser.util.network.APIService;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Session {

    private static final String prefName = "mta";

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }

    public static int getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("userId", 0);
    }

    public static void setSession(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", user.getToken());
        editor.putInt("userId", user.getId());
        editor.putString("firstname", user.getFirstName());
        editor.putString("lastname", user.getLastName());
        editor.putString("avatar", user.getGallery().getMedia().get(0).getPath());
        editor.putString("email", user.getEmail());
        editor.commit();
    }

    public static User getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        User user = new User();
        user.setId(sharedPreferences.getInt("userId", 0));
        user.setEmail(sharedPreferences.getString("email", ""));
        user.setFirstName(sharedPreferences.getString("firstname", ""));
        user.setLastName(sharedPreferences.getString("lastname", ""));
        MyMedia media = new MyMedia();
        media.setPath(sharedPreferences.getString("avatar", ""));
        MyGallery gallery = new MyGallery();
        List<MyMedia> medias = new ArrayList<>();
        medias.add(media);
        gallery.setMedia(medias);
        user.setGallery(gallery);
        return user;
    }

    public static void clearSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
