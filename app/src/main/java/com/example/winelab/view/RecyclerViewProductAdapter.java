package com.example.winelab.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.winelab.R;
import com.example.winelab.model.Cat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import io.reactivex.subjects.BehaviorSubject;

public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.MyViewHolder> {

    protected ArrayList<Cat> mDataSet;
    protected Context context;
    protected LayoutInflater mInflater;
    private Integer page = 1;

    private BehaviorSubject<Integer> pagination = BehaviorSubject.create();
    protected BehaviorSubject<Cat> item = BehaviorSubject.create();

    public RecyclerViewProductAdapter(ArrayList<Cat> cats, Context context) {
            this.mDataSet = cats;
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
        }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.recycler_view_products_row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);

        loadImage(mDataSet.get(position),holder);

        holder.downloadBtn.setOnClickListener(v -> {
            Glide.with(context)
                    .asBitmap()
                    .load(mDataSet.get(position).getUrl())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            saveImage(resource,mDataSet.get(position).getId());
                        }
                    });
        });

        holder.favoriteView.setVisibility(mDataSet.get(position).getFavotite() ? View.VISIBLE : View.INVISIBLE);
        holder.favorite.setOnClickListener(v -> item.onNext(mDataSet.get(position)));

//        holder.favorite.setImage(mDataSet.get(position).getFavotite() ? R.drawable.ic_outline_favorite_border_50 : R.drawable.ic_baseline_favorite_50);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.onNext(mDataSet.get(position)));

        checkPagination(position);
    }

    private void checkPagination(int pos){
        if( pos == (mDataSet.size() - 1)) pagination.onNext(++page);
    }


    protected String saveImage(Bitmap image, String name) {
        String savedImagePath = null;
        String imageFileName = name + ".jpg";
        File storageDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            galleryAddPic(savedImagePath);
            Toast.makeText(context, "Image saved", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    protected void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    protected void loadImage(final Cat cat, MyViewHolder holder){
        if (cat != null && !TextUtils.isEmpty(cat.getUrl())){
            Glide.with(context)
                    .load(cat.getUrl())
                    .apply(new RequestOptions().override(300, 450))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.catImg);
        }
    }

    public void updateNewList(ArrayList<Cat> catsList){
        mDataSet = new ArrayList<>();
        mDataSet.addAll(catsList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public BehaviorSubject<Integer> getNextData() {
        return pagination;
    }
    public BehaviorSubject<Cat> getCat() {
        return item;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView favorite;
        ImageView catImg;
        ImageView downloadBtn;

        ProgressBar progressBar;
        CheckBox checkBox;
        View favoriteView;

        MyViewHolder(View itemView) {
            super(itemView);
            favorite = itemView.findViewById(R.id.favorite_img);
//            catImg = itemView.findViewById(R.id.card_view_img);
//            downloadBtn = itemView.findViewById(R.id.download_img);
//            progressBar = itemView.findViewById(R.id.progressBar);
//            checkBox = itemView.findViewById(R.id.checkbox_is_favotite);
//            favoriteView = itemView.findViewById(R.id.favorite_view);
        }
    }
}
