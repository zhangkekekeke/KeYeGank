package com.dih.keye.keyegank.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dih.keye.keyegank.R;
import com.dih.keye.keyegank.model.Image;
import com.dih.keye.keyegank.databinding.GirlItemBinding;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;


/**
 * Created by zsj on 2015/11/20 0020.
 */
public class GirlAdapter extends RecyclerView.Adapter<GirlAdapter.GirlViewHolder>
        implements Action1<List<Image>> {
    private final Glide glide;
    private Context context;
    private List<Image> images;
    private OnTouchListener onTouchListener;

    public GirlAdapter(Context context, List<Image> images) {
        this.context = context;
        this.images = images;
        glide = Glide.get(this.context);
        glide.setMemoryCategory(MemoryCategory.HIGH);
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    @Override
    public GirlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GirlViewHolder holder = new GirlViewHolder(LayoutInflater.from(context).inflate(
                R.layout.girl_item, parent, false
        ));
        return holder;
    }

    @Override
    public void onBindViewHolder(GirlViewHolder holder, int position) {
        Image image = images.get(position);

        holder.image = image;
        holder.binding.setImage(image);
        holder.binding.executePendingBindings();

        glide.with(context)
                .load(image.url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.image);
    }

    @Override
    public int getItemViewType(int position) {
        Image image = images.get(position);

        return Math.round((float) image.width / (float) image.height * 10f);
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public void call(List<Image> images) {
        notifyDataSetChanged();
    }


    class GirlViewHolder extends RecyclerView.ViewHolder {
        GirlItemBinding binding;
        Image image;

        public GirlViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            //防止手抖，连续点击图片打开多个页面
            RxView.clicks(binding.girlLayout)
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .subscribe(aVoid -> {
                        if (onTouchListener != null) {
                            onTouchListener.onImageClick(binding.image, image);
                        }
                    });
        }
    }

    public interface OnTouchListener {
        void onImageClick(View v, Image image);
    }
}
