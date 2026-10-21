package com.damaya.monitoreo.rs232.cliente.recepcion;

import java.io.Serializable;
import com.damaya.monitoreo.rs232.flujomasico.RecepcionDatosRS232InstrumentoFlujo;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;

/**
 * The Class ClienteRecepcionDatosRS232InstrumentoEW32907.
 */
public class ClienteRecepcionDatosRS232InstrumentoFlujo implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		FuncionesUtilitarias.cargarConfiguracionLog();
		Runnable runnable = new RecepcionDatosRS232InstrumentoFlujo();
		new Thread(runnable).start();
	}
}
