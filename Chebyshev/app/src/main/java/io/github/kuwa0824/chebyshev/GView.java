package io.github.kuwa0824.chebyshev;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.*;
import android.view.*;
import android.widget.*;

public class GView extends View
{
    Paint paint;
    float scale;
    int height = 40;
    Chebyshev filt = new Chebyshev(0, 1);
    
    public GView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.scale = getContext().getResources().getDisplayMetrics().density;
        paint = new Paint();
    }

    public void startCalc(int type, int stg, double f1, double f2, double f3, double rpl, double z0, double g1, double g2) {
        filt = new Chebyshev(stg, rpl);
        if(type % 3 == 0) {
            filt.lpf(f1, z0, type/3);
            filt.lpf_frqres(g1, g2, 131);
        } else if(type % 3 == 1) {
            filt.hpf(f1, z0, type/3);
            filt.hpf_frqres(g1, g2, 131);
        } else {
            filt.bpf(f2, f3, z0, type/3);
            filt.bpf_frqres(g1, g2, 131);
        }
        height = filt.order * 25 + 380;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.scale(this.scale, this.scale);
        canvas.drawColor(Color.argb(200,245,245,255));
        paint.setColor(Color.argb(255,0,0,0));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(18);
        switch(filt.func_type) {
            case 0:
                canvas.drawText("LPF, \u03c0-layout", 20, 40, paint);
                break;
            case 1:
                canvas.drawText("HPF, \u03c0-layout", 20, 40, paint);
                break;
            case 2:
                canvas.drawText("BPF, \u03c0-layout", 20, 40, paint);
                break;
            case 3:
                canvas.drawText("LPF, T-layout", 20, 40, paint);
                break;
            case 4:
                canvas.drawText("HPF, T-layout", 20, 40, paint);
                break;
            case 5:
                canvas.drawText("BPF, T-layout", 20, 40, paint);
                break;
            default:
                return;
        }
        if(filt.func_type % 3 != 2) {
            canvas.drawText(String.format("fc = %.3f MHz", filt.f1_MHz), 20, 65, paint);
        } else {
            canvas.drawText(String.format("fpass = %.3f - %.3f MHz", filt.f1_MHz, filt.f2_MHz), 20, 65, paint);    
        }
        canvas.drawText(String.format("ripple = %.3f dB", filt.rpl_dB), 20, 90, paint);
        if(filt.order > 1) {
            for(int i=0; i<filt.order; i++) {
                canvas.drawText(filt.getLCstr(i), 20, 25*i+120, paint);
            }
        }
        int vofs = 40;
        int hofs = filt.order * 25 + 125;
        paint.setTextSize(12);
        canvas.drawText("0",vofs-16,hofs+5,paint);
        canvas.drawText("[dB]",vofs-32,hofs+110,paint);
        canvas.drawText("-35",vofs-28,hofs+215,paint);
        canvas.drawText(String.format("%.0f",filt.fr_start),vofs-5,hofs+230,paint);
        canvas.drawText("[MHz]",vofs+120,hofs+230,paint);
        canvas.drawText(String.format("%.0f",filt.fr_stop),vofs+245,hofs+230,paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.argb(255,180,180,180));
        for(int i=1; i<10; i++) {
            canvas.drawLine(vofs+26*i,hofs,vofs+26*i,hofs+210,paint);
        }
        for(int i=1; i<7; i++) {
            canvas.drawLine(vofs,hofs+30*i,vofs+260,hofs+30*i,paint);
        }
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(255,100,100,100));
        canvas.drawRect(vofs,hofs,vofs+260,hofs+210,paint);
        if(filt.fr_point > 0) {
            paint.setColor(Color.argb(255,255,100,100));
            double pf = -filt.s11[0] / 35 * 210;
            int p1 = (int)pf;
            p1 = (p1 < 0)? 0 : (p1 > 210)? 210 : p1;
            int p2 = p1;
            for(int i=1; i<131; i++) {
                pf = -filt.s11[i] / 35 * 210;
                p1 = (int)pf;
                p1 = (p1 < 0)? 0 : (p1 > 210)? 210 : p1;
                canvas.drawLine(vofs+(i-1)*2,hofs+p2,vofs+i*2,hofs+p1,paint);
                p2 = p1;
            }
            paint.setColor(Color.argb(255,100,100,255));
            pf = -filt.s12[0] / 35 * 210;
            p1 = (int)pf;
            p1 = (p1 < 0)? 0 : (p1 > 210)? 210 : p1;
            p2 = p1;
            for(int i=1; i<131; i++) {
                pf = -filt.s12[i] / 35 * 210;
                p1 = (int)pf;
                p1 = (p1 < 0)? 0 : (p1 > 210)? 210 : p1;
                canvas.drawLine(vofs+(i-1)*2,hofs+p2,vofs+i*2,hofs+p1,paint);
                p2 = p1;
            }
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Float w, h;
        w = scale * 320;
        h = scale * this.height;
        setMeasuredDimension(w.intValue(), h.intValue());
    }

}
