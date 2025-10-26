public enum BonusCard implements BonusCardInterface
{
    // temporary bonus card just so the errors stop. also an example
    ECOLOGIST((player) -> {
        // Some logic that finds the habitats with the least amount of birds
    });

    //VARIABLES:

    //stores the method for the action, retrieved from the abstract method
    private final BonusCardInterface bonusAbility;
    private int deckCount;

    //CONSTRUCTOR:
    private BonusCard(BonusCardInterface bonusAbility)
    {
        this.bonusAbility = bonusAbility;
        this.deckCount = 1;
    }

    //VOID METHODS:

    //carries out the card's scoring
    @Override
    public void bonusScore(Player player)
    {
        bonusAbility.bonusScore(player);
    }

    //RETURN METHODS:
    public int getDeckCount() { return deckCount; }

    //MUTATOR METHODS:
    public void removeCardFromDeck() { deckCount -= 1; }
}
