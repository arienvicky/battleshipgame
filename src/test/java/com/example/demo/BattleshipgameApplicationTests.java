package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.FixMethodOrder;

/**
 * Test cases for battleship game to cover all scenarios
 * 
 * @author vbawej
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BattleshipgameApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@LocalServerPort
	private int port;

	private SockJsClient sockJsClient;

	private WebSocketStompClient stompClient;

	private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

	@Before
	public void setup() {
		List<Transport> transports = new ArrayList<>();
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		this.sockJsClient = new SockJsClient(transports);

		this.stompClient = new WebSocketStompClient(sockJsClient);
		this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}


	/**
	 * Test case for Player1 GameStatus check
	 */
	@Test
	public void test1() {
		ResponseEntity<BattleshipGameMessage> responseEntity = restTemplate.getForEntity("/gameStatus",
				BattleshipGameMessage.class, new BattleshipGameMessage());
		BattleshipGameMessage battleshipGameMessage = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("notStarted", battleshipGameMessage.getGameStatus());
	}

	/**
	 * Test case for Player1 Start Game 
	 */
	@Test
	public void test2() {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("/startGame", String.class, new String());
		String battleshipGameMessage = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("success", battleshipGameMessage);
	}

	/**
	 * Test case for sending notification for player 1 ready state and receiving and acting on response 
	 */
	@Test
	public void test4() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assert "player1Ready".equals(greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player1Ready");
					messages.setBattleAreaCols("5");
					messages.setBattleAreaRow("C");
					messages.setPlayer("playerone");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.MINUTES)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player 2 ready state and receiving and acting on response 
	 */
	@Test
	public void test5() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assert "player2Ready".equals(greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});

				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player2Ready");
					messages.setBattleAreaCols("5");
					messages.setBattleAreaRow("C");
					messages.setPlayer("playerone");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.MINUTES)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player1Hit state and receiving and acting on success response 
	 */
	@Test
	public void test6() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assertEquals("hitSuccess", greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player1Hit");
					messages.setBattleAreaCols("5");
					messages.setBattleAreaRow("C");
					messages.setHitKey("10");
					messages.setPlayer("playerone");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player1Hit state and receiving and acting on failure response 
	 */
	@Test
	public void test7() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assertEquals("hitFailure", greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player1Hit");
					messages.setBattleAreaCols("5");
					messages.setBattleAreaRow("C");
					messages.setHitKey("30");
					messages.setPlayer("playerone");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player1Hit state and receiving and acting on success response 
	 */
	@Test
	public void test8() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assertEquals("hitSuccess", greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player1Hit");
					messages.setBattleAreaCols("5");
					messages.setBattleAreaRow("C");
					messages.setHitKey("22");
					messages.setPlayer("playerone");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player1Hit state and receiving and acting on game over response 
	 */
	@Test
	public void test9() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assertEquals("GameOver", greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player1Hit");
					messages.setBattleAreaCols("5");
					messages.setBattleAreaRow("C");
					messages.setHitKey("21");
					messages.setPlayer("playerone");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for restart game
	 */
	@Test
	public void test91() {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("/reStartGame", String.class, new String());
		String battleshipGameMessage = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("success", battleshipGameMessage);
	}

	/**
	 * Test case for Player2 GameStatus check
	 */
	@Test
	public void test92() {
		ResponseEntity<BattleshipGameMessage> responseEntity = restTemplate.getForEntity("/gameStatus",
				BattleshipGameMessage.class, new BattleshipGameMessage());
		BattleshipGameMessage battleshipGameMessage = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("notStarted", battleshipGameMessage.getGameStatus());
	}

	/**
	 * Test case for Player2 Start Game
	 */
	@Test
	public void test93() {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("/startGame", String.class, new String());
		String battleshipGameMessage = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("success", battleshipGameMessage);
	}

	/**
	 * Test case for sending notification for player 1 ready state and receiving and acting on response 
	 */
	@Test
	public void test94() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assert "player1Ready".equals(greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player1Ready");
					messages.setBattleAreaCols("5");
					messages.setBattleAreaRow("C");
					messages.setPlayer("playerone");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.MINUTES)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player 2 ready state and receiving and acting on response 
	 */
	@Test
	public void test95() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assert "player2Ready".equals(greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});

				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player2Ready");
					messages.setBattleAreaCols("5");
					messages.setBattleAreaRow("C");
					messages.setPlayer("playerone");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.MINUTES)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player2Hit state and receiving and acting on success response 
	 */
	@Test
	public void test96() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assertEquals("hitSuccess", greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player2Hit");
					messages.setBattleAreaCols("5");
					messages.setHitKey("10");
					messages.setBattleAreaRow("C");
					messages.setPlayer("playertwo");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player2Hit state and receiving and acting on failure response 
	 */
	@Test
	public void test97() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assertEquals("hitFailure", greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player2Hit");
					messages.setBattleAreaCols("5");
					messages.setBattleAreaRow("C");
					messages.setHitKey("30");
					messages.setPlayer("playertwo");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player1Hit state and receiving and acting on success response 
	 */
	@Test
	public void test98() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assertEquals("hitSuccess", greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player2Hit");
					messages.setBattleAreaCols("5");
					messages.setHitKey("22");
					messages.setBattleAreaRow("C");
					messages.setPlayer("playertwo");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for sending notification for player1Hit state and receiving and acting on GameOver response 
	 */
	@Test
	public void test99() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/messages", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return BattleshipGameMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						BattleshipGameMessage greeting = (BattleshipGameMessage) payload;
						try {
							assertEquals("GameOver", greeting.getMessageType());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					BattleshipGameMessage messages = new BattleshipGameMessage();
					messages.setMessageType("player2Hit");
					messages.setBattleAreaCols("5");
					messages.setHitKey("21");
					messages.setBattleAreaRow("C");
					messages.setPlayer("playertwo");
					messages.setPlayerCount("2");
					messages.setShipCount("2");
					List<BattleshipGameSelectedValue> list = new ArrayList<>();
					BattleshipGameSelectedValue val1 = new BattleshipGameSelectedValue();
					val1.setHit(false);
					val1.setKey(10L);
					list.add(val1);
					BattleshipGameSelectedValue val2 = new BattleshipGameSelectedValue();
					val2.setHit(false);
					val2.setKey(22L);
					list.add(val2);
					BattleshipGameSelectedValue val3 = new BattleshipGameSelectedValue();
					val3.setHit(false);
					val3.setKey(21L);
					list.add(val3);
					messages.setSelectedValues(list);
					session.send("/app/messages", messages);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connect("ws://localhost:{port}/messages", this.headers, handler, this.port);

		if (latch.await(30, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		} else {
			fail("Greeting not received");
		}

	}

	/**
	 * Test case for restart game
	 */
	@Test
	public void test991() {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("/reStartGame", String.class, new String());
		String battleshipGameMessage = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("success", battleshipGameMessage);
	}

	private class TestSessionHandler extends StompSessionHandlerAdapter {

		private final AtomicReference<Throwable> failure;

		public TestSessionHandler(AtomicReference<Throwable> failure) {
			this.failure = failure;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			this.failure.set(new Exception(headers.toString()));
		}

		@Override
		public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
			this.failure.set(ex);
		}

		@Override
		public void handleTransportError(StompSession session, Throwable ex) {
			this.failure.set(ex);
		}
	}
}
