package org.hackathon.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hackathon.R;
import org.hackathon.common.model.Drug;
import org.hackathon.common.model.Prescription;
import org.hackathon.common.store.DrugStore;

import java.util.List;

/**
 * Created by rares.vultur on 11/18/2017.
 */

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MyViewHolder> {

    private List<Prescription> prescriptionList;
    private Context context;

    public MedicationAdapter(List<Prescription> prescriptionList, Context context) {
        this.context = context;
        this.prescriptionList = prescriptionList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medication_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Prescription prescription = prescriptionList.get(position);
        final Drug drug = DrugStore.getInstance().get(prescription.getDrugId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(drug.getManualLink()));
                context.startActivity(intent);
            }
        });
        holder.tvTitle.setText(drug.getTitle());
        holder.tvInterval.setText(prescription.getFrequency());
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvInterval;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.medication_title);
            tvInterval = itemView.findViewById(R.id.medication_interval);
        }
    }
}
