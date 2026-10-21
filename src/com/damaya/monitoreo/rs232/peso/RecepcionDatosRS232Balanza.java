package com.damaya.monitoreo.rs232.peso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import com.damaya.monitoreo.rs232.config.ParametrosConfiguracion;
import com.damaya.monitoreo.rs232.constantes.ConstantesEnvioRecepcionDatosRS232;
import com.damaya.monitoreo.rs232.interfaces.IRecepcionDatosPuertoSerial;
import com.damaya.monitoreo.rs232.mapeo.MapeadorParametrosConfiguracion;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * The Class RecepcionDatosRS232Balanza.
 */
public class RecepcionDatosRS232Balanza implements Runnable, IRecepcionDatosPuertoSerial, SerialPortEventListener {
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
	final static Logger logger = Logger.getLogger(RecepcionDatosRS232Balanza.class);
	
	/**
	 * Instantiates a new recepcion datos r s232 balanza.
	 */
	public RecepcionDatosRS232Balanza() {
		super();
		inicializarDatosEntrada();
	}
	
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
		nombrePuerto = (String) JOptionPane.showInputDialog(null, "Seleccione un puerto para inicar la recepción de datos: ", "Selecci�n de datos de puerto serial: �Qu� puerto vas a usar?", JOptionPane.QUESTION_MESSAGE, null, listaPuertosAsVector, listaPuertosAsVector[1]);
		if (nombrePuerto != null) {
			try {
				CommPortIdentifier identificadorPuerto = CommPortIdentifier.getPortIdentifier(nombrePuerto);
				if (identificadorPuerto.isCurrentlyOwned()) {
					logger.error("El puerto: " + nombrePuerto + " se encuentra en uso.");
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
					if (segundosRestantes == 0) {
						try {
							logger.info("Se ha terminado la recepción de datos obtenidos de la balanza suministrando.");
							JOptionPane.showMessageDialog(null, "Se ha terminado la recepción de datos desde el puerto: " + nombrePuerto, "Cierre de conexi�n.", JOptionPane.INFORMATION_MESSAGE);
							flujoEntrada.close();
							flujoEntrada = null;
						}
						catch (IOException ex) {
							logger.error("Se ha presentado un error al cerrar el flujo de datos recibidos por la balanza. ");
							System.exit(1);
						}
					}
					puertoSerialCompleto();
				}
			}
			catch (NoSuchPortException e1) {
				logger.error("No se puede establecer en este momento conexi�n con el pierto: " + nombrePuerto + ". Por tanto termina la ejecución del programa.");
				System.exit(1);
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
		if (getPuertoSerialAbierto() != false) {
			return false;
		}
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			idPuertoSerial = enumComm.nextElement();
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
			JOptionPane.showMessageDialog(null, "Se ha establecido la conexi�n con el puerto: " + nombrePuerto, "Conexi�n exitosa.", JOptionPane.INFORMATION_MESSAGE);
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
	public void abrirPuerto(String nombrePuerto) {
		try {
			puertoSerial = (SerialPort) idPuertoSerial.open(ConstantesEnvioRecepcionDatosRS232.ID_RECEPCION_PUERTO_SERIAL_BALANZA, ConstantesEnvioRecepcionDatosRS232.TIEMPO_APERTURA_PUERTO_MILISEGUNDOS);
		}
		catch (PortInUseException e) {
			logger.error("El Puerto " + nombrePuerto + " se encuentra ocupado.");
		}
	}
	
	@Override
	public void fijarParametrosComunicacionPuerto() {
		try {
			ParametrosConfiguracion parametrosConfiguracion = MapeadorParametrosConfiguracion.mapearParametrosConfiguracion(nombrePuerto);
			puertoSerial.setSerialPortParams(parametrosConfiguracion.getTasaBaudios(), parametrosConfiguracion.getBitDatos(), parametrosConfiguracion.getBitParada(), parametrosConfiguracion.getParidad());
			puertoSerial.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		}
		catch (UnsupportedCommOperationException e) {
			logger.error("No se pudieron establecer los par�metros de la interfaz.");
			System.exit(1);
		}
	}
	
	@Override
	public void obtenerCanalEntrada() {
		try {
			flujoEntrada = puertoSerial.getInputStream();
		}
		catch (IOException e) {
			logger.error("No se puede acceder al flujo de entrada.");
			System.exit(1);
		}
	}
	
	/**
	 * A�adimos un receptor de eventos para estar informados de lo que suceda en el puerto.
	 */
	@Override
	public void adicionarReceptorEventos() {
		try {
			puertoSerial.addEventListener(this);
		}
		catch (TooManyListenersException e) {
			logger.error("Se ha presentado un error al procesar el receptor de eventos en el puerto serial.");
			System.exit(1);
		}
	}
	
	/**
	 * Hacemos que se nos notifique cuando haya datos disponibles para lectura en el buffer de la puerta.
	 */
	public void notificarDatosDisponiblesBufferEntrada() {
		try {
			puertoSerial.notifyOnDataAvailable(true);
		}
		catch (Exception e) {
			logger.error("Se ha presentado un error al notificar la disponibilidad de la informaci�n en el puerto serial.");
			System.exit(1);
		}
	}
	
	/**
	 * Puerto serial completo.
	 */
	@Override
	public void puertoSerialCompleto() {
		if (getPuertoSerialAbierto() == true) {
			if (puertoSerial != null) {
				puertoSerial.close();
				puertoSerial = null;
				setPuertoSerialAbierto(false);
				logger.info("La conexi�n con el puerto serial: " + nombrePuerto + " ha sido cerrada.");
				System.exit(0);
			}
		}
		else {
			logger.warn("El puerto serial " + nombrePuerto + " se encuentra cerrado.");
			System.exit(0);
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
				String datoRecibido = new String(datoBufferLectura, 0, numeroBytes).trim();
				logger.info("Informaci�n recibida desde la balanza: (" + datoRecibido.length() + "): \"" + datoRecibido + "\"");
			}
		}
		catch (IOException e) {
			logger.error("Se ha presentado un error al leer los datos recibidos.");
		}
	}
	
	/**
	 * La mayor�a de los eventos no se trata, �stos son los que se producen por cambios en las l�neas de control del puerto que se está monitorizando, Cuando haya datos disponibles se leen y luego se imprime lo recibido en la consola
	 */
	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
		logger.info("Se ingresa y procesa el evento serial.");
		switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				logger.info("Inicia la verificaci�n del dato recibido desde la balanza. ");
				long horaInicio = System.currentTimeMillis();
				verificarDatosPuertoSerial();
				long horaFin = System.currentTimeMillis();
				long tiempoTransferencia = (horaFin - horaInicio);
				logger.info("Tiempo de Transferencia: " + tiempoTransferencia + " ms.");
				logger.info("Termina la verificaci�n del dato recibido. ");
				break;
			case SerialPortEvent.BI:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.FE:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;
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