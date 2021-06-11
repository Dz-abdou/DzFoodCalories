package example.com.pfe;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SendDemand extends AppCompatActivity {

    private Uri mImageUri;
    private StorageReference mStorageRef;
    FirebaseFirestore db;
    DocumentReference documentReference;
    private StorageTask<UploadTask.TaskSnapshot> mUploadTask;
    ProgressBar progressBar;

    String foodName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_demand);

        ImageView imageView = findViewById(R.id.foodPredictionImageView);
        Bitmap img = MainActivity.img;
        imageView.setImageBitmap(img);
        mImageUri = getImageUri(SendDemand.this, img);



        progressBar = findViewById(R.id.progressBar);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void sendDemand(View view) {
        EditText foodNameEt = findViewById(R.id.dishEditText);
        foodName = foodNameEt.getText().toString();;

        if(foodName.isEmpty()) {
            foodNameEt.setError("Dish name is required!");
            foodNameEt.requestFocus();
            return;
        }

        mStorageRef = FirebaseStorage.getInstance().getReference("images");

        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(SendDemand.this, "Upload in progress", Toast.LENGTH_SHORT).show();
        } else {
            uploadFile();
        }

    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(foodName);
            progressBar.setVisibility(View.VISIBLE);
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(SendDemand.this, "Upload successful", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SendDemand.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectButtonClicked(View view) {

        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            try {
                Uri uri = data.getData();
                ProfileActivity.img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Intent intent = new Intent(getApplicationContext(), predictionActivity.class);
                intent.putExtra("selected", 1);
                intent.putExtra("fromProfileActivity", true);
                startActivity(intent);
                finish();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(requestCode == 1) {
            try {
                ProfileActivity.img = (Bitmap) data.getExtras().get("data");
                Intent intent = new Intent(getApplicationContext(), predictionActivity.class);
                intent.putExtra("selected", 0);
                intent.putExtra("fromProfileActivity", true);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cameraButtonClicked(View view) {

        if(ContextCompat.checkSelfPermission(SendDemand.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SendDemand.this, new String[]{
                    Manifest.permission.CAMERA}, 1);

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, 1);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }
        }

    }

    public void HomeButtonClicked(View view) {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void navBarProfileOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("userLoggedIn", true);
        startActivity(intent);
        finish();
    }
}