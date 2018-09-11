package com.wisdomrider.bloodnepal.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wisdomrider.bloodnepal.Activities.FindPeoples;
import com.wisdomrider.bloodnepal.Lists.Peoples;
import com.wisdomrider.bloodnepal.R;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.MyViewHolder> {
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.people, parent, false);
        return new MyViewHolder(itemView);

    }

    FindPeoples peoples;

    public PeopleAdapter(FindPeoples peoples) {
        this.peoples = peoples;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Peoples people = peoples.peoples.get(position);
        holder.name.setText(people.name);
        holder.num.setText(people.number);
        Glide.with(peoples)
                .load(people.pro_pic)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.propic);
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", people.number, null));
                peoples.startActivity(intent);
            }
        });
        holder.msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(peoples, "Sending you to  message ", Toast.LENGTH_SHORT).show();
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address",people.number);
                smsIntent.putExtra("sms_body","Hi "+people.name+" ");
                peoples.startActivity(smsIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return peoples.peoples.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, num;
        ImageView propic,call,msg;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            call=itemView.findViewById(R.id.call);
            msg=itemView.findViewById(R.id.message);
            num = itemView.findViewById(R.id.phone);
            propic = itemView.findViewById(R.id.pro_pic);

        }
    }

}
