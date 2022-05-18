import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class tmmorris {
    private static final char PLAYER_X = 'x';
    private static final char PLAYER_O = 'o';
    private static int ply;
    //original turn
    private static char srcTurn;
    private static final List<String> CONTENT_LIST = new ArrayList<>();
    private static String bestString = "";
    private static int bestInt;

    public static void main(String[] args) {
        String state = args[0];
        String path = args[1];
        ply = Integer.parseInt(args[2]);
        char turn = args[3].charAt(0);
        srcTurn = turn;

        alphaBeta(state, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, turn, 0);

        try (PrintStream printStream = new PrintStream(path)) {
            CONTENT_LIST.forEach(printStream::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (!"".equals(bestString)) {
            System.out.println(bestString);
        }
    }

    private static int alphaBeta(String state, int alpha, int beta, int curPly, char turn, int depth) {
        if (curPly++ == ply || isOver(state)) {
            return getScore(state);
        }

        if (srcTurn == 'x') {
            return getMaxWithPly(state, alpha, beta, depth, curPly, turn);
        } else {
            return getMinWithPly(state, alpha, beta, depth, curPly, turn);
        }
    }

    private static int[][] turnStateToDiArr(String state) {
        int[][] arr = new int[3][3];
        int row;
        int column;

        for (int i = 0; i < 9; i++) {
            if (state.charAt(i) == 'x') {
                row = i / 3;
                column = i % 3;
                arr[row][column] = 1;
            } else if (state.charAt(i) == '-') {
                row = i / 3;
                column = i % 3;
                arr[row][column] = 0;
            } else if (state.charAt(i) == 'o') {
                row = i / 3;
                column = i % 3;
                arr[row][column] = -1;
            }
        }

        return arr;
    }

    private static String turnDiArrToState(int[][] array) {
        StringBuilder state = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (array[i][j] == 1) {
                    state.append('x');
                } else if (array[i][j] == -1) {
                    state.append('o');
                } else if (array[i][j] == 0) {
                    state.append('-');
                }
            }
        }

        return state.toString();
    }

    private static int getMinWithPly(String state, int alpha, int beta, int depth, int curPly, char turn) {
        int utility = Integer.MAX_VALUE;
        int[][] diArr = turnStateToDiArr(state);
        int turnNum = trunTurnToInt(turn);
        char reversedTurn = reverseTurn(turn);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (diArr[i][j] == turnNum) {
                    if (i == 1 || i == 2) {
                        if (diArr[i - 1][j] == 0) {
                            int tmp = diArr[i - 1][j];
                            diArr[i - 1][j] = diArr[i][j];
                            diArr[i][j] = tmp;
                            String newState = turnDiArrToState(diArr);
                            tmp = diArr[i - 1][j];
                            diArr[i - 1][j] = diArr[i][j];
                            diArr[i][j] = tmp;
                            int score = alphaBeta(newState, alpha, beta, curPly, reversedTurn, depth + 1);
                            utility = Math.min(utility, score);

                            if (curPly <= ply) {
                                CONTENT_LIST.add(newState + " " + score);
                            }

                            if (score < beta) {
                                beta = score;
                            }

                            if (alpha >= beta) {
                                break;
                            }

                            if (depth == 0) {
                                setBestValue(turn, newState, score);
                            }
                        }
                    }

                    if (i == 0 || i == 1) {
                        if (diArr[i + 1][j] == 0) {
                            int tmp = diArr[i + 1][j];
                            diArr[i + 1][j] = diArr[i][j];
                            diArr[i][j] = tmp;
                            String newState = turnDiArrToState(diArr);
                            tmp = diArr[i + 1][j];
                            diArr[i + 1][j] = diArr[i][j];
                            diArr[i][j] = tmp;
                            int score = alphaBeta(newState, alpha, beta, curPly, reversedTurn, depth + 1);
                            utility = Math.min(utility, score);


                            if (curPly <= ply) {
                                CONTENT_LIST.add(newState + " " + score);
                            }

                            if (score < beta) {
                                beta = score;
                            }

                            if (alpha >= beta) {
                                break;
                            }

                            if (depth == 0) {
                                setBestValue(turn, newState, score);
                            }
                        }
                    }

                    if (j == 1 || j == 2) {
                        if (diArr[i][j - 1] == 0) {
                            int tmp = diArr[i][j - 1];
                            diArr[i][j - 1] = diArr[i][j];
                            diArr[i][j] = tmp;
                            String newState = turnDiArrToState(diArr);
                            tmp = diArr[i][j - 1];
                            diArr[i][j - 1] = diArr[i][j];
                            diArr[i][j] = tmp;
                            int score = alphaBeta(newState, alpha, beta, curPly, reversedTurn, depth + 1);
                            utility = Math.min(utility, score);


                            if (curPly <= ply) {
                                CONTENT_LIST.add(newState + " " + score);
                            }

                            if (score < beta) {
                                beta = score;
                            }

                            if (alpha >= beta) {
                                break;
                            }

                            if (depth == 0) {
                                setBestValue(turn, newState, score);
                            }
                        }
                    }

                    if (j == 0 || j == 1) {
                        if (diArr[i][j + 1] == 0) {
                            int tmp = diArr[i][j + 1];
                            diArr[i][j + 1] = diArr[i][j];
                            diArr[i][j] = tmp;
                            String newState = turnDiArrToState(diArr);
                            tmp = diArr[i][j + 1];
                            diArr[i][j + 1] = diArr[i][j];
                            diArr[i][j] = tmp;
                            int score = alphaBeta(newState, alpha, beta, curPly, reversedTurn, depth + 1);
                            utility = Math.min(utility, score);


                            if (curPly <= ply) {
                                CONTENT_LIST.add(newState + " " + score);
                            }

                            if (score < beta) {
                                beta = score;
                            }

                            if (alpha >= beta) {
                                break;
                            }

                            if (depth == 0) {
                                setBestValue(turn, newState, score);
                            }
                        }
                    }
                }
            }
        }

        return utility;
    }

    private static int getMaxWithPly(String state, int alpha, int beta, int depth, int curPly, char turn) {
        int bestIndex = -1;
        int utility = Integer.MIN_VALUE;
        int[][] diArr = turnStateToDiArr(state);
        int turnNum = trunTurnToInt(turn);
        char reversedTurn = reverseTurn(turn);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (diArr[i][j] == turnNum) {
                    if (i == 1 || i == 2) {
                        if (diArr[i - 1][j] == 0) {
                            int tmp = diArr[i - 1][j];
                            diArr[i - 1][j] = diArr[i][j];
                            diArr[i][j] = tmp;
                            String newState = turnDiArrToState(diArr);
                            tmp = diArr[i - 1][j];
                            diArr[i - 1][j] = diArr[i][j];
                            diArr[i][j] = tmp;
                            int score = alphaBeta(newState, alpha, beta, curPly, reversedTurn, depth + 1);
                            utility = Math.max(utility, score);


                            if (curPly <= ply) {
                                CONTENT_LIST.add(newState + " " + score);
                            }

                            if (score > alpha) {
                                alpha = score;
                                bestIndex = 3 * i + j;
                            }

                            if (alpha >= beta) {
                                break;
                            }

                            if (bestIndex != -1 && depth == 0) {
                                setBestValue(turn, newState, score);
                            }
                        }
                    }

                    if (i == 0 || i == 1) {
                        if (diArr[i + 1][j] == 0) {
                            int tmp = diArr[i + 1][j];
                            diArr[i + 1][j] = diArr[i][j];
                            diArr[i][j] = tmp;
                            String newState = turnDiArrToState(diArr);
                            tmp = diArr[i + 1][j];
                            diArr[i + 1][j] = diArr[i][j];
                            diArr[i][j] = tmp;
                            int score = alphaBeta(newState, alpha, beta, curPly, reversedTurn, depth + 1);
                            utility = Math.max(utility, score);


                            if (curPly <= ply) {
                                CONTENT_LIST.add(newState + " " + score);
                            }

                            if (score > alpha) {
                                alpha = score;
                                bestIndex = 3 * i + j;
                            }

                            if (alpha >= beta) {
                                break;
                            }

                            if (depth == 0) {
                                setBestValue(turn, newState, score);
                            }
                        }
                    }

                    if (j == 1 || j == 2) {
                        if (diArr[i][j - 1] == 0) {
                            int tmp = diArr[i][j - 1];
                            diArr[i][j - 1] = diArr[i][j];
                            diArr[i][j] = tmp;
                            String newState = turnDiArrToState(diArr);
                            tmp = diArr[i][j - 1];
                            diArr[i][j - 1] = diArr[i][j];
                            diArr[i][j] = tmp;
                            int score = alphaBeta(newState, alpha, beta, curPly, reversedTurn, depth + 1);
                            utility = Math.max(utility, score);


                            if (curPly <= ply) {
                                CONTENT_LIST.add(newState + " " + score);
                            }

                            if (score > alpha) {
                                alpha = score;
                                bestIndex = 3 * i + j;
                            }

                            if (alpha >= beta) {
                                break;
                            }

                            if (depth == 0) {
                                setBestValue(turn, newState, score);
                            }
                        }
                    }

                    if (j == 0 || j == 1) {
                        if (diArr[i][j + 1] == 0) {
                            int tmp = diArr[i][j + 1];
                            diArr[i][j + 1] = diArr[i][j];
                            diArr[i][j] = tmp;
                            String newState = turnDiArrToState(diArr);
                            tmp = diArr[i][j + 1];
                            diArr[i][j + 1] = diArr[i][j];
                            diArr[i][j] = tmp;
                            int score = alphaBeta(newState, alpha, beta, curPly, reversedTurn, depth + 1);
                            utility = Math.max(utility, score);


                            if (curPly <= ply) {
                                CONTENT_LIST.add(newState + " " + score);
                            }

                            if (score > alpha) {
                                alpha = score;
                                bestIndex = 3 * i + j;
                            }

                            if (alpha >= beta) {
                                break;
                            }

                            if (depth == 0) {
                                setBestValue(turn, newState, score);
                            }
                        }
                    }
                }
            }
        }

        return utility;
    }

    private static void setBestValue(char turn, String newState, int score) {
        if ("".equals(bestString)) {
            bestString = newState;
            bestInt = score;
        } else {
            if (turn == srcTurn) {
                if (score > bestInt) {
                    bestString = newState;
                    bestInt = score;
                }
            } else {
                if (score < bestInt) {
                    bestString = newState;
                    bestInt = score;
                }
            }
        }
    }

    private static char reverseTurn(char turn) {
        return turn == 'x' ? 'o' : 'x';
    }

    private static int trunTurnToInt(char turn) {
        if (turn == 'x') {
            return 1;
        }

        if (turn == '-') {
            return 0;
        }

        return -1;
    }

    /**
     * evaluation function is defined as the minimum number of steps for the opponent to
     * achieve wining minus the minimum number of steps for current player to achieve wining
     *
     * @param state: input state
     * @return: the value needed
     */
    private static int getScore(String state) {
        char oppositeTurn = getOppositeTurn(srcTurn);

        return getWinDistance(state, oppositeTurn) - getWinDistance(state, srcTurn);
    }

    private static int getWinDistance(String state, char turn) {
        int minDistance = Integer.MAX_VALUE;

        int[][] array = getTwoDimentional(state, turn);

        for (int i = 0; i < 3; i++) {
            minDistance = Math.min(rowDistance(array, i), minDistance);
            minDistance = Math.min(columnDistance(array, i), minDistance);
        }

        return minDistance;
    }

    private static int columnDistance(int[][] array, int column) {
        int distance = 0;
        int sumColumnIndex = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (array[i][j] == 1) {
                    distance += Math.abs(column - j);
                    sumColumnIndex += i;
                }
            }
        }

        distance += Math.abs(sumColumnIndex - 3);

        return distance;
    }

    //calculate the distance needed to a certain row
    private static int rowDistance(int[][] array, int row) {
        int distance = 0;
        int sumColumnIndex = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (array[i][j] == 1) {
                    distance += Math.abs(row - i);
                    sumColumnIndex += j;
                }
            }
        }

        distance += Math.abs(sumColumnIndex - 3);

        return distance;
    }

    private static int[][] getTwoDimentional(String state, char turn) {
        int[][] arr = new int[3][3];
        int row, column;

        for (int i = 0; i < state.length(); i++) {
            if (state.charAt(i) == turn) {
                row = i / 3;
                column = i % 3;

                arr[row][column] = 1;
            }
        }

        return arr;
    }

    private static char getOppositeTurn(char turn) {
        return 'x' == turn ? 'o' : 'x';
    }

    private static boolean isOver(String state) {
        return hasWon(PLAYER_X, state) || hasWon(PLAYER_O, state);
    }

    private static boolean hasWon(char player, String input) {
        for (int i = 0; i < 3; i++) {
            if (//column
                    (input.charAt(i) == input.charAt(i + 3) && input.charAt(i) == input.charAt(i + 6) && input.charAt(i) == player) ||
                            //row
                            (input.charAt(3 * i) == input.charAt(3 * i + 1) && input.charAt(3 * i) == input.charAt(3 * i + 2) && input.charAt(3 * i) == player)
            ) {
                return true;
            }
        }

        return false;
    }
}