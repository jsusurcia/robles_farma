package com.example.robles_farma.ui.citas;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentCitaConfirmadaBinding;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder; // Necesitas la librería ZXing

public class CitaConfirmadaFragment extends Fragment {

    private FragmentCitaConfirmadaBinding binding;

    // Datos recibidos
    private String nombreDoctor;
    private String fecha;
    private String hora;
    private String ubicacion;
    private double precio;
    private String codigoQrData; // El texto para generar el QR
    private String nroComprobante;
    private String fechaEmision;
    private double igv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCitaConfirmadaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recibirDatos();
        configurarUI();
        generarQR();

        // Botón: Ver en Mis Citas
        binding.btnMisCitas.setOnClickListener(v -> {
            // Navegar al fragmento de Citas (donde está el listado)
            Navigation.findNavController(view).navigate(R.id.navigation_citas);
        });

        // Botón: Comprobante (Opcional por ahora)
        binding.btnComprobante.setOnClickListener(v -> {
            ComprobanteBottomSheet sheet = ComprobanteBottomSheet.newInstance(
                    nroComprobante,
                    fechaEmision,
                    precio,
                    igv,
                    nombreDoctor + " - " + "Consulta"
            );
            sheet.show(getParentFragmentManager(), "ComprobanteSheet");
        });
    }

    private void recibirDatos() {
        if (getArguments() != null) {
            nombreDoctor = getArguments().getString("nombre_doctor");
            fecha = getArguments().getString("fecha");
            hora = getArguments().getString("hora");
            ubicacion = getArguments().getString("ubicacion");
            precio = getArguments().getDouble("precio");
            codigoQrData = getArguments().getString("codigo_qr_data");
            nroComprobante = getArguments().getString("nro_comprobante");
            fechaEmision = getArguments().getString("fecha_emision");
            igv = getArguments().getDouble("igv");
        }
    }

    private void configurarUI() {
        binding.tvConfirmadaDoctor.setText(nombreDoctor);
        binding.tvConfirmadaFecha.setText(fecha);
        binding.tvConfirmadaHora.setText(hora);
        binding.tvConfirmadaUbicacion.setText(ubicacion);
        binding.tvConfirmadaCoste.setText("S/ " + String.format("%.2f", precio));
    }

    private void generarQR() {
        if (codigoQrData != null && !codigoQrData.isEmpty()) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                // Genera un Bitmap de 400x400 pixeles
                Bitmap bitmap = barcodeEncoder.encodeBitmap(codigoQrData, BarcodeFormat.QR_CODE, 400, 400);
                binding.ivQrCode.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}