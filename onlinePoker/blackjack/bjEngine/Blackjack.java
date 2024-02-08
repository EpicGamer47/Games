package bjEngine;

import java.util.ArrayList;

/**
 * The Blackjack class allows a single player to play a game of blackjack.
 * The class tracks the player's bankroll but makes no attempt to prevent
 * a negative bankroll.
 *
 */
public class Blackjack
{
	private static final int DECKS = 6, CARDS_PER_DECK = 52;
	private static final double SHOE_PENETRATION = 0.75;
	
    private Shoe shoe;
    
    private double playersMoney;
    
    private ArrayList<Hand> playerHands;
    private ArrayList<Double> playerBets;
    private int currentPlayerHand;
    
    private Hand dealersHand;
    
    /**
     * Constructs a blackjack object that is ready to play.
     * @param playersMoney the player's starting bankroll (all values, including 0 and negative values, are permitted)
     */
    public Blackjack(double playersMoney)
    {
        this.shoe = new Shoe(DECKS);
        this.playersMoney = playersMoney;
        playerHands = new ArrayList<Hand>(4);
        playerBets = new ArrayList<Double>(4);
        reset();
    }
    
    /**
     * Resets for another round, including reseting shoe if necessary
     */
    private void reset()
    {
        playerBets.clear();
        playerHands.clear();
        currentPlayerHand = 0;
        dealersHand = null;
        
        if (shoe.cardsLeft() < DECKS * CARDS_PER_DECK * (1 - SHOE_PENETRATION)) {
        	shoe.reset();
        }
    }
    
    /**
     * Starts checking the next hand.
     * @return true if the next hand exists, false otherwise
     */
    public boolean nextHand()
    {
    	currentPlayerHand++;
        return currentPlayerHand < playerHands.size();
    }
    
    /**
     * Returns the number of hands the player has played
     * @return the number of player hands
     */
    public int getNumHands() {
		return playerHands.size();
    }
    
    /**
     * Returns the player's money (can be negative)
     * @return the player's money
     */
    public double getPlayersMoney()
    {
        return playersMoney;
    }
    
    /**
     * Returns the player's bet
     * @param hand the hand to check
     * @return the player's bet for the hand
     */
    public double getPlayersBet(int hand)
    {
        return playerBets.get(hand);
    }
    
    /**
     * Returns the player's bet for the current hand
     * @return the player's bet for the hand
     */
    public double getPlayersBet()
    {
        return playerBets.get(currentPlayerHand);
    }
    
    /**
     * Returns the specified hand
     * @return hand the hand to return
     */
    public Hand getPlayersHand(int hand)
    {
        return playerHands.get(hand);
    }
    
    /**
     * Returns the hand that the player is currently playing
     * @return the hand that the player is currently playing
     */
    public Hand getPlayersHand()
    {
        return playerHands.get(currentPlayerHand);
    }
    
    /**
     * Places a bet at the start of a round. Deals cards to the player and dealer.
     * @param amount the amount to bet
     */
    public void placeBetAndDealCards(double amount)
    {
        playerBets.add(amount);
        playersMoney -= amount;
        
        Card[] cards = {shoe.dealCard(), shoe.dealCard(), shoe.dealCard(), shoe.dealCard()};
        playerHands.add(new Hand(cards[0], cards[2]));
        dealersHand = new Hand(cards[1], cards[3]);
    }
    
    /**
     * Returns the player's hands
     * @return the player's hands
     */
    public ArrayList<Hand> getPlayerHands()
    {
        return playerHands;
    }
    
    /**
     * Returns the dealer's hand
     * @return the dealer's hand
     */
    public Hand getDealersHand()
    {
        return dealersHand;
    }
    
    /**
     * Sets the dealer's hand
     */
    public void setDealersHand(Hand dealersHand)
    {
       this.dealersHand = dealersHand;
    }
    
    /**
     * Returns true if the player can hit
     * @return true if the player can hit, false otherwise
     */
    public boolean canHit()
    {
		return canHit(currentPlayerHand);
    }
    
