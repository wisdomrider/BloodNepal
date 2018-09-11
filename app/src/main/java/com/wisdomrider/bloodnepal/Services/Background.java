package com.wisdomrider.bloodnepal.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.wisdomrider.bloodnepal.Activities.MainActivity;
import com.wisdomrider.bloodnepal.Lists.Blood;
import com.wisdomrider.bloodnepal.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.wisdomrider.bloodnepal.Utils.ENUM.URGENT;

/*
CREated by avi(Wisdomrider)
on 9/10/2018
*/
public class Background extends Service {
    public static int isConnected = 0;
    public FirebaseFirestore db;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isConnected = 1;
        preferences = this.getSharedPreferences("data", 0);
        editor = preferences.edit();
        db = FirebaseFirestore.getInstance();
        loadBingoDB();
        return START_STICKY;
    }

    private void loadBingoDB() {
        String location = preferences.getString("zone", "");
        if (!location.isEmpty()) {
            db.collection("requests")
                    .whereEqualTo("location", location)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            for (DocumentChange documentSnapshot : queryDocumentSnapshots.getDocumentChanges()) {
                                Blood blood = documentSnapshot.getDocument().toObject(Blood.class);
                                blood.setUrl(documentSnapshot.getDocument().getId());
                                try {
                                    checkAndLoad(blood);
                                } catch (JSONException e1) {

                                }
                            }
                        }
                    });

        }
    }

    private void checkAndLoad(Blood blood) throws JSONException {
        String notifications = preferences.getString("notifications", "");
        if (!notifications.contains(blood.getUrl())) {
            JSONArray array;
            if (notifications.isEmpty()) array = new JSONArray();
            else array = new JSONArray(notifications);
            checkForNotification(blood);
            array.put(blood.getUrl());
            editor.putString("notifications", array.toString());
            editor.apply();
        }
    }

    private void checkForNotification(Blood blood) {
        if (!preferences.getString("user","").equals(blood.getUser())) {
            if (!isShowing) {
                showBackground(blood);
            } else {
                addNotification(blood);
            }
        }
    }

    private void addNotification(Blood blood) {
        NotificationManager notificationManager;
        notificationManager = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notice);
        mBuilder.setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContent(contentView);
        contentView.setTextViewText(R.id.text, Html.fromHtml("Blood Needed ! <br> Blood Grp : " + blood.getBloodGroup() + "<br>Number  : " + blood.getPhone()));
        notificationManager.notify(new Random().nextInt(999), mBuilder.build());

    }

    public void showBackground(Blood blood) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this))
                show(blood);

        } else {
            show(blood);
        }
    }

    WindowManager windowManager;
    View itemView;

    boolean isShowing = false;

    public void show(final Blood blood) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        itemView = LayoutInflater.from(this)
                .inflate(R.layout.blood_details, null, false);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LOCAL_FOCUS_MODE,
                PixelFormat.TRANSLUCENT);
        TextView title, receiver, blood_grp, age, desc, phone, location, urgency;
        ImageView call, message, fb, cross;
        title = itemView.findViewById(R.id.title);
        receiver = itemView.findViewById(R.id.blood_for);
        blood_grp = itemView.findViewById(R.id.blood_grp);
        age = itemView.findViewById(R.id.age_group);
        desc = itemView.findViewById(R.id.description);
        phone = itemView.findViewById(R.id.phone);
        location = itemView.findViewById(R.id.location);
        urgency = itemView.findViewById(R.id.urgency);
        call = itemView.findViewById(R.id.call);
        message = itemView.findViewById(R.id.message);
        fb = itemView.findViewById(R.id.messenger);
        cross = itemView.findViewById(R.id.cross);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/font.ttf");
        receiver.setTypeface(tf);
        blood_grp.setTypeface(tf);
        age.setTypeface(tf);
        desc.setTypeface(tf);
        phone.setTypeface(tf);
        itemView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
                Toast.makeText(getApplicationContext(), "Sharing Please Wait !", Toast.LENGTH_SHORT).show();
                String shareBody = "Blood Required ! \n" + blood.getBloodGroup() + " blood group \n(" + blood.getUrgency() + ")  on " + blood.getHospital() + "\n Please Help ! \n Call : " + blood.getPhone();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share Blood request using "));
            }
        });
        location.setTypeface(tf);
        urgency.setTypeface(tf);
        title.setText("Blood Request !");
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
            }
        });
        receiver.setText("For : " + blood.getReceiver());
        blood_grp.setText("Blood Group : " + blood.getBloodGroup());
        age.setText("Age Group : " + blood.getAge());
        if (blood.getDescription().isEmpty())
            desc.setVisibility(View.GONE);
        else
            desc.setText("Description : " + blood.getDescription());
        if (blood.getUrgency().equals(URGENT))
            urgency.setText("Urgent Required !");
        else
            urgency.setText("Blood for  : " + blood.getUrgency());

        phone.setText("Phone number : " + blood.getPhone());
        location.setText("Location : " + blood.getHospital());

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", blood.getPhone(), null));
                startActivity(intent);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
                Toast.makeText(Background.this, "Sending you to  message ", Toast.LENGTH_SHORT).show();
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", blood.getPhone());
                smsIntent.putExtra("sms_body", "Hi i want to donate my blood for " + blood.getReceiver());
                startActivity(smsIntent);
            }
        });
        if (blood.getFb().isEmpty())
            fb.setVisibility(View.GONE);
        else {
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.me/" + blood.getFb()));
                    startActivity(browserIntent);
                }
            });
        }
        isShowing = true;
        windowManager.addView(itemView, params);

    }

    private void remove() {
        isShowing = false;
        windowManager.removeViewImmediate(itemView);

    }

}
