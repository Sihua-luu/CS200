import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

/**
 * Main class for Pokemon Tower RPG game.
 * Controls game flow, battle mechanics, and player interactions.
 * Implements turn-based combat with type advantages and team management.
 * @author Sihua Lu
 * @version 2.0
 */

public class Main {
    static ArrayList<Pokemon> playerTeam = new ArrayList<>(),
                              playerBag = new ArrayList<>(),
                              currEnemyTeam = new ArrayList<>();
    static Pokemon[][] towerFloors;
    static int currentFloor = 1;
    static final int TOTAL_FLOORS = 8;
    static Scanner inputScanner = new Scanner(System.in);
    static Random randomGen = new Random();
    static String[] pokemonTypes = {"Fire", "Water", "Grass", "Electric", "Ground"};
    
    /**
     * Main game entry point.
     */
    public static void main(String[] args) {
        setupGame();
        
        System.out.println("Welcome to Pokemon Tower!");
        System.out.println("Press space + enter to start game...");
        waitForSpace();
        
        boolean gameActive = true;
        while (gameActive && currentFloor <= TOTAL_FLOORS) {
            boolean shouldContinue = beginFloor(currentFloor);
            
            if (!shouldContinue) {
                System.out.println("1.Next Floor 2.Back to Train");
                int choice = getValidInput(1, 2);
                
                if (choice == 2 && currentFloor > 1) {
                    System.out.println("Select floor (1-" + (currentFloor - 1) + "):");
                    currentFloor = getValidInput(1, currentFloor - 1);
                } else {
                    currentFloor++;
                }
                continue;
            }
            
            boolean wonBattle = runBattle();
            
            if (!wonBattle) {
                System.out.println("Game Over! Your team was defeated!");
                gameActive = false;
            } else if (currentFloor == TOTAL_FLOORS) {
                System.out.println("Congratulations! You cleared the Pokemon Tower!");
                gameActive = false;
            } else {
                handleVictory();
                currentFloor++;
            }
        }
        
        inputScanner.close();
    }
    
    /**
     * Waits for user to press spacebar.
     */
    public static void waitForSpace() {
        while (true) {
            String input = inputScanner.nextLine();
            if (input.equals(" ")) break;
            System.out.println("Press space + enter to start...");
        }
    }
    
    /**
     * Initializes game.
     */
    public static void setupGame() {
        playerTeam.add(new Pokemon("Pikachu", "Electric", 5, 25, 10));
        
        towerFloors = new Pokemon[TOTAL_FLOORS + 1][];
        
        for (int floor = 1; floor <= TOTAL_FLOORS; floor++) {
            int enemyCount = getEnemyCountForFloor(floor);
            towerFloors[floor] = new Pokemon[enemyCount];
            
            for (int i = 0; i < enemyCount; i++) {
                towerFloors[floor][i] = createFloorEnemy(floor, i);
            }
        }
    }
    
    /**
     * Returns enemies for floor.
     */
    private static int getEnemyCountForFloor(int floor) {
        switch (floor) {
            case 1: return 1;
            case 2: return 2;
            case 8: return 1;
            default: return 3;
        }
    }
    
    /**
     * Creates enemy Pokemon.
     */
    private static Pokemon createFloorEnemy(int floor, int position) {
        if (floor == TOTAL_FLOORS) return new Pokemon("Mewtwo", "Psychic", 20, 50, 17);
        if (floor == 1) return new Pokemon("Squirtle", "Water", 3, 15, 6);
        if (floor == 2) return (position == 0) ? 
            new Pokemon("Charmander", "Fire", 4, 16, 7) : 
            new Pokemon("Geodude", "Ground", 4, 18, 6);
        
        String type = pokemonTypes[randomGen.nextInt(pokemonTypes.length)];
        int level = 3 + floor, hp = 15 + (floor * 3), attack = 6 + floor;
        return new Pokemon(type + (position + 1), type, level, hp, attack);
    }
    
    /**
     * Starts new floor.
     */
    public static boolean beginFloor(int floorNum) {
        currEnemyTeam.clear();
        System.out.println("\n=== Floor " + floorNum + " ===");
        
        if (floorNum > 1) {
            System.out.println("1.Manage Team 2.View Enemies 3.Start Battle");
            int choice = getValidInput(1, 3);
            
            if (choice == 1) {
                manageTeamOrder();
                return beginFloor(floorNum);
            }
        }
        
        for (Pokemon enemy : towerFloors[floorNum]) {
            currEnemyTeam.add(enemy.copy());
        }
        
        displayTeam("Enemy Team:", currEnemyTeam, false);
        displayTeam("Your Team:", playerTeam, true);
        
        if (floorNum == 1) {
            System.out.println("Press space + enter to start battle...");
            waitForSpace();
            return true;
        } else {
            System.out.println("1.Start Battle 2.Leave Floor");
            return getValidInput(1, 2) == 1;
        }
    }
    
