package com.wisdomrider.bloodnepal.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.wisdomrider.bloodnepal.R;
import com.wisdomrider.bloodnepal.Services.Background;

import static com.wisdomrider.bloodnepal.Utils.ENUM.Zones;

public class MainActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 222;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInButton = findViewById(R.id.signin);
        if(Background.isConnected!=1)
            startService(new Intent(this,Background.class));
        wisdom.initSharedPreference("data");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        if (mAuth.getCurrentUser() != null) {
            signInButton.setVisibility(View.GONE);
            wisdom.progressBar(R.id.progress).setVisibility(View.VISIBLE);
            db.collection("users")
                    .document(mAuth.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    wisdom.edit.putString("user",mAuth.getUid());
                                    wisdom.edit.putString("blood_group", documentSnapshot.getString("blood_group"));
                                    wisdom.edit.putString("number", documentSnapshot.getString("number"));
                                    wisdom.edit.putString("zone", documentSnapshot.getString("zone"));
                                    wisdom.edit.apply();
                                    nextpage(1000);

                                } else {
                                    showDialog(mAuth.getCurrentUser());
                                }
                            }
                        }
                    });
        }


    }

    private void nextpage(long i) {
        new CountDownTimer(i, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {


            }

            @Override
            public void onFinish() {
                finish();
                startActivity(new Intent(MainActivity.this, Dash.class));
            }
        }.start();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInButton.setVisibility(View.GONE);
        wisdom.progressBar(R.id.progress).setVisibility(View.VISIBLE);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                wisdom.toast("Something went wrong.");
            }
        }
    }

    public void showDialog(final FirebaseUser user) {
        wisdom.progressBar(R.id.progress).setVisibility(View.GONE);
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.register);
        String[] bloods = {"Select your blood group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        final Spinner spinner = dialog.findViewById(R.id.spinner);
        final Spinner spinner1 = dialog.findViewById(R.id.location);
        ArrayAdapter<String> adapter0 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloods);
        adapter0.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter0);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Zones);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner1.setAdapter(adapter1);
        final EditText text = dialog.findViewById(R.id.phone);
        dialog.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String num = text.getText().toString().trim();
                if (num.isEmpty()) {
                    wisdom.toast("Please type your number so that someone can call you when needed !");
                } else if (num.length() !=10) {
                    wisdom.toast("Please type valid number");
                } else if (spinner.getSelectedItemPosition() == 0) {
                    wisdom.toast("Plase select your blood group !");
                } else if (spinner1.getSelectedItemPosition() == 0) {
                    wisdom.toast("Please select your zone ");
                } else {
                    dialog.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                    hashMap.clear();
                    hashMap.put("name", mAuth.getCurrentUser().getDisplayName());
                    hashMap.put("picUrl", mAuth.getCurrentUser().getPhotoUrl().toString());
                    hashMap.put("number", num);
                    hashMap.put("zone", spinner1.getSelectedItem().toString());
                    hashMap.put("blood_group", spinner.getSelectedItem().toString());
                    hashMap.put("id", mAuth.getUid());
                    db.collection("users")
                            .document(mAuth.getUid())
                            .set(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    wisdom.edit.putString("blood_group", spinner.getSelectedItem().toString());
                                    wisdom.edit.putString("number", num);
                                    wisdom.edit.putString("zone", spinner1.getSelectedItem().toString());
                                    wisdom.edit.apply();
                                    wisdom.toast("Thank you for registration . you will get notification when someone needs blood !");
                                    nextpage(1000);
                                }
                            });
                    v.setVisibility(View.GONE);
                }

            }
        });
        dialog.setCancelable(false);
        dialog.show();

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = mAuth.getCurrentUser();
                            db.collection("users")
                                    .document(mAuth.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    nextpage(1000);
                                                } else {
                                                    showDialog(user);

                                                }

                                            }

                                        }
                                    });
                        } else {
                            wisdom.progressBar(R.id.progress).setVisibility(View.GONE);
                            signInButton.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}
