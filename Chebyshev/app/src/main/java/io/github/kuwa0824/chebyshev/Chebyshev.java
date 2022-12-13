package io.github.kuwa0824.chebyshev;

public class Chebyshev {

    int func_type = -1;;
    int order;
    double rpl_dB;
    double f1_MHz;
    double f2_MHz;
    double[] Gk;
    double[] L;
    double[] C;
    int fr_point;
    double fr_start;
    double fr_stop;
    double[] s11;
    double[] s12;

    public Chebyshev(int p1_order, double p2_rpl) {
        int i;
        if(p1_order < 2 || p1_order > 20 || p2_rpl <= 0) {
            this.order = 0;
            return;
        }
        this.order = p1_order;
        this.rpl_dB = p2_rpl;
        this.Gk = new double[this.order];
        this.L = new double[this.order];
        this.C = new double[this.order];
        double b = Math.log(1 / Math.tanh(this.rpl_dB / 17.37));
        double c = Math.sinh(b/(this.order*2));
        double[] ak = new double[this.order];
        double[] bk = new double[this.order];
        for(i=0; i<this.order; i++) {
            ak[i] = Math.sin((2*i+1)*Math.PI/(2*this.order));
            bk[i] = Math.pow(c,2)+Math.pow(Math.sin((i+1)*Math.PI/this.order),2);
        }
        this.Gk[0] = 2*ak[0]/c;
        for(i=1; i<this.order; i++) {
            this.Gk[i] = 4*ak[i-1]*ak[i]/(bk[i-1]*this.Gk[i-1]);
        }
    }

    public String getLCstr(int i) {
        String sx = "";
        String sy = "";
        double x, y;
        x = L[i] * 1.0E+9;
        y = C[i] * 1.0E+12;
        if(x > 0) {
            if(x < 1) {
                sx = String.format("L%d = %.3f [nH]", i+1, x);
            } else if(x < 10) {
                sx = String.format("L%d = %.2f [nH]", i+1, x);    
            } else if(x < 100) {
                sx = String.format("L%d = %.1f [nH]", i+1, x);    
            } else {
                sx = String.format("L%d = %.0f [nH]", i+1, x);
            }
        }
        if(y > 0) {
            if(y < 1) {
                sy = String.format("C%d = %.3f [pF]", i+1, y);
            } else if(y < 10) {
                sy = String.format("C%d = %.2f [pF]", i+1, y);    
            } else if(y < 100) {
                sy = String.format("C%d = %.1f [pF]", i+1, y);    
            } else {
                sy = String.format("C%d = %.0f [pF]", i+1, y);
            }
        }
        if(x < 0) {
            return sy;
        } else if(y < 0) {
            return sx;
        } else {
            return sx + ", " + sy;
        }
    }
    
    // type=0 : PI, type=1 : TEE
    public void lpf(double fc, double z0, int type) {
        int i;
        this.func_type = type*3;
        if(fc <= 0 || z0 <= 0) {
            this.order = 0;
            return;
        }
        this.f1_MHz = fc;
        double x = 1.0 / (2.0E+6 * this.f1_MHz * Math.PI);
        type &= 1;
        for(i=0; i<this.order; i++) {
            this.L[i] = (i % 2 != type)? x*this.Gk[i]*z0 : -1.0;
            this.C[i] = (i % 2 == type)? x*this.Gk[i]/z0 : -1.0;
        }
    }

    public void hpf(double fc, double z0, int type) {
        int i;
        this.func_type = type*3+1;
        if(fc <= 0 || z0 <= 0) {
            this.order = 0;
            return;
        }
        this.f1_MHz = fc;
        double x = 1.0 / (2.0E+6 * this.f1_MHz * Math.PI);
        type &= 1;
        for(i=0; i<this.order; i++) {
            this.L[i] = (i % 2 == type)? x/this.Gk[i]*z0 : -1.0;
            this.C[i] = (i % 2 != type)? x/this.Gk[i]/z0 : -1.0;
        }
    }

