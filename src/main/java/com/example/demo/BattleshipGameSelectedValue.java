
package com.example.demo;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This is a domain class for holding the selected value keys and hit status
 * 
 * @author vbawej
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "key", "hit" })
public class BattleshipGameSelectedValue implements Serializable {

	/**
	 * Holds the key/td id selected by the user
	 */
	@JsonProperty("key")
	private Long key;
	/**
	 * Holds the status of id hit
	 */
	@JsonProperty("hit")
	private Boolean hit;
	/**
	 * SerialVersion UID
	 */
	private final static long serialVersionUID = 8927191011996168247L;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public BattleshipGameSelectedValue() {
	}

	/**
	 * 
	 * @param hit
	 * @param key
	 */
	public BattleshipGameSelectedValue(Long key, Boolean hit) {
		super();
		this.key = key;
		this.hit = hit;
	}

	/**
	 * @return key the battleship location id (td id)
	 */
	@JsonProperty("key")
	public Long getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the battleship location id (td id)
	 */
	@JsonProperty("key")
	public void setKey(Long key) {
		this.key = key;
	}

	/**
	 * @return the battleship location hit status
	 */
	@JsonProperty("hit")
	public Boolean getHit() {
		return hit;
	}

	/**
	 * @param hit
	 *            the battleship location hit status
	 */
	@JsonProperty("hit")
	public void setHit(Boolean hit) {
		this.hit = hit;
	}
}
