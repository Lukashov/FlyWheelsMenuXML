package com.cuctomviews.den.testmenu;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();

        setContentView(R.layout.activity_main);
        FlyWheelMenu pie = (FlyWheelMenu) this.findViewById(R.id.Pie);

//        SectorFlyWheelView view = (SectorFlyWheelView) findViewById(R.id.sector1);
        for (int i = 0; i < 7; i++){
//            pie.addItem(1, getResources().getColor(R.color.fillSector),getResources().getColor(R.color.strokeColor));
        }

        RelativeLayout relativeLayout;

        LinearLayout linearLayout;

        FrameLayout frameLayout;

        RadioGroup radioGroup;

    }
}
