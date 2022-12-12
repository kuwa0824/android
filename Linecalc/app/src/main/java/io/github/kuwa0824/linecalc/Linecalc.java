package io.github.kuwa0824.linecalc;

public class Linecalc {

    // Microstripline model
    //    H.A.Wheeler,"Transmisson-Line Properties of a Strip on a Dielectric Sheet on a Plate", IEEE Trans. Microwave Theory Tech., vol.MTT-25, No.8, Aug 1977.
    public static double msl(double w, double t, double h, double er) {
        if(w <= 0 || t <= 0 || h <= 0 || er <= 0) {
            return -1.0;
        }
        double weff = w+(t/Math.PI*Math.log(4.0*Math.E/Math.sqrt(Math.pow(t / h, 2)+Math.pow(t/(w*Math.PI+1.1*t*Math.PI),2))))*(er+1)/2.0/er;
        double x1 = 4.0*(14.0*er+8.0)/(11.0*er)*h/weff;
        double x2 = Math.sqrt(16.0*Math.pow(h/weff,2)*Math.pow((14.0*er+8.0)/(11.0*er),2)+(er+1.0)/(2.0*er)*Math.pow(Math.PI,2));
        double z0 = 120.0*Math.PI/(2.0*Math.PI*Math.sqrt(2.0)*Math.sqrt(er+1.0))*Math.log(1.0+4*(h/weff)*(x1+x2));
        return z0;
    }

    // Stripline model
    //    IPC-2141A, "Design Guide for High-Speed Controlled Impedance Circuit Boards", Mar 2004.
    public static double sl_sym(double w, double t, double h, double er) {
        if(w <= 0 || t <= 0 || h-t <= 0 || er <= 0) {
            return -1.0;
        }
        double b = 2.0*h+t;
        double D = w/2.0*(1.0+t/(Math.PI*w)*(1.0+Math.log(4.0*Math.PI*w/t))+0.551*Math.pow(t/w,2));
        double z0 = 60.0/Math.sqrt(er)*Math.log(4.0*b/Math.PI/D);
        return z0;
    }

    // Asymmetric Stripline model
    //    IPC-2141A, "Design Guide for High-Speed Controlled Impedance Circuit Boards", Mar 2004.
    public static double sl_asym(double w, double t, double h1, double h2, double er) {
        if(w <= 0 || t <= 0 || h1 <= 0 || h2 <= 0 || er <= 0) {
            return -1.0;
        }
        double heff = (h1 + h2)/2.0;
        double z0ah1 = sl_sym(w, t, h1, 1.0);
        double z0ah2 = sl_sym(w, t, h2, 1.0);
        double z0aeff = sl_sym(w, t, heff, 1.0);
        double z0air = 2.0*(z0ah1*z0ah2)/(z0ah1+z0ah2);
        double dz0air = 0.0325*Math.PI*Math.pow(z0air,2)*Math.pow(Math.abs(0.5-0.5*(2.0*h1+t)/(h1+h2+t)),2.2)*Math.pow((w+t)/(h1+h2+t),2.9);
        double z0 = (z0aeff-dz0air)/Math.sqrt(er);
        return z0;
    }

    // Differential Microstripline model
    //    M.Kirschning and R.H.Jansen, "Accurate Wide-Range Design Equqtions for the Frequency-Dependent Characteristic of Parallel Coupled Microstrip Lines", IEEE Trans. Microwave Theory Tech., vol.MTT-32, No.1, Jan 1984.
    public static double[] diff_msl(double w, double t, double h, double s, double er) {
        double z[] = new double[2];
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
        return z;
    }

}

