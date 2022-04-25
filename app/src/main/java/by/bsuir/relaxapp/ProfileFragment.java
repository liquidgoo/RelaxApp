package by.bsuir.relaxapp;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BLOB_STORE_SERVICE;
import static by.bsuir.relaxapp.MainActivity.CURR_USER_DB_INFO;
import static by.bsuir.relaxapp.MainActivity.DB_HELPER;
import static by.bsuir.relaxapp.MainActivity.MAIN_ACTIVITY_CONTEXT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    public static final int MAX_IMAGE_SIZE_IN_BYTES = 1500000;
    public static final int MAX_ATTACH_PHOTOS = 6;
    public static int ADD_IMAGE_INDEX = 0;


    private CircleImageView profilePhoto;

    private EditText
            BodyWeightEditText,
            HeightEditText,
            SystolicEditText,
            DiastolicEditText,
            AgeEditText;

    private Spinner horoscopeSpinner;

    private MaterialButton
            bodyWeightOKButton,
            heightOKButton,
            bloodPressureOKButton,
            ageOKButton,
            zodiacOKButton;

    private GridView photoGalleryGrid;


    public static boolean FETCH_USER_NAME_FIRST_TIME = true;

    private FirebaseUser user;
    private DatabaseReference reference;
    public static String fullName;
    private String email;

    private ProgressBar progressBar;

    public static String userID;

    private TextView userNameTextView;
    private TextView userEmailTextView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public ProfileFragment() {    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void insertNewUserIntoDatabase(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();

        {//TABLE_USERS
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.KEY_id, userID);
            contentValues.put(DatabaseHelper.KEY_weight, CURR_USER_DB_INFO.WEIGHT);
            contentValues.put(DatabaseHelper.KEY_height, CURR_USER_DB_INFO.HEIGHT);
            contentValues.put(DatabaseHelper.KEY_sysPressure, CURR_USER_DB_INFO.SYS_PRESSURE);
            contentValues.put(DatabaseHelper.KEY_diaPressure, CURR_USER_DB_INFO.DIA_PRESSURE);
            contentValues.put(DatabaseHelper.KEY_age, CURR_USER_DB_INFO.AGE);
            contentValues.put(DatabaseHelper.KEY_zodiac, CURR_USER_DB_INFO.ZODIAC);
            contentValues.put(DatabaseHelper.KEY_realImageCount, CURR_USER_DB_INFO.REAL_IMAGES_COUNT);

            sQlitedatabase.insert(DatabaseHelper.TABLE_USERS, null, contentValues);
        }

        {//TABLE_MOODS
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.KEY_id, userID);
            contentValues.put(DatabaseHelper.KEY_calm, 0);
            contentValues.put(DatabaseHelper.KEY_relax, 0);
            contentValues.put(DatabaseHelper.KEY_focus, 0);
            contentValues.put(DatabaseHelper.KEY_excited, 0);
            contentValues.put(DatabaseHelper.KEY_authentic, 0);

            sQlitedatabase.insert(DatabaseHelper.TABLE_MOODS, null, contentValues);
        }
        DB_HELPER.close();

        saveAttachableImagesToDatabase();
    }

    @SuppressLint("Range")
    private Bitmap[] fillUserImagesBytes(byte[][] userImagesBytes, SQLiteDatabase sQlitedatabase, int RealPhotoCount) {

        Bitmap[] toReturn = new Bitmap[MAX_ATTACH_PHOTOS];
        for (int i = 0; i < MAX_ATTACH_PHOTOS; ++i) {
            Cursor cursor = sQlitedatabase.query(
                    DatabaseHelper.TABLE_USERS,
                    new String[]{DatabaseHelper.SUPP_pic_str + String.valueOf(i)},
                    DatabaseHelper.KEY_id + " = '" + userID + "'",
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                userImagesBytes[i] = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.SUPP_pic_str + String.valueOf(i)));
                if (userImagesBytes[i] != null) {
                    toReturn[i] = BitmapFactory.decodeByteArray(userImagesBytes[i], 0, userImagesBytes[i].length);
                } else {
                    toReturn[i] = null;
                }
            }
        }

        if (RealPhotoCount == MAX_ATTACH_PHOTOS){
            ADD_IMAGE_INDEX = -1;
        } else if (RealPhotoCount == MAX_ATTACH_PHOTOS - 1){
            ADD_IMAGE_INDEX = MAX_ATTACH_PHOTOS - 1;
        } else{
            int j = -1;
            for (int i = 0; i < toReturn.length; ++i) {
                if (toReturn[i] == null) {
                    j = i;
                    break;
                }
            }

            if (j == -1){
                ADD_IMAGE_INDEX = -1;
            } else {
                ADD_IMAGE_INDEX = j - 1;
            }
        }

        return toReturn;
    }


    private void getDataFromDatabase(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();
        Cursor cursor = sQlitedatabase.query(
                DatabaseHelper.TABLE_USERS,
                new String[] {
                        DatabaseHelper.KEY_weight,
                        DatabaseHelper.KEY_height,
                        DatabaseHelper.KEY_sysPressure,
                        DatabaseHelper.KEY_diaPressure,
                        DatabaseHelper.KEY_age,
                        DatabaseHelper.KEY_zodiac,
                        DatabaseHelper.KEY_profilePic,
                        DatabaseHelper.KEY_realImageCount
                        /*DatabaseHelper.KEY_pic0,
                        DatabaseHelper.KEY_pic1,
                        DatabaseHelper.KEY_pic2, не влазиют в один курсор, переносим в fillUserImagesBytes
                        DatabaseHelper.KEY_pic3,
                        DatabaseHelper.KEY_pic4,
                        DatabaseHelper.KEY_pic5*/},
                DatabaseHelper.KEY_id + " = '" + userID + "'",
                null, null, null, null);

        if (cursor.moveToFirst()) {
            int weightInd = cursor.getColumnIndex(DatabaseHelper.KEY_weight);
            int heightInd = cursor.getColumnIndex(DatabaseHelper.KEY_height);
            int sysPressureInd = cursor.getColumnIndex(DatabaseHelper.KEY_sysPressure);
            int diaPressureInd = cursor.getColumnIndex(DatabaseHelper.KEY_diaPressure);
            int ageInd = cursor.getColumnIndex(DatabaseHelper.KEY_age);
            int zodiacInd = cursor.getColumnIndex(DatabaseHelper.KEY_zodiac);
            int profilePicInd = cursor.getColumnIndex(DatabaseHelper.KEY_profilePic);
            int realPhotoCount = cursor.getColumnIndex(DatabaseHelper.KEY_realImageCount);

            int earlyRealPhotoCount = cursor.getInt(realPhotoCount);

            byte[] profileImageBytes = cursor.getBlob((profilePicInd));
            byte[][] userImagesBytes = new byte[MAX_ATTACH_PHOTOS][];
            Bitmap[] userImagesBitmaps = fillUserImagesBytes(userImagesBytes, sQlitedatabase, earlyRealPhotoCount);
            CURR_USER_DB_INFO = new UserInfoDB(
                        cursor.getInt(weightInd),
                        cursor.getInt(heightInd),
                        cursor.getInt(sysPressureInd),
                        cursor.getInt(diaPressureInd),
                        cursor.getInt(ageInd),
                        cursor.getInt(zodiacInd),
                    (profileImageBytes == null) ? null : BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length),
                    userImagesBitmaps,//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!/,
                    earlyRealPhotoCount
                );



        } else {
            CURR_USER_DB_INFO = new UserInfoDB();
            insertNewUserIntoDatabase();
            ADD_IMAGE_INDEX = 0;
        }

        Log.e("DB", CURR_USER_DB_INFO.toString());
        DB_HELPER.close();
    }

    private void fillEditTexts(){
        if (CURR_USER_DB_INFO.WEIGHT != -1){
            BodyWeightEditText.setText(String.valueOf(CURR_USER_DB_INFO.WEIGHT));
        }
        if (CURR_USER_DB_INFO.HEIGHT != -1){
            HeightEditText.setText(String.valueOf(CURR_USER_DB_INFO.HEIGHT));
        }
        if (CURR_USER_DB_INFO.SYS_PRESSURE != -1){
            SystolicEditText.setText(String.valueOf(CURR_USER_DB_INFO.SYS_PRESSURE));
        }
        if (CURR_USER_DB_INFO.DIA_PRESSURE != -1){
            DiastolicEditText.setText(String.valueOf(CURR_USER_DB_INFO.DIA_PRESSURE ));
        }
        if (CURR_USER_DB_INFO.AGE != -1){
            AgeEditText.setText(String.valueOf(CURR_USER_DB_INFO.AGE));
        }
    }

    private void findAllOKButtons(View view){
        bodyWeightOKButton = view.findViewById(R.id.BodyWeightOKButton);
        heightOKButton = view.findViewById(R.id.HeightOKButton);
        bloodPressureOKButton = view.findViewById(R.id.BloodPressureOKButton);
        ageOKButton = view.findViewById(R.id.AgeOKButton);
        zodiacOKButton = view.findViewById(R.id.HoroscopeOKButton);
    }

    private void findAllEditTexts(View view) {
        BodyWeightEditText = view.findViewById(R.id.BodyWeightEditText);
        HeightEditText = view.findViewById(R.id.HeightEditText);
        SystolicEditText = view.findViewById(R.id.SystolicEditText);
        DiastolicEditText = view.findViewById(R.id.DiastolicEditText);
        AgeEditText = view.findViewById(R.id.AgeEditText);
    }

    private void findHoroscopeSpinner(View view){
        horoscopeSpinner = view.findViewById(R.id.HoroscopeSpinner);
    }

    private void findPhotoGalleryGrid(View view){
        photoGalleryGrid = view.findViewById(R.id.photoGalleryGrid);
    }

    private void findProfilePhoto(View view){
        profilePhoto = view.findViewById(R.id.profilePhoto);
    }

    private boolean weightCorrect(){
        String weightStr = BodyWeightEditText.getText().toString().trim();
        try{
            int weight = Integer.parseInt(weightStr);

            if (weight < 20 || weight > 350) return false;

            CURR_USER_DB_INFO.WEIGHT = weight;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void updateWeight(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_weight, CURR_USER_DB_INFO.WEIGHT);
        String where = DatabaseHelper.KEY_id + " = '" + userID + "'";
        sQlitedatabase.update(DatabaseHelper.TABLE_USERS, contentValues, where, null);

        DB_HELPER.close();
    }

    private boolean heightCorrect(){
        String heightStr = HeightEditText.getText().toString().trim();
        try{
            int height = Integer.parseInt(heightStr);

            if (height < 50 || height > 290) return false;

            CURR_USER_DB_INFO.HEIGHT = height;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void updateHeight(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_height, CURR_USER_DB_INFO.HEIGHT);
        String where = DatabaseHelper.KEY_id + " = '" + userID + "'";
        sQlitedatabase.update(DatabaseHelper.TABLE_USERS, contentValues, where, null);

        DB_HELPER.close();
    }

    private boolean sysPressureCorrect(){
        String sysStr = SystolicEditText.getText().toString().trim();
        try{
            int sys = Integer.parseInt(sysStr);

            if (sys < 100 || sys > 210) return false;

            CURR_USER_DB_INFO.SYS_PRESSURE = sys;
        } catch (NumberFormatException e) {
            return false;
        }


        return true;
    }

    private void updateSysPressure(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_sysPressure, CURR_USER_DB_INFO.SYS_PRESSURE);
        String where = DatabaseHelper.KEY_id + " = '" + userID + "'";
        sQlitedatabase.update(DatabaseHelper.TABLE_USERS, contentValues, where, null);

        DB_HELPER.close();
    }

    private boolean diaPressureCorrect(){
        String diaStr = DiastolicEditText.getText().toString().trim();
        try{
            int dia = Integer.parseInt(diaStr);

            if (dia < 60 || dia > 150) return false;

            CURR_USER_DB_INFO.DIA_PRESSURE= dia;
        } catch (NumberFormatException e) {
            return false;
        }


        return true;
    }

    private void updateDiaPressure(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_diaPressure, CURR_USER_DB_INFO.DIA_PRESSURE);
        String where = DatabaseHelper.KEY_id + " = '" + userID + "'";
        sQlitedatabase.update(DatabaseHelper.TABLE_USERS, contentValues, where, null);

        DB_HELPER.close();
    }

    private void updateZodiac(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_zodiac, CURR_USER_DB_INFO.ZODIAC);
        String where = DatabaseHelper.KEY_id + " = '" + userID + "'";
        sQlitedatabase.update(DatabaseHelper.TABLE_USERS, contentValues, where, null);

        DB_HELPER.close();
    }

    private boolean ageCorrect(){
        String ageStr = AgeEditText.getText().toString().trim();
        try{
            int age = Integer.parseInt(ageStr);

            if (age < 1 || age > 190) return false;

            CURR_USER_DB_INFO.AGE = age;
        } catch (NumberFormatException e) {
            return false;
        }


        return true;
    }

    private void updateAge(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_age, CURR_USER_DB_INFO.AGE);
        String where = DatabaseHelper.KEY_id + " = '" + userID + "'";
        sQlitedatabase.update(DatabaseHelper.TABLE_USERS, contentValues, where, null);

        DB_HELPER.close();
    }

    private void updateProfilePicture(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CURR_USER_DB_INFO.USER_PROFILE_IMAGE.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageInBytes = byteArrayOutputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_profilePic, imageInBytes);
        String where = DatabaseHelper.KEY_id + " = '" + userID + "'";
        sQlitedatabase.update(DatabaseHelper.TABLE_USERS, contentValues, where, null);

        DB_HELPER.close();
    }

    private static final int PICK_IMAGE_REQUEST = 1337;
    private static final int ATTACH_PHOTO = 228;
    private Uri imageFilePath;
    private Bitmap profileImageToBeStored;

    private void chooseProfilePic(){
        Intent intent = new Intent();
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void addAttachablePhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ATTACH_PHOTO);
    }

    private boolean checkIfImageIsLessThanPoltoraMegabyte(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageInBytes = byteArrayOutputStream.toByteArray();
        return imageInBytes.length < MAX_IMAGE_SIZE_IN_BYTES;
    }

    private void saveAttachableImagesToDatabase(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();

        for (int i = 0; i < MAX_ATTACH_PHOTOS; i++) {
            ContentValues contentValues = new ContentValues();
            if (CURR_USER_DB_INFO.USER_IMAGES[i] == null){
                contentValues.putNull(DatabaseHelper.SUPP_pic_str + String.valueOf(i));
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                CURR_USER_DB_INFO.USER_IMAGES[i].compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageInBytes = byteArrayOutputStream.toByteArray();

                contentValues.put(DatabaseHelper.SUPP_pic_str + String.valueOf(i), imageInBytes);
            }
            String where = DatabaseHelper.KEY_id + " = '" + userID + "'";
            sQlitedatabase.update(DatabaseHelper.TABLE_USERS, contentValues, where, null);
        }

        DB_HELPER.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if      (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null){
            imageFilePath = data.getData();
            try {
                profileImageToBeStored = MediaStore.Images.Media.getBitmap(MAIN_ACTIVITY_CONTEXT.getContentResolver(), imageFilePath);
                if (checkIfImageIsLessThanPoltoraMegabyte(profileImageToBeStored)) {
                    profilePhoto.setImageBitmap(profileImageToBeStored);
                    CURR_USER_DB_INFO.USER_PROFILE_IMAGE = profileImageToBeStored;
                    updateProfilePicture();
                } else{
                    Toast.makeText(MainActivity.MAIN_ACTIVITY_CONTEXT, "Images have to be less than 1.5 MB!", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        else

            if (requestCode == ATTACH_PHOTO && resultCode == RESULT_OK &&
                data != null && data.getData() != null){
                imageFilePath = data.getData();
                try {
                    Bitmap localBitmap = MediaStore.Images.Media.getBitmap(MAIN_ACTIVITY_CONTEXT.getContentResolver(), imageFilePath);
                    if (checkIfImageIsLessThanPoltoraMegabyte(localBitmap)) {
                        CURR_USER_DB_INFO.USER_IMAGES[ADD_IMAGE_INDEX] = localBitmap;
                        if (ADD_IMAGE_INDEX <= MAX_ATTACH_PHOTOS - 2) {
                            CURR_USER_DB_INFO.USER_IMAGES[ADD_IMAGE_INDEX + 1] =
                                    BitmapFactory.decodeResource(MAIN_ACTIVITY_CONTEXT.getResources(), R.drawable.add_image);
                        }

                        CURR_USER_DB_INFO.REAL_IMAGES_COUNT++;
                        saveImageCountToDatabase();

                        ADD_IMAGE_INDEX++;
                        if (ADD_IMAGE_INDEX == MAX_ATTACH_PHOTOS) ADD_IMAGE_INDEX = -1;
                        initAttachablePhotosAdapter();
                        saveAttachableImagesToDatabase();
                    } else{
                        Toast.makeText(MainActivity.MAIN_ACTIVITY_CONTEXT, "Images have to be less than 1.5 MB!", Toast.LENGTH_LONG).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    private void initAttachablePhotosAdapter(){
        ProfileImageGridAdapter photoAdapter = new ProfileImageGridAdapter(MainActivity.MAIN_ACTIVITY_CONTEXT, CURR_USER_DB_INFO.USER_IMAGES);
        photoGalleryGrid.setAdapter(photoAdapter);
        photoGalleryGrid.setOnItemClickListener((adapterView, view1, i, l) -> {
            if (i == ADD_IMAGE_INDEX){
                addAttachablePhoto();
            } else {
                Bitmap item_pos = CURR_USER_DB_INFO.USER_IMAGES[i];
                showDialogBox(item_pos, i);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        MusicFragment.stopMusic();

        progressBar = view.findViewById(R.id.progressBarProfileFragment);


        findProfilePhoto(view);
        profilePhoto.setOnClickListener(lambda->{
            chooseProfilePic();
        });

        findAllOKButtons(view);
        findAllEditTexts(view);
        findHoroscopeSpinner(view);
        findPhotoGalleryGrid(view);

        bodyWeightOKButton.setOnClickListener(lambda->{
            if (weightCorrect()) {
                updateWeight();
            } else{
                BodyWeightEditText.setError("20<=Вес<=350!");
                BodyWeightEditText.requestFocus();
            }
        });
        heightOKButton.setOnClickListener(lambda->{
            if (heightCorrect()){
                updateHeight();
            } else{
                HeightEditText.setError("50<=Рост<=290!");
                HeightEditText.requestFocus();
            }
        });
        bloodPressureOKButton.setOnClickListener(lambda->{
            if (sysPressureCorrect()){
                updateSysPressure();
            } else{
                SystolicEditText.setError("100<=Сист.<=210!");
                SystolicEditText.requestFocus();
            }

            if (diaPressureCorrect()){
                updateDiaPressure();
            } else{
                DiastolicEditText.setError("60<=Диаст.<=150!");
                DiastolicEditText.requestFocus();
            }
        });
        ageOKButton.setOnClickListener(lambda->{
            if (ageCorrect()){
                updateAge();
            } else{
                AgeEditText.setError("1<=Возраст<=190!");
                AgeEditText.requestFocus();
            }
        });
        zodiacOKButton.setOnClickListener(lambda->{
            CURR_USER_DB_INFO.ZODIAC = horoscopeSpinner.getSelectedItemPosition();
            updateZodiac();
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.MAIN_ACTIVITY_CONTEXT,
                R.array.horoscope_options_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        horoscopeSpinner.setAdapter(adapter);


        userNameTextView = view.findViewById(R.id.UserNameTextView);
        userEmailTextView = view.findViewById(R.id.UserEmailTextView);
        if (FETCH_USER_NAME_FIRST_TIME) {
            progressBar.setVisibility(View.VISIBLE);
            user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users");
            userID = user.getUid();

            Log.e("ID", userID);

            reference.child(userID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User userProfile = snapshot.getValue(User.class);

                            if (userProfile != null) {
                                fullName = userProfile.name;
                                email = userProfile.email;

                                userNameTextView.setText(fullName);
                                userEmailTextView.setText(email);
                                //!ВНИМАНИЕ НАЧИНАЕТСЯ ЧТЕНИЕ ИЗ БД
                                {
                                    getDataFromDatabase();
                                    fillEditTexts();
                                    if (CURR_USER_DB_INFO.ZODIAC != -1) {
                                        horoscopeSpinner.setSelection(CURR_USER_DB_INFO.ZODIAC);
                                    }
                                    if (CURR_USER_DB_INFO.USER_PROFILE_IMAGE != null){
                                        profilePhoto.setImageBitmap(CURR_USER_DB_INFO.USER_PROFILE_IMAGE);
                                    }
                                    initAttachablePhotosAdapter();
                                }
                                //!ВНИМАНИЕ
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.MAIN_ACTIVITY_CONTEXT, "Something wrong!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });

            FETCH_USER_NAME_FIRST_TIME = false;
        } else {
            userNameTextView.setText(fullName);
            userEmailTextView.setText(email);
            if (CURR_USER_DB_INFO.USER_PROFILE_IMAGE != null){
                profilePhoto.setImageBitmap(CURR_USER_DB_INFO.USER_PROFILE_IMAGE);
            }
            initAttachablePhotosAdapter();
        }

        return view;
    }

    private void deleteGalleryItem(int toDeleteIndex){
        if (ADD_IMAGE_INDEX == -1){
            if (toDeleteIndex != MAX_ATTACH_PHOTOS - 1){
                int count = MAX_ATTACH_PHOTOS - 2;
                for (int i = toDeleteIndex; i <= count; i++){
                    CURR_USER_DB_INFO.USER_IMAGES[i] = CURR_USER_DB_INFO.USER_IMAGES[i + 1];
                }
            }
            CURR_USER_DB_INFO.USER_IMAGES[MAX_ATTACH_PHOTOS - 1] = BitmapFactory.decodeResource(MAIN_ACTIVITY_CONTEXT.getResources(), R.drawable.add_image);
            ADD_IMAGE_INDEX = MAX_ATTACH_PHOTOS - 1;
        } else{
            int count = ADD_IMAGE_INDEX - 1;
            for (int i = toDeleteIndex; i <= count; i++){
                CURR_USER_DB_INFO.USER_IMAGES[i] = CURR_USER_DB_INFO.USER_IMAGES[i + 1];
            }

            for (int i = count + 1; i < MAX_ATTACH_PHOTOS; ++i){
                CURR_USER_DB_INFO.USER_IMAGES[i] = null;
            }
            ADD_IMAGE_INDEX = count;
        }

        CURR_USER_DB_INFO.REAL_IMAGES_COUNT--;
        saveImageCountToDatabase();

        initAttachablePhotosAdapter();
        saveAttachableImagesToDatabase();
    }


    public void showDialogBox(Bitmap itemId, int i){
        Dialog dialog = new Dialog(MAIN_ACTIVITY_CONTEXT, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.gallery_item_dialog_box);

        ImageView imgView = dialog.findViewById(R.id.ItemImage);
        Button deleteItemBtn = dialog.findViewById(R.id.deleteItemButton);
        Button closeItemBtn = dialog.findViewById(R.id.closeItemButton);

        imgView.setImageBitmap(itemId);

        closeItemBtn.setOnClickListener(lambda->{
            dialog.dismiss();
        });

        deleteItemBtn.setOnClickListener(lambda->{
            deleteGalleryItem(i);
            dialog.dismiss();
        });

        dialog.show();
    }









    private void saveImageCountToDatabase(){
        DB_HELPER = new DatabaseHelper(MainActivity.MAIN_ACTIVITY_CONTEXT);
        SQLiteDatabase sQlitedatabase = DB_HELPER.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_realImageCount, CURR_USER_DB_INFO.REAL_IMAGES_COUNT);
        String where = DatabaseHelper.KEY_id + " = '" + userID + "'";
        sQlitedatabase.update(DatabaseHelper.TABLE_USERS, contentValues, where, null);

        DB_HELPER.close();
    }
}