package com.example.login2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ScanFragment extends Fragment {

    private static final String TAG = "ScanFragment";

    private static final Pattern QR_PATTERN =
            Pattern.compile("^OH-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}$");

    private PreviewView      cameraPreview;
    private TextView         tvInvalidQr;
    private View             touchArea;

    private ExecutorService  cameraExecutor;
    private BarcodeScanner   barcodeScanner;
    private FirebaseFirestore db;

    private boolean escaneando  = true;
    private boolean qrDetectado = false;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnQrValidadoListener {
        void onQrValido(String qrId);
    }

    private OnQrValidadoListener qrListener;

    public void setOnQrValidadoListener(OnQrValidadoListener listener) {
        this.qrListener = listener;
    }

    // ─── Ciclo de vida ────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cameraPreview = view.findViewById(R.id.camera_preview);
        tvInvalidQr   = view.findViewById(R.id.tv_invalid_qr);
        touchArea     = view.findViewById(R.id.touch_area);

        db             = FirebaseFirestore.getInstance();
        cameraExecutor = Executors.newSingleThreadExecutor();

        configurarBarcodeScanner();
        verificarPermisoYArrancarCamara();
        configurarTouchArea();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
        if (barcodeScanner != null) barcodeScanner.close();
    }

    // ─── ML Kit ───────────────────────────────────────────────────────────────

    private void configurarBarcodeScanner() {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);
    }

    // ─── Permisos y camara ────────────────────────────────────────────────────

    private void verificarPermisoYArrancarCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            arrancarCamara();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            arrancarCamara();
        }
    }

    private void arrancarCamara() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(requireContext());

        future.addListener(() -> {
            try {
                bindCameraUseCases(future.get());
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error iniciando camara", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            if (!escaneando || qrDetectado) {
                imageProxy.close();
                return;
            }

            android.media.Image mediaImage = imageProxy.getImage();
            if (mediaImage == null) {
                imageProxy.close();
                return;
            }

            InputImage inputImage = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.getImageInfo().getRotationDegrees()
            );

            barcodeScanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String raw = barcode.getRawValue();
                            if (raw != null) procesarQr(raw);
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error procesando imagen", e))
                    .addOnCompleteListener(task -> imageProxy.close());
        });

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(
                    getViewLifecycleOwner(),
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
            );
        } catch (Exception e) {
            Log.e(TAG, "Error enlazando camara", e);
        }
    }

    // ─── Validacion QR ────────────────────────────────────────────────────────

    private void procesarQr(String rawValue) {
        if (qrDetectado) return;

        if (!QR_PATTERN.matcher(rawValue).matches()) {
            mainHandler.post(this::mostrarQrInvalido);
            return;
        }

        qrDetectado = true;
        escaneando  = false;

        mainHandler.post(() -> consultarProductoEnFirestore(rawValue));
    }

    // ─── Consulta Firestore ───────────────────────────────────────────────────

    private void consultarProductoEnFirestore(String codigoQr) {
        db.collection("codigos_qr")
                .whereEqualTo("codigo_qr", codigoQr)
                .whereEqualTo("activo", true)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        qrDetectado = false;
                        escaneando  = true;
                        mostrarQrInvalido();
                        return;
                    }

                    db.collection("productos")
                            .whereEqualTo("id_qr", codigoQr)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(prodSnapshot -> {
                                if (prodSnapshot.isEmpty()) {
                                    qrDetectado = false;
                                    escaneando  = true;
                                    mostrarQrInvalido();
                                    return;
                                }

                                com.google.firebase.firestore.DocumentSnapshot doc =
                                        prodSnapshot.getDocuments().get(0);

                                Boolean esVegano = doc.getBoolean("es_vegano");
                                String alergenos = doc.getString("alergenos");

                                ProductData.getInstance().setProducto(
                                        codigoQr,
                                        doc.getString("nombre"),
                                        doc.getString("descripcion"),
                                        doc.getString("detalles"),
                                        esVegano != null && esVegano,
                                        alergenos != null ? alergenos : ""
                                );

                                registrarEscaneo(codigoQr);

                                if (tvInvalidQr != null) tvInvalidQr.setVisibility(View.GONE);
                                if (qrListener != null) qrListener.onQrValido(codigoQr);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error buscando producto", e);
                                qrDetectado = false;
                                escaneando  = true;
                                mostrarQrInvalido();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error buscando QR", e);
                    qrDetectado = false;
                    escaneando  = true;
                    mostrarQrInvalido();
                });
    }

    // ─── Registrar escaneo ────────────────────────────────────────────────────

    private void registrarEscaneo(String codigoQr) {
        Map<String, Object> escaneo = new HashMap<>();
        escaneo.put("uid_usuario",   SesionUsuario.getInstance().getUid());
        escaneo.put("codigo_qr",     codigoQr);
        escaneo.put("fecha_escaneo", new Date());

        db.collection("escaneos").add(escaneo)
                .addOnFailureListener(e -> Log.e(TAG, "Error registrando escaneo", e));
    }

    // ─── UI helpers ───────────────────────────────────────────────────────────

    private void mostrarQrInvalido() {
        if (tvInvalidQr == null) return;
        tvInvalidQr.setVisibility(View.VISIBLE);
        tvInvalidQr.announceForAccessibility(getString(R.string.scan_invalid_qr));
        mainHandler.postDelayed(() -> {
            if (tvInvalidQr != null) tvInvalidQr.setVisibility(View.GONE);
        }, 2500);
    }

    private void configurarTouchArea() {
        touchArea.setOnClickListener(v -> {
            if (qrDetectado && qrListener != null) {
                qrListener.onQrValido(ProductData.getInstance().getQrId());
                return;
            }
            touchArea.announceForAccessibility(getString(R.string.scan_hint_cd));
        });
    }

    public void reactivarScanner() {
        qrDetectado = false;
        escaneando  = true;
        if (tvInvalidQr != null) tvInvalidQr.setVisibility(View.GONE);
    }
}