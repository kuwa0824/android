package io.github.kuwa0824.waveguide;

import android.app.*;
import android.content.res.AssetManager;
import android.os.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ImageView;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity 
{
    private Spinner s1, s2;
    private ImageView fig;
    private final String[] files = {
        "WR-187_CPR-187G.png", "WR-187_CMR-187.png", "WR-159_CPR-159G.png", "WR-159_CMR-159.png",
        "WR-137_CPR-137G.png", "WR-137_CMR-137.png", "WR-112_UBR-84.png", "WR-90_UBR-100.png", 
        "WR-75_UBR-120.png", "WR-62_UBR-140.png", "WR-51_UBR-180.png", "WR-42_UBR-220.png",
        "WR-34_UBR-260.png", "WR-28_UBR-320.png", "WR-22_UG-383.png", "WR-19_UG-383.png",
        "WR-15_UG-385.png", "WR-12_UG-387.png", "WR-10_UG-387.png", "WR-8_UG-387.png",
        "WR-6_UG-387.png", "WR-5_UG-387.png", "WR-4_UG-387.png", "WR-3_UG-387.png"
    };
    private Integer ofs = 0;
    private Integer idx = 0;
    private Bitmap img;
    private final String[] freq_band = {
        "C-band (4-8GHz)", "X-band (8-12GHz)", "Ku-band (12-18GHz)", "K-band (18-26GHz)",
        "Ka-band (26-40GHz)", "V-band (40-75GHz)", "W-band (75-110GHz)", "Over 100GHz"
    };
    private final String[] waveguide_all = {
        "WR-187 (4.0-5.9GHz) + CPR-187G choke flange",
        "WR-187 (4.0-5.9GHz) + CMR-187 flange",
        "WR-159 (5.0-7.0GHz) + CPR-159G choke flange",
        "WR-159 (5.0-7.0GHz) + CMR-159 flange",
        "WR-137 (5.85-8.2GHz) + CPR-137G choke flange",
        "WR-137 (5.85-8.2GHz) + CMR-137 flange",
        "WR-112 (7.1-10.0GHz) + UBR-84/UG-138 flange",
        "WR-90 (8.5-12.4GHz) + UBR-100/UG-135 flange",
        "WR-75 (10.0-15.0GHz) + UBR-120 flange",
        "WR-62 (12.4-18.0GHz) + UBR-140/UG-419 flange",
        "WR-51 (15.0-22.0GHz) + UBR-180 flange",
        "WR-42 (18.0-26.5GHz) + UBR-220/UG-595 flange",
        "WR-34 (22.0-33.0GHz) + UBR-260 flange",
        "WR-28 (26.5-40.0GHz) + UBR-320 flange",
        "WR-22 (33.0-50.0GHz) + UG-383 flange",
        "WR-19 (40.0-60.0GHz) + UG-383 flange",
        "WR-15 (50.0-75.0GHz) + UG-385 flange",
        "WR-12 (60.0-90.0GHz) + UG-387 flange",
        "WR-10 (75.0-110.0GHz) + UG-387 flange",
        "WR-8 (90.0-140.0GHz) + UG-387 flange",
        "WR-6 (110.0-170.0GHz) + UG-387 flange",
        "WR-5 (140.0-220.0GHz) + UG-387 flange",
        "WR-4 (172.0-260.0GHz) + UG-387 flange",
        "WR-3 (220.0-325.0GHz) + UG-387 flange"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        s1 = findViewById(R.id.spin1);
        s2 = findViewById(R.id.spin2);
        ArrayAdapter<String> adap1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, freq_band);
        adap1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adap1);
        fig = findViewById(R.id.img);
        img = getBitmapFromAsset(files[idx]);
        fig.setImageBitmap(img);

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> av, View v, int pos, long id) {
                Integer sel = av.getSelectedItemPosition();
                Integer len = 0;
                switch(sel) {
                    case 0:
                        ofs = 0;
                        len = 7;
                        break;
                    case 1:
                        ofs = 4;
                        len = 5;
                        break;
                    case 2:
                        ofs = 7;
                        len = 5;
                        break;
                    case 3:
                        ofs = 9;
                        len = 4;
                        break;
                    case 4:
                        ofs = 11;
                        len = 5;
                        break;
                    case 5:
                        ofs = 13;
                        len = 6;
                        break;
                    case 6:
                        ofs = 16;
                        len = 5;
                        break;
                    case 7:
                        ofs = 18;
                        len = 6;
                }
                setSpin2Item(len);
            }
            public void onNothingSelected(AdapterView<?> av) {
            }
        });

        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> av, View v, int pos, long id) {
                idx = av.getSelectedItemPosition();
                fig.setImageDrawable(null);
                if (!img.isRecycled()) {
                    img.recycle();
                }
                img = null;
                img = getBitmapFromAsset(files[ofs+idx]);
                fig.setImageBitmap(img);
            }
            public void onNothingSelected(AdapterView<?> av) {
            }
        });
    }

    private void setSpin2Item(Integer len)
    {
        String[] waveguide = new String[len];
        for (int i = 0; i < len; i++) {
            waveguide[i] = waveguide_all[ofs+i];
        }
        ArrayAdapter<String> adap2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, waveguide);
        adap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(adap2);
    }

    private Bitmap getBitmapFromAsset(String strName)
    {
        AssetManager assetMgr = getAssets();
        InputStream istr = null;
        try {
            istr = assetMgr.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap img = BitmapFactory.decodeStream(istr);
        return img;
    }
}
