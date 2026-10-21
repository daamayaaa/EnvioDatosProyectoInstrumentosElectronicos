package com.damaya.monitoreo.rs232.flujomasico;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import com.damaya.monitoreo.rs232.constantes.ConstantesEnvioRecepcionDatosRS232;
import com.damaya.monitoreo.rs232.interfaces.IRecepcionDatosPuertoSerial;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * The Class RecepcionDatosRS232InstrumentoEW32907.
 */
public class RecepcionDatosRS232InstrumentoFlujo implements Runnable, Serializable, IRecepcionDatosPuertoSerial, SerialPortEventListener {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** The id puerto serial. */
	private CommPortIdentifier idPuertoSerial;
	/** The enum comm. */
	private Enumeration<CommPortIdentifier> enumComm;
	/** The puerto serial. */
	private SerialPort puertoSerial;
	/** The flujo entrada. */
	private InputStream flujoEntrada;
	/** The puerto serial abierto. */
	private Boolean puertoSerialAbierto;
	/** The tiempo ejecucion restante. */
	private int tiempoEjecucionRestante;
	/** The lista puertos. */
	private List<String> listaPuertos;
	/** The lista puertos as vector. */
	private String[] listaPuertosAsVector;
	/** The nombre puerto. */
	private String nombrePuerto;
	/**
	 * 
	 */
	final static Logger logger = Logger.getLogger(RecepcionDatosRS232InstrumentoFlujo.class);
	
	/**
	 * Instantiates a new recepcion datos r s232 instrumento e w32907.
	 */
	public RecepcionDatosRS232InstrumentoFlujo() {
		super();
		inicializarDatosEntrada();
	}
	
