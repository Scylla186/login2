package com.example.login2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private TextInputLayout   til_name, til_user, til_password, til_confirm_password;
    private TextInputEditText et_name, et_user, et_password, et_confirm_password;
    private Button            btn_register;
    private ProgressBar       progress;

    private FirebaseAuth      auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IdiomaUtils.aplicarIdioma(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        inicializarVistas();
        configurarInsets();
        configurarListeners();
    }

    private void inicializarVistas() {
        til_name             = findViewById(R.id.til_name);
        til_user             = findViewById(R.id.til_user);
        til_password         = findViewById(R.id.til_password);
        til_confirm_password = findViewById(R.id.til_confirm_password);

        et_name             = findViewById(R.id.et_name);
        et_user             = findViewById(R.id.et_user);
        et_password         = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);

        btn_register = findViewById(R.id.btn_register);
        progress     = findViewById(R.id.progress_register);
    }

    private void configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void configurarListeners() {
        btn_register.setOnClickListener(v -> manejarRegistro());
    }

    private void manejarRegistro() {
        til_name.setError(null);
        til_user.setError(null);
        til_password.setError(null);
        til_confirm_password.setError(null);

        String nombre    = et_name.getText() != null ? et_name.getText().toString().trim() : "";
        String correo    = et_user.getText() != null ? et_user.getText().toString().trim() : "";
        String password  = et_password.getText() != null ? et_password.getText().toString() : "";
        String confirmar = et_confirm_password.getText() != null ? et_confirm_password.getText().toString() : "";

        if (TextUtils.isEmpty(nombre)) {
            til_name.setError(getString(R.string.error_empty_name));
            et_name.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(correo)) {
            til_user.setError(getString(R.string.error_empty_user));
            et_user.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            til_password.setError(getString(R.string.error_short_password));
            et_password.requestFocus();
            return;
        }

        if (!password.equals(confirmar)) {
            til_confirm_password.setError(getString(R.string.error_password_mismatch));
            et_confirm_password.requestFocus();
            return;
        }

        mostrarCargando(true);

        auth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    Map<String, Object> datos = new HashMap<>();
                    datos.put("nombre",         nombre);
                    datos.put("usuario",        correo);
                    datos.put("correo",         correo);
                    datos.put("fecha_registro", new Date());
                    datos.put("activo",         true);

                    db.collection("usuarios").document(uid).set(datos)
                            .addOnSuccessListener(unused -> {
                                mostrarCargando(false);
                                Intent intent = new Intent(Register.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra("registro_exitoso", true);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                mostrarCargando(false);
                                til_name.setError(getString(R.string.error_generic));
                            });
                })
                .addOnFailureListener(e -> {
                    mostrarCargando(false);
                    String mensaje = e.getMessage();
                    if (mensaje != null && mensaje.contains("already in use")) {
                        til_user.setError(getString(R.string.error_invalid_credentials));
                    } else if (mensaje != null && mensaje.contains("badly formatted")) {
                        til_user.setError(getString(R.string.error_empty_user));
                    } else {
                        til_user.setError(getString(R.string.error_generic));
                    }
                });
    }

    private void mostrarCargando(boolean cargando) {
        if (progress != null) progress.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btn_register.setEnabled(!cargando);
        btn_register.announceForAccessibility(cargando
                ? getString(R.string.register_loading)
                : getString(R.string.register_cd_btn));
    }
}