    public void bpf(double f1, double f2, double z0, int type) {
        int i;
        this.func_type = type*3+2;
        if(f1 <= 0 || f2 <= f1 || z0 <= 0) {
            this.order = 0;
            return;
        }
        this.f1_MHz = f1;
        this.f2_MHz = f2;
        double w0 = Math.sqrt(f1*f2) * 2.0E+6 * Math.PI;
        double wr = (f2-f1) / Math.sqrt(f1*f2);
        type &= 1;
        for(i=0; i<this.order; i++) {
            if(i%2 != type) {
                this.L[i] = this.Gk[i]/w0/wr*z0;
                this.C[i] = wr/w0/this.Gk[i]/z0;
            } else {
                this.L[i] = wr/w0/this.Gk[i]*z0;
                this.C[i] = this.Gk[i]/w0/wr/z0;
            }
        }
    }

    public void lpf_frqres(double fmin_MHz, double fmax_MHz, int point) {
        double f, x;
        if(fmin_MHz < 0 || fmax_MHz <= fmin_MHz) {
            fr_point = 0;
            return;
        }
        this.fr_point = point;
        this.fr_start = fmin_MHz;
        this.fr_stop = fmax_MHz;
        this.s11 = new double[point];
        this.s12 = new double[point];
        for(int i=0; i<point; i++) {
            f = (fmax_MHz - fmin_MHz) / (point - 1) * i + fmin_MHz;
            if(f <= this.f1_MHz) {
                x = -10.0 * Math.log10(1 + (Math.pow(10.0, this.rpl_dB*0.1) - 1) * Math.pow(Math.cos(this.order * Math.acos(f/this.f1_MHz)), 2.0));
            } else {
                x = -10.0 * Math.log10(1 + (Math.pow(10.0, this.rpl_dB*0.1) - 1) * Math.pow(Math.cosh(this.order * acosh(f/this.f1_MHz)), 2.0));
            }
            this.s12[i] = x;
            this.s11[i] = 10.0 * Math.log10(1.0 - Math.pow(10, x/10));
        }
    }

    public void hpf_frqres(double fmin_MHz, double fmax_MHz, int point) {
        double f, x;
        if(fmin_MHz < 0 || fmax_MHz <= fmin_MHz) {
            fr_point = 0;
            return;
        }
        this.fr_point = point;
        this.fr_start = fmin_MHz;
        this.fr_stop = fmax_MHz;
        this.s11 = new double[point];
        this.s12 = new double[point];
        for(int i=0; i<point; i++) {
            f = (fmax_MHz - fmin_MHz) / (point - 1) * i + fmin_MHz;
            if(f >= this.f1_MHz) {
                x = -10.0 * Math.log10(1 + (Math.pow(10.0, this.rpl_dB*0.1) - 1) * Math.pow(Math.cos(this.order * Math.acos(this.f1_MHz/f)), 2.0));
            } else {
                x = -10.0 * Math.log10(1 + (Math.pow(10.0, this.rpl_dB*0.1) - 1) * Math.pow(Math.cosh(this.order * acosh(this.f1_MHz/f)), 2.0));
            }
            this.s12[i] = x;
            this.s11[i] = 10.0 * Math.log10(1.0 - Math.pow(10, x/10));
        }
    }

    public void bpf_frqres(double fmin_MHz, double fmax_MHz, int point) {
        double f, fofs, x;
        if(fmin_MHz < 0 || fmax_MHz <= fmin_MHz) {
            fr_point = 0;
            return;
        }
        this.fr_point = point;
        this.fr_start = fmin_MHz;
        this.fr_stop = fmax_MHz;
        this.s11 = new double[point];
        this.s12 = new double[point];
        double f0 = Math.sqrt(this.f1_MHz*this.f2_MHz);
        double bw = this.f2_MHz-this.f1_MHz;
        for(int i=0; i<point; i++) {
            f = (fmax_MHz - fmin_MHz) / (point - 1) * i + fmin_MHz;
            fofs = Math.abs(f-f0*f0/f);
            if(fofs <= bw) {
                x = -10.0 * Math.log10(1 + (Math.pow(10.0, this.rpl_dB*0.1) - 1) * Math.pow(Math.cos(this.order * Math.acos(fofs/bw)), 2.0));
            } else {
                x = -10.0 * Math.log10(1 + (Math.pow(10.0, this.rpl_dB*0.1) - 1) * Math.pow(Math.cosh(this.order * acosh(fofs/bw)), 2.0));
            }
            this.s12[i] = x;
            this.s11[i] = 10.0 * Math.log10(1.0 - Math.pow(10, x/10));
        }
    }

    private double acosh(double x) {
        return Math.log(x + Math.sqrt(x*x-1.0));
    }

}

