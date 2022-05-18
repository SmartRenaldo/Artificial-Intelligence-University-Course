import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class inference {
    private static final int COLUMN = 2;
    /**
     * Alarm in burglarquery2
     */
    private static final Pattern PROBABILITY_PATTERN = Pattern.compile("(\\()\\w+(\\s+)");
    /**
     * Earthquake & Burglar in burglarquery2
     */
    private static final Pattern CONDITION_PATTERN = Pattern.compile("(\\s)[A-Za-z]+(=)(true|false)");
    private static Node node0;
    private static Node node1;
    private static Node node;
    private static Node node0ToBeDeleted;
    private static Node node1ToBeDeleted;

    public static void main(String[] args) {
        String graphFilePath = args[0];
        String queryFilePath = args[1];

        try (BufferedReader graphReader = new BufferedReader(new FileReader(graphFilePath));
             BufferedReader queryReader = new BufferedReader(new FileReader(queryFilePath))
        ) {
            //5
            int varNum = Integer.parseInt(graphReader.readLine());
            graphReader.readLine();

            //Burglar Earthquake Alarm John Mary
            String[] varArr = graphReader.readLine().trim().split("\\s+");
            graphReader.readLine();

            //0 0 1 0 0
            //0 0 1 0 0
            //0 0 0 1 1
            //0 0 0 0 0
            //0 0 0 0 0
            int[][] hierarchicalMatrix = new int[varNum][varNum];
            for (int i = 0; i < varNum; i++) {
                String[] strNum = graphReader.readLine().trim().split("\\s+");
                for (int j = 0; j < varNum; j++) {
                    hierarchicalMatrix[i][j] = Integer.parseInt(strNum[j]);
                }
            }
            graphReader.readLine();

            /*
            0.01 0.99

            0.02 0.98

            0.001 0.999
            0.29 0.71
            0.94 0.06
            0.95 0.05

            0.05 0.95
            0.9 0.1

            0.01 0.99
            0.7 0.3
            */
            List<BigDecimal[][]> probabilityList = new ArrayList<>();

            for (int i = 0; i < varNum; i++) {
                int rowNum = getProbabilityRow(hierarchicalMatrix, i);
                BigDecimal[][] probabilityArr = new BigDecimal[rowNum][COLUMN];

                for (int j = 0; j < rowNum; j++) {
                    String[] lineEle = graphReader.readLine().trim().split("\\s+");
                    probabilityArr[j][0] = BigDecimal.valueOf(Double.parseDouble(lineEle[0]));
                    probabilityArr[j][1] = BigDecimal.valueOf(Double.parseDouble(lineEle[1]));
                }

                probabilityList.add(probabilityArr);

                graphReader.readLine();
            }

            String queryLine = queryReader.readLine();
            String probabilityName = "";
            Matcher probabilityMatcher = PROBABILITY_PATTERN.matcher(queryLine);
            if (probabilityMatcher.find()) {
                probabilityName = probabilityMatcher.group().substring(1, probabilityMatcher.group().length() - 1);
            }

            //index of Alarm is 2
            int probabilityIndex = getIndexByName(varArr, probabilityName);

            List<Condition> conditionList = new ArrayList<>();
            Matcher conditionMatcher = CONDITION_PATTERN.matcher(queryLine);

            while (conditionMatcher.find()) {
                String condition = conditionMatcher.group(0).substring(1);
                String[] conditionArr = condition.split("=");
                int index = getIndexByName(varArr, conditionArr[0]);
                int bool = turnBoolToInt(conditionArr[1]);
                conditionList.add(new Condition(index, bool));
            }

            conditionList.sort(Comparator.comparingInt(Condition::getConditionIndex));

            //ConditionProbability{nameIndex=2, conditionList=[Condition{conditionIndex=0, bool=1}, Condition{conditionIndex=1, bool=1}]}
            QueryContent queryContent = new QueryContent(probabilityIndex, conditionList);

            if (probabilityExist(hierarchicalMatrix, queryContent)) {
                int rowIndex = getRowIndexOfConditionProbability(queryContent);
                System.out.println(probabilityList.get(probabilityIndex)[rowIndex][0] + " " + probabilityList.get(probabilityIndex)[rowIndex][1]);
            } else {
                //get mentioned index 0, 3, 4 (Burglar, John, Mary)
                //get mentioned index 0, 1, 2 (Burglar, Earthquake, Alarm)
                List<Integer> mentionedList = new ArrayList<>();
                ArrayList<Integer> certainList = new ArrayList<>();
                mentionedList.add(queryContent.getNameIndex());
                for (Condition condition : queryContent.getConditionList()) {
                    mentionedList.add(condition.getConditionIndex());
                    certainList.add(condition.getConditionIndex());
                }
                //get mentioned index 3, 4 (John, Mary)

                //get unmentioned index 1, 2 (Earthquake, Alarm)
                //get unmentioned index 3, 4 (John, Mary)
                List<Integer> unmentionedList = new ArrayList<>();
                for (int i = 0; i < varNum; i++) {
                    unmentionedList.add(i);
                }
                for (Integer integer : mentionedList) {
                    unmentionedList.remove(integer);
                }

                /*
                [Node{index=0, parentList=[], probabilityArr=[[0.01, 0.99]]},
                Node{index=1, parentList=[], probabilityArr=[[0.02, 0.98]]},
                Node{index=2, parentList=[0, 1], probabilityArr=[[0.001, 0.999], [0.29, 0.71], [0.94, 0.06], [0.95, 0.05]]},
                Node{index=3, parentList=[2], probabilityArr=[[0.05, 0.95], [0.9, 0.1]]},
                Node{index=4, parentList=[2], probabilityArr=[[0.01, 0.99], [0.7, 0.3]]}]
                 */
                List<Node> nodeList = new ArrayList<>();
                //System.out.println("unmentionedList = " + unmentionedList);
                //System.out.println("mentionedList = " + mentionedList);
                //System.out.println("certainList = " + certainList);
                List<Integer> eleToBeExtractedAndEliminatedList = unmentionedList;

                for (int i = 0; i < varNum; i++) {
                    //if i unmentioned in srcMentionedList && condition(s) known --> not add
                    if (judgeAdd(unmentionedList, certainList, i, getParentList(hierarchicalMatrix, i), getOccurrenceNum(hierarchicalMatrix, i))
                    ) {
                        Node node = new Node(i, getParentList(hierarchicalMatrix, i), probabilityList.get(i));
                        nodeList.add(node);
                    } else {
                        // removed Integer in eleToBeExtractedAndEliminatedList
                        Integer removedInteger = i;
                        eleToBeExtractedAndEliminatedList.remove(removedInteger);
                    }
                }

                //System.out.println("nodeList = " + nodeList);
                //System.out.println("eleToBeExtractedAndEliminatedList = " + eleToBeExtractedAndEliminatedList);

                List<Integer> remainedList = new ArrayList<>();
                for (Node node : nodeList) {
                    if (!judgeIntersection(node.getMentionedIndexList(), unmentionedList)) {
                        remainedList.add(node.getNameIndex());
                    }
                }

                //get P(E), P(A | B, E), P(j | A) & P(┐m | A) (have index in unmentionedList)
                //[[1], [2, 0, 1], [3, 2], [4, 2]]
                ArrayList<ListEntry> listOfUnmentionedNodeIndexList = new ArrayList<>();

                for (int i = 0; i < nodeList.size(); i++) {
                    List<Integer> mentionedNodeIndexList = nodeList.get(i).getMentionedIndexList();
                    if (judgeIntersection(mentionedNodeIndexList, unmentionedList)) {
                        listOfUnmentionedNodeIndexList.add(new ListEntry(mentionedNodeIndexList, 0));
                    }
                }

                //1, 2 (Alarm, John)
                for (int i = 0; i < eleToBeExtractedAndEliminatedList.size(); i++) {
                    //list contains 1 (Earthquake)
                    ArrayList<List<Integer>> tmpMentionedNodeIndexList = new ArrayList<>();
                    //[[1], [2, 0, 1], [3, 2], [4, 2]]
                    //[[3, 2], [4, 2]]
                    for (ListEntry listEntry : listOfUnmentionedNodeIndexList) {
                        if (listEntry.getList().contains(unmentionedList.get(i)) && listEntry.getUsingCondition() == 0) {
                            tmpMentionedNodeIndexList.add(listEntry.getList());
                        }
                    }
                    BigDecimal[][] tmpArr;
                    ArrayList<Node> newNodeList = new ArrayList<>();

                    //if integerList.size() == 1 e.g. P(E) --> P(e) + P(┐e) = 1
                    if (tmpMentionedNodeIndexList.size() != 1) {
                        tmpArr = new BigDecimal[0][];
                        for (int j = 0; j < tmpMentionedNodeIndexList.size() - 1; j++) {
                            if (tmpArr.length == 0) {
                                //Node{index=1, parentList=[], probabilityArr=[[0.02, 0.98]]}
                                node0 = getRequiredNode(nodeList, tmpMentionedNodeIndexList.get(0).get(0));
                                node1 = getRequiredNode(nodeList, tmpMentionedNodeIndexList.get(1).get(0));
                                node0ToBeDeleted = node0;
                                node1ToBeDeleted = node1;
                                //System.out.println("node0 = " + node0);
                                //System.out.println("node1 = " + node1);
                                int maxRow = Math.max(node0.getRow(), node1.getRow());

                                if (node0.getRow() == maxRow) {
                                    if (judgeIntersection(queryContent.getParentList(), node0.getMentionedIndexList())) {
                                        node0 = getArrWithMentionedEle(queryContent, node0);
                                    }

                                    if (judgeIntersection(queryContent.getParentList(), node1.getMentionedIndexList())) {
                                        node1 = getArrWithMentionedEle(queryContent, node1);
                                    }
                                } else {
                                    if (judgeIntersection(queryContent.getParentList(), node1.getMentionedIndexList())) {
                                        node1 = getArrWithMentionedEle(queryContent, node1);
                                    }

                                    if (judgeIntersection(queryContent.getParentList(), node0.getMentionedIndexList())) {
                                        node0 = getArrWithMentionedEle(queryContent, node0);
                                    }
                                }

                                Node newNode = getNewNode(node0, node1);
                                tmpArr = newNode.getProbabilityArr();
                                //System.out.println("newNode = " + newNode);
                                //System.out.println("Arrays.deepToString(newNode.getProbabilityArr()) = " + Arrays.deepToString(newNode.getProbabilityArr()));

                                setUsingCondition(listOfUnmentionedNodeIndexList, node0);
                                setUsingCondition(listOfUnmentionedNodeIndexList, node1);
                                nodeList.remove(node0ToBeDeleted);
                                nodeList.remove(node1ToBeDeleted);

                                newNodeList.add(newNode);
                            } else {
                                node0 = newNodeList.get(newNodeList.size() - 1);
                                node1 = getRequiredNode(nodeList, tmpMentionedNodeIndexList.get(j + 1).get(0));
                                node0ToBeDeleted = node0;
                                node1ToBeDeleted = node1;
                                //System.out.println("node0 = " + node0);
                                //System.out.println("node1 = " + node1);
                                int maxRow = Math.max(node0.getRow(), node1.getRow());

                                if (node0.getRow() == maxRow) {
                                    if (judgeIntersection(queryContent.getParentList(), node0.getMentionedIndexList())) {
                                        node0 = getArrWithMentionedEle(queryContent, node0);
                                    }

                                    if (judgeIntersection(queryContent.getParentList(), node1.getMentionedIndexList())) {
                                        node1 = getArrWithMentionedEle(queryContent, node1);
                                    }
                                } else {
                                    if (judgeIntersection(queryContent.getParentList(), node1.getMentionedIndexList())) {
                                        node1 = getArrWithMentionedEle(queryContent, node1);
                                    }

                                    if (judgeIntersection(queryContent.getParentList(), node0.getMentionedIndexList())) {
                                        node0 = getArrWithMentionedEle(queryContent, node0);
                                    }
                                }

                                Node newNode = getNewNode(node0, node1);
                                tmpArr = newNode.getProbabilityArr();
                                //System.out.println("newNode = " + newNode);
                                //System.out.println("Arrays.deepToString(newNode.getProbabilityArr()) = " + Arrays.deepToString(newNode.getProbabilityArr()));

                                newNodeList.add(newNode);
                                setUsingCondition(listOfUnmentionedNodeIndexList, node0);
                                setUsingCondition(listOfUnmentionedNodeIndexList, node1);
                                nodeList.remove(node0ToBeDeleted);
                                nodeList.remove(node1ToBeDeleted);
                            }
                        }
                    } else {
                        Integer ele = eleToBeExtractedAndEliminatedList.get(i);
                        for (Node nd : nodeList) {
                            if (nd.getMentionedIndexList().contains(ele)) {
                                node = nd;
                                break;
                            }
                        }

                        nodeList.remove(node);
                        if (node.getProbabilityArr().length > 1) {
                            newNodeList.add(node);
                        }
                    }

                    if (newNodeList.size() > 0) {
                        //remove current i (E)
                        //[0.00098, 0.97902]
                        //[0.0058, 0.0142]
                        //[0.9212, 0.0588]
                        //[0.0190, 0.0010]
                        //-->
                        //[0.00678, 0.99322]
                        //[0.9402, 0.0589]
                        Node nodeToBeEliminated = newNodeList.get(newNodeList.size() - 1);
                        int indexToBeEliminated = eleToBeExtractedAndEliminatedList.get(i);
                        Node eleEliminatedNode = getEleEliminatedNode(nodeToBeEliminated, indexToBeEliminated);
                        //System.out.println("eleEliminatedNode = " + eleEliminatedNode);
                        nodeList.add(eleEliminatedNode);

                        listOfUnmentionedNodeIndexList.add(new ListEntry(eleEliminatedNode.getMentionedIndexList(), 0));
                    }
                }

                //handle remained ele Burglar(0)
                listOfUnmentionedNodeIndexList = new ArrayList<>();

                for (Node node : nodeList) {
                    List<Integer> mentionedNodeIndexList = node.getMentionedIndexList();
                    listOfUnmentionedNodeIndexList.add(new ListEntry(mentionedNodeIndexList, 0));
                }

                int length = nodeList.size();
                for (int i = 0; i < length - 1; i++) {
                    //list contains 1 (Earthquake)
                    ArrayList<List<Integer>> tmpMentionedNodeIndexList = new ArrayList<>();
                    //[[1], [2, 0, 1], [3, 2], [4, 2]]

                    BigDecimal[][] tmpArr;
                    Node smallNode;
                    Node bigNode;

                    node0 = nodeList.get(0);
                    node1 = nodeList.get(1);
                    node0ToBeDeleted = node0;
                    node1ToBeDeleted = node1;
                    //System.out.println("node0 = " + node0);
                    //System.out.println("node1 = " + node1);
                    int maxRow = Math.max(node0.getRow(), node1.getRow());

                    if (node0.getRow() == maxRow) {
                        if (judgeIntersection(queryContent.getParentList(), node0.getMentionedIndexList())) {
                            node0 = getArrWithMentionedEle(queryContent, node0);
                        }

                        if (judgeIntersection(queryContent.getParentList(), node1.getMentionedIndexList())) {
                            node1 = getArrWithMentionedEle(queryContent, node1);
                        }
                    } else {
                        if (judgeIntersection(queryContent.getParentList(), node1.getMentionedIndexList())) {
                            node1 = getArrWithMentionedEle(queryContent, node1);
                        }

                        if (judgeIntersection(queryContent.getParentList(), node0.getMentionedIndexList())) {
                            node0 = getArrWithMentionedEle(queryContent, node0);
                        }
                    }

                    Node newNode = getNewNode(node0, node1);
                    //System.out.println("newNode = " + newNode);
                    //System.out.println("Arrays.deepToString(newNode.getProbabilityArr()) = " + Arrays.deepToString(newNode.getProbabilityArr()));

                    nodeList.add(newNode);
                    setUsingCondition(listOfUnmentionedNodeIndexList, node0);
                    setUsingCondition(listOfUnmentionedNodeIndexList, node1);
                    nodeList.remove(node0ToBeDeleted);
                    nodeList.remove(node1ToBeDeleted);
                }
                BigDecimal[][] probabilityArr = nodeList.get(0).getProbabilityArr();
                BigDecimal a = BigDecimal.valueOf(1 / (probabilityArr[0][0].add(probabilityArr[0][1]).doubleValue()));
                System.out.println(probabilityArr[0][0].multiply(a) + " " + probabilityArr[0][1].multiply(a));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Node getNewNode(Node node0, Node node1) {
        Node nodeToBeReturned = null;
        Node bigNode;
        Node smallNode;
        BigDecimal[][] arrToBeReturnedThroughNode;

        if (node0.getProbabilityArr().length >= node1.getProbabilityArr().length) {
            bigNode = node0;
            smallNode = node1;
        } else {
            bigNode = node1;
            smallNode = node0;
        }

        if (bigNode.getProbabilityArr().length == 1 && smallNode.getProbabilityArr().length == 1) {
            if (bigNode.getNameIndex() == -1 && smallNode.getNameIndex() == -1) {
                arrToBeReturnedThroughNode = new BigDecimal[1][COLUMN];
                arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);

                List<Integer> parentList = new ArrayList<>();
                nodeToBeReturned = new Node(-1, parentList, arrToBeReturnedThroughNode);
                //smallNode.getNameIndex() != -1
            } else if (bigNode.getNameIndex() == -1) {
                arrToBeReturnedThroughNode = new BigDecimal[1][COLUMN];
                arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);

                nodeToBeReturned = new Node(smallNode.getNameIndex(), smallNode.getParentList(), arrToBeReturnedThroughNode);
                //bigNode.getNameIndex() != -1
            } else if (smallNode.getNameIndex() == -1) {
                arrToBeReturnedThroughNode = new BigDecimal[1][COLUMN];
                arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);

                nodeToBeReturned = new Node(bigNode.getNameIndex(), bigNode.getParentList(), arrToBeReturnedThroughNode);
            } else if (bigNode.getNameIndex() != -1 && smallNode.getNameIndex() != -1) {
                int nameIndex;
                if (bigNode.getNameIndex() == smallNode.getNameIndex()) {
                    arrToBeReturnedThroughNode = new BigDecimal[1][COLUMN];
                    arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);

                    nodeToBeReturned = new Node(bigNode.getNameIndex(), bigNode.getParentList(), arrToBeReturnedThroughNode);
                } else {
                    arrToBeReturnedThroughNode = new BigDecimal[2][COLUMN];
                    nameIndex = bigNode.getNameIndex();
                    arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][0]);

                    List<Integer> parentList = new ArrayList<>();
                    parentList.add(smallNode.getNameIndex());
                    nodeToBeReturned = new Node(nameIndex, parentList, arrToBeReturnedThroughNode);
                }
            }
        } else if (bigNode.getProbabilityArr().length == 2 && smallNode.getProbabilityArr().length == 1) {
            if (smallNode.getNameIndex() == -1) {
                arrToBeReturnedThroughNode = new BigDecimal[2][COLUMN];
                for (int i = 0; i < arrToBeReturnedThroughNode.length; i++) {
                    for (int j = 0; j < arrToBeReturnedThroughNode[i].length; j++) {
                        arrToBeReturnedThroughNode[i][j] = bigNode.getProbabilityArr()[i][j].multiply(smallNode.getProbabilityArr()[0][j]);
                    }
                }

                nodeToBeReturned = new Node(bigNode.getNameIndex(), bigNode.getParentList(), arrToBeReturnedThroughNode);
                //no index intersected
            } else if (getIntersection(bigNode.getMentionedIndexList(), smallNode.getMentionedIndexList()).size() == 0) {
                arrToBeReturnedThroughNode = new BigDecimal[4][COLUMN];
                arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[2][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[2][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[3][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[3][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][0]);

                List<Integer> parentList = new ArrayList<>();
                parentList.addAll(bigNode.getParentList());
                parentList.addAll(smallNode.getParentList());
                nodeToBeReturned = new Node(bigNode.getNameIndex(), parentList, arrToBeReturnedThroughNode);
            } else if (getIntersection(bigNode.getMentionedIndexList(), smallNode.getMentionedIndexList()).size() == 1) {
                arrToBeReturnedThroughNode = new BigDecimal[2][COLUMN];
                List<Integer> intersectionList = getIntersection(bigNode.getMentionedIndexList(), smallNode.getMentionedIndexList());
                //both have ele bigNode.getNameIndex()
                if (intersectionList.contains(bigNode.getNameIndex())) {
                    for (int i = 0; i < arrToBeReturnedThroughNode.length; i++) {
                        for (int j = 0; j < arrToBeReturnedThroughNode[i].length; j++) {
                            arrToBeReturnedThroughNode[i][j] = bigNode.getProbabilityArr()[i][j].multiply(smallNode.getProbabilityArr()[0][j]);
                        }
                    }

                    nodeToBeReturned = new Node(bigNode.getNameIndex(), bigNode.getParentList(), arrToBeReturnedThroughNode);
                } else if (judgeIntersection(intersectionList, bigNode.getParentList())) {
                    arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][0]);

                    nodeToBeReturned = new Node(bigNode.getNameIndex(), bigNode.getParentList(), arrToBeReturnedThroughNode);
                }
            }
        } else if (bigNode.getProbabilityArr().length == 2 && smallNode.getProbabilityArr().length == 2) {
            if (getIntersection(bigNode.getMentionedIndexList(), smallNode.getMentionedIndexList()).size() == 0) {
                arrToBeReturnedThroughNode = new BigDecimal[8][COLUMN];
                arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[1][1]);
                arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[1][1]);
                arrToBeReturnedThroughNode[2][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[2][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[3][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[1][0]);
                arrToBeReturnedThroughNode[3][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[1][0]);
                arrToBeReturnedThroughNode[4][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[4][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[5][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[1][1]);
                arrToBeReturnedThroughNode[5][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[1][1]);
                arrToBeReturnedThroughNode[6][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[6][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[7][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[1][0]);
                arrToBeReturnedThroughNode[7][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[1][0]);

                List<Integer> parentList = new ArrayList<>();
                parentList.addAll(bigNode.getParentList());
                parentList.addAll(smallNode.getMentionedIndexList());

                nodeToBeReturned = new Node(bigNode.getNameIndex(), parentList, arrToBeReturnedThroughNode);
            } else if (getIntersection(bigNode.getMentionedIndexList(), smallNode.getMentionedIndexList()).size() == 1) {
                arrToBeReturnedThroughNode = new BigDecimal[4][COLUMN];
                //common index in name index
                if (bigNode.getNameIndex() == smallNode.getNameIndex()) {
                    arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[1][0]);
                    arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[1][1]);
                    arrToBeReturnedThroughNode[2][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[2][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[3][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[1][0]);
                    arrToBeReturnedThroughNode[3][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[1][1]);

                    List<Integer> parentList = new ArrayList<>();
                    parentList.addAll(bigNode.getParentList());
                    parentList.addAll(smallNode.getParentList());

                    nodeToBeReturned = new Node(bigNode.getNameIndex(), parentList, arrToBeReturnedThroughNode);
                    //common index in parent
                } else if (bigNode.getParentList().toString().equals(smallNode.getParentList().toString())) {
                    arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[1][1]);
                    arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[1][0]);
                    arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[2][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[1][1]);
                    arrToBeReturnedThroughNode[2][1] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[3][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[1][0]);
                    arrToBeReturnedThroughNode[3][1] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);

                    int nameIndex = bigNode.getParentList().get(0);
                    List<Integer> parentList = new ArrayList<>();
                    parentList.add(bigNode.getNameIndex(), smallNode.getNameIndex());

                    nodeToBeReturned = new Node(nameIndex, parentList, arrToBeReturnedThroughNode);
                } else if (isIntersectedInDifferentPosition(bigNode, smallNode)) {
                    List<Integer> intersectionList = getIntersection(bigNode.getMentionedIndexList(), smallNode.getMentionedIndexList());

                    //need to swap small and big node --> common index in small node's name index and big node's parent
                    if (intersectionList.contains(smallNode.getNameIndex()) && judgeIntersection(bigNode.getParentList(), intersectionList)) {
                        Node tmp = smallNode;
                        smallNode = bigNode;
                        bigNode = tmp;
                    }

                    //common index in big node's name index and small node's parent
                    arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[1][1]);
                    arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[1][0]);
                    arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[2][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[1][1]);
                    arrToBeReturnedThroughNode[2][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[3][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[1][0]);
                    arrToBeReturnedThroughNode[3][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][0]);

                    List<Integer> parentList = new ArrayList<>();
                    parentList.addAll(bigNode.getParentList());
                    parentList.add(smallNode.getNameIndex());

                    nodeToBeReturned = new Node(bigNode.getNameIndex(), parentList, arrToBeReturnedThroughNode);
                }
            }
        } else if (bigNode.getProbabilityArr().length == 4 && smallNode.getProbabilityArr().length == 1) {
            if (getIntersection(bigNode.getMentionedIndexList(), smallNode.getMentionedIndexList()).size() == 0) {
                arrToBeReturnedThroughNode = new BigDecimal[8][COLUMN];
                arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[2][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[2][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[3][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[3][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[4][0] = bigNode.getProbabilityArr()[2][0].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[4][1] = bigNode.getProbabilityArr()[2][1].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[5][0] = bigNode.getProbabilityArr()[2][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[5][1] = bigNode.getProbabilityArr()[2][1].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[6][0] = bigNode.getProbabilityArr()[3][0].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[6][1] = bigNode.getProbabilityArr()[3][1].multiply(smallNode.getProbabilityArr()[0][1]);
                arrToBeReturnedThroughNode[7][0] = bigNode.getProbabilityArr()[3][0].multiply(smallNode.getProbabilityArr()[0][0]);
                arrToBeReturnedThroughNode[7][1] = bigNode.getProbabilityArr()[3][1].multiply(smallNode.getProbabilityArr()[0][0]);

                List<Integer> parentList = bigNode.getParentList();
                parentList.addAll(smallNode.getParentList());

                nodeToBeReturned = new Node(bigNode.getNameIndex(), parentList, arrToBeReturnedThroughNode);
            } else if (getIntersection(bigNode.getMentionedIndexList(), smallNode.getMentionedIndexList()).size() == 1) {
                arrToBeReturnedThroughNode = new BigDecimal[4][COLUMN];

                //intersected in name index
                if (bigNode.getNameIndex() == smallNode.getNameIndex()) {
                    for (int i = 0; i < arrToBeReturnedThroughNode.length; i++) {
                        for (int j = 0; j < arrToBeReturnedThroughNode[i].length; j++) {
                            arrToBeReturnedThroughNode[i][j] = bigNode.getProbabilityArr()[i][j].multiply(smallNode.getProbabilityArr()[0][j]);
                        }
                    }

                    nodeToBeReturned = new Node(bigNode.getNameIndex(), bigNode.getParentList(), arrToBeReturnedThroughNode);
                } else if (smallNode.getNameIndex() == bigNode.getParentList().get(0)) {
                    arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[2][0] = bigNode.getProbabilityArr()[2][0].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[2][1] = bigNode.getProbabilityArr()[2][1].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[3][0] = bigNode.getProbabilityArr()[3][0].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[3][1] = bigNode.getProbabilityArr()[3][1].multiply(smallNode.getProbabilityArr()[0][0]);

                    nodeToBeReturned = new Node(bigNode.getNameIndex(), bigNode.getParentList(), arrToBeReturnedThroughNode);
                } else if (smallNode.getNameIndex() == bigNode.getParentList().get(1)) {
                    arrToBeReturnedThroughNode[0][0] = bigNode.getProbabilityArr()[0][0].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[0][1] = bigNode.getProbabilityArr()[0][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[1][0] = bigNode.getProbabilityArr()[1][0].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[1][1] = bigNode.getProbabilityArr()[1][1].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[2][0] = bigNode.getProbabilityArr()[2][0].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[2][1] = bigNode.getProbabilityArr()[2][1].multiply(smallNode.getProbabilityArr()[0][1]);
                    arrToBeReturnedThroughNode[3][0] = bigNode.getProbabilityArr()[3][0].multiply(smallNode.getProbabilityArr()[0][0]);
                    arrToBeReturnedThroughNode[3][1] = bigNode.getProbabilityArr()[3][1].multiply(smallNode.getProbabilityArr()[0][0]);

                    nodeToBeReturned = new Node(bigNode.getNameIndex(), bigNode.getParentList(), arrToBeReturnedThroughNode);
                }
            }
        }

        return nodeToBeReturned;
    }

    /**
     * judge if node1's name index and node2's parent has common index, or node2's name index and node1's parent has common index
     *
     * @param node1 first input node
     * @param node2 second input node
     * @return true: has common index (node1's name index and node2's parent, or node2's name index and node1's parent)
     */
    private static boolean isIntersectedInDifferentPosition(Node node1, Node node2) {
        if (!judgeIntersection(node1.getMentionedIndexList(), node2.getMentionedIndexList())) {
            return false;
        }

        List<Integer> intersectionList = getIntersection(node1.getMentionedIndexList(), node2.getMentionedIndexList());

        if (intersectionList.contains(node1.getNameIndex()) && judgeIntersection(intersectionList, node2.getParentList())) {
            return true;
        }

        if (intersectionList.contains(node2.getNameIndex()) && judgeIntersection(intersectionList, node1.getParentList())) {
            return true;
        }

        return false;
    }

    private static Node getEleEliminatedNode(Node nodeEleToBeEliminated, int indexToBeEliminated) {
        BigDecimal[][] srcProbabilityArr = nodeEleToBeEliminated.getProbabilityArr();
        int srcNameIndex = nodeEleToBeEliminated.getNameIndex();
        List<Integer> srcParentList = nodeEleToBeEliminated.getParentList();
        BigDecimal[][] probabilityArrToBeReturned;

        //srcParentList.size() = 1
        if (srcProbabilityArr.length == 2) {
            probabilityArrToBeReturned = new BigDecimal[1][COLUMN];
            //change name index
            if (indexToBeEliminated == srcNameIndex) {
                int newNameIndex = srcParentList.get(0);
                List<Integer> newParentList = new ArrayList<>();
                probabilityArrToBeReturned[0][0] = srcProbabilityArr[1][0].add(srcProbabilityArr[1][1]);
                probabilityArrToBeReturned[0][1] = srcProbabilityArr[0][0].add(srcProbabilityArr[0][1]);

                return new Node(newNameIndex, newParentList, probabilityArrToBeReturned);
                //do not change name index
            } else {
                List<Integer> newParentList = new ArrayList<>();
                probabilityArrToBeReturned[0][0] = srcProbabilityArr[0][0].add(srcProbabilityArr[1][0]);
                probabilityArrToBeReturned[0][1] = srcProbabilityArr[0][1].add(srcProbabilityArr[1][1]);

                return new Node(srcNameIndex, newParentList, probabilityArrToBeReturned);
            }
            //srcParentList.size() = 2
        } else if (srcProbabilityArr.length == 4) {
            probabilityArrToBeReturned = new BigDecimal[2][COLUMN];
            //change name index
            if (indexToBeEliminated == srcNameIndex) {
                int newNameIndex = srcParentList.get(0);
                srcParentList.remove(srcParentList.get(0));
                probabilityArrToBeReturned[0][0] = srcProbabilityArr[2][0].add(srcProbabilityArr[2][1]);
                probabilityArrToBeReturned[0][1] = srcProbabilityArr[0][0].add(srcProbabilityArr[0][1]);
                probabilityArrToBeReturned[1][0] = srcProbabilityArr[3][0].add(srcProbabilityArr[3][1]);
                probabilityArrToBeReturned[1][1] = srcProbabilityArr[1][0].add(srcProbabilityArr[1][1]);

                return new Node(newNameIndex, srcParentList, probabilityArrToBeReturned);
                //do not change name index
            } else {
                if (indexToBeEliminated == srcParentList.get(0)) {
                    srcParentList.remove(srcParentList.get(0));
                    probabilityArrToBeReturned[0][0] = srcProbabilityArr[0][0].add(srcProbabilityArr[2][0]);
                    probabilityArrToBeReturned[0][1] = srcProbabilityArr[0][1].add(srcProbabilityArr[2][1]);
                    probabilityArrToBeReturned[1][0] = srcProbabilityArr[1][0].add(srcProbabilityArr[3][0]);
                    probabilityArrToBeReturned[1][1] = srcProbabilityArr[1][1].add(srcProbabilityArr[3][1]);

                    return new Node(srcNameIndex, srcParentList, probabilityArrToBeReturned);
                } else if (indexToBeEliminated == srcParentList.get(1)) {
                    srcParentList.remove(srcParentList.get(1));
                    probabilityArrToBeReturned[0][0] = srcProbabilityArr[0][0].add(srcProbabilityArr[1][0]);
                    probabilityArrToBeReturned[0][1] = srcProbabilityArr[0][1].add(srcProbabilityArr[1][1]);
                    probabilityArrToBeReturned[1][0] = srcProbabilityArr[2][0].add(srcProbabilityArr[3][0]);
                    probabilityArrToBeReturned[1][1] = srcProbabilityArr[2][1].add(srcProbabilityArr[3][1]);

                    return new Node(srcNameIndex, srcParentList, probabilityArrToBeReturned);
                }
            }
        }

        return null;
    }

    private static int getOccurrenceNum(int[][] hierarchicalMatrix, int index) {
        int num = 1;

        for (int i : hierarchicalMatrix[index]) {
            if (i == 1) {
                num++;
            }
        }

        return num;
    }

    private static boolean judgeAdd(List<Integer> unmentionedList, ArrayList<Integer> certainList, Integer ele, List<Integer> parentList, int occurrenceNum) {
        if (occurrenceNum == 1 && unmentionedList.contains(ele) && latterIsSubsetOfFormer(certainList, parentList)) {
            return false;
        }

        if (parentList == null || parentList.size() == 0) {
            if (certainList.contains(ele)) {
                return false;
            }
        }

        return true;
        //return (occurrenceNum == 1) && (!unmentionedList.contains(ele) || !latterIsSubsetOfFormer(certainList, parentList));
    }

    private static boolean latterIsSubsetOfFormer(ArrayList<Integer> certainList, List<Integer> parentList) {
        for (Integer parentInteger : parentList) {
            if (!certainList.contains(parentInteger)) {
                return false;
            }
        }

        return true;
    }

    private static Node getRequiredNode(List<Node> nodeList, Integer nameIndex) {
        for (Node node : nodeList) {
            if (node.getNameIndex() == nameIndex) {
                return node;

            }
        }

        return null;
    }
    
    private static void setUsingCondition(ArrayList<ListEntry> listOfUnmentionedNodeIndexList, Node node) {
        List<Integer> mentionedIndexList = node.getMentionedIndexList();
        for (ListEntry listEntry : listOfUnmentionedNodeIndexList) {
            if (listEntry.getList().toString().equals(mentionedIndexList.toString())) {
                listEntry.setUsingCondition(1);
                return;
            }
        }
    }

    private static Node getNewNode(Node bigNode, Node smallNode, BigDecimal[][] tmpArr, QueryContent queryContent, List<Integer> certainList, Integer eleToBeEliminated) {
        List<Integer> parentList = new ArrayList<>();

        List<Integer> intersectionParent0 = getIntersection(bigNode.getParentList(), queryContent.getParentList());
        List<Integer> intersectionParent1 = getIntersection(smallNode.getParentList(), queryContent.getParentList());

        for (Integer integer : bigNode.getParentList()) {
            if (!parentList.contains(integer)) {
                parentList.add(integer);
            }
        }

        for (Integer integer : smallNode.getParentList()) {
            if (!parentList.contains(integer)) {
                parentList.add(integer);
            }
        }

        parentList.removeAll(intersectionParent0);
        parentList.removeAll(intersectionParent1);

        Integer bigNodeNameIndex = bigNode.getNameIndex();
        Integer smallNodeNameIndex = smallNode.getNameIndex();
        if (certainList.contains(bigNodeNameIndex) && certainList.contains(smallNodeNameIndex) && bigNode.getParentList().size() == 1) {
            Integer nameIndex = parentList.get(0);
            parentList.remove(nameIndex);
            return new Node(nameIndex, parentList, tmpArr);
        }

        return new Node(bigNode.getNameIndex(), parentList, tmpArr);
    }

    private static Node getArrWithMentionedEle(QueryContent queryContent, Node node) {
        BigDecimal[][] tmpArr;
        Node nodeToBeReturned = null;
        List<Integer> intersection = getIntersection(queryContent.getParentList(), node.getMentionedIndexList());
        List<int[]> intersectionOrdinal = new ArrayList<>();
        for (Integer intersectionInteger : intersection) {
            int ordinal = node.getOrdinalFromParentList(intersectionInteger);
            intersectionOrdinal.add(new int[]{ordinal, queryContent.getConditionBoolNum(intersectionInteger)});
        }

        intersectionOrdinal.sort(Comparator.comparingInt(o -> o[0]));

        //do not change name Index
        if (node.getProbabilityArr().length == 1) {
            tmpArr = new BigDecimal[1][COLUMN];
            if (intersectionOrdinal.get(0)[0] == -1) {
                // only retain false (second value)
                if (intersectionOrdinal.get(0)[1] == 0) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[0][1];
                    }
                    // only retain true (first value)
                } else {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[0][0];
                    }
                }
            }

            nodeToBeReturned = new Node(-1, node.getParentList(), tmpArr);
        } else if (node.getProbabilityArr().length == 2) {
            tmpArr = new BigDecimal[1][COLUMN];
            if (intersectionOrdinal.size() == 1) {
                //change name index
                if (intersectionOrdinal.get(0)[0] == -1) {
                    // only retain false (second value)
                    if (intersectionOrdinal.get(0)[1] == 0) {
                        tmpArr[0][0] = node.getProbabilityArr()[1][1];
                        tmpArr[0][1] = node.getProbabilityArr()[0][1];
                        // only retain true (first value)
                    } else {
                        tmpArr[0][0] = node.getProbabilityArr()[1][0];
                        tmpArr[0][1] = node.getProbabilityArr()[0][0];
                    }

                    Integer nameIndex = node.getParentList().get(0);
                    List<Integer> parentList = node.getParentList();
                    parentList.remove(nameIndex);
                    nodeToBeReturned = new Node(nameIndex, parentList, tmpArr);
                    //do not change name index
                } else {
                    // only retain false (first value in a column)
                    if (intersectionOrdinal.get(0)[1] == 0) {
                        tmpArr[0][0] = node.getProbabilityArr()[0][0];
                        tmpArr[0][1] = node.getProbabilityArr()[0][1];
                        // only retain true (second value in a column)
                    } else {
                        tmpArr[0][0] = node.getProbabilityArr()[1][0];
                        tmpArr[0][1] = node.getProbabilityArr()[1][1];
                    }

                    int index = intersectionOrdinal.get(0)[0] - 1;
                    List<Integer> parentList = node.getParentList();
                    parentList.remove(index);
                    nodeToBeReturned = new Node(node.getNameIndex(), parentList, tmpArr);
                }
                //intersectionOrdinal.size() == 2, cut lines and make column same (change name index)
            } else {
                List<Integer> trueOrFalseList = getTrueOrFalseList(intersectionOrdinal);
                if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 0) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[0][0];
                    }
                } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 0) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[0][1];
                    }
                } else if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 1) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[1][0];
                    }
                } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 1) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[1][1];
                    }
                }

                int index = intersectionOrdinal.get(1)[0] - 1;
                List<Integer> parentList = node.getParentList();
                parentList.remove(index);
                nodeToBeReturned = new Node(-1, parentList, tmpArr);
            }
            //node.getProbabilityArr().length == 4
        } else {
            List<Integer> ordinalList = getOrdinalList(intersectionOrdinal);
            List<Integer> trueOrFalseList = getTrueOrFalseList(intersectionOrdinal);
            if (ordinalList.size() == 1) {
                tmpArr = new BigDecimal[2][COLUMN];
                //change name index
                if (ordinalList.get(0) == -1) {
                    //src name index == true
                    if (trueOrFalseList.get(0) == 1) {
                        tmpArr[0][0] = node.getProbabilityArr()[2][0];
                        tmpArr[0][1] = node.getProbabilityArr()[0][0];
                        tmpArr[1][0] = node.getProbabilityArr()[3][0];
                        tmpArr[1][1] = node.getProbabilityArr()[1][0];
                        //src name index ==false
                    } else {
                        tmpArr[0][0] = node.getProbabilityArr()[2][1];
                        tmpArr[0][1] = node.getProbabilityArr()[0][1];
                        tmpArr[1][0] = node.getProbabilityArr()[3][1];
                        tmpArr[1][1] = node.getProbabilityArr()[1][1];
                    }

                    Integer nameIndex = node.getParentList().get(0);
                    List<Integer> parentList = node.getParentList();
                    parentList.remove(nameIndex);
                    nodeToBeReturned = new Node(nameIndex, parentList, tmpArr);
                    //do not change name index
                    //handle first element in parent list
                } else if (ordinalList.get(0) == 1) {
                    //ordinal ele == true
                    if (trueOrFalseList.get(0) == 1) {
                        tmpArr[0][0] = node.getProbabilityArr()[2][0];
                        tmpArr[0][1] = node.getProbabilityArr()[2][1];
                        tmpArr[1][0] = node.getProbabilityArr()[3][0];
                        tmpArr[1][1] = node.getProbabilityArr()[3][1];
                        //ordinal ele ==false
                    } else {
                        tmpArr[0][0] = node.getProbabilityArr()[0][0];
                        tmpArr[0][1] = node.getProbabilityArr()[0][1];
                        tmpArr[1][0] = node.getProbabilityArr()[1][0];
                        tmpArr[1][1] = node.getProbabilityArr()[1][1];
                    }

                    int index = intersectionOrdinal.get(0)[0] - 1;
                    List<Integer> parentList = node.getParentList();
                    parentList.remove(index);
                    nodeToBeReturned = new Node(node.getNameIndex(), parentList, tmpArr);
                    //handle second element in parent list
                    //ordinalList.get(0) == 2
                } else {
                    //ordinal ele == true
                    if (trueOrFalseList.get(0) == 1) {
                        tmpArr[0][0] = node.getProbabilityArr()[1][0];
                        tmpArr[0][1] = node.getProbabilityArr()[1][1];
                        tmpArr[1][0] = node.getProbabilityArr()[3][0];
                        tmpArr[1][1] = node.getProbabilityArr()[3][1];
                        //ordinal ele ==false
                    } else {
                        tmpArr[0][0] = node.getProbabilityArr()[0][0];
                        tmpArr[0][1] = node.getProbabilityArr()[0][1];
                        tmpArr[1][0] = node.getProbabilityArr()[2][0];
                        tmpArr[1][1] = node.getProbabilityArr()[2][1];
                    }

                    int index = intersectionOrdinal.get(0)[0] - 1;
                    List<Integer> parentList = node.getParentList();
                    parentList.remove(index);
                    nodeToBeReturned = new Node(node.getNameIndex(), parentList, tmpArr);
                }
            } else if (ordinalList.size() == 2) {
                tmpArr = new BigDecimal[1][COLUMN];
                //change name index
                if (ordinalList.get(0) == -1) {
                    if (ordinalList.get(1) == 1) {
                        if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 0) {
                            tmpArr[0][0] = node.getProbabilityArr()[1][0];
                            tmpArr[0][1] = node.getProbabilityArr()[0][0];
                        } else if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 1) {
                            tmpArr[0][0] = node.getProbabilityArr()[3][0];
                            tmpArr[0][1] = node.getProbabilityArr()[2][0];
                        } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 0) {
                            tmpArr[0][0] = node.getProbabilityArr()[1][1];
                            tmpArr[0][1] = node.getProbabilityArr()[0][1];
                        } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 1) {
                            tmpArr[0][0] = node.getProbabilityArr()[3][1];
                            tmpArr[0][1] = node.getProbabilityArr()[2][1];
                        }

                        List<Integer> parentList = new ArrayList<>();
                        nodeToBeReturned = new Node(node.getParentList().get(1), parentList, tmpArr);
                        //ordinalList.get(1) == 2
                    } else {
                        if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 0) {
                            tmpArr[0][0] = node.getProbabilityArr()[2][0];
                            tmpArr[0][1] = node.getProbabilityArr()[0][0];
                        } else if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 1) {
                            tmpArr[0][0] = node.getProbabilityArr()[3][0];
                            tmpArr[0][1] = node.getProbabilityArr()[1][0];
                        } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 0) {
                            tmpArr[0][0] = node.getProbabilityArr()[2][1];
                            tmpArr[0][1] = node.getProbabilityArr()[0][1];
                        } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 1) {
                            tmpArr[0][0] = node.getProbabilityArr()[3][1];
                            tmpArr[0][1] = node.getProbabilityArr()[1][1];
                        }

                        List<Integer> parentList = new ArrayList<>();
                        nodeToBeReturned = new Node(node.getParentList().get(0), parentList, tmpArr);
                    }
                    //do not change name index, ordinalList = 1, 2
                } else {
                    if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 0) {
                        tmpArr[0][0] = node.getProbabilityArr()[0][0];
                        tmpArr[0][1] = node.getProbabilityArr()[0][1];
                    } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 1) {
                        tmpArr[0][0] = node.getProbabilityArr()[1][0];
                        tmpArr[0][1] = node.getProbabilityArr()[1][1];
                    } else if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 0) {
                        tmpArr[0][0] = node.getProbabilityArr()[2][0];
                        tmpArr[0][1] = node.getProbabilityArr()[2][1];
                    } else if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 1) {
                        tmpArr[0][0] = node.getProbabilityArr()[3][0];
                        tmpArr[0][1] = node.getProbabilityArr()[3][1];
                    }

                    List<Integer> parentList = new ArrayList<>();
                    nodeToBeReturned = new Node(node.getNameIndex(), parentList, tmpArr);
                }
                //remain one line with same ele
            } else if (ordinalList.size() == 3) {
                tmpArr = new BigDecimal[1][COLUMN];
                if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 0 && trueOrFalseList.get(2) == 0) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[0][0];
                    }
                } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 0 && trueOrFalseList.get(2) == 0) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[0][1];
                    }
                } else if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 0 && trueOrFalseList.get(2) == 1) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[1][0];
                    }
                } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 0 && trueOrFalseList.get(2) == 1) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[1][1];
                    }
                } else if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 1 && trueOrFalseList.get(2) == 0) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[2][0];
                    }
                } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 1 && trueOrFalseList.get(2) == 0) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[2][1];
                    }
                } else if (trueOrFalseList.get(0) == 1 && trueOrFalseList.get(1) == 1 && trueOrFalseList.get(2) == 1) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[3][0];
                    }
                } else if (trueOrFalseList.get(0) == 0 && trueOrFalseList.get(1) == 1 && trueOrFalseList.get(2) == 1) {
                    for (int i = 0; i < 2; i++) {
                        tmpArr[0][i] = node.getProbabilityArr()[3][1];
                    }
                }

                List<Integer> parentList = new ArrayList<>();
                nodeToBeReturned = new Node(-1, parentList, tmpArr);
            }
        }

        return nodeToBeReturned;
    }

    private static List<Integer> getTrueOrFalseList(List<int[]> intersectionOrdinal) {
        List<Integer> integers = new ArrayList<>();

        for (int[] ints : intersectionOrdinal) {
            integers.add(ints[1]);
        }

        return integers;
    }

    private static List<Integer> getOrdinalList(List<int[]> intersectionOrdinal) {
        List<Integer> integers = new ArrayList<>();

        for (int[] ints : intersectionOrdinal) {
            integers.add(ints[0]);
        }

        return integers;
    }

    private static boolean judgeIntersection(List<Integer> list1, List<Integer> list2) {
        List<Integer> retainedList = new ArrayList<>(list1);
        retainedList.retainAll(list2);
        return retainedList.size() > 0;
    }

    private static List<Integer> getIntersection(List<Integer> list1, List<Integer> list2) {
        List<Integer> retainedList = new ArrayList<>(list1);
        retainedList.retainAll(list2);
        return retainedList;
    }

    /**
     * return row index by boolean value
     * e.g. 2 conditions
     * 0, 0 -> 0
     * 0, 1 -> 1
     * 1, 0 -> 2
     * 1, 1 -> 3
     *
     * @param queryContent: condition probability
     * @return row index
     */
    private static int getRowIndexOfConditionProbability(QueryContent queryContent) {
        //2
        int size = queryContent.getConditionList().size();

        //check the value of size
        if (size == 0) {
            return 0;
        }

        int multiplier = 1;
        int rowIndex = 0;
        for (int i = queryContent.getConditionList().size() - 1; i >= 0; i--) {
            rowIndex += multiplier * queryContent.getConditionList().get(i).getBool();
            multiplier *= 2;
        }

        return rowIndex;
    }

    /**
     * judge whether the condition probability needs to calculate
     *
     * @param hierarchicalMatrix: show hierarchy
     * @param queryContent:       condition probability
     * @return true(do not need to calculate)/ false(need to calculate)
     */
    private static boolean probabilityExist(int[][] hierarchicalMatrix, QueryContent queryContent) {
        //0, 1
        List<Integer> parentList = getParentList(hierarchicalMatrix, queryContent);
        //0, 1
        List<Integer> conditionIndexList = new ArrayList<>();
        for (Condition condition : queryContent.getConditionList()) {
            conditionIndexList.add(condition.getConditionIndex());
        }

        return parentList.toString().equals(conditionIndexList.toString());
    }

    /**
     * @param hierarchicalMatrix: show hierarchy
     * @param queryContent:       condition probability
     * @return list of parents' index
     */
    private static List<Integer> getParentList(int[][] hierarchicalMatrix, QueryContent queryContent) {
        List<Integer> parentList = new ArrayList<>();
        //2
        int nameIndex = queryContent.getNameIndex();

        for (int i = 0; i < hierarchicalMatrix.length; i++) {
            if (hierarchicalMatrix[i][nameIndex] == 1) {
                parentList.add(i);
            }
        }

        //0,1
        return parentList;
    }

    private static List<Integer> getParentList(int[][] hierarchicalMatrix, int index) {
        List<Integer> parentList = new ArrayList<>();

        for (int i = 0; i < hierarchicalMatrix.length; i++) {
            if (hierarchicalMatrix[i][index] == 1) {
                parentList.add(i);
            }
        }

        //0,1
        return parentList;
    }

    /**
     * turn false/true to 0/1
     *
     * @param s false/true
     * @return 0/1
     */
    private static int turnBoolToInt(String s) {
        if ("false".equals(s)) {
            return 0;
        }

        return 1;
    }

    /**
     * turn index to name
     * e.g. Alarm -> 2
     *
     * @param varArr: array of variables
     * @param name:   case name
     * @return index
     */
    private static int getIndexByName(String[] varArr, String name) {
        int index = 0;

        for (int i = 0; i < varArr.length; i++) {
            if (name.equals(varArr[i])) {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * @param matrix: show parents' number for all conditional probability
     * @param index:  show parents' number for a certain conditional probability
     * @return how many lines need to receive for a given conditional probability
     */
    private static int getProbabilityRow(int[][] matrix, int index) {
        int parentNum = getParentNum(matrix, index);

        int line = 1;

        for (int i = 0; i < parentNum; i++) {
            line *= 2;
        }

        return line;
    }

    private static int getParentNum(int[][] matrix, int index) {
        int parentNum = 0;

        for (int[] row : matrix) {
            parentNum += (row[index] == 1 ? 1 : 0);
        }
        return parentNum;
    }

    private static int compare(Integer o1, Integer o2) {
        return o1 - o2;
    }
}


class ListEntry {
    private List<Integer> list;
    /**
     * 0: unused; 1: used
     */
    private int usingCondition;

    public ListEntry() {
        this.list = new ArrayList<>();
    }

    public ListEntry(List<Integer> list, int usingCondition) {
        this.list = list;
        this.usingCondition = usingCondition;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public int getUsingCondition() {
        return usingCondition;
    }

    public void setUsingCondition(int usingCondition) {
        this.usingCondition = usingCondition;
    }

    @Override
    public String toString() {
        return "ListEntry{" +
                "list=" + list +
                ", usingCondition=" + usingCondition +
                '}';
    }
}

class QueryContent {
    //0, 1, 2, 3, 4
    private int nameIndex;
    //[Condition{conditionIndex=1, bool=1}, Condition{conditionIndex=0, bool=1}]
    private List<Condition> conditionList;

    // get index list of parents
    public List<Integer> getParentList() {
        ArrayList<Integer> parentList = new ArrayList<>();

        for (Condition condition : conditionList) {
            parentList.add(condition.getConditionIndex());
        }

        return parentList;
    }

    public QueryContent() {
        this.conditionList = new ArrayList<>();
    }

    public QueryContent(int nameIndex, List<Condition> conditionList) {
        this.nameIndex = nameIndex;
        this.conditionList = conditionList;
    }

    public int getConditionBoolNum(int index) {
        for (Condition condition : conditionList) {
            if (condition.getConditionIndex() == index) {
                return condition.getBool();
            }
        }

        return -1;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public List<Condition> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<Condition> conditionList) {
        this.conditionList = conditionList;
    }

    @Override
    public String toString() {
        return "ConditionProbability{" +
                "nameIndex=" + nameIndex +
                ", conditionList=" + conditionList +
                '}';
    }
}

class Condition {
    private int conditionIndex;
    private int bool;

    public Condition() {
    }

    public Condition(int conditionIndex, int bool) {
        this.conditionIndex = conditionIndex;
        this.bool = bool;
    }

    public int getConditionIndex() {
        return conditionIndex;
    }

    public void setConditionIndex(int conditionIndex) {
        this.conditionIndex = conditionIndex;
    }

    public int getBool() {
        return bool;
    }

    public void setBool(int bool) {
        this.bool = bool;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "conditionIndex=" + conditionIndex +
                ", bool=" + bool +
                '}';
    }
}

class Node {
    private int nameIndex;
    private List<Integer> parentList;
    private BigDecimal[][] probabilityArr;

    //P(Burglar | John=true, Mary=false) --> 3(John): 1, 4(Mary): 2
    public int getOrdinalFromParentList(Integer index) {
        int ordinal = -1;

        if (parentList == null || parentList.size() == 0) {
            return -1;
        }

        if (parentList.size() == 1 && parentList.contains(index)) {
            return 1;
        }

        if (parentList.size() == 2) {
            if (parentList.get(0).equals(index)) {
                return 1;
            }

            if (parentList.get(1).equals(index)) {
                return 2;
            }
        }

        return -1;
    }

    public int getRow() {
        return this.probabilityArr.length;
    }

    /**
     * @return index 0 ele is its index, and other indices are its condition indices
     */
    public List<Integer> getMentionedIndexList() {
        List<Integer> mentionedIndexList = new ArrayList<>();
        mentionedIndexList.add(nameIndex);
        mentionedIndexList.addAll(parentList);

        return mentionedIndexList;
    }

    public Node() {
    }

    public Node(int nameIndex, List<Integer> parentList, BigDecimal[][] probabilityArr) {
        this.nameIndex = nameIndex;
        this.parentList = parentList;
        this.probabilityArr = probabilityArr;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    public List<Integer> getParentList() {
        return parentList;
    }

    public void setParentList(List<Integer> parentList) {
        this.parentList = parentList;
    }

    public BigDecimal[][] getProbabilityArr() {
        return probabilityArr;
    }

    public void setProbabilityArr(BigDecimal[][] probabilityArr) {
        this.probabilityArr = probabilityArr;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nameIndex=" + nameIndex +
                ", parentList=" + parentList +
                ", probabilityArr=" + Arrays.deepToString(probabilityArr) +
                '}';
    }
}
