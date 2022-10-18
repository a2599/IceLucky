package com.albumi;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class PlugActivity extends AppCompatActivity {

    private Drawable[] drawables;
    private int units[] = new int[]{0,0,0,0,0,0,0,0,0};
    private boolean stop[] = new boolean[]{false,false,false,false,false,false,false,false,false};
    private int stopId = 0;
    private Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug);

        game();
    }
    //==============================================================================================

    private void game() {
        drawables = new Drawable[]{
                new BitmapDrawable(getResources(), BitmapFactory.decodeResource(this.getResources(), R.drawable.element1)),
                new BitmapDrawable(getResources(), BitmapFactory.decodeResource(this.getResources(), R.drawable.element2)),
                new BitmapDrawable(getResources(), BitmapFactory.decodeResource(this.getResources(), R.drawable.element3)),
                new BitmapDrawable(getResources(), BitmapFactory.decodeResource(this.getResources(), R.drawable.element4)),
                new BitmapDrawable(getResources(), BitmapFactory.decodeResource(this.getResources(), R.drawable.element5))
        };


        final ImageView slot1 = (ImageView) findViewById(R.id.slot1);
        final ImageView slot2 = (ImageView) findViewById(R.id.slot2);
        final ImageView slot3 = (ImageView) findViewById(R.id.slot3);
        final ImageView slot4 = (ImageView) findViewById(R.id.slot4);
        final ImageView slot5 = (ImageView) findViewById(R.id.slot5);
        final ImageView slot6 = (ImageView) findViewById(R.id.slot6);
        final ImageView slot7 = (ImageView) findViewById(R.id.slot7);
        final ImageView slot8 = (ImageView) findViewById(R.id.slot8);
        final ImageView slot9 = (ImageView) findViewById(R.id.slot9);


        loadSlot(slot1, 150, 0, 0);
        loadSlot(slot4, 150, 3, 0);
        loadSlot(slot7, 150, 6, 0);

        loadSlot(slot2, 100, 1, 0);
        loadSlot(slot5, 100, 4, 0);
        loadSlot(slot8, 100, 7, 0);

        loadSlot(slot3, 50, 2, 0);
        loadSlot(slot6, 50, 5, 0);
        loadSlot(slot9, 50, 8, 0);

        Button button = (Button) findViewById(R.id.spinButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (stopId>=3){

                    stop = new boolean[]{false, false, false,false,false,false,false,false,false};
                    loadSlot(slot1, 150, 0, 0);
                    loadSlot(slot4, 150, 3, 0);
                    loadSlot(slot7, 150, 6, 0);

                    loadSlot(slot2, 100, 1, 0);
                    loadSlot(slot5, 100, 4, 0);
                    loadSlot(slot8, 100, 7, 0);

                    loadSlot(slot3, 50, 2, 0);
                    loadSlot(slot6, 50, 5, 0);
                    loadSlot(slot9, 50, 8, 0);

                    stopId = 0;
                    button.setText("Stop");
                }else {
                    stop[stopId] = true;
                    stop[stopId+3] = true;
                    stop[stopId+6] = true;
                    stopId++;
                    if (stopId>=3)
                        button.setText("Spin");
                }
            }
        });

    }
    //==============================================================================================


    private void loadSlot(final ImageView imageView, final int delayMillis, final int id, final int prevUnit) {
        if(!stop[id]) {
            int unit = prevUnit;
            while (unit == prevUnit)
                unit = random.nextInt(drawables.length);

            imageView.setBackground(drawables[unit]);
            final int unitF = unit;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadSlot(imageView, delayMillis, id, unitF);
                }
            }, delayMillis);
        }  else
            units[id]=prevUnit;
    }
    //==============================================================================================



}