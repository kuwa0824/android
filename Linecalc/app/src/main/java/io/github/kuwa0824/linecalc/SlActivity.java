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

public class SlActivity extends Activity 
{
    private EditText s1, s2, s3, s4;
    private Button b1;
    private TextView a1;
    private ImageView fig;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sl);
        s1 = findViewById(R.id.sl_p1);
        s2 = findViewById(R.id.sl_p2);
        s3 = findViewById(R.id.sl_p3);
        s4 = findViewById(R.id.sl_p4);
        b1 = findViewById(R.id.sl_btn);
        a1 = findViewById(R.id.sl_ans);
        fig = findViewById(R.id.sl_img);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fig2);
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
                double z = Linecalc.sl_sym(w, t, 0.5*(h-t), er);
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
