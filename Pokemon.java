/**
 * Represents a Pokemon entity with battle attributes and capabilities.
 * Manages Pokemon state including health, attacks, and evolution phases.
 * @author Sihua Lu
 * @version 1.0
 */
public class Pokemon {
    String name, type;
    int level, maxHp, currHp, attack, baseAttack;
    boolean isBossSecPhase;
    
    /**
     * Creates a new Pokemon instance.
     */
    public Pokemon(String name, String type, int level, int maxHp, int attack) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.maxHp = maxHp;
        this.currHp = maxHp;
        this.attack = attack;
        this.baseAttack = attack;
        this.isBossSecPhase = false;
    }
    
    /**
     * Checks if Pokemon has fainted.
     * @return true if HP <= 0
     */
    public boolean isFainted() {
        return currHp <= 0;
    }
    
    /**
     * Fully restores HP and resets attack.
     */
    public void fullHeal() {
        this.currHp = this.maxHp;
        this.attack = this.baseAttack;
    }
    
    /**
     * Creates a deep copy of this Pokemon.
     * @return new Pokemon with same attributes
     */
    public Pokemon copy() {
        Pokemon copy = new Pokemon(this.name, this.type, this.level,
                                  this.maxHp, this.baseAttack);
        copy.currHp = this.currHp;
        copy.attack = this.attack;
        copy.isBossSecPhase = this.isBossSecPhase;
        return copy;
    }
}
