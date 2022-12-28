
public class User {
	
	//attributes
	protected String name;
	protected Hand hand;
	protected UserID userID;
	protected UserType userType;
	protected Boolean isStillInGame = true;
	protected Boolean isStanding = false;
	protected Boolean isTurn = false;
	
	
	//default constructor
	public User() {
	}
	
	//constructor sets name
	public User(String name) {
		this.name = name;
	}
	
	public User(String name, UserID userID) {
		this.name = name;
		this.userID = userID;
	}
	
	//returns if player intends to play next game
	public Boolean isUserStillInGame() {
		return this.isStillInGame;
	}
	
	
	public Hand getHand() {
		return this.hand;
	}
	
	//checks if user has a blackjack or not
	public Boolean checkBlackJack() {
		if(hand.getHandTotal() == 21) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//check is the user has bust or not, total over 21;
	public Boolean checkBust() {
		if(hand.getHandTotal() > 21) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void hit() {
		this.isStanding = false;
	}
	
	//
	public void stand() {
		this.isStanding = true;
	}
	
	public Boolean getIsStanding() {
		return this.isStanding;
	}
	
	//returns user's name
	public String getName() {
		return this.name;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public UserID getUserID() {
		return userID;
	}

	public void setUserID(UserID userID) {
		this.userID = userID;
	}
	
	
	
	
	
}
