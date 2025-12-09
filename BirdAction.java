import java.util.*;
import java.util.function.Consumer;

public enum BirdAction implements BirdActionInterface
{
	// USED THIS LINK TO VIEW SOME BIRD ABILITIES. CAN USE BUT NEED TO DESELECT ALL EXPANSIONS AND PROMO PACKS
	// https://navarog.github.io/wingsearch/

	// This ability has all players draw 1 bird card from the deck WHEN ACTIVATED
	// CANVASBACK | NORTHERN_SHOVELER | PURPLE_GALLINULE | SPOTTED_SANDPIPER | WILSONS_SNIPE
	ALLDRAW1BIRD((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addBirdHand(gameContext.pullRandomBirds(1).get(0), gameContext);

		gameContext.getPanel().promptPlayer("All players drew a bird card!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// All players gain 1 berry
	// BLACK_CHINNED_HUMMINGBIRD
	ALLGET1BERRY((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addFood("berry", 1, gameContext);

		gameContext.getPanel().promptPlayer("All players gained a berry!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// All players gain 1 fish
	// OSPREY
	ALLGET1FISH((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addFood("fish", 1, gameContext);

		gameContext.getPanel().promptPlayer("All players gained a fish!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// All players gain 1 seed
	// RED_CROSSBILL
	ALLGET1SEED((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addFood("seed", 1, gameContext);

		gameContext.getPanel().promptPlayer("All players gained a seed!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// All players gain 1 worm
	// EASTERN_PHOEBE | SCISSOR_TAILED_FLYCATCHER
	ALLGET1WORM((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addFood("worm", 1, gameContext);

		gameContext.getPanel().promptPlayer("All players gained a worm!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Cache 1 seed from the supply on this bird
	// CAROLINA_CHICKADEE | JUNIPER_TITMOUSE | MOUNTAIN_CHICKADEE | RED_BREASTED_NUTHATCH | WHITE_BREASTED_NUTHATCH
	CACHE1SEED((gameContext, player, birdInstance) -> {
		birdInstance.cacheFood(1);

		gameContext.getPanel().promptPlayer("A seed was cached on " + birdInstance.getName() + "!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// This ability has a player discard an egg from any bird to gain 1 food from the supply INCOMPLETE
	// AMERICAN_CROW | BLACK_CROWNED_NIGHT_HERON | FISH_CROW
	DISCARDEGGANDGAIN1FOOD((gameContext, player, birdInstance) -> {
		// UI has the player choose what egg to remove and which food to gain
		// really can't bs the method for now just will be blank

		gameContext.getPanel().promptPlayerFood("Which food would you like?", (food) -> {
					player.addFood(food, 1, gameContext);
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Discard 1 egg from any other bird to gain 2 of any food from the supply; same as one above but 2 foods INCOMPLETE
	// CHIHUAHUAN_RAVEN | COMMON_RAVEN
	DISCARDEGGANDGAIN2FOOD((gameContext, player, birdInstance) -> {
		// UI has the player choose what egg to remove and which food to gain
		// really can't bs the method for now just will be blank

		gameContext.getPanel().promptPlayerFood("Which food would you like?", (food) -> {
			player.addFood(food, 1, gameContext);
			gameContext.getPanel().promptPlayerFood("Which second food would you like?", (food2) -> {
				player.addFood(food2, 1, gameContext);
				gameContext.askPlayerAboutActivatingAbility();
			});
		});
	}),
	// Discard 1 egg to draw 2 bird cards INCOMPLETE
	// FRANKLINS_GULL | KILLDEER
	DISCARDEGGANDGAIN2BIRDS((gameContext, player, birdInstance) -> {
		// UI has the player choose what egg to remove; for now it'll just remove from this bird


		BirdInstance bird = birdInstance;
		if(bird.removeEggs(1)) {
			ArrayList<Bird> birds = gameContext.pullRandomBirds(2);
			for(Bird b: birds)
				player.addBirdHand(b, gameContext);
		}

		gameContext.askPlayerAboutActivatingAbility();
	}),
	// Discard 1 fish to tuck 2 bird cards from the deck behind this bird
	// AMERICAN_WHITE_PELICAN | DOUBLE_CRESTED_CORMORANT
	DISCARDFISHANDTUCK2BIRDS((gameContext, player, birdInstance) -> {
		gameContext.getPanel().promptPlayer("Would you like to discard a fish to tuck 2 bird cards?", "Yes", "No", (y) -> {
			if(y) {
				if (player.removeFood("fish", 1, gameContext)) {
					gameContext.pullRandomBirds(2); // should auto remove the birds from deck, no need to track which ones exactly
					birdInstance.tuckCard(2);
				}
			}
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Discard 1 seed to tuck 2 bird cards from the deck behind this bird
	// BLACK_BELLIED_WHISTLING_DUCK | CANADA_GOOSE | SANDHILL_CRANE
	DISCARDSEEDANDTUCK2BIRDS((gameContext, player, birdInstance) -> {
		gameContext.getPanel().promptPlayer("Would you like to discard a seed to tuck 2 bird cards?", "Yes", "No", (y) -> {
			if(player.removeFood("seed", 1, gameContext)) {
				gameContext.pullRandomBirds(2); // should auto remove the birds from deck, no need to track which ones exactly
				birdInstance.tuckCard(2);
			}
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Draw 1 bird card; super simple
	// MALLARD
	DRAW1BIRD((gameContext, player, birdInstance) -> {
		ArrayList<Bird> cards = gameContext.pullRandomBirds(1);
		for(Bird b: cards) // just makes sure it is an actual list
			player.addBirdHand(b, gameContext);

		gameContext.getPanel().promptPlayer("You drew a " + cards.get(0).getName() +"!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	/* IGNORE NO LONGER IN GAME
	// This ability has the table draw bird cards equal to num of players + 1 and go clockwise from player who played it. Each plaeyr selects 1 of the cards and places in their hand
	// with the player who activated it keeping the extra
	// AMERICAN_OYSTERCATCHER
	DRAWBIRDEQUALTOPLAYERANDCLOCKWISEDISTRIBUTE((gameContext, player, birdInstance) -> {
		// I not do this now too complicated
	}),
	*/
	// This ability checks for the player(s) with the fewest bird in the wetlands and has them draw 1 bird card
	// AMERICAN_BITTERN | COMMON_LOON
	DRAW1BIRDIFLEASTWETLAND((gameContext, player, birdInstance) -> {
		int leastAmount = 6;
		ArrayList<Player> players = gameContext.getPlayers();

		for(Player p: players)
			if (leastAmount > p.getBoard().get("wetland").size())
				leastAmount = p.getBoard().get("wetland").size();

		for(Player p: players)
			if (leastAmount == p.getBoard().get("wetland").size())
				p.addBirdHand(gameContext.pullRandomBirds(1).get(0), gameContext);

		gameContext.askPlayerAboutActivatingAbility();
	}),
	// Draw 2 bird cards
	// BLACK_NECKED_STILT | CAROLINA_WREN
	DRAW2BIRDCARDS((gameContext, player, birdInstance) -> {
		ArrayList<Bird> cards = gameContext.pullRandomBirds(2);
		for(Bird b: cards) {
			player.addBirdHand(b, gameContext);
		}
		gameContext.getPanel().promptPlayer("You drew a " + cards.get(0).getName() + "!", "Ok", null, (b) ->
				gameContext.getPanel().promptPlayer("You drew a " + cards.get(1).getName() + "!", "Ok", null, (a) ->
						gameContext.askPlayerAboutActivatingAbility(), cards.get(1)), cards.get(0));
	}),
	// This ability draws 2 bonus cards for the player and keep 1 WHEN PLAYED
	// ATLANTIC_PUFFIN | BELLS_VIREO | CALIFORNIA_CONDOR | CASSINS_FINCH | CERULEAN_WARBLER | CHESTNUT_COLLARED_LONGSPUR | GREATER_PRAIRIE_CHICKEN | KING_RAIL | PAINTED_BUNTING
	// RED_COCKADED_WOODPECKER | ROSEATE_SPOONBILL | SPOTTED_OWL | SPRAGUES_PIPIT | WHOOPING_CRANE | WOOD_STORK
	DRAW2BONUSKEEP1((gameContext, player, birdInstance) -> {
		ArrayList<BonusCard> cards = gameContext.pullRandomBonusCards(2);
		BonusCard card1 = cards.get(0);
		BonusCard card2 = cards.get(1);
		gameContext.getPanel().promptPlayerBonus("Which bonus card would you like to keep?", card1, card2, (bonus) -> {
			player.addBonusHand(bonus, gameContext);
			gameContext.askPlayerAboutActivatingAbility();
		});

	}),
	// Look at a bird card from deck (face down pile) and if less than 50 cm wingpsan, tuck it behind card, if not discard
	// GREATER_ROADRUNNER
	DRAW1BIRDANDTUCKIF50CM((gameContext, player, birdInstance) -> {
		Bird card = gameContext.pullRandomBirds(1).get(0);
		if(card.getWingspan() < 50) {
			birdInstance.tuckCard(1);
			gameContext.getPanel().promptPlayer("You succeeded in tucking a bird card behind!", "Ok", null, (y) -> {
				gameContext.pinkAbilityActivation("ifPredatorSucceeds");
			});
		}
		else {
			gameContext.getPanel().promptPlayer("You failed in tucking a bird card behind!", "Ok", null, (y) -> {
				gameContext.askPlayerAboutActivatingAbility();
			});
		}
	}),
	// Look at a bird card from deck (face down pile) and if less than 75 cm wingpsan, tuck it behind card, if not discard
	// BARRED_OWL | COOPERS_HAWK | NORTHERN_HARRIER | RED_SHOULDERED_HAWK | RED_TAILED_HAWK | SWAINSONS_HAWK
	DRAW1BIRDANDTUCKIF75CM((gameContext, player, birdInstance) -> {
		Bird card = gameContext.pullRandomBirds(1).get(0);
		if(card.getWingspan() < 75) {
			birdInstance.tuckCard(1);
			gameContext.getPanel().promptPlayer("You succeeded in tucking a bird card behind!", "Ok", null, (y) -> {
				gameContext.pinkAbilityActivation("ifPredatorSucceeds");
			});
		}
		else {
			gameContext.getPanel().promptPlayer("You failed in tucking a bird card behind!", "Ok", null, (y) -> {
				gameContext.askPlayerAboutActivatingAbility();
			});
		}
	}),
	// Look at a bird card from deck (face down pile) and if less than 100 cm wingpsan, tuck it behind card, if not discard
	// GOLDEN_EAGLE | GREAT_HORNED_OWL | PEREGRINE_FALCON
	DRAW1BIRDANDTUCKIF100CM((gameContext, player, birdInstance) -> {
		Bird card = gameContext.pullRandomBirds(1).get(0);
		if(card.getWingspan() < 100) {
			birdInstance.tuckCard(1);
			gameContext.getPanel().promptPlayer("You succeeded in tucking a bird card behind!", "Ok", null, (y) -> {
				gameContext.pinkAbilityActivation("ifPredatorSucceeds");
			});
		}
		else {
			gameContext.getPanel().promptPlayer("You failed in tucking a bird card behind!", "Ok", null, (y) -> {
				gameContext.askPlayerAboutActivatingAbility();
			});
		}
	}),
	// Draw the 3 face up bird cards in the bird tray
	// BRANT
	DRAW3FACEUPBIRD((gameContext, player, birdInstance) -> {
		ArrayList<Bird> cards = new ArrayList<>(gameContext.getFaceUpTray());
		for(int i = 0; i < 3; ++i)
			gameContext.grabFaceUpCard(i, player);

		gameContext.getPanel().promptPlayer("You drew a " + cards.get(0).getName() + "!", "Ok", null, (b) -> {
				gameContext.getPanel().promptPlayer("You drew a " + cards.get(1).getName() + "!", "Ok", null, (a) -> {
						gameContext.getPanel().promptPlayer("You drew a " + cards.get(2).getName() + "!", "Ok", null, (c) -> {
							gameContext.askPlayerAboutActivatingAbility();
						}, cards.get(2));
				}, cards.get(1));
		}, cards.get(0));
		// Not sure if restore the faceuptray within this method or at the end of player turn
		// I think player turn because it'll be more consistent logic
		gameContext.askPlayerAboutActivatingAbility();
	}),
	// This ability allows for the player to gain 1 seed if there is one in the birdFeeder. If it is available, cache it on the bird
	// ACORN_WOODPECKER | BLUE_JAY | CLARKS_NUTCRACKER | RED_BELLIED_WOODPECKER | RED_HEADED_WOODPECKER | STELLERS_JAY
	GAIN1SEEDANDCACHE((gameContext, player, birdInstance) -> {
		if(gameContext.grabFood("seed", player, 1)) // this method auto adds the food into player
		{
			// need some UI prompt to ask the player whether they want to cache the food or not; false for now
            gameContext.getPanel().promptPlayer("Would you like to cache the seed on the bird?", "Yes", "No", (y) -> {
                if (y) {
                    if(player.removeFood("seed", 1, gameContext))
                        birdInstance.cacheFood(1);
                }
				gameContext.askPlayerAboutActivatingAbility();
            });
		} else gameContext.askPlayerAboutActivatingAbility();
	}),
	// This ability allows the player to grab 1 food from the birdFeeder
	// AMERICAN_REDSTART
	GET1FOODBIRDFEEDER((gameContext, player, birdInstance) -> {
		// UI has the player select which food they want. For now it will be the first food in the feeder
		if (!gameContext.getBirdFeeder().isEmpty())
			gameContext.getPanel().promptPlayerFood("Which food would you like to grab from the bird feeder?", (food) -> {
				gameContext.grabFood(food, player, 1);
				gameContext.askPlayerAboutActivatingAbility();
			}, gameContext.getBirdFeeder());
		else gameContext.askPlayerAboutActivatingAbility();
	}),
	// This ability allows the player to gain 1 berry WHEN ACTIVATED
	// BALTIMORE_ORIOLE | NORTHERN_CARDINAL
	GET1BERRY((gameContext, player, birdInstance) -> {
		player.addFood("berry", 1, gameContext);
		gameContext.getPanel().promptPlayer("You gained a berry!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// This ability allows the player to gain 1 seed WHEN ACTIVATED
	// SPOTTED_TOWHEE
	GET1SEED((gameContext, player, birdInstance) -> {
		player.addFood("seed", 1, gameContext);
		gameContext.getPanel().promptPlayer("You gained a seed!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Gain 1 worm
	// BLUE_GRAY_GNATCATCHER | PAINTED_WHITESTART | YELLOW_BELLIED_SAPSUCKER
	GET1WORM((gameContext, player, birdInstance) -> {
		player.addFood("worm", 1, gameContext);
		gameContext.getPanel().promptPlayer("You gained a worm!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Gain 1 worm from birdfeeder if available
	// GREAT_CRESTED_FLYCATCHER
	GET1WORMINBIRDFEEDER((gameContext, player, birdInstance) -> {
		if (gameContext.grabFood("worm", player, 1))
			gameContext.getPanel().promptPlayer("You gained a worm!", "Ok", null, (y) -> {
				gameContext.askPlayerAboutActivatingAbility();
			});
		else
			gameContext.getPanel().promptPlayer("You failed to gain a worm!", "Ok", null, (y) -> {
				gameContext.askPlayerAboutActivatingAbility();
			});
	}),
	// This ability allows the player to gain 3 fish WHEN PLAYED
	// BROWN_PELICAN
	GET3FISH((gameContext, player, birdInstance) -> {
		player.addFood("fish", 3, gameContext);
		gameContext.getPanel().promptPlayer("You gained 3 fish!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// This ability allows the player to get 3 wheat from the supply
	// AMERICAN_GOLDFINCH
	GET3SEED((gameContext, player, birdInstance) -> {
		player.addFood("seed", 3, gameContext);
		gameContext.getPanel().promptPlayer("You gained 3 seed!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Gain 1 seed or berry from birdFeeder if available
	// INDIGO_BUNTING | ROSE_BREASTED_GROSBEAK | WESTERN_TANAGER
	GET1BERRYOR1SEED((gameContext, player, BirdInstance) -> {
		// UI shows the birdFeeder and has them choose which food they want, for now it just chooses berry and if no berry, then worm

		ArrayList<String> birdFeeder = new ArrayList<>();
		for(String s : gameContext.getBirdFeeder())
			if(s.equalsIgnoreCase("seed") || s.equalsIgnoreCase("berry"))
				birdFeeder.add(s);

		if(birdFeeder.contains("seed") || birdFeeder.contains("berry"))
			gameContext.getPanel().promptPlayerFood("Which food would you like to grab from the bird feeder?", (food) -> {
				gameContext.grabFood(food, player, 1);
				gameContext.askPlayerAboutActivatingAbility();
			}, birdFeeder);
		else gameContext.getPanel().promptPlayer("There were no seed or berries in the bird feeder.", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// This ability checks for the player(s) with the fewest bird in the forest and has them gain 1 food from birdFeeder
	// HERMIT_THRUSH
	GET1FOODIFLEASTFOREST((gameContext, player, birdInstance) -> {
		int leastAmount = 6;
		ArrayList<Player> players = gameContext.getPlayers();

		for(Player p: players)
			if (leastAmount > p.getBoard().get("forest").size())
				leastAmount = p.getBoard().get("forest").size();

		/* moved to game so we can recursively prompt players, it's kinda hard to do here
		for(Player p: players)
			if (leastAmount == p.getBoard().get("forest").size())
				gameContext.getPanel().promptPlayerFood("Which food would you like to grab from the bird feeder?", (food) -> {
					gameContext.grabFood(food, player, 1);
					gameContext.askPlayerAboutActivatingAbility();
				});*/

		gameContext.askPlayersToGrabFood(new ArrayList<>(players), leastAmount);
	}),
	// Gain all fish that are in the bird feeder
	// BALD_EAGLE
	GETALLFISHINBIRDFEEDER((gameContext, player, birdInstance) -> {
		int count = 0;
		for(String s: gameContext.getBirdFeeder())
			if(s.equals("fish"))
				count++;
		gameContext.grabFood("fish", player, count);
		gameContext.getPanel().promptPlayer("You gained " + count + " fishes!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Gain all worm that are in the bird feeder
	// NORTHERN_FLICKER
	GETALLWORMINBIRDFEEDER((gameContext, player, birdInstance) -> {
		int count = 0;
		for(String s: gameContext.getBirdFeeder())
			if(s.equals("worm"))
				count++;
		gameContext.grabFood("worm", player, count);
		gameContext.getPanel().promptPlayer("You gained " + count + " worms!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Lays 1 egg on each of your birds with a bowl nest
	// SAYS_PHOEBE
	LAYEGGONALLBOWL((gameContext, player, birdInstance) -> {
		//ArrayList of every bird on the board
		ArrayList<BirdInstance> birdSuperList = new ArrayList<>();

		//iterates through the player board, combining the habitats into one ArrayList
		for (ArrayList<BirdInstance> birdList: player.getBoard().values())
			birdSuperList.addAll(birdList);

		//iterates through all cards on the board, adding eggs to birds with correct nest
		for (BirdInstance birdCard: birdSuperList) {
			if(birdCard.getNest().equalsIgnoreCase("Bowl"))
			birdCard.addEggs(1);
		}

		gameContext.getPanel().promptPlayer("You laid an egg on all birds with a bowl nest!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Lays 1 egg on each of your birds with a cavity nest
	// ASH_THROATED_FLYCATCHER
	LAYEGGONALLCAVITY((gameContext, player, birdInstance) -> {
		//ArrayList of every bird on the board
		ArrayList<BirdInstance> birdSuperList = new ArrayList<>();

		//iterates through the player board, combining the habitats into one ArrayList
		for (ArrayList<BirdInstance> birdList: player.getBoard().values())
			birdSuperList.addAll(birdList);

		//iterates through all cards on the board, adding eggs to birds with correct nest
		for (BirdInstance birdCard: birdSuperList) {
			if(birdCard.getNest().equalsIgnoreCase("Cavity"))
			birdCard.addEggs(1);
		}

		gameContext.getPanel().promptPlayer("You laid an egg on all birds with a cavity nest!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Lays 1 egg on each of your birds with a ground nest
	// BOBOLINK
	LAYEGGONALLGROUND((gameContext, player, birdInstance) -> {
		//ArrayList of every bird on the board
		ArrayList<BirdInstance> birdSuperList = new ArrayList<>();

		//iterates through the player board, combining the habitats into one ArrayList
		for (ArrayList<BirdInstance> birdList: player.getBoard().values())
			birdSuperList.addAll(birdList);

		//iterates through all cards on the board, adding eggs to birds with correct nest
		for (BirdInstance birdCard: birdSuperList) {
			if(birdCard.getNest().equalsIgnoreCase("Ground"))
			birdCard.addEggs(1);
		}

		gameContext.getPanel().promptPlayer("You laid an egg on all birds with a ground nest!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Lays 1 egg on each of your birds with a platform nest
	// INCA_DOVE
	LAYEGGONALLPLATFORM((gameContext, player, birdInstance) -> {
		//ArrayList of every bird on the board
		ArrayList<BirdInstance> birdSuperList = new ArrayList<>();

		//iterates through the player board, combining the habitats into one ArrayList
		for (ArrayList<BirdInstance> birdList: player.getBoard().values())
			birdSuperList.addAll(birdList);

		//iterates through all cards on the board, adding eggs to birds with correct nest
		for (BirdInstance birdCard: birdSuperList) {
			if(birdCard.getNest().equalsIgnoreCase("Platform"))
			birdCard.addEggs(1);
		}

		gameContext.getPanel().promptPlayer("You laid an egg on all birds with a platform nest!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Lay 1 egg on any bird of player choosing
	// BAIRDS_SPARROW | CASSINS_SPARROW | CHIPPING_SPARROW | GRASSHOPPER_SPARROW
	LAYEGGONANYBIRD((gameContext, player, birdInstance) -> {
		// need UI to ask player which bird, for now will just place on this bird
		gameContext.getPanel().promptPlayerLayEggs(gameContext.getPlayerIndex(player), "Choose which bird to lay an egg on.", 1, () -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// Lay 1 egg on this bird
	// CALIFORNIA_QUAIL | MOURNING_DOVE | NORTHERN_BOBWHITE | SCALED_QUAIL
	LAYEGGONTHISBIRD((gameContext, player, birdInstance) -> {
		birdInstance.addEggs(1);
		gameContext.getPanel().promptPlayer("You laid an egg on " + birdInstance.getName() + "!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// If this bird is to the right of all other birds in its habitat move it to another habitat
	// BEWICKS_WREN | BLUE_GROSBEAK | CHIMNEY_SWIFT | COMMON_NIGHTHAWK | LINCOLNS_SPARROW | SONG_SPARROW | WHITE_CROWNED_SPARROW | YELLOW_BREASTED_CHAT
	MOVEIFATVERYRIGHT((gameContext, player, birdInstance) -> {
		String habitat = birdInstance.getCurrentHabitat();
		ArrayList<BirdInstance> birdInstances = player.getBoard().get(habitat);

		if(birdInstances.get(birdInstances.size()-1) == birdInstance)
		{
			gameContext.getPanel().promptPlayer("Would you like to move habitats?", "Yes", "No", (y) -> {
				if (y)
				{
					String habitat1 = "";
					String habitat2 = "";

					if (habitat.equalsIgnoreCase("forest")) { habitat1 = "grassland"; habitat2 = "wetland"; }
					else if (habitat.equalsIgnoreCase("grassland")) { habitat1 = "forest"; habitat2 = "wetland"; }
					else if (habitat.equalsIgnoreCase("wetland")) { habitat1 = "forest"; habitat2 = "grassland"; }
					else System.out.println("MOVEIFATVERYRIGHT BIRD ACTION BROKE SOMEHOW GGS");

					gameContext.getPanel().promptPlayerHabitat(gameContext.getPlayerIndex(player), "Which habitat would you like to move to?", (choice) -> {
						String newHabitat = choice;
						BirdInstance bird = birdInstances.get(birdInstances.size() - 1);
						player.getBoard().get(newHabitat).add(bird);
						bird.setCurrentHabitat(newHabitat);
						birdInstances.remove(birdInstance);
						gameContext.getPanel().removeFromPlayerBoard(bird);
						gameContext.getPanel().addToPlayerBoard(gameContext.getPlayerIndex(player), birdInstance, newHabitat);
						gameContext.askPlayerAboutActivatingAbility();
					}, List.of(new String[]{habitat1, habitat2}));
				} else gameContext.askPlayerAboutActivatingAbility();
			});
		} else gameContext.askPlayerAboutActivatingAbility();
	}),
	// This ability is for birds with no ability. My favourite.
	// AMERICAN_WOODCOCK | BLUE_WINGED_WARBLER | HOODED_WARBLER | PROTHONOTARY_WARBLER | TRUMPETER_SWAN | WILD_TURKEY
	NONE((gameContext, player, birdInstance) -> {
		gameContext.askPlayerAboutActivatingAbility();
	}),
	// Play an additional bird in forest
	// DOWNY_WOODPECKER

	// Play an additional bird in grassland
	// EASTERN_BLUEBIRD

	// Play an additional bird in wetland

	// Repeats a brown ability in the same habitat

	// Rolls all the dice not in the birdFeeder and if any are fish, cache 1 fish into the supply of the bird
	// ANHINGA | BLACK_SKIMMER | COMMON_MERGANSER | SNOWY_EGRET | WHITE_FACED_IBIS | WILLET
	ROLLDICEANDFINDFISH((gameContext, player, birdInstance) -> {
		String[] foods = {"berry", "fish", "rat", "seed", "worm", "seed/worm"};
		ArrayList<String> rolledFoods = new ArrayList<>();
		for(int i = 0; i < (5-gameContext.getBirdFeeder().size()); ++i)
		{
			int randFood = (int) (Math.random() * foods.length);
			rolledFoods.add(foods[randFood]);
		}
		if(rolledFoods.contains("fish"))
		{
			birdInstance.cacheFood(1);
			gameContext.getPanel().promptPlayer("You cached a fish!", "Ok", null, (y) -> {
				gameContext.pinkAbilityActivation("ifPredatorSucceeds");
			});
		}
		else gameContext.getPanel().promptPlayer("You failed to cache a fish!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});


	}),
	// Rolls all the dice not in the birdFeeder and if any are rat, cache 1 rat into the supply of the bird
	// AMERICAN_KESTREL | BARN_OWL | BROAD_WINGED_HAWK | BURROWING_OWL | EASTERN_SCREECH_OWL | FERRUGINOUS_HAWK | MISSISSIPPI_KITE
	ROLLDICEANDFINDRAT((gameContext, player, birdInstance) -> {
		String[] foods = {"berry", "fish", "rat", "seed", "worm", "seed/worm"};
		ArrayList<String> rolledFoods = new ArrayList<>();
		for(int i = 0; i < (5-gameContext.getBirdFeeder().size()); ++i)
		{
			int randFood = (int) (Math.random() * foods.length);
			rolledFoods.add(foods[randFood]);
		}
		if(rolledFoods.contains("rat")) {
			birdInstance.cacheFood(1);
			gameContext.getPanel().promptPlayer("You cached a fish!", "Ok", null, (y) -> {
				gameContext.pinkAbilityActivation("ifPredatorSucceeds");
			});
		}
		else gameContext.getPanel().promptPlayer("You failed to cache a fish!", "Ok", null, (y) -> {
			gameContext.askPlayerAboutActivatingAbility();
		});
	}),
	// trade 1 of any type of food for any other type from the supply
	// GREEN_HERON
	TRADE1FOODFOR1OTHERFOOD((gameContext, player, birdInstance) -> {
		// UI has the player choose what food they give up for what food they want ; for now it'll just switch seed for worm
		gameContext.getPanel().promptPlayerFood("Which food would you like to trade in?", (choice) -> {
			player.removeFood(choice, 1, gameContext);
			gameContext.getPanel().promptPlayerFood("Which food would you like to receive?", (newTrade) -> {
				player.addFood(newTrade, 1, gameContext);
				gameContext.askPlayerAboutActivatingAbility();
			});
		});
	}),
	// This ability allows a player to tuck a bird card behind the bird and if done, draw 1 bird card
	// AMERICAN_ROBIN | AMERICAN_COOT | BARN_SWALLOW | HOUSE_FINCH | PURPLE_MARTIN | RING_BILLED_GULL | TREE_SWALLOW | VIOLET_GREEN_SWALLOW | YELLOW_RUMPED_WARBLER
	TUCK1BIRDANDDRAW1BIRD((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck
        gameContext.getPanel().promptPlayer("Would you like to tuck a bird card behind " + birdInstance.getName() + " and draw a bird afterwards?", "Yes", "No", (y) -> {
            if(y && !player.getBirdHand().isEmpty())  {
                // now here has to be more UI asking for which bird card to remove
				ArrayList<Card> list = new ArrayList<>(player.getBirdHand());
				gameContext.getPanel().promptPlayerBirdCard(list, (Consumer<Bird>) (bird) -> {
					player.removeBirdCard(bird, gameContext);
					birdInstance.tuckCard(1);
					player.addBirdHand(gameContext.pullRandomBirds(1).getFirst(), gameContext);
					gameContext.askPlayerAboutActivatingAbility();
				});
            } else gameContext.askPlayerAboutActivatingAbility();
        });
	}),
	// tuck 1 bird card from your hand behind this bird and if you do gain 1 berry
	// CEDAR_WAXWING
	TUCK1BIRDANDGET1BERRY((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck
        gameContext.getPanel().promptPlayer("Would you like to tuck a bird card behind " + birdInstance.getName() + " and gain a berry afterwards?", "Yes", "No", (y) -> {
            if(y && !player.getBirdHand().isEmpty())  {
                // now here has to be more UI asking for which bird card to remove
				ArrayList<Card> list = new ArrayList<>(player.getBirdHand());
				gameContext.getPanel().promptPlayerBirdCard(list, (Consumer<Bird>) (bird) -> {
					player.removeBirdCard(bird, gameContext);
					birdInstance.tuckCard(1);
					player.addFood("berry", 1, gameContext);
					gameContext.askPlayerAboutActivatingAbility();
				});
            } else gameContext.askPlayerAboutActivatingAbility();
        });
	}),
	// tuck 1 bird card from your hand behind this bird and if you do gain 1 seed
	// DARK_EYED_JUNCO | PINE_SISKIN
	TUCK1BIRDANDGET1SEED((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
        gameContext.getPanel().promptPlayer("Would you like to tuck a bird card behind " + birdInstance.getName() + " and gain a seed afterwards?", "Yes", "No", (y) -> {
            if(y && !player.getBirdHand().isEmpty())  {
                // now here has to be more UI asking for which bird card to remove
				ArrayList<Card> list = new ArrayList<>(player.getBirdHand());
				gameContext.getPanel().promptPlayerBirdCard(list, (Consumer<Bird>) (bird) -> {
					player.removeBirdCard(bird, gameContext);
					birdInstance.tuckCard(1);
					player.addFood("seed", 1, gameContext);
					gameContext.askPlayerAboutActivatingAbility();
				});
            } else gameContext.askPlayerAboutActivatingAbility();
        });
	}),
	// tuck 1 bird card from your hand behind this bird and if you do gain 1 seed or worm of your choose
	// PYGMY_NUTHATCH
	TUCK1BIRDANDGET1SEEDORWORM((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
        gameContext.getPanel().promptPlayer("Would you like to tuck a bird card behind " + birdInstance.getName() + " and gain a berry or seed afterwards?", "Yes", "No", (y) -> {
            if(y && !player.getBirdHand().isEmpty())  {
                // now here has to be more UI asking for which bird card to remove
				ArrayList<Card> list = new ArrayList<>(player.getBirdHand());
				gameContext.getPanel().promptPlayerBirdCard(list, (Consumer<Bird>) (bird) -> {
					player.removeBirdCard(bird, gameContext);
					birdInstance.tuckCard(1);
					gameContext.getPanel().promptPlayerFood("Which food would you like?", (food) -> {
						player.addFood(food, 1, gameContext);
						gameContext.askPlayerAboutActivatingAbility();
					}, List.of(new String[]{"seed", "worm"}));
				});
            } else gameContext.askPlayerAboutActivatingAbility();
        });
	}),
	// tuck 1 bird card from your hand behind this bird and if you do gain 1 worm
	// VAUXS_SWIFT
	TUCK1BIRDANDGET1WORM((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
        gameContext.getPanel().promptPlayer("Would you like to tuck a bird card behind " + birdInstance.getName() + " and gain a seed afterwards?", "Yes", "No", (y) -> {
            if(y && !player.getBirdHand().isEmpty())  {
                // now here has to be more UI asking for which bird card to remove
				ArrayList<Card> list = new ArrayList<>(player.getBirdHand());
				gameContext.getPanel().promptPlayerBirdCard(list, (Consumer<Bird>) (bird) -> {
					player.removeBirdCard(bird, gameContext);
					birdInstance.tuckCard(1);
					player.addFood("worm", 1, gameContext);
					gameContext.askPlayerAboutActivatingAbility();
				});
            } else gameContext.askPlayerAboutActivatingAbility();
        });
	}),
	// tuck 1 bird from hand behind the bird and if done, lay 1 egg on this bird
	// BREWERS_BLACKBIRD | BUSHTIT | COMMON_GRACKLE | DICKCISSEL | RED_WINGED_BLACKBIRD | WHITE_THROATED_SWIFT | YELLOW_HEADED_BLACKBIRD
	TUCK1BIRDANDLAY1EGG((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
        gameContext.getPanel().promptPlayer("Would you like to tuck a bird card behind " + birdInstance.getName() + " and gain a seed afterwards?", "Yes", "No", (y) -> {
            if(y && !player.getBirdHand().isEmpty())  {
                // now here has to be more UI asking for which bird card to remove
				ArrayList<Card> list = new ArrayList<>(player.getBirdHand());
				gameContext.getPanel().promptPlayerBirdCard(list, (Consumer<Bird>) (bird) -> {
					player.removeBirdCard(bird, gameContext);
					birdInstance.tuckCard(1);
					birdInstance.addEggs(1);
					gameContext.askPlayerAboutActivatingAbility();
				});
            } else gameContext.askPlayerAboutActivatingAbility();
        });
	}),
	// FROM NOW ON ARE PINK BIRD ABILITIES
	// when player plays a bird in the forest, gain 1 worm from supply
	// ticks at game addBirdToBoard method
	// EASTERN_KINGBIRD
	PLAYFORESTANDGAIN1WORM((gameContext, player, birdInstance) -> {
		if(!birdInstance.checkPlayedThisTurn())
			player.addFood("worm", 1, gameContext);
		birdInstance.played();
		gameContext.askPlayerAboutActivatingAbility();
	}),
	// when player plays a bird in the grassland, tuck 1 bird from hand
	// ticks at game addBirdToBoard method
	// HORNED_LARK
	PLAYGRASSLANDANDTUCK((gameContext, player, birdInstance) -> {
		if(!birdInstance.checkPlayedThisTurn())
		{
			// UI will have to ask the player to choose a bird card from their hand; for now, empty as if they declined ability
			gameContext.getPanel().promptPlayer("Do you want to trade tuck a bird card from your hand underneath this bird?", "Yes", "No", (y) -> {
				ArrayList<Card> list = new ArrayList<>(player.getBirdHand());
				gameContext.getPanel().promptPlayerBirdCard(list, (Consumer<Bird>) (bird) -> {
					player.removeBirdCard(bird, gameContext);
					birdInstance.tuckCard(1);
					birdInstance.played();
					gameContext.askPlayerAboutActivatingAbility();
				});
			});
		} else gameContext.askPlayerAboutActivatingAbility();
	}),
	// when player plays a bird in the wetland, gain 1 fish from supply
	// ticks at game addBirdToBoard method
	// BELTED_KINGFISHER
	PLAYWETLANDANDGAIN1FISH((gameContext, player, birdInstance) -> {
		if(!birdInstance.checkPlayedThisTurn())
			player.addFood("fish", 1, gameContext);
		birdInstance.played();
		gameContext.askPlayerAboutActivatingAbility();
	}),
	// when another player's predator ability succeeds, gain 1 food from birdfeeder
	// ticks at birdAction ROLLDICEANDFINDFISH, ROLLDICEANDFINDRAT, and DRAW1BIRDANDTUCKIF___CM; so 5 methods in total
	// BLACK_VULTURE | BLACK_BILLED_MAGPIE | TURKEY_VULTURE
	IFPREDATORSUCCESSGAIN1FOOD((gameContext, player, birdInstance) -> {
		// UI will have to have a prompt that asks the player for which food they want from feeder; for now it'll be rat
		gameContext.getPanel().promptPlayerFood("Which food would you like to grab from the bird feeder?", (food) -> {
			gameContext.grabFood(food, player, 1);
			birdInstance.played();
			gameContext.askPlayerAboutActivatingAbility();
		}, gameContext.getBirdFeeder());
	}),
	/*
// when another player takes the lay egg action, lay an egg on another bird with Bowl nest
// ticks at game layEggs method
// BRONZED_COWBIRD | BROWN_HEADED_COWBIRD | YELLOW_BILLED_CUCKOO
LAYEGGTHENLAYBOWL((gameContext, player, birdInstance) -> {
// first need to check if the player even has a bird with the correct nest
boolean containsNest = false;
List<BirdInstance> birds = player.getBoard().values().stream().flatMap(List::stream).toList();
for(BirdInstance b : birds)
if(b.getNest().equalsIgnoreCase("Bowl"))
containsNest = true;

if(!containsNest) return; // if no nest then just stop the method

// UI should have the player select a bird with the habitat
// unsure whether the UI will ensure it is the correct habitat or not but i will check here too
// completely commented out for now so no inf loop
while (true)
{
BirdInstance bird = ;
if(bird.getNest().equalsIgnoreCase("Bowl"))
{
bird.addEggs(1);
break;
}
}

		birdInstance.played();
		gameContext.askPlayerAboutActivatingAbility();
	}),
	// when another player takes the lay egg action, lay an egg on another bird with Cavity nest
	// ticks at game layEggs method
	// BARROW'S_GOLDENEYE
	LAYEGGTHENLAYCAVITY((gameContext, player, birdInstance) -> {
		// first need to check if the player even has a bird with the correct nest
		boolean containsNest = false;
		List<BirdInstance> birds = player.getBoard().values().stream().flatMap(List::stream).toList();
		for(BirdInstance b : birds)
			if(b.getNest().equalsIgnoreCase("Cavity"))
				containsNest = true;

		if(!containsNest) return; // if no nest then just stop the method

		// UI should have the player select a bird with the habitat
		// unsure whether the UI will ensure it is the correct habitat or not but i will check here too
		// completely commented out for now so no inf loop

		while (true)
		{
			BirdInstance bird = ;
			if(bird.getNest().equalsIgnoreCase("Cavity"))
			{
				bird.addEggs(1);
				break;
			}
		}

		birdInstance.played();
		gameContext.askPlayerAboutActivatingAbility();
	}),

	// when another player takes the lay egg action, lay an egg on another bird with ground nest
	// ticks at game layEggs method
	// AMERICAN_AVOCET
	LAYEGGTHENLAYGROUND((gameContext, player, birdInstance) -> {
		// first need to check if the player even has a bird with the correct nest
		boolean containsNest = false;
		List<BirdInstance> birds = player.getBoard().values().stream().flatMap(List::stream).toList();
		for(BirdInstance b : birds)
			if(b.getNest().equalsIgnoreCase("Ground"))
				containsNest = true;

		if(!containsNest) return; // if no nest then just stop the method

		// UI should have the player select a bird with the habitat
		// unsure whether the UI will ensure it is the correct habitat or not but i will check here too
		// completely commented out for now so no inf loop
		while (true)
		{
			BirdInstance bird = ;
			if(bird.getNest().equalsIgnoreCase("Ground"))
			{
				bird.addEggs(1);
				break;
			}
		}
		birdInstance.played();
		gameContext.askPlayerAboutActivatingAbility();
	})
*/
	// when another player takes the grab food action and grabs a rat, cache a rat from supply on this bird
	// ticks at game getFood method
	// LOGGERHEAD_SHRIKE
	GETRATTHENCACHERAT((gameContext, player, birdInstance) -> {
		birdInstance.cacheFood(1);
		birdInstance.played();
		gameContext.askPlayerAboutActivatingAbility();
	});


	// stores the action value of each birdAction within the ENUM
	private final BirdActionInterface action;

	// constructor that assigns the variable action to the necessary logic
	private BirdAction(BirdActionInterface action)
	{
		this.action = action;
	}

	// overriding function for BirdActionInterface; just initiates the action of the bird
	@Override
	public void execute(Game gameContext, Player player, BirdInstance birdInstance)
	{
		action.execute(gameContext, player, birdInstance);
	}
}
