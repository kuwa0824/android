package io.github.kuwa0824.linecalc;

import android.app.*;
import android.os.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class SlasymActivity extends Activity 
{
    private EditText s1, s2, s3, s4, s5;
    private Button b1;
    private TextView a1;
    private ImageView fig;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slasym);
        s1 = findViewById(R.id.slasym_p1);
        s2 = findViewById(R.id.slasym_p2);
        s3 = findViewById(R.id.slasym_p3);
        s4 = findViewById(R.id.slasym_p4);
        s5 = findViewById(R.id.slasym_p5);
        b1 = findViewById(R.id.slasym_btn);
        a1 = findViewById(R.id.slasym_ans);
        fig = findViewById(R.id.slasym_img);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fig3);
        fig.setImageBitmap(bmp);
        
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double w, t, h1, h2, er;
                String s;
                try {
                    w = Double.parseDouble(s1.getText().toString());
                    t = Double.parseDouble(s2.getText().toString());
                    h1 = Double.parseDouble(s3.getText().toString());
                    h2 = Double.parseDouble(s4.getText().toString());
                    er = Double.parseDouble(s5.getText().toString());
                } catch(NumberFormatException e) {
                    w = 0;
                    t = 0;
                    h1 = 0;
                    h2 = 0;
                    er = 0;
                }
                double z = Linecalc.sl_asym(w, t, h1, h2, er);
                if(z <= 0) {
                    s = "NaN";
                } else {
                    s = String.format("%.3f", z);
                }
                a1.setText("Z [ohm] = "+s);
            }
        });
    }
}
