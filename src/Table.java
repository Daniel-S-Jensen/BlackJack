import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Table {
	
	//attributes
	static final private int Max_Players = 5;
	static private int count = 0;
	private int id;
	private int playerCount;
	private Player[] players;
	private Boolean full;
	private long joinTime;
	private Deck deck;
	private int betCount;
	private int playerTurn;
	private int payoutcount;
	private int playAgainCount;
	
	private ArrayList<Socket> clientList;
	
	//constructor
	public Table() {
		this.id = ++count;
		this.players = new Player[Max_Players];
		this.playerCount = 0;
		this.clientList = new ArrayList<Socket>();
		this.full = false;
		this.betCount = 0;
		this.playerTurn = 0;
		this.payoutcount = 0;
		this.playAgainCount = 0;
	}
	
	//adds a player to this table
	public void addPlayer(User user, Socket socket) {
		this.players[this.playerCount] = (Player) user;
		this.playerCount++;
		this.clientList.add(socket);
		if(this.playerCount == Max_Players) {
			this.full = true;
		}
	}
	
	private void updateClients(Socket socket, Message message) {
		try {
			for (int i=0; i < this.clientList.size(); i++) {
				if (this.clientList.get(i) == socket) {
					continue;
				}
				else {
					List<Message> messages = new ArrayList<>();
					OutputStream outputStream = this.clientList.get(i).getOutputStream();
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
					messages.add(message);
					objectOutputStream.writeObject(messages);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String tableString = "";
		
		return tableString;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public Boolean getFull() {
		return full;
	}

	public void setFull(Boolean full) {
		this.full = full;
	}
	
	public Deck getDeck() {
		return this.deck;
	}
	
	public Player[] getPlayers() {
		return this.players;
	}

	public long getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(long joinTime) {
		this.joinTime = joinTime;
	}

	public ArrayList<Socket> getClientList() {
		return clientList;
	}
	
	public void incrementBetCount() {
		this.betCount++;
	}
	
	public int getBetCount() {
		return this.betCount;
	}
	
	public void incrementPlayerTurn() {
		this.playerTurn++;
	}
	
	public int getPlayerTurn() {
		return this.playerTurn;
	}

	public int getPayoutcount() {
		return payoutcount;
	}

	public void setPayoutcount(int payoutcount) {
		this.payoutcount = payoutcount;
	}
	
	public void incrementPayoutcount() {
		this.payoutcount++;
	}
	
	public int getPlayAgainCount() {
		return playAgainCount;
	}
	
	public void incrementPlayAgaincount() {
		this.playAgainCount++;
	}
	
	public void decrementPlayercount() {
		this.playerCount--;
	}
	
	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public void setClientList(ArrayList<Socket> clientList) {
		this.clientList = clientList;
	}

	public void resetTable() {
		this.betCount = 0;
		this.playerTurn = 0;
		this.payoutcount = 0;
		this.playAgainCount= 0;
	}
}
