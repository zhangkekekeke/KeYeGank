package com.dih.keye.keyegank.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.dih.keye.keyegank.R;
import com.dih.keye.keyegank.adapter.GirlAdapter;
import com.dih.keye.keyegank.model.GirlData;
import com.dih.keye.keyegank.model.Image;
import com.dih.keye.keyegank.model.PrettyGirl;
import com.dih.keye.keyegank.databinding.ActivityMainBinding;
import com.dih.keye.keyegank.retrofitUtils.GirlApi;
import com.dih.keye.keyegank.retrofitUtils.GirlRetrofit;
import com.dih.keye.keyegank.retrofitUtils.Results;
import com.dih.keye.keyegank.utils.ConfigUtils;
import com.dih.keye.keyegank.utils.NetUtils;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends RxAppCompatActivity {
    private List<Image> mImages = new ArrayList<>();

    private GirlAdapter girlAdapter;
    GirlApi girlApi;
    private int page = 1;
    private boolean refreshing;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        girlApi = new GirlRetrofit().getGirlApi();
        flyToTop();
        swipeRefresh();
        setupRecyclerView();
        onImageClick();
    }

    private void setupRecyclerView() {
        girlAdapter = new GirlAdapter(this, mImages);
        int spanCount = 2;
        if (ConfigUtils.isOrientationPortrait(this))
            spanCount = 2;
        else if (ConfigUtils.isOrientationLandscape(this))
            spanCount = 3;

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                spanCount, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(girlAdapter);

        RxRecyclerView.scrollEvents(binding.recyclerView)
                .compose(bindToLifecycle())
                .map(scrollEvent -> {
                    boolean isBottom = false;
                    if (ConfigUtils.isOrientationPortrait(this)) {
                        isBottom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])
                                [1] >= mImages.size() - 4;
                    } else if (ConfigUtils.isOrientationLandscape(this)) {
                        isBottom = layoutManager.findLastCompletelyVisibleItemPositions(
                                new int[3]
                        )[2] >= mImages.size() - 4;
                    }
                    return isBottom;
                })
                .filter(isBottom -> !binding.refreshLayout.isRefreshing() && isBottom)
                .subscribe(recyclerViewScrollEvent -> {
                    //这么做的目的是一旦下拉刷新，RxRecyclerView scrollEvents 也会被触发，page就会加一
                    //所以要将page设为0，这样下拉刷新才能获取第一页的数据
                    if (refreshing) {
                        page = 0;
                        refreshing = false;
                    }
                    page += 1;
                    binding.refreshLayout.setRefreshing(true);
                    fetchGrilData();
                });
    }

    private void onImageClick() {
        girlAdapter.setOnTouchListener((v, image) -> Picasso.with(getApplicationContext()).load(image.url).fetch(new Callback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                intent.putExtra("url", image.url);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat compat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                                v, "girl");
                ActivityCompat.startActivity(MainActivity.this, intent, compat.toBundle());
            }

            @Override
            public void onError() {
            }
        }));

    }

    private void swipeRefresh() {
        RxSwipeRefreshLayout.refreshes(binding.refreshLayout)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    page = 1;
                    refreshing = true;
                    fetchGrilData();
                });
    }


    private void flyToTop() {
        RxView.clicks(binding.toolbar)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    binding.recyclerView.smoothScrollToPosition(0);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetUtils.checkNet(this)) {
            Snackbar.make(binding.recyclerView, "无网络不能获取妹纸哦！", Snackbar.LENGTH_LONG).show();
        }

        fetchGrilData();
    }

    private void fetchGrilData() {
        Observable<List<Image>> results = girlApi.fetchPrettyGirl(page)
                .compose(bindToLifecycle())
                .filter(Results.isSuccess())
                .map(girlDataResult -> girlDataResult.response().body())
                .flatMap(imageFetcher)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        results.filter(Results.isNull())
                .compose(bindToLifecycle())
                .doOnCompleted(() -> binding.refreshLayout.setRefreshing(false))
                .subscribe(girlAdapter, dataError);
    }


    private final Func1<GirlData, Observable<List<Image>>> imageFetcher = girlData -> {
        for (PrettyGirl girl : girlData.results) {
            try {
                Bitmap bitmap = Picasso.with(this).load(girl.url)
                        .get();
                Image image = new Image();
                image.width = bitmap.getWidth();
                image.height = bitmap.getHeight();
                image.url = girl.url;
                mImages.add(image);
            } catch (IOException e) {
                e.printStackTrace();
                return Observable.error(e);
            }
        }
        return Observable.just(mImages);
    };

    private Action1<Throwable> dataError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            throwable.printStackTrace();
            binding.refreshLayout.setRefreshing(false);
            Snackbar.make(binding.refreshLayout, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
