import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// represents a card of value 1 to 13
class Card {
  int value;
  String suit;
  boolean isFlipped;
  Posn location;

  // constructor
  Card(int value, String suit, boolean isFlipped, Posn location) {
    if (value > 13 || value < 1) {
      throw new IllegalArgumentException("invalid card value");
    }
    this.value = value;
    if (!suit.equals("♣") && !suit.equals("♦") && !suit.equals("♥") && !suit.equals("♠")) {
      throw new IllegalArgumentException("invalid suit");
    }
    this.suit = suit;
    this.isFlipped = isFlipped;
    this.location = location;
  }

  // constructor that sets isFlipped to false
  Card(int value, String suit) {
    if (value > 13 || value < 1) {
      throw new IllegalArgumentException("invalid card value");
    }
    this.value = value;
    if (!suit.equals("♣") && !suit.equals("♦") && !suit.equals("♥") && !suit.equals("♠")) {
      throw new IllegalArgumentException("invalid suit");
    }
    this.suit = suit;
    this.isFlipped = false;
  }

  // returns a string containing the Card's value and suit
  public String toString() {
    return this.value + this.suit;
  }

  // draws the image of the card depending on isFlipped
  public WorldImage draw() {
    if (isFlipped) {
      if (this.suit.equals("♠") || this.suit.equals("♣")) {
        return new OverlayImage(new RectangleImage(50, 80, "outline", Color.BLACK),
            new TextImage(this.toString(), 16, FontStyle.BOLD, Color.BLACK));
      }
      else {
        return new OverlayImage(new RectangleImage(50, 80, "outline", Color.BLACK),
            new TextImage(this.toString(), 16, FontStyle.BOLD, Color.RED));
      }
    }
    else {
      return new RectangleImage(50, 80, "solid", Color.BLACK);
    }
  }

  // checks if this card is clicked
  public boolean isClicked(Posn pos) {
    return (pos.x > this.location.x - 25) && (pos.x < this.location.x + 25)
        && (pos.y > this.location.y - 40) && (pos.y < this.location.y + 40);
  }

  // checks if two cards are considered equal(same color and suit)
  public boolean checkCards(Card c) {
    return this.value == c.value && sameColor(c);
  }

  // checks if two cards are of the same color
  public boolean sameColor(Card c) {
    if (this.suit.equals("♣")) {
      return (c.suit.equals("♠"));
    }
    else if (this.suit.equals("♠")) {
      return (c.suit.equals("♣"));
    }
    else if (this.suit.equals("♥")) {
      return (c.suit.equals("♦"));
    }
    else {
      return (c.suit.equals("♥"));
    }
  }
}

// class representing our version of the World
class OurWorld extends World {
  Utils util = new Utils();
  ArrayList<Card> deck;
  int score;
  ArrayList<Card> flippedDeck;
  int tickCount;
  int timer;
  int stepsLeft;

  // constructor
  OurWorld(ArrayList<Card> deck, int score) {
    this.deck = deck;
    this.score = score;
    this.flippedDeck = new ArrayList<Card>();
    this.tickCount = 0;
    this.timer = 0;
    this.stepsLeft = 50;
  }

  // constructor that sets the shuffled deck and score
  OurWorld() {
    this.deck = util.shuffleDeck(new Random());
    this.score = 26;
    this.flippedDeck = new ArrayList<Card>();
    this.tickCount = 0;
    this.timer = 0;
    this.stepsLeft = 50;
  }

  // constructor that sets an ordered deck
  OurWorld(boolean ordered) {
    this.deck = util.makeDeck();
    this.score = 26;
    this.flippedDeck = new ArrayList<Card>();
    this.tickCount = 0;
    this.timer = 0;
    this.stepsLeft = 50;
  }

  //constructor with personalized parameters
  OurWorld(ArrayList<Card> deck, int score, ArrayList<Card> flippedDeck, int tickCount, int timer) {
    this.deck = deck;
    this.score = score;
    this.flippedDeck = flippedDeck;
    this.tickCount = tickCount;
    this.timer = timer;
    this.stepsLeft = 50;
  }

