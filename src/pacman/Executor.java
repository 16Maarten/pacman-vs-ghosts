package pacman;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;
import pacman.controllers.Controller;
import pacman.controllers.HumanController;
import pacman.controllers.KeyBoardInput;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.controllers.examples.NearestPillPacMan;
import pacman.controllers.examples.NearestPillPacManVS;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.RandomNonRevPacMan;
import pacman.controllers.examples.RandomPacMan;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.StarterPacMan;
import pacman.entries.pacman.MyPacMan;
import pacman.entries.pacman.QLearningPacMan;
import pacman.entries.pacman.QLearningPacMan2;
import pacman.game.Game;
import pacman.game.GameView;

import static pacman.game.Constants.*;

/**
 * This class may be used to execute the game in timed or un-timed modes, with
 * or without
 * visuals. Competitors should implement their controllers in
 * game.entries.ghosts and
 * game.entries.pacman respectively. The skeleton classes are already provided.
 * The package
 * structure should not be changed (although you may create sub-packages in
 * these packages).
 */
@SuppressWarnings("unused")
public class Executor {
	/**
	 * The main method. Several options are listed - simply remove comments to use
	 * the option you want.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Executor exec = new Executor();

		// ----------- run multiple games in batch mode - good for testing. -----------
		QLearningPacMan testPacman = new QLearningPacMan(0.3, 0.9, 0.5, 5);
		int numTrials = 10000;

		// RESULTAAT PRE GHOSTS: (0.3, 0.9, 0.5) @ 100000 trials, 1926.2458 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 20 minDistanceGhost,
		// 7936,957 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 15 minDistanceGhost,
		// 8633.337 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 10 minDistanceGhost,
		// 9559.578 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 9 minDistanceGhost,
		// 9810.081 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 8 minDistanceGhost,
		// 9963.003 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 7 minDistanceGhost,
		// 10063.210 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 6 minDistanceGhost,
		// 10270.400 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 5 minDistanceGhost,
		// 10394.791 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 4 minDistanceGhost,
		// 2722.890 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 3 minDistanceGhost,
		// 1910.818 points
		// RESULTAAT PRO GHOSTS: (0.3, 0.9, 0.5) @ 10000 trials & 2 minDistanceGhost,
		// 1905.355 points

		// exec.runExperiment(testPacman,new
		// RandomGhosts(),numTrials);
		// exec.runExperiment(new QLearningPacMan2(0.3, 0.9, 0.5, 5), new
		// RandomGhosts(), numTrials);

		// RESULTAAT PRO REWARD SYSTEM: (0, 0.6, 0.7) @ 10000 trials & 5
		// minDistanceGhost, 1942.263 points
		// exec.runExperiment(new MyPacMan(),new RandomGhosts(),numTrials);
		// exec.runExperiment(new RandomPacMan(), new RandomGhosts(), numTrials);
		// exec.runExperiment(new StarterPacMan(), new RandomGhosts(), numTrials);
		// exec.runExperiment(new NearestPillPacMan(), new RandomGhosts(), numTrials);

		// BENCHMARK (0, 0, 0) @ 10000 trails: 932.319 points
		// RESULTAAT: (0.3, 0.9, 0.5) @ 1000 trials, 1 decimal, 36m: 2064,18 POINTS
		// RESULTAAT: (0.8, 0.9, 0.8) @ 100 trials, 1 decimal, 3m30s: 2181,10 points
		// exec.runExperimentQLearning(new QLearningPacMan(5), new RandomGhosts(),
		// numTrials);

		// ----------- run a game in synchronous mode: game waits until controllers
		// respond. -----------
		int delay = 5;
		boolean visual = true;

		exec.runGame(new QLearningPacMan(0.3, 0.9, 0.5, 5), new RandomGhosts(), visual, delay);
		// exec.runGame(new QLearningPacMan2(0.3, 0.9, 0.5, 5),new
		// RandomGhosts(),visual,delay);
		// exec.runGame(new QLearningPacMan(),new RandomGhosts(),visual,delay);
		// exec.runGame(new MyPacMan(),new RandomGhosts(),visual,delay);
		// exec.runGame(new RandomPacMan(),new RandomGhosts(),visual,delay);
		// exec.runGame(new StarterPacMan(),new RandomGhosts(),visual,delay);

		// ----------- run the game in asynchronous mode. -----------
		// boolean visual=true;

		// exec.runGameTimed(testPacman,new RandomGhosts(),visual);
		// exec.runGameTimed(new QLearningPacMan(),new StarterGhosts(),visual);
		// exec.runGameTimed(new MyPacMan(),new StarterGhosts(),visual);
		// exec.runGameTimed(new NearestPillPacMan(),new AggressiveGhosts(),visual);
		// exec.runGameTimed(new StarterPacMan(),new StarterGhosts(),visual);
		// exec.runGameTimed(new HumanController(new KeyBoardInput()),new
		// StarterGhosts(),visual);

		// ----------- run the game in asynchronous mode but advance as soon as both
		// controllers are ready - this is the mode of the competition. -----------
		// time limit of DELAY ms still applies.
		// boolean visual=true;
		// boolean fixedTime=false;

		// exec.runGameTimedSpeedOptimised(new QLearningPacMan(),new
		// RandomGhosts(),fixedTime,visual);
		// exec.runGameTimedSpeedOptimised(new MyPacMan(),new
		// RandomGhosts(),fixedTime,visual);
		// exec.runGameTimedSpeedOptimised(new RandomPacMan(),new
		// RandomGhosts(),fixedTime,visual);

		// ----------- run game in asynchronous mode and record it to file for replay at
		// a later stage. -----------
		// boolean visual=true;
		// String fileName="replay.txt";
		// exec.runGameTimedRecorded(new HumanController(new KeyBoardInput()),new
		// RandomGhosts(),visual,fileName);
		// exec.replayGame(fileName,visual);
	}

	/**
	 * For running multiple games without visuals. This is useful to get a good idea
	 * of how well a controller plays
	 * against a chosen opponent: the random nature of the game means that
	 * performance can vary from game to game.
	 * Running many games and looking at the average score (and standard
	 * deviation/error) helps to get a better
	 * idea of how well the controller is likely to do in the competition.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController  The Ghosts controller
	 * @param trials           The number of trials to be executed
	 */
	public void runExperiment(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController,
			int trials) {
		try {
			double avgScore = 0;
			FileWriter fw = new FileWriter(String.format("./results/results.%s.txt",
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy.HH'u'mm'm'ss's'"))));

			Random rnd = new Random(0);
			Game game;

			for (int i = 0; i < trials; i++) {
				game = new Game(rnd.nextLong());

				while (!game.gameOver()) {
					game.advanceGame(pacManController.getMove(game.copy(), System.currentTimeMillis() + DELAY),
							ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
				}

				avgScore += game.getScore();

				System.out.println(i + "\t" + game.getScore());
				fw.write(game.getScore() + "\n");
			}

			System.out.println(avgScore / trials);
			fw.write("AVERAGE:" + avgScore / trials);
			fw.close();
		} catch (Exception e) {
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public void runExperimentQLearning(QLearningPacMan qLearningPacManController,
			Controller<EnumMap<GHOST, MOVE>> ghostController, int trials) {
		double highestAvgScore = 0.0;
		double alpha = 0.0;
		double gamma = 0.0;
		double epsilon = 0.0;

		Random rnd = new Random(0);
		Game game;

		System.out.println("Parameter calibration started...");
		for (int x = 0; x < 11; x++) {
			for (int y = 0; y < 11; y++) {
				for (int z = 0; z < 11; z++) {
					double summedScore = 0.0;
					double alphaTemp = (double) x / 10;
					double gammaTemp = (double) y / 10;
					double epsilonTemp = (double) z / 10;
					qLearningPacManController.setParameters(alphaTemp, gammaTemp, epsilonTemp);
					// System.out.print(String.format("CURRENT: ALPHA = %1.1f | GAMMA = %1.1f |
					// EPSILON = %1.1f \r", alphaTemp, gammaTemp, epsilonTemp));

					for (int i = 0; i < trials; i++) {
						game = new Game(rnd.nextLong());

						while (!game.gameOver()) {
							game.advanceGame(
									qLearningPacManController.getMove(game.copy(), System.currentTimeMillis() + DELAY),
									ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
						}

						summedScore += game.getScore();
					}
					double avgScore = summedScore / trials;

					if (avgScore > highestAvgScore) {
						highestAvgScore = avgScore;
						alpha = alphaTemp;
						gamma = gammaTemp;
						epsilon = epsilonTemp;
						// System.out.println(String.format("HIGHEST: ALPHA = %1.1f | GAMMA = %1.1f |
						// EPSILON = %1.1f | %1.2f POINTS", alphaTemp, gammaTemp, epsilonTemp,
						// highestAvgScore));
					}
				}

				if (x != 10 && y != 10 || x == 10 && y == 0)
					System.out.print(String.format("%% %d%d \r", x, y));
			}
		}

		System.out.println(String.format(
				"----------RESULTS----------\nALPHA=%1.1f\nGAMMA=%1.1f\nEPSILON=%1.1f\n%1.2f POINTS", alpha,
				gamma, epsilon, highestAvgScore));
	}

	/**
	 * Run a game in asynchronous mode: the game waits until a move is returned. In
	 * order to slow thing down in case
	 * the controllers return very quickly, a time limit can be used. If fasted
	 * gameplay is required, this delay
	 * should be put as 0.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController  The Ghosts controller
	 * @param visual           Indicates whether or not to use visuals
	 * @param delay            The delay between time-steps
	 */
	public void runGame(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController,
			boolean visual, int delay) {
		Game game = new Game(0);

		GameView gv = null;

		if (visual)
			gv = new GameView(game).showGame();

		while (!game.gameOver()) {
			game.advanceGame(pacManController.getMove(game.copy(), -1), ghostController.getMove(game.copy(), -1));

			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}

			if (visual)
				gv.repaint();
		}
	}

	/**
	 * Run the game with time limit (asynchronous mode). This is how it will be done
	 * in the competition.
	 * Can be played with and without visual display of game states.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController  The Ghosts controller
	 * @param visual           Indicates whether or not to use visuals
	 */
	public void runGameTimed(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController,
			boolean visual) {
		Game game = new Game(0);

		GameView gv = null;

		if (visual)
			gv = new GameView(game).showGame();

		if (pacManController instanceof HumanController)
			gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());

		new Thread(pacManController).start();
		new Thread(ghostController).start();

		while (!game.gameOver()) {
			pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
			ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			game.advanceGame(pacManController.getMove(), ghostController.getMove());

			if (visual)
				gv.repaint();
		}

		pacManController.terminate();
		ghostController.terminate();
	}

	/**
	 * Run the game in asynchronous mode but proceed as soon as both controllers
	 * replied. The time limit still applies so
	 * so the game will proceed after 40ms regardless of whether the controllers
	 * managed to calculate a turn.
	 * 
	 * @param pacManController The Pac-Man controller
	 * @param ghostController  The Ghosts controller
	 * @param fixedTime        Whether or not to wait until 40ms are up even if both
	 *                         controllers already responded
	 * @param visual           Indicates whether or not to use visuals
	 */
	public void runGameTimedSpeedOptimised(Controller<MOVE> pacManController,
			Controller<EnumMap<GHOST, MOVE>> ghostController, boolean fixedTime, boolean visual) {
		Game game = new Game(0);

		GameView gv = null;

		if (visual)
			gv = new GameView(game).showGame();

		if (pacManController instanceof HumanController)
			gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());

		new Thread(pacManController).start();
		new Thread(ghostController).start();

		while (!game.gameOver()) {
			pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
			ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

			try {
				int waited = DELAY / INTERVAL_WAIT;

				for (int j = 0; j < DELAY / INTERVAL_WAIT; j++) {
					Thread.sleep(INTERVAL_WAIT);

					if (pacManController.hasComputed() && ghostController.hasComputed()) {
						waited = j;
						break;
					}
				}

				if (fixedTime)
					Thread.sleep(((DELAY / INTERVAL_WAIT) - waited) * INTERVAL_WAIT);

				game.advanceGame(pacManController.getMove(), ghostController.getMove());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (visual)
				gv.repaint();
		}

		pacManController.terminate();
		ghostController.terminate();
	}

	/**
	 * Run a game in asynchronous mode and recorded.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController  The Ghosts controller
	 * @param visual           Whether to run the game with visuals
	 * @param fileName         The file name of the file that saves the replay
	 */
	public void runGameTimedRecorded(Controller<MOVE> pacManController,
			Controller<EnumMap<GHOST, MOVE>> ghostController, boolean visual, String fileName) {
		StringBuilder replay = new StringBuilder();

		Game game = new Game(0);

		GameView gv = null;

		if (visual) {
			gv = new GameView(game).showGame();

			if (pacManController instanceof HumanController)
				gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());
		}

		new Thread(pacManController).start();
		new Thread(ghostController).start();

		while (!game.gameOver()) {
			pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
			ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			game.advanceGame(pacManController.getMove(), ghostController.getMove());

			if (visual)
				gv.repaint();

			replay.append(game.getGameState() + "\n");
		}

		pacManController.terminate();
		ghostController.terminate();

		saveToFile(replay.toString(), fileName, false);
	}

	/**
	 * Replay a previously saved game.
	 *
	 * @param fileName The file name of the game to be played
	 * @param visual   Indicates whether or not to use visuals
	 */
	public void replayGame(String fileName, boolean visual) {
		ArrayList<String> timeSteps = loadReplay(fileName);

		Game game = new Game(0);

		GameView gv = null;

		if (visual)
			gv = new GameView(game).showGame();

		for (int j = 0; j < timeSteps.size(); j++) {
			game.setGameState(timeSteps.get(j));

			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (visual)
				gv.repaint();
		}
	}

	// save file for replays
	public static void saveToFile(String data, String name, boolean append) {
		try {
			FileOutputStream outS = new FileOutputStream(name, append);
			PrintWriter pw = new PrintWriter(outS);

			pw.println(data);
			pw.flush();
			outS.close();

		} catch (IOException e) {
			System.out.println("Could not save data!");
		}
	}

	// load a replay
	private static ArrayList<String> loadReplay(String fileName) {
		ArrayList<String> replay = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String input = br.readLine();

			while (input != null) {
				if (!input.equals(""))
					replay.add(input);

				input = br.readLine();
			}

			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return replay;
	}
}