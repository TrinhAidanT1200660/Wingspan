public enum Bonus
{
    //VARIABLES:

    //stores the method for the action, retrieved from the abstract method
    private final BonusInterface bonusAbility;

    //CONSTRUCTOR:
    private Bonus(BonusInterface bonusAbility)
    {
        this.bonusAbility = bonusAbility;
    }

    