  //constructor with personalized parameters for stepsleft
  OurWorld(ArrayList<Card> deck, int score, ArrayList<Card> flippedDeck, int tickCount, int timer,
      int stepsLeft) {
    this.deck = deck;
    this.score = score;
    this.flippedDeck = flippedDeck;
    this.tickCount = tickCount;
    this.timer = timer;
    this.stepsLeft = stepsLeft;
  }

  // initializes the worldscene
  // EFFECT: draws the deck and score onto the empty worldscene
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(1000, 500);
    for (int i = 1; i < 5; i++) {
      for (int j = 0; j < 13; j++) {
        if (deck.size() > (j + 13 * (i - 1))) {
          Card currCard = deck.get(j + 13 * (i - 1));
          scene.placeImageXY(currCard.draw(), currCard.location.x, currCard.location.y);
        }
      }
    }
    scene.placeImageXY(new TextImage("Score: " + this.score, 20, FontStyle.BOLD, Color.BLACK), 500,
        430);
    scene.placeImageXY(
        new TextImage("Steps Left: " + this.stepsLeft, 20, FontStyle.BOLD, Color.BLACK), 500, 460);
    scene.placeImageXY(new TextImage("Time: " + (this.timer / 20), 20, FontStyle.BOLD, Color.BLACK),
        500, 490);
    return scene;
  }

  // changes the world on mouse click
  // EFFECT: adds clicked cards to the flipped deck
  public void onMousePressed(Posn pos) {
    for (Card c : deck) {
      if (this.flippedDeck.size() < 2 && c.isClicked(pos)) {
        c.isFlipped = true;
        flippedDeck.add(c);
        this.tickCount = 0;
      }
    }
  }

  // chnages the world on ticks
  // EFFECT: removes cards if they are the same and resets the flipped deck,
  // as well as incrementing the score, time, tick count and steps left
  public void onTick() {
    this.tickCount++;
    this.timer++;
    if (flippedDeck.size() == 2 && this.tickCount > 20) {
      if (flippedDeck.get(0).checkCards(flippedDeck.get(1))) {
        this.deck.remove(this.deck.indexOf(flippedDeck.get(1)));
        this.deck.remove(this.deck.indexOf(flippedDeck.get(0)));
        this.score = this.score - 1;
        flippedDeck.clear();
      }
      else {
        this.deck.get(this.deck.indexOf(flippedDeck.get(1))).isFlipped = false;
        this.deck.get(this.deck.indexOf(flippedDeck.get(0))).isFlipped = false;
        flippedDeck.clear();
      }
      this.stepsLeft--;
    }
    if (this.score == 0) {
      this.endOfWorld("You win!");
    }
    if (this.stepsLeft == 0) {
      this.endOfWorld("You lose :(");
    }
  }

  // returns the final scene of the game
  public WorldScene lastScene(String msg) {
    WorldScene scene = new WorldScene(1000, 500);
    scene.placeImageXY(new TextImage(msg, Color.GREEN), 500, 250);
    return scene;
  }
}

// utilities class with functions to make the deck of cards
class Utils {

  // creates the sorted deck of cards
  ArrayList<Card> makeDeck() {
    ArrayList<Card> emptyDeck = new ArrayList<Card>();
    ArrayList<String> allSuits = new ArrayList<String>();
    allSuits.add("♣");
    allSuits.add("♦");
    allSuits.add("♥");
    allSuits.add("♠");
    for (String suit : allSuits) {
      for (int i = 1; i < 14; i++) {
        Card c = new Card(i, suit);
        c.location = new Posn((i - 1) * 75 + 50, allSuits.indexOf(suit) * 100 + 50);
        emptyDeck.add(c);

      }
    }
    return emptyDeck;
  }

