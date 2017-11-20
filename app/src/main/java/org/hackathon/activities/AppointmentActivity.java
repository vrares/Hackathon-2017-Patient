package org.hackathon.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.hackathon.R;
import org.hackathon.common.model.Consultation;
import org.hackathon.common.net.Messenger;
import org.hackathon.common.store.ConsultationStore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static org.hackathon.activities.IdentificationActivity.PATIENT_ID;
import static org.hackathon.activities.IdentificationActivity.PREF_NAME;
import static org.hackathon.activities.UserDetailsActivity.DOCTOR_ID;

public class AppointmentActivity extends AppCompatActivity implements Observer, View.OnClickListener{

    private Button btnDate;
    private Button btnCreateAppointment;
    private EditText etSymptoms;
    private TextView tvDate;
    private Consultation newConsultation;
    private Calendar newCalendar;

    private int doctorId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        init();
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

    private void init() {
        btnDate = findViewById(R.id.appointment_btn_calendar);
        tvDate = findViewById(R.id.appointment_tv_date);
        btnCreateAppointment = findViewById(R.id.appointment_btn_create);
        etSymptoms = findViewById(R.id.appointment_et_symptoms);
        btnDate.setOnClickListener(this);
        btnCreateAppointment.setOnClickListener(this);
        doctorId = getIntent().getIntExtra(DOCTOR_ID, -1);
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        userId = preferences.getInt(PATIENT_ID, MODE_PRIVATE);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_btn_calendar:
                final Calendar calendar = Calendar.getInstance();
                newCalendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);


                final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        newCalendar.set(Calendar.YEAR, year);
                        newCalendar.set(Calendar.MONTH, month);
                        newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        List<Consultation> consultationList = ConsultationStore.getInstance().list(new ConsultationStore.DoctorFilter(doctorId));
                        for (Consultation consultation : consultationList) {
                            if (calendar.getTime().compareTo(new Date(consultation.getDate())) == 0) {
                                Toast.makeText(AppointmentActivity.this, "same time", Toast.LENGTH_SHORT).show();
                            } else {
                                String pickedDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(newCalendar.getTime());
                                tvDate.setText(pickedDate);
                            }
                        }

                    }
                }, year, month, day);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        newCalendar.set(Calendar.MINUTE, minute);
                        datePickerDialog.show();

                    }
                }, hour, minute, false);

                timePickerDialog.show();

                break;

            case R.id.appointment_btn_create:
                if (etSymptoms.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please fill in your symptoms", Toast.LENGTH_SHORT).show();
                } else {

                    newConsultation = new Consultation();
                    newConsultation.setDate((newCalendar.getTime().getTime()));
                    newConsultation.setPatientId(userId);
                    newConsultation.setDescription(etSymptoms.getText().toString());



                    Messenger.UpdateMessage message = new Messenger.UpdateMessage();
                    message.setConsultations(new ArrayList<Consultation>());
                    message.getConsultations().add(newConsultation);

                    Messenger.getInstance().push(message);

                    Toast.makeText(this, "Appointment set!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, UserDetailsActivity.class);
                    startActivity(intent);
                }
                break;

        }


    }
}
