package io.github.kuwa0824.pll;

public class PhaseNoise {
    
    public double VcoFreq;
    public double VcoKv;
    public double VcoNoise;
    public double PllFreq;
    public double PllIcp;
    public double PllNoise;
    public double FiltR1;
    public double FiltC1;
    public double FiltC2;
    public double Temp;
    public double[] SimFreq;
    public Complex[] FiltFs;
    public Complex[] OpenLoop;
    public double[] NoiseVcoOpenLoop;
    public double[] NoiseVco;
    public double[] NoisePll;
    public double[] NoiseR1;
    public double[] NoiseAll;
    public String[] StrParams;
    
    public PhaseNoise() {
        Temp = 300.0;
        FiltFs = new Complex[141];
        OpenLoop = new Complex[141];
        NoiseVcoOpenLoop = new double[141];
        NoiseVco = new double[141];
        NoisePll = new double[141];
        NoiseR1 = new double[141];
        NoiseAll = new double[141];
        SimFreq = new double[141];
        for(int i=0; i<141; i++) {
            SimFreq[i] = Math.pow(10.0, i/20.0+1);
        }
        StrParams = new String[9];
        clearParam();
    }

    public void clearParam() {
        VcoFreq = 0;
    }
    
    public boolean checkParam() {
        if(VcoFreq > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean setVcoParam(String Fr, int FrOrder, String Kv, int KvOrder, String Ns) {
        try {
            VcoFreq = Double.parseDouble(Fr);
            VcoKv = Double.parseDouble(Kv);
            VcoNoise = Double.parseDouble(Ns);
        } catch(NumberFormatException e) {
            return false;
        }
        if(VcoFreq <= 0 || VcoKv <= 0 || VcoNoise > 0) {
            clearParam();
            return false;
        }
        StringBuffer buf = new StringBuffer();
        buf.append("VCO Freq. = ");
        buf.append(Fr);
        if(FrOrder == 0) {
            VcoFreq *= 1.0E+9;
            buf.append(" GHz");
        } else {
            VcoFreq *= 1.0E+6;
            buf.append(" MHz");    
        }
        StrParams[0] = buf.toString();
        buf.setLength(0);
        buf.append("VCO Kv = ");
        buf.append(Kv);
        if(KvOrder == 0) {
            VcoKv *= 1.0E+6;
            buf.append(" MHz/V");
        } else {
            VcoKv *= 1.0E+3;
            buf.append(" kHz/V");    
        }
        StrParams[1] = buf.toString();
        buf.setLength(0);
        buf.append("VCO Phase Noise @100k ofs = ");
        buf.append(Ns);
        buf.append(" dBc/Hz");
        StrParams[2] = buf.toString();
        return true;
    }
    
    public boolean setPllParam(String Fr, int FrOrder, String Ic, String Ns) {
        try {
            PllFreq = Double.parseDouble(Fr);
            PllIcp = Double.parseDouble(Ic) * 1.0E-3;
            PllNoise = Double.parseDouble(Ns);
        } catch(NumberFormatException e) {
            return false;
        }
        if(PllFreq <= 0 || PllIcp <= 0 || PllNoise > 0) {
            clearParam();
            return false;
        }
        StringBuffer buf = new StringBuffer();
        buf.append("PFD Input Freq. = ");
        buf.append(Fr);
        if(FrOrder == 0) {
            PllFreq *= 1.0E+6;
            buf.append(" MHz");
        } else {
            PllFreq *= 1.0E+3;
            buf.append(" kHz");    
        }
        StrParams[3] = buf.toString();
        buf.setLength(0);
        buf.append("Charge Pump Current = ");
        buf.append(Ic);
        buf.append(" mA");
        StrParams[4] = buf.toString();
        buf.setLength(0);
        buf.append("Normalized Phase Noise Floor = ");
        buf.append(Ns);
        buf.append(" dBc/Hz");
        StrParams[5] = buf.toString();
        return true;
    }
    
    public boolean setFiltParam(String R1, int R1Order, String C1, int C1Order, String C2, int C2Order) {
        try {
            FiltR1 = Double.parseDouble(R1);
            FiltC1 = Double.parseDouble(C1);
            FiltC2 = Double.parseDouble(C2);
        } catch(NumberFormatException e) {
            return false;
        }
        if(FiltR1 <= 0 || FiltC1 <= 0 || FiltC2 <= 0) {
            clearParam();
            return false;
        }
        StringBuffer buf = new StringBuffer();
        buf.append("R1 = ");
        buf.append(R1);
        if(R1Order == 0) {
            FiltR1 *= 1.0E+3;
            buf.append(" k\u03A9");
        } else {
            buf.append(" \u03A9");    
        }
        StrParams[6] = buf.toString();
        buf.setLength(0);
        buf.append("C1 = ");
        buf.append(C1);
        if(C1Order == 0) {
            FiltC1 *= 1.0E-6;
            buf.append(" uF");
        } else if(C1Order == 1) {
            FiltC1 *= 1.0E-9;
            buf.append(" nF");
        } else {
            FiltC1 *= 1.0E-12;
            buf.append(" pF");
        }
        StrParams[7] = buf.toString();
        buf.setLength(0);
        buf.append("C2 = ");
        buf.append(C2);
        if(C2Order == 0) {
            FiltC2 *= 1.0E-6;
            buf.append(" uF");
        } else if(C2Order == 1) {
            FiltC2 *= 1.0E-9;
            buf.append(" nF");
        } else {
            FiltC2 *= 1.0E-12;
            buf.append(" pF");
        }
        StrParams[8] = buf.toString();
        return true;
    }
    
    public void calc() {
        calcNoiseVcoOpenLoop();
        for(int i=0; i<141; i++) {
            FiltFs[i] = calcFiltFs(SimFreq[i]);
            OpenLoop[i] = calcOpenLoop(SimFreq[i], FiltFs[i]);
            NoiseVco[i] = calcNoiseVco(OpenLoop[i], NoiseVcoOpenLoop[i]);
            NoisePll[i] = calcNoisePll(SimFreq[i], OpenLoop[i], FiltFs[i]);
            NoiseR1[i] = calcNoiseR1(SimFreq[i], FiltFs[i]);
            NoiseAll[i] = 10.0 * Math.log10(Math.pow(10.0, NoiseVco[i] / 10.0) + Math.pow(10.0, NoisePll[i] / 10.0) + Math.pow(10.0, NoiseR1[i] / 10.0));
        }
    }

    private Complex calcFiltFs(double freq) {
        Complex c1, c2;
        c1 = new Complex(1.0, angFreq(freq) * FiltC1 * FiltR1);
        c2 = new Complex(FiltC1, 0.0);
        c1 = c2.div(c1);
        c2 = new Complex(FiltC2, 0.0);
        c2 = c1.add(c2);
        c1 = new Complex(0.0, angFreq(freq));
        c2 = c1.mul(c2);
        c1 = new Complex(1.0, 0.0);
        return c1.div(c2);
    }

    private Complex calcOpenLoop(double freq, Complex fs) {
        Complex x = new Complex(0.0, VcoKv * PllIcp * PllFreq / VcoFreq / angFreq(freq));
        return fs.mul(x);
    }
    
    private void calcNoiseVcoOpenLoop() {
        double n1, n2;
        for(int i=0; i<141; i++) {
            n1 = VcoNoise + (80-i)*1.0;
            n2 = VcoNoise + (80-i)*1.5 - 5.0;
            NoiseVcoOpenLoop[i] = 10.0 * Math.log10(Math.pow(10.0, n1/10.0) + Math.pow(10.0, n2/10.0)) - 1.2;
        }
    }

    private double calcNoiseVco(Complex oloop, double vcopn) {
        Complex c1 = new Complex(1.0, 0);
        c1 = c1.sub(oloop);
        double x = c1.abs();
        x = Math.pow(10.0, vcopn/20.0) / x;
        return 20.0 * Math.log10(x);
    }

    private double calcNoisePll(double freq, Complex oloop, Complex fs) {
        Complex c1 = new Complex(1.0, 0);
        c1 = c1.sub(oloop);
        c1 = fs.div(c1);
        double x = Math.pow(10.0, PllNoise / 20.0) * VcoKv * Math.sqrt(PllFreq) * PllIcp / angFreq(freq);
        x *= c1.abs();
        return 20.0 * Math.log10(x);
    }

    private double calcNoiseR1(double freq, Complex fs) {
        Complex c1 = new Complex(1.0, 0);
        c1 = c1.div(fs);
        Complex c2 = new Complex(0.0, -VcoKv * PllIcp * PllFreq / VcoFreq / angFreq(freq));
        c1 = c1.add(c2);
        c2 = new Complex(FiltR1, -0.5 / Math.PI / freq / FiltC1);
        c1 = c1.mul(c2);
        double x = Math.sqrt(Temp * FiltR1 * 5.52E-23);
        x /= c1.abs();
        x *= VcoKv / freq / Math.sqrt(2);
        return 20.0 * Math.log10(x);
    }

    private double angFreq(double freq) {
        return 2.0 * Math.PI * freq;
    }

}

