package com.github.satellite.ui.theme;

import java.awt.*;

public class Theme {
	
	public Color c0;
	public Color c1;
	public Color c2;
	public Color c3;
	public Color c4;
	String name;

	public Theme(String name, Color c0, Color c1, Color c2, Color c3, Color c4) {
		this.name = name;
		this.c0 = c0;
		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;
		this.c4 = c4;
	}
	
	public Color dark(int dark) {
		if(dark == 0)
			return c0;
		if(dark == 1)
			return c1;
		if(dark == 2)
			return c2;
		if(dark == 3)
			return c3;
		if(dark == 4)
			return c4;
		return c4;
	}
	
	public Color light(int dark) {
		if(dark == 0)
			return c4;
		if(dark == 1)
			return c3;
		if(dark == 2)
			return c2;
		if(dark == 3)
			return c1;
		if(dark == 4)
			return c0;
		return c0;
	}
	
	public Color getC0() {
		return c0;
	}

	public void setC0(Color c0) {
		this.c0 = c0;
	}

	public Color getC1() {
		return c1;
	}

	public void setC1(Color c1) {
		this.c1 = c1;
	}

	public Color getC2() {
		return c2;
	}

	public void setC2(Color c2) {
		this.c2 = c2;
	}

	public Color getC3() {
		return c3;
	}

	public void setC3(Color c3) {
		this.c3 = c3;
	}

	public Color getC4() {
		return c4;
	}

	public void setC4(Color c4) {
		this.c4 = c4;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