    /**
     * Returns true if the player can hit the specified hand
     * @return true if the player can hit the specified hand, false otherwise
     */
    private boolean canHit(int hand)
    {
		boolean result = playerHands.get(hand).getValue() < 21 && !dealersHand.isBlackjack();
		
        if (playerHands.size() == 1) {
        	return result;
        }
        else {
        	return result && playerHands.get(hand).getCards().get(0).getValue() != 1;
        } // doesn't allow hitting on split aces
    }
    
    /**
     * Deals another card to the player's hand.
     * 
     * Precondition: canHit()
     */
    public void hit()
    {
		getPlayersHand().addCard(shoe.dealCard());
    }
    
    /**
     * Returns true if the player can double, false otherwise
     * @return true if the player can double, false otherwise
     */
    public boolean canDoubleDown()
    {
        return canHit() && getPlayersHand().getNumCards() == 2 && playersMoney >= getPlayersBet();
    }
    
    /**
     * Deals another card to the player's hand, then stands.
     * 
     * Precondition: canDoubleDown()
     */
    public void doubleDown()
    {
        hit();
        playersMoney -= getPlayersBet();
        playerBets.set(currentPlayerHand, getPlayersBet()*2);
    }
    
    /**
     * Returns whether the current hand can be split
     * @return true if the hand can be split, false otherwise
     */
    public boolean canSplit() {
    	var playerCards = getPlayersHand().getCards();
    	
		return playerHands.size() != 4 && 
				getPlayersHand().getNumCards() == 2 && 
				playersMoney >= getPlayersBet() &&
				playerCards.get(0).getAdjustedValue() == playerCards.get(1).getAdjustedValue();
    }
    
    /**
     * Splits the specified hand.
     *
     * Precondition: canSplit();
     */
    public void split() {
		Card card1 = getPlayersHand().getCards().get(0),
			 card2 = getPlayersHand().getCards().get(0);

		playerHands.remove(currentPlayerHand);
		playerHands.add(new Hand(card1, shoe.dealCard()));
		playerHands.add(new Hand(card2, shoe.dealCard()));

		double bet = getPlayersBet();
		playerBets.remove(currentPlayerHand);
		playerBets.add(bet);
		playerBets.add(bet);
		playersMoney -= bet;
    }
    
    /**
     * Plays the dealer's hand.
     */
    public void playDealersHand()
    {
    	boolean arePlayerHandsHittable = false;
		for (int i = 0; i < playerHands.size(); i++) {
			arePlayerHandsHittable = arePlayerHandsHittable || canHit(i);
		}

		if (!arePlayerHandsHittable) return;
		
		while (dealersHand.getValue() < 17) {
			dealersHand.addCard(shoe.dealCard());
		}
    }
    
    /**
     * Returns the result of the hand, as described below.
     * -1: player loss/dealer win
     *  0: push (tie)
     *  1: player wins without blackjack
     *  2: player wins with blackjack 
     * @param hand the hand to check
     * @return the result of the hand, as described
     */
	public int getResult(int hand)
	{
		Hand playersHand = playerHands.get(hand);
		int playerScore = playersHand.getValue(),
			dealerScore = dealersHand.getValue();

		if (playerHands.size() == 1 && playersHand.isBlackjack() && !dealersHand.isBlackjack()) return 2;
		if (dealersHand.isBlackjack() && !(playersHand.isBlackjack() && playerHands.size() == 1)) return -1;

		if (playerScore > 21) return -1;
		if (dealerScore > 21) return 1;

		if (dealerScore > playerScore) return -1;
		if (dealerScore == playerScore) return 0;
		return 1;
    }
    
    /**
     * Resolves the player's bets (updates player's money based on the
     * results of the round) and resets for another round
     */
    public void resolveBetsAndReset()
    {
    	double[] payTable = {0, 1, 2, 2.5};
        for (int i = 0; i < playerHands.size(); i++) {
        	playersMoney += payTable[getResult(i) + 1] * getPlayersBet(i);
        }
        
        reset();
    }
    
    /**
     * returns the shoe
     * @return the shoe
     */
    public Shoe getShoe() {
    	return shoe;
    }
}
