package io.github.kuwa0824.pll;

import static java.security.AccessController.getContext;

import android.app.*;
import android.net.Uri;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.pm.*;
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
                if(img == null) {
                    Toast.makeText(getApplicationContext(), "fail to get image", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date md = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String filename = sdf.format(md) + ".png";
                ContentResolver resolv = getApplicationContext().getContentResolver();
                if (android.os.Build.VERSION.SDK_INT >= 29) {
                    ContentValues cv = new ContentValues();
                    cv.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                    cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                    cv.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                    cv.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PLL/");
                    cv.put(MediaStore.Images.Media.IS_PENDING, true);
                    Uri coll = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    Uri uri = resolv.insert(coll, cv);
                    try {
                        if (uri != null) {
                            OutputStream outStream = resolv.openOutputStream(uri);
                            saveImageToStream(img, outStream);
                            cv.put(MediaStore.Images.Media.IS_PENDING, false);
                            resolv.update(uri, cv, null, null);
                        } else {
                            Toast.makeText(getApplicationContext(), "fail to get uri", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "fail to save", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/PLL");
                    if (!dir.isDirectory()) {
                        dir.mkdir();
                    }
                    File imgfile = new File(dir, filename);
                    try {
                        if (imgfile != null) {
                            OutputStream outStream = new FileOutputStream(imgfile);
                            saveImageToStream(img, outStream);
                            ContentValues cv = new ContentValues();
                            cv.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                            cv.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                            cv.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                            cv.put(MediaStore.Images.Media.DATA, imgfile.getAbsolutePath());
                            resolv.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "fail to save", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(getApplicationContext(), "save in " + filename, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cb = new Canvas(bitmap);
        view.draw(cb);
        return bitmap;
    }

    public void saveImageToStream(Bitmap img, OutputStream outStream) throws IOException {
        if (outStream != null) {
            try {
                img.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new IOException();
        }
    }
}
