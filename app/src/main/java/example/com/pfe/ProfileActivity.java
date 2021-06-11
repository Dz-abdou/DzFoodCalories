package example.com.pfe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    Spinner illnessSpinner;
    Spinner goalSpinner;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    DocumentReference documentReference;

    EditText ageEditText;
    EditText heightEditText;
    EditText weightEditText;

    ProgressBar progressBar;

    TextView completeProfileTextView;
    TextView skipTextView;

    LinearLayout navBarLinearLayout;

    public static Bitmap img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        setIllnessSpinner();
        setGoalSpinner();

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        documentReference = fStore.collection("users").document(mAuth.getCurrentUser().getUid());

        ageEditText = findViewById(R.id.ageEditText);
        weightEditText = findViewById(R.id.WeightEditText);
        heightEditText = findViewById(R.id.heightEditText);

        progressBar = findViewById(R.id.progressBar);

        completeProfileTextView = findViewById(R.id.completeProfileTextView);
        skipTextView = findViewById(R.id.skipTextView);

    }

    @Override
    protected void onStart() {
            if (getIntent().getBooleanExtra("userLoggedIn", false)) {
            completeProfileTextView.setText("Edit your profile");
            skipTextView.setVisibility(View.GONE);



            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(task.isSuccessful()) {
                        String age = "";
                        String weight = "";
                        String height = "";
                        String illness = "";
                        String goal = "";

                        Object ageObject = documentSnapshot.get("age");
                        Object weightObject = documentSnapshot.get("weight");
                        Object heightObject = documentSnapshot.get("height");
                        Object illnessObject = documentSnapshot.get("illness");
                        Object goalObject = documentSnapshot.get("goal");

                        if(ageObject != null)
                            age = ageObject.toString();
                        if(weightObject != null)
                            weight = weightObject.toString();
                        if(heightObject != null)
                            height = heightObject.toString();
                        if(illnessObject != null)
                            illness = illnessObject.toString();
                        if(goalObject != null)
                            goal = goalObject.toString();

                        if(!age.isEmpty()) {
                            ageEditText.setText((age + "Years old"));
                        }
                        if(!weight.isEmpty()) {
                            weightEditText.setText((weight + "kg"));
                        }
                        if(!height.isEmpty()) {
                            heightEditText.setText((height + "cm"));
                        }
                        if(!illness.isEmpty()){
                           if(illness.equals(illnessSpinner.getItemAtPosition(1).toString())) {
                               illnessSpinner.setSelection(1);
                           } else if (illness.equals(illnessSpinner.getItemAtPosition(2).toString())) {
                                illnessSpinner.setSelection(2);
                           } else if (illness.equals(illnessSpinner.getItemAtPosition(3).toString())) {
                               illnessSpinner.setSelection(3);
                           } else if (illness.equals(illnessSpinner.getItemAtPosition(4).toString())) {
                               illnessSpinner.setSelection(4);
                           }
                        }

                        if(!goal.isEmpty()){
                            if(goal.equals(goalSpinner.getItemAtPosition(1).toString())) {
                                goalSpinner.setSelection(1);
                            } else if (goal.equals(goalSpinner.getItemAtPosition(2).toString())) {
                                goalSpinner.setSelection(2);
                            } else if (goal.equals(goalSpinner.getItemAtPosition(3).toString())) {
                                goalSpinner.setSelection(3);
                            } else if (goal.equals(goalSpinner.getItemAtPosition(4).toString())) {
                                goalSpinner.setSelection(4);
                            }
                        }

                    }

                }
            });
        }else {
                navBarLinearLayout = findViewById(R.id.navigationBar);
                navBarLinearLayout.setVisibility(View.GONE);
            }
        super.onStart();

    }

    public void setIllnessSpinner(){
        illnessSpinner = findViewById(R.id.illnessSpinner);

        ArrayList<String> illnessesList = new ArrayList<>();
        illnessesList.add("Chronic illness?");
        illnessesList.add("Diabetes");
        illnessesList.add("Heart Disease");
        illnessesList.add("High blood pressure");
        illnessesList.add("None");

        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item ,illnessesList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,R.layout.illness_spinner_item,illnessesList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(getResources().getColor(R.color.dark_orange));
                }
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(R.layout.illness_spinner_dropdown_item);
        illnessSpinner.setAdapter(arrayAdapter);

        illnessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    if(!getIntent().getBooleanExtra("userLoggedIn", false))
                        Toast.makeText
                                (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                                .show();
                    TextView tv = findViewById(R.id.itemTv);
                    tv.setTextColor(getResources().getColor(R.color.dark_orange));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setGoalSpinner(){
        goalSpinner = findViewById(R.id.goalSpinner);

        ArrayList<String> goalsList = new ArrayList<>();
        goalsList.add("What's your goal?");
        goalsList.add("I'd like to put on weight");
        goalsList.add("I'd like to lose weight");
        goalsList.add("I'd like to maintain my weight");

        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item ,illnessesList);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,R.layout.goal_spinner_item,goalsList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(getResources().getColor(R.color.dark_orange));
                }
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(R.layout.goal_spinner_dropdown_item);
        goalSpinner.setAdapter(arrayAdapter);

        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                TextView tv = findViewById(R.id.goalItemTv);
                if(position > 0){
                    // Notify the selected item text
                    if(!getIntent().getBooleanExtra("userLoggedIn", false))
                        Toast.makeText
                              (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                                .show();
                    tv.setTextColor(getResources().getColor(R.color.dark_orange));
                }

                if(position == 3) {
                    tv.setTextSize(14);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void saveButtonOnClick(View view) {

        String age = ageEditText.getText().toString();
        String weight = weightEditText.getText().toString();
        String height = heightEditText.getText().toString();

        Map<String, Object> user = new HashMap<>();

        if (!age.isEmpty())
            user.put("age", age);

        if(!weight.isEmpty())
            user.put("weight", weight);

        if(!height.isEmpty())
            user.put("height", height);

        if(goalSpinner.getSelectedItemPosition() != 0)
            user.put("goal", goalSpinner.getSelectedItem().toString());

        if(illnessSpinner.getSelectedItemPosition() != 0)
            user.put("illness", illnessSpinner.getSelectedItem().toString());

        progressBar.setVisibility(View.VISIBLE);
        documentReference.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }  else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "A problem occurred while updating " +
                            "your profile, please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "A problem occurred while updating " +
                        "your profile, please try again!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void skipButtonClicked(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void selectButtonClicked(View view) {

        try {
            Intent  intent = new Intent(Intent.ACTION_GET_CONTENT);
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
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
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
                img = (Bitmap) data.getExtras().get("data");
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

        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{
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
}
