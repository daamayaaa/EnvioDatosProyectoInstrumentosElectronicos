package com.damaya.monitoreo.rs232.cliente.recepcion;

import java.io.Serializable;
import com.damaya.monitoreo.rs232.hyt.RecepcionDatosRS232InstrumentoHyT;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;

/**
 * The Class ClienteRecepcionDatosRS232Balanza.
 */
public class ClienteRecepcionDatosRS232HyT implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		FuncionesUtilitarias.cargarConfiguracionLog();

		Runnable runnable = new RecepcionDatosRS232InstrumentoHyT();
		new Thread(runnable).start();
	}
}