    /**
     * Displays team information.
     */
    public static void displayTeam(String title, ArrayList<Pokemon> team, boolean showStats) {
        System.out.println(title);
        for (int i = 0; i < team.size(); i++) {
            Pokemon p = team.get(i);
            String info = (i + 1) + ". " + p.name + " [" + p.type + "] Lv." + p.level;
            if (showStats) info += " | HP:" + p.maxHp + " | ATK:" + p.attack;
            System.out.println(info);
        }
    }
    
    /**
     * Manages team order.
     */
    public static void manageTeamOrder() {
        while (true) {
            System.out.println("\n=== Manage Team ===");
            displayTeam("Your Team:", playerTeam, false);
            
            if (!playerBag.isEmpty()) {
                System.out.println("Bag:");
                for (int i = 0; i < playerBag.size(); i++) {
                    Pokemon p = playerBag.get(i);
                    System.out.println((i + 1) + ". " + p.name + " [" + p.type + "] Lv." + p.level);
                }
            }
            
            System.out.println("1.Swap 2.To Bag 3.To Team 4.Done");
            int choice = getValidInput(1, 4);
            
            if (choice == 1 && playerTeam.size() >= 2) {
                System.out.println("Swap positions (1-" + playerTeam.size() + "):");
                int p1 = getValidInput(1, playerTeam.size());
                int p2 = getValidInput(1, playerTeam.size());
                
                Pokemon temp = playerTeam.get(p1 - 1);
                playerTeam.set(p1 - 1, playerTeam.get(p2 - 1));
                playerTeam.set(p2 - 1, temp);
                System.out.println("Swapped!");
            } else if (choice == 2 && playerTeam.size() > 1) {
                System.out.println("Move to bag (1-" + playerTeam.size() + "):");
                int pos = getValidInput(1, playerTeam.size());
                playerBag.add(playerTeam.remove(pos - 1));
                System.out.println("Moved to bag!");
            } else if (choice == 3 && !playerBag.isEmpty() && playerTeam.size() < 3) {
                System.out.println("Move to team (1-" + playerBag.size() + "):");
                int pos = getValidInput(1, playerBag.size());
                playerTeam.add(playerBag.remove(pos - 1));
                System.out.println("Moved to team!");
            } else if (choice == 4) {
                break;
            } else {
                System.out.println("Invalid operation!");
            }
        }
    }
    
    /**
     * Runs main battle loop.
     */
    public static boolean runBattle() {
        int roundCount = 1;
        int currentPlayerIndex = 0;
        int currentEnemyIndex = 0;
        Pokemon lastHitPokemon = null;
        
        ArrayList<Pokemon> playerBackup = new ArrayList<>();
        ArrayList<Pokemon> enemyBackup = new ArrayList<>();
        for (Pokemon p : playerTeam) playerBackup.add(p.copy());
        for (Pokemon e : currEnemyTeam) enemyBackup.add(e.copy());
        
        System.out.println("\n=== BATTLE START ===");
        
        while (hasAlivePokemon(playerTeam) && hasAlivePokemon(currEnemyTeam)) {
            Pokemon player = playerTeam.get(currentPlayerIndex);
            Pokemon enemy = currEnemyTeam.get(currentEnemyIndex);
            
            if (player.isFainted()) {
                System.out.println(player.name + " fainted!");
                currentPlayerIndex = findNextAlivePokemon(playerTeam, currentPlayerIndex);
                if (currentPlayerIndex == -1) break;
                
                player = playerTeam.get(currentPlayerIndex);
                System.out.println("Go! " + player.name + "!");
            }
            
            if (enemy.isFainted()) {
                if (lastHitPokemon != null && lastHitPokemon.level <= enemy.level) {
                    System.out.println("\n--- LEVEL UP ---");
                    levelUp(lastHitPokemon);
                    System.out.println(lastHitPokemon.name + " reached Lv." + lastHitPokemon.level + "!");
                }
                
                lastHitPokemon = null;
                currentEnemyIndex++;
                if (currentEnemyIndex >= currEnemyTeam.size()) break;
                
                enemy = currEnemyTeam.get(currentEnemyIndex);
                System.out.println("Enemy: " + enemy.name + "!");
                roundCount = 1;
                continue;
            }
            
            System.out.println("\n--- Round " + roundCount + " ---");
            System.out.println("Your: " + player.name + " [" + player.type + 
                             "] Lv." + player.level + " | HP:" + player.currHp + 
                             " | ATK:" + player.attack);
            System.out.println("Enemy: " + enemy.name + " [" + enemy.type + 
                             "] Lv." + enemy.level + " | HP:" + enemy.currHp + 
                             " | ATK:" + enemy.attack);
            
            currentPlayerIndex = playerTurn(currentPlayerIndex, enemy);
            if (currentPlayerIndex == -1) {
                restoreBattleState(playerBackup, enemyBackup);
                return false;
            }
            
            player = playerTeam.get(currentPlayerIndex);
            
            if (enemy.isFainted()) {
                lastHitPokemon = player;
                System.out.println(enemy.name + " fainted!");
                continue;
            }
            
            enemyTurn(enemy, player);
            
            if (player.isFainted()) {
                System.out.println(player.name + " fainted!");
            }
            
            roundCount++;
        }
        
        if (lastHitPokemon != null && hasAlivePokemon(playerTeam)) {
            Pokemon lastEnemy = currEnemyTeam.get(currEnemyTeam.size() - 1);
            if (lastHitPokemon.level <= lastEnemy.level) {
                System.out.println("\n--- LEVEL UP ---");
                levelUp(lastHitPokemon);
                System.out.println(lastHitPokemon.name + " reached Lv." + lastHitPokemon.level + "!");
            }
        }
        
        return hasAlivePokemon(playerTeam);
    }
    
