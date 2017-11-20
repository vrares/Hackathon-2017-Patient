package org.hackathon.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.hackathon.R;
import org.hackathon.common.model.Entity;
import org.hackathon.common.net.Messenger;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class IdentificationActivity extends AppCompatActivity implements Observer, View.OnClickListener {

    public static final String PREF_NAME = "userPref";
    public static final String PATIENT_ID = "patientId";

    private EditText etUser;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        etUser = findViewById(R.id.register_et_user);
        etPassword = findViewById(R.id.register_et_pass);
        btnLogin = findViewById(R.id.register_btn_register_action);
        btnLogin.setOnClickListener(this);




    }

    @Override
    protected void onStart() {
        super.onStart();
        Messenger.getInstance().addObserver(this);
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int id = preferences.getInt(PATIENT_ID, -1);
        if (id != -1) {
            Messenger.getInstance().start();
            Intent intent = new Intent(this, UserDetailsActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

//        Patient x = new Patient();
//        x.getDoctorId();
//        Doctor doctor = DoctorStore.getInstance().get(x.getDoctorId());
//
//        Consultation caca = new Consultation();
//        //caca.set bla bla
//        Messenger.UpdateMessage message = new Messenger.UpdateMessage();
//        List<Consultation> consultations = new ArrayList<>();
//        consultations.add(caca);
//        message.setConsultations(consultations);
//
//        Messenger.getInstance().push(message);


        Messenger.getInstance().deleteObserver(this);
    }



    void login() {
        new Thread() {
            public void run() {
                Messenger.AuthenticateRequest request = new Messenger.AuthenticateRequest();
                request.setType(Messenger.AuthenticateRequest.TYPE_PATIENT);
                request.setUser(etUser.getText().toString());
                request.setPass(etPassword.getText().toString());

                try {
                    final Messenger.AuthenticateResponse response = Messenger.getInstance().authenticate(request);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.getId() == Entity.NO_ID) {
                                Toast.makeText(IdentificationActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
                            } else {
                                Messenger.getInstance().start();
                                SharedPreferences editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                                editor.edit().putInt(PATIENT_ID, response.getId()).apply();
                                Intent intent = new Intent(IdentificationActivity.this, UserDetailsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn_register_action:
                login();
                break;
        }
    }
}
