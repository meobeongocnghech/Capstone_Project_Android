package com.viettrekker.mountaintrekkingadviser;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.viettrekker.mountaintrekkingadviser.util.network.APIUtils;

import java.net.URISyntaxException;

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
}
