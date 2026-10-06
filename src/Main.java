import java.util.Scanner;

public class Main {
    private static final int TYPEWRITER_DELAY_MS = 65;
    private static final int RESULT_PAUSE_MS = 1000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Champion[] champions = createChampions();

        System.out.println("=== Champion-Arena ===");
        System.out.println("Wähle deinen Champion:");
        for (int i = 0; i < champions.length; i++) {
            System.out.println((i + 1) + ". " + champions[i].name + " ("
                    + champions[i].role + ")"
                    + " (Geschwindigkeit: " + champions[i].speed
                    + ", Stärke: " + champions[i].strength
                    + ", Schutz: " + champions[i].protection
                    + ", Treffsicherheit: " + champions[i].accuracy + ")");
        }

        int choice = readChoice(scanner, champions.length);
        Champion player = champions[choice - 1];
        System.out.println("\nDu spielst " + player.name + "!");

        int wins = 0;
        int round = 1;
        for (int i = 0; i < champions.length; i++) {
            if (i != choice - 1) {
                if (playRound(scanner, player, champions[i], round)) {
                    wins++;
                }
                round++;
            }
        }

        System.out.println("\n=== Spielende ===");
        System.out.println("Gewonnene Runden: " + wins + " von 3");
        if (wins == 3) {
            System.out.println("Du hast alle Champions besiegt. Glückwunsch!");
        } else {
            System.out.println("Danke fürs Spielen!");
        }
        scanner.close();
    }

    private static Champion[] createChampions() {
        Champion[] champions = new Champion[4];
        champions[0] = new Champion("Seratine", "Magierin", 5, 7, 5, 7,
                new Ability[]{
                        new Ability("Arkane Kugel", "Zuverlässiger Zauber", 10, 0),
                        new Ability("Feuerstoß", "Starker Zauber", -5, 6),
                        new Ability("Magischer Strahl", "Ausgewogener Zauber", 0, 3)
                });
        champions[1] = new Champion("Volichair", "Kämpfer", 5, 8, 7, 4,
                new Ability[]{
                        new Ability("Schneller Hieb", "Zuverlässiger Angriff", 10, 0),
                        new Ability("Wuchtiger Schlag", "Starker Angriff", -5, 6),
                        new Ability("Schildstoß", "Ausgewogener Angriff", 0, 3)
                });
        champions[2] = new Champion("Master Chi", "Assassin", 9, 6, 4, 5,
                new Ability[]{
                        new Ability("Schattenstich", "Zuverlässiger Angriff", 10, 0),
                        new Ability("Tödlicher Schnitt", "Starker Angriff", -5, 6),
                        new Ability("Wirbelklinge", "Ausgewogener Angriff", 0, 3)
                });
        champions[3] = new Champion("Hashe", "Schützin", 6, 5, 5, 8,
                new Ability[]{
                        new Ability("Schnellschuss", "Zuverlässiger Angriff", 10, 0),
                        new Ability("Durchschuss", "Starker Angriff", -5, 6),
                        new Ability("Präzisionsschuss", "Ausgewogener Angriff", 0, 3)
                });
        return champions;
    }

    private static int readChoice(Scanner scanner, int numberOfChampions) {
        while (true) {
            System.out.print("Deine Wahl (1-" + numberOfChampions + "): ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= numberOfChampions) {
                    return choice;
                }
            } catch (NumberFormatException exception) {
                System.out.println("Bitte gib eine Zahl von 1 bis " + numberOfChampions + " ein.");
                continue;
            }
            System.out.println("Bitte gib eine Zahl von 1 bis " + numberOfChampions + " ein.");
        }
    }

    private static boolean playRound(Scanner scanner, Champion player, Champion opponent, int round) {
        player.health = player.maximumHealth;
        opponent.health = opponent.maximumHealth;

        while (player.health > 0 && opponent.health > 0) {
            clearScreen();
            printRoundHeader(round, player, opponent);
            printHealth(player, opponent);
            Ability selectedAbility = readAbilityChoice(scanner, player);
            showAttackResult(round, player, opponent,
                    performAttack(player, opponent, selectedAbility));

            if (opponent.health > 0) {
                Ability opponentAbility = opponent.abilities[randomIndex(opponent.abilities.length)];
                showAttackResult(round, player, opponent,
                        performAttack(opponent, player, opponentAbility));
            }
        }

        clearScreen();
        printRoundHeader(round, player, opponent);
        printHealth(player, opponent);
        if (player.health > 0) {
            typeText(player.name + " gewinnt die Runde!");
            System.out.println();
            return true;
        }

        typeText(opponent.name + " gewinnt die Runde!");
        System.out.println();
        return false;
    }

    private static String performAttack(Champion attacker, Champion defender, Ability ability) {
        String result = attacker.name + " verwendet " + ability.name + ". ";
        int hitChance = 70 + attacker.accuracy * 3 + ability.accuracyBonus;
        if (hitChance < 5) {
            hitChance = 5;
        }
        if (hitChance > 95) {
            hitChance = 95;
        }
        if (!random(hitChance)) {
            return result + attacker.name + " verfehlt den Angriff.";
        }

        int dodgeChance = 5 + defender.speed;
        if (random(dodgeChance)) {
            return result + defender.name + " weicht dem Angriff aus.";
        }

        int damage = 4 + attacker.strength + ability.damageBonus;
        if (random(attacker.strength * 6)) {
            damage += 5;
            result += attacker.name + " landet einen starken Treffer! ";
        }

        if (random(defender.protection * 5)) {
            damage = (damage + 1) / 2;
            result += defender.name + " blockt einen Teil des Schadens. ";
        }

        if (damage < 1) {
            damage = 1;
        }
        defender.health -= damage;
        if (defender.health < 0) {
            defender.health = 0;
        }
        return result + attacker.name + " trifft und verursacht " + damage + " Schaden.";
    }

    private static boolean random(int probability) {
        return Math.random() * 100 < probability;
    }

    private static int randomIndex(int length) {
        return (int) (Math.random() * length);
    }

    private static Ability readAbilityChoice(Scanner scanner, Champion champion) {
        while (true) {
            System.out.println("\nWähle eine Fähigkeit für " + champion.name + ":");
            for (int i = 0; i < champion.abilities.length; i++) {
                Ability ability = champion.abilities[i];
                System.out.println((i + 1) + ". " + ability.name + " - " + ability.description);
            }
            System.out.print("Deine Wahl (1-" + champion.abilities.length + "): ");
            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= champion.abilities.length) {
                    return champion.abilities[choice - 1];
                }
            } catch (NumberFormatException exception) {
                System.out.println("Bitte gib eine gültige Zahl ein.");
                continue;
            }
            System.out.println("Bitte wähle eine Zahl von 1 bis " + champion.abilities.length + ".");
        }
    }

    private static void showAttackResult(int round, Champion player, Champion opponent, String result) {
        clearScreen();
        printRoundHeader(round, player, opponent);
        printHealth(player, opponent);
        System.out.println();
        typeText(result);
        System.out.println();
        pauseAfterAttack();
    }

    private static void printRoundHeader(int round, Champion player, Champion opponent) {
        System.out.println("=== Runde " + round + ": " + player.name
                + " gegen " + opponent.name + " ===");
        System.out.println();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void typeText(String text) {
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            System.out.flush();
            try {
                Thread.sleep(TYPEWRITER_DELAY_MS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static void pauseAfterAttack() {
        try {
            Thread.sleep(RESULT_PAUSE_MS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private static void printHealth(Champion player, Champion opponent) {
        System.out.println("Leben - " + player.name + ": " + player.health + "/"
                + player.maximumHealth + " | " + opponent.name + ": " + opponent.health
                + "/" + opponent.maximumHealth);
    }

    private static class Champion {
        private final String name;
        private final String role;
        private final int speed;
        private final int strength;
        private final int protection;
        private final int accuracy;
        private final int maximumHealth;
        private final Ability[] abilities;
        private int health;

        private Champion(String name, String role, int speed, int strength,
                         int protection, int accuracy, Ability[] abilities) {
            this.name = name;
            this.role = role;
            this.speed = speed;
            this.strength = strength;
            this.protection = protection;
            this.accuracy = accuracy;
            this.maximumHealth = 70 + protection * 10;
            this.abilities = abilities;
            this.health = maximumHealth;
        }
    }

    private static class Ability {
        private final String name;
        private final String description;
        private final int accuracyBonus;
        private final int damageBonus;

        private Ability(String name, String description, int accuracyBonus, int damageBonus) {
            this.name = name;
            this.description = description;
            this.accuracyBonus = accuracyBonus;
            this.damageBonus = damageBonus;
        }
    }
}
