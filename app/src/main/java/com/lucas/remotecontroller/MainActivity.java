package com.lucas.remotecontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout refresh;
    TextView txtInfo;
    RecyclerView list;
    OkHttpClient client;
    List<String> channels = new ArrayList<>();
    LinearLayoutManager llm;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Controle Remoto TV");

        refresh = findViewById(R.id.refresh);
        txtInfo = findViewById(R.id.txtInfoList);
        list = findViewById(R.id.listChannels);
        client = new OkHttpClient();
        llm = new LinearLayoutManager(this);
        list.setLayoutManager(llm);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(true);
                refresh();
            }
        });
        refresh();
    }

    private void refresh() {
        showInfo(Messages.LOADING);
        downloadChannelListFromServer();
    }

    private void downloadChannelListFromServer() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String server = sharedPreferences.getString("pref_server", "");
        if (server.equals("")) {
            showInfo(Messages.NO_SERVER_CONFIGURED);
            return;
        }
        Request request = new Request.Builder()
                .get()
                .url("http://" + server + "/channels").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showInfo(Messages.ERROR);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                Type type = Types.newParameterizedType(List.class, String.class);
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<List<String>> adapter = moshi.adapter(type);
                channels = adapter.fromJson(json);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showList();
                    }
                });
            }
        });
    }

    private void showList() {
        refresh.setRefreshing(false);
        if (channels == null || channels.size() == 0) {
            showInfo(Messages.EMPTY_LIST);
            return;
        }
        hideInfoShowList();
        listAdapter = new ListAdapter(channels);
        list.setAdapter(listAdapter);
    }

    private void hideListShowInfo() {
        refresh.setVisibility(View.GONE);
        txtInfo.setVisibility(View.VISIBLE);
    }

    private void hideInfoShowList() {
        txtInfo.setVisibility(View.GONE);
        refresh.setVisibility(View.VISIBLE);
    }

    private void showInfo(String message) {
        refresh.setRefreshing(false);
        hideListShowInfo();
        txtInfo.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_settings) {
            startActivity(new Intent(MainActivity.this, Settings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
