package io.github.kuwa0824.linecalc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DiffmslActivity extends Activity 
{
    private EditText s1, s2, s3, s4, s5;
    private Button b1;
    private TextView a1, a2;
    private ImageView fig;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diffmsl);
        s1 = findViewById(R.id.diffmsl_p1);
        s2 = findViewById(R.id.diffmsl_p2);
        s3 = findViewById(R.id.diffmsl_p3);
        s4 = findViewById(R.id.diffmsl_p4);
        s5 = findViewById(R.id.diffmsl_p5);
        b1 = findViewById(R.id.diffmsl_btn);
        a1 = findViewById(R.id.diffmsl_ans1);
        a2 = findViewById(R.id.diffmsl_ans2);
        fig = findViewById(R.id.diffmsl_img);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fig4);
        fig.setImageBitmap(bmp);
        
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double w, t, h, d, er;
                String t1, t2;
                try {
                    w = Double.parseDouble(s1.getText().toString());
                    t = Double.parseDouble(s2.getText().toString());
                    h = Double.parseDouble(s3.getText().toString());
                    d = Double.parseDouble(s4.getText().toString());
                    er = Double.parseDouble(s5.getText().toString());
                } catch(NumberFormatException e) {
                    w = 0;
                    t = 0;
                    h = 0;
                    d = 0;
                    er = 0;
                }
                double z[] = Linecalc.diff_msl(w, t, h, d, er);
                if(z[0] <= 0) {
                    t1 = "NaN";
                    t2 = "NaN";
                } else {
                    t1 = String.format("%.3f", z[0]);
                    t2 = String.format("%.3f", z[1]);
                }
                a1.setText("Zo [ohm] = "+t1+",   Zdiff = Zo*2");
                a2.setText("Ze [ohm] = "+t2+",   Zcom = Ze/2");
            }
        });
    }
}
