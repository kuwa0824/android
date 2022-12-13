package io.github.kuwa0824.friis;

import android.app.*;
import android.os.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MainActivity extends Activity 
{
    private EditText s1, s2, s3, s4, s5, s6, s7, s8, s9, sA, sB;
    private CheckBox cb1, cb2;
    private Button b1;
    private TextView a1, a2, a3;
    private ImageView fig;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        s1 = findViewById(R.id.p1);
        s2 = findViewById(R.id.p2);
        s3 = findViewById(R.id.p3);
        s4 = findViewById(R.id.p4);
        s5 = findViewById(R.id.p5);
        s6 = findViewById(R.id.p6);
        s7 = findViewById(R.id.p7);
        s8 = findViewById(R.id.p8);
		s9 = findViewById(R.id.p9);
		sA = findViewById(R.id.pA);
		sB = findViewById(R.id.pB);
		cb1 = findViewById(R.id.c1);
		cb2 = findViewById(R.id.c2);
        b1 = findViewById(R.id.b1);
        a1 = findViewById(R.id.a1);
        a2 = findViewById(R.id.a2);
        a3 = findViewById(R.id.a3);
        fig = findViewById(R.id.img);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.friis);
        fig.setImageBitmap(bmp);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double fr, bw, txpw, rxnf, txant, rxant, dm;
                double att = 0.0;
                boolean flg1 = true;
                try {
                    fr = Double.parseDouble(s1.getText().toString());
                    txpw = Double.parseDouble(s3.getText().toString());
                    txant = Double.parseDouble(s5.getText().toString());
                    rxant = Double.parseDouble(s6.getText().toString());
                    dm = Double.parseDouble(s7.getText().toString());
                } catch(NumberFormatException e) {
                    flg1 = false;
                    fr = 0;
                    txpw = 0;
                    txant = 0;
                    rxant = 0;
                    dm = 0;
                }
                if(fr <= 0 || fr > 350 || dm <= 0) {
                    flg1 = false;
                }
                boolean flg2 = true;
                try {
                    bw = Double.parseDouble(s2.getText().toString());
                    rxnf = Double.parseDouble(s4.getText().toString());
                } catch(NumberFormatException e) {
                    flg2 = false;
                    bw = 0;
                    rxnf = 0;
                }
                if(bw <= 0 || rxnf < 0) {
                    flg2 = false;
                }
                if(cb1.isChecked()) {
                    try {
                        double ap = Double.parseDouble(s8.getText().toString());
                        double at = Double.parseDouble(s9.getText().toString());
                        double av = Double.parseDouble(sA.getText().toString());
                        if(ap >= 0 && at >= 0 && av >= 0) {
                            att += Friis.atm_att(fr, ap, at, av) * dm/1000;
                        } else {
                            flg1 = false;
                        }
                    } catch(NumberFormatException e) {
                        flg1 = false;
                    }
                }
                if(cb2.isChecked()) {
                    try {
                        double rr = Double.parseDouble(sB.getText().toString());
                        if(rr >= 0) {
                            att += Friis.rain_att(fr, rr) * dm/1000;
                        } else {
                            flg1 = false;
                        }
                    } catch(NumberFormatException e) {
                        flg1 = false;
                    }
                }
                if(!flg1) {
                    a1.setText("Received Power [dBm] = NaN");
                    a2.setText("E-field Strength [dBuV/m] = NaN");
                    a3.setText("Received S/N [dB] = NaN");
                } else {
                    double x = Friis.recv_power(fr, dm, txant, rxant, txpw);
                    x -= att;
                    a1.setText("Received Power [dBm] = " + String.format("%.3f", x));
                    double y = Friis.e_field_strength(x, fr, rxant);
                    a2.setText("E-field Strength [dBuV/m] = " + String.format("%.3f", y));
                    if(flg2) {
                        y = Friis.snr(x, rxnf, bw);
                        a3.setText("Received S/N [dB] = " + String.format("%.3f", y));
                    } else {
                        a3.setText("Received S/N [dB] = NaN");
                    }
                }
            }
        });
    }
}
