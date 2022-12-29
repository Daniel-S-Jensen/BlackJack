import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static ArrayList<Player> playerList = new ArrayList<Player>();
	private static ArrayList<Login> loginList = new ArrayList<Login>();
	private static ArrayList<Table> tableList = new ArrayList<Table>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = null;

        try {
            int port = 7777;
            server = new ServerSocket(port);
            System.out.println("Server is listening on " + port);
            server.setReuseAddress(true);

            //infinite loop as server will never stop running unless powered down
            while(true) {
                //socket to receive requests on
                Socket client = server.accept();
                System.out.println("Connection from " + client);

                //new thread is created
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
                            //client input stream from connected client socket
                            InputStream inputStream = clientSocket.getInputStream();
                            //object input stream
                            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                            List<Message> listOfMessages = (List<Message>) objectInputStream.readObject();

                            if (listOfMessages.size() > 0) {
                                Message receivedMessage  = listOfMessages.get(0);

                                if (receivedMessage.getType() == MessageType.login) {
                                    messageReceived = true;
                                    System.out.println("Login attempt received from: " + clientSocket);
                                    
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
									Message message = receivedMessage;
									if (userFound) {
                                        for (int i = 0; i < playerList.size(); i++) {
                                            if(playerList.get(i).getUserID() == foundLogin.getUserID()) {
                                                loadedUser = playerList.get(i);
                                                break;
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
                        Player tempPlayer = new Player(tempUserID, tokens[1], Integer.valueOf(tokens[2]));
                        playerList.add(tempPlayer);
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