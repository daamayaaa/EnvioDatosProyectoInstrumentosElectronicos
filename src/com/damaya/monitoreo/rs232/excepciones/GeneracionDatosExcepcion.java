package com.damaya.monitoreo.rs232.excepciones;

public class GeneracionDatosExcepcion extends Exception {
	private static final long serialVersionUID = 1L;
	
	public GeneracionDatosExcepcion() {
		super();
	}
	
	public GeneracionDatosExcepcion(String mensaje) {
		super(mensaje);
	}
	
	public GeneracionDatosExcepcion(Throwable causa) {
		super(causa);
	}
	
	public GeneracionDatosExcepcion(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
