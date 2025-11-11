import java.util.Arrays;
import java.util.HashSet;

public enum Bird 
{
	// USED THIS LINK TO VIEW SOME BIRD ABILITIES. CAN USE BUT NEED TO DESELECT ALL EXPANSIONS AND PROMO PACKS
	// https://navarog.github.io/wingsearch/
	
	// ENUM SEQUENCE: string name, string image, int eggMax, int wingspan, int points, String[] habitat, String abilityColor, String aType, String food, String nest, BirdAction action
	// Name: obviously it's top name
	// Image: get the image from package
	// eggMax: number of egg photos
	// Wingspan: number of cms on right of card
	// Points: number next to feather
	// Habitats, go in order from top to bottom of board: forest -> grassland -> wetland
	// abilityColor: WHITE, PINK, BROWN, NONE
	// actionTypes: EggLaying, CardDrawing, Flocking (card tucks), Predator (food cache), Other
	// FoodTypes, go in order alphabetically: any -> berry -> fish -> rat -> seed -> worm 
	// nestTypes: Platform (logs on top each other), Bowl (a bowl), Cavity (tree with hole in it), Ground (bunch of circles), Wild (a star)
	// action: directly type in from birdAction
	// ACTUAL BIRD ENUMS, NO IMAGES IMPORTED YET
	ACORN_WOODPECKER("ACORN WOODPECKER", "temp", 4, 46, 5, new String[] {"forest"}, "BROWN", "Other", "and 3seed", "Cavity", BirdAction.GAIN1SEEDANDCACHE),
	// AMERICAN_AVOCET
	AMERICAN_BITTERN("AMERICAN BITTERN", "temp", 2, 107, 7, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1fish 1rat 1worm", "Platform", BirdAction.DRAW1BIRDIFLEASTWETLAND),
	AMERICAN_COOT("AMERICAN COOT", "temp", 5, 61, 3, new String[] {"wetland"}, "BROWN", "Flocking", "and 1any 1seed", "Platform", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	AMERICAN_CROW("AMERICAN CROW", "temp", 2, 99, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1any", "Platform", BirdAction.DISCARDEGGANDGAIN1FOOD),
	AMERICAN_GOLDFINCH("AMERICAN GOLDFINCH", "temp", 3, 23, 3, new String[] {"grassland"}, "WHITE", "Other", "and 2seed", "Bowl", BirdAction.GET3SEED),
	AMERICAN_KESTREL("AMERICAN KESTREL", "temp", 3, 56, 5, new String[] {"grassland"}, "BROWN", "Predator", "and 1rat 1worm", "Cavity", BirdAction.ROLLDICEANDFINDRAT),
	AMERICAN_OYSTERCATCHER("AMERICAN OYSTERCATCHER", "temp", 2, 81, 5, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 2worm", "Ground", BirdAction.DRAWBIRDEQUALTOPLAYERANDCLOCKWISEDISTRIBUTE),
	AMERICAN_REDSTART("AMERICAN REDSTART", "temp", 2, 20, 4, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1worm", "Bowl", BirdAction.GET1FOODBIRDFEEDER),
	AMERICAN_ROBIN("AMERICAN ROBIN", "temp", 4, 43, 1, new String[] {"grassland", "forest"}, "BROWN", "Flocking", "or 1berry 1worm", "Bowl", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	AMERICAN_WHITE_PELICAN("AMERICAN WHITE PELICAN", "temp", 1, 274, 5, new String[] {"wetland"}, "BROWN", "Flocking", "and 2fish", "Ground", BirdAction.DISCARDFISHANDTUCK2BIRDS),
	AMERICAN_WOODCOCK("AMERICAN WOODCOCK", "temp", 2, 46, 9, new String[] {"forest", "grassland"}, "NONE", "Other", "and 1seed 2worm", "Ground", BirdAction.NONE),
	ANHINGA("ANHINGA", "temp", 2, 114, 6, new String[] {"wetland"}, "BROWN", "Predator", "and 2fish", "Platform", BirdAction.ROLLDICEANDFINDFISH),
	// ANNAS_HUMMINGBIRD
	ASH_THROATED_FLYCATCHER("ASH THROATED FLYCATCHER", "temp", 4, 30, 4, new String[] {"grassland"}, "WHITE", "EggLaying", "and 1berry 2worm", "Cavity", BirdAction.LAYEGGONALLCAVITY),
	ATLANTIC_PUFFIN("ATLANTIC PUFFIN", "temp", 1, 53, 8, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 3fish", "Wild", BirdAction.DRAW2BONUSKEEP1),
	BAIRDS_SPARROW("BAIRD'S SPARROW", "birds/baird's_sparrow.png", 2, 23, 3, new String[] {"grassland"}, "BROWN", "EggLaying", "and 1seed 1worm", "Ground", BirdAction.LAYEGGONANYBIRD),
	BALD_EAGLE("BALD EAGLE", "birds/bald_eagle.png", 1, 203, 9, new String[] {"wetland"}, "WHITE", "Other", "and 2fish 1rat", "Platform", BirdAction.ROLLDICEANDFINDFISH),
	BALTIMORE_ORIOLE("BALTIMORE ORIOLE", "birds/baltimore_oriole.png", 2, 30, 9, new String[] {"forest"}, "BROWN", "Other", "and 2berry 1worm", "Wild", BirdAction.GET1BERRY),
	BARN_OWL("BARN OWL", "temp", 4, 107, 5, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Predator", "and 2rat", "Cavity",BirdAction.ROLLDICEANDFINDRAT),
	BARN_SWALLOW("BARN SWALLOW", "temp", 2, 107, 3, new String[] {"grassland", "wetland"}, "BROWN", "Flocking", "and 1worm", "Wild", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	BARRED_OWL("BARRED OWL", "temp", 2, 107, 3, new String[] {"forest"}, "BROWN", "Predator", "and 1rat", "Cavity", BirdAction.DRAW1BIRDANDTUCKIF75CM),
	// BARROWS_GOLDENEYE
	BELLS_VIREO("BELL'S VIREO", "birds/bell's_vireo.png", 2, 18, 4, new String[] {"forest", "grassland"}, "WHITE", "CardDrawing", "and 2worm", "Wild", BirdAction.DRAW2BONUSKEEP1),
	// BELTED_KINGFISHER
	BEWICKS_WREN("BEWICK'S WREN", "birds/bewick's_wren.png", 3, 18, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1seed 2worm", "Cavity", BirdAction.MOVEIFATVERYRIGHT),
	// BLACK_TERN
	// BLACK_VULTURE
	BLACK_BELLIED_WHISTLING_DUCK("BLACK BELLIED WHISTLING DUCK", "temp", 5, 76, 2, new String[] {"wetland"}, "BROWN", "Flocking", "and 2seed", "Cavity", BirdAction.DISCARDSEEDANDTUCK2BIRDS),
	// BLACK_BILLED_MAGPIE
	BLACK_CHINNED_HUMMINGBIRD("BLACK CHINNED HUMMINGBIRD", "temp", 2, 8, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1any", "Bowl", BirdAction.ALLGET1BERRY),
	BLACK_CROWNED_NIGHT_HERON("BLACK CROWNED NIGHT HERON", "birds/black-crowned_night-heron.png", 2, 112, 9, new String[] {"wetland"}, "BROWN", "Other", "and 1fish 1rat 1worm", "Platform", BirdAction.DISCARDEGGANDGAIN1FOOD),
	BLACK_NECKED_STILT("BLACK NECKED STILT", "temp", 2, 74, 4, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 2worm", "Ground", BirdAction.DRAW2BIRDCARDS),
	BLACK_SKIMMER("BLACK SKIMMER", "temp", 2, 112, 6, new String[] {"wetland"}, "BROWN", "Predator", "and 2fish", "Ground", BirdAction.ROLLDICEANDFINDFISH),
	BLUE_GROSBEAK("BLUE GROSBEAK", "birds/blue_grosbeak.png", 3, 28, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 2seed 1worm", "Bowl", BirdAction.MOVEIFATVERYRIGHT),
	BLUE_JAY("BLUE JAY", "temp", 2, 41, 3, new String[] {"forest"}, "BROWN", "Other", "and 1any 1seed", "Bowl", BirdAction.GAIN1SEEDANDCACHE),
	BLUE_GRAY_GNATCATCHER("BLUE GRAY GNATCATCHER", "temp", 3, 15, 1, new String[] {"forest"}, "BROWN", "Other", "and 1worm", "Bowl", BirdAction.GET1WORM),
	BLUE_WINGED_WARBLER("BLUE WINGED WARBLER", "temp", 2, 20, 8, new String[] {"forest", "grassland"}, "NONE", "Other", "and 2worm", "Bowl", BirdAction.NONE),
	BOBOLINK("BOBOLINK", "temp", 3, 30, 4, new String[] {"grassland"}, "WHITE", "EggLaying", "and 2seed 1worm", "Ground", BirdAction.LAYEGGONALLGROUND),
	BRANT("BRANT", "birds/brant.png", 2, 114, 3, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 1any 1seed", "Ground", BirdAction.DRAW3FACEUPBIRD),
	BREWERS_BLACKBIRD("BREWER'S BLACKBIRD", "temp", 3, 41, 3, new String[] {"grassland"}, "BROWN", "Flocking", "and 1any 1seed", "Bowl", BirdAction.TUCK1BIRDANDLAY1EGG),
	BROAD_WINGED_HAWK("BROAD WINGED HAWK", "temp", 2, 85, 4, new String[] {"forest"}, "BROWN", "Predator", "and 1rat", "Platform", BirdAction.ROLLDICEANDFINDRAT),
	// BRONZED_COWBIRD
	BROWN_PELICAN("BROWN PELICAN", "birds/brown_pelican.png", 2, 201, 4, new String[] {"wetland"}, "WHITE", "Other", "and 2fish", "Platform", BirdAction.GET3FISH),
	// BROWN_HEADED_COWBIRD
	BURROWING_OWL("BURROWING OWL", "temp", 4, 53, 5, new String[] {"grassland"}, "BROWN", "Predator", "and 1rat 1worm", "Wild", BirdAction.ROLLDICEANDFINDRAT),
	BUSHTIT("BUSHTIT", "birds/bushtit.png", 5, 15, 2, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Flocking", "and 1seed 1worm", "Wild", BirdAction.TUCK1BIRDANDLAY1EGG),
	CALIFORNIA_CONDOR("CALIFORNIA CONDOR", "birds/california_condor.png", 1, 277, 1, new String[] {"forest", "grassland", "wetland"}, "WHITE", "CardDrawing", "and 0any", "Ground", BirdAction.DRAW2BONUSKEEP1),
	CALIFORNIA_QUAIL("CALIFORNIA QUAIL", "birds/california_quail.png", 6, 36, 3, new String[] {"forest", "grassland"}, "BROWN", "EggLaying", "and 2seed 1worm", "Ground", BirdAction.LAYEGGONTHISBIRD),
	CANADA_GOOSE("CANADA GOOSE", "birds/canada_goose.png", 3, 132, 3, new String[] {"grassland", "wetland"}, "BROWN", "Flocking", "and 2seed", "Ground", BirdAction.DISCARDSEEDANDTUCK2BIRDS),
	CANVASBACK("CANVASBACK", "birds/canvasback.png", 4, 82, 4, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1any 1seed", "Wild", BirdAction.ALLDRAW1BIRD),
	CAROLINA_CHICKADEE("CAROLINA CHICKADEE", "birds/carolina_chickadee.png", 3, 20, 2, new String[] {"forest"}, "BROWN", "Other", "or 1seed 1worm", "Cavity", BirdAction.CACHE1SEED),
	CAROLINA_WREN("CAROLINA WREN", "birds/carolina_wren.png", 5, 20, 1, new String[] {"forest"}, "WHITE", "CardDrawing", "or 1berry 1worm", "Cavity", BirdAction.DRAW2BIRDCARDS),
	CASSINS_FINCH("CASSIN'S FINCH", "birds/cassin's_finch.png", 3, 30, 4, new String[] {"forest"}, "WHITE", "CardDrawing", "and 1berry 1seed", "Bowl", BirdAction.DRAW2BONUSKEEP1),
	CASSINS_SPARROW("CASSIN'S SPARROW", "birds/cassin's_sparrow.png", 2, 20, 3, new String[] {"grassland"}, "BROWN", "EggLaying", "and 1seed 1worm", "Ground", BirdAction.LAYEGGONANYBIRD),
	CEDAR_WAXWING("CEDAR WAXWING", "birds/cedar_waxwing.png", 3, 25, 3, new String[] {"forest", "grassland"}, "BROWN", "Flocking", "and 2berry", "Bowl", BirdAction.TUCK1BIRDANDGET1BERRY),
	CERULEAN_WARBLER("CERULEAN WARBLER", "birds/cerulean_warbler.png", 2, 20, 4, new String[] {"forest"}, "WHITE", "CardDrawing", "and 1seed 1worm", "Bowl", BirdAction.DRAW2BONUSKEEP1),
	CHESTNUT_COLLARED_LONGSPUR("CHESTNUT COLLARED LONGSPUR", "birds/chestnut-collared_longspur.png", 4, 25, 5, new String[] {"grassland"}, "WHITE", "CardDrawing", "and 2seed 1worm", "Ground", BirdAction.DRAW2BONUSKEEP1),
	CHIHUAHUAN_RAVEN("CHIHUAHUAN RAVEN", "birds/chihuahuan_raven.png", 3, 112, 4, new String[] {"grassland"}, "BROWN", "Other", "and 2any 1rat", "Platform", BirdAction.DISCARDEGGANDGAIN2FOOD),
	CHIMNEY_SWIFT("CHIMNEY SWIFT", "birds/chimney_swift.png", 2, 36, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 2worm", "Wild", BirdAction.MOVEIFATVERYRIGHT),
	CHIPPING_SPARROW("CHIPPING SPARROW", "birds/chipping_sparrow.png", 3, 23, 1, new String[] {"forest", "grassland"}, "BROWN", "EggLaying", "or 1seed 1worm", "Bowl", BirdAction.LAYEGGONANYBIRD),
	// CLARKS_GREBE
	CLARKS_NUTCRACKER("CLARK'S NUTCRACKER", "birds/clark's_nutcracker.png", 2, 61, 5, new String[] {"forest"}, "BROWN", "Other", "and 1any 2seed", "Platform", BirdAction.GAIN1SEEDANDCACHE),
	COMMON_GRACKLE("COMMON GRACKLE", "birds/common_grackle.png", 3, 43, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Flocking", "and 1any 1seed", "Bowl", BirdAction.TUCK1BIRDANDLAY1EGG),
	COMMON_LOON("COMMON LOON", "birds/common_loon.png", 1, 117, 6, new String[] {"wetland"}, "BROWN", "Other", "and 1any 1fish", "Ground", BirdAction.DRAW1BIRDIFLEASTWETLAND),
	COMMON_MERGANSER("COMMON MERGANSER", "birds/common_merganser.png", 4, 86, 5, new String[] {"wetland"}, "BROWN", "Predator", "and 1any 1fish", "Cavity", BirdAction.ROLLDICEANDFINDFISH),
	COMMON_NIGHTHAWK("COMMON NIGHTHAWK", "birds/common_nighthawk.png", 2, 56, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 2worm", "Ground", BirdAction.MOVEIFATVERYRIGHT),
	COMMON_RAVEN("COMMON RAVEN", "birds/common_raven.png", 2, 135, 5, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 2any 1rat", "Platform", BirdAction.DISCARDEGGANDGAIN2FOOD),
	// COMMON_YELLOWTHROAT
	COOPERS_HAWK("COOPER'S HAWK", "", 2, 79, 3, new String[] {"forest"}, "BROWN", "Predator", "and 1rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF75CM),
	DARK_EYED_JUNCO("DARK EYED JUNCO", "temp", 3, 23, 3, new String[] {"forest", "grassland"}, "BROWN", "Flocking", "and 1seed 1worm", "Ground", BirdAction.TUCK1BIRDANDGET1SEED),
	DICKCISSEL("DICKCISSEL", "temp", 3, 25, 4, new String[] {"grassland"}, "BROWN", "Flocking", "and 2seed 1worm", "Ground", BirdAction.TUCK1BIRDANDLAY1EGG),
	DOUBLE_CRESTED_CORMORANT("DOUBLE CRESTED CORMORANT", "temp", 3, 132, 3, new String[] {"wetland"}, "BROWN", "Flocking", "and 1any 1fish", "Platform", BirdAction.DISCARDFISHANDTUCK2BIRDS),
	// DOWNY_WOODPECKER
	// EASTERN_BLUEBIRD
	// EASTERN_KINGBIRD
	EASTERN_PHOEBE("EASTERN PHOEBE", "temp", 4, 28, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "or 1berry 1worm", "Wild", BirdAction.ALLGET1WORM),
	EASTERN_SCREECH_OWL("EASTERN SCREECH OWL", "temp", 2, 51, 4, new String[] {"forest"}, "BROWN", "Predator", "or 1rat 1worm", "Cavity", BirdAction.ROLLDICEANDFINDRAT),
	FERRUGINOUS_HAWK("FERRUGINOUS HAWK", "temp", 2, 142, 6, new String[] {"grassland"}, "BROWN", "Predator", "and 2rat", "Platform", BirdAction.ROLLDICEANDFINDRAT),
	FISH_CROW("FISH CROW", "temp", 2, 91, 6, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1any 1fish", "Platform", BirdAction.DISCARDEGGANDGAIN1FOOD),
	// FORESTERS_TERN
	FRANKLINS_GULL("FRANKLIN'S GULL", "temp", 2, 91, 3, new String[] {"grassland", "wetland"}, "BROWN", "CardDrawing", "and 1any 1fish", "Wild", BirdAction.DISCARDEGGANDGAIN2BIRDS),
	GOLDEN_EAGLE("GOLDEN EAGLE", "temp", 1, 201, 8, new String[] {"grassland", "wetland"}, "BROWN", "Predator", "and 3rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF100CM),
	GRASSHOPPER_SPARROW("GRASSHOPPER SPARROW", "temp", 2, 20, 2, new String[] {"grassland"}, "BROWN", "EggLaying", "or 1seed 1worm", "Ground", BirdAction.LAYEGGONANYBIRD),
	// GRAY_CATBIRD
	// GREAT_BLUE_HERON
	GREAT_CRESTED_FLYCATCHER("GREAT CRESTED FLYCATCHER", "temp", 3, 33, 5, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1worm", "Cavity", BirdAction.GET1WORMINBIRDFEEDER),
	// GREAT_EGRET
	GREAT_HORNED_OWL("GREAT HORNED OWL", "temp", 2, 112, 8, new String[] {"forest"}, "BROWN", "Predator", "and 3rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF100CM),
	GREATER_PRAIRIE_CHICKEN("GREATER PRAIRIE CHICKEN", "birds/greater_prairie_chicken.png", 4, 71, 5, new String[] {"grassland"}, "WHITE", "CardDrawing", "and 2seed 1worm", "Ground", BirdAction.DRAW2BONUSKEEP1),
	GREATER_ROADRUNNER("GREATER ROADRUNNER", "temp", 2, 56, 7, new String[] {"grassland"}, "BROWN", "Predator", "and 1any 1rat 1worm", "Platform", BirdAction.DRAW1BIRDANDTUCKIF50CM),
	GREEN_HERON("GREEN HERON", "temp", 3, 66, 4, new String[] {"wetland"}, "BROWN", "Other", "or 1fish 1worm", "Platform", BirdAction.TRADE1FOODFOR1OTHERFOOD),
	HERMIT_THRUSH("HERMIT THRUSH", "temp", 2, 30, 7, new String[] {"forest"}, "BROWN", "Other", "and 2berry 1worm", "Wild", BirdAction.GET1FOODIFLEASTFOREST),
	// HOODED_MERGANSER
	HOODED_WARBLER("HOODED WARBLER", "temp", 3, 18, 7, new String[] {"forest"}, "NONE", "Other", "and 2worm", "Bowl", BirdAction.NONE),
	// HORNED_LARK
	HOUSE_FINCH("HOUSE FINCH", "temp", 6, 25, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Flocking", "and 1berry 1seed", "Bowl", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	// HOUSE_WREN
	INCA_DOVE("INCA DOVE", "temp", 4, 28, 2, new String[] {"grassland"}, "WHITE", "EggLaying", "and 2seed", "Platform", BirdAction.LAYEGGONALLPLATFORM),
	INDIGO_BUNTING("INDIGO BUNTING", "temp", 3, 20, 5, new String[] {"forest", "grassland"}, "BROWN", "Other", "and 1berry 1seed 1worm", "Bowl", BirdAction.GET1BERRYOR1SEED),
	JUNIPER_TITMOUSE("JUNIPER TITMOUSE", "temp", 3, 23, 4, new String[] {"forest"}, "BROWN", "Other", "and 1seed 1worm", "Cavity", BirdAction.CACHE1SEED),
	KILLDEER("KILLDEER", "birds/killdeer.png", 3, 46, 1, new String[] {"grassland", "wetland"}, "BROWN", "CardDrawing", "or 1seed 1worm", "Ground", BirdAction.DISCARDEGGANDGAIN2BIRDS),
	KING_RAIL("KING RAIL", "temp", 6, 51, 4, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 1any 1fish 1worm", "Platform", BirdAction.DRAW2BONUSKEEP1),
	// LAZULI_BUNTING
	LINCOLNS_SPARROW("LINCOLN'S SPARROW", "birds/lincoln's_sparrow.png", 2, 20, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1seed 1worm", "Ground", BirdAction.MOVEIFATVERYRIGHT),
	// LOGGERHEAD_SHRIKE
	MALLARD("MALLARD", "birds/mallard.png", 4, 89, 0, new String[] {"wetland"}, "BROWN", "CardDrawing", "or 1seed 1worm", "Ground", BirdAction.DRAW1BIRD),
	MISSISSIPPI_KITE("MISSISSIPPI KITE", "birds/mississippi_kite.png", 1, 79, 4, new String[] {"forest", "grassland"}, "BROWN", "Predator", "or 1rat 1worm", "Platform", BirdAction.ROLLDICEANDFINDRAT),
	// MOUNTAIN_BLUEBIRD
	MOUNTAIN_CHICKADEE("MOUNTAIN CHICKADEE", "birds/mountain_chickadee.png", 3, 23, 2, new String[] {"forest"}, "BROWN", "Other", "or 1seed 1worm", "Cavity", BirdAction.CACHE1SEED),
	MOURNING_DOVE("MOURNING DOVE", "birds/mourning_dove.png", 5, 46, 0, new String[] {"forest", "grassland", "wetland"}, "BROWN", "EggLaying", "and 1seed", "Platform", BirdAction.LAYEGGONTHISBIRD),
	NORTHERN_BOBWHITE("NORTHERN BOBWHITE", "birds/northern_bobwhite.png", 6, 33, 5, new String[] {"grassland"}, "BROWN", "EggLaying", "and 3seed", "Ground", BirdAction.LAYEGGONTHISBIRD),
	NORTHERN_CARDINAL("NORTHERN CARDINAL", "birds/northern_cardinal.png", 5, 30, 3, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1seed", "Bowl", BirdAction.GET1BERRY),
	NORTHERN_FLICKER("NORTHERN FLICKER", "birds/northern_flicker.png", 4, 51, 2, new String[] {"forest", "grassland"}, "WHITE", "Other", "or 1berry 1seed 1worm", "Cavity", BirdAction.GETALLWORMINBIRDFEEDER),
	NORTHERN_HARRIER("NORTHERN HARRIER", "birds/northern_harrier.png", 2, 109, 3, new String[] {"grassland", "wetland"}, "BROWN", "Predator", "and 1rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF75CM),
	// NORTHERN_MOCKINGBIRD
	NORTHERN_SHOVELER("NORTHERN SHOVELER", "birds/northern_shoveler.png", 4, 76, 7, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 2seed 1worm", "Ground", BirdAction.ALLDRAW1BIRD),
	OSPREY("OSPREY", "birds/osprey.png", 2, 160, 5, new String[] {"wetland"}, "BROWN", "Other", "and 1fish", "Platform", BirdAction.ALLGET1FISH),
	PAINTED_BUNTING("PAINTED BUNTING", "birds/painted_bunting.png", 4, 23, 5, new String[] {"grassland"}, "WHITE", "CardDrawing", "and 2seed 1worm", "Bowl", BirdAction.DRAW2BONUSKEEP1),
	PAINTED_WHITESTART("PAINTED WHITESTART", "birds/painted_whitestart.png", 3, 22, 1, new String[] {"forest"}, "BROWN", "Other", "and 1worm", "Ground", BirdAction.GET1WORM),
	PEREGRINE_FALCON("PEREGRINE FALCON", "birds/peregrine_falcon.png", 2, 104, 5, new String[] {"grassland", "wetland"}, "BROWN", "Predator", "and 2rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF100CM),
	// PIED_BILLED_GREBE
	// PILEATED_WOODPECKER
	PINE_SISKIN("PINE SISKIN", "temp", 2, 23, 3, new String[] {"forest"}, "BROWN", "Flocking", "and 2seed", "Bowl", BirdAction.TUCK1BIRDANDGET1SEED),
	PROTHONOTARY_WARBLER("PROTHONOTARY WARBLER", "birds/prothonotary_warbler.png", 4, 23, 8, new String[] {"forest", "wetland"}, "NONE", "Other", "and 1seed 2worm", "Cavity", BirdAction.NONE),
	PURPLE_GALLINULE("PURPLE GALLINULE", "birds/purple_gallinule.png", 4, 56, 7, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1any 1berry 1seed", "Platform", BirdAction.ALLDRAW1BIRD),
	PURPLE_MARTIN("PURPLE MARTIN", "birds/purple_martin.png", 3, 46, 2, new String[] {"grassland", "wetland"}, "BROWN", "Flocking", "and 1worm", "Cavity", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	PYGMY_NUTHATCH("PYGMY NUTHATCH", "temp", 4, 20, 2, new String[] {"forest"}, "BROWN", "Flocking", "and 1seed 1worm", "Cavity", BirdAction.TUCK1BIRDANDGET1SEEDORWORM),
	RED_CROSSBILL("RED CROSSBILL", "temp", 2, 28, 6, new String[] {"forest"}, "BROWN", "Other", "and 2seed", "Bowl", BirdAction.ALLGET1SEED),
	RED_BELLIED_WOODPECKER("RED BELLIED WOODPECKER", "temp", 3, 41, 1, new String[] {"forest"}, "BROWN", "Other", "or 1seed 1worm", "Cavity", BirdAction.GAIN1SEEDANDCACHE),
	// RED_BREASTED_MERGANSER
	RED_BREASTED_NUTHATCH("RED BREASTED NUTHATCH", "temp", 3, 23, 2, new String[] {"forest"}, "BROWN", "Other", "or 1seed 1worm", "Cavity", BirdAction.CACHE1SEED),
	RED_COCKADED_WOODPECKER("RED COCKADED WOODPECKER", "temp", 2, 36, 4, new String[] {"forest"}, "WHITE", "CardDrawing", "and 1berry 1worm", "Cavity", BirdAction.DRAW2BONUSKEEP1),
	// RED_EYED_VIREO
	RED_HEADED_WOODPECKER("RED HEADED WOODPECKER", "temp", 3, 43, 4, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1any 1seed 1worm", "Cavity", BirdAction.GAIN1SEEDANDCACHE),
	RED_SHOULDERED_HAWK("RED SHOULDERED HAWK", "temp", 2, 102, 3, new String[] {"forest"}, "BROWN", "Predator", "and 1rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF75CM),
	RED_TAILED_HAWK("RED TAILED HAWK", "temp", 2, 124, 5, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Predator", "and 2rat", "Platform", BirdAction.DRAW1BIRDANDTUCKIF75CM),
	RED_WINGED_BLACKBIRD("RED WINGED BLACKBIRD", "temp", 3, 33, 2, new String[] {"grassland", "wetland"}, "BROWN", "Flocking", "and 1seed", "Bowl", BirdAction.TUCK1BIRDANDLAY1EGG),
	RING_BILLED_GULL("RING BILLED GULL", "temp", 2, 122, 4, new String[] {"wetland"}, "BROWN", "Flocking", "and 2any", "Ground", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	ROSE_BREASTED_GROSBEAK("ROSE BREASTED GROSBEAK", "temp", 3, 33, 6, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1seed 1worm", "Bowl", BirdAction.GET1BERRYOR1SEED),
	ROSEATE_SPOONBILL("ROSEATE SPOONBILL", "temp", 2, 127, 6, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 1fish 1seed 1worm", "Platform", BirdAction.DRAW2BONUSKEEP1),
	// RUBY_CROWNED_KINGLET
	// RUBY_THROATED_HUMMINGBIRD
	// RUDDY_DUCK
	SANDHILL_CRANE("SANDHILL CRANE", "temp", 1, 196, 5, new String[] {"grassland", "wetland"}, "BROWN", "Flocking", "and 1any 2seed", "Ground", BirdAction.DISCARDSEEDANDTUCK2BIRDS),
	// SAVANNAH_SPARROW
	SAYS_PHOEBE("SAY'S PHOEBE", "temp", 3, 33, 5, new String[] {"grassland"}, "WHITE", "EggLaying", "and 3worm", "Bowl", BirdAction.LAYEGGONALLBOWL),
	SCALED_QUAIL("SCALED QUAIL", "temp", 6, 36, 0, new String[] {"grassland"}, "BROWN", "EggLaying", "and 1seed", "Ground", BirdAction.LAYEGGONTHISBIRD),
	SCISSOR_TAILED_FLYCATCHER("SCISSOR TAILED FLYCATCHER", "temp", 2, 38, 8, new String[] {"grassland"}, "BROWN", "Other", "and 1berry 2worm", "Bowl", BirdAction.ALLGET1WORM),
	SNOWY_EGRET("SNOWY EGRET", "temp", 2, 104, 4, new String[] {"wetland"}, "BROWN", "Predator", "or 1fish 1worm", "Platform", BirdAction.ROLLDICEANDFINDFISH),
	SONG_SPARROW("SONG SPARROW", "temp", 5, 20, 0, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "or 1berry 1seed 1worm", "Bowl", BirdAction.MOVEIFATVERYRIGHT),
	SPOTTED_OWL("SPOTTED OWL", "temp", 1, 102, 5, new String[] {"forest"}, "WHITE", "CardDrawing", "and 2rat", "Cavity", BirdAction.DRAW2BONUSKEEP1),
	SPOTTED_SANDPIPER("SPOTTED SANDPIPER", "temp", 2, 38, 5, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1worm", "Ground", BirdAction.ALLDRAW1BIRD),
	SPOTTED_TOWHEE("SPOTTED TOWHEE", "temp", 4, 28, 0, new String[] {"forest", "grassland"}, "BROWN", "Other", "or 1berry 1seed 1worm", "Ground", BirdAction.GET1SEED),
	SPRAGUES_PIPIT("SPRAGUE'S PIPIT", "temp", 3, 25, 3, new String[] {"grassland"}, "WHITE", "CardDrawing", "and 1seed 1worm", "Ground", BirdAction.DRAW2BONUSKEEP1),
	STELLERS_JAY("STELLER'S JAY", "temp", 2, 48, 5, new String[] {"forest"}, "BROWN", "Other", "and 1any 2seed", "Bowl", BirdAction.GAIN1SEEDANDCACHE),
	SWAINSONS_HAWK("SWAINSON'S HAWK", "temp", 2, 130, 5, new String[] {"grassland"}, "BROWN", "Predator", "and 1rat 1worm", "Platform", BirdAction.DRAW1BIRDANDTUCKIF75CM),
	TREE_SWALLOW("TREE SWALLOW", "temp", 4, 38, 3, new String[] {"wetland"}, "BROWN", "Flocking", "and 1berry 1worm", "Cavity", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	TRUMPETER_SWAN("TRUMPETER SWAN", "temp", 2, 203, 9, new String[] {"wetland"}, "NONE", "Other", "and 1any 2seed", "Ground", BirdAction.NONE),
	// TUFTED_TITMOUSE
	// TURKEY_VULTURE
	VAUXS_SWIFT("VAUX'S SWIFT", "temp", 3, 31, 2, new String[] {"forest"}, "BROWN", "Flocking", "and 1worm", "Cavity", BirdAction.TUCK1BIRDANDGET1WORM),
	VIOLET_GREEN_SWALLOW("VIOLET GREEN SWALLOW", "temp", 3, 36, 3, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Flocking", "and 2worm", "Cavity", BirdAction.TUCK1BIRDANDDRAW1BIRD),
	// WESTERN_MEADOWLARK
	WESTERN_TANAGER("WESTERN TANAGER", "temp", 2, 30, 6, new String[] {"forest"}, "BROWN", "Other", "and 1berry 2worm", "Bowl", BirdAction.GET1BERRYOR1SEED),
	WHITE_BREASTED_NUTHATCH("WHITE BREASTED NUTHATCH", "temp", 3, 28, 2, new String[] {"forest"}, "BROWN", "Other", "or 1seed 1worm", "Cavity", BirdAction.CACHE1SEED),
	WHITE_CROWNED_SPARROW("WHITE CROWNED SPARROW", "temp" , 5, 25, 2, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 1seed 1worm", "Ground", BirdAction.MOVEIFATVERYRIGHT),
	WHITE_FACED_IBIS("WHITE FACED IBIS", "temp", 2, 91, 8, new String[] {"wetland"}, "BROWN", "Predator", "and 1fish 2worm", "Platform", BirdAction.ROLLDICEANDFINDFISH),
	WHITE_THROATED_SWIFT("WHITE THROATED SWIFT", "temp", 2, 38, 2, new String[] {"grassland"}, "BROWN", "Flocking", "and 1worm", "Cavity", BirdAction.TUCK1BIRDANDLAY1EGG),
	WHOOPING_CRANE("WHOOPING CRANE", "temp", 1, 221, 6, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 3any", "Ground", BirdAction.DRAW2BONUSKEEP1),
	WILD_TURKEY("WILD TURKEY", "temp", 5, 135, 8, new String[] {"forest", "grassland"}, "NONE", "Other", "and 1berry 2seed", "Ground", BirdAction.NONE),
	WILLET("WILLET", "temp", 2, 66, 4, new String[] {"wetland"}, "BROWN", "Predator", "or 1fish 1worm", "Ground", BirdAction.ROLLDICEANDFINDFISH),
	WILSONS_SNIPE("WILSON'S SNIPE", "temp", 2, 41, 5, new String[] {"wetland"}, "BROWN", "CardDrawing", "and 1worm", "Ground", BirdAction.ALLDRAW1BIRD),
	// WOOD_DUCK
	WOOD_STORK("WOOD STORK", "temp", 2, 155, 6, new String[] {"wetland"}, "WHITE", "CardDrawing", "and 1any 1fish 1rat", "PLATFORM", BirdAction.DRAW2BIRDCARDS),
	YELLOW_BELLIED_SAPSUCKER("YELLOW BELLIED SAPSUCKER", "temp", 3, 41, 3, new String[] {"forest"}, "BROWN", "Other", "and 1berry 1worm", "Cavity", BirdAction.GET1WORM),
	// YELLOW_BILLED_CUCKOO
	YELLOW_BREASTED_CHAT("YELLOW BREASTED CHAT", "temp", 3, 25, 5, new String[] {"forest", "grassland", "wetland"}, "BROWN", "Other", "and 2berry 1worm", "Bowl", BirdAction.MOVEIFATVERYRIGHT),
	YELLOW_HEADED_BLACKBIRD("YELLOW HEADED BLACKBIRD", "temp", 3, 38, 4, new String[] {"wetland"}, "BROWN", "Flocking", "and 1seed 1worm", "Bowl", BirdAction.TUCK1BIRDANDLAY1EGG),
	YELLOW_RUMPED_WARBLER("YELLOW RUMPED WARBLER", "temp", 4, 23, 1, new String[] {"forest"}, "BROWN", "Flocking", "or 1berry 1seed 1worm", "Bowl", BirdAction.TUCK1BIRDANDDRAW1BIRD)
	;

	private final String name; // holds birds name
	private final String imageFileString;
	private int deckCount; // holds the number of bird cards in the deck, decreases when drawn once
	private final int eggMax; // holds the max number of eggs this bird can have
	private final int wingspan; // holds the wingspan in centimeters this bird has
	private final int pointValue; // holds the point value this bird has
	private final String[] habitat; // holds the biomes this bird can be in
	private final String actionColor; // holds the action color type this bird has (PINK, WHITE, BROWN)
	private final String actionType; // holds the action type this bird has (EggLaying, CardDrawing, Flocking, Predator, Other)
	private final String foodRequired; // holds the food types this bird requires ("and 2worm 1berry" means bird requires 2 worms and 1 berry, or for / foods, any otherwise)
	private final String nest; // holds the nest type this bird has
	private final BirdAction action; // holds the action ability this bird has through another ENUM and interface implementation
	
	// constructor
	private Bird(String name, String imageFileString, int eggMax, int wingspan, int pointValue,
			String[] habitat, String actionColor, String actionType, String foodRequired, String nest, BirdAction action)
	{
		this.name = name;
		this.imageFileString = imageFileString;
		this.deckCount = 1;
		this.eggMax = eggMax;
		this.wingspan = wingspan;
		this.pointValue = pointValue;
		this.habitat = habitat;
		this.actionColor = actionColor;
		this.actionType = actionType;
		this.foodRequired = foodRequired;
		this.nest = nest;
		this.action = action;
	}
	
	// RETURN METHODS
	
	// returns a String with the bird's name
	public String getName() { return name; }

	// returns a String of the bird's image file path
	public String getImage() { return imageFileString; }

	// returns an int with the number of bird cards left in the deck
	public int getDeckCount() { return deckCount; }

	// returns an int with the bird's maximum egg capacity
	public int getEggMax() { return eggMax; }

	// returns an int with the bird's wingspan in centimeters
	public int getWingspan() { return wingspan; }

	// returns an int with the bird's point value
	public int getPointValue() { return pointValue; }

	// returns a String array with the habitat this bird can be in
	public String[] getHabitat() { return habitat; }

	// returns a String with the bird's action color type (PINK, WHITE, BROWN)
	public String getActionColor() { return actionColor; }

	// returns a String with the bird's action type (EggLaying, CardDrawing, etc.)
	public String getActionType() { return actionType; }

	// returns a String with the food required for this bird
	public String getFoodRequired() { return foodRequired; }

	// returns a String with the bird's nest type
	public String getNest() { return nest; }
	
	// VOID METHODS / MUTATOR METHODS
	
	// performs this bird's stored BirdAction ability on the given player
	public void performAction(Game gameContext, Player player, BirdInstance birdInstance) { action.execute(gameContext, player, birdInstance); }

	public void removeCardFromDeck() { deckCount -= 1; }

	//For BonusCard: returns all the birdEnums applicable to a name-based bonus card (photographer, anatomist, cartographer)
	public static HashSet<Bird> getBonusName(String type)
	{
		if (type.equals("anatomist"))
		// BLACK-HEADED GULL BLACK-NECKED STILT BLACK-TAILED GODWIT BLACK-THROATED DIVER BLUE GROSBEAK BLUE-WINGED WARBLER BLUETHROAT BROAD-WINGED HAWK 
		// BROWN-HEADED COWBIRD CANVASBACK CEDAR WAXWING CHESTNUT-COLLARED LONGSPUR COMMON GOLDENEYE COMMON YELLOWTHROAT DARK-EYED JUNCO DOUBLE-CRESTED CORMORANT 
		// EURASIAN COLLARED DOVE GOLDCREST GREAT CRESTED FLYCATCHER GREAT CRESTED GREBE LESSER WHITETHROAT LOGGERHEAD SHRIKE LONG-TAILED TIT PARROT CROSSBILL 
		// PIED-BILLED GREBE RED CROSSBILL RED-BACKED SHRIKE RED-BELLIED WOODPECKER RED-BREASTED MERGANSER RED-BREASTED NUTHATCH RED-EYED VIREO RED-HEADED WOODPECKER 
		// WHITE-FACED IBIS WHITE-THROATED DIPPER WHITE-THROATED SWIFT YELLOW-BELLIED SAPSUCKER YELLOW-BILLED CUCKOO YELLOW-BREASTED CHAT YELLOW-HEADED BLACKBIRD YELLOW-RUMPED WARBLER
		// RED-LEGGED PARTRIDGE RED-SHOULDERED HAWK RED-TAILED HAWK RED-WINGED BLACKBIRD RING-BILLED GULL ROSE-BREASTED GROSBEAK ROSEATE SPOONBILL RUBY-CROWNED KINGLET 
		// RUBY-THROATED HUMMINGBIRD SCISSOR-TAILED FLYCATCHER SHORT-TOED TREECREEPER WHITE WAGTAIL WHITE-BACKED WOODPECKER WHITE-BREASTED NUTHATCH WHITE-CROWNED SPARROW 
    	// ASH-THROATED FLYCATCHER BARROW’S GOLDENEYE BLACK-BELLIED WHISTLING DUCK BLACK-BILLED MAGPIE BLACK-CHINNED HUMMINGBIRD BLACK-CROWNED NIGHT-HERON 
		{
			Bird[] birds = {BLACK_NECKED_STILT, BLUE_GROSBEAK, BLUE_WINGED_WARBLER, BROAD_WINGED_HAWK, CANVASBACK, CEDAR_WAXWING, CHESTNUT_COLLARED_LONGSPUR, DARK_EYED_JUNCO, 
			DOUBLE_CRESTED_CORMORANT, GREAT_CRESTED_FLYCATCHER, BLACK_BELLIED_WHISTLING_DUCK, BLACK_CHINNED_HUMMINGBIRD, BLACK_CROWNED_NIGHT_HERON };
		   return new HashSet<Bird>(Arrays.asList(birds));
		}
		if (type.equals("cartographer"))
	    // AMERICAN AVOCET AMERICAN BITTERN AMERICAN COOT AMERICAN CROW AMERICAN GOLDFINCH AMERICAN KESTREL AMERICAN OYSTERCATCHER AMERICAN REDSTART AMERICAN ROBIN 
		// CAROLINA WREN CHIHUAHUAN RAVEN COMMON MOORHEN CORSICAN NUTHATCH EASTERN BLUEBIRD EASTERN IMPERIAL EAGLE EASTERN KINGBIRD EASTERN PHOEBE EASTERN SCREECH OWL 
		// EURASIAN COLLARED DOVE EURASIAN GOLDEN ORIOLE EURASIAN HOBBY EURASIAN JAY EURASIAN MAGPIE EURASIAN NUTCRACKER EURASIAN NUTHATCH EURASIAN SPARROWHAWK 
		// EURASIAN TREE SPARROW EUROPEAN BEE-EATER EUROPEAN GOLDFINCH EUROPEAN GREEN WOODPECKER EUROPEAN HONEY BUZZARD EUROPEAN ROBIN EUROPEAN ROLLER EUROPEAN 
		// TURTLE DOVE GREATER PRAIRIE CHICKEN INCA DOVE MISSISSIPPI KITE MOUNTAIN BLUEBIRD MOUNTAIN CHICKADEE NORTHERN BOBWHITE NORTHERN CARDINAL NORTHERN FLICKER 
		// NORTHERN GANNET NORTHERN GOSHAWK NORTHERN HARRIER NORTHERN MOCKINGBIRD NORTHERN SHOVELER SANDHILL CRANE SAVANNAH SPARROW WESTERN MEADOWLARK WESTERN TANAGER     
    	// AMERICAN WHITE PELICAN AMERICAN WOODCOCK ATLANTIC PUFFIN BALTIMORE ORIOLE CALIFORNIA CONDOR CALIFORNIA QUAIL CANADA GOOSE CAROLINA CHICKADEE 
		{
			Bird[] birds = {AMERICAN_BITTERN, AMERICAN_COOT, AMERICAN_CROW, AMERICAN_GOLDFINCH, AMERICAN_KESTREL, AMERICAN_OYSTERCATCHER, AMERICAN_REDSTART, AMERICAN_ROBIN, 
			AMERICAN_WHITE_PELICAN, AMERICAN_WOODCOCK, ATLANTIC_PUFFIN, BALTIMORE_ORIOLE, CALIFORNIA_CONDOR, CALIFORNIA_QUAIL, CANADA_GOOSE, CAROLINA_CHICKADEE,
			CAROLINA_WREN, CHIHUAHUAN_RAVEN, EASTERN_PHOEBE, EASTERN_SCREECH_OWL, GREATER_PRAIRIE_CHICKEN, INCA_DOVE, MISSISSIPPI_KITE, NORTHERN_CARDINAL};
		   return new HashSet<Bird>(Arrays.asList(birds));
		}
		if (type.equals("photographer"))
		  //  AMERICAN GOLDFINCH AMERICAN REDSTART AMERICAN WHITE PELICAN ASH-THROATED FLYCATCHER BARROW’S GOLDENEYE3 BLACK REDSTART BLACK SKIMMER BLACK TERN BLACK VULTURE 
		  //  BLACK-TAILED GODWIT BLACK-THROATED DIVER BLUE GROSBEAK BLUE JAY BLUE-GRAY GNATCATCHER BLUE-WINGED WARBLER BLUETHROAT BREWER’S BLACKBIRD BRONZED COWBIRD 
		  //  BROWN PELICAN BROWN-HEADED COWBIRD CERULEAN WARBLER CHESTNUT-COLLARED LONGSPUR COAL TIT COMMON BLACKBIRD COMMON GOLDENEYE COMMON YELLOWTHROAT EASTERN BLUEBIRD 
		  //  EURASIAN GOLDEN ORIOLE EUROPEAN GOLDFINCH EUROPEAN GREEN WOODPECKER EUROPEAN HONEY BUZZARD FERRUGINOUS HAWK GOLDCREST GOLDEN EAGLE3 GRAY CATBIRD GREAT BLUE HERON 
		  //  GREEN HERON GREY HERON GREYLAG GOOSE INDIGO BUNTING LAZULI BUNTING LESSER WHITETHROAT MOUNTAIN BLUEBIRD NORTHERN BOBWHITE PAINTED WHITESTART PURPLE GALLINULE 
		  //  PURPLE MARTIN RED CROSSBILL RED KITE RED KNOT RED-BACKED SHRIKE RED-BELLIED WOODPECKER RED-BREASTED MERGANSER RED-BREASTED NUTHATCH RED-COCKADED WOODPECKER 
		  //  RED-EYED VIREO RED-HEADED WOODPECKER RED-LEGGED PARTRIDGE RED-SHOULDERED HAWK RED-TAILED HAWK RED-WINGED BLACKBIRD ROSE-BREASTED GROSBEAK ROSEATE SPOONBILL 
		  //  RUBY-CROWNED KINGLET RUBY-THROATED HUMMINGBIRD RUDDY DUCK SNOWY EGRET SNOWY OWL VIOLET-GREEN SWALLOW4 WHITE STORK WHITE WAGTAIL WHITE-BACKED WOODPECKER 
		  //  WHITE-BREASTED NUTHATCH WHITE-CROWNED SPARROW WHITE-FACED IBIS WHITE-THROATED DIPPER WHITE-THROATED SWIFT YELLOW-BELLIED SAPSUCKER YELLOW-BILLED CUCKOO 
		  //  YELLOW-BREASTED CHAT YELLOW-HEADED BLACKBIRD YELLOW-RUMPED WARBLER YELLOWHAMMER
		  //  BLACK WOODPECKER BLACK-BELLIED WHISTLING DUCK BLACK-BILLED MAGPIE BLACK-CHINNED HUMMINGBIRD BLACK-CROWNED NIGHT-HERON BLACK-HEADED GULL BLACK-NECKED STILT 
		{
			Bird[] birds = {AMERICAN_GOLDFINCH, AMERICAN_REDSTART, AMERICAN_WHITE_PELICAN, BLACK_SKIMMER, BLUE_GROSBEAK, BLUE_JAY, BLUE_GRAY_GNATCATCHER, BLUE_WINGED_WARBLER, 
			BREWERS_BLACKBIRD, BROWN_PELICAN, CERULEAN_WARBLER, CHESTNUT_COLLARED_LONGSPUR, FERRUGINOUS_HAWK, GOLDEN_EAGLE, GREEN_HERON, INDIGO_BUNTING, BLACK_BELLIED_WHISTLING_DUCK, 
			BLACK_CHINNED_HUMMINGBIRD, BLACK_CROWNED_NIGHT_HERON, BLACK_NECKED_STILT};
           return new HashSet<Bird>(Arrays.asList(birds));
		}
		
		return new HashSet<Bird>();
	}
}
