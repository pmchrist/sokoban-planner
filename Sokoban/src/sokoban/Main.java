/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sokoban;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.sound.midi.Soundbank;
import org.omg.CORBA.MARSHAL;

/**
 *
 * @author arash
 */
public class Main {

    static public int[] a = {1};

    static public int[] getA() {
        return a;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // First line: # of rows
        // Second line: # of columns
        // Walls -> #
        // Character -> $
        // Box -> o
        // Target -> _

        if (args.length != 1) {
            System.out.println("Error: missing map filename or more arguments than expected!");
            System.exit(1);
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(args[0])));
        } catch (Throwable e) {
            System.out.println("Error opening/reading the file: " + e.getMessage());
            System.exit(1);
        }

        Map.SquareType[][] map_array = null;
        Point agent = null;
        List<Point> boxes_list = null;
        int targets = 0;
        try {
            int rows = Integer.parseInt(br.readLine());
            int cols = Integer.parseInt(br.readLine());

            if (rows < 3 || cols < 3) {
                System.out.println("Error: Number of columns or rows too little");
                System.exit(1);
            }

            map_array = new Map.SquareType[rows][];

            for (int y = 0; y < rows; y++) {
                map_array[y] = new Map.SquareType[cols];

                String line = br.readLine();
                for (int x = 0; x < line.length(); x++) {
                    switch (line.charAt(x)) {
                        case ' ':
                            map_array[y][x] = Map.SquareType.Empty;
                            break;
                        case '#':
                            map_array[y][x] = Map.SquareType.Wall;
                            break;
                        case '$':
                            if (agent != null) {
                                System.out.println("Error: Agent appears more than once in the map");
                                System.exit(1);
                            }
                            agent = new Point(x, y);
                            map_array[y][x] = Map.SquareType.Empty;
                            break;
                        case '_':
                            map_array[y][x] = Map.SquareType.Target;
                            ++targets;
                            break;
                        case 'o':
                            if (boxes_list == null) {
                                boxes_list = new LinkedList<Point>();
                            }
                            boxes_list.add(new Point(x, y));
                            map_array[y][x] = Map.SquareType.Empty;
                            break;
                    }
                }
                for (int j = line.length(); j < cols; ++j) {
                    map_array[y][j] = Map.SquareType.Empty;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("Error reading file");
            System.exit(1);
        }

        if (agent == null || boxes_list == null) {
            System.out.println("Error: No agent or nor boxes where found");
            System.exit(1);
        }
        if (targets != boxes_list.size()) {
            System.out.println("Error: Number of boxes and number of targets don't match");
            System.exit(1);
        }

        Set<Point> boxes = new HashSet<Point>(boxes_list);

        Map map = new Map(map_array);
        State state = new State(agent, boxes);

        // OUTPUT OF THE READ MAP
        //print_status(map, state);

        // Initialize game
        Sokoban game = new Sokoban(map, state);
        game.solve();

    }

    public static void print_status(Map map, State state) {
        Map.SquareType[][] map_array = map.map;
        Point agent = state.getAgent();
        HashSet<Point> boxes = new HashSet<Point>(state.getBoxes());

        for (int y = 0; y < map_array.length; ++y) {
            for (int x = 0; x < map_array[y].length; ++x) {
                String out = null;
                switch (map_array[y][x]) {
                    case Wall:
                        out = "#";
                        break;
                    case Empty:
                        out = " ";
                        break;
                    case Target:
                        out = "_";
                        break;
                }
                if (agent.y == y && agent.x == x) {
                    out = "$";
                }
                if (boxes.contains(new Point(x, y))) {
                    if (map_array[y][x] == Map.SquareType.Target) {
                        out = "O";
                    } else {
                        out = "o";
                    }
                }
                System.out.print(out);
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