  // creates a shuffled deck of cards
  ArrayList<Card> shuffleDeck(Random r) {
    ArrayList<Card> shuffledDeck = new ArrayList<Card>();
    ArrayList<Card> sortedList = new ArrayList<Card>(this.makeDeck());
    for (int i = sortedList.size(); i > 0; i--) {
      int randIdx = r.nextInt(i);
      sortedList.get(randIdx).location = new Posn(((i - 1) % 13) * 75 + 50,
          (((i - 1) / 13) * 100) + 50);
      shuffledDeck.add(sortedList.get(randIdx));
      sortedList.remove(randIdx);
    }
    return shuffledDeck;
  }
}

//examples for concentration
class ExamplesConcentration {
  ExamplesConcentration() {
  }

  Utils u = new Utils();
  Card oneClubs1 = new Card(1, "♣", false, new Posn(50, 50));
  Card twoClubs1 = new Card(2, "♣", false, new Posn(125, 50));
  Card threeClubs1 = new Card(3, "♣", false, new Posn(200, 50));
  Card fourClubs1 = new Card(4, "♣", false, new Posn(275, 50));
  Card fiveClubs1 = new Card(5, "♣", false, new Posn(350, 50));
  Card sixClubs1 = new Card(6, "♣", false, new Posn(425, 50));
  Card sevenClubs1 = new Card(7, "♣", false, new Posn(500, 50));
  Card eightClubs1 = new Card(8, "♣", false, new Posn(575, 50));
  Card nineClubs1 = new Card(9, "♣", false, new Posn(650, 50));
  Card tenClubs1 = new Card(10, "♣", false, new Posn(725, 50));
  Card elevenClubs1 = new Card(11, "♣", false, new Posn(800, 50));
  Card twelveClubs1 = new Card(12, "♣", false, new Posn(875, 50));
  Card thirteenClubs1 = new Card(13, "♣", false, new Posn(950, 50));

  Card oneDiamonds1 = new Card(1, "♦", false, new Posn(50, 150));
  Card twoDiamonds1 = new Card(2, "♦", false, new Posn(125, 150));
  Card threeDiamonds1 = new Card(3, "♦", false, new Posn(200, 150));
  Card fourDiamonds1 = new Card(4, "♦", false, new Posn(275, 150));
  Card fiveDiamonds1 = new Card(5, "♦", false, new Posn(350, 150));
  Card sixDiamonds1 = new Card(6, "♦", false, new Posn(425, 150));
  Card sevenDiamonds1 = new Card(7, "♦", false, new Posn(500, 150));
  Card eightDiamonds1 = new Card(8, "♦", false, new Posn(575, 150));
  Card nineDiamonds1 = new Card(9, "♦", false, new Posn(650, 150));
  Card tenDiamonds1 = new Card(10, "♦", false, new Posn(725, 150));
  Card elevenDiamonds1 = new Card(11, "♦", false, new Posn(800, 150));
  Card twelveDiamonds1 = new Card(12, "♦", false, new Posn(875, 150));
  Card thirteenDiamonds1 = new Card(13, "♦", false, new Posn(950, 150));

  Card oneHearts1 = new Card(1, "♥", false, new Posn(50, 250));
  Card twoHearts1 = new Card(2, "♥", false, new Posn(125, 250));
  Card threeHearts1 = new Card(3, "♥", false, new Posn(200, 250));
  Card fourHearts1 = new Card(4, "♥", false, new Posn(275, 250));
  Card fiveHearts1 = new Card(5, "♥", false, new Posn(350, 250));
  Card sixHearts1 = new Card(6, "♥", false, new Posn(425, 250));
  Card sevenHearts1 = new Card(7, "♥", false, new Posn(500, 250));
  Card eightHearts1 = new Card(8, "♥", false, new Posn(575, 250));
  Card nineHearts1 = new Card(9, "♥", false, new Posn(650, 250));
  Card tenHearts1 = new Card(10, "♥", false, new Posn(725, 250));
  Card elevenHearts1 = new Card(11, "♥", false, new Posn(800, 250));
  Card twelveHearts1 = new Card(12, "♥", false, new Posn(875, 250));
  Card thirteenHearts1 = new Card(13, "♥", false, new Posn(950, 250));

