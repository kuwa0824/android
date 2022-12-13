package io.github.kuwa0824.pll;

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
    private ImageView gFig;
    private Bitmap bmp;
    private EditText sVcoFreq, sVcoKv, sVcoNoise;
    private EditText sPllFpfd, sPllIcp, sPllNoise;
    private EditText sFiltR1, sFiltC1, sFiltC2;
    private Spinner o1, o2, o3, o4, o5, o6;
    private Button bCalc, bSave;
    private GView gView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gFig = findViewById(R.id.grf_fig);
        sVcoFreq = findViewById(R.id.p_vcofreq);
        sVcoKv = findViewById(R.id.p_vcokv);
        sVcoNoise = findViewById(R.id.p_vcopn);
        sPllFpfd = findViewById(R.id.p_pllfreq);
        sPllIcp = findViewById(R.id.p_pllicp);
        sPllNoise = findViewById(R.id.p_pllnoise);
        sFiltR1 = findViewById(R.id.p_filtr1);
        sFiltC1 = findViewById(R.id.p_filtc1);
        sFiltC2 = findViewById(R.id.p_filtc2);
        bCalc = findViewById(R.id.btn_calc);
        gView = findViewById(R.id.grf_view);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.fig);
        gFig.setImageBitmap(bmp);
        o1 = findViewById(R.id.osel_vcofreq);
        o2 = findViewById(R.id.osel_vcokv);
        o3 = findViewById(R.id.osel_pllfreq);
        o4 = findViewById(R.id.osel_r1);
        o5 = findViewById(R.id.osel_c1);
        o6 = findViewById(R.id.osel_c2);
        o1.setSelection(0);
        o2.setSelection(0);
        o3.setSelection(0);
        o4.setSelection(1);
        o5.setSelection(1);
        o6.setSelection(1);
        checkPermission();
        
        bCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p1, p2, p3, p4, p5, p6, p7, p8, p9;
                int pA, pB, pC, pD, pE, pF;
                p1 = sVcoFreq.getText().toString();
                p2 = sVcoKv.getText().toString();
                p3 = sVcoNoise.getText().toString();
                p4 = sPllFpfd.getText().toString();
                p5 = sPllIcp.getText().toString();
                p6 = sPllNoise.getText().toString();
                p7 = sFiltR1.getText().toString();
                p8 = sFiltC1.getText().toString();
                p9 = sFiltC2.getText().toString();
                pA = o1.getSelectedItemPosition();
                pB = o2.getSelectedItemPosition();
                pC = o3.getSelectedItemPosition();
                pD = o4.getSelectedItemPosition();
                pE = o5.getSelectedItemPosition();
                pF = o6.getSelectedItemPosition();
                if(!gView.startCalc(p1, p2, p3, p4, p5, p6, p7, p8, p9, pA, pB, pC, pD, pE, pF)) {
                    (Toast.makeText(getApplicationContext(), "wrong parameter", Toast.LENGTH_SHORT)).show();
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
        bSave = findViewById(R.id.btn_save);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap img = getViewBitmap(gView);
                try {
                    File dir= new File(Environment.getExternalStorageDirectory().getPath() + "/pictures/PLL");
                    if(!dir.isDirectory()) {
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
                    Toast.makeText(getApplicationContext(), "save in pictures/PLL/save.png", Toast.LENGTH_SHORT).show();
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

