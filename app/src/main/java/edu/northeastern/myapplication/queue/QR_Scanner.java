package edu.northeastern.myapplication.queue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.northeastern.myapplication.R;

public class QR_Scanner extends AppCompatActivity {
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private String qrCode;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private ArrayList allowed_QRs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);
        previewView = findViewById(R.id.activity_main_previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();
        allowed_QRs = new ArrayList<>();
        allowed_QRs.add("bench_press");
        allowed_QRs.add("squat_rack");
    }

    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(QR_Scanner.this,
                        new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                // Permission denied, show a message to the user
                AlertDialog alertDialog =
                        new AlertDialog.Builder(QR_Scanner.this).create();
                alertDialog.setTitle("Camera permission denied!");
                alertDialog.setMessage("Please enable camera permissions in your settings to" +
                        " use this feature.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                close_act();
                            }
                        });
                alertDialog.show();
            }
        }
    }

    public void close_act() {
        this.finish();
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this),
                new QRCodeImageAnalyzer(new QRCodeFoundListener() {
                    @Override
                    public void onQRCodeFound(String _qrCode) {
                        // pass QR data back to queue home and exit activity
                        qrCode = _qrCode;

                        if (!allowed_QRs.contains(qrCode.toString())) {
                            make_toast();
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("data", qrCode);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    }

                    @Override
                    public void qrCodeNotFound() {}
                }));

        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector,
                preview, imageAnalysis);
    }

    private void make_toast() {
        Toast.makeText(this, "Invalid QR code", Toast.LENGTH_LONG).show();
    }

}