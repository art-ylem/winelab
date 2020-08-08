package com.example.winelab.view;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.winelab.R;
import com.example.winelab.model.Cat;


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

        holder.title.setText(String.valueOf(mDataSet.get(position).getWidth()));
        holder.count.setText(String.valueOf(mDataSet.get(position).getCount()));
        holder.favorite.setImageResource(mDataSet.get(position).getFavotite() ? R.drawable.ic_baseline_favorite_50 : R.drawable.ic_outline_favorite_border_50);

        Glide.with(context)
                .load(mDataSet.get(position).getUrl())
                .into(holder.productImg);

        holder.favorite.setOnClickListener(v -> {
            if (mDataSet.get(position).getFavotite()) {
                mDataSet.get(position).setFavotite(false);
            } else {
                mDataSet.get(position).setFavotite(true);
            }
            holder.favorite.setImageResource(mDataSet.get(position).getFavotite() ? R.drawable.ic_baseline_favorite_50 : R.drawable.ic_outline_favorite_border_50);
        });


        holder.addBtn.setOnClickListener(v -> {
            mDataSet.get(position).setCount(mDataSet.get(position).getCount() + 1);
            holder.count.setText(String.valueOf(mDataSet.get(position).getCount()));
        });
        holder.removeBtn.setOnClickListener(v -> {
            if (mDataSet.get(position).getCount() > 1) {
                mDataSet.get(position).setCount(mDataSet.get(position).getCount() - 1);
                holder.count.setText(String.valueOf(mDataSet.get(position).getCount()));
            }
        });


//        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.onNext(mDataSet.get(position)));
//
//        checkPagination(position);
    }

    private void checkPagination(int pos) {
        if (pos == (mDataSet.size() - 1)) pagination.onNext(++page);
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

    public void updateList(ArrayList<Cat> cats){
        mDataSet = new ArrayList<>();
        mDataSet.addAll(cats);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView favorite;
        ImageView productImg;

        ImageButton removeBtn;
        ImageButton addBtn;
        TextView count;
        TextView title;

        MyViewHolder(View itemView) {
            super(itemView);
            favorite = itemView.findViewById(R.id.favorite_img);
            productImg = itemView.findViewById(R.id.img_of_product);
            removeBtn = itemView.findViewById(R.id.remove);
            addBtn = itemView.findViewById(R.id.add);
            count = itemView.findViewById(R.id.count_tv);
            title = itemView.findViewById(R.id.product_name);
//            downloadBtn = itemView.findViewById(R.id.download_img);
//            progressBar = itemView.findViewById(R.id.progressBar);
//            checkBox = itemView.findViewById(R.id.checkbox_is_favotite);
//            favoriteView = itemView.findViewById(R.id.favorite_view);
        }
    }

}
