package com.wisdomrider.bloodnepal.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wisdomrider.bloodnepal.Activities.Dash;
import com.wisdomrider.bloodnepal.Activities.FindPeoples;
import com.wisdomrider.bloodnepal.Lists.Blood;
import com.wisdomrider.bloodnepal.Lists.Peoples;
import com.wisdomrider.bloodnepal.R;

import java.util.concurrent.ExecutionException;

import static com.wisdomrider.bloodnepal.Utils.ENUM.URGENT;

public class BloodAdapter extends RecyclerView.Adapter<BloodAdapter.MyViewHolder> {
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blood, parent, false);
        return new MyViewHolder(itemView);

    }

    Dash dash;

    public BloodAdapter(Dash peoples) {
        this.dash = peoples;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Blood blood = dash.bloodList.get(position);
        holder.grp_name.setText(blood.getBloodGroup());
        holder.location.setText(blood.getHospital());
        if (blood.getUrgency().equals(URGENT))
            holder.urgency.setText("Urgent !");
        else
            holder.urgency.setText(blood.getUrgency());
        holder.phone.setText(blood.getPhone());
        holder.time.setReferenceTime(blood.getTime());
        if (blood.isAdmin())
            holder.dlt.setVisibility(View.VISIBLE);
        else
            holder.dlt.setVisibility(View.GONE);
        holder.dlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(dash);
                dialog.setTitle("Are you sure ?");
                dialog.setMessage("Deleted requests cannot be recovered");
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dash.wisdom.progressBar(R.id.progress).setVisibility(View.VISIBLE);
                        dash.db.collection("requests")
                                .document(blood.getUrl())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dash.wisdom.progressBar(R.id.progress).setVisibility(View.GONE);
                                        dash.bloodList.remove(position);
                                        dash.mAdapter.notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                dash.wisdom.toast("Please Check  Your Internet !");
                                dash.wisdom.progressBar(R.id.progress).setVisibility(View.GONE);
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialog(blood);
            }
        });
    }

    private void loadDialog(final Blood blood) {
        final Dialog dialog = new Dialog(dash);
        dialog.setContentView(R.layout.blood_details);
        ((TextView) dialog.findViewById(R.id.blood_for)).setText("For : " + blood.getReceiver());
        ((TextView) dialog.findViewById(R.id.blood_grp)).setText("Blood Group : " + blood.getBloodGroup());
        ((TextView) dialog.findViewById(R.id.age_group)).setText("Age Group : " + blood.getAge());
        if (blood.getDescription().isEmpty())
            dialog.findViewById(R.id.description).setVisibility(View.GONE);
        else
            ((TextView) dialog.findViewById(R.id.description)).setText("Description : " + blood.getDescription());

        if (blood.getUrgency().equals(URGENT))
            ((TextView) dialog.findViewById(R.id.urgency)).setText("Urgent Required !");
        else
            ((TextView) dialog.findViewById(R.id.urgency)).setText("Blood for  : " + blood.getUrgency());

        ((TextView)dialog.findViewById(R.id.phone)).setText("Phone number : "+blood.getPhone());
        ((TextView)dialog.findViewById(R.id.location)).setText("Location : "+blood.getHospital());

        dialog.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", blood.getPhone(), null));
                dash.startActivity(intent);
            }
        });
        dialog.findViewById(R.id.message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(dash, "Sending you to  message ", Toast.LENGTH_SHORT).show();
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address",blood.getPhone());
                smsIntent.putExtra("sms_body","Hi i want to donate my blood for "+blood.getReceiver());
                dash.startActivity(smsIntent);
            }
        });
        if(blood.getFb().isEmpty())
            dialog.findViewById(R.id.messenger).setVisibility(View.GONE);
        else{
            dialog.findViewById(R.id.messenger).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.me/"+blood.getFb()));
                    dash.startActivity(browserIntent);
                }
            });
        }
        dialog.findViewById(R.id.cross).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(dash, "Sharing Please Wait !", Toast.LENGTH_SHORT).show();
                 String shareBody ="Blood Required ! \n"+blood.getBloodGroup()+" blood group \n("+blood.getUrgency()+")  on "+blood.getHospital()+"\n Please Help ! \n Call : "+blood.getPhone();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
               dash.startActivity(Intent.createChooser(sharingIntent, "Share Blood request using"));

            }
        });
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return dash.bloodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView grp_name, location, urgency, phone;
        ImageView dlt;
        CardView cardView;
        RelativeTimeTextView time;

        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            grp_name = itemView.findViewById(R.id.blood_grp);
            dlt = itemView.findViewById(R.id.delete);
            location = itemView.findViewById(R.id.location);
            urgency = itemView.findViewById(R.id.urgency);
            phone = itemView.findViewById(R.id.phone);
            time = itemView.findViewById(R.id.time);
        }
    }

}
