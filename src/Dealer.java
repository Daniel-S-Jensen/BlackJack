
public class Dealer extends User {
	
	//attributes
	private Deck deck;
	
	
	//constructor
	public Dealer() {
		this.userType = UserType.dealer;
		this.deck = new Deck();
	}
	
	public Dealer(UserID userID) {
		this.userID = userID;
		this.deck = new Deck();
	}
	
	public Dealer(UserID userID, String name) {
		this.userID = userID;
		this.name = name;
		this.deck = new Deck();
	}
	
	public Deck getDeck() {
		return this.deck;
	}
	
	//
	/*public  getFaceDownCard() {
		
	}
	
	//
	public  askPlayerStandOrHit(User user) {
		
	}
	
	//
	public  payPlayerPayout(User user) {
		
	}*/
	
	public String toString() {
		return (this.userID.toString() + "," + this.name + ","  + 0);
	}
	
}
