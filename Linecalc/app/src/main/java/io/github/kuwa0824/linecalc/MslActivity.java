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

public class MslActivity extends Activity 
{
    private EditText s1, s2, s3, s4;
    private Button b1;
    private TextView a1;
    private ImageView fig;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msl);
        s1 = findViewById(R.id.msl_p1);
        s2 = findViewById(R.id.msl_p2);
        s3 = findViewById(R.id.msl_p3);
        s4 = findViewById(R.id.msl_p4);
        b1 = findViewById(R.id.msl_btn);
        a1 = findViewById(R.id.msl_ans);
        fig = findViewById(R.id.msl_img);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fig1);
        fig.setImageBitmap(bmp);
        
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double w, t, h, er;
                String s;
                try {
                    w = Double.parseDouble(s1.getText().toString());
                    t = Double.parseDouble(s2.getText().toString());
                    h = Double.parseDouble(s3.getText().toString());
                    er = Double.parseDouble(s4.getText().toString());
                } catch(NumberFormatException e) {
                    w = 0;
                    t = 0;
                    h = 0;
                    er = 0;
                }
                double z = Linecalc.msl(w, t, h, er);
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
