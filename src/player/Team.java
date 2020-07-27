package player;

/**
 * Keeps track of the teams score.
 * Every Player is assigned to team before the game starts.
 * It is made that way so 4 player game can be programmed in more easy way. 
 * (2 teams, 4 players)
 */
public class Team {
    private int score = 0;
    private String name;

    public Team(String name) {
        this.name = name;
    }
    
    /**
    * Gets the score.
    * @return Returns score of the team.
    */
    public int getScore() {
        return this.score;
    }

    /**
     * Sets the score.
     * @param score Takes a number of what score you want it to set.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
    * Adds point to a score.
    */
    public void addPoint() {
        this.score++;
    }
    
    public String toString() {
        return name;
    }
}
