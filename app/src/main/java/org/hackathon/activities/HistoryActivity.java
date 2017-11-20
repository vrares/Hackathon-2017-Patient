package org.hackathon.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.hackathon.R;
import org.hackathon.adapters.HistoryAdapter;
import org.hackathon.common.model.Consultation;
import org.hackathon.common.model.Patient;
import org.hackathon.common.net.Messenger;
import org.hackathon.common.store.ConsultationStore;
import org.hackathon.common.store.PatientStore;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class HistoryActivity extends AppCompatActivity implements Observer{

    private RecyclerView rvHistory;
    private int userId;
    private HistoryAdapter historyAdapter;
    private List<Consultation> consultationList;

    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        SharedPreferences preferences = getSharedPreferences(IdentificationActivity.PREF_NAME, MODE_PRIVATE);
        userId = preferences.getInt(IdentificationActivity.PATIENT_ID, -1);
        init();
    }

    private void init() {
        rvHistory = findViewById(R.id.history_rv);
        patient = PatientStore.getInstance().get(userId);
        consultationList = ConsultationStore.getInstance().list(new ConsultationStore.PatientFilter(userId));
        historyAdapter = new HistoryAdapter(consultationList, this);
        rvHistory.setAdapter(historyAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvHistory.setLayoutManager(linearLayoutManager);
        historyAdapter.notifyDataSetChanged();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }
}
