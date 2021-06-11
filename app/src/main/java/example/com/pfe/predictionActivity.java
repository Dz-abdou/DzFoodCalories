package example.com.pfe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;



import example.com.pfe.ml.Model;

public class predictionActivity extends AppCompatActivity {

    Bitmap img;
    ImageView imageView;
    static int REQUEST_IMAGE_CAPTURE = 1;
    boolean  foodRecognized ;

    ArrayList<Float> predictionValues;
    String[] labels = {"Chtitha", "Couscous", "Ftir", "Mahchi", "Tlitli", "Bourak", "Chakhchoukha"
            , "Kefta", "Marka hlouwa", "Mloukhia", "Trida"};

    TextView homeTextView;
    TextView imageTextView;
    TextView cameraTextView;

    ImageView selectImageView;
    ImageView homeImageView;
    ImageView cameraImageView;


    RelativeLayout predictionStartRelativeLayout;
    RelativeLayout predictionMidRelativeLayout;
    RelativeLayout predictionEndRelativeLayout;

    TextView  predictionStartTextView;
    TextView  predictionMidTextView;
    TextView  predictionEndTextView;
    SQLiteDatabase foodDB;

    FoodNutritionFacts food1Facts;
    FoodNutritionFacts food2Facts;
    FoodNutritionFacts food3Facts;


    String food1;
    String food2;
    String food3;

    int food1Pos = -1;
    int food2Pos = -1;
    int food3Pos = -1;

    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    DocumentReference documentReference;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        foodRecognized = false;

        homeTextView = findViewById(R.id.homeTextView);
        imageTextView = findViewById(R.id.imageTextView);
        cameraTextView = findViewById(R.id.cameraTextView);

        selectImageView = findViewById(R.id.selectImageView);
        homeImageView = findViewById(R.id.homeImageView);
        cameraImageView = findViewById(R.id.cameraImageView);

        setNavBarIcons();
        if(getIntent().getBooleanExtra("fromProfileActivity", false))
            img = ProfileActivity.img;
        else
            img = MainActivity.img;

