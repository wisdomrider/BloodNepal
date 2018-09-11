package com.wisdomrider.bloodnepal.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wisdomrider.bloodnepal.Adapters.PeopleAdapter;
import com.wisdomrider.bloodnepal.Lists.Peoples;
import com.wisdomrider.bloodnepal.R;

import java.util.ArrayList;
import java.util.Random;
//    CREATED BY WISDOMRIDER
//    FOR APPS CONTACT avishekzone@gmail.com


public class FindPeoples extends BaseActivity {
    Spinner spinner;
    String[] bloods = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
   public ArrayList<Peoples> peoples = new ArrayList<>();

    RecyclerView leaderRecycle;
    PeopleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_peoples);
        wisdom.initSharedPreference("data");
        spinner = findViewById(R.id.blood);
        ArrayAdapter<String> adapter0 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloods);
        adapter0.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter0);
        loadbloodgroup();
        leaderRecycle=findViewById(R.id.peoples_recycle);
        mAdapter = new PeopleAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        leaderRecycle.setLayoutManager(mLayoutManager);
        leaderRecycle.setItemAnimator(new DefaultItemAnimator());
        leaderRecycle.setAdapter(mAdapter);
        choose(new View(this));

    }

    private void loadbloodgroup() {
        String blood = wisdom.getStringSharedPreference("blood_group");
        for (int i = 0; i < bloods.length; i++) {
            if (blood.equals(bloods[i])) {
                spinner.setSelection(i);
                return;
            }

        }
    }

    public void choose(View v) {
        try {
            wisdom.linearLayout(R.id.notfound).setVisibility(View.GONE);
            wisdom.progressBar(R.id.progress).setVisibility(View.VISIBLE);
            db.collection("users")
                    .whereEqualTo("blood_group", spinner.getSelectedItem().toString())
                    .limit(25)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            peoples.clear();
                            mAdapter.notifyDataSetChanged();
                            wisdom.progressBar(R.id.progress).setVisibility(View.GONE);
                            if (queryDocumentSnapshots.isEmpty()) {
                                nopeoplefound();
                            } else {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                    peoples.add(new Peoples(documentSnapshot.getString("name"), documentSnapshot.getString("picUrl"), documentSnapshot.getString("number")));
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    wisdom.progressBar(R.id.progress).setVisibility(View.GONE);
                    Toast.makeText(FindPeoples.this, "Sorry ! You dont have internet Connection.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e){
            wisdom.progressBar(R.id.progress).setVisibility(View.GONE);
            wisdom.toast("Check Internet Connection .");
        }

    }

    private void nopeoplefound() {
        wisdom.linearLayout(R.id.notfound).setVisibility(View.VISIBLE);
        Glide.with(this)
                .asGif()
                .load(R.drawable.oops)
                .into(wisdom.imageView(R.id.not_pic));
    }
}