    /**
     * Handles player's turn.
     */
    public static int playerTurn(int currentIndex, Pokemon enemy) {
        Pokemon player = playerTeam.get(currentIndex);
        boolean actionTaken = false;
        
        while (!actionTaken) {
            System.out.println("1.Attack 2.Boost 3.Switch 4.Run");
            int action = getValidInput(1, 4);
            
            switch (action) {
                case 1:
                    int dmg = calcDamage(player, enemy);
                    enemy.currHp -= dmg;
                    System.out.println(player.name + " hits " + enemy.name + " for " + dmg + " damage!");
                    checkBossPhase(enemy, enemy.currHp + dmg);
                    actionTaken = true;
                    break;
                case 2:
                    int heal = (int)((player.maxHp - player.currHp) * 0.25);
                    player.currHp = Math.min(player.maxHp, player.currHp + heal);
                    int boost = (int)(player.baseAttack * 0.2);
                    player.attack += boost;
                    System.out.println(player.name + " uses Boost! +" + heal + " HP, +" + boost + " ATK");
                    actionTaken = true;
                    break;
                case 3:
                    int newIndex = switchPokemon(currentIndex);
                    if (newIndex != -1 && newIndex != currentIndex) {
                        currentIndex = newIndex;
                        player = playerTeam.get(currentIndex);
                        System.out.println("Switched to " + player.name + "!");
                    }
                    break;
                case 4:
                    System.out.println("Ran away!");
                    return -1;
            }
        }
        
        return currentIndex;
    }
    
    /**
     * Switches Pokemon.
     */
    public static int switchPokemon(int currentIndex) {
        System.out.println("Switch to:");
        for (int i = 0; i < playerTeam.size(); i++) {
            Pokemon p = playerTeam.get(i);
            String status = p.isFainted() ? " FAINTED" : " HP:" + p.currHp;
            System.out.println((i + 1) + ". " + p.name + status);
        }
        System.out.println("0.Cancel");
        
        int choice = getValidInput(0, playerTeam.size());
        if (choice == 0) return currentIndex;
        
        if (playerTeam.get(choice - 1).isFainted()) {
            System.out.println("Fainted!");
            return currentIndex;
        }
        
        return choice - 1;
    }
    
