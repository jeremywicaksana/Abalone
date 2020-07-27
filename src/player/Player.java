package player;

import game.Color;

/**
 * It is responsible for creation of the Player.
 * Player has it's name and Color of the Marbles assigned.
 * In the Game class, player is also associated to particular Team.
 */
public class Player {
    private String name;
    private Color color;
    private Team team;


    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Returns color of the player's marbles.
     * @return Returns color of the player's marbles.
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Returns team of the player.
     * @return Returns team of the player.
     */
    public Team getTeam() {
        return team;
    }
    
    /**
    * Returns name of the player.
    * @return Returns name of the player.
    */
    public String getName() {
        return name;
    }
    
    /**
     * Sets player to the particular team. 
     * @param team Takes a team that player is assigned to.
     */
    public void setTeam(Team team) {
        this.team = team;
    }
    
}
