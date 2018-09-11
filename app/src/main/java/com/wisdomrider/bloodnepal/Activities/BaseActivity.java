package com.wisdomrider.bloodnepal.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/*
CREated by avi(Wisdomrider)
on 9/2/2018
*/
public class BaseActivity extends com.wisdomrider.Activities.BaseActivity {
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;
    HashMap<String, Object> hashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }
    public void setGlide(int id,String url){
        Glide.with(this)
                .load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .apply(RequestOptions.circleCropTransform())
                .into(wisdom.imageView(id));

    }    public void setGlide(ImageView imageView, String url){
        Glide.with(this)
                .load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);

    }
}