    /**
     * Finds next alive Pokemon.
     */
    public static int findNextAlivePokemon(ArrayList<Pokemon> team, int currentIndex) {
        for (int i = 0; i < team.size(); i++) {
            if (i != currentIndex && !team.get(i).isFainted()) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Handles enemy's turn.
     */
    public static void enemyTurn(Pokemon enemy, Pokemon player) {
        System.out.println("[Enemy Turn]");
        
        if (randomGen.nextDouble() < 0.7) {
            int dmg = calcDamage(enemy, player);
            player.currHp -= dmg;
            System.out.println(enemy.name + " hits " + player.name + " for " + dmg + " damage!");
            
            if (enemy.isBossSecPhase) {
                applyBossLifesteal(enemy, dmg);
            }
        } else {
            int heal = (int)((enemy.maxHp - enemy.currHp) * 0.25);
            enemy.currHp = Math.min(enemy.maxHp, enemy.currHp + heal);
            System.out.println(enemy.name + " uses Boost! +" + heal + " HP");
        }
    }
    
    /**
     * Restores battle state.
     */
    public static void restoreBattleState(ArrayList<Pokemon> playerBackup,
                                         ArrayList<Pokemon> enemyBackup) {
        playerTeam.clear();
        for (Pokemon p : playerBackup) playerTeam.add(p.copy());
        
        currEnemyTeam.clear();
        for (Pokemon e : enemyBackup) currEnemyTeam.add(e.copy());
    }
    
    /**
     * Calculates damage.
     */
    public static int calcDamage(Pokemon attacker, Pokemon defender) {
        double bonus = attacker.isBossSecPhase ? 2.0 : getTypeAdvantage(attacker.type, defender.type);
        return (int)(attacker.attack * bonus);
    }
    
    /**
     * Checks boss phase.
     */
    public static void checkBossPhase(Pokemon boss, int oldHp) {
        if (currentFloor == TOTAL_FLOORS && !boss.isBossSecPhase && 
            boss.currHp <= boss.maxHp / 2) {
            System.out.println("!!! BOSS TRANSFORMATION !!!");
            boss.isBossSecPhase = true;
        }
    }
    
    /**
     * Applies boss lifesteal.
     */
    public static void applyBossLifesteal(Pokemon boss, int damage) {
        if (boss.isBossSecPhase) {
            int heal = (int)(damage * 0.25);
            boss.currHp = Math.min(boss.maxHp, boss.currHp + heal);
            System.out.println("Mewtwo absorbs life! +" + heal + " HP");
        }
    }
    
    /**
     * Handles victory.
     */
    public static void handleVictory() {
        System.out.println("Battle Won!");
        
        if (currentFloor != TOTAL_FLOORS) {
            catchPokemon();
        }
        
        for (Pokemon p : playerTeam) {
            p.fullHeal();
        }
    }
    
    /**
     * Levels up Pokemon.
     */
    public static void levelUp(Pokemon p) {
        p.level++;
        p.maxHp += 3;
        p.baseAttack += 2;
        p.attack = p.baseAttack;
        p.currHp = p.maxHp;
        System.out.println("HP +3, ATK +2");
    }
    
    /**
     * Catches Pokemon.
     */
    public static void catchPokemon() {
        ArrayList<Pokemon> fainted = new ArrayList<>();
        for (Pokemon e : currEnemyTeam) {
            if (e.isFainted()) {
                fainted.add(e);
            }
        }
        
        if (!fainted.isEmpty()) {
            System.out.println("Catch which Pokemon?");
            for (int i = 0; i < fainted.size(); i++) {
                System.out.println((i + 1) + ". " + fainted.get(i).name);
            }
            System.out.println("0.Skip");
            
            int choice = getValidInput(0, fainted.size());
            
            if (choice > 0) {
                Pokemon caught = fainted.get(choice - 1);
                caught.fullHeal();
                
                if (playerTeam.size() < 3) {
                    playerTeam.add(caught);
                    System.out.println(caught.name + " joined your team!");
                } else {
                    playerBag.add(caught);
                    System.out.println("Caught " + caught.name + "! (Added to bag)");
                }
            } else {
                System.out.println("Skipped.");
            }
        }
    }
    
    /**
     * Determines type advantage.
     */
    public static double getTypeAdvantage(String atk, String def) {
        if (atk.equals("Fire") && def.equals("Grass")) return 2.0;
        if (atk.equals("Fire") && (def.equals("Water") || def.equals("Ground"))) return 0.5;
        if (atk.equals("Water") && (def.equals("Fire") || def.equals("Ground"))) return 2.0;
        if (atk.equals("Water") && def.equals("Grass")) return 0.5;
        if (atk.equals("Grass") && (def.equals("Water") || def.equals("Ground"))) return 2.0;
        if (atk.equals("Grass") && def.equals("Fire")) return 0.5;
        if (atk.equals("Electric") && def.equals("Water")) return 2.0;
        if (atk.equals("Electric") && def.equals("Ground")) return 0.5;
        if (atk.equals("Ground") && (def.equals("Fire") || def.equals("Electric"))) return 2.0;
        if (atk.equals("Ground") && (def.equals("Water") || def.equals("Grass"))) return 0.5;
        return 1.0;
    }
    
    /**
     * Checks for alive Pokemon.
     */
    public static boolean hasAlivePokemon(ArrayList<Pokemon> team) {
        for (Pokemon p : team) {
            if (!p.isFainted()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calculates team total HP.
     */
    public static int getTeamTotalHp(ArrayList<Pokemon> team) {
        int total = 0;
        for (Pokemon p : team) {
            if (!p.isFainted()) {
                total += p.currHp;
            }
        }
        return total;
    }
    
    /**
     * Gets valid input.
     */
    public static int getValidInput(int min, int max) {
        while (true) {
            if (inputScanner.hasNextInt()) {
                int input = inputScanner.nextInt();
                inputScanner.nextLine();
                
                if (input >= min && input <= max) {
                    return input;
                }
            } else {
                inputScanner.next();
            }
            System.out.println("Invalid! Enter " + min + "-" + max + ":");
        }
    }
}