  Card oneSpades1 = new Card(1, "♠", false, new Posn(50, 350));
  Card twoSpades1 = new Card(2, "♠", false, new Posn(125, 350));
  Card threeSpades1 = new Card(3, "♠", false, new Posn(200, 350));
  Card fourSpades1 = new Card(4, "♠", false, new Posn(275, 350));
  Card fiveSpades1 = new Card(5, "♠", false, new Posn(350, 350));
  Card sixSpades1 = new Card(6, "♠", false, new Posn(425, 350));
  Card sevenSpades1 = new Card(7, "♠", false, new Posn(500, 350));
  Card eightSpades1 = new Card(8, "♠", false, new Posn(575, 350));
  Card nineSpades1 = new Card(9, "♠", false, new Posn(650, 350));
  Card tenSpades1 = new Card(10, "♠", false, new Posn(725, 350));
  Card elevenSpades1 = new Card(11, "♠", false, new Posn(800, 350));
  Card twelveSpades1 = new Card(12, "♠", false, new Posn(875, 350));
  Card thirteenSpades1 = new Card(13, "♠", false, new Posn(950, 350));

  Card oneClubs = new Card(1, "♣", false, new Posn(725, 350));
  Card twoClubs = new Card(2, "♣", false, new Posn(500, 150));
  Card threeClubs = new Card(3, "♣", false, new Posn(425, 350));
  Card fourClubs = new Card(4, "♣", false, new Posn(125, 150));
  Card fiveClubs = new Card(5, "♣", false, new Posn(500, 250));
  Card sixClubs = new Card(6, "♣", false, new Posn(500, 350));
  Card sevenClubs = new Card(7, "♣", false, new Posn(575, 50));
  Card eightClubs = new Card(8, "♣", false, new Posn(350, 250));
  Card nineClubs = new Card(9, "♣", false, new Posn(875, 150));
  Card tenClubs = new Card(10, "♣", false, new Posn(425, 50));
  Card elevenClubs = new Card(11, "♣", false, new Posn(875, 350));
  Card twelveClubs = new Card(12, "♣", false, new Posn(650, 250));
  Card thirteenClubs = new Card(13, "♣", false, new Posn(125, 50));

  Card oneDiamonds = new Card(1, "♦", false, new Posn(275, 150));
  Card twoDiamonds = new Card(2, "♦", false, new Posn(575, 350));
  Card threeDiamonds = new Card(3, "♦", false, new Posn(350, 150));
  Card fourDiamonds = new Card(4, "♦", false, new Posn(650, 350));
  Card fiveDiamonds = new Card(5, "♦", false, new Posn(950, 350));
  Card sixDiamonds = new Card(6, "♦", false, new Posn(200, 250));
  Card sevenDiamonds = new Card(7, "♦", false, new Posn(50, 50));
  Card eightDiamonds = new Card(8, "♦", false, new Posn(275, 250));
  Card nineDiamonds = new Card(9, "♦", false, new Posn(425, 250));
  Card tenDiamonds = new Card(10, "♦", false, new Posn(500, 50));
  Card elevenDiamonds = new Card(11, "♦", false, new Posn(200, 150));
  Card twelveDiamonds = new Card(12, "♦", false, new Posn(275, 50));
  Card thirteenDiamonds = new Card(13, "♦", false, new Posn(950, 250));

  Card oneHearts = new Card(1, "♥", false, new Posn(200, 350));
  Card twoHearts = new Card(2, "♥", false, new Posn(650, 50));
  Card threeHearts = new Card(3, "♥", false, new Posn(275, 350));
  Card fourHearts = new Card(4, "♥", false, new Posn(875, 50));
  Card fiveHearts = new Card(5, "♥", false, new Posn(725, 250));
  Card sixHearts = new Card(6, "♥", false, new Posn(650, 150));
  Card sevenHearts = new Card(7, "♥", false, new Posn(725, 150));
  Card eightHearts = new Card(8, "♥", false, new Posn(575, 150));
  Card nineHearts = new Card(9, "♥", false, new Posn(575, 250));
  Card tenHearts = new Card(10, "♥", false, new Posn(50, 250));
  Card elevenHearts = new Card(11, "♥", false, new Posn(125, 250));
  Card twelveHearts = new Card(12, "♥", false, new Posn(800, 250));
  Card thirteenHearts = new Card(13, "♥", false, new Posn(725, 50));

