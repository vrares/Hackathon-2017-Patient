package org.hackathon.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.hackathon.R;
import org.hackathon.common.model.Consultation;
import org.hackathon.common.model.Doctor;
import org.hackathon.common.model.Patient;
import org.hackathon.common.net.Messenger;
import org.hackathon.common.store.ConsultationStore;
import org.hackathon.common.store.DoctorStore;
import org.hackathon.common.store.PatientStore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static org.hackathon.activities.IdentificationActivity.PATIENT_ID;

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener, Observer{

    private static final String TAG = "tag";
    private static final String CONSULTATION_LIST = "consultations";
    public static final String DOCTOR_ID = "doctorId";

    private TextView tvUserName;
    private TextView tvDoctorName;
    private TextView tvAppointment;
    private TextView tvDoctorAddress;
    private Button btnMakeAppointment;
    private Button btnHistory;
    private Button btnUrgentAppointment;
    private Button btnLogout;
    private LinearLayout llReminder;

    private int userId;
    private int doctorId;
    private Patient patient;
    private Doctor doctor;
    private List<Consultation> consultationList;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        tvUserName = findViewById(R.id.tv_details_name);
        tvDoctorName = findViewById(R.id.tv_details_doc_name);
        tvAppointment = findViewById(R.id.tv_reminder_date);
        tvDoctorAddress = findViewById(R.id.details_tv_address);
        btnMakeAppointment = findViewById(R.id.details_btn_appointments);
        btnHistory = findViewById(R.id.details_btn_history);
        btnLogout = findViewById(R.id.details_btn_logout);
        btnUrgentAppointment = findViewById(R.id.details_btn_sos);
        llReminder = findViewById(R.id.details_ll_reminder);


        btnMakeAppointment.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnUrgentAppointment.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        preferences = getSharedPreferences(IdentificationActivity.PREF_NAME, MODE_PRIVATE);

        init();

    }

    private void init() {
        userId = preferences.getInt(PATIENT_ID, -1);
        Patient patient = PatientStore.getInstance().get(userId);
        doctorId = patient.getDoctorId();
        patient = PatientStore.getInstance().get(userId);
        doctor = DoctorStore.getInstance().get(patient.getDoctorId());
        tvUserName.setText(patient.getFirstName() + " " + patient.getLastName());
        tvDoctorName.setText(doctor.getFirstName() + " " + doctor.getLastName());
        tvDoctorAddress.setText(doctor.getAddress());

        consultationList = ConsultationStore.getInstance().list(new ConsultationStore.PatientFilter(userId));
        if (consultationList.size() > 0) {
            llReminder.setVisibility(View.VISIBLE);
            Date date = new Date(consultationList.get(0).getDate());
            String formatDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(date);
            tvAppointment.setText(formatDate);
        }



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
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.details_btn_appointments:
                intent = new Intent(this, AppointmentActivity.class);
                intent.putExtra(DOCTOR_ID, doctorId);
                startActivity(intent);
                break;

            case R.id.details_btn_history:
                List<Consultation> validatedConsultation = new ArrayList<>();
                for (Consultation consultation : consultationList) {
                    if (isPastConsultation(new Date(consultation.getDate()))) {
                        validatedConsultation.add(consultation);
                    }
                }
                    intent = new Intent(this, HistoryActivity.class);
                    startActivity(intent);
                break;

            case R.id.details_btn_sos:
                String number = doctor.getPhone();
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
                break;

            case R.id.details_btn_logout:
                userId = -1;
                preferences.edit().putInt(PATIENT_ID, userId).apply();
                intent = new Intent(this, IdentificationActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    private boolean isPastConsultation(Date prescriptionDate) {
        return new Date().after(prescriptionDate);
    }
}
