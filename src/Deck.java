import java.util.*;

public class Deck {

	//attributes
	private static final int maxNumOfCards = 52;	//max number of cards in the deck
	private int numberOfCardsLeft;					//current number of cards in deck
	private Card[] cards;							//deck, array holding the cards
	
	//constructor
	public Deck() {
		this.numberOfCardsLeft = maxNumOfCards;
		this.generateDeck();
		this.shuffleDeck();
	}
	
	//returns the top card from the deck and "removes" it
	public Card drawCard() {
		Card card = this.cards[this.numberOfCardsLeft - 1];
		this.numberOfCardsLeft--;
		return card;
	}
	
	//returns the top card from the deck and "removes" it
	public Card drawFaceDownCard() {
		Card card = this.cards[this.numberOfCardsLeft - 1];
		this.numberOfCardsLeft--;
		card.setIsFacedown(true);
		return card;
	}
	
	//shuffles the deck
	public void shuffleDeck() {
		List<Card> cardList = Arrays.asList(this.cards);
		Collections.shuffle(cardList);
		cardList.toArray(this.cards);
	}
	
	//creates a new deck of cards
	public void generateDeck() {
		this.cards = new Card[maxNumOfCards];
		for (int i = 0; i < maxNumOfCards; i++) {
			int cardsInSuit = maxNumOfCards/4;
			if (i < cardsInSuit) {
				Card card = new Card(i % cardsInSuit, CardSuit.Clubs);
				cards[i] = card;
			}
			else if (i < 2 * cardsInSuit) {
				Card card = new Card(i % cardsInSuit ,CardSuit.Diamonds);
				cards[i] = card;
				continue;
			}
			else if (i < 3 * cardsInSuit) {
				Card card = new Card(i % cardsInSuit ,CardSuit.Hearts);
				cards[i] = card;
				continue;
			}
			else {
				Card card = new Card(i % cardsInSuit ,CardSuit.Spades);
				cards[i] = card;
				continue;
			}
		}
	}
	
	//returns number of cards in deck
	public int getNumberOfCardsLeft() {
		return this.numberOfCardsLeft;
	}
	
	
	
	
	
	
	
}
