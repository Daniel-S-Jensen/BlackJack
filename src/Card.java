public class Card {
	
	//attributes
	private int cardValue;					//face value of the card
	private String suitString;
	private CardSuit cardSuit;				//the suit of the card
	private Boolean isFaceDown = false;		//whether the card is facing up(false) or down(true)
	private String rankString;
	private CardRank rank;
	private Boolean isAce = false;
	
	//constructor
	public Card(int cardValue, CardSuit cardSuit) {
		switch (cardValue) {
		case 0:
			this.rank = CardRank.Ace;
			this.rankString = "Ace";
			this.cardValue = 11;
			this.isAce = true;
			break;
		case 1:
			this.rank = CardRank.Two;
			this.rankString = "Two";
			this.cardValue = 2;
			break;
		case 2:
			this.rank = CardRank.Three;
			this.rankString = "Three";
			this.cardValue = 3;
			break;
		case 3:
			this.rank = CardRank.Four;
			this.rankString = "Four";
			this.cardValue = 4;
			break;
		case 4:
			this.rank = CardRank.Five;
			this.rankString = "Five";
			this.cardValue = 5;
			break;
		case 5:
			this.rank = CardRank.Six;
			this.rankString = "Six";
			this.cardValue = 6;
			break;
		case 6:
			this.rank = CardRank.Seven;
			this.rankString = "Seven";
			this.cardValue = 7;
			break;
		case 7:
			this.rank = CardRank.Eight;
			this.rankString = "Eight";
			this.cardValue = 8;
			break;
		case 8:
			this.rank = CardRank.Nine;
			this.rankString = "Nine";
			this.cardValue = 9;
			break;
		case 9:
			this.rank = CardRank.Ten;
			this.rankString = "Ten";
			this.cardValue = 10;
			break;
		case 10:
			this.rank = CardRank.Jack;
			this.rankString = "Jack";
			this.cardValue = 10;
			break;
		case 11:
			this.rank = CardRank.Queen;
			this.rankString = "Queen";
			this.cardValue = 10;
			break;
		case 12:
			this.rank = CardRank.King;
			this.rankString = "King";
			this.cardValue = 10;
			break;
		default:
			break;
		}
		this.cardSuit = cardSuit;
		if (cardSuit == CardSuit.Clubs) {
			this.suitString = "Clubs";
		}
		else if (cardSuit == CardSuit.Diamonds) {
			this.suitString = "Diamonds";
		}
		else if (cardSuit == CardSuit.Hearts) {
			this.suitString = "Hearts";
		}
		else if (cardSuit == CardSuit.Spades) {
			this.suitString = "Spades";
		}
		
	}
	
	//returns the face value of the card
	 public int getValue() {
		return this.cardValue;
	}
	
	//returns the suit of the card
	public CardSuit getSuit() {
		return this.cardSuit;
	}
	
	//returns the rank of the card
	public CardRank getCardRank() {
		return this.rank;
	}
	
	//returns true if card is face down, false if not
	public Boolean getIsFacedown() {
		return this.isFaceDown;
	}
	
	//returns true if card is an ace, false if not
	public Boolean getIsAce() {
		return this.isAce;
	}
	
	//setter method for isFaceDown
	public void setIsFacedown(Boolean isFacedown) {
		this.isFaceDown = isFacedown;
	}
	
	//to string
	public String toString() {
		return (this.rankString + " of " + this.suitString);
	}
	
	public void changeAce() {
		if (this.isAce == true) {
			this.cardValue = 1;
		}
	}
}