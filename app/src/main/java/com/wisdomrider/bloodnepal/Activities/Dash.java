package com.wisdomrider.bloodnepal.Activities;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wisdomrider.bloodnepal.Adapters.BloodAdapter;
import com.wisdomrider.bloodnepal.Adapters.PeopleAdapter;
import com.wisdomrider.bloodnepal.Lists.Blood;
import com.wisdomrider.bloodnepal.R;
import com.wisdomrider.bloodnepal.Services.Background;

import java.io.File;
import java.util.ArrayList;

import static com.wisdomrider.bloodnepal.Utils.ENUM.EXPIRY;
import static com.wisdomrider.bloodnepal.Utils.ENUM.URGENT;
import static com.wisdomrider.bloodnepal.Utils.ENUM.URGENT_EXPIRY;

public class Dash extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    NavigationView navigationView;
    public ArrayList<Blood> bloodList = new ArrayList<>();
    RecyclerView leaderRecycle;
    public BloodAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        loadPermission();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, new Toolbar(this), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        wisdom.initSharedPreference("data");
        setGlide(((ImageView) navigationView.getHeaderView(0).findViewById(R.id.userimg)), mAuth.getCurrentUser().getPhotoUrl().toString());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.name)).setText(mAuth.getCurrentUser().getDisplayName() + " [ " + wisdom.getStringSharedPreference("blood_group") + " ]");
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.email)).setText(mAuth.getCurrentUser().getEmail());
        spinner = findViewById(R.id.location);
        ArrayAdapter<String> adapter0 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zones);
        adapter0.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter0);
        selectLocation();
        leaderRecycle = findViewById(R.id.requests_recycle);
        mAdapter = new BloodAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        leaderRecycle.setLayoutManager(mLayoutManager);
        leaderRecycle.setItemAnimator(new DefaultItemAnimator());
        leaderRecycle.setAdapter(mAdapter);
        loadData();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    private void loadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                Toast.makeText(this, "We need This Permission for showing blood requests !", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {

            }
            else{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("We need this permission !");
                builder.setMessage("This permission allows us to show you blood requests any time anywhere . So please allow us this permission");
                builder.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        loadPermission();
                    }
                });
                builder.show();

            }
        }
    }

    private void loadData() {
        wisdom.progressBar(R.id.progress).setVisibility(View.VISIBLE);
        db.collection("requests")
                .whereEqualTo("location", spinner.getSelectedItem().toString())
                .limit(30)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryDocumentSnapshot = task.getResult();
                            wisdom.progressBar(R.id.progress).setVisibility(View.GONE);
                            bloodList.clear();
                            mAdapter.notifyDataSetChanged();
                            if (queryDocumentSnapshot.isEmpty()) {
                                noData();
                            } else {
                                wisdom.linearLayout(R.id.notfound).setVisibility(View.GONE);
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshot.getDocuments()) {
                                    Blood blood = documentSnapshot.toObject(Blood.class);
                                    blood.setUrl(documentSnapshot.getId());
                                    blood.setAdmin(blood.getUser().equals(mAuth.getUid()));
                                    if (blood.getLimit() < System.currentTimeMillis()) {
                                        db.collection("requests")
                                                .document(documentSnapshot.getId())
                                                .delete();
                                    } else {
                                        bloodList.add(blood);
                                    }
                                }
                                if (bloodList.isEmpty()) {
                                    noData();
                                }
                                mAdapter.notifyDataSetChanged();

                            }
                        } else {
                            wisdom.progressBar(R.id.progress).setVisibility(View.GONE);
                            wisdom.toast("Please Check Your Internet Connection !");
                        }
                    }
                });

    }

    private void noData() {
        wisdom.linearLayout(R.id.notfound).setVisibility(View.VISIBLE);
        Glide.with(this)
                .asGif()
                .load(R.drawable.oops)
                .into(wisdom.imageView(R.id.not_pic));

    }

    Spinner spinner;
    String[] Zones = {"Mechi", "Koshi", "Sagarmatha", "JanakPur", "Bagmati", "Narayani", "Gandaki", "Lumbini", "Dhawalagiri", "Rapti", "Karnali", "Bheri",
            "Seti", "Mahakali"};

    private void selectLocation() {
        String blood = wisdom.getStringSharedPreference("zone");
        for (int i = 0; i < Zones.length; i++) {
            if (blood.equals(Zones[i])) {
                spinner.setSelection(i);
                return;
            }

        }
    }

    public void choose(View v) {
        loadData();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void navigation(View v) {
        drawer.openDrawer(GravityCompat.START);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.find_peoples) {
            startActivity(new Intent(getApplicationContext(), FindPeoples.class));
        } else if (id == R.id.request_blood) {
            startActivity(new Intent(getApplicationContext(), RequestBlood.class));
        } else if (id == R.id.about) {
            startActivity(new Intent(getApplicationContext(), AboutApp.class));

        } else if (id == R.id.share) {
            wisdom.toast("Sharing Please Wait !");
            ApplicationInfo app = getApplicationContext().getApplicationInfo();
            String filePath = app.sourceDir;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "Share app via"));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
