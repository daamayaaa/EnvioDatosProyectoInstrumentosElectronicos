/*
 * 
 */
package com.damaya.monitoreo.rs232.dto;

import java.io.Serializable;

/**
 * The Class BalanzaDAO.
 */
public class BalanzaDTO implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/** The peso gota. */
	private String peso;
	
	/**
	 * Instantiates a new balanza dao.
	 */
	public BalanzaDTO() {
		super();
	}
	
	/**
	 * Instantiates a new balanza dao.
	 *
	 * @param peso the peso 
	 */
	public BalanzaDTO(String peso) {
		super();
		this.peso = peso;
	}
	
	/**
	 * Gets the peso.
	 * @return the peso
	 */
	public String getPeso() {
		return this.peso;
	}
	
	/**
	 * Sets the peso.
	 * @param peso the new peso
	 */
	public void setPeso(String peso) {
		this.peso = peso;
	}
}