// Basic Interface to implement in BirdAction ENUM to define each ability

@FunctionalInterface
public interface BirdActionInterface
{
	void execute(Game gameContext,Player player, BirdInstance birdInstance);
}
