package com.wisdomrider.bloodnepal.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.wisdomrider.bloodnepal.R;

import static com.wisdomrider.bloodnepal.Utils.ENUM.ENCRYPT1;
import static com.wisdomrider.bloodnepal.Utils.ENUM.ENCRYPT2;
import static com.wisdomrider.bloodnepal.Utils.ENUM.email;
//    CREATED BY WISDOMRIDER
//    FOR APPS CONTACT avishekzone@gmail.com


public class AboutApp extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        wisdom.initEncryption(ENCRYPT1, ENCRYPT2);
        wisdom.textView(R.id.about).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String shareBody = "Secret Key : " + wisdom.encrypt(email);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "App is made using !"));
                return true;
            }
        });
        String email="developerwisdomrider@gmail.com";
        wisdom.textView(R.id.about).setText(Html.fromHtml("This app is developed by me  to help all of my brother and sisters from <font color='red'>Nepal</font><br><br>  So that no people in nepal have to face a problem in getting blood for them or their loved ones .<br>This app got many features with the help of which the vicitim can easily get blood.<br><br>For any suggestions,feedbacks or help contact me on Facebook <br>m.me/wisdomrider <br><br> Developed By : Wisdomrider (Avishek Adhikari) <br>" +
                "For making any type of android apps <br>Contact me on developerwisdomrider@gmail.com <br> or call me directly 9806021524 <br><br>Copyright Project 2018<br><br>Thank you for using our app !"));

    }
}