  Card oneSpades = new Card(1, "♠", false, new Posn(950, 150));
  Card twoSpades = new Card(2, "♠", false, new Posn(125, 350));
  Card threeSpades = new Card(3, "♠", false, new Posn(350, 350));
  Card fourSpades = new Card(4, "♠", false, new Posn(350, 50));
  Card fiveSpades = new Card(5, "♠", false, new Posn(800, 50));
  Card sixSpades = new Card(6, "♠", false, new Posn(425, 150));
  Card sevenSpades = new Card(7, "♠", false, new Posn(950, 50));
  Card eightSpades = new Card(8, "♠", false, new Posn(875, 250));
  Card nineSpades = new Card(9, "♠", false, new Posn(800, 150));
  Card tenSpades = new Card(10, "♠", false, new Posn(50, 350));
  Card elevenSpades = new Card(11, "♠", false, new Posn(800, 350));
  Card twelveSpades = new Card(12, "♠", false, new Posn(200, 50));
  Card thirteenSpades = new Card(13, "♠", false, new Posn(50, 150));

  ArrayList<Card> shuffledDeck1 = u.shuffleDeck(new Random(1));
  ArrayList<Card> shuffledArray1 = new ArrayList<Card>(Arrays.asList(this.fiveDiamonds,
      this.elevenClubs, this.elevenSpades, this.oneClubs, this.fourDiamonds, this.twoDiamonds,
      this.sixClubs, this.threeClubs, this.threeSpades, this.threeHearts, this.oneHearts,
      this.twoSpades, this.tenSpades, this.thirteenDiamonds, this.eightSpades, this.twelveHearts,
      this.fiveHearts, this.twelveClubs, this.nineHearts, this.fiveClubs, this.nineDiamonds,
      this.eightClubs, this.eightDiamonds, this.sixDiamonds, this.elevenHearts, this.tenHearts,
      this.oneSpades, this.nineClubs, this.nineSpades, this.sevenHearts, this.sixHearts,
      this.eightHearts, this.twoClubs, this.sixSpades, this.threeDiamonds, this.oneDiamonds,
      this.elevenDiamonds, this.fourClubs, this.thirteenSpades, this.sevenSpades, this.fourHearts,
      this.fiveSpades, this.thirteenHearts, this.twoHearts, this.sevenClubs, this.tenDiamonds,
      this.tenClubs, this.fourSpades, this.twelveDiamonds, this.twelveSpades, this.thirteenClubs,
      this.sevenDiamonds));

  ArrayList<Card> orderedDeck = u.makeDeck();

  OurWorld orderedWorld = new OurWorld(true);
  OurWorld shuffledWorld = new OurWorld();

  // test to use big bang
  // use orderedworld for ordered deck,
  // shuffled world for shuffled deck
  void testBigBang(Tester t) {
    OurWorld world = this.shuffledWorld;
    int worldWidth = 1000;
    int worldHeight = 500;
    double tickRate = 0.05;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }

  // test for makeDeck
  void testMakeDeck(Tester t) {
    t.checkExpect(u.makeDeck(),
        new ArrayList<Card>(Arrays.asList(this.oneClubs1, this.twoClubs1, this.threeClubs1,
            this.fourClubs1, this.fiveClubs1, this.sixClubs1, this.sevenClubs1, this.eightClubs1,
            this.nineClubs1, this.tenClubs1, this.elevenClubs1, this.twelveClubs1,
            this.thirteenClubs1, this.oneDiamonds1, this.twoDiamonds1, this.threeDiamonds1,
            this.fourDiamonds1, this.fiveDiamonds1, this.sixDiamonds1, this.sevenDiamonds1,
            this.eightDiamonds1, this.nineDiamonds1, this.tenDiamonds1, this.elevenDiamonds1,
            this.twelveDiamonds1, this.thirteenDiamonds1, this.oneHearts1, this.twoHearts1,
            this.threeHearts1, this.fourHearts1, this.fiveHearts1, this.sixHearts1,
            this.sevenHearts1, this.eightHearts1, this.nineHearts1, this.tenHearts1,
            this.elevenHearts1, this.twelveHearts1, this.thirteenHearts1, this.oneSpades1,
            this.twoSpades1, this.threeSpades1, this.fourSpades1, this.fiveSpades1, this.sixSpades1,
            this.sevenSpades1, this.eightSpades1, this.nineSpades1, this.tenSpades1,
            this.elevenSpades1, this.twelveSpades1, this.thirteenSpades1)));
  }

