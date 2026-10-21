package com.damaya.monitoreo.rs232.cliente.envio;

import java.io.Serializable;
import org.apache.log4j.Logger;
import com.damaya.monitoreo.rs232.peso.EnvioDatosRS232Balanza;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;

/**
 * The Class ClienteEnvioDatosRS232Balanza.
 */
public class ClienteEnvioDatosRS232Balanza implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	final static Logger logger = Logger.getLogger(ClienteEnvioDatosRS232Balanza.class);
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		FuncionesUtilitarias.cargarConfiguracionLog();
		Runnable runnable = new EnvioDatosRS232Balanza();
		new Thread(runnable).start();
		logger.info("Se ha lanzado el cliente responsable del envío de datos de la balanza.");
	}
}
