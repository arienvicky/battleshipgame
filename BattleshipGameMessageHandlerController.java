package com.example.demo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * BattleshipGameMessageHandlerController class caters all rest api calls as well as websocket messages
 * 
 * @author vbawej
 *
 */
@RestController
public class BattleshipGameMessageHandlerController {

	/**
	 * Holds the domain information used for request and response
	 */
	private static BattleshipGameMessage message;

	/**
	 * This Websocket API method is the core of this Battleship Game, which
	 * receives multiple requests and acts on them accordingly. This can be
	 * extended to multiple mappings instead of this single mapping to support
	 * large number of users.
	 * 
	 * @param messages
	 *            the domain object
	 * @return Message the domain object
	 */
	@MessageMapping("/messages")
	public BattleshipGameMessage myMessages(BattleshipGameMessage messages) {
		switch (messages.getMessageType()) {
		case "player1Ready":
			setPlayer1Data(messages);
			break;
		case "player2Ready":
			setPlayer2Data(messages);
			break;
		case "player1Hit":
			player1HitCheck(messages);
			break;
		case "player2Hit":
			player2HitCheck(messages);
			break;
		default:
			break;
		}
		return getMessage();
	}

	/**
	 * This API method exposes the current game status
	 * 
	 * @return Message the game status wrapped in this object
	 */
	@RequestMapping(value = "/gameStatus", method = RequestMethod.GET)
	public BattleshipGameMessage gameStatus() {
		if (getMessage().getGameStatus() == null) {
			getMessage().setGameStatus("notStarted");
		}
		return getMessage();

	}

	/**
	 * This API method enables to start the game
	 * 
	 * @return status game started
	 */
	@RequestMapping(value = "/startGame", method = RequestMethod.GET)
	public String startGame() {
		if ("notStarted".equals(getMessage().getGameStatus())) {
			getMessage().setGameStatus("started");
			return "success";
		} else {
			return "falilure";
		}

	}

	/**
	 * This API method enables to restart the game
	 * 
	 * @return status restart success status
	 */
	@RequestMapping(value = "/reStartGame", method = RequestMethod.GET)
	public String reStartGame() {
	/*	if ("started".equals(getMessage().getGameStatus())) {
			return "failure";	
		}	*/
		reStartMessage();
		getMessage().setGameStatus("notStarted");
		return "success";
	}

	/**
	 * This method provides the instance of domain object. This can be moved to
	 * service layer and other modes like db/webservice can be used to keep this
	 * information.
	 * 
	 * @return Message the domain object
	 */
	private BattleshipGameMessage getMessage() {
		if (null == message) {
			message = new BattleshipGameMessage();
		}
		return message;
	}

	/**
	 * This method clears the data of domain object. This can be moved to
	 * service layer and other modes like db/webservice can be used to clear
	 * this information.
	 * 
	 * @return Message the domain object
	 */
	private BattleshipGameMessage reStartMessage() {
		message = new BattleshipGameMessage();
		return message;
	}

	/**
	 * This method keep track of player 1's move and act on that move to provide
	 * the success, failure and game over results.
	 * 
	 * @param messages
	 *            the domain object
	 * @return Message the domain object
	 */
	private BattleshipGameMessage player1HitCheck(BattleshipGameMessage messages) {
		getMessage().setPlayer(messages.getPlayer());
		getMessage().setHitKey(messages.getHitKey());
		getMessage().setMessageType("hitFailure");

		getMessage().getSelectedValuesPlayer2().forEach(selectedvalue -> {
			if (selectedvalue.getKey().toString().equals(messages.getHitKey())) {
				getMessage().setMessageType("hitSuccess");
				selectedvalue.setHit(true);
			}
		});

		List<BattleshipGameSelectedValue> winlist = getMessage().getSelectedValuesPlayer2().stream()
				.filter((selectedvalue) -> selectedvalue.getHit().equals(false)).collect(Collectors.toList());
		if (winlist.isEmpty()) {
			getMessage().setGameStatus("GameOver");
			getMessage().setMessageType("GameOver");
		}

		return getMessage();
	}

	/**
	 * This method keep track of player 2's move and act on that move to provide
	 * the success, failure and game over results.
	 * 
	 * @param messages
	 *            the domain object
	 * @return Message the domain object
	 */
	private BattleshipGameMessage player2HitCheck(BattleshipGameMessage messages) {
		getMessage().setPlayer(messages.getPlayer());
		getMessage().setHitKey(messages.getHitKey());
		getMessage().setMessageType("hitFailure");

		getMessage().getSelectedValuesPlayer1().forEach(selectedvalue -> {
			if (selectedvalue.getKey().toString().equals(messages.getHitKey())) {
				getMessage().setMessageType("hitSuccess");
				selectedvalue.setHit(true);
			}
		});

		List<BattleshipGameSelectedValue> winlist = getMessage().getSelectedValuesPlayer1().stream()
				.filter((selectedvalue) -> selectedvalue.getHit().equals(false)).collect(Collectors.toList());
		if (winlist.isEmpty()) {
			getMessage().setGameStatus("GameOver");
			getMessage().setMessageType("GameOver");
		}

		return getMessage();
	}

	/**
	 * This method keep track of player 1's selected battle area.
	 * 
	 * @param messages
	 *            the domain object
	 * @return Message the domain object
	 */
	private BattleshipGameMessage setPlayer1Data(BattleshipGameMessage messages) {
		if (!"started".equals(getMessage().getGameStatus())) {
			getMessage().setGameStatus("falilure");
			return getMessage();
		}
		BeanUtils.copyProperties(messages, getMessage());
		getMessage().setGameStatus(messages.getMessageType());
		getMessage().setSelectedValuesPlayer1(messages.getSelectedValues());
		return getMessage();
	}

	/**
	 * This method keep track of player 2's selected battle area.
	 * 
	 * @param messages
	 *            the domain object
	 * @return Message the domain object
	 */
	private BattleshipGameMessage setPlayer2Data(BattleshipGameMessage messages) {
		if (!"player1Ready".equals(getMessage().getGameStatus())) {
			getMessage().setGameStatus("falilure");
			return getMessage();
		}
		getMessage().setGameStatus(messages.getMessageType());
		getMessage().setMessageType(messages.getMessageType());
		getMessage().setSelectedValuesPlayer2(messages.getSelectedValues());
		return getMessage();
	}
}