  // tests for toString
  void testToString(Tester t) {
    t.checkExpect(this.oneClubs.toString(), "1♣");
    t.checkExpect(this.eightDiamonds.toString(), "8♦");
    t.checkExpect(this.tenHearts.toString(), "10♥");
    t.checkExpect(this.thirteenSpades.toString(), "13♠");
  }

  // tests for ShuffleDeck
  void testShuffleDeck(Tester t) {
    t.checkExpect(this.shuffledDeck1, this.shuffledArray1);
  }

  // tests for Draw
  void testDraw(Tester t) {
    t.checkExpect(new Card(1, "♣", true, new Posn(0, 0)).draw(),
        new OverlayImage(new RectangleImage(50, 80, "outline", Color.BLACK),
            new TextImage("1♣", 16, FontStyle.BOLD, Color.BLACK)));
    t.checkExpect(new Card(1, "♥", true, new Posn(0, 0)).draw(),
        new OverlayImage(new RectangleImage(50, 80, "outline", Color.BLACK),
            new TextImage("1♥", 16, FontStyle.BOLD, Color.RED)));
    t.checkExpect(new Card(1, "♣", false, new Posn(0, 0)).draw(),
        new RectangleImage(50, 80, "solid", Color.BLACK));
  }

  // test for makescene
  void testMakeScene(Tester t) {
    WorldScene emptyScene = new WorldScene(1000, 500);
    emptyScene.placeImageXY(this.fiveDiamonds.draw(), 950, 350);
    emptyScene.placeImageXY(this.elevenClubs.draw(), 875, 350);
    emptyScene.placeImageXY(this.elevenSpades.draw(), 800, 350);
    emptyScene.placeImageXY(this.oneClubs.draw(), 725, 350);
    emptyScene.placeImageXY(this.fourDiamonds.draw(), 650, 350);
    emptyScene.placeImageXY(this.twoDiamonds.draw(), 575, 350);
    emptyScene.placeImageXY(this.sixClubs.draw(), 500, 350);
    emptyScene.placeImageXY(this.threeClubs.draw(), 425, 350);
    emptyScene.placeImageXY(this.threeSpades.draw(), 350, 350);
    emptyScene.placeImageXY(this.threeHearts.draw(), 275, 350);
    emptyScene.placeImageXY(this.oneHearts.draw(), 200, 350);
    emptyScene.placeImageXY(this.twoSpades.draw(), 125, 350);
    emptyScene.placeImageXY(this.tenSpades.draw(), 50, 350);
    emptyScene.placeImageXY(this.thirteenDiamonds.draw(), 950, 250);
    emptyScene.placeImageXY(this.eightSpades.draw(), 875, 250);
    emptyScene.placeImageXY(this.twelveHearts.draw(), 800, 250);
    emptyScene.placeImageXY(this.fiveHearts.draw(), 725, 250);
    emptyScene.placeImageXY(this.twelveClubs.draw(), 650, 250);
    emptyScene.placeImageXY(this.nineHearts.draw(), 575, 250);
    emptyScene.placeImageXY(this.fiveClubs.draw(), 500, 250);
    emptyScene.placeImageXY(this.nineDiamonds.draw(), 425, 250);
    emptyScene.placeImageXY(this.eightClubs.draw(), 350, 250);
    emptyScene.placeImageXY(this.eightDiamonds.draw(), 275, 250);
    emptyScene.placeImageXY(this.sixDiamonds.draw(), 200, 250);
    emptyScene.placeImageXY(this.elevenHearts.draw(), 125, 250);
    emptyScene.placeImageXY(this.tenHearts.draw(), 50, 250);
    emptyScene.placeImageXY(this.oneSpades.draw(), 950, 150);
    emptyScene.placeImageXY(this.nineClubs.draw(), 875, 150);
    emptyScene.placeImageXY(this.nineSpades.draw(), 800, 150);
    emptyScene.placeImageXY(this.sevenHearts.draw(), 725, 150);
    emptyScene.placeImageXY(this.sixHearts.draw(), 650, 150);
    emptyScene.placeImageXY(this.eightHearts.draw(), 575, 150);
    emptyScene.placeImageXY(this.twoClubs.draw(), 500, 150);
    emptyScene.placeImageXY(this.sixSpades.draw(), 425, 150);
    emptyScene.placeImageXY(this.fourDiamonds.draw(), 350, 150);
    emptyScene.placeImageXY(this.oneDiamonds.draw(), 275, 150);
    emptyScene.placeImageXY(this.elevenDiamonds.draw(), 200, 150);
    emptyScene.placeImageXY(this.fourClubs.draw(), 125, 150);
    emptyScene.placeImageXY(this.thirteenSpades.draw(), 50, 150);
    emptyScene.placeImageXY(this.sevenSpades.draw(), 950, 50);
    emptyScene.placeImageXY(this.fourHearts.draw(), 875, 50);
    emptyScene.placeImageXY(this.fiveSpades.draw(), 800, 50);
    emptyScene.placeImageXY(this.thirteenHearts.draw(), 725, 50);
    emptyScene.placeImageXY(this.twoHearts.draw(), 650, 50);
    emptyScene.placeImageXY(this.sevenClubs.draw(), 575, 50);
    emptyScene.placeImageXY(this.tenDiamonds.draw(), 500, 50);
    emptyScene.placeImageXY(this.tenClubs.draw(), 425, 50);
    emptyScene.placeImageXY(this.fourSpades.draw(), 350, 50);
    emptyScene.placeImageXY(this.twelveDiamonds.draw(), 275, 50);
    emptyScene.placeImageXY(this.twelveSpades.draw(), 200, 50);
    emptyScene.placeImageXY(this.thirteenClubs.draw(), 125, 50);
    emptyScene.placeImageXY(this.sevenDiamonds.draw(), 50, 50);
    emptyScene.placeImageXY(new TextImage("Score: " + 26, 20, FontStyle.BOLD, Color.BLACK), 500,
        430);
    emptyScene.placeImageXY(new TextImage("Steps Left: " + 50, 20, FontStyle.BOLD, Color.BLACK),
        500, 460);
    emptyScene.placeImageXY(new TextImage("Time: " + 0, 20, FontStyle.BOLD, Color.BLACK), 500, 490);
    t.checkExpect(emptyScene, new OurWorld(this.shuffledArray1, 26).makeScene());
  }

