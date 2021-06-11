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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements RecyclerViewOnItemClick{
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;

    String name;
    TextView nameTextView;

    ArrayList<String> suggestions;

    AutoCompleteTextView searchAutoCompleteTextView;

    String[] foodNames = {"COUSCOUS", "MLOUKHIA", "DOULMA", "TILTLI"};
    int[] images = {R.drawable.common_couscous, R.drawable.common_mloukhia, R.drawable.common_doulma, R.drawable.common_tlitli};
    int[] autoCompleteImages = {R.drawable.bourak,R.drawable.chakhchoukha,R.drawable.chtitha, R.drawable.common_couscous,
            R.drawable.ftir, R.drawable.common_doulma,R.drawable.mahchi,R.drawable.marka_hloua, R.drawable.common_mloukhia,
            R.drawable.common_tlitli, R.drawable.trida};
    String[] calories = {"360 calories in 100g", "239 calories in 100g", "190 calories in 100g", "320 calories in 100g"};
    RecyclerView recyclerView;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Toolbar toolbar;

    public static Bitmap img;
    public static Uri uri;

    public static int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        nameTextView = findViewById(R.id.nameTextView);

        setNameTextView();

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
        String[] var = {"Bourak", "Chakhchoukha", "Chtitha", "Couscous", "Ftir", "Kefta", "Mahchi"
                , "Marka hlouwa", "Mloukhia", "Tlitli", "Trida"};

        suggestions = new ArrayList<String>(Arrays.asList(var));
        recyclerView = findViewById(R.id.recyclerView);
        FoodItemAdapter adapter = new FoodItemAdapter(foodNames, images, calories, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView);
        searchAutoCompleteTextView.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line,suggestions));
        searchAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = suggestions.indexOf(searchAutoCompleteTextView.getText().toString());
                img = BitmapFactory.decodeResource(getResources(), autoCompleteImages[pos]);
                Intent intent = new Intent(getApplicationContext(), predictionActivity.class);
                intent.putExtra("selected", 1);
                startActivity(intent);

            }
        });

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);

    }

    private void setNameTextView() {
        DocumentReference documentReference = fstore.collection("users").document(mAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if(task.isSuccessful()) {
                    name = documentSnapshot.get("fullName").toString();
                    String[] fullName = name.split(" ");
                    name = fullName[0];
                    nameTextView.setText(name);
                }
            }
        });
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


    public void navBarProfileOnClick(View view) {
        openProfileActivity();
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
                uri = data.getData();
                img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Intent intent = new Intent(getApplicationContext(), predictionActivity.class);
                intent.putExtra("selected", 1);
                startActivity(intent);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            try {
                uri = data.getData();
                img = (Bitmap) data.getExtras().get("data");
                Intent intent = new Intent(getApplicationContext(), predictionActivity.class);
                intent.putExtra("selected", 0);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cameraButtonClicked(View view) {

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }
        }

    }

    @Override
    public void onItemClick(int position) {
        img = BitmapFactory.decodeResource(getResources(), images[position]);
        Intent intent = new Intent(getApplicationContext(), predictionActivity.class);
        intent.putExtra("selected", 1);
        startActivity(intent);
    }
}