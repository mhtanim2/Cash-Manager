package com.example.cashmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.cashmanager.Model.Data;
import com.example.cashmanager.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.Random;

public class SignUp extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference ref;
    ProgressDialog mDialog;
    Dialog dialog;
    int forImage = 1;
    int forCamera = 2;
    Bitmap bitmap;
    Uri filepath;
    private UploadTask uploadTask;
    private String downloadUrl = "";

    // TextInputLayout mName,mEmail,mPass,mRePass;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.img_dialogebox);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.alart_back));
        dialog.getWindow().setWindowAnimations(R.style.animation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        dialog.setCancelable(true);
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Processing...");
        Button camera = dialog.findViewById(R.id.cam);
        Button galery = dialog.findViewById(R.id.gal);
        galery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(SignUp.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Please select Image"), 1);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
                dialog.dismiss();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(SignUp.this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, forCamera);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
                dialog.dismiss();
            }
        });
        binding.browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration();
            }
        });

        binding.goSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignIn.class));
            }
        });
    }

    private void registration() {
        mDialog.show();
        String name = binding.etName.getEditText().getText().toString().trim();
        String email = binding.etUserName2.getEditText().getText().toString().trim();
        String pass = binding.etPass2.getEditText().getText().toString().trim();
        String repass = binding.etRePass.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.etName.setError("Email Required..");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            binding.etUserName2.setError("email Required..");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            binding.etPass2.setError("email Required..");
            return;
        }
        if (TextUtils.isEmpty(repass)) {
            binding.etRePass.setError("repass Required..");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mDialog.dismiss();
                    Data data = new Data(name, email, pass, repass);
                    String id = mAuth.getUid();
                    assert id != null;
                    ref.child("Users").child(id).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            gotoStorage();
                            Toast.makeText(SignUp.this, "Success!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finishAffinity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUp.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void gotoStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference uploader = storage.getReference().child("image1" + new Random().nextInt(100000));
        uploadTask = uploader.putFile(filepath);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    gotoDatabase(downloadUrl);
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            mDialog.setMessage("Uploaded :" + (int) percent + " %");
                        }
                    });
                }
            }
        });

    }

    private void gotoDatabase(String downloadUrl) {
        String key = mAuth.getUid();
        Data ob = new Data(downloadUrl);
        assert key != null;
        ref.child("category").child(key).setValue(ob).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SignUp.this, "Data saved properly.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*    private void gotoDatabase(String downloadUrl){
            String key = mAuth.getUid();
            Data ob = new Data(downloadUrl,key);
            assert key != null;
            ref.child("category").child(key).setValue(ob).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(SignUp.this, "Data saved properly.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == forImage) {
            filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                binding.upImage.setImageBitmap(bitmap);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == forCamera) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            binding.upImage.setImageBitmap(bitmap);
        }
    }

}