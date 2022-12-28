
public class Login {
	//attributes
	private String username;
	private String password;
	private UserID userID;
	
	//constructor
	public Login(String username, String password, UserIDType userIDType) {
		this.username = username;
		this.password = password;
		this.userID = new UserID(userIDType);
	}
	
	public Login(String username, String password, UserID userID) {
		this.username = username;
		this.password = password;
		this.userID = userID;
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

	public UserID getUserID() {
		return userID;
	}

	public void setUserID(UserID userID) {
		this.userID = userID;
	}

	@Override
	public String toString() {
		return username + "," + password + "," + userID.toString();
	}
	
	
	
}
