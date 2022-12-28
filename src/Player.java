
public class Player extends User {
	
	//attributes
	private int accountBalance;		//holds the amount of money in the player's account
	private int betAmount;			//the amount the player bet for the current hand
	private double payoutFactor;
	
	//constructor
	public Player() {
		this.userType = UserType.player;
		this.accountBalance = 0;
	}
	
	public Player(String name, UserID userID) {
		this.name = name;
		this.userID = userID;
	}
	
	public Player(UserID userID, String name, int accountBalance) {
		this.userID = userID;
		this.name = name;
		this.accountBalance = accountBalance;
	}
	
	//returns how much the player bet this round
	public int getBet() {
		return this.betAmount;
	}
	
	//sets how much the player bet this round
	public void setBet(int bet) {
		this.betAmount = bet;
		this.accountBalance -= bet;
	}
	
	//
	public void receivePayout(double payout) {
		this.accountBalance += payout;
	}
	
	//
	public int getAccountBalance() {
		return accountBalance;
	}

	//
	public void setAccountBalance(int accountBalance) {
		this.accountBalance = accountBalance;
	}
	
	public String toString() {
		return (this.userID.toString() + "," + this.name + "," + this.accountBalance);
	}

	public double getPayoutFactor() {
		return payoutFactor;
	}

	public void setPayoutFactor(double payoutFactor) {
		this.payoutFactor = payoutFactor;
	}
	
	
}
