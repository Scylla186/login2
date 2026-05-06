package com.example.login2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private TextInputLayout   til_user, til_password;
    private TextInputEditText et_user, et_password;
    private Button            btn_login;
    private TextView          tv_register;
    private ProgressBar       progress;

    private FirebaseAuth      auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IdiomaUtils.aplicarIdioma(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        // Si ya hay sesion activa saltar al scanner
        if (auth.getCurrentUser() != null) {
            irAlScanner();
            return;
        }

        inicializarVistas();
        configurarInsets();
        configurarListeners();
        mostrarMensajeRegistro();
    }

    private void inicializarVistas() {
        til_user     = findViewById(R.id.til_user);
        til_password = findViewById(R.id.til_password);
        et_user      = findViewById(R.id.et_user);
        et_password  = findViewById(R.id.et_password);
        btn_login    = findViewById(R.id.btn_login);
        tv_register  = findViewById(R.id.tv_register);
        progress     = findViewById(R.id.progress_login);
    }

    private void configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void mostrarMensajeRegistro() {
        if (getIntent().getBooleanExtra("registro_exitoso", false)) {
            til_user.setHelperText(getString(R.string.register_success));
        }
    }

    private void configurarListeners() {
        btn_login.setOnClickListener(v -> manejarLogin());
        tv_register.setOnClickListener(v ->
                startActivity(new Intent(Login.this, Register.class)));
    }

    private void manejarLogin() {
        til_user.setError(null);
        til_password.setError(null);

        String correo     = et_user.getText() != null ? et_user.getText().toString().trim() : "";
        String contrasena = et_password.getText() != null ? et_password.getText().toString().trim() : "";

        if (TextUtils.isEmpty(correo)) {
            til_user.setError(getString(R.string.error_empty_user));
            et_user.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contrasena)) {
            til_password.setError(getString(R.string.error_empty_password));
            et_password.requestFocus();
            return;
        }

        mostrarCargando(true);

        auth.signInWithEmailAndPassword(correo, contrasena)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    db.collection("usuarios").document(uid).get()
                            .addOnSuccessListener(doc -> {
                                mostrarCargando(false);
                                SesionUsuario.getInstance().iniciarSesion(
                                        uid,
                                        doc.getString("nombre"),
                                        doc.getString("usuario")
                                );
                                irAlScanner();
                            })
                            .addOnFailureListener(e -> {
                                mostrarCargando(false);
                                til_user.setError(getString(R.string.error_generic));
                            });
                })
                .addOnFailureListener(e -> {
                    mostrarCargando(false);
                    til_password.setError(getString(R.string.error_invalid_credentials));
                    til_password.announceForAccessibility(getString(R.string.error_invalid_credentials));
                });
    }

    private void irAlScanner() {
        Intent intent = new Intent(Login.this, ScanActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarCargando(boolean cargando) {
        if (progress != null) progress.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btn_login.setEnabled(!cargando);
        btn_login.announceForAccessibility(cargando
                ? getString(R.string.login_loading)
                : getString(R.string.login_cd_btn_enter));
    }
}