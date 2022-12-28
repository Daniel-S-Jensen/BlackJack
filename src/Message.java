import java.io.Serializable;

public class Message implements Serializable {
	protected MessageType type;
	protected MessageStatus status;
	protected double value;
	protected String name;				//for signup
	protected String username;			//for login
	protected String password;			//for login
	protected User user;				//
	protected Table table;				//for joining games
	protected Boolean userTurn;
	protected Player player;

	//constructors
	public Message(MessageType type) {
		this.type = type;
		this.status = MessageStatus.undefined;
	}

	
	//getters and setters
	public MessageStatus getStatus() {
		return status;
	}

	public void setStatus(MessageStatus status) {
		this.status = status;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Table getTable() {
		return table;
	}


	public void setTable(Table table) {
		this.table = table;
	}


	public Boolean getTurn() {
		return userTurn;
	}


	public void setTurn(Boolean userTurn) {
		this.userTurn = userTurn;
	}


	public Player getPlayer() {
		return player;
	}


	public void setPlayer(Player player) {
		this.player = player;
	}

	
	

	

}
