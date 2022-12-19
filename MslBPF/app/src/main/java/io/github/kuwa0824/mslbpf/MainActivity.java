package io.github.kuwa0824.mslbpf;

import android.app.*;
import android.os.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.*;

public class MainActivity extends Activity 
{
    private EditText sH, sT, sEr, sOrder, sFc, sBw, sRpl;
    private Button btn;
    private TextView tbl1, tbl2, tbl3, tbl4;
    private ImageView fig;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sH = findViewById(R.id.param_H);
        sT = findViewById(R.id.param_T);
        sEr = findViewById(R.id.param_Er);
        sOrder = findViewById(R.id.param_order);
        sFc = findViewById(R.id.param_fc);
        sBw = findViewById(R.id.param_bw);
        sRpl = findViewById(R.id.param_rpl);
        btn = findViewById(R.id.btn_id);
        tbl1 = findViewById(R.id.tbl1_id);
        tbl2 = findViewById(R.id.tbl2_id);
        tbl3 = findViewById(R.id.tbl3_id);
        tbl4 = findViewById(R.id.tbl4_id);
        fig = findViewById(R.id.fig_id);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fig);
        fig.setImageBitmap(bmp);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int order;
                double H, T, Er, fc, bw, rpl;
                boolean chk = true;
                try {
                    H = Double.parseDouble(sH.getText().toString());
                    T = Double.parseDouble(sT.getText().toString());
                    Er = Double.parseDouble(sEr.getText().toString());
                    order = Integer.parseInt(sOrder.getText().toString());
                    fc = Double.parseDouble(sFc.getText().toString());
                    bw = Double.parseDouble(sBw.getText().toString());
                    rpl = Double.parseDouble(sRpl.getText().toString());
                } catch(NumberFormatException e) {
                    H = 0;
                    T = 0;
                    Er = 0;
                    order = 0;
                    fc = 0;
                    bw = 0;
                    rpl = 0;
                }
                if(H <= 0 || T <= 0 || Er <= 0 || order <= 1 || order > 20 || fc <= 0 || bw <= 0 || rpl <= 0) {
                    chk = false;
                }
                if(fc < bw*0.6) {
                    chk = false;
                }
                if(chk) {
                    MslBpf bpf = new MslBpf(H, T, Er, order, fc, bw, rpl);
                    bpf.calc();
                    StringBuffer buf1 = new StringBuffer();
                    StringBuffer buf2 = new StringBuffer();
                    StringBuffer buf3 = new StringBuffer();
                    StringBuffer buf4 = new StringBuffer();
                    buf1.append("#\n");
                    buf2.append("W [mm]\n");
                    buf3.append("S [mm]\n");
                    buf4.append("L [mm]\n");
                    for(int i=0; i<bpf.order; i++) {
                        buf1.append(String.format("%d\n", i+1));
                        buf2.append(String.format("%.4f\n", bpf.Aw[i]));
                        buf3.append(String.format("%.4f\n", bpf.As[i]));
                        buf4.append(String.format("%.4f\n", bpf.Al[i]));
                    }
                    tbl1.setText(buf1.toString());
                    tbl2.setText(buf2.toString());
                    tbl3.setText(buf3.toString());
                    tbl4.setText(buf4.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong parameter", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
