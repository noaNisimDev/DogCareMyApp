package com.example.dogcaremyapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Main_Activity extends AppCompatActivity {

    private String TAG = "Main_Activity";

    //Buttons
    private Button main_BTN_left;
    private Button main_BTN_center;
    private Button main_BTN_right;
    private Button main_BTN_logout;

    private LinearLayout login_LAY_linearlistdogs;
    private LinearLayout login_LAY_linearbody;

    private TextView main_TXT_dogname;

    private List<Dog> userDogs;
    private Dog selectedDog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private SweetAlertDialog pDialog;
    private String israelPrefix = "+972 ";

    //table layout
    private TableLayout main_TBL_dogdetails;
    private View view;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //clear screen - no titles
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        selectedDog = new Dog();


        setContentView(R.layout.main_activity);

        findViews();
        main_BTN_left.setEnabled(false);
        main_BTN_right.setEnabled(false);
        userDogs = new ArrayList<Dog>();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        //real ad - waiting for google verification
        //mInterstitialAd.setAdUnitId("ca-app-pub-4243140761374436/6107989060");
        //test ad just for now
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        final AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd.loadAd(adRequestBuilder.build());
            }
        });
        mInterstitialAd.loadAd(adRequestBuilder.build());


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading..");
        pDialog.setCancelable(false);
        pDialog.show();

        new CountDownTimer(7000, 1000) {
            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                pDialog.dismiss();
            }

        }.start();


        //get user dogs
        mDatabase.getDatabase().getReference("users").child(mAuth.getUid()).child("dogs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String dogID = snapshot.getValue(String.class);
                mDatabase.getDatabase().getReference("dogs").orderByChild("id").equalTo(dogID).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Dog dog = snapshot.getValue(Dog.class);
                        userDogs.add(dog);
                        addDogImageToUpper(dog.getImageUri(), dog);
                        pDialog.dismiss();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged: " + snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved: " + snapshot);
                int deletedDogIndex = -1;
                for (int i = 0; i < userDogs.size(); i++) {
                    if (userDogs.get(i).getId().equals(snapshot.getValue().toString())) {
                        deletedDogIndex = i;
                    }
                }
                userDogs.remove(deletedDogIndex);
                login_LAY_linearlistdogs.removeViewAt(deletedDogIndex);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildMoved: " + snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        main_BTN_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(CreateNewDog_Activity.class);
            }
        });

        //share
        main_BTN_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Main_Activity.this);
                alert.setTitle("Share " + selectedDog.getName() + " with other dogcare users");
                alert.setMessage("Enter your friend's phone number");

                // Set an EditText view to get user input
                final EditText input = new EditText(Main_Activity.this);
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String phoneNumber = input.getText().toString();
                        phoneNumber = israelPrefix.concat(String.copyValueOf(phoneNumber.toCharArray(), 1, phoneNumber.length() - 1)).replaceAll("\\s+", "");

                        mDatabase.child("users").orderByChild("phoneNumber").equalTo(phoneNumber).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                if (snapshot.exists()) {

                                    snapshot.getRef().child("dogs").child(selectedDog.getId()).setValue(selectedDog.getId());
                                } else {
                                    //user not found
                                    Toast.makeText(getApplicationContext(), "Sory, this phone number isn't a dogcare member.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Toast.makeText(Main_Activity.this, selectedDog.getName() + " shared", Toast.LENGTH_SHORT).show();;

                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alert.show();

            }
        });


        //delete
        main_BTN_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main_Activity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Delete " + selectedDog.getName() + " ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        mDatabase.child("users")
                                .child(mAuth.getCurrentUser().getUid())
                                .child("dogs")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            if (childSnapshot.getValue(String.class).equals(selectedDog.getId())) {
                                                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("dogs").child(childSnapshot.getKey()).removeValue();
                                                main_TBL_dogdetails.removeAllViews();
                                                main_TXT_dogname.setText("");
                                                Toast.makeText(Main_Activity.this, "say goodbye", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO, I was wrong", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        main_BTN_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mAuth.signOut();
                launchActivity(Login_Activity.class);
            }
        });
    }

    private void addDogImageToUpper(String imageUri, final Dog dog) {
        final ImageButton dogImageButton = new ImageButton(this);
        Glide
                .with(this)
                .load(Uri.parse(imageUri)) // the uri you got from Firebase
                .circleCrop()
                .apply(new RequestOptions().override(600, 200))
                .into(dogImageButton);

        dogImageButton.setBackgroundResource(R.drawable.roundcorners);
        dogImageButton.setMaxHeight(login_LAY_linearlistdogs.getHeight());
        dogImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                selectedDog = dog;
                try {
                    showDogTable(selectedDog);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                main_TXT_dogname.setText(dog.getName());

                main_BTN_left.setEnabled(true);
                main_BTN_right.setEnabled(true);
                for (int i = 0; i < login_LAY_linearlistdogs.getChildCount(); i++) {
                    login_LAY_linearlistdogs.getChildAt(i).setAlpha(0.5f);
                }
                view.setAlpha(1f);
            }
        });

        login_LAY_linearlistdogs.addView(dogImageButton);

    }

    //from button to Activity
    private void launchActivity(Class Activity) {
        Intent intent = new Intent(this, Activity);
        startActivity(intent);
    }


    private void findViews() {
        main_BTN_left = findViewById(R.id.main_BTN_left);
        main_BTN_center = findViewById(R.id.main_BTN_center);
        main_BTN_right = findViewById(R.id.main_BTN_right);
        main_BTN_logout = findViewById(R.id.main_BTN_logout);
        main_TBL_dogdetails = findViewById(R.id.main_TBL_dogdetails);
        login_LAY_linearlistdogs = findViewById(R.id.login_LAY_linearlistdogs);
        login_LAY_linearbody = findViewById(R.id.login_LAY_linearbody);
        main_TXT_dogname = findViewById(R.id.main_TXT_dogname);

    }

    private void addRow(Boolean walk, Boolean meal, boolean isHeadline, final int rowNumber, final Dog dog) throws ParseException {
        TableRow row = new TableRow(this);

        TableRow.LayoutParams tableParams = new TableRow.LayoutParams();
        tableParams.setMargins(20, 20, 20, 20);
        row.setGravity(Gravity.CENTER_HORIZONTAL);

        if (isHeadline) {
            TextView walksHeadline = new TextView(this);
            TextView mealsHeadline = new TextView(this);

            walksHeadline.setText("Walks");
            mealsHeadline.setText("Meals");

            walksHeadline.setTextColor(Color.BLACK);
            mealsHeadline.setTextColor(Color.BLACK);

            walksHeadline.setTextSize(25);
            mealsHeadline.setTextSize(25);

            row.addView(walksHeadline, tableParams);
            row.addView(mealsHeadline, tableParams);

        } else {
            final CheckBox walkCheckbox = new CheckBox(this);
            final CheckBox mealCheckbox = new CheckBox(this);

            Date todayDate = Calendar.getInstance().getTime();
            Date zeroDate = new Date(0);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM");
            final String todayString = formatter.format(todayDate);
            final String zeroString = formatter.format(zeroDate);

            DatabaseReference dogRef = mDatabase.getDatabase().getReference("dogs").child(selectedDog.getId());
            final DatabaseReference walkOperations = dogRef.child("todayWalks").child(String.valueOf(rowNumber)).child("walkDate");
            final DatabaseReference mealOperations = dogRef.child("todayMeals").child(String.valueOf(rowNumber)).child("mealDate");

            String walkDate = todayString;
            String mealDate = todayString;
            try {
                walkDate = selectedDog.getTodayWalks().get(rowNumber).getWalkDate();
            } catch (IndexOutOfBoundsException e) {
                //ignore no walk in this row
            }
            try {
                mealDate = selectedDog.getTodayMeals().get(rowNumber).getMealDate();
            } catch (IndexOutOfBoundsException e) {
                //ignore no meal in this row
            }

            if (!walk)
                walkCheckbox.setVisibility(View.INVISIBLE);
            if (!meal)
                mealCheckbox.setVisibility(View.INVISIBLE);
            //delete
            /*mDatabase.getDatabase().getReference("users").child(mAuth.getUid()).child("dogs").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String dogID = snapshot.getValue(String.class);
                    Log.d(TAG, "onChildAdded: " + dogID);
                    mDatabase.getDatabase().getReference("dogs").orderByChild("id").equalTo(dogID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot datas : snapshot.getChildren()) {
                                    String key = datas.getKey();
                                    final DatabaseReference mealOperations = mDatabase.getDatabase().getReference("dogs").child(key).child("todayMeals").child(String.valueOf(rowNumber)).child("mealDate");
                                    final DatabaseReference walkOperations = mDatabase.getDatabase().getReference("dogs").child(key).child("todayWalks").child(String.valueOf(rowNumber)).child("walkDate");
                                    Dog currentDogFromDB =  datas.getValue(Dog.class);
                                    Date todayDate = Calendar.getInstance().getTime();
                                    Date zeroDate = new Date(0);
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM");
                                    final String todayString = formatter.format(todayDate);
                                    final String zeroString = formatter.format(zeroDate);

                                    String lastMeal = null;
                                    String lastWalk = null;


                                    try {
                                        lastMeal = selectedDog.getTodayMeals().get(rowNumber).getMealDate();
                                        if (sameDay(lastMeal, todayString, formatter)) {
                                            mealCheckbox.setChecked(true);
                                        }
                                        else{
                                            mealCheckbox.setChecked(false);
                                        }

                                        mealCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    mealOperations.setValue(todayString);
                                                    selectedDog.getTodayMeals().get(rowNumber).setMealDate(todayString);

                                                } else {
                                                    mealOperations.setValue(zeroString);
                                                    selectedDog.getTodayMeals().get(rowNumber).setMealDate(zeroString);
                                                }

                                            }
                                        });

                                    } catch (IndexOutOfBoundsException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        lastWalk = selectedDog.getTodayWalks().get(rowNumber).getWalkDate();
                                        if (sameDay(lastWalk, todayString, formatter)) {
                                            walkCheckbox.setChecked(true);
                                        }
                                        else{
                                            walkCheckbox.setChecked(false);
                                        }
                                        walkCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                if (b) {
                                                    Log.d(TAG, "onCheckedChanged: walkOperations " + walkOperations +  " set true");
                                                    Log.d(TAG, "onCheckedChanged: DOG = " + selectedDog);
                                                    walkOperations.setValue(todayString);
                                                    selectedDog.getTodayWalks().get(rowNumber).setWalkDate(todayString);

                                                } else{
                                                    Log.d(TAG, "onCheckedChanged: walkOperations " + walkOperations +  " set false");
                                                    Log.d(TAG, "onCheckedChanged: DOG = " + selectedDog);
                                                    walkOperations.setValue(zeroString);
                                                    selectedDog.getTodayWalks().get(rowNumber).setWalkDate(zeroString);
                                                }

                                            }
                                        });
                                    } catch (IndexOutOfBoundsException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/

            if (sameDay(walkDate, todayString, formatter)) {
                walkCheckbox.setChecked(true);
            }

            if (sameDay(mealDate, todayString, formatter)) {
                mealCheckbox.setChecked(true);
            }


            walkCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        walkOperations.setValue(todayString);
                        selectedDog.getTodayWalks().get(rowNumber).setWalkDate(todayString);

                    } else {
                        walkOperations.setValue(zeroString);
                        selectedDog.getTodayWalks().get(rowNumber).setWalkDate(zeroString);
                    }

                }
            });

            mealCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        mealOperations.setValue(todayString);
                        selectedDog.getTodayMeals().get(rowNumber).setMealDate(todayString);

                    } else {
                        mealOperations.setValue(zeroString);
                        selectedDog.getTodayMeals().get(rowNumber).setMealDate(zeroString);
                    }

                }
            });

            //end delete
            row.addView(walkCheckbox, tableParams);
            row.addView(mealCheckbox, tableParams);

        }
        main_TBL_dogdetails.addView(row);
    }

    private void showDogTable(Dog dog) throws ParseException {
        main_TBL_dogdetails.removeAllViews();
        int rowNumber = 0;
        //add headline
        addRow(false, false, true, 0, dog);

        int counterOfWalks = dog.getNumOfWalksPerDay();
        int counterOfMeals = dog.getNumOfMealsPerDay();
        for (int i = 0; i < getHigherNumber(dog.getNumOfMealsPerDay(), dog.getNumOfWalksPerDay()); i++) {

            if (counterOfMeals > 0 && counterOfWalks > 0)
                addRow(true, true, false, rowNumber, dog);

            else if (counterOfMeals > 0) {
                addRow(false, true, false, rowNumber, dog);
            } else if (counterOfWalks > 0) {
                addRow(true, false, false, rowNumber, dog);
            }
            counterOfMeals--;
            counterOfWalks--;
            rowNumber++;
        }
    }

    private int getHigherNumber(int first, int second) {
        if (first > second)
            return first;
        else
            return second;
    }

    private boolean sameDay(String day1, String day2, SimpleDateFormat formatter) throws ParseException {
        boolean same = false;
        Date first = formatter.parse(day1);
        Date second = formatter.parse(day2);
        if (first.compareTo(second) == 0)
            same = true;
        return same;
    }

}