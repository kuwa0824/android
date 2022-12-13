package io.github.kuwa0824.mslbpf;

public class MslBpf
{
    public int order;
    public double h, t, Er, f0, fc, bw, rpl;
    public double[] Ze;
    public double[] Zo;
    public double[] Aw;
    public double[] As;
    public double[] Al;

    public MslBpf(double h_mm, double t_mm, double Er_n, int order_n, double fc_GHz, double bw_GHz, double rpl_dB) {
        h = h_mm;
        t = t_mm;
        Er = Er_n;
        order = order_n;
        fc = fc_GHz;
        bw = bw_GHz;
        f0 = Math.sqrt((fc - bw/2)*(fc + bw/2));
        rpl = rpl_dB;
        Ze = new double[order];
        Zo = new double[order];
        Aw = new double[order];
        As = new double[order];
        Al = new double[order];
    }

    public void calc() {
        calcZeZo();
        for(int i=0; i<order; i++) {
            search(i);
        }
    }

    private void calcZeZo() {
        double W = bw / f0;
        double X = Math.sinh(Math.log(1.0/Math.tanh(rpl/17.37))/((order-1)*2.0));
        double[] ak = new double[order];
        double[] bk = new double[order];
        double[] ck = new double[order];
        double[] gk = new double[order];

        for(int i=0; i<order-1; i++) {
            ak[i] = Math.sin((2*i+1)*Math.PI/(2*(order-1)));
        }
        for(int i=0; i<order-1; i++) {
            bk[i] = Math.pow(X,2)+Math.pow(Math.sin((i+1)*Math.PI/(order-1)),2);
        }
        for(int i=0; i<order-1; i++) {
            if(i > 0) {
                gk[i] = 4*ak[i-1]*ak[i]/(bk[i-1]*gk[i-1]);
            } else {
                gk[i] = 2*ak[i]/X;
            }
        }

        ck[0] = Math.sqrt(Math.PI * W / 2 / gk[0]);
        for(int i=1; i<order-1; i++) {
            ck[i] = Math.PI * W /Math.sqrt(gk[i-1]*gk[i])/2;
        }
        ck[order-1] = ck[0];

        for(int i=0; i<order; i++) {
            Ze[i] = 50.0 * (1 + ck[i] + ck[i] * ck[i]);
            Zo[i] = 50.0 * (1 - ck[i] + ck[i] * ck[i]);
        }
    }

    private void search(int target) {
        double w = h;
        double s = h/2.0;
        int chk;
        double[] z = new double[3];
        z[2] = Er;
        double[] err = new double[9];
        double[] dt = new double[3];
        dt[1] = 1.0;

        for(int depth=0; depth<4; depth++) {
            if(depth == 0) {
                dt[0] = 0.9; dt[2] = 1.1;
            } else if(depth == 1) {
                dt[0] = 0.99; dt[2] = 1.01;
            } else if(depth == 2) {
                dt[0] = 0.999; dt[2] = 1.001;
            } else if(depth == 3) {
                dt[0] = 0.9999; dt[2] = 1.0001;
            }
            for(int i=0; i<30; i++) {
                for(int p=0; p<3; p++) {
                    for(int q=0; q<3; q++) {
                        z = diff_msl(w*dt[q], t, h, s*dt[p], Er);
                        err[p*3+q] = Math.pow(z[0]-Zo[target],2)+Math.pow(z[1]-Ze[target],2);
                    }
                }
                chk = 0;
                for(int j=1; j<9; j++) {
                    if(err[j] < err[chk]) {
                        chk = j;
                    }
                }
                if(chk == 4) {
                    break;
                } else {
                    w *= dt[chk%3];
                    s *= dt[chk/3];
                    w = (w < 1.0E-6)? 1.0E-6 : w;
                    s = (s < 1.0E-6)? 1.0E-6 : s;
                }
            }
        }
        Aw[target] = w;
        As[target] = s;
        Al[target] = 75.0 / f0 / Math.sqrt(z[2]);
    }


