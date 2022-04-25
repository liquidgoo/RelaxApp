package by.bsuir.relaxapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;

public class HoroscopeActivity extends AppCompatActivity {

    private void getRidOfTopBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    TextView zodiacSignName, zodiacSignDateRange, zodiacForetelling,
            potentialPartner, signMood, zodiacSignColor, luckyNumber,
            luckyTime;

    private void findAllTextViews(){
        zodiacSignName = findViewById(R.id.zodiacSignName);
        zodiacSignDateRange = findViewById(R.id.zodiacSignDateRange);
        zodiacForetelling = findViewById(R.id.zodiacForetelling);
        potentialPartner = findViewById(R.id.potentialPartner);
        signMood = findViewById(R.id.signMood);
        zodiacSignColor = findViewById(R.id.zodiacSignColor);
        luckyNumber = findViewById(R.id.luckyNumber);
        luckyTime = findViewById(R.id.luckyTime);
    }

    private void fillAllTextViews(){
        Sign currSign = MainActivity.signs[MainActivity.CURR_USER_DB_INFO.ZODIAC];
        String signName = currSign.name;

        signName = signName.substring(0,1).toUpperCase() + signName.substring(1).toLowerCase();

        zodiacSignName.setText(signName);
        zodiacSignDateRange.setText(currSign.dateRange);
        zodiacForetelling.setText(currSign.description);
        potentialPartner.setText(currSign.compatibility);
        signMood.setText(currSign.mood);
        zodiacSignColor.setText(currSign.color);
        luckyNumber.setText(currSign.luckyNumber);
        luckyTime.setText(currSign.luckyTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getRidOfTopBar();

        setContentView(R.layout.activity_horoscope);

        findAllTextViews();
        fillAllTextViews();
    }
}