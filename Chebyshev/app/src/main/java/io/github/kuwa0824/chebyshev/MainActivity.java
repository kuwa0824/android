package io.github.kuwa0824.chebyshev;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.view.*;
import android.graphics.*;
import java.io.*;
import android.content.pm.*;
import java.security.acl.*;
import android.content.*;
import android.Manifest;
import android.provider.*;

public class MainActivity extends Activity 
{
    private Spinner sp1_func, sp2_type;
    private ImageView fig;
    private Bitmap[] bmp;
    private EditText p1_order, p2_f1, p3_f2, p4_f3, p5_rpl, p6_z0;
    private EditText g1_start, g2_stop;
    private TextView label_f1, label_f2, label_f3;
    private Button b1, b2;
    private GView v1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sp1_func = findViewById(R.id.funcsel_id);
        sp2_type = findViewById(R.id.typesel_id);
        fig = findViewById(R.id.img_id);
        p1_order = findViewById(R.id.param_order);
        p2_f1 = findViewById(R.id.param_f1);
        p3_f2 = findViewById(R.id.param_f2);
        p4_f3 = findViewById(R.id.param_f3);
        p5_rpl = findViewById(R.id.param_rpl);
        p6_z0 = findViewById(R.id.param_z0);
        label_f1 = findViewById(R.id.label_f1);
        label_f2 = findViewById(R.id.label_f2);
        label_f3 = findViewById(R.id.label_f3);
        g1_start = findViewById(R.id.param_g1);
        g2_stop = findViewById(R.id.param_g2);
        b1 = findViewById(R.id.btn_id);
        v1 = findViewById(R.id.grf_id);
        bmp = new Bitmap[6];
        bmp[0] = BitmapFactory.decodeResource(getResources(), R.drawable.pi_lpf);
        bmp[1] = BitmapFactory.decodeResource(getResources(), R.drawable.pi_hpf);
        bmp[2] = BitmapFactory.decodeResource(getResources(), R.drawable.pi_bpf);
        bmp[3] = BitmapFactory.decodeResource(getResources(), R.drawable.tee_lpf);
        bmp[4] = BitmapFactory.decodeResource(getResources(), R.drawable.tee_hpf);
        bmp[5] = BitmapFactory.decodeResource(getResources(), R.drawable.tee_bpf);
        fig.setImageBitmap(bmp[0]);
        checkPermission();
        
        sp1_func.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int pos, long id) {
                int i = sp2_type.getSelectedItemPosition();
                fig.setImageBitmap(bmp[i*3+pos]);
                if(pos == 2) {
                    p2_f1.setTextColor(Color.parseColor("#dddddd"));
                    p3_f2.setTextColor(Color.parseColor("#333333"));
                    p4_f3.setTextColor(Color.parseColor("#333333"));
                    label_f1.setTextColor(Color.parseColor("#dddddd"));
                    label_f2.setTextColor(Color.parseColor("#333333"));
                    label_f3.setTextColor(Color.parseColor("#333333"));
                } else {
                    p2_f1.setTextColor(Color.parseColor("#333333"));
                    p3_f2.setTextColor(Color.parseColor("#dddddd"));
                    p4_f3.setTextColor(Color.parseColor("#dddddd"));    
                    label_f1.setTextColor(Color.parseColor("#333333"));
                    label_f2.setTextColor(Color.parseColor("#dddddd"));
                    label_f3.setTextColor(Color.parseColor("#dddddd"));    
                }
            }
            @Override
            public void onNothingSelected(AdapterView parent) {
                //
            }
        });

        sp2_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int pos, long id) {
                int i = sp1_func.getSelectedItemPosition();
                fig.setImageBitmap(bmp[i+3*pos]);
            }
            @Override
            public void onNothingSelected(AdapterView parent) {
                //
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = sp2_type.getSelectedItemPosition();
                type *= 3;
                type += sp1_func.getSelectedItemPosition();
                try {
                    int order = Integer.parseInt(p1_order.getText().toString());
                    double f1 = Double.parseDouble(p2_f1.getText().toString());
                    double f2 = Double.parseDouble(p3_f2.getText().toString());
                    double f3 = Double.parseDouble(p4_f3.getText().toString());
                    double a0 = Double.parseDouble(p5_rpl.getText().toString());
                    double z0 = Double.parseDouble(p6_z0.getText().toString());
                    double g1 = Double.parseDouble(g1_start.getText().toString());
                    double g2 = Double.parseDouble(g2_stop.getText().toString());
                    boolean chk = true;
                    if(order < 2 || order > 20 || a0 <= 0 || z0 <= 0 || g1 < 0 || g2 <= g1) {
                        chk = false;
                    }
                    if(type % 3 == 2 && (f2 <= 0 || f3 <= f2)) {
                        chk = false;
                    }
                    if(type % 3 != 2 && f1 <= 0) {
                        chk = false;
                    }
                    if(chk) {
                        v1.startCalc(type, order, f1, f2, f3, a0, z0, g1, g2);
                    } else {
                        Toast.makeText(getApplicationContext(), "wrong parameter", Toast.LENGTH_SHORT).show();
                    }
                } catch(NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "wrong parameter", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpWriteExternalStorage();
            }
        }
    }
    
    public void checkPermission() {
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            setUpWriteExternalStorage();
        } else {
            if(shouldShowRequestPermissionRationale (Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            }
        }
    }
 
    private void setUpWriteExternalStorage() {
        b2 = findViewById(R.id.save_id);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap img = getViewBitmap(v1);
                if (img == null) {
                    return;
                }
                try {
                    File dir= new File(Environment.getExternalStorageDirectory().getPath() + "/pictures/Chebyshev");
                    if(!dir.isDirectory()) {
                        Toast.makeText(getApplicationContext(), "fail to save", Toast.LENGTH_SHORT).show();
                        dir.mkdir();
                    }
                    File file = new File(dir, "save.png");
                    FileOutputStream outStream = new FileOutputStream(file);
                    img.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.close();
                    ContentValues cv = new ContentValues();
                    cv.put(MediaStore.Images.Media.TITLE, "save.png");
                    cv.put(MediaStore.Images.Media.DISPLAY_NAME, "save.png");
                    cv.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                    cv.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    ContentResolver cr = getApplicationContext().getContentResolver();
                    cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                    Toast.makeText(getApplicationContext(), "save in pictures/Chebyshev/save.png", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Bitmap getViewBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        if(cache == null) { return null; }
        Bitmap bitmap = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}