        imageView = findViewById(R.id.foodPredictionImageView);
        imageView.setImageBitmap(img);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        documentReference = fstore.collection("users").document(mAuth.getCurrentUser().getUid());

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.bringToFront();
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home :
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.profile :
                        openProfileActivity();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.logout:
                        logout();
                        break;

                }
                return true;
            }
        });

        predictionValues = new ArrayList<>();

        predictionStartRelativeLayout = findViewById(R.id.predictionStartRelativeLayout);
        predictionMidRelativeLayout = findViewById(R.id.predictionMidRelativeLayout);
        predictionEndRelativeLayout = findViewById(R.id.predictionEndRelativeLayout);

        predictionStartTextView = findViewById(R.id.predictionStartTextView);
        predictionMidTextView = findViewById(R.id.predictionMidTextView);
        predictionEndTextView = findViewById(R.id.predictionEndTextView);



        try {
            foodDB = this.openOrCreateDatabase("Food", MODE_PRIVATE, null);
            foodDB.execSQL("CREATE TABLE IF NOT EXISTS nutritionFacts (name VARCHAR primary key, calories VARCHAR, protein VARCHAR" +
                    ",carbohydrates VARCHAR, lipids VARCHAR, saturatedFat VARCHAR, unsaturatedFat VARCHAR" +
                    ",polyunsaturatedFat VARCHAR, MonounsaturatedFat VARCHAR, cholesterol VARCHAR, dietaryFiber VARCHAR" +
                    ", sugars VARCHAR, unique(name))");

                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Couscous' ,'220', '3.8', '35.7', '0.8', '1', '0', '0', '0', '0', '0', '0')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Bourak' ,'173', '10', '20', '7', '1.5', '-', '-', '-', '-', '1.5', '1.5')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Chtitha' ,'160', '22.8', '6.4', '1', '1', '-', '0.2', '0.9', '0', '0', '0.4')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Chakhchoukha' ,'180', '12.28', '31.4', '4,3', '1.7', '-', '-', '-', '11', '1.7', '1')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Ftir' ,'180', '12.28', '31,4', '4.3', '1.7', '-', '-', '-', '11', '1.7', '1')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Kefta' ,'190.3', '20.8', '30', '7', '1.4', '0', '0', '0', '72', '0', '0')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Marka hlouwa' ,'112', '3,7', '16.2', '2.6', '0.3', '-', '-', '-', '-', '4.6', '10.7')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Mahchi' , '210', '23', '43', '4', '0.3', '-', '-', '-', '-', '-', '-')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Mloukhia' ,' 239 ', '6.9', '17.3', '4.9', '9', '0', '4', '-', '40', '2', '0')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Tlitli' , '200', '12', '72', '4', '1', 'O', 'O', 'O', '0.2', '-', '3')");
                foodDB.execSQL("INSERT OR REPLACE INTO nutritionFacts (name,calories, protein, carbohydrates, lipids, saturatedFat ,unsaturatedFat, polyunsaturatedFat, MonounsaturatedFat, cholesterol, dietaryFiber, sugars) VALUES ('Trida' , '190', '12', '61', '2.3', '1', '0.6', 'O', 'O', '0', '-', '2')");

                foodDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        predict();
        if(foodRecognized) {
            getTop3();
            getNutritionFacts();
            fillNutritionFactsTextViews(food1Facts);
            setNutritionFactsColors();
        } else {
            startActivity(new Intent(getApplicationContext(), SendDemand.class));
            finish();
        }
    }

    public void setNavBarIcons() {
        int var = getIntent().getIntExtra("selected", -1) ;

        homeTextView.setTextColor(getResources().getColor(R.color.gray_text));
        homeImageView.setImageResource(R.drawable.ic_home_gray);
        if(var == 1) {
            imageTextView.setTextColor(getResources().getColor(R.color.dark_orange));
            selectImageView.setImageResource(R.drawable.ic_image_orange);
        } else if(var == 0) {
            cameraTextView.setTextColor(getResources().getColor(R.color.dark_orange));
            cameraImageView.setImageResource(R.drawable.ic_camera_orange);
        }
    }


    public void logout() {
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    public void openProfileActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("userLoggedIn", true);
        startActivity(intent);
    }


    private void getTop3() {

        Float  first, second, third;
        int i;

        third = first = second = Float.MIN_VALUE;
        for (i = 0; i < 11; i++) {
            /* If current element is greater than
            first*/
            if (predictionValues.get(i) > first) {
                third = second;
                second = first;
                first = predictionValues.get(i);
            }

            else if (predictionValues.get(i) > second) {
                third = second;
                second = predictionValues.get(i);
            }

            else if (predictionValues.get(i) > third)
                third = predictionValues.get(i);
        }

        food1Pos = predictionValues.indexOf(first);
        for(i = 0; i < 11; i++) {
            if(predictionValues.get(i).equals(second) && i != food1Pos)
                food2Pos = i;

        }

        for(i = 0; i < 11; i++) {
            if(predictionValues.get(i).equals(third) && i != food1Pos && i != food2Pos)
                food3Pos = i;

        }



        food1 = labels[food1Pos].trim();
        food2 = labels[food2Pos].trim();
        food3 = labels[food3Pos].trim();

        predictionStartTextView.setText(food1);
        predictionMidTextView.setText(food2);
        predictionEndTextView.setText(food3);
    }

    public void getNutritionFacts() {
        foodDB = this.openOrCreateDatabase("Food", MODE_PRIVATE, null);
        Cursor c = foodDB.rawQuery("SELECT * FROM nutritionFacts WHERE name='" + food1 + "'", null);

        if(c.getCount() > 0)
        {
            c.moveToFirst();
            food1Facts = new FoodNutritionFacts();
            food1Facts.setName(c.getString(c.getColumnIndex("name")));
            food1Facts.setCalories(c.getString(c.getColumnIndex("calories"))+ "Cal");
            food1Facts.setProtein(c.getString(c.getColumnIndex("protein"))+ "g");
            food1Facts.setCarbohydrates(c.getString(c.getColumnIndex("carbohydrates"))+ "g");
            food1Facts.setLipids(c.getString(c.getColumnIndex("lipids"))+ "g");
            food1Facts.setSaturatedFat(c.getString(c.getColumnIndex("saturatedFat"))+ "g");
            food1Facts.setUnsaturatedFat(c.getString(c.getColumnIndex("unsaturatedFat"))+ "g");
            food1Facts.setPolyunsaturatedFat(c.getString(c.getColumnIndex("polyunsaturatedFat"))+ "g");
            food1Facts.setMonounsaturatedFat(c.getString(c.getColumnIndex("MonounsaturatedFat"))+ "g");
            food1Facts.setCholesterol(c.getString(c.getColumnIndex("cholesterol"))+ "mg");
            food1Facts.setDietaryFiber(c.getString(c.getColumnIndex("dietaryFiber"))+ "g");
            food1Facts.setSugars(c.getString(c.getColumnIndex("sugars"))+ "g");
            c.close();
        }



        c = foodDB.rawQuery("SELECT * FROM nutritionFacts WHERE name='" + food2 + "'", null);

        if(c.getCount() > 0) {
            c.moveToFirst();
            food2Facts = new FoodNutritionFacts();
            food2Facts.setName(c.getString(c.getColumnIndex("name")));
            food2Facts.setCalories(c.getString(c.getColumnIndex("calories"))+ "Cal");
            food2Facts.setProtein(c.getString(c.getColumnIndex("protein" )) + "g");
            food2Facts.setCarbohydrates(c.getString(c.getColumnIndex("carbohydrates")) + "g");
            food2Facts.setLipids(c.getString(c.getColumnIndex("lipids")) + "g");
            food2Facts.setSaturatedFat(c.getString(c.getColumnIndex("saturatedFat")) + "g");
            food2Facts.setUnsaturatedFat(c.getString(c.getColumnIndex("unsaturatedFat"))+ "g");
            food2Facts.setPolyunsaturatedFat(c.getString(c.getColumnIndex("polyunsaturatedFat"))+ "g");
            food2Facts.setMonounsaturatedFat(c.getString(c.getColumnIndex("MonounsaturatedFat"))+ "g");
            food2Facts.setCholesterol(c.getString(c.getColumnIndex("cholesterol"))+ "mg");
            food2Facts.setDietaryFiber(c.getString(c.getColumnIndex("dietaryFiber"))+ "g");
            food2Facts.setSugars(c.getString(c.getColumnIndex("sugars"))+ "g");
            c.close();
        }


        c = foodDB.rawQuery("SELECT * FROM nutritionFacts WHERE name='" + food3 + "'", null);

        if(c.getCount() > 0) {
            c.moveToFirst();
            food3Facts = new FoodNutritionFacts();
            food3Facts.setName(c.getString(c.getColumnIndex("name")));
            food3Facts.setCalories(c.getString(c.getColumnIndex("calories")) + "Cal");
            food3Facts.setProtein(c.getString(c.getColumnIndex("protein")) + "g");
            food3Facts.setCarbohydrates(c.getString(c.getColumnIndex("carbohydrates")) + "g");
            food3Facts.setLipids(c.getString(c.getColumnIndex("lipids")) + "g");
            food3Facts.setSaturatedFat(c.getString(c.getColumnIndex("saturatedFat")) + "g");
            food3Facts.setUnsaturatedFat(c.getString(c.getColumnIndex("unsaturatedFat")) + "g");
            food3Facts.setPolyunsaturatedFat(c.getString(c.getColumnIndex("polyunsaturatedFat")) + "g");
            food3Facts.setMonounsaturatedFat(c.getString(c.getColumnIndex("MonounsaturatedFat")) + "g");
            food3Facts.setCholesterol(c.getString(c.getColumnIndex("cholesterol")) + "mg");
            food3Facts.setDietaryFiber(c.getString(c.getColumnIndex("dietaryFiber")) + "g");
            food3Facts.setSugars(c.getString(c.getColumnIndex("sugars")) + "g");
            c.close();
        }

        foodDB.close();


    }

    public void predict() {
        Bitmap predictionImg;
        predictionValues.clear();
        predictionValues = new ArrayList<>();

        predictionImg = Bitmap.createScaledBitmap(img, 224, 224, true);
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
            TensorImage tensorImage = new TensorImage(DataType.UINT8);
            tensorImage.load(predictionImg);
            ByteBuffer byteBuffer = tensorImage.getBuffer();

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
            for(int i = 0 ; i < 11; i++) {
                if(outputFeature0.getFloatValue(i) > 60F && !foodRecognized)
                    foodRecognized = true;
                predictionValues.add(outputFeature0.getFloatValue(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void midItemSelected(View view) {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            predictionStartRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.predicted_start_unselected_bg));
            predictionMidRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.predicted_mid_selected_bg));
            predictionEndRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.predicted_end_unselected_bg));
        } else {
            predictionStartRelativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.predicted_start_unselected_bg));
            predictionMidRelativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.predicted_mid_selected_bg));
            predictionEndRelativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.predicted_end_unselected_bg));
        }
        predictionStartTextView.setTextColor(getResources().getColor(R.color.gray_text));
        predictionMidTextView.setTextColor(getResources().getColor(R.color.white));
        predictionEndTextView.setTextColor(getResources().getColor(R.color.gray_text));

        fillNutritionFactsTextViews(food2Facts);
    }

    public void startItemSelected(View view) {

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            predictionStartRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.predicted_start_selected_bg));
            predictionMidRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.predicted_mid_unselected_bg));
            predictionEndRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.predicted_end_unselected_bg));
        } else {
            predictionStartRelativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.predicted_start_selected_bg));
            predictionMidRelativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.predicted_mid_unselected_bg));
            predictionEndRelativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.predicted_end_unselected_bg));
        }
        predictionStartTextView.setTextColor(getResources().getColor(R.color.white));
        predictionMidTextView.setTextColor(getResources().getColor(R.color.gray_text));
        predictionEndTextView.setTextColor(getResources().getColor(R.color.gray_text));

        fillNutritionFactsTextViews(food1Facts);
    }

    public void endItemSelected(View view) {

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            predictionStartRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.predicted_start_unselected_bg));
            predictionMidRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.predicted_mid_unselected_bg));
            predictionEndRelativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.predicted_end_selected_bg));
        } else {
            predictionStartRelativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.predicted_start_unselected_bg));
            predictionMidRelativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.predicted_mid_unselected_bg));
            predictionEndRelativeLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.predicted_end_selected_bg));
        }
        predictionStartTextView.setTextColor(getResources().getColor(R.color.gray_text));
        predictionMidTextView.setTextColor(getResources().getColor(R.color.gray_text));
        predictionEndTextView.setTextColor(getResources().getColor(R.color.white));

        fillNutritionFactsTextViews(food3Facts);
    }

    public void setNutritionFactsColors() {
        TextView totalFatAmountTv = findViewById(R.id.totalFatAmountTextView);
        TextView cholesterolAmountTv = findViewById(R.id.cholesterolAmountTextView);
        TextView proteinAmountTv = findViewById(R.id.proteinAmountTextView);
        TextView carbohydratesAmountTv = findViewById(R.id.totalCarbohydratesAmountTextView);
        TextView sugarsAmountTv = findViewById(R.id.sugarsAmountTextView);

        TextView totalFatTv = findViewById(R.id.totalFatTextView);
        TextView cholesterolTv = findViewById(R.id.cholesterolTextView);
        TextView proteinTv = findViewById(R.id.proteinTextView);
        TextView carbohydratesTv = findViewById(R.id.totalCarbohydratesTextView);
        TextView sugarsTv = findViewById(R.id.sugarsTextView);
        try {
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

                        Float heightFloat;
                        Float weightFloat;
                        Float bmi = 0F;
                        if(!age.isEmpty() && !weight.isEmpty()) {
                            heightFloat = Float.parseFloat(height) / 100;
                            weightFloat = Float.parseFloat(weight);
                            bmi = weightFloat / (heightFloat*heightFloat);
                        }

                        if(bmi > 30 || goal.equals("I'd like to lose weight")) {
                                totalFatAmountTv.setTextColor(getResources().getColor(R.color.red));
                                sugarsAmountTv.setTextColor(getResources().getColor(R.color.red));
                                totalFatTv.setTextColor(getResources().getColor(R.color.red));
                                sugarsTv.setTextColor(getResources().getColor(R.color.red));
                                sugarsTv.setAlpha(1);
                                sugarsAmountTv.setAlpha(1);



                        } else if(bmi < 20 || goal.equals("I'd like to put on weight")) {
                            carbohydratesAmountTv.setTextColor(getResources().getColor(R.color.light_green));
                            proteinAmountTv.setTextColor(getResources().getColor(R.color.light_green));
                            carbohydratesTv.setTextColor(getResources().getColor(R.color.light_green));
                            proteinTv.setTextColor(getResources().getColor(R.color.light_green));
                        }

                        if(illness.equals("Diabetes")) {
                            sugarsAmountTv.setTextColor(getResources().getColor(R.color.red));
                            sugarsTv.setTextColor(getResources().getColor(R.color.red));
                            sugarsAmountTv.setAlpha(1);
                            sugarsTv.setAlpha(1);
                        }

                        if(illness.equals("Heart Disease")) {
                            totalFatAmountTv.setTextColor(getResources().getColor(R.color.red));
                            sugarsAmountTv.setTextColor(getResources().getColor(R.color.red));
                            cholesterolAmountTv.setTextColor(getResources().getColor(R.color.red));
                            totalFatTv.setTextColor(getResources().getColor(R.color.red));
                            sugarsTv.setTextColor(getResources().getColor(R.color.red));
                            cholesterolTv.setTextColor(getResources().getColor(R.color.red));
                            sugarsAmountTv.setAlpha(1);
                            sugarsTv.setAlpha(1);

                        }

                        if(illness.equals("High blood pressure")) {
                            totalFatAmountTv.setTextColor(getResources().getColor(R.color.red));
                            cholesterolAmountTv.setTextColor(getResources().getColor(R.color.red));
                            totalFatTv.setTextColor(getResources().getColor(R.color.red));
                            cholesterolTv.setTextColor(getResources().getColor(R.color.red));
                        }


                    }
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void fillNutritionFactsTextViews(FoodNutritionFacts selectedFood) {
        TextView caloriesAmountTv = findViewById(R.id.caloriesAmountTextView);
        TextView totalFatAmountTv = findViewById(R.id.totalFatAmountTextView);
        TextView saturatedFatAmountTv = findViewById(R.id.saturatedFatAmountTextView);
        TextView unSaturatedFatAmountTv = findViewById(R.id.unsaturatedFatAmountTextView);
        TextView polyunsaturatedFatAmountTv = findViewById(R.id.polyunsaturatedAmountTextView);
        TextView monounsaturatedFatAmountTv = findViewById(R.id.monounsaturatedAmountTextView);
        TextView cholesterolAmountTv = findViewById(R.id.cholesterolAmountTextView);
        TextView proteinAmountTv = findViewById(R.id.proteinAmountTextView);
        TextView carbohydratesAmountTv = findViewById(R.id.totalCarbohydratesAmountTextView);
        TextView dietaryFibersAmountTv = findViewById(R.id.dietaryFiberAmountTextView);
        TextView sugarsAmountTv = findViewById(R.id.sugarsAmountTextView);


        caloriesAmountTv.setText(selectedFood.getCalories());
        totalFatAmountTv.setText(selectedFood.getLipids());
        saturatedFatAmountTv.setText(selectedFood.getSaturatedFat());
        unSaturatedFatAmountTv.setText(selectedFood.getUnsaturatedFat());
        polyunsaturatedFatAmountTv.setText(selectedFood.getPolyunsaturatedFat());
        monounsaturatedFatAmountTv.setText(selectedFood.getMonounsaturatedFat());
        cholesterolAmountTv.setText(selectedFood.getCholesterol());
        proteinAmountTv.setText(selectedFood.getProtein());
        carbohydratesAmountTv.setText(selectedFood.getCarbohydrates());
        dietaryFibersAmountTv.setText(selectedFood.getDietaryFiber());
        sugarsAmountTv.setText(selectedFood.getSugars());

    }

    public void navBarProfileOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("userLoggedIn", true);
        startActivity(intent);
        finish();
    }

    public void selectButtonClicked(View view) {
        try {
            Intent  intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
            cameraTextView.setTextColor(getResources().getColor(R.color.gray_text));
            cameraImageView.setImageResource(R.drawable.ic_camera_gray);
            imageTextView.setTextColor(getResources().getColor(R.color.dark_orange));
            selectImageView.setImageResource(R.drawable.ic_image_orange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cameraButtonClicked(View view) {
        if(ContextCompat.checkSelfPermission(predictionActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(predictionActivity.this, new String[]{
                    Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    cameraTextView.setTextColor(getResources().getColor(R.color.dark_orange));
                    cameraImageView.setImageResource(R.drawable.ic_camera_orange);
                    imageTextView.setTextColor(getResources().getColor(R.color.gray_text));
                    selectImageView.setImageResource(R.drawable.ic_image_gray);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            try {
                Uri uri = data.getData();
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                rePredict();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            try {
                img = (Bitmap) data.getExtras().get("data");
                rePredict();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void rePredict() {
        imageView.setImageBitmap(img);
        predict();
        getTop3();
        getNutritionFacts();
        fillNutritionFactsTextViews(food1Facts);
    }

    public void homeButtonClicked(View view) {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
