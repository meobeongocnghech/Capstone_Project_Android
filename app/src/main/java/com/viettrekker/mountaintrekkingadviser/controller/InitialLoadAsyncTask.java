package com.viettrekker.mountaintrekkingadviser.controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

public class InitialLoadAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProgressBar progressBar;
    private int status;
    private Context context;

    public InitialLoadAsyncTask(ProgressBar progressBar, Context context) {
        this.progressBar = progressBar;
        this.context = context;
        status = 1;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(status);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (status < 100) {
            status += 25;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        progressBar.setProgress(status);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context.startActivity(new Intent(context, LoginActivity.class));
    }
}