  // tests for isClicked
  void testIsClicked(Tester t) {
    t.checkExpect(this.oneClubs.isClicked(new Posn(50, 350)), false);
    t.checkExpect(this.oneClubs.isClicked(new Posn(700, 700)), false);
    t.checkExpect(this.oneClubs.isClicked(new Posn(710, 360)), true);
  }

  // tests for CheckCards
  void testCheckCards(Tester t) {
    t.checkExpect(this.oneClubs.checkCards(this.oneClubs), false); // same card
    t.checkExpect(this.oneClubs.checkCards(this.eightClubs), false); // different cards
    t.checkExpect(this.oneClubs.checkCards(this.oneHearts), false); // same rack different colors
    t.checkExpect(this.oneDiamonds.checkCards(this.oneSpades), false); // same rank different colors
    t.checkExpect(this.oneHearts.checkCards(this.oneDiamonds), true); // same rank and color
    t.checkExpect(this.oneClubs.checkCards(this.oneSpades), true); // same rank and color
  }

  // tests for SameColor
  void testSameColor(Tester t) {
    t.checkExpect(this.oneClubs.sameColor(this.twoClubs), false); // same suit
    t.checkExpect(this.oneClubs.sameColor(this.twoHearts), false);
    t.checkExpect(this.oneClubs.sameColor(this.twoDiamonds), false);
    t.checkExpect(this.oneClubs.sameColor(this.twoSpades), true);
  }

