import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	private static ArrayList<Dealer> dealerList = new ArrayList<Dealer>();
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static ArrayList<Login> loginList = new ArrayList<Login>();
	
	private static ArrayList<Table> tableList = new ArrayList<Table>();

	Player test = new Player();

	public static void main(String[] args) throws IOException {

		ServerSocket server = null;

		try {
			//port 7777 is localhost
			server = new ServerSocket(7777);
			System.out.println("Server is listening on port 7777"); //
			server.setReuseAddress(true);

			while(true) {
				//socket to receive requests on
				Socket client = server.accept();

				System.out.println("Connection from " + client); //

				//new thread
				ClientHandler clientSock = new ClientHandler(client);
				new Thread(clientSock).start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (server != null) {
				try {
					server.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//ClientHandler class
	private static class ClientHandler implements Runnable {
		private final Socket clientSocket;

		public ClientHandler(Socket socket) {
			this.clientSocket = socket;
		}

		public void run() throws IndexOutOfBoundsException {

			loadLoginList();
			loadUserList();

			try {
				Boolean closeThread = false;
				while (!closeThread) {

					Boolean loginMatches = false;
					while (!loginMatches) {

						Boolean messageReceived = false;
						while (!messageReceived) {
							//client input stream from connected socket
							InputStream inputStream = clientSocket.getInputStream();

							//object input stream
							ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

							List<Message> listOfMessages = (List<Message>) objectInputStream.readObject();

							if (listOfMessages.size() > 0) {
								Message receivedMessage = listOfMessages.get(0);

								if (receivedMessage.getType() == MessageType.login) {
									messageReceived = true;
									System.out.println("Login attempt received from " + clientSocket);
									Message message;

									//finds login in list of valid logins
									User loadedUser = null;
									Login foundLogin = null;
									Boolean userFound = false;
									for (int i = 0; i < loginList.size(); i++) {
										if (receivedMessage.getUsername().equals(loginList.get(i).getUsername()) && receivedMessage.getPassword().equals(loginList.get(i).getPassword())) {
											loginMatches = true;
											userFound = true;
											foundLogin = loginList.get(i);
											break;
										}
									}
									//send message back
									message = receivedMessage;
									if (userFound) {
										if (foundLogin.getUserID().getUserType() == UserType.dealer) {
											for (int i = 0; i < dealerList.size(); i++) {
												if(dealerList.get(i).getUserID() == foundLogin.getUserID()) {
													loadedUser = dealerList.get(i);
													break;
												}
											}
										}
										else {
											for (int i = 0; i < playerList.size(); i++) {
												if(playerList.get(i).getUserID() == foundLogin.getUserID()) {
													loadedUser = playerList.get(i);
													break;
												}
											}
										}

										message.setStatus(MessageStatus.success);
										message.setUser(loadedUser);
										System.out.println("Login attempt :" + "success");
									}
									else {
										message.setStatus(MessageStatus.failure);
										System.out.println("Login attempt :" + "failure");
									}

									sendMessage(clientSocket, message);
								}
								else if (receivedMessage.getType() == MessageType.signup) {
									Boolean validUsername = true;
									Message message = receivedMessage;
									for (int i = 0; i < loginList.size(); i++) {
										if(loginList.get(i).getUsername() == receivedMessage.getUsername()) {
											validUsername = false;
											message.setStatus(MessageStatus.failure);
											break;
										}
									}
									if (validUsername) {
										loginMatches = true;
										Login login = new Login(receivedMessage.getUsername(), receivedMessage.getPassword(), UserIDType.P);
										loginList.add(login);
										saveLoginList();
										Player player = new Player(receivedMessage.getName(), login.getUserID());
										playerList.add(player);
										saveUserList();
										message.setUser(player);
										message.setStatus(MessageStatus.success);

									}
									sendMessage(clientSocket, message);
								}
							}
						}
					}

					Boolean logoutReceived = false;
					while (!logoutReceived) {

						//client input stream from connected socket
						InputStream inputStream = clientSocket.getInputStream();

						//object input stream
						ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

						List<Message> listOfMessages = (List<Message>) objectInputStream.readObject();
						if (listOfMessages.size() > 0) {
							
							Message receivedMessage = listOfMessages.get(0);
							if (receivedMessage.getType() == MessageType.logout) {
								logoutReceived = true;
								System.out.println("Logout received from " + clientSocket);
								Message message = receivedMessage;
								message.setStatus(MessageStatus.success);
								//send message back
								sendMessage(clientSocket, message);
								closeThread = true;
							}
							else if (receivedMessage.getType() == MessageType.transaction) {
								System.out.println("Balance update received from " + clientSocket);
								Message message = receivedMessage;
								for (int i = 0; i < playerList.size(); i++) {
									if(playerList.get(i).getUserID() == receivedMessage.getUser().getUserID()) {
										playerList.get(i).receivePayout(receivedMessage.value);
										saveUserList();
										break;
									}
								}
								message.setStatus(MessageStatus.success);
								//send message back
								sendMessage(clientSocket, message);				
							}
							else {
								Message message = receivedMessage;
								if (receivedMessage.getUser().getUserType() == UserType.player) {
									if(receivedMessage.getType() == MessageType.joinTable) {
										//single player
										if(receivedMessage.getValue() == 1) {
											for(int i = 0; i < tableList.size(); i++) {
												if(tableList.get(i).getPlayerCount() == 0) {
													tableList.get(i).addPlayer(receivedMessage.getUser(), clientSocket);
													tableList.get(i).setFull(true);
													message.setStatus(MessageStatus.success);
													message.setTable(tableList.get(i));
													sendMessage(clientSocket, message);
													break;
												}
											}
										}
										//multi player
										else {
											for(int i = 0; i < tableList.size(); i++) {
												if(tableList.get(i).getFull() != true) {
													if(tableList.get(i).getPlayerCount() == 0) {
														tableList.get(i).setJoinTime(System.currentTimeMillis() / 1000);
													}
													tableList.get(i).addPlayer(receivedMessage.getUser(), clientSocket);
													message.setStatus(MessageStatus.failure);
													message.setTable(tableList.get(i));
													sendMessage(clientSocket, message);
													break;
												}
											}
										}
									}
									else if(receivedMessage.getType() == MessageType.requestBet) {
										if(receivedMessage.getValue() >= 0) {
											message.setType(MessageType.betRequest);
											message.setName(receivedMessage.getTable().getPlayers()[0].getName());
											message.setValue(receivedMessage.getValue());
											message.setUser(receivedMessage.getTable().getPlayers()[0]);
											sendMessage(clientSocket, message);
										}
									}
								}
								else if (receivedMessage.getUser().getUserType() == UserType.dealer) {
									if(receivedMessage.getType() == MessageType.joinTable) {
										Table table = new Table();
										table.addDealer(receivedMessage.getUser(), clientSocket);
										message.setTable(table);
										message.setStatus(MessageStatus.success);
										sendMessage(clientSocket, message);
									}
									else if(receivedMessage.getType() == MessageType.startGame) {
										receivedMessage.getTable().setFull(true);
									}
									else if(receivedMessage.getType() == MessageType.dealRequest) {
										if (receivedMessage.getStatus() == MessageStatus.success) {
											message.getTable().resetTable();
											message.getTable().getDeck().shuffleDeck();
											for (int i = 0; i <= message.getTable().getPlayerCount(); i++) {
												if (i == 0) {
													message.getTable().getDealer().getHand().addCard(message.getTable().getDeck().drawCard());
													message.getTable().getDealer().getHand().addCard(message.getTable().getDeck().drawFaceDownCard());
												}
												else {
													message.getTable().getPlayers()[i].getHand().addCard(message.getTable().getDeck().drawCard());
													message.getTable().getPlayers()[i].getHand().addCard(message.getTable().getDeck().drawCard());
												}
											}
											message.setType(MessageType.update);
											for(int i = 0; i < message.getTable().getClientList().size(); i++) {
												sendMessage(message.getTable().getClientList().get(i), message);
											}
											
											//cards dealt now start first turn
											Message startRound = message;
											startRound.setType(MessageType.requestGameAction);
											startRound.setUser(receivedMessage.getTable().getPlayers()[0]);
											startRound.setTurn(true);
											sendMessage(message.getTable().getClientList().get(1), startRound);
										}
									}
									else if(receivedMessage.getType() == MessageType.betRequest) {
										if(receivedMessage.getStatus() == MessageStatus.success) {
											receivedMessage.getTable().incrementBetCount();
											if (receivedMessage.getTable().getBetCount() == receivedMessage.getTable().getPlayerCount() - 1) {
												message.setType(MessageType.dealRequest);
												message.setUser(receivedMessage.getTable().getPlayers()[0]);
												message.setTurn(true);
												sendMessage(clientSocket, message);
											}
											else {
												message.setType(MessageType.requestGameAction);
												message.setUser(receivedMessage.getTable().getPlayers()[receivedMessage.getTable().getBetCount()]);
												message.setTurn(true);
												sendMessage(message.getTable().getClientList().get(receivedMessage.getTable().getBetCount() + 1), message);
											}
										}
									}
									else if(receivedMessage.getType() == MessageType.requestGameAction) {
										if (receivedMessage.getValue() == 1) {	//hit
											message.setType(MessageType.hitRequest);
											message.setName(receivedMessage.getUser().getName());
											if (receivedMessage.getUser().getUserType() == UserType.player) {
												Player temp;
												temp = (Player) receivedMessage.getUser();
												message.setPlayer(temp);
											}
											else {
												message.setPlayer(null);
											}
											message.setUser(receivedMessage.getTable().getPlayers()[0]);
											message.setTurn(true);
											sendMessage(clientSocket, message);
										}
										else {	//stay
											if (receivedMessage.getPlayer() != null) {
												message.getTable().incrementPlayerTurn();
												if (message.getTable().getPlayerTurn() > message.getTable().getPlayerCount()) {
													message.setUser(receivedMessage.getTable().getDealer());
												}
												else {
													message.setUser(receivedMessage.getTable().getPlayers()[receivedMessage.getTable().getPlayerTurn()]);
												}
												message.setType(MessageType.requestGameAction);
												message.setTurn(true);
												sendMessage(clientSocket, message);
											}
											else {
												message.setType(MessageType.endGame);
												message.setUser(receivedMessage.getTable().getDealer());
												message.setTurn(true);
												sendMessage(clientSocket, message);
											}
										}
									}
									else if(receivedMessage.getType() == MessageType.hitRequest) {
										if(receivedMessage.getStatus() == MessageStatus.success) {
											if (receivedMessage.getPlayer() != null) {
												message.getTable().getPlayers()[message.getTable().getPlayerTurn()].getHand().addCard(message.getTable().getDeck().drawCard());
												if(message.getTable().getPlayers()[message.getTable().getPlayerTurn()].getHand().getHasBust() != true) {
													message.setType(MessageType.requestGameAction);
													message.setUser(receivedMessage.getTable().getPlayers()[receivedMessage.getTable().getPlayerTurn()]);
													message.setTurn(true);
													sendMessage(clientSocket, message);
												}
												else {
													message.getTable().incrementPlayerTurn();
													if (message.getTable().getPlayerTurn() > message.getTable().getPlayerCount()) {
														message.setUser(receivedMessage.getTable().getDealer());
													}
													else {
														message.setUser(receivedMessage.getTable().getPlayers()[receivedMessage.getTable().getPlayerTurn()]);
													}
													message.setType(MessageType.requestGameAction);
													message.setTurn(true);
													sendMessage(clientSocket, message);
												}
											}
											else {
												message.getTable().getDealer().getHand().addCard(message.getTable().getDeck().drawCard());
												if(message.getTable().getDealer().getHand().getHasBust() != true) {
													message.setType(MessageType.requestGameAction);
													message.setUser(receivedMessage.getTable().getDealer());
													message.setTurn(true);
													sendMessage(clientSocket, message);
												}
												else if(message.getTable().getDealer().getHand().getHasBust() == true) {
													message.setType(MessageType.endGame);
													message.setUser(receivedMessage.getTable().getDealer());
													message.setTurn(true);
													sendMessage(clientSocket, message);
												}
											}
										}
									}
									else if(receivedMessage.getType() == MessageType.endGame) {
										if(receivedMessage.getStatus() == MessageStatus.success) {
											//calculates payout factors
											if(receivedMessage.getTable().getDealer().getHand().getHandTotal() > 21) {
												for (int i = 0; i < receivedMessage.getTable().getPlayerCount(); i++) {
													if(receivedMessage.getTable().getPlayers()[i].getHand().getHandTotal() <= 21) {
														receivedMessage.getTable().getPlayers()[i].setPayoutFactor(2.0);
													}
													else if(receivedMessage.getTable().getPlayers()[i].getHand().getHandTotal() > 21) {
														receivedMessage.getTable().getPlayers()[i].setPayoutFactor(0.0);
													}
												}
											}
											else if(receivedMessage.getTable().getDealer().getHand().getHandTotal() == 21) {
												for (int i = 0; i < receivedMessage.getTable().getPlayerCount(); i++) {
													if(receivedMessage.getTable().getDealer().getHand().getNumCardsInHand() == 2) {
														if(receivedMessage.getTable().getPlayers()[i].getHand().getHandTotal() == 21) {
															if(receivedMessage.getTable().getPlayers()[i].getHand().getNumCardsInHand() == 2) {
																receivedMessage.getTable().getPlayers()[i].setPayoutFactor(1.0);
															}
															else {
																receivedMessage.getTable().getPlayers()[i].setPayoutFactor(0.0);
															}
														}
													}
													else {
														receivedMessage.getTable().getPlayers()[i].setPayoutFactor(0.0);
													}
												}
											}
											else {	// dealer less than 21
												for (int i = 0; i < receivedMessage.getTable().getPlayerCount(); i++) {
													if(receivedMessage.getTable().getPlayers()[i].getHand().getHandTotal() == 21) {
														if(receivedMessage.getTable().getDealer().getHand().getNumCardsInHand() == 2) {
															receivedMessage.getTable().getPlayers()[i].setPayoutFactor(2.5);
														}
														else {
															receivedMessage.getTable().getPlayers()[i].setPayoutFactor(2.0);
														}
													}
													else if(receivedMessage.getTable().getPlayers()[i].getHand().getHandTotal() > 21) {
														receivedMessage.getTable().getPlayers()[i].setPayoutFactor(0.0);
													}
													else {
														if(receivedMessage.getTable().getPlayers()[i].getHand().getHandTotal() > receivedMessage.getTable().getDealer().getHand().getHandTotal()) {
															receivedMessage.getTable().getPlayers()[i].setPayoutFactor(2.0);
														}
														else if(receivedMessage.getTable().getPlayers()[i].getHand().getHandTotal() == receivedMessage.getTable().getDealer().getHand().getHandTotal()) {
															receivedMessage.getTable().getPlayers()[i].setPayoutFactor(1.0);
														}
														else {
															receivedMessage.getTable().getPlayers()[i].setPayoutFactor(0.0);
														}
													}
												}
											}
											//
											message.setType(MessageType.payoutRequest);
											message.setName(receivedMessage.getTable().getPlayers()[0].getName());
											message.setTurn(true);
											message.setUser(receivedMessage.getTable().getDealer());
											message.setValue(receivedMessage.getTable().getPlayers()[0].getBet() * receivedMessage.getTable().getPlayers()[0].getPayoutFactor());
											sendMessage(message.getTable().getClientList().get(1), message);
										}
									}
									else if(receivedMessage.getType() == MessageType.payoutRequest) {
										if(receivedMessage.getStatus() == MessageStatus.success) {
											receivedMessage.getTable().incrementPayoutcount();
											if (receivedMessage.getTable().getPayoutcount() == receivedMessage.getTable().getPlayerCount() - 1) {
												//payouts done
												message.setType(MessageType.gameOver);
												message.setUser(receivedMessage.getTable().getPlayers()[0]);
												message.setTurn(true);
												sendMessage(clientSocket, message);
											}
											else {
												message.setType(MessageType.payoutRequest);
												message.setName(receivedMessage.getTable().getPlayers()[receivedMessage.getTable().getBetCount()].getName());
												message.setValue(receivedMessage.getTable().getPlayers()[receivedMessage.getTable().getBetCount()].getBet() * receivedMessage.getTable().getPlayers()[receivedMessage.getTable().getBetCount()].getPayoutFactor());
												message.setUser(receivedMessage.getTable().getDealer());
												message.setTurn(true);
												sendMessage(message.getTable().getClientList().get(receivedMessage.getTable().getBetCount() + 1), message);
											}
										}
									}
									else if(receivedMessage.getType() == MessageType.playAgain) {
										if(receivedMessage.getStatus() == MessageStatus.success) {
											receivedMessage.getTable().incrementPlayAgaincount();
										}
										else {
											receivedMessage.getTable().incrementPlayAgaincount();
											Player[] temp = new Player[receivedMessage.getTable().getPlayerCount()];
											ArrayList<Socket> tempList = new ArrayList<Socket>();
											for (int i = 0, k = 0; i < receivedMessage.getTable().getPlayerCount(); i++) {
												tempList.add(receivedMessage.getTable().getClientList().get(0));
												if (receivedMessage.getTable().getPlayers()[i] == receivedMessage.getUser()) {
													tempList.add(receivedMessage.getTable().getClientList().get(i+1));
													continue;
												}
												temp[k++] = receivedMessage.getTable().getPlayers()[i];
											}
											receivedMessage.getTable().decrementPlayercount();
											receivedMessage.getTable().setPlayers(temp);
											receivedMessage.getTable().setClientList(tempList);
										}
										if (receivedMessage.getTable().getPlayAgainCount() == receivedMessage.getTable().getPlayerCount() - 1) {
											message.setType(MessageType.gameOver);
											message.setUser(receivedMessage.getTable().getDealer());
											message.setTurn(true);
											message.setValue(receivedMessage.getTable().getPlayerCount());
											sendMessage(receivedMessage.getTable().getClientList().get(0), message);
										}
									}
								}
							}
						}
						else {
							for(int i = 0; i < tableList.size(); i++) {
								long currentTime = System.currentTimeMillis() / 1000;
								if (currentTime - tableList.get(i).getJoinTime() >= 180) {
									Message message = new Message(MessageType.requestBet);
									message.setTable(tableList.get(i));
									message.setUser(tableList.get(i).getDealer());
									sendMessage(tableList.get(i).getClientList().get(0), message);
								}

							}
						}
					}
				}	
			}
			catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			finally {
				try {
					System.out.println("Closing socket: " + clientSocket);
					clientSocket.close();
					System.out.println("Socket closed.");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		private static void sendMessage(Socket socket, Message message) {
			try {
				List<Message> messages = new ArrayList<>();
				OutputStream outputStream = socket.getOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
				messages.add(message);
				objectOutputStream.writeObject(messages);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}



		private void loadLoginList() {
			String filename = "loginList.txt";	
			try {
				File inFile = new File(filename);
				Scanner input = new Scanner(inFile);
				while (input.hasNextLine()) {
					String inLine = input.nextLine();
					String[] tokens = inLine.split(",");
					if(tokens.length == 3) {
						UserID tempUserID = new UserID(tokens[2].charAt(0), Integer.valueOf(tokens[2].substring(1)));
						Login tempLogin = new Login(tokens[0], tokens[1], tempUserID);
						loginList.add(tempLogin);
					}
					else {
						break;
					}
				}
				UserID temp = new UserID();
				temp.loadCount(loginList.size());
				//input.close();

			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		private static void loadUserList() {
			String filename = "userList.txt";
			try {
				File inFile = new File(filename);
				Scanner input = new Scanner(inFile);
				while (input.hasNextLine()) {
					String inLine = input.nextLine();
					String[] tokens = inLine.split(",");
					if(tokens.length == 3) {
						UserID tempUserID = new UserID(tokens[0].charAt(0), Integer.valueOf(tokens[0].substring(1)));
						if (tempUserID.getUserType() == UserType.dealer) {
							Dealer tempDealer = new Dealer(tempUserID, tokens[1]);
							dealerList.add(tempDealer);
						}
						else {
							Player tempPlayer = new Player(tempUserID, tokens[1], Integer.valueOf(tokens[2]));
							playerList.add(tempPlayer);
						}

					}
					else {
						break;
					}
				}
				UserID temp = new UserID();
				temp.loadCount(loginList.size());
				input.close();

			} catch (FileNotFoundException e) {
				//e.printStackTrace();
			}
		}

		private static void saveLoginList() {
			try {
				String filename = "loginList.txt";
				FileWriter outFile = new FileWriter(filename);
				for (int i = 0; i < loginList.size(); i++) {
					outFile.write(loginList.get(i).toString() + "\n");
				}
				outFile.close();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}

		private static void saveUserList() {
			try {
				String filename = "userList.txt";
				FileWriter outFile = new FileWriter(filename);
				for (int i = 0; i < dealerList.size(); i++) {
					outFile.write(dealerList.get(i).toString() + "\n");
				}
				for (int i = 0; i < playerList.size(); i++) {
					outFile.write(playerList.get(i).toString() + "\n");
				}
				outFile.close();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}


}