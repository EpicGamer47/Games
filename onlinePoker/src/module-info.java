module onlinePoker {
	exports singlePlayer;
	exports pokerEngine;
	exports chess;
	exports test;
	exports onlineEngine;
	exports bjEngine;
	exports multiDealer;

	opens chess to core;
	requires core;
	requires java.desktop;
}