  // tests for OnMousePressed
  void testOnMousePressed(Tester t) {
    // case where flippedDeck is empty
    OurWorld world1 = new OurWorld(true);
    world1.onMousePressed(new Posn(50, 50));
    t.checkExpect(world1.flippedDeck.get(0), new Card(1, "♣", true, new Posn(50, 50)));
    // case where flippedDeck has a size of 2 (card is not added, shows because size
    // remains 2)
    world1.flippedDeck.add(this.eightClubs);
    world1.onMousePressed(new Posn(50, 150));
    t.checkExpect(world1.flippedDeck.size(), 2);

  }

  OurWorld w1;
  OurWorld w2;
  OurWorld w3;
  OurWorld w4;
  ArrayList<Card> fd1;
  ArrayList<Card> fd2;
  ArrayList<Card> fd4;

  ArrayList<Card> newDeck;

  // sets data for Tick test
  void setTickTest() {
    w1 = new OurWorld(this.u.makeDeck(), 10, null, 30, 0);
    w2 = new OurWorld(this.u.makeDeck(), 10, null, 30, 0);
    w3 = new OurWorld(this.u.makeDeck(), 10, new ArrayList<Card>(), 30, 0);
    w4 = new OurWorld(this.u.makeDeck(), 10, null, 10, 0);

    fd1 = new ArrayList<Card>(Arrays.asList(w1.deck.get(0), w1.deck.get(1)));
    fd2 = new ArrayList<Card>(Arrays.asList(w2.deck.get(0), w2.deck.get(13)));
    fd4 = new ArrayList<Card>(Arrays.asList(w4.deck.get(0), w4.deck.get(13)));

    newDeck = this.u.makeDeck();
    newDeck.remove(0);
    newDeck.remove(38);
    w1.flippedDeck = fd1;
    w2.flippedDeck = fd2;
    w4.flippedDeck = fd4;
  }

  // tests for OnTick
  void testOnTick(Tester t) {
    this.setTickTest();
    t.checkExpect(w1, new OurWorld(this.u.makeDeck(), 10, fd1, 30, 0));
    Card a = fd1.get(0);
    Card b = fd1.get(1);
    w1.onTick();
    t.checkExpect(w1, new OurWorld(this.u.makeDeck(), 10, new ArrayList<Card>(), 31, 1, 49));

    t.checkExpect(a.isFlipped, false);
    t.checkExpect(b.isFlipped, false);

    t.checkExpect(w2, new OurWorld(this.u.makeDeck(), 10, fd2, 30, 0));
    Card c = fd2.get(0);
    Card d = fd2.get(1);
    w2.onTick();
    t.checkExpect(w2, new OurWorld(this.u.makeDeck(), 10, new ArrayList<Card>(), 31, 1, 49));
    t.checkExpect(c.isFlipped, false);
    t.checkExpect(d.isFlipped, false);

    t.checkExpect(w3, new OurWorld(this.u.makeDeck(), 10, new ArrayList<Card>(), 30, 0));
    w3.onTick();
    t.checkExpect(w3, new OurWorld(this.u.makeDeck(), 10, new ArrayList<Card>(), 31, 1, 50));

    t.checkExpect(w4, new OurWorld(this.u.makeDeck(), 10, fd4, 10, 0));
    w4.onTick();
    t.checkExpect(w4, new OurWorld(this.u.makeDeck(), 10, fd4, 11, 1));
  }

  // test for LastScene
  void testLastScene(Tester t) {
    WorldScene scene = new WorldScene(1000, 500);
    scene.placeImageXY(new TextImage("You win!", Color.GREEN), 500, 250);
    t.checkExpect(this.shuffledWorld.lastScene("You win!"), scene);
  }
}
