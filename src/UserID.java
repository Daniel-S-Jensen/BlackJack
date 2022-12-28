
public class UserID {
	//attributes
	static private int count = 0;
	private UserIDType userIDType;
	private int userIDNum;
	
	//constructor
	public UserID() {
		
	}
	
	public UserID(UserIDType userIDType) {
		this.userIDType = userIDType;
		this.userIDNum = ++this.count;
	}
	
	public UserID(char userIDChar, int userIDNum) {
		if (userIDChar == 'D') {
			this.userIDType = UserIDType.D;
		}
		else if (userIDChar == 'P') {
			this.userIDType = UserIDType.P;
		}
		this.userIDNum = userIDNum;
	}
	
	public String toString() {
		String IDString = "";
		if (this.userIDType == UserIDType.D) {
			IDString += "D";
		}
		else if (this.userIDType == UserIDType.P) {
			IDString += "P";
		}
		
		IDString += userIDNum;
		return IDString;
	}
	
	public void loadCount(int count) {
		this.count = count;
	}
	
	public UserType getUserType() {
		if (this.userIDType == UserIDType.D) {
			return UserType.dealer;
		}
		else {
			return UserType.player;
		}
	}
	
}

