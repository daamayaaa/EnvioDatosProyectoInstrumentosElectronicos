package com.damaya.monitoreo.rs232.peso;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import com.damaya.monitoreo.rs232.config.ParametrosConfiguracion;
import com.damaya.monitoreo.rs232.constantes.ConstantesEnvioRecepcionDatosRS232;
import com.damaya.monitoreo.rs232.constantes.ConstantesErrores;
import com.damaya.monitoreo.rs232.dto.BalanzaDTO;
import com.damaya.monitoreo.rs232.excepciones.GeneracionDatosExcepcion;
import com.damaya.monitoreo.rs232.flujomasico.EnvioDatosRS232InstrumentoFlujo;
import com.damaya.monitoreo.rs232.interfaces.IEnvioDatosHaciaPuertoSerial;
import com.damaya.monitoreo.rs232.interfaces.IEnvioDatosRS232Balanza;
import com.damaya.monitoreo.rs232.mapeo.MapeadorParametrosConfiguracion;
import com.damaya.monitoreo.rs232.utils.FuncionesUtilitarias;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

/**
 * The Class EnvioDatosRS232Balanza.
 */
public class EnvioDatosRS232Balanza implements IEnvioDatosRS232Balanza, IEnvioDatosHaciaPuertoSerial, Runnable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The id puerto serial.
	 */
	private CommPortIdentifier idPuertoSerial;
	/** The enum comm. */
	private Enumeration<CommPortIdentifier> enumComm;
	/** The puerto serial. */
	private SerialPort puertoSerial;
	/** The flujo salida. */
	private OutputStream flujoSalida;
	/** The puerto serial para datos abierto. */
	private Boolean puertoSerialParaDatosAbierto;
	/** The lista puertos. */
	private List<String> listaPuertos;
	/** The lista puertos as vector. */
	private String[] listaPuertosAsVector;
	/** The nombre puerto. */
	private String nombrePuerto;
	/**
	 * 
	 */
	final static Logger logger = Logger.getLogger(EnvioDatosRS232InstrumentoFlujo.class);
	
	/**
	 * Instantiates a new envio datos r s232 balanza.
	 */
	public EnvioDatosRS232Balanza() {
		super();
		inicializarDatosEntrada();
	}

	public void inicializarDatosEntrada() {
		setPuertoSerialParaDatosAbierto(false);
		listaPuertos = FuncionesUtilitarias.obtenerListaPuertos();
		listaPuertosAsVector = listaPuertos.toArray(new String[listaPuertos.size()]);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Integer segundosRestantes = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el tiempo de ejecución de la aplicación: ", "Tiempo de ejecución", JOptionPane.INFORMATION_MESSAGE));
		if (listaPuertosAsVector != null || !ArrayUtils.isEmpty(listaPuertosAsVector)) {
			if (FuncionesUtilitarias.existeValor(listaPuertosAsVector, 1)) {
				nombrePuerto = (String) JOptionPane.showInputDialog(null, "Seleccione un puerto para inicar el envio de datos desde la balanza: ", "Selección de datos de puerto serial", JOptionPane.QUESTION_MESSAGE, null, listaPuertosAsVector, listaPuertosAsVector[1]);
				if (nombrePuerto != null) {
					CommPortIdentifier portIdentifier;
					try {
						portIdentifier = CommPortIdentifier.getPortIdentifier(nombrePuerto);
						if (portIdentifier.isCurrentlyOwned()) {
							logger.error("El puerto: " + nombrePuerto + " se encuentra en uso.");
							System.exit(1);
						}
						else {
							if (abrirPuertoSerial(nombrePuerto) != true) {
								return;
							}
							while (segundosRestantes > 0) {
								try {
									segundosRestantes--;
									logger.info("Se inicia el envío de datos de la balanza suministrando un tiempo de : " + segundosRestantes + " ms.");
									String mensajeGenerado = generarRegistroParaEnvioHaciaPuertoSerial();
									long horaInicio = System.currentTimeMillis();
									enviarDatoPuertoSerial(mensajeGenerado);
									long horaFin = System.currentTimeMillis();
									long tiempoTransferencia = (horaFin - horaInicio);
									logger.info("Bytes enviados: " + mensajeGenerado.getBytes().length + " Tiempo de Transferencia: " + tiempoTransferencia + " ms.");
									Thread.sleep(ConstantesEnvioRecepcionDatosRS232.FRECUENCIA);
								}
								catch (InterruptedException e) {
									logger.error("El hilo ha sido interrumpido mientras dormóa: " + e.getMessage());
									Thread.currentThread().interrupt();
								}
							}
							if (segundosRestantes == 0) {
								try {
									logger.info("Termina el envío de datos obtenidos de la balanza suministrando.");
									JOptionPane.showMessageDialog(null, "Se ha terminado el envío de datos desde el puerto: " + nombrePuerto, "Cierre de conexión.", JOptionPane.INFORMATION_MESSAGE);
									flujoSalida.close();
									flujoSalida = null;
								}
								catch (IOException ex) {
									logger.error("Se ha presentado un error al cerrar el flujo de datos enviados por la balanza. ");
									System.exit(1);
								}
							}
							puertoSerialCompleto();
						}
					}
					catch (NoSuchPortException e) {
						logger.error("El puerto no puede establecer una conexión. Por tanto termina la ejecución del programa.");
						System.exit(1);
					}
				}
				else {
					logger.error("El puerto no puede permanecer vacóo. Por tanto termina la ejecución del programa.");
					System.exit(1);
				}
			}
			else {
				logger.error("No se encontraron puertos disponibles para lanzar la ejecución.");
				System.exit(1);
			}
		}
		else {
			logger.warn("No se encontraron puertos disponibles para lanzar la ejecución.");
			System.exit(1);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public String generarRegistroParaEnvioHaciaPuertoSerial() {
		try {
			BalanzaDTO balanzaDTO = new BalanzaDTO();
			Random valorAleatorio = new Random();
			String[] indicadoresBalanza = { "S D  ", "S S  " };
			int indiceIndicadorBalanza = new Random().nextInt(indicadoresBalanza.length);
			String indicadorAleatorio = (indicadoresBalanza[indiceIndicadorBalanza]);
			String mensaje = indicadorAleatorio + generarValorAleatorio(valorAleatorio, 0, 1, 6) + " g" + "\t";
			balanzaDTO.setPeso(mensaje.replaceAll(",", "."));
			return balanzaDTO.getPeso();
		}
		catch (GeneracionDatosExcepcion e) {
			logger.error("Ha ocurrido un error con la generación del registro a enviar hacia el puerto serial.");
			return "NO_DATA";
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232Balanza#generarValorAleatorio(java.util.Random, int, int, int)
	 */
	@Override
	public String generarValorAleatorio(final Random random, final int lowerBound, final int upperBound, final int decimalPlaces) throws GeneracionDatosExcepcion {
		if (lowerBound < 0 || upperBound <= lowerBound || decimalPlaces < 0) {
			throw new GeneracionDatosExcepcion("Put error message here");
		}
		return String.format("%." + decimalPlaces + "f", ((random == null ? new Random() : random).nextDouble() * (upperBound - lowerBound)) + lowerBound);
	}
	
	/*
	 * (non-Javadoc)
	 * @see co.edu.unal.fing.diee.monitoreo.rs232.interfaces.IEnvioDatosRS232Balanza#abrirPuertoSerial(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean abrirPuertoSerial(String nombrePuerto) {
		Boolean puertoEncontrado = false;
		if (puertoSerialParaDatosAbierto != false) {
			logger.warn("El puerto serial se encuentra abierto.");
			return false;
		}
		logger.info("Puerto serial abierto.");
		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			idPuertoSerial = (CommPortIdentifier) enumComm.nextElement();
			if (idPuertoSerial.isCurrentlyOwned()) {
				logger.error("El puerto: " + nombrePuerto + " se encunetra en uso.");
			}
			if (nombrePuerto.contentEquals(idPuertoSerial.getName())) {
				puertoEncontrado = true;
				break;
			}
		}
		if (puertoEncontrado != true) {
			logger.warn("No se encontro el puerto serial: " + nombrePuerto);
			return false;
		}
		abrirPuerto(nombrePuerto);
		if (puertoSerial instanceof SerialPort) {
			ParametrosConfiguracion parametrosConfiguracion = MapeadorParametrosConfiguracion.mapearParametrosConfiguracion(nombrePuerto);
			logger.warn("Se conectó el puerto serial: " + nombrePuerto);
			obtenerCanalEntrada();
			fijarParametrosComunicacionPuerto(parametrosConfiguracion);
			consultarInformacionPuerto(puertoSerial);
			JOptionPane.showMessageDialog(null, "Se ha establecido la conexión con el puerto: " + nombrePuerto, "Conexión exitosa.", JOptionPane.INFORMATION_MESSAGE);
			setPuertoSerialParaDatosAbierto(true);
			return true;
		}
		else {
			logger.warn("Únicamente se aceptan puertos seriales.");
			setPuertoSerialParaDatosAbierto(false);
			return false;
		}
	}
	
	/**
	 * Si el puerto no está en uso, se intenta abrir.
	 */
	@Override
	public void abrirPuerto(String nombrePuerto) {
		try {
			puertoSerial = (SerialPort) idPuertoSerial.open(ConstantesEnvioRecepcionDatosRS232.ID_ENVIO_PUERTO_SERIAL_BALANZA, 500);
		}
		catch (PortInUseException e) {
			logger.error("El Puerto " + nombrePuerto + " se encuentra ocupado.");
			System.exit(1);
		}
	}
	
	/**
	 * Se obtiene un canal de entrada
	 */
	@Override
	public void obtenerCanalEntrada() {
		try {
			flujoSalida = puertoSerial.getOutputStream();
		}
		catch (IOException e) {
			logger.error("Ha ocurrido un error con el flujo de salida.");
		}
	}
	
	/**
	 * Se fijan los parómetros de comunicación del puerto
	 */
	@Override
	public void fijarParametrosComunicacionPuerto(ParametrosConfiguracion parametrosConfiguracion) {
		try {
			puertoSerial.setSerialPortParams(parametrosConfiguracion.getTasaBaudios(), parametrosConfiguracion.getBitDatos(), parametrosConfiguracion.getBitParada(), parametrosConfiguracion.getParidad());
			puertoSerial.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			puertoSerial.setDTR(true);
			puertoSerial.disableReceiveTimeout();
			puertoSerial.enableReceiveThreshold(1);
		}
		catch (UnsupportedCommOperationException e) {
			logger.error(ConstantesErrores.ERROR_CONFIGURACION_PARAMETROS_REQUERIDOS);
			System.exit(1);
		}
	}
	
	@Override
	public void consultarInformacionPuerto(SerialPort puertoSerial) {
		logger.info("Nombre del puerto: " + puertoSerial.getName());
		logger.info("Tasa de Baudios: " + puertoSerial.getBaudRate());
		logger.info("Bits de Datos: " + puertoSerial.getDataBits());
		logger.info("Bits de Parada: " + puertoSerial.getStopBits());
		logger.info("Paridad:  " + puertoSerial.getParity());
	}
	
	/**
	 * Se cierra el puerto serial y se notifica al usuario.
	 */
	@Override
	public void puertoSerialCompleto() {
		if (getPuertoSerialParaDatosAbierto() == true) {
			if (puertoSerial != null) {
				puertoSerial.close();
				puertoSerial = null;
				setPuertoSerialParaDatosAbierto(false);
				logger.info("La conexión con el puerto serial: " + nombrePuerto + " ha sido cerrada.");
				JOptionPane.showMessageDialog(null, "Termina la ejecución del envío de datos desde el puerto: " + nombrePuerto + ".", "Terminación del programa.", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
		}
		else {
			logger.info("El puerto serial se encuentra cerrado. ");
		}
	}
	

	/**
	 * 
	 */
	@Override
	public void enviarDatoPuertoSerial(String mensaje) {
		if (getPuertoSerialParaDatosAbierto() != true) {
			return;
		}
		try {
			flujoSalida.write(mensaje.getBytes());
			flujoSalida.flush();
		}
		catch (IOException e) {
			logger.error("Se ha presentado un error al enviar el dato de la balanza. ");
			System.exit(1);
		}
	}
	
	public Boolean getPuertoSerialParaDatosAbierto() {
		return puertoSerialParaDatosAbierto;
	}
	
	public void setPuertoSerialParaDatosAbierto(Boolean puertoSerialParaDatosAbierto) {
		this.puertoSerialParaDatosAbierto = puertoSerialParaDatosAbierto;
	}
}