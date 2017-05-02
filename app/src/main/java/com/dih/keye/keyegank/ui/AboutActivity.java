package com.dih.keye.keyegank.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;

import com.dih.keye.keyegank.R;
import com.dih.keye.keyegank.databinding.ActivityAboutBinding;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

public class AboutActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_about);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RxToolbar.navigationClicks(binding.toolbar)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    onBackPressed();
                });

        RxView.clicks(binding.cardView)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://github.com/zhangkekekeke/KeYeGank"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });

        RxView.clicks(binding.cardGankio)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://gank.io"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });

    }
}
