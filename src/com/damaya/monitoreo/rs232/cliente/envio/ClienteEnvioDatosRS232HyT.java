package com.damaya.monitoreo.rs232.cliente.envio;

import java.io.Serializable;
import org.apache.log4j.Logger;
import com.damaya.monitoreo.rs232.hyt.EnvioDatosRS232InstrumentoHyT;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;

public class ClienteEnvioDatosRS232HyT implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	final static Logger logger = Logger.getLogger(ClienteEnvioDatosRS232HyT.class);
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		FuncionesUtilitarias.cargarConfiguracionLog();
		Runnable runnable = new EnvioDatosRS232InstrumentoHyT();
		new Thread(runnable).start();
		logger.info("Se ha lanzado el cliente responsable del envío de datos de los sensores de humedad y temperatura.");
	}
}
