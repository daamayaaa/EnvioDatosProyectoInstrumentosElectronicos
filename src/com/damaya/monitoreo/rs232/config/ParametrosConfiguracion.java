package com.damaya.monitoreo.rs232.config;

import java.io.Serializable;

public class ParametrosConfiguracion implements Serializable {
	private static final long serialVersionUID = -6702708144994454707L;
	private String numeroPuertoSerial;
	private int tasaBaudios;
	private int digitoControl;
	private int bitDatos;
	private int bitParada;
	private int paridad;
	
	public ParametrosConfiguracion() {
		super();
	}
	
	public ParametrosConfiguracion(String numeroPuertoSerial, int tasaBaudios, int digitoControl, int bitDatos, int bitParada, int paridad) {
		super();
		this.numeroPuertoSerial = numeroPuertoSerial;
		this.tasaBaudios = tasaBaudios;
		this.digitoControl = digitoControl;
		this.bitDatos = bitDatos;
		this.bitParada = bitParada;
		this.paridad = paridad;
	}
	
	public String getNumeroPuertoSerial() {
		return numeroPuertoSerial;
	}
	
	public void setNumeroPuertoSerial(String numeroPuertoSerial) {
		this.numeroPuertoSerial = numeroPuertoSerial;
	}
	
	public int getTasaBaudios() {
		return tasaBaudios;
	}
	
	public void setTasaBaudios(int tasaBaudios) {
		this.tasaBaudios = tasaBaudios;
	}
	
	public int getDigitoControl() {
		return digitoControl;
	}
	
	public void setDigitoControl(int digitoControl) {
		this.digitoControl = digitoControl;
	}
	
	public int getBitDatos() {
		return bitDatos;
	}
	
	public void setBitDatos(int bitDatos) {
		this.bitDatos = bitDatos;
	}
	
	public int getBitParada() {
		return bitParada;
	}
	
	public void setBitParada(int bitParada) {
		this.bitParada = bitParada;
	}

	public int getParidad() {
		return paridad;
	}

	public void setParidad(int paridad) {
		this.paridad = paridad;
	}

	@Override
	public String toString() {
		return "ParametrosConfiguracion [numeroPuertoSerial=" + numeroPuertoSerial + ", tasaBaudios=" + tasaBaudios + ", digitoControl=" + digitoControl + ", bitDatos=" + bitDatos + ", bitParada=" + bitParada + ", paridad=" + paridad + "]";
	}
}