package org.hackathon.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.hackathon.R;
import org.hackathon.adapters.MedicationAdapter;
import org.hackathon.common.model.Consultation;
import org.hackathon.common.model.Prescription;
import org.hackathon.common.net.Messenger;
import org.hackathon.common.store.ConsultationStore;
import org.hackathon.common.store.PrescriptionStore;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PrescriptionActivity extends AppCompatActivity implements Observer {

    public static final String CONSULTATION_ID = "consultationId";

    private int consultationId;
    private Consultation consultation;
    private MedicationAdapter medicationAdapter;

    private TextView tvPrescriptionId;
    private TextView tvPrescriptionDate;
    private TextView tvPrescriptionPeriod;
    private RecyclerView rvMedication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        consultationId = getIntent().getIntExtra(CONSULTATION_ID, -1);
        consultation = ConsultationStore.getInstance().get(consultationId);
        init();
    }

    private void init() {
        tvPrescriptionId = findViewById(R.id.prescription_tv_prescription_id);
        tvPrescriptionDate = findViewById(R.id.prescription_tv_date);
        rvMedication = findViewById(R.id.medication_rv);

        List<Prescription> prescriptionList = PrescriptionStore.getInstance().list(new PrescriptionStore.ConsultationFilter(consultationId));
        rvMedication = findViewById(R.id.medication_rv);
        medicationAdapter = new MedicationAdapter(prescriptionList, this);
        rvMedication.setAdapter(medicationAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMedication.setLayoutManager(linearLayoutManager);
        medicationAdapter.notifyDataSetChanged();

        tvPrescriptionId.setText(Integer.toString(consultationId));
        Date prescriptionDate = new Date(consultation.getDate());
        String formatPrescriptionDate =  new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(prescriptionDate);
        tvPrescriptionDate.setText(formatPrescriptionDate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Messenger.getInstance().addObserver(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Messenger.getInstance().deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
