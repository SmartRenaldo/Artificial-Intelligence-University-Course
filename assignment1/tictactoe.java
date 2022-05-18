import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class tictactoe {
    //first move
    private static final char PLAYER_X = 'x';
    //second move
    private static final char PLAYER_O = 'o';
    //write the content in contentList to txt
    private static final List<String> CONTENT_LIST = new ArrayList<>();
    private static final Map<String, Integer> MAP = new LinkedHashMap<>();
    private static int ply;

    public static void main(String[] args) {
        String input = args[0];
        String pathname = args[1];
        String turn = getTurn(input);

        if (args.length == 2) {
            minimax(0, input);
        } else if (args.length == 3) {
            alphaBeta(input, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        } else {
            tictactoe.ply = Integer.parseInt(args[3]);
            alphaBetaWithPly(input, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }

        try (PrintStream printStream = new PrintStream(pathname)) {
            CONTENT_LIST.forEach(printStream::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (!MAP.isEmpty()) {
            List<String> positiveEightList = new ArrayList<>();
            List<String> positiveSevenList = new ArrayList<>();
            List<String> positiveSixList = new ArrayList<>();
            List<String> positiveFiveList = new ArrayList<>();
            List<String> positiveFourList = new ArrayList<>();
            List<String> positiveThreeList = new ArrayList<>();
            List<String> positiveTwoList = new ArrayList<>();
            List<String> positiveOneList = new ArrayList<>();
            List<String> zeroList = new ArrayList<>();
            List<String> negativeOneList = new ArrayList<>();
            List<String> negativeTwoList = new ArrayList<>();
            List<String> negativeThreeList = new ArrayList<>();
            List<String> negativeFourList = new ArrayList<>();
            List<String> negativeFiveList = new ArrayList<>();
            List<String> negativeSixList = new ArrayList<>();
            List<String> negativeSevenList = new ArrayList<>();
            List<String> negativeEightList = new ArrayList<>();

            MAP.forEach((k, v) -> {
                if (v == 8) {
                    positiveEightList.add(k);
                } else if (v == 7) {
                    positiveSevenList.add(k);
                }else if (v == 6) {
                    positiveSixList.add(k);
                }else if (v == 5) {
                    positiveFiveList.add(k);
                }else if (v == 4) {
                    positiveFourList.add(k);
                }else if (v == 3) {
                    positiveThreeList.add(k);
                }else if (v == 2) {
                    positiveTwoList.add(k);
                }else if (v == 1) {
                    positiveOneList.add(k);
                }else if (v == 0) {
                    zeroList.add(k);
                }else if (v == -1) {
                    negativeOneList.add(k);
                }else if (v == -2) {
                    negativeTwoList.add(k);
                }else if (v == -3) {
                    negativeThreeList.add(k);
                }else if (v == -4) {
                    negativeFourList.add(k);
                }else if (v == -5) {
                    negativeFiveList.add(k);
                }else if (v == -6) {
                    negativeSixList.add(k);
                }else if (v == -7) {
                    negativeSevenList.add(k);
                }else if (v == -8) {
                    negativeEightList.add(k);
                }
            });

            if ("x".equals(turn)) {
                if (!positiveEightList.isEmpty()){
                    System.out.println(positiveEightList.get(0));
                }else if (!positiveSevenList.isEmpty()){
                    System.out.println(positiveSevenList.get(0));
                }else if (!positiveSixList.isEmpty()){
                    System.out.println(positiveSixList.get(0));
                }else if (!positiveFiveList.isEmpty()){
                    System.out.println(positiveFiveList.get(0));
                }else if (!positiveFourList.isEmpty()){
                    System.out.println(positiveFourList.get(0));
                }else if (!positiveThreeList.isEmpty()){
                    System.out.println(positiveThreeList.get(0));
                }else if (!positiveTwoList.isEmpty()){
                    System.out.println(positiveTwoList.get(0));
                }else if (!positiveOneList.isEmpty()){
                    System.out.println(positiveOneList.get(0));
                }else if (!zeroList.isEmpty()){
                    System.out.println(zeroList.get(0));
                }else if (!negativeOneList.isEmpty()){
                    System.out.println(negativeOneList.get(0));
                }else if (!negativeTwoList.isEmpty()){
                    System.out.println(negativeTwoList.get(0));
                }else if (!negativeThreeList.isEmpty()){
                    System.out.println(negativeThreeList.get(0));
                }else if (!negativeFourList.isEmpty()){
                    System.out.println(negativeFourList.get(0));
                }else if (!negativeFiveList.isEmpty()){
                    System.out.println(negativeFiveList.get(0));
                }else if (!negativeSixList.isEmpty()){
                    System.out.println(negativeSixList.get(0));
                }else if (!negativeSevenList.isEmpty()){
                    System.out.println(negativeSevenList.get(0));
                }else if (!negativeEightList.isEmpty()){
                    System.out.println(negativeEightList.get(0));
                }
            } else {
                if (!negativeEightList.isEmpty()){
                    System.out.println(negativeEightList.get(0));
                }else if (!negativeSevenList.isEmpty()){
                    System.out.println(negativeSevenList.get(0));
                }else if (!negativeSixList.isEmpty()){
                    System.out.println(negativeSixList.get(0));
                }else if (!negativeFiveList.isEmpty()){
                    System.out.println(negativeFiveList.get(0));
                }else if (!negativeFourList.isEmpty()){
                    System.out.println(negativeFourList.get(0));
                }else if (!negativeThreeList.isEmpty()){
                    System.out.println(negativeThreeList.get(0));
                }else if (!negativeTwoList.isEmpty()){
                    System.out.println(negativeTwoList.get(0));
                }else if (!negativeOneList.isEmpty()){
                    System.out.println(negativeOneList.get(0));
                }else if (!zeroList.isEmpty()){
                    System.out.println(zeroList.get(0));
                }else if (!positiveOneList.isEmpty()){
                    System.out.println(positiveOneList.get(0));
                }else if (!positiveTwoList.isEmpty()){
                    System.out.println(positiveTwoList.get(0));
                }else if (!positiveThreeList.isEmpty()){
                    System.out.println(positiveThreeList.get(0));
                }else if (!positiveFourList.isEmpty()){
                    System.out.println(positiveFourList.get(0));
                }else if (!positiveFiveList.isEmpty()){
                    System.out.println(positiveFiveList.get(0));
                }else if (!positiveSixList.isEmpty()){
                    System.out.println(positiveSixList.get(0));
                }else if (!positiveSevenList.isEmpty()){
                    System.out.println(positiveSevenList.get(0));
                }else if (!positiveEightList.isEmpty()){
                    System.out.println(positiveEightList.get(0));
                }
            }
        }
    }

    private static int alphaBetaWithPly(String input, int alpha, int beta, int depth, int curPly) {
        if (curPly++ == ply || isOver(input)) {
            return getScore(input);
        }

        if ("x".equals(getTurn(input))) {
            return getMaxWithPly(input, alpha, beta, depth, curPly);
        } else {
            return getMinWithPly(input, alpha, beta, depth, curPly);
        }
    }

    private static int getMinWithPly(String input, int alpha, int beta, int depth, int curPly) {
        int bestIndex = -1;
        String turn = getTurn(input);
        int utility = Integer.MAX_VALUE;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '-') {
                String replacedInput = input.substring(0, i) + turn + input.substring(i + 1);
                int score = alphaBetaWithPly(replacedInput, alpha, beta, depth + 1, curPly);
                utility = Math.min(utility, score);

                if (curPly <= ply) {
                    CONTENT_LIST.add(replacedInput + " " + score);
                }

                if (score < beta) {
                    beta = score;
                    bestIndex = i;
                }

                if (alpha >= beta) {
                    break;
                }

                if (bestIndex != -1 && depth == 0) {
                    MAP.put(replacedInput, score);
                }
            }
        }

        return utility;
    }

    private static int getMaxWithPly(String input, int alpha, int beta, int depth, int curPly) {
        int bestIndex = -1;
        String turn = getTurn(input);
        int utility = Integer.MIN_VALUE;

        for (Integer cell : getRemainedCells(input)) {

            if (input.charAt(cell) == '-') {
                String replacedInput = input.substring(0, cell) + turn + input.substring(cell + 1);
                int score = alphaBetaWithPly(replacedInput, alpha, beta, depth + 1, curPly);
                utility = Math.max(utility, score);

                if (curPly <= ply) {
                    CONTENT_LIST.add(replacedInput + " " + score);
                }

                if (score > alpha) {
                    alpha = score;
                    bestIndex = cell;
                }

                if (alpha >= beta) {
                    break;
                }

                if (bestIndex != -1 && depth == 0) {
                    MAP.put(replacedInput, score);
                }
            }
        }

        return utility;
    }

    private static int getScore(String input) {
        char opponent = getTurn(input).charAt(0);
        char curPlayer = (opponent == PLAYER_X) ? PLAYER_O : PLAYER_X;

        if (curPlayer == 'x') {
            return getWinLines(curPlayer, input) - getWinLines(opponent, input);
        } else {
            return getWinLines(opponent, input) - getWinLines(curPlayer, input);
        }
    }

    private static int getWinLines(char player, String input) {
        int line = 0;

        for (int i = 0; i < 3; i++) {
            if ((input.charAt(3 * i) == player || input.charAt(3 * i) == '-') &&
                    (input.charAt(3 * i + 1) == player || input.charAt(3 * i + 1) == '-') &&
                    (input.charAt(3 * i + 2) == player || input.charAt(3 * i + 2) == '-')) {
                line++;
            }

            if ((input.charAt(i) == player || input.charAt(i) == '-') &&
                    (input.charAt(i + 3) == player || input.charAt(i + 3) == '-') &&
                    (input.charAt(i + 6) == player || input.charAt(i + 6) == '-')) {
                line++;
            }
        }

        if ((input.charAt(0) == player || input.charAt(0) == '-') &&
                (input.charAt(4) == player || input.charAt(4) == '-') &&
                (input.charAt(8) == player || input.charAt(8) == '-')) {
            line++;
        }

        if ((input.charAt(2) == player || input.charAt(2) == '-') &&
                (input.charAt(4) == player || input.charAt(4) == '-') &&
                (input.charAt(6) == player || input.charAt(6) == '-')) {
            line++;
        }

        return line;
    }

    private static boolean terminal(int curPly, String input) {
        return curPly == ply || hasWon(input, PLAYER_X) || hasWon(input, PLAYER_O) || getRemainedCells(input).isEmpty();
    }

    private static int alphaBeta(String input, int alpha, int beta, int depth) {
        if (hasWon(input, PLAYER_X)) {
            return 1;
        }

        if (hasWon(input, PLAYER_O)) {
            return -1;
        }

        List<Integer> remainedCells = getRemainedCells(input);

        if (remainedCells.isEmpty()) {
            return 0;
        }

        if ("x".equals(getTurn(input))) {
            return getMax(input, alpha, beta, depth);
        } else {
            return getMin(input, alpha, beta, depth);
        }
    }

    private static int getMax(String input, int alpha, int beta, int depth) {
        int bestIndex = -1;
        String turn = getTurn(input);
        int utility = Integer.MIN_VALUE;

        for (Integer cell : getRemainedCells(input)) {

            if (input.charAt(cell) == '-') {
                String replacedInput = input.substring(0, cell) + turn + input.substring(cell + 1);
                int score = alphaBeta(replacedInput, alpha, beta, depth + 1);
                utility = Math.max(utility, score);
                CONTENT_LIST.add(replacedInput + " " + score);

                if (score > alpha) {
                    alpha = score;
                    bestIndex = cell;
                }

                if (alpha >= beta) {
                    break;
                }

                if (bestIndex != -1 && depth == 0) {
                    MAP.put(replacedInput, score);
                }
            }
        }

        return utility;
    }

    private static int getMin(String input, int alpha, int beta, int depth) {
        int bestIndex = -1;
        String turn = getTurn(input);
        int utility = Integer.MAX_VALUE;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '-') {
                String replacedInput = input.substring(0, i) + turn + input.substring(i + 1);
                int score = alphaBeta(replacedInput, alpha, beta, depth + 1);
                utility = Math.min(utility, score);
                CONTENT_LIST.add(replacedInput + " " + score);

                if (score < beta) {
                    beta = score;
                    bestIndex = i;
                }

                if (alpha >= beta) {
                    break;
                }

                if (bestIndex != -1 && depth == 0) {
                    MAP.put(replacedInput, score);
                }
            }
        }

        return utility;
    }

    private static List<Integer> getRemainedCells(String input) {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            char ch = input.charAt(i);

            if (ch == '-') {
                list.add(i);
            }
        }

        return list;
    }

    private static boolean hasWon(String input, char player) {
        for (int i = 0; i < 3; i++) {
            if (//column
                    (input.charAt(i) == input.charAt(i + 3) && input.charAt(i) == input.charAt(i + 6) && input.charAt(i) == player) ||
                            //row
                            (input.charAt(3 * i) == input.charAt(3 * i + 1) && input.charAt(3 * i) == input.charAt(3 * i + 2) && input.charAt(3 * i) == player) ||
                            //diagonal
                            (input.charAt(0) == input.charAt(4) && input.charAt(0) == input.charAt(8) && input.charAt(0) == player) ||
                            (input.charAt(2) == input.charAt(4) && input.charAt(2) == input.charAt(6) && input.charAt(2) == player)
            ) {
                return true;
            }
        }

        return false;
    }

    private static int minimax(int depth, String input) {
        if (hasWon(input, PLAYER_X)) {
            return 1;
        }

        if (hasWon(input, PLAYER_O)) {
            return -1;
        }

        List<Integer> remainedCells = getRemainedCells(input);

        if (remainedCells.isEmpty()) {
            return 0;
        }

        String turn = getTurn(input);

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '-') {
                String replacedInput = input.substring(0, i) + turn + input.substring(i + 1);
                int score = minimax(depth + 1, replacedInput);
                CONTENT_LIST.add(replacedInput + " " + score);

                if (depth == 0) {
                    MAP.put(replacedInput, score);
                }

                if ("x".equals(turn)) {
                    max = Math.max(max, score);
                } else if ("o".equals(turn)) {
                    min = Math.min(min, score);
                }
            }
        }

        return "x".equals(turn) ? max : min;
    }

    private static boolean isOver(String input) {
        return hasWon(input, PLAYER_X) || hasWon(input, PLAYER_O) || getRemainedCells(input).isEmpty();
    }

    private static String getTurn(String input) {
        int xNum = 0;
        int oNum = 0;

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == PLAYER_X) {
                xNum++;
            } else if (input.charAt(i) == PLAYER_O) {
                oNum++;
            }
        }

        if (xNum == oNum) {
            return "x";
        }

        return "o";
    }
}