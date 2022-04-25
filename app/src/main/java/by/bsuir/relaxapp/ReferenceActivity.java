package by.bsuir.relaxapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class ReferenceActivity extends AppCompatActivity {

    ViewPager viewPager;
    DotsIndicator dots;
    ViewAdapter viewAdapter;

    private void getRidOfTopBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getRidOfTopBar();

        setContentView(R.layout.activity_reference);

        viewPager = findViewById(R.id.view_pager);
        dots = findViewById(R.id.dots);

        viewAdapter = new ViewAdapter(this);
        viewPager.setAdapter(viewAdapter);
        dots.setViewPager(viewPager);
    }
}