package com.example.winelab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.winelab.model.Cat;
import com.example.winelab.network.NetworkService;
import com.example.winelab.view.RecyclerViewProductAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewProductAdapter recyclerViewProductAdapter;

    private SearchView searchView;

    public static final String SIZE = "middle";
    private HashMap<String, Integer> map;

    private ArrayList<Cat> cats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view_products_container);
        searchView = findViewById(R.id.searchView);

        getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(cats -> {
                    recyclerViewProductAdapter = new RecyclerViewProductAdapter(cats, this);
                    recyclerView.setAdapter(recyclerViewProductAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    this.cats = cats;
                })
                .subscribe();


        final PublishSubject<String> subject = PublishSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                subject.onComplete();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                subject.onNext(text);
                return true;
            }
        });

        Disposable disposable = subject.debounce(600, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> {
                            Log.d("TAG", text);
                            String userInput = text.toLowerCase();
                            ArrayList<Cat> newFilteredList = null;
                            newFilteredList = new ArrayList<>();
                            for (Cat cat : cats) {
                                if (String.valueOf(cat.getWidth()).startsWith(userInput))
                                    newFilteredList.add(cat);
                            }
                            if (TextUtils.isEmpty(userInput)) {
                                recyclerViewProductAdapter.updateList(cats);
                            } else {
                                recyclerViewProductAdapter.updateList(newFilteredList);
                            }
                            Log.d("TAG", "onCreate: " + newFilteredList.size());

                        }
                );


    }


    private Observable<ArrayList<Cat>> getProducts() {

        map = new HashMap<>();
        map.put("limit", 10);
        map.put("page", 1);

        return NetworkService.getInstance()
                .getJSONApi()
                .getCats(map, SIZE);
    }


//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String query) {
//
//        String userInput = query.toLowerCase();
//        ArrayList<Cat> newFilteredList = new ArrayList<>();
//
//
//        for (Cat cat : cats) {
//            if (String.valueOf(cat.getWidth()).startsWith(userInput)) newFilteredList.add(cat);
//        }
//
//        if (newFilteredList.size() != 0) {
//            recyclerViewProductAdapter.updateList(newFilteredList);
//        }
//
//        return true;
//    }
}