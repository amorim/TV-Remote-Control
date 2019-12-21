package com.lucas.remotecontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class TVAPI {
    static OkHttpClient client = new OkHttpClient();
    public static void switchChannel(int adapterPosition, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String server = sharedPreferences.getString("pref_server", "");
        Request req =  new Request.Builder()
                .get()
                .url("http://" + server + "/watch/" + adapterPosition).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                Log.i("CANAL", "erro");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i("CANAL", "alterado");
            }
        });

    }
}
