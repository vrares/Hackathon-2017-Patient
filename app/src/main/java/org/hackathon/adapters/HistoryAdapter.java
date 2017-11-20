package org.hackathon.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hackathon.R;
import org.hackathon.activities.PrescriptionActivity;
import org.hackathon.common.model.Consultation;
import org.hackathon.common.model.Diagnostic;
import org.hackathon.common.store.DiagnosticStore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hackathon.activities.PrescriptionActivity.CONSULTATION_ID;

/**
 * Created by rares.vultur on 11/18/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<Consultation> consultationList;
    private Context context;

    public HistoryAdapter(List<Consultation> consultationList, Context context) {
        this.consultationList = consultationList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Consultation consultation = consultationList.get(position);
        Diagnostic diagnostic = DiagnosticStore.getInstance().get(consultation.getDiagnosticId());
        if (diagnostic == null) {
            holder.tvIssue.setText("Diagnostic not yet set");

        } else {
            holder.tvIssue.setText(diagnostic.getTitle());
        }
        Date date = new Date(consultation.getDate());
        String formatDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(date);
        holder.tvDate.setText(formatDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PrescriptionActivity.class);
                intent.putExtra(CONSULTATION_ID, consultation.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return consultationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvIssue;
        private TextView tvDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvIssue = itemView.findViewById(R.id.history_issue);
            tvDate = itemView.findViewById(R.id.history_date);
        }
    }

}

