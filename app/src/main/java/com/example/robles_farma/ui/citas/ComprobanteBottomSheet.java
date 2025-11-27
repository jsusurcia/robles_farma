package com.example.robles_farma.ui.citas;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.robles_farma.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ComprobanteBottomSheet extends BottomSheetDialogFragment {

    // Keys para el Bundle
    private static final String ARG_NRO = "arg_nro";
    private static final String ARG_FECHA = "arg_fecha";
    private static final String ARG_TOTAL = "arg_total";
    private static final String ARG_IGV = "arg_igv";
    private static final String ARG_DOCTOR = "arg_doctor";

    public static ComprobanteBottomSheet newInstance(String nro, String fecha, double total, double igv, String doctor) {
        ComprobanteBottomSheet fragment = new ComprobanteBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_NRO, nro);
        args.putString(ARG_FECHA, fecha);
        args.putDouble(ARG_TOTAL, total);
        args.putDouble(ARG_IGV, igv);
        args.putString(ARG_DOCTOR, doctor);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comprobante_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) return;

        // 1. Vincular vistas
        View ticketView = view.findViewById(R.id.card_ticket_view);

        TextView tvNro = view.findViewById(R.id.tv_ticket_nro);
        TextView tvFecha = view.findViewById(R.id.tv_ticket_fecha);
        TextView tvDoctor = view.findViewById(R.id.tv_ticket_doctor);
        TextView tvPrecio = view.findViewById(R.id.tv_ticket_precio);
        TextView tvSubtotal = view.findViewById(R.id.tv_ticket_subtotal);
        TextView tvIgv = view.findViewById(R.id.tv_ticket_igv);
        TextView tvTotal = view.findViewById(R.id.tv_ticket_total);
        View btnGuardar = view.findViewById(R.id.btn_guardar_ticket);

        // 2. Obtener datos
        String nro = getArguments().getString(ARG_NRO);
        String fechaRaw = getArguments().getString(ARG_FECHA); // Viene como ISO: 2025-11-25T23:38...
        double total = getArguments().getDouble(ARG_TOTAL);
        double igv = getArguments().getDouble(ARG_IGV);
        String doctor = getArguments().getString(ARG_DOCTOR);
        double subtotal = total - igv;

        // 3. Formatear y asignar
        tvNro.setText(nro);
        tvDoctor.setText(doctor);

        // Formateo de fecha (De ISO a leíble)
        tvFecha.setText(formatearFecha(fechaRaw));

        // Formateo de moneda
        String txtTotal = String.format(Locale.getDefault(), "S/ %.2f", total);
        String txtIgv = String.format(Locale.getDefault(), "S/ %.2f", igv);
        String txtSubtotal = String.format(Locale.getDefault(), "S/ %.2f", subtotal);

        tvPrecio.setText(txtTotal);
        tvSubtotal.setText(txtSubtotal);
        tvIgv.setText(txtIgv);
        tvTotal.setText(txtTotal);

        // 4. Acción del botón Guardar
        btnGuardar.setOnClickListener(v -> {
            // A. Capturar la vista como Bitmap
            Bitmap bitmap = capturarBitmapDeVista(ticketView);

            if (bitmap != null) {
                // B. Guardar en galería
                guardarBitmapEnGaleria(bitmap, "Comprobante_" + nro);
            }
        });
    }

    private void guardarBitmapEnGaleria(Bitmap bitmap, String nombreArchivo) {
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Para Android 10+ (API 29+) usamos MediaStore (Más seguro y moderno)
                ContentResolver resolver = requireContext().getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CitaSalud");

                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(imageUri);
            } else {
                Toast.makeText(getContext(), "Versión de Android antigua no soportada en esta demo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Comprimir y guardar
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (fos != null) {
                fos.close();
            }

            Toast.makeText(getContext(), "¡Guardado en Imágenes/CitaSalud!", Toast.LENGTH_LONG).show();
            dismiss();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap capturarBitmapDeVista (View view) {
        // Crea un bitmap con el ancho y alto de la vista (el ticket)
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // Dibuja la vista en el lienzo del bitmap
        view.draw(canvas);
        return bitmap;
    }

    private String formatearFecha(String fechaIso) {
        try {
            // Ajusta este formato al que se recibe en la API
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = input.parse(fechaIso);
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return output.format(date);
        } catch (ParseException e) {
            return fechaIso;
        }
    }
}