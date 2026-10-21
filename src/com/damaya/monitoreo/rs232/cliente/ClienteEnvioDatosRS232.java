package com.damaya.monitoreo.rs232.cliente;

import java.io.Serializable;
import org.apache.log4j.Logger;
import com.damaya.monitoreo.rs232.flujomasico.EnvioDatosRS232InstrumentoFlujo;
import com.damaya.monitoreo.rs232.hyt.EnvioDatosRS232InstrumentoHyT;
import com.damaya.monitoreo.rs232.peso.EnvioDatosRS232Balanza;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;

public class ClienteEnvioDatosRS232 implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	final static Logger logger = Logger.getLogger(ClienteEnvioDatosRS232.class);
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		FuncionesUtilitarias.cargarConfiguracionLog();
		Runnable runnableHyT = new EnvioDatosRS232InstrumentoHyT();
		new Thread(runnableHyT).start();
		logger.info("Se ha lanzado el cliente responsable del envio de datos de los sensores de humedad y temperatura.");
		Runnable runnableBalanza = new EnvioDatosRS232Balanza();
		new Thread(runnableBalanza).start();
		logger.info("Se ha lanzado el cliente responsable del envío de datos de la balanza.");
		Runnable runnableInstrumentoFlujo = new EnvioDatosRS232InstrumentoFlujo();
		new Thread(runnableInstrumentoFlujo).start();
		logger.info("Se ha lanzado el cliente responsable del envío de datos del instrumento de flujo.");
	}
}
