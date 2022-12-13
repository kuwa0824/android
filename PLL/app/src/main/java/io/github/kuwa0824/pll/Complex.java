package io.github.kuwa0824.pll;

class Complex {
	public double re;
	public double im;
	Complex(double r0, double i0) {
		this.re = r0;
		this.im = i0;
	}
	Complex() {
		this(0,0);
	}
	public Complex add(Complex v) {
		return new Complex(this.re+v.re, this.im+v.im);
	}
	public Complex sub(Complex v) {
		return new Complex(this.re-v.re, this.im-v.im);
	}
	public Complex mul(Complex v) {
		return new Complex(this.re*v.re-this.im*v.im, this.re*v.im+this.im*v.re);
	}
	public Complex div(Complex v) {
		double w = v.re*v.re+v.im*v.im;
		return new Complex((this.re*v.re+this.im*v.im)/w, (this.im*v.re-this.re*v.im)/w);
	}
	public double abs() {
		return Math.sqrt(this.re*this.re+this.im*this.im);
	}
	public double dB() {
		return 20.0*Math.log10(this.abs());
	}
	public double phase() {
		return Math.atan2(this.im, this.re)*180.0/Math.PI;
	}
}

