package com.damaya.monitoreo.rs232.mapeo;

import com.damaya.monitoreo.rs232.config.ParametrosConfiguracion;
import com.damaya.monitoreo.rs232.constantes.ConstantesEnvioRecepcionDatosRS232;

public class MapeadorParametrosConfiguracion {
	/**
	 * @param nombrePuerto
	 * @return
	 */
	public static ParametrosConfiguracion mapearParametrosConfiguracion(String nombrePuerto) {
		ParametrosConfiguracion parametrosConfiguracion = new ParametrosConfiguracion();
		parametrosConfiguracion.setNumeroPuertoSerial(nombrePuerto);
		parametrosConfiguracion.setTasaBaudios(ConstantesEnvioRecepcionDatosRS232.TASA_BAUDIOS);
		parametrosConfiguracion.setBitDatos(ConstantesEnvioRecepcionDatosRS232.BITS_DATOS);
		parametrosConfiguracion.setBitParada(ConstantesEnvioRecepcionDatosRS232.BITS_PARADA);
		parametrosConfiguracion.setParidad(ConstantesEnvioRecepcionDatosRS232.PARIDAD);
		return parametrosConfiguracion;
	}
}
