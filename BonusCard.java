public enum BonusCard
{
    //VARIABLES:

    //stores the method for the action, retrieved from the abstract method
    private final BonusCardInterface bonusAbility;

    //CONSTRUCTOR:
    private BonusCard(BonusCardInterface bonusAbility)
    {
        this.bonusAbility = bonusAbility;
    }

    //VOID METHODS:

    //carries out the card's scoring
    public void performBonus(Player player)
    {}
}
