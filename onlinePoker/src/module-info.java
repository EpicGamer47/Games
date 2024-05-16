module onlinePoker {
	exports singlePlayer;
	exports pokerEngine;
	exports TwoPlayer;
	exports test;
	exports onlineEngine;
	exports bjEngine;
	exports multiDealer;

	opens TwoPlayer to core;
	requires core;
	requires java.desktop;
}