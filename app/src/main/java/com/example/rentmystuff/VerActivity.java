package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.rentmystuff.databinding.ActivityVerBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.TimeUnit;

public class VerActivity extends AppCompatActivity {

    private Uri imageUri; // the URI of the profile image.
    private StorageReference storageReference; // reference to the storage in firebase.
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    private ActivityVerBinding binding;
//
//    private String imageURL; // URL of the profile image.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);
        binding = ActivityVerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonVerifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_number = binding.editPhoneNumber.getText().toString();

                if(!phone_number.equals("")) {
                    binding.textVerificationCode.setVisibility(View.VISIBLE);
                    binding.editVerificationCode.setVisibility(View.VISIBLE);
                    binding.buttonVerifyCode.setVisibility(View.VISIBLE);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+972" + phone_number,
                            60,
                            TimeUnit.SECONDS,
                            VerActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted (@NonNull PhoneAuthCredential phoneAuthCredential) {
                                }
                                @Override
                                public void onVerificationFailed (@NonNull FirebaseException e) {
                                    Toast.makeText(VerActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onCodeSent(@NonNull String verification_id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                }
                            });
                } else{
                    Toast.makeText(VerActivity.this, "Enter Mobile Phone", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}