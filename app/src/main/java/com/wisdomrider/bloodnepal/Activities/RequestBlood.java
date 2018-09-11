package com.wisdomrider.bloodnepal.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.wisdomrider.bloodnepal.Lists.Blood;
import com.wisdomrider.bloodnepal.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static com.wisdomrider.bloodnepal.Utils.ENUM.URGENT;
//    CREATED BY WISDOMRIDER
//    FOR APPS CONTACT avishekzone@gmail.com


public class RequestBlood extends BaseActivity {
    private int mYear, mMonth, mDay, mHour, mMinute;
    Spinner spinner, spinner1, spinner2, spinner3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_blood);
        String[] bloods = {"Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        String[] Ages = {"Select Age Interval", "0-5", "5-10", "10-15", "15-20", "20-30", "30-50", "50-70", "70-90", "90-110"};
        String[] Zones = {"Select Zone", "Mechi", "Koshi", "Sagarmatha", "JanakPur", "Bagmati", "Narayani", "Gandaki", "Lumbini", "Dhawalagiri", "Rapti", "Karnali", "Bheri",
                "Seti", "Mahakali"};
        final String[] Time = {"Select Urgency Level", "Urgent (Within 2 Hours)", "Select Time (Until)"};
        spinner = findViewById(R.id.blood_grp);
        spinner1 = findViewById(R.id.zone);
        spinner2 = findViewById(R.id.age_group);
        spinner3 = findViewById(R.id.time);
        wisdom.editText(R.id.blood_for).requestFocus();
        wisdom.textView(R.id.required).setText(Html.fromHtml("Required field are coloured with <font color='red'>Red</font><br>" +
                "Long click on any field to know about it in details ! "));
        ArrayAdapter<String> adapter0 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloods);
        adapter0.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter0);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zones);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner1.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Ages);
        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2.setAdapter(adapter2);
        final ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Time);
        adapter3.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);


                    final DatePickerDialog datePickerDialog = new DatePickerDialog(RequestBlood.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(final DatePicker view1, final int year,
                                                      final int monthOfYear, final int dayOfMonth) {
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(RequestBlood.this,
                                            new TimePickerDialog.OnTimeSetListener() {

                                                @Override
                                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                                      int minute) {
                                                    c.set(Calendar.YEAR, year);
                                                    c.set(Calendar.MONTH, monthOfYear);
                                                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                    c.set(Calendar.MINUTE, minute);
                                                    c.set(Calendar.SECOND, 0);
                                                    String weekdays[] = new DateFormatSymbols(Locale.US).getWeekdays();
                                                    DateFormat sdf = DateFormat.getDateTimeInstance();
                                                    Time[2] = sdf.format(c.getTime()).replace(":00", "") + " , " + weekdays[c.get(Calendar.DAY_OF_WEEK)];
                                                    limit = c.getTimeInMillis();
                                                    adapter3.notifyDataSetChanged();
                                                }
                                            }, mHour, mMinute, false);
                                    timePickerDialog.show();

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loadDialog(v.getId());
                return true;
            }
        };

        wisdom.editText(R.id.blood_for).setOnLongClickListener(longClickListener);
        wisdom.editText(R.id.description).setOnLongClickListener(longClickListener);
        wisdom.editText(R.id.number).setOnLongClickListener(longClickListener);
        wisdom.editText(R.id.hospital_name).setOnLongClickListener(longClickListener);
        wisdom.editText(R.id.messanger_id).setOnLongClickListener(longClickListener);
    }

    TextView textView;

    private void loadDialog(int id) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.description);
        textView = dialog.findViewById(R.id.description);
        if (id == R.id.blood_for) {
            Message("Receiver Name <br> This information is about the victim name who wants the blood . It is saved so that other users can know who wants blood.");
        } else if (id == R.id.description) {
            Message("Description <br> This information is about the description of the victim.");

        } else if (id == R.id.number) {
            Message("PhoneNumber <br> This information is about the phone number of the victim so that other can contact him/her");

        } else if (id == R.id.hospital_name) {
            Message("Hospital/Place Name <br> This information is about the name of the hospital or the place where victim lives . So that other users can go and donate blood easily ");

        } else if (id == R.id.messanger_id) {
            Message("Facebook Username <br>This information is about the facebook username so that other users can directly message the victim via message on facebook . <br> For getting Victim/Your username go to facebook.com from web and then go to your profile . After that copy the url and the part after www.facebook.com/ will be your username !<br> (Alternate way)  You can open your messenger and in third tab you can see m.me/something the part after m.me/ is your fb username .");
        }
        dialog.show();
    }

    private void Message(String a) {
        textView.setText(Html.fromHtml(a));
    }


    long limit = 0;

    public void submit(View v) {
        String receiver = wisdom.editText(R.id.blood_for).getText().toString().trim();
        String blood_group = spinner.getSelectedItem().toString().trim();
        String age_group = spinner2.getSelectedItem().toString().trim();
        String location = spinner1.getSelectedItem().toString().trim();
        String urgency = "";
        if (spinner3.getSelectedItemPosition() == 1) {
            urgency = URGENT;
            limit = System.currentTimeMillis() + 18000000;
        } else if (spinner3.getSelectedItemPosition() == 2)
            urgency = spinner3.getSelectedItem().toString().trim();
        String description = wisdom.editText(R.id.description).getText().toString().trim();
        String number = wisdom.editText(R.id.number).getText().toString().trim();
        if (receiver.isEmpty()) {
            wisdom.toast("Please Type Receiver Name !");
        } else if (spinner.getSelectedItemPosition() == 0) {
            wisdom.toast("Please select blood group");
        } else if (spinner1.getSelectedItemPosition() == 0) {
            wisdom.toast("Please select location");
        } else if (spinner2.getSelectedItemPosition() == 0) {
            wisdom.toast("Please select Age group.");
        } else if (spinner3.getSelectedItemPosition() == 0) {
            wisdom.toast("Please select Urgency.");
        } else if (number.length() != 10 || number.isEmpty()) {
            wisdom.toast("Please enter valid number !");
        } else if (wisdom.textView(R.id.hospital_name).getText().toString().trim().isEmpty()) {
            wisdom.toast("Please provide place name !");
        } else {
            String[] receivers = receiver.split(" ");
            receiver = "";
            for (String a : receivers) {
                a = a.substring(0, 1).toUpperCase() + a.substring(1, a.length());
                receiver = receiver + " " + a;
            }
            wisdom.button(R.id.submit).setVisibility(View.GONE);
            wisdom.progressBar(R.id.progress).setVisibility(View.VISIBLE);
            Blood blood = new Blood(age_group, blood_group, description, wisdom.editText(R.id.messanger_id).getText().toString().trim(), wisdom.editText(R.id.hospital_name).getText().toString().trim(), limit, location, number, receiver, System.currentTimeMillis(), urgency, mAuth.getUid());
            db.collection("requests")
                    .document("REQ_" + new Random().nextInt(900) + (receiver.length() + blood_group.length() + mAuth.getUid().length() + urgency.length()))
                    .set(blood)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {
                            finish();
                            wisdom.toast("Request Uploaded SuccesFully !");
                            startActivity(new Intent(RequestBlood.this, Dash.class));
                        }
                    });


        }

    }
}
