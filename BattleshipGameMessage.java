
package com.example.demo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * This is Domain class that contains request/response data
 * @author vbawej
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "messageType",
    "tableValues",
    "battleAreaRow",
    "gameStatus",
    "battleAreaCols",
    "shipCount",
    "playerCount",
    "selectedValuesPlayer1",
    "selectedValuesPlayer2",
    "selectedValues", 
    "player",
    "hitKey"
})
@JsonIgnoreProperties
public class BattleshipGameMessage implements Serializable{
	/**
	 * Holds the different states of message e.g. player1Ready, hitsuccess, hit failure etc
	 */
	@JsonProperty("messageType")
    private String messageType;
	/**
	 * Holds the battle area values
	 */
	@JsonProperty("tableValues")
    private HashMap<String, String> tableValues;
    /**
     * Holds the y-axis values of battle field
     */
    @JsonProperty("battleAreaRow")
    private String battleAreaRow;
    /**
     * holds the game status value
     */
    @JsonProperty("gameStatus")
    private String gameStatus;
    /**
     * Holds the x-axis values of battle field
     */
    @JsonProperty("battleAreaCols")
    private String battleAreaCols;
    /**
     * Holds the value of ship count
     */
    @JsonProperty("shipCount")
    private String shipCount;
    /**
     * Holds the value of playercount i.e. 1 player game or two
     */
    @JsonProperty("playerCount")
    private String playerCount;
	/**
     * Holds the selected values of player1
	 */
	@JsonProperty("selectedValuesPlayer1")
    private List<BattleshipGameSelectedValue> selectedValuesPlayer1 = null;
    /**
     * Holds the selected values of player2
     */
    @JsonProperty("selectedValuesPlayer2")
    private List<BattleshipGameSelectedValue> selectedValuesPlayer2 = null; 
    /**
     * Holds the selected values
     */
    @JsonProperty("selectedValues")
    private List<BattleshipGameSelectedValue> selectedValues = null;
    /**
     * Holds the value of current player 
     */
    @JsonProperty("player")
    private String player;
    /**
     * Holds the user clicked key value
     */
    @JsonProperty("hitKey")
    private String hitKey;
    /**
     * SerialVersion UID
     */
    private final static long serialVersionUID = 8718098881642679410L;
	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}
	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	/**
	 * @return the tableValues
	 */
	public HashMap<String, String> getTableValues() {
		return tableValues;
	}
	/**
	 * @param tableValues the tableValues to set
	 */
	public void setTableValues(HashMap<String, String> tableValues) {
		this.tableValues = tableValues;
	}
	/**
	 * @return the battleAreaRow
	 */
	public String getBattleAreaRow() {
		return battleAreaRow;
	}
	/**
	 * @param battleAreaRow the battleAreaRow to set
	 */
	public void setBattleAreaRow(String battleAreaRow) {
		this.battleAreaRow = battleAreaRow;
	}
	/**
	 * @return the gameStatus
	 */
	public String getGameStatus() {
		return gameStatus;
	}
	/**
	 * @param gameStatus the gameStatus to set
	 */
	public void setGameStatus(String gameStatus) {
		this.gameStatus = gameStatus;
	}
	/**
	 * @return the battleAreaCols
	 */
	public String getBattleAreaCols() {
		return battleAreaCols;
	}
	/**
	 * @param battleAreaCols the battleAreaCols to set
	 */
	public void setBattleAreaCols(String battleAreaCols) {
		this.battleAreaCols = battleAreaCols;
	}
	/**
	 * @return the shipCount
	 */
	public String getShipCount() {
		return shipCount;
	}
	/**
	 * @param shipCount the shipCount to set
	 */
	public void setShipCount(String shipCount) {
		this.shipCount = shipCount;
	}
	/**
	 * @return the playerCount
	 */
	public String getPlayerCount() {
		return playerCount;
	}
	/**
	 * @param playerCount the playerCount to set
	 */
	public void setPlayerCount(String playerCount) {
		this.playerCount = playerCount;
	}
	/**
	 * @return the selectedValuesPlayer1
	 */
	public List<BattleshipGameSelectedValue> getSelectedValuesPlayer1() {
		return selectedValuesPlayer1;
	}
	/**
	 * @param selectedValuesPlayer1 the selectedValuesPlayer1 to set
	 */
	public void setSelectedValuesPlayer1(List<BattleshipGameSelectedValue> selectedValuesPlayer1) {
		this.selectedValuesPlayer1 = selectedValuesPlayer1;
	}
	/**
	 * @return the selectedValuesPlayer2
	 */
	public List<BattleshipGameSelectedValue> getSelectedValuesPlayer2() {
		return selectedValuesPlayer2;
	}
	/**
	 * @param selectedValuesPlayer2 the selectedValuesPlayer2 to set
	 */
	public void setSelectedValuesPlayer2(List<BattleshipGameSelectedValue> selectedValuesPlayer2) {
		this.selectedValuesPlayer2 = selectedValuesPlayer2;
	}
	/**
	 * @return the selectedValues
	 */
	public List<BattleshipGameSelectedValue> getSelectedValues() {
		return selectedValues;
	}
	/**
	 * @param selectedValues the selectedValues to set
	 */
	public void setSelectedValues(List<BattleshipGameSelectedValue> selectedValues) {
		this.selectedValues = selectedValues;
	}
	/**
	 * @return the player
	 */
	public String getPlayer() {
		return player;
	}
	/**
	 * @param player the player to set
	 */
	public void setPlayer(String player) {
		this.player = player;
	}
	/**
	 * @return the hitKey
	 */
	public String getHitKey() {
		return hitKey;
	}
	/**
	 * @param hitKey the hitKey to set
	 */
	public void setHitKey(String hitKey) {
		this.hitKey = hitKey;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
