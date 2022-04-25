package by.bsuir.relaxapp;

import static by.bsuir.relaxapp.MainActivity.MAIN_ACTIVITY_CONTEXT;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UserInfoDB {
    public int WEIGHT;
    public int HEIGHT;
    public int SYS_PRESSURE;
    public int DIA_PRESSURE;
    public int AGE;
    public int ZODIAC;
    public Bitmap USER_PROFILE_IMAGE;
    public Bitmap USER_IMAGES[];
    public int REAL_IMAGES_COUNT;

    public UserInfoDB(int weight, int height, int sys_pressure, int dia_pressure,
                      int age, int zodiac, Bitmap user_profile_image, Bitmap[] user_images, int real_images_count){

        WEIGHT = weight;
        HEIGHT = height;
        SYS_PRESSURE = sys_pressure;
        DIA_PRESSURE = dia_pressure;
        AGE = age;
        ZODIAC = zodiac;
        USER_PROFILE_IMAGE = user_profile_image;
        USER_IMAGES = user_images;
        REAL_IMAGES_COUNT = real_images_count;
    }

    public UserInfoDB(){
        WEIGHT = -1;
        HEIGHT = -1;
        SYS_PRESSURE = -1;
        DIA_PRESSURE = -1;
        AGE = -1;
        ZODIAC = 0;
        USER_PROFILE_IMAGE = null;
        USER_IMAGES = new Bitmap[]{
                BitmapFactory.decodeResource(MAIN_ACTIVITY_CONTEXT.getResources(), R.drawable.add_image),
                        null,
                        null,
                        null,
                        null,
                        null
                };
        REAL_IMAGES_COUNT = 0;
    }

    @Override
    public String toString(){
        return WEIGHT + " кг\n" + HEIGHT + " см\n" + SYS_PRESSURE + "/" + DIA_PRESSURE + "\n" + AGE + " годиков\n" + ZODIAC + " зодиак\n"
                + "User profile image: " + (USER_PROFILE_IMAGE != null) + "\n" + "User images: " + REAL_IMAGES_COUNT + "\n";
    }
}
