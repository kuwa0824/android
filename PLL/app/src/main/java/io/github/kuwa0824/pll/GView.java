package io.github.kuwa0824.pll;

import android.content.Context;
import android.util.AttributeSet;
import android.graphics.*;
import android.view.*;
import android.widget.*;

public class GView extends View
{
    float scale;
    int height = 840;
    Paint paint;
    PhaseNoise sim;
    
    public GView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.scale = getContext().getResources().getDisplayMetrics().density;
        paint = new Paint();
        sim = new PhaseNoise();
        sim.clearParam();
    }

    public boolean startCalc(String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8, String p9, int pA, int pB, int pC, int pD, int pE, int pF) {
        boolean b1 = sim.setVcoParam(p1, pA, p2, pB, p3);
        boolean b2 = sim.setPllParam(p4, pC, p5, p6);
        boolean b3 = sim.setFiltParam(p7, pD, p8, pE, p9, pF);
        if(b1 & b2 & b3) {
            sim.calc();
            invalidate();
            requestLayout();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.scale(this.scale, this.scale);
        canvas.drawColor(Color.argb(200,245,245,255));
        paint.setAntiAlias(true);
        drawComment(canvas, 40, 30);
        drawGrid(canvas, 55, 230);
        drawInfo1(canvas, 55, 230);
        drawPlot1(canvas, 55, 230);
        drawGrid(canvas, 55, 550);
        drawInfo2(canvas, 55, 550);
        drawPlot2(canvas, 55, 550);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Float w, h;
        w = scale * 380;
        h = scale * this.height;
        setMeasuredDimension(w.intValue(), h.intValue());
    }

    private void drawGrid(Canvas canvas, float xofs, float yofs) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255,150,150,150));
        paint.setStrokeWidth(0.5f);
        float g;
        for(int i=0; i<7; i++) {
            for(int j=1; j<10; j++) {
                g = (float)(40.0 * (i + Math.log10(j)));
                canvas.drawLine(xofs+g, yofs, xofs+g, yofs+240, paint);
            }
        }
        for(int i=1; i<12; i++) {
            canvas.drawLine(xofs, yofs+i*20, xofs+280, yofs+i*20, paint);
        }
        paint.setColor(Color.argb(255,0,0,0));
        paint.setStrokeWidth(3);
        canvas.drawRect(xofs, yofs, xofs+280, yofs+240, paint);
    }

    private void drawComment(Canvas canvas, float xofs, float yofs) {
        if(!sim.checkParam()) {
            return;
        }
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.argb(255,0,0,0));
        paint.setStrokeWidth(0);
        paint.setTextSize(14);
        for(int i=0; i<9; i++) {
            canvas.drawText(sim.StrParams[i], xofs, yofs+18*i, paint);
        }
    }

    private void drawInfo1(Canvas canvas, float xofs, float yofs) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255,0,0,0));
        paint.setStrokeWidth(1);
        canvas.drawLine(xofs, yofs+120, xofs+280, yofs+120, paint);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.argb(255,0,0,0));
        paint.setStrokeWidth(0);
        paint.setTextSize(14);
        canvas.drawText("Open Loop Gain/Phase", xofs+60, yofs-16, paint);
        paint.setTextSize(12);
        canvas.drawText("[dB]", xofs-15, yofs-10, paint);
        canvas.drawText("60", xofs-21, yofs+10, paint);
        canvas.drawText("40", xofs-21, yofs+45, paint);
        canvas.drawText("20", xofs-21, yofs+85, paint);
        canvas.drawText("0", xofs-16, yofs+125, paint);
        canvas.drawText("-20", xofs-24, yofs+165, paint);
        canvas.drawText("-40", xofs-24, yofs+205, paint);
        canvas.drawText("-60", xofs-24, yofs+240, paint);
        canvas.drawText("[deg]", xofs+270, yofs-10, paint);
        canvas.drawText("90", xofs+287, yofs+10, paint);
        canvas.drawText("60", xofs+287, yofs+45, paint);
        canvas.drawText("30", xofs+287, yofs+85, paint);
        canvas.drawText("0", xofs+287, yofs+125, paint);
        canvas.drawText("-30", xofs+287, yofs+165, paint);
        canvas.drawText("-60", xofs+287, yofs+205, paint);
        canvas.drawText("-90", xofs+287, yofs+240, paint);
        canvas.drawText("10", xofs-5, yofs+255, paint);
        canvas.drawText("100", xofs+30, yofs+255, paint);
        canvas.drawText("1k", xofs+70, yofs+255, paint);
        canvas.drawText("10k", xofs+110, yofs+255, paint);
        canvas.drawText("100k", xofs+145, yofs+255, paint);
        canvas.drawText("1M", xofs+190, yofs+255, paint);
        canvas.drawText("10M", xofs+230, yofs+255, paint);
        canvas.drawText("100M [Hz]", xofs+260, yofs+255, paint);
    }

    private void drawInfo2(Canvas canvas, float xofs, float yofs) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.argb(255,0,0,0));
        paint.setStrokeWidth(0);
        paint.setTextSize(14);
        canvas.drawText("SSB Phase Noise", xofs+80, yofs-16, paint);
        paint.setTextSize(12);
        canvas.drawText("[dBc/Hz]", xofs-25, yofs-10, paint);
        canvas.drawText("-40", xofs-24, yofs+10, paint);
        canvas.drawText("-60", xofs-24, yofs+45, paint);
        canvas.drawText("-80", xofs-24, yofs+85, paint);
        canvas.drawText("-100", xofs-30, yofs+125, paint);
        canvas.drawText("-120", xofs-30, yofs+165, paint);
        canvas.drawText("-140", xofs-30, yofs+205, paint);
        canvas.drawText("-160", xofs-30, yofs+240, paint);
        canvas.drawText("10", xofs-5, yofs+255, paint);
        canvas.drawText("100", xofs+30, yofs+255, paint);
        canvas.drawText("1k", xofs+70, yofs+255, paint);
        canvas.drawText("10k", xofs+110, yofs+255, paint);
        canvas.drawText("100k", xofs+145, yofs+255, paint);
        canvas.drawText("1M", xofs+190, yofs+255, paint);
        canvas.drawText("10M", xofs+230, yofs+255, paint);
        canvas.drawText("100M [Hz]", xofs+260, yofs+255, paint);
    }

    private void drawPlot1(Canvas canvas, float xofs, float yofs) {
        if(sim.VcoFreq <= 0) {
            return;
        }
        float p1, p2;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(255,255,0,0));
        p1 = (float)((60.0 - sim.OpenLoop[0].dB()) * 2.0);
        p1 = (p1 < 0)? 0 : (p1 > 240)? 240 : p1;
        for(int i=1; i<141; i++) {
            p2 = (float)((60.0 - sim.OpenLoop[i].dB()) * 2.0);
            p2 = (p2 < 0)? 0 : (p2 > 240)? 240 : p2;
            if(p1 > 0 && p1 < 240 && p2 > 0 && p2 < 240) {
                canvas.drawLine(xofs+(i-1)*2, yofs+p1, xofs+i*2, yofs+p2, paint);
            }
            p1 = p2;
        }
        canvas.drawLine(xofs+20, yofs+196, xofs+40, yofs+196, paint);
        paint.setColor(Color.argb(255,0,0,255));
        p1 = (float)((90.0 - sim.OpenLoop[0].phase()) / 0.75);
        p1 = (p1 < 0)? 0 : (p1 > 240)? 240 : p1;
        for(int i=1; i<141; i++) {
            p2 = (float)((90.0 - sim.OpenLoop[i].phase()) / 0.75);
            p2 = (p2 < 0)? 0 : (p2 > 240)? 240 : p2;
            if(p1 > 0 && p1 < 240 && p2 > 0 && p2 < 240) {
                canvas.drawLine(xofs+(i-1)*2, yofs+p1, xofs+i*2, yofs+p2, paint);
            }
            p1 = p2;
        }
        canvas.drawLine(xofs+20, yofs+212, xofs+40, yofs+212, paint);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.argb(255,0,0,0));
        paint.setStrokeWidth(0);
        paint.setTextSize(12);
        canvas.drawText("Gain", xofs+45, yofs+200, paint);
        canvas.drawText("Phase", xofs+45, yofs+216, paint);
    }

    private void drawPlot2(Canvas canvas, float xofs, float yofs) {
        if(sim.VcoFreq <= 0) {
            return;
        }
        float p1, p2;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.argb(255,150,150,150));
        p1 = (float)((-40.0 - sim.NoiseVcoOpenLoop[0]) * 2.0);
        p1 = (p1 < 0)? 0 : (p1 > 240)? 240 : p1;
        for(int i=1; i<141; i++) {
            p2 = (float)((-40.0 - sim.NoiseVcoOpenLoop[i]) * 2.0);
            p2 = (p2 < 0)? 0 : (p2 > 240)? 240 : p2;
            if(p1 > 0 && p1 < 240 && p2 > 0 && p2 < 240) {
                canvas.drawLine(xofs+(i-1)*2, yofs+p1, xofs+i*2, yofs+p2, paint);
            }
            p1 = p2;
        }
        canvas.drawLine(xofs+160, yofs+13, xofs+180, yofs+13, paint);
        paint.setColor(Color.argb(255,255,200,0));
        p1 = (float)((-40.0 - sim.NoiseVco[0]) * 2.0);
        p1 = (p1 < 0)? 0 : (p1 > 240)? 240 : p1;
        for(int i=1; i<141; i++) {
            p2 = (float)((-40.0 - sim.NoiseVco[i]) * 2.0);
            p2 = (p2 < 0)? 0 : (p2 > 240)? 240 : p2;
            if(p1 > 0 && p1 < 240 && p2 > 0 && p2 < 240) {
                canvas.drawLine(xofs+(i-1)*2, yofs+p1, xofs+i*2, yofs+p2, paint);
            }
            p1 = p2;
        }
        canvas.drawLine(xofs+160, yofs+27, xofs+180, yofs+27, paint);
        paint.setColor(Color.argb(255,0,200,0));
        p1 = (float)((-40.0 - sim.NoisePll[0]) * 2.0);
        p1 = (p1 < 0)? 0 : (p1 > 240)? 240 : p1;
        for(int i=1; i<141; i++) {
            p2 = (float)((-40.0 - sim.NoisePll[i]) * 2.0);
            p2 = (p2 < 0)? 0 : (p2 > 240)? 240 : p2;
            if(p1 > 0 && p1 < 240 && p2 > 0 && p2 < 240) {
                canvas.drawLine(xofs+(i-1)*2, yofs+p1, xofs+i*2, yofs+p2, paint);
            }
            p1 = p2;
        }
        canvas.drawLine(xofs+160, yofs+41, xofs+180, yofs+41, paint);
        paint.setColor(Color.argb(255,0,0,255));
        p1 = (float)((-40.0 - sim.NoiseR1[0]) * 2.0);
        p1 = (p1 < 0)? 0 : (p1 > 240)? 240 : p1;
        for(int i=1; i<141; i++) {
            p2 = (float)((-40.0 - sim.NoiseR1[i]) * 2.0);
            p2 = (p2 < 0)? 0 : (p2 > 240)? 240 : p2;
            if(p1 > 0 && p1 < 240 && p2 > 0 && p2 < 240) {
                canvas.drawLine(xofs+(i-1)*2, yofs+p1, xofs+i*2, yofs+p2, paint);
            }
            p1 = p2;
        }
        canvas.drawLine(xofs+160, yofs+55, xofs+180, yofs+55, paint);
        paint.setStrokeWidth(3);
        paint.setColor(Color.argb(255,255,0,0));
        p1 = (float)((-40.0 - sim.NoiseAll[0]) * 2.0);
        p1 = (p1 < 0)? 0 : (p1 > 240)? 240 : p1;
        for(int i=1; i<141; i++) {
            p2 = (float)((-40.0 - sim.NoiseAll[i]) * 2.0);
            p2 = (p2 < 0)? 0 : (p2 > 240)? 240 : p2;
            if(p1 > 0 && p1 < 240 && p2 > 0 && p2 < 240) {
                canvas.drawLine(xofs+(i-1)*2, yofs+p1, xofs+i*2, yofs+p2, paint);
            }
            p1 = p2;
        }
        canvas.drawLine(xofs+160, yofs+69, xofs+180, yofs+69, paint);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.argb(255,0,0,0));
        paint.setStrokeWidth(0);
        paint.setTextSize(12);
        canvas.drawText("VCO Open Loop", xofs+185, yofs+16, paint);
        canvas.drawText("VCO Noise", xofs+185, yofs+30, paint);
        canvas.drawText("PFD Noise", xofs+185, yofs+44, paint);
        canvas.drawText("R1 Noise", xofs+185, yofs+58, paint);
        canvas.drawText("Total Noise", xofs+185, yofs+72, paint);
    }

}
