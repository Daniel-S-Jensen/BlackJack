
public class Hand {

	//attributes
	private static int MAX_HAND_SIZE = 11;	// the maximum number of cards that could be in a hand (4 As. 4 2s, 3 3s = 21 total)
	private Card[] hand;			//the hand of cards,
	private int numCardsInHand;		//the number of cards in the hand
	private int handTotal;			//the total value of the hand
	private Boolean hasBust = false;
	
	//constructor
	public Hand() {
		this.hand = new Card[MAX_HAND_SIZE];
		this.numCardsInHand = 0;
		this.handTotal = 0;
	}
	
	//returns the total of the hand
	public int getHandTotal() {
		return this.handTotal;
	}
	
	//returns the number of cards in the hand
	public int getNumCardsInHand() {
		return this.numCardsInHand;
	}
	
	//adds a card to hand
	public void addCard(Card card) {
		this.hand[numCardsInHand] = card;
		this.numCardsInHand++;
		this.handTotal += card.getValue();
		this.checkHasBust();
	}
	
	//converts hand to string
	public String toString() {
		String string = "";
		for (int i = 0; i < numCardsInHand; i++) {
			string += hand[i].toString();
			if (i != numCardsInHand-1) {
				string += ", ";
			}
		}
		return (string);
	}
	
	private void checkHasBust() {
		if (this.handTotal > 21) {
			for (int i = 0; i < this.numCardsInHand; i++) {
				if (this.hand[i].getIsAce() == true) {
					this.hand[i].changeAce();
					this.handTotal -= 10;
				}
				if (this.handTotal < 21) {
					break;
				}
			}
			if (this.handTotal > 21) {
				this.hasBust = true;
			}
		}
	}
	
	public Boolean getHasBust() {
		return this.hasBust;
	}
	
}
