# Deal or No Deal | Java & JavaFX

Originally created as my high school senior project using Code.org, where I designed the interface and developed the game's mechanics from scratch.

In 2026, I revisited the concept and developed an updated take on the original game using Java and JavaFX. This version introduces a redesigned interface, interactive briefcases, randomized case values, and a dynamic banker offer system while applying object-oriented programming and event-driven development.

This project highlights my growth as a developer, combining an original idea with the programming skills and technologies I've learned since high school.

<!-- Add a screenshot: run the game, take a screenshot, save it as docs/screenshot.png -->
<!-- ![Deal or No Deal screenshot](docs/screenshot.png) -->

## How to play

1. Pick the briefcase you want to keep until the end.
2. Open other cases to reveal their values. Opened values are crossed off the prize board.
3. After each round (6, 5, 4, 3, 2, then 1 case at a time), the banker makes an offer: **Deal** ends the game, **No Deal** keeps playing.
4. When two cases are left, choose whether to keep your case or swap.

## How it works

- **Randomized values:** the 26 prize amounts are shuffled into the cases at the start of every game.
- **Game state:** the case values, opened cases, your chosen case, and the current round are tracked in the app's state, and the board, prize list, and labels all update from it.
- **Banker offers:** each offer is based on the average of the values still in play, multiplied by a percentage that rises each round (with a little randomness), so offers get closer to the expected value as the game goes on.
- **Event-driven UI:** every briefcase and button is a JavaFX control with an event handler; the game advances only in response to the player's clicks.

## Run it

Requires **JDK 21** and **Maven** (IntelliJ IDEA includes Maven support).

```bash
mvn javafx:run
```

Or in IntelliJ: open this folder as a Maven project, then run `DealOrNoDealApp`.

## Tech

Java 21 · JavaFX 21 · Maven
