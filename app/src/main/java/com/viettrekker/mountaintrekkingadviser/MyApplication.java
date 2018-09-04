package com.viettrekker.mountaintrekkingadviser;

import android.app.Application;
import android.os.StrictMode;
import android.support.v7.app.AppCompatDelegate;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.twitter.TwitterEmojiProvider;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.net.URISyntaxException;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_AUTO;

public class MyApplication extends Application {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(APIUtils.BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    @Override public void onCreate() {
        super.onCreate();

        EmojiManager.install(new TwitterEmojiProvider());
    }
}
