package game;

import player.Player;
import player.Team;

/**
 * Represents a game Marble including Color.
 * Marbles are assigned to player based on Color.
 */

public class Marble {
    private Color color;
    private Player player;

    /**
     * Constructor of the Marble class.
     * @param color Color of instantiated Marble.
     * @param player Player which holds instantiated Marbles.
     */
    public Marble(Color color, Player player) {
        this.color = color;
        this.player = player;
    }

    /**
     * Gets the color of the Marble.
     * @return Color of the Marble.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the Color of the Marble.
     * @param color Color of the Marble is set.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the player of a particular Marble.
     * @return Returns Player which holds particular Marble.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the team of a particular Marble.
     * @return Returns Team which holds particular Marble.
     */
    public Team getTeam() {
        return getPlayer().getTeam();
    }
}
