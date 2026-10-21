package com.damaya.monitoreo.rs232.constantes;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

// TODO: Auto-generated Javadoc
/**
 * The Interface ConstantesEnvioRecepcionDatosRS232.
 */
public interface ConstantesEnvioRecepcionDatosRS232 {
	
	/** The Constant TASA_BAUDIOS. */
	public static final int TASA_BAUDIOS = 9600;
	
	/** The Constant BITS_DATOS. */
	public static final int BITS_DATOS = SerialPort.DATABITS_8;
	
	/** The Constant BITS_PARADA. */
	public static final int BITS_PARADA = SerialPort.STOPBITS_1;
	
	/** The Constant PARIDAD. */
	public static final int PARIDAD = SerialPort.PARITY_NONE;
	
	/** The Constant ID_PUERTO_SERIAL. */
	public static final String ID_ENVIO_PUERTO_SERIAL_H_Y_T = "EnvioDatosHumedadYTemperaturaRS232";

	/** The Constant ID_PUERTO_SERIAL. */
	public static final String ID_ENVIO_PUERTO_SERIAL_BALANZA = "EnvioDatosBalanzaRS232";

	/** The Constant ID_PUERTO_SERIAL. */
	public static final String ID_ENVIO_PUERTO_SERIAL_INSTRUMENTO_FLUJO = "EnvioDatosInstrumentoFlujoRS232";

	/** The Constant ID_PUERTO_SERIAL. */
	public static final String ID_RECEPCION_PUERTO_SERIAL_H_Y_T = "RecepcionDatosHumedadYTemperaturaRS232";

	/** The Constant ID_PUERTO_SERIAL. */
	public static final String ID_RECEPCION_PUERTO_SERIAL_BALANZA = "RecepcionDatosBalanzaRS232";

	/** The Constant ID_PUERTO_SERIAL. */
	public static final String ID_RECEPCION_PUERTO_SERIAL_INSTRUMENTO_FLUJO = "RecepcionDatosInstrumentoFlujoRS232";

	public static final int TAMANO_BUFFER_LECTURA = 15000;
	
	/** The Constant TIEMPO_EJECUCION. */
	public static final int TIEMPO_EJECUCION = 122200000;
	
	/** The Constant ID_COM_PUERTO_SERIAL. */
	public static final int ID_COM_PUERTO_SERIAL = CommPortIdentifier.PORT_SERIAL;
	
	/** The Constant ID_COM_PUERTO_PARALELOL. */
	public static final int ID_COM_PUERTO_PARALELO = CommPortIdentifier.PORT_PARALLEL;

	/**. Tiempo apertura de puerto en milisegundos. */
	public static final int TIEMPO_APERTURA_PUERTO_MILISEGUNDOS = 500;
	
	public static final int FRECUENCIA = 1000;
	
	public static final int TIEMPO_EJECUCION_RESTANTE = 600000;


}
