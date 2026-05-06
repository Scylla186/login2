package com.example.login2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProductInfoFragment extends Fragment {

    private TextView tvQrId;
    private TextView tvNombre;
    private TextView tvDescripcion;
    private TextView tvDetalles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvQrId        = view.findViewById(R.id.tv_qr_id);
        tvNombre      = view.findViewById(R.id.tv_product_name);
        tvDescripcion = view.findViewById(R.id.tv_product_description);
        tvDetalles    = view.findViewById(R.id.tv_product_details);
        actualizarUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarUI();
    }

    public void actualizarUI() {
        if (tvNombre == null) return;

        ProductData data = ProductData.getInstance();

        if (!data.tieneProducto()) {
            tvQrId.setText("");
            tvNombre.setText(getString(R.string.scan_no_product));
            tvDescripcion.setText("");
            tvDetalles.setText("");
            tvNombre.announceForAccessibility(getString(R.string.scan_no_product));
            return;
        }

        tvQrId.setText(data.getQrId());
        tvNombre.setText(data.getNombre());
        tvDescripcion.setText(data.getDescripcion());

        StringBuilder detallesCompletos = new StringBuilder();
        detallesCompletos.append(data.getDetalles());

        if (data.getAlergenos() != null && !data.getAlergenos().isEmpty()) {
            detallesCompletos.append("\n\n")
                    .append(getString(R.string.product_allergens_label))
                    .append(" ")
                    .append(data.getAlergenos());
        }

        detallesCompletos.append("\n\n")
                .append(data.isEsVegano()
                        ? getString(R.string.product_vegan_yes)
                        : getString(R.string.product_vegan_no));

        tvDetalles.setText(detallesCompletos.toString());

        tvNombre.setContentDescription(
                getString(R.string.product_section_name) + ": " + data.getNombre());
        tvDescripcion.setContentDescription(
                getString(R.string.product_section_description) + ": " + data.getDescripcion());
        tvDetalles.setContentDescription(
                getString(R.string.product_section_details) + ": " + detallesCompletos);

        tvNombre.announceForAccessibility(
                getString(R.string.product_section_name) + ": " + data.getNombre());
    }
}