	@Override
	public void inicializarDatosEntrada() {
		setPuertoSerialAbierto(false);
		tiempoEjecucionRestante = ConstantesEnvioRecepcionDatosRS232.TIEMPO_EJECUCION_RESTANTE;
		listaPuertos = FuncionesUtilitarias.obtenerListaPuertos();
		listaPuertosAsVector = listaPuertos.toArray(new String[listaPuertos.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Integer segundosRestantes = tiempoEjecucionRestante;
		nombrePuerto = (String) JOptionPane.showInputDialog(null, "Seleccione un puerto para iniciar la recepción de datos: ", "Selecci�n de datos de puerto serial", JOptionPane.QUESTION_MESSAGE, null, listaPuertosAsVector, listaPuertosAsVector[1]);
		if (nombrePuerto != null) {
			try {
				CommPortIdentifier identificadorPuerto = CommPortIdentifier.getPortIdentifier(nombrePuerto);
				if (identificadorPuerto.isCurrentlyOwned()) {
					logger.error("El puerto: " + nombrePuerto + " se encuentra en usoPort is currently in use");
					System.exit(1);
				}
				else {
					if (abrirPuertoSerial(nombrePuerto) != true) {
						return;
					}
					while (segundosRestantes > 0) {
						segundosRestantes--;
						dormirHilo();
					}
					puertoSerialCompleto();
				}
			}
			catch (NoSuchPortException e1) {
				e1.printStackTrace();
			}
		}
		else {
			logger.error("El puerto no puede permanecer vac�o. Por tanto termina la ejecución del programa.");
			System.exit(1);
		}
	}
	
	/**
	 * Dormir hilo de ejecución.
	 */
	public void dormirHilo() {
		try {
			Thread.sleep(ConstantesEnvioRecepcionDatosRS232.FRECUENCIA);
		}
		catch (InterruptedException e) {
			logger.error("El hilo ha sido interrumpido mientras dorm�a: " + e.getMessage());
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * Abrir puerto serial.
	 *
	 * @param nombrePuerto
	 *            the port name
	 * @return true, if successful
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean abrirPuertoSerial(String nombrePuerto) {
		Boolean puertoEncontrado = false;
		if (puertoSerialAbierto != false) {
			return false;
		}
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			idPuertoSerial = (CommPortIdentifier) enumComm.nextElement();
			if (nombrePuerto.contentEquals(idPuertoSerial.getName())) {
				puertoEncontrado = true;
				break;
			}
		}
		if (puertoEncontrado != true) {
			logger.warn("El puerto serial: " + nombrePuerto + " no pudo ser encontrado.");
			return false;
		}
		abrirPuerto(nombrePuerto);
		if (puertoSerial instanceof SerialPort) {
			logger.warn("Se conect� el puerto serial: " + nombrePuerto);
			obtenerCanalEntrada();
			adicionarReceptorEventos();
			notificarDatosDisponiblesBufferEntrada();
			fijarParametrosComunicacionPuerto();
			setPuertoSerialAbierto(true);
			return true;
		}
		else {
			logger.warn("�nicamente se aceptan puertos seriales.");
			setPuertoSerialAbierto(false);
			return false;
		}
	}
	
	@Override
	public void obtenerCanalEntrada() {
		try {
			flujoEntrada = puertoSerial.getInputStream();
		}
		catch (IOException e) {
			logger.error("No se pudo acceder al flujo de entrada.");
		}
	}
	
	/**
	 * Si el puerto no está en uso, se intenta abrir
	 */
	@Override
	public void abrirPuerto(String nombrePuerto) {
		try {
			puertoSerial = (SerialPort) idPuertoSerial.open(ConstantesEnvioRecepcionDatosRS232.ID_ENVIO_PUERTO_SERIAL_H_Y_T, 500);
		}
		catch (PortInUseException e) {
			logger.error("El Puerto " + nombrePuerto + " se encuentra ocupado.");
		}
	}
	
	@Override
	public void fijarParametrosComunicacionPuerto() {
		try {
			puertoSerial.setSerialPortParams(ConstantesEnvioRecepcionDatosRS232.TASA_BAUDIOS, ConstantesEnvioRecepcionDatosRS232.BITS_DATOS, ConstantesEnvioRecepcionDatosRS232.BITS_PARADA, ConstantesEnvioRecepcionDatosRS232.PARIDAD);
		}
		catch (UnsupportedCommOperationException e) {
			logger.error("No se pudieron establecer los par�metros de la interfaz.");
		}
	}
	
	@Override
	public void adicionarReceptorEventos() {
		try {
			puertoSerial.addEventListener(this);
		}
		catch (TooManyListenersException e) {
			logger.error("TooManyListenersException para el puerto serial.");
		}
	}
	
	public void notificarDatosDisponiblesBufferEntrada() {
		puertoSerial.notifyOnDataAvailable(true);
	}
	
	/**
	 * Puerto serial completo.
	 */
	@Override
	public void puertoSerialCompleto() {
		if (getPuertoSerialAbierto() == true) {
			if (puertoSerial != null) {
				puertoSerial.close();
				setPuertoSerialAbierto(false);
			}
		}
		else {
			logger.warn("El puerto serial se encuentra cerrado.");
		}
	}
	
	/**
	 * Verificar datos puerto serial.
	 */
	public void verificarDatosPuertoSerial() {
		try {
			byte[] datoBufferLectura = new byte[ConstantesEnvioRecepcionDatosRS232.TAMANO_BUFFER_LECTURA];
			while (flujoEntrada.available() > 0) {
				int numeroBytes = flujoEntrada.read(datoBufferLectura, 0, datoBufferLectura.length);
				String datoRecibido = new String(datoBufferLectura, 0, numeroBytes);
				logger.info("Reecibiendo informaci�n desde el instrumento de flujo: " + datoRecibido);
			}
		}
		catch (IOException e) {
			logger.info("Se ha presentado un error al leer los datos recibidos.");
		}
	}
	
	/**
	 * The listener interface for receiving serialPortEvent events. The class that is interested in processing a serialPortEvent event implements this interface, and the object created with that class is registered with a component using the component's <code>addserialPortEventListener<code> method. When the serialPortEvent event occurs, that object's appropriate method is invoked.
	 *
	 * @see serialPortEventEvent
	 */
	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
		logger.info("Se ingresa y procesa el evento serial.");
		switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				logger.info("Inicia la verificaci�n y recepción del dato recibido desde la balanza. ");
				verificarDatosPuertoSerial();
				logger.info("Termina la verificaci�n y recepción del dato. ");
				break;
			case SerialPortEvent.BI:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.FE:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			case SerialPortEvent.PE:
			case SerialPortEvent.RI:
			default:
		}
	}
	
	public Boolean getPuertoSerialAbierto() {
		return puertoSerialAbierto;
	}
	
	public void setPuertoSerialAbierto(Boolean puertoSerialAbierto) {
		this.puertoSerialAbierto = puertoSerialAbierto;
	}
	
	public int getTiempoEjecucionRestante() {
		return tiempoEjecucionRestante;
	}
	
	public void setTiempoEjecucionRestante(int tiempoEjecucionRestante) {
		this.tiempoEjecucionRestante = tiempoEjecucionRestante;
	}
}