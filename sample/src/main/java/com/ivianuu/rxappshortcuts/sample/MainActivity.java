package com.ivianuu.rxappshortcuts.sample;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ivianuu.rxappshortcuts.AppShortcut;
import com.ivianuu.rxappshortcuts.RxAppShortcuts;

import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final ShortcutAdapter shortcutAdapter = new ShortcutAdapter();
        recyclerView.setAdapter(shortcutAdapter);

        RxAppShortcuts.getShortcutsFor(this, "com.google.android.googlequicksearchbox")
                .subscribe(new Consumer<List<AppShortcut>>() {
                    @Override
                    public void accept(final List<AppShortcut> appShortcuts) throws Exception {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // to lazy to include rxandroid dependency :DD
                                shortcutAdapter.update(appShortcuts);
                            }
                        });
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }
}
