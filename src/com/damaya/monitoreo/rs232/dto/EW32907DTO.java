package com.damaya.monitoreo.rs232.dto;

import java.io.Serializable;

/**
* The Class EW32907DAO.
*/
public class EW32907DTO implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** The presion absoluta psia. */
	private String presionAbsolutaPSIA;
	/** The temperatura. */
	private String temperatura;
	/** The volumen flujo. */
	private String volumenFlujo;
	/** The flujo masa. */
	private String flujoMasa;
	/** The valor puesta punto. */
	private String valorPuestaPunto;
	/** The gas. */
	private String gas;
	
	/**
	 * Instantiates a new e w32907 dao.
	 */
	public EW32907DTO() {
		super();
	}
	
	/**
	 * Instantiates a new e w32907 dao.
	 * @param presionAbsolutaPSIA the presion absoluta psia
	 * @param temperatura the temperatura
	 * @param volumenFlujo the volumen flujo
	 * @param flujoMasa the flujo masa
	 * @param valorPuestaPunto the valor puesta punto
	 * @param gas the gas
	 */
	public EW32907DTO(String presionAbsolutaPSIA, String temperatura, String volumenFlujo, String flujoMasa, String valorPuestaPunto, String gas) {
		super();
		this.presionAbsolutaPSIA = presionAbsolutaPSIA;
		this.temperatura = temperatura;
		this.volumenFlujo = volumenFlujo;
		this.flujoMasa = flujoMasa;
		this.valorPuestaPunto = valorPuestaPunto;
		this.gas = gas;
	}
	
	/**
	 * Gets the presion absoluta psia.
	 * @return the presion absoluta psia
	 */
	public String getPresionAbsolutaPSIA() {
		return this.presionAbsolutaPSIA;
	}
	
	/**
	 * Sets the presion absoluta psia.
	 * @param presionAbsolutaPSIA the new presion absoluta psia
	 */
	public void setPresionAbsolutaPSIA(String presionAbsolutaPSIA) {
		this.presionAbsolutaPSIA = presionAbsolutaPSIA;
	}
	
	/**
	 * Gets the temperatura.
	 * @return the temperatura
	 */
	public String getTemperatura() {
		return this.temperatura;
	}
	
	/**
	 * Sets the temperatura.
	 * @param temperatura the new temperatura
	 */
	public void setTemperatura(String temperatura) {
		this.temperatura = temperatura;
	}
	
	/**
	 * Gets the volumen flujo.
	 * @return the volumen flujo
	 */
	public String getVolumenFlujo() {
		return this.volumenFlujo;
	}
	
	/**
	 * Sets the volumen flujo.
	 * @param volumenFlujo the new volumen flujo
	 */
	public void setVolumenFlujo(String volumenFlujo) {
		this.volumenFlujo = volumenFlujo;
	}
	
	/**
	 * Gets the flujo masa.
	 * @return the flujo masa
	 */
	public String getFlujoMasa() {
		return this.flujoMasa;
	}
	
	/**
	 * Sets the flujo masa.
	 * @param flujoMasa the new flujo masa
	 */
	public void setFlujoMasa(String flujoMasa) {
		this.flujoMasa = flujoMasa;
	}
	
	/**
	 * Gets the valor puesta punto.
	 * @return the valor puesta punto
	 */
	public String getValorPuestaPunto() {
		return this.valorPuestaPunto;
	}
	
	/**
	 * Sets the valor puesta punto.
	 * @param valorPuestaPunto the new valor puesta punto
	 */
	public void setValorPuestaPunto(String valorPuestaPunto) {
		this.valorPuestaPunto = valorPuestaPunto;
	}
	
	/**
	 * Gets the gas.
	 * @return the gas
	 */
	public String getGas() {
		return this.gas;
	}
	
	/**
	 * Sets the gas.
	 * @param gas the new gas
	 */
	public void setGas(String gas) {
		this.gas = gas;
	}
	
	@Override
	public String toString() {
		return presionAbsolutaPSIA + " " + temperatura + " " + volumenFlujo + " " + flujoMasa + " " + valorPuestaPunto + "     " + gas + " ";
	}
}
