import java.util.*;

public enum BirdAction implements BirdActionInterface
{
	// USED THIS LINK TO VIEW SOME BIRD ABILITIES. CAN USE BUT NEED TO DESELECT ALL EXPANSIONS AND PROMO PACKS
	// https://navarog.github.io/wingsearch/

	// This ability has all players draw 1 bird card from the deck WHEN ACTIVATED
	// CANVASBACK | NORTHERN_SHOVELER | PURPLE_GALLINULE | SPOTTED_SANDPIPER | WILSONS_SNIPE
	ALLDRAW1BIRD((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addBirdHand(gameContext.pullRandomBirds(1).get(0));
	}),
	// All players gain 1 berry
	// BLACK_CHINNED_HUMMINGBIRD
	ALLGET1BERRY((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addFood("berry", 1);
	}),
	// All players gain 1 fish
	// OSPREY
	ALLGET1FISH((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addFood("fish", 1);
	}),
	// All players gain 1 seed
	// RED_CROSSBILL
	ALLGET1SEED((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addFood("seed", 1);
	}),
	// All players gain 1 worm
	// EASTERN_PHOEBE | SCISSOR_TAILED_FLYCATCHER
	ALLGET1WORM((gameContext, player, birdInstance) -> {
		for(Player p: gameContext.getPlayers())
			p.addFood("worm", 1);
	}),
	// Cache 1 seed from the supply on this bird
	// CAROLINA_CHICKADEE | JUNIPER_TITMOUSE | MOUNTAIN_CHICKADEE | RED_BREASTED_NUTHATCH | WHITE_BREASTED_NUTHATCH
	CACHE1SEED((gameContext, player, birdInstance) -> {
		birdInstance.cacheFood(1);
	}),
	// This ability has a player discard an egg from any bird to gain 1 food from the supply
	// AMERICAN_CROW | BLACK_CROWNED_NIGHT_HERON | FISH_CROW
	DISCARDEGGANDGAIN1FOOD((gameContext, player, birdInstance) -> {
		// UI has the player choose what egg to remove and which food to gain
		// really can't bs the method for now just will be blank
	}),
	// Discard 1 egg from any other bird to gain 2 of any food from the supply; same as one above but 2 foods
	// CHIHUAHUAN_RAVEN | COMMON_RAVEN
	DISCARDEGGANDGAIN2FOOD((gameContext, player, birdInstance) -> {
		// UI has the player choose what egg to remove and which food to gain
		// really can't bs the method for now just will be blank
	}),
	// Discard 1 egg to draw 2 bird cards
	// FRANKLINS_GULL | KILLDEER
	DISCARDEGGANDGAIN2BIRDS((gameContext, player, birdInstance) -> {
		// UI has the player choose what egg to remove; for now it'll just remove from this bird
		BirdInstance bird = birdInstance;
		if(bird.removeEggs(1)) {
			ArrayList<Bird> birds = gameContext.pullRandomBirds(2);
			for(Bird b: birds)
				player.addBirdHand(b);
		}
	}),
	// Discard 1 fish to tuck 2 bird cards from the deck behind this bird
	// AMERICAN_WHITE_PELICAN | DOUBLE_CRESTED_CORMORANT
	DISCARDFISHANDTUCK2BIRDS((gameContext, player, birdInstance) -> {
		if(player.removeFood("fish", 1)) {
			gameContext.pullRandomBirds(2); // should auto remove the birds from deck, no need to track which ones exactly
			birdInstance.tuckCard(2);
		}
	}),
	// Discard 1 seed to tuck 2 bird cards from the deck behind this bird
	// BLACK_BELLIED_WHISTLING_DUCK | CANADA_GOOSE | SANDHILL_CRANE
	DISCARDSEEDANDTUCK2BIRDS((gameContext, player, birdInstance) -> {
		if(player.removeFood("seed", 1)) {
			gameContext.pullRandomBirds(2); // should auto remove the birds from deck, no need to track which ones exactly
			birdInstance.tuckCard(2);
		}
	}),
	// Draw 1 bird card; super simple
	// MALLARD
	DRAW1BIRD((gameContext, player, birdInstance) -> {
		ArrayList<Bird> cards = gameContext.pullRandomBirds(1); 
		for(Bird b: cards) // just makes sure it is an actual list
			player.addBirdHand(b);
	}),
	// This ability has the table draw bird cards equal to num of players + 1 and go clockwise from player who played it. Each plaeyr selects 1 of the cards and places in their hand
	// with the player who activated it keeping the extra
	// AMERICAN_OYSTERCATCHER
	DRAWBIRDEQUALTOPLAYERANDCLOCKWISEDISTRIBUTE((gameContext, player, birdInstance) -> {
		// I not do this now too complicated
	}),
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
				p.addBirdHand(gameContext.pullRandomBirds(1).get(0));
	}),
	// Draw 2 bird cards
	// BLACK_NECKED_STILT | CAROLINA_WREN
	DRAW2BIRDCARDS((gameContext, player, birdInstance) -> {
		ArrayList<Bird> cards = gameContext.pullRandomBirds(2);
		for(Bird b: cards)
			player.addBirdHand(b);
	}),
	// This ability draws 2 bonus cards for the player and keep 1 WHEN PLAYED 
	// ATLANTIC_PUFFIN | BELLS_VIREO | CALIFORNIA_CONDOR | CASSINS_FINCH | CERULEAN_WARBLER | CHESTNUT_COLLARED_LONGSPUR | GREATER_PRAIRIE_CHICKEN | KING_RAIL | PAINTED_BUNTING
	// RED_COCKADED_WOODPECKER | ROSEATE_SPOONBILL | SPOTTED_OWL | SPRAGUES_PIPIT | WHOOPING_CRANE | WOOD_STORK
	DRAW2BONUSKEEP1((gameContext, player, birdInstance) -> {
		ArrayList<BonusCard> cards = gameContext.pullRandomBonusCards(2);
		BonusCard card1 = cards.get(0);
		BonusCard card2 = cards.get(1);

		// Need UI implementation here to allow the player to choose which card they would like to keep. Cannot continue this ability for now.
		
	}),
	// Look at a bird card from deck (face down pile) and if less than 50 cm wingpsan, tuck it behind card, if not discard
	// GREATER_ROADRUNNER
	DRAW1BIRDANDTUCKIF50CM((gameContext, player, birdInstance) -> {
		Bird card = gameContext.pullRandomBirds(1).get(0);
		if(card.getWingspan() < 50)
			birdInstance.tuckCard(1);
	}),
	// Look at a bird card from deck (face down pile) and if less than 75 cm wingpsan, tuck it behind card, if not discard
	// BARRED_OWL | COOPERS_HAWK | NORTHERN_HARRIER | RED_SHOULDERED_HAWK | RED_TAILED_HAWK | SWAINSONS_HAWK
	DRAW1BIRDANDTUCKIF75CM((gameContext, player, birdInstance) -> {
		Bird card = gameContext.pullRandomBirds(1).get(0);
		if(card.getWingspan() < 75)
			birdInstance.tuckCard(1);
	}),
	// Look at a bird card from deck (face down pile) and if less than 100 cm wingpsan, tuck it behind card, if not discard
	// GOLDEN_EAGLE | GREAT_HORNED_OWL | PEREGRINE_FALCON
	DRAW1BIRDANDTUCKIF100CM((gameContext, player, birdInstance) -> {
		Bird card = gameContext.pullRandomBirds(1).get(0);
		if(card.getWingspan() < 100)
			birdInstance.tuckCard(1);
	}),
	// Draw the 3 face up bird cards in the bird tray
	// BRANT
	DRAW3FACEUPBIRD((gameContext, player, birdInstance) -> {
		for(int i = 0; i < 3; ++i)
			gameContext.grabFaceUpCard(i, player);
		// Not sure if restore the faceuptray within this method or at the end of player turn
		// I think player turn because it'll be more consistent logic
	}),
	// This ability allows for the player to gain 1 seed if there is one in the birdFeeder. If it is available, cache it on the bird
	// ACORN_WOODPECKER | BLUE_JAY | CLARKS_NUTCRACKER | RED_BELLIED_WOODPECKER | RED_HEADED_WOODPECKER | STELLERS_JAY
	GAIN1SEEDANDCACHE((gameContext, player, birdInstance) -> {
		if(gameContext.grabFood("seed", player, 1)) // this method auto adds the food into player
		{
			// need some UI prompt to ask the player whether they want to cache the food or not; false for now
			boolean cached = false;
			if(cached && player.removeFood("seed", 1))
			{
				birdInstance.cacheFood(1);
			}
		}
	}),	
	// This ability allows the player to grab 1 food from the birdFeeder
	// AMERICAN_REDSTART
	GET1FOODBIRDFEEDER((gameContext, player, birdInstance) -> {
		// UI has the player select which food they want. For now it will be the first food in the feeder
		if (!gameContext.getBirdFeeder().isEmpty()) {
			String food = gameContext.getBirdFeeder().get(0);
			player.addFood(food, 1);
		}
	}),
	// This ability allows the player to gain 1 berry WHEN ACTIVATED
	// BALTIMORE_ORIOLE | NORTHERN_CARDINAL
	GET1BERRY((gameContext, player, birdInstance) -> {
		player.addFood("berry", 1);
	}),
	// This ability allows the player to gain 1 seed WHEN ACTIVATED
	// SPOTTED_TOWHEE
	GET1SEED((gameContext, player, birdInstance) -> {
		player.addFood("seed", 1);
	}),
	// Gain 1 worm 
	// BLUE_GRAY_GNATCATCHER | PAINTED_WHITESTART | YELLOW_BELLIED_SAPSUCKER
	GET1WORM((gameContext, player, birdInstance) -> {
		player.addFood("worm", 1);
	}),
	// Gain 1 worm from birdfeeder if available
	// GREAT_CRESTED_FLYCATCHER
	GET1WORMINBIRDFEEDER((gameContext, player, birdInstance) -> {
		gameContext.grabFood("worm", player, 1);
	}),
	// This ability allows the player to gain 3 fish WHEN PLAYED
	// BROWN_PELICAN
	GET3FISH((gameContext, player, birdInstance) -> {
		player.addFood("fish", 3);
	}),
	// This ability allows the player to get 3 wheat from the supply
	// AMERICAN_GOLDFINCH
	GET3SEED((gameContext, player, birdInstance) -> {
		player.addFood("seed", 3);
	}),
	// Gain 1 seed or berry from birdFeeder if available
	// INDIGO_BUNTING | ROSE_BREASTED_GROSBEAK | WESTERN_TANAGER
	GET1BERRYOR1SEED((gameContext, player, BirdInstance) -> {
		// UI shows the birdFeeder and has them choose which food they want, for now it just chooses berry and if no berry, then worm
		String food = "berry";
		if(gameContext.getBirdFeeder().contains(food))
			gameContext.grabFood(food, player, 1);
		// Not sure what i am lwk doing but lwk just delete what is below once UI is made
		else {
			if(gameContext.getBirdFeeder().contains(food))
				gameContext.grabFood(food, player, 1);
		}
	}),
	// This ability checks for the player(s) with the fewest bird in the forest and has them gain 1 food from birdFeeder
	// HERMIT_THRUSH
	GET1FOODIFLEASTFOREST((gameContext, player, birdInstance) -> {
		int leastAmount = 6;
		ArrayList<Player> players = gameContext.getPlayers();

		for(Player p: players)
			if (leastAmount > p.getBoard().get("forest").size())
				leastAmount = p.getBoard().get("forest").size();

		for(Player p: players)
			if (leastAmount == p.getBoard().get("forest").size())
				p.addFood("seed", 1); // UI is supposed to have player choose which food desired in birdFeeder; for now just seed
	}),
	// Gain all fish that are in the bird feeder
	// BALD_EAGLE
	GETALLFISHINBIRDFEEDER((gameContext, player, birdInstance) -> {
		int count = 0;
		for(String s: gameContext.getBirdFeeder())
			if(s.equals("fish"))	
				count++;
		gameContext.grabFood("fish", player, count);
	}),
	// Gain all worm that are in the bird feeder
	// NORTHERN_FLICKER
	GETALLWORMINBIRDFEEDER((gameContext, player, birdInstance) -> {
		int count = 0;
		for(String s: gameContext.getBirdFeeder())
			if(s.equals("worm"))	
				count++;
		gameContext.grabFood("worm", player, count);
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
	}),
	// Lay 1 egg on any bird of player choosing
	// BAIRDS_SPARROW | CASSINS_SPARROW | CHIPPING_SPARROW | GRASSHOPPER_SPARROW
	LAYEGGONANYBIRD((gameContext, player, birdInstance) -> {
		// need UI to ask player which bird, for now will just place on this bird
		BirdInstance bird = birdInstance;
		bird.addEggs(1);
	}),
	// Lay 1 egg on this bird
	// CALIFORNIA_QUAIL | MOURNING_DOVE | NORTHERN_BOBWHITE | SCALED_QUAIL
	LAYEGGONTHISBIRD((gameContext, player, birdInstance) -> {
		birdInstance.addEggs(1);
	}),
	// If this bird is to the right of all other birds in its habitat move it to another habitat
	// BEWICKS_WREN | BLUE_GROSBEAK | CHIMNEY_SWIFT | COMMON_NIGHTHAWK | LINCOLNS_SPARROW | SONG_SPARROW | WHITE_CROWNED_SPARROW | YELLOW_BREASTED_CHAT
	MOVEIFATVERYRIGHT((gameContext, player, birdInstance) -> {
		String habitat = "";
		for (Map.Entry<String, ArrayList<BirdInstance>> entry : player.getBoard().entrySet())
		{
			if(entry.getValue().contains(birdInstance))
				habitat = entry.getKey();
		}
		ArrayList<BirdInstance> birdInstances = player.getBoard().get(habitat);
		if(birdInstances.get(birdInstances.size()-1) == birdInstance)
		{
			// Has the UI ask which habitat the player would like the bird to move in; for now it will just not move the bird
			String newHabitat = "";
			// player.getBoard().get(newHabitat).add(birdInstance);
			// birdInstances.remove(birdInstance);
		}
	}),
	// This ability is for birds with no ability. My favourite.
	// AMERICAN_WOODCOCK | BLUE_WINGED_WARBLER | HOODED_WARBLER | PROTHONOTARY_WARBLER | TRUMPETER_SWAN | WILD_TURKEY
	NONE((gameContext, player, birdInstance) -> {

	}),
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
			birdInstance.cacheFood(1);
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
		if(rolledFoods.contains("rat"))
			birdInstance.cacheFood(1);
	}),
	// trade 1 of any type of food for any other type from the supply
	// GREEN_HERON
	TRADE1FOODFOR1OTHERFOOD((gameContext, player, birdInstance) -> {
		// UI has the player choose what food they give up for what food they want ; for now it'll just switch seed for worm
		String foodRemoved = "seed";
		String foodAdded = "worm";
		if(player.removeFood(foodRemoved, 1)) {
			player.addFood(foodAdded, 1);
		}
	}),
	// This ability allows a player to tuck a bird card behind the bird and if done, draw 1 bird card
	// AMERICAN_ROBIN | AMERICAN_COOT | BARN_SWALLOW | HOUSE_FINCH | PURPLE_MARTIN | RING_BILLED_GULL | TREE_SWALLOW | VIOLET_GREEN_SWALLOW | YELLOW_RUMPED_WARBLER
	TUCK1BIRDANDDRAW1BIRD((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
		boolean tuck = false;
		if(tuck && !player.getBirdHand().isEmpty())
		{
			Bird card = player.getBirdHand().remove(0);
			birdInstance.tuckCard(1);
			player.addBirdHand(gameContext.pullRandomBirds(1).get(0));
		}
	}),
	// tuck 1 bird card from your hand behind this bird and if you do gain 1 berry
	// CEDAR_WAXWING
	TUCK1BIRDANDGET1BERRY((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
		boolean tuck = false;
		if(tuck && !player.getBirdHand().isEmpty())
		{
			Bird card = player.getBirdHand().remove(0);
			birdInstance.tuckCard(1);
			player.addFood("berry", 1);
		}
	}),
	// tuck 1 bird card from your hand behind this bird and if you do gain 1 seed
	// DARK_EYED_JUNCO | PINE_SISKIN
	TUCK1BIRDANDGET1SEED((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
		boolean tuck = false;
		if(tuck && !player.getBirdHand().isEmpty())
		{
			Bird card = player.getBirdHand().remove(0);
			birdInstance.tuckCard(1);
			player.addFood("seed", 1);
		}
	}),
	// tuck 1 bird card from your hand behind this bird and if you do gain 1 seed or worm of your choose
	// PYGMY_NUTHATCH
	TUCK1BIRDANDGET1SEEDORWORM((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
		boolean tuck = false;
		if(tuck && !player.getBirdHand().isEmpty())
		{
			Bird card = player.getBirdHand().remove(0);
			birdInstance.tuckCard(1);
			// UI has the player choose which food they want to get, for now will be seed
			String food = "seed";
			player.addFood("food", 1);
		}
	}),
	// tuck 1 bird card from your hand behind this bird and if you do gain 1 seed
	// VAUXS_SWIFT
	TUCK1BIRDANDGET1WORM((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
		boolean tuck = false;
		if(tuck && !player.getBirdHand().isEmpty())
		{
			Bird card = player.getBirdHand().remove(0);
			birdInstance.tuckCard(1);
			player.addFood("seed", 1);
		}
	}),
	// tuck 1 bird from hand behind the bird and if done, lay 1 egg on this bird
	// BREWERS_BLACKBIRD | BUSHTIT | COMMON_GRACKLE | DICKCISSEL | RED_WINGED_BLACKBIRD | WHITE_THROATED_SWIFT | YELLOW_HEADED_BLACKBIRD
	TUCK1BIRDANDLAY1EGG((gameContext, player, birdInstance) -> {
		// UI has the player choose which card they want to remove / tuck, for now will be false
		boolean tuck = false;
		if(tuck && !player.getBirdHand().isEmpty())
		{
			Bird card = player.getBirdHand().remove(0);
			birdInstance.tuckCard(1);
			birdInstance.addEggs(1);
		}
	})
	;

	
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