    // Microstripline model
    //    H.A.Wheeler,"Transmisson-Line Properties of a Strip on a Dielectric Sheet on a Plate", IEEE Trans. Microwave Theory Tech., vol.MTT-25, No.8, Aug 1977.
    private double msl(double w, double t, double h, double er) {
        if(w <= 0 || t <= 0 || h <= 0 || er <= 0) {
            return -1.0;
        }
        double weff = w+(t/Math.PI*Math.log(4.0*Math.E/Math.sqrt(Math.pow(t / h, 2)+Math.pow(t/(w*Math.PI+1.1*t*Math.PI),2))))*(er+1)/2.0/er;
        double x1 = 4.0*(14.0*er+8.0)/(11.0*er)*h/weff;
        double x2 = Math.sqrt(16.0*Math.pow(h/weff,2)*Math.pow((14.0*er+8.0)/(11.0*er),2)+(er+1.0)/(2.0*er)*Math.pow(Math.PI,2));
        double z0 = 120.0*Math.PI/(2.0*Math.PI*Math.sqrt(2.0)*Math.sqrt(er+1.0))*Math.log(1.0+4*(h/weff)*(x1+x2));
        return z0;
    }

    // Differential Microstripline model
    //    M.Kirschning and R.H.Jansen, "Accurate Wide-Range Design Equqtions for the Frequency-Dependent Characteristic of Parallel Coupled Microstrip Lines", IEEE Trans. Microwave Theory Tech., vol.MTT-32, No.1, Jan 1984.
    private double[] diff_msl(double w, double t, double h, double s, double er) {
        double z[] = new double[3];
        if(w <= 0 || t <= 0 || h <= 0 || s <= 0 || er <= 0) {
            z[0] = -1.0;
            z[1] = -1.0;
            return z;
        }
        double u = w/h;
        double g = s/h;
        double e1 = (er+1.0)/2.0;
        double e2 = (er-1.0)/2.0;
        double wx = Math.sqrt(w/(w+12.0*h));
        double ereff;
        if(w < h) {
            ereff = e1+e2*(wx+0.04*Math.pow(1.0-u,2));
        } else {
            ereff = e1+e2*wx;
        }
        double a0 = 0.7287*(ereff-e1)*(1.0-Math.exp(-0.179*u));
        double b0 = 0.747*er/(0.15+er);
        double c0 = b0-(b0-0.207)*Math.exp(-0.414*u);
        double d0 = 0.593+0.694*Math.exp(-0.562*u);
        double ereffo = (e1+a0-ereff)*Math.exp(-1.0*c0*Math.pow(g,d0))+ereff;
        double zosuf = msl(w, t, h, er);
        double q1 = 0.8695*Math.pow(u,0.194);
        double q2 = 1.0+0.7519*g+0.189*Math.pow(g,2.31);
        double q3 = 0.1975+Math.pow(16.6+Math.pow(8.4/g,6),-0.387)+Math.log(Math.pow(g,10)/(1.0+Math.pow(g/3.4,10)))/241.0;
        double q4 = 2.0*q1/q2/(Math.exp(-g)*Math.pow(u,q3)+(2.0-Math.exp(-g))*Math.pow(u,-q3));
        double q5 = 1.794+1.14*Math.log(1.0+0.638/(g+0.517*Math.pow(g,2.43)));
        double q6 = 0.2305+Math.log(Math.pow(g,10)/(1.0+Math.pow(g/5.8,10)))/281.3+Math.log(1.0+0.598*Math.pow(g,1.154))/5.1;
        double q7 = (10.0+190.0*Math.pow(g,2))/(1.0+82.3*Math.pow(g,3));
        double q8 = Math.exp(-6.5-0.95*Math.log(g)-Math.pow(g/0.15,5));
        double q9 = Math.log(q7)*(q8+1.0/16.5);
        double q10 = (q2*q4-q5*Math.exp(Math.log(u)*q6*Math.pow(u,-q9)))/q2;
        double zoodd = zosuf*Math.sqrt(ereff/ereffo)/(1.0-zosuf/120.0/Math.PI*q10*Math.sqrt(ereff));
        double v = u*(20.0+Math.pow(g,2))/(10.0+Math.pow(g,2))+g*Math.exp(-g);
        double ae = 1.0+Math.log((Math.pow(v,4)+Math.pow(v/52.0,2))/(Math.pow(v,4)+0.432))/49.0+Math.log(1.0+Math.pow(v/18.1,3))/18.7;
        double be = 0.564*Math.pow((er-0.9)/(er+3.0),0.053);
        double ereffe = e1+e2*Math.pow(1.0+10.0/v,-ae*be);
        double zoeven = zosuf*Math.sqrt(ereff/ereffe)/(1.0-zosuf/120.0/Math.PI*q4*Math.sqrt(ereff));
        z[0] = zoodd;
        z[1] = zoeven;
    z[2] = ereff;
        return z;
    }

}

