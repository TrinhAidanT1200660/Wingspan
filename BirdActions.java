
public enum BirdActions implements BirdActionInterface
{
	GRABFISH((player) ->
	{
		// this temporary to just show the structure
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
	public void execute(Player player)
	{
		action.execute(player);
	}
}
