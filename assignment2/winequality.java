import java.io.;
import java.util.;

public class winequality {
    public static void main(String[] args) {
        String trainPath = args[0];
        String testPath = args[1];
        int minleaf = Integer.parseInt(args[2]);

        String trainPath = DIdeaProjectsAssignment2srctrain;
        String testPath = DIdeaProjectsAssignment2srctest-sample;
        int minleaf = 30;

        int trainLineNum = 0;
        int testLineNum = 0;
        try (BufferedReader trainBufferedReader = new BufferedReader(new FileReader(trainPath));
             BufferedReader testBufferedReader = new BufferedReader(new FileReader(testPath))) {
            while (trainBufferedReader.readLine() != null) {
                trainLineNum++;
            }

            while (testBufferedReader.readLine() != null) {
                testLineNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader trainBufferedReader = new BufferedReader(new FileReader(trainPath));
             BufferedReader testBufferedReader = new BufferedReader(new FileReader(testPath))) {
            int trainLineIndex = 0;

            int testLineIndex = 0;

            Data[] trainData = new Data[trainLineNum - 1];
            X[] testX = new X[testLineNum - 1];

            String trainLine;
            ignore title line
            trainBufferedReader.readLine();

            while ((trainLine = trainBufferedReader.readLine()) != null) {
                String[] split = trainLine.trim().split(s+);
                double[] array = turnDoubleArray(split);
                System.out.println(Arrays.toString(array));
                X x = new X(array[0], array[1], array[2], array[3], array[4], array[5],
                        array[6], array[7], array[8], array[9], array[10]);
                Y y = new Y(Integer.parseInt(split[11]));
                trainData[trainLineIndex] = new Data(x, y);
                trainLineIndex++;
            }

            String testLine;
            ignore title line
            testBufferedReader.readLine();

            while ((testLine = testBufferedReader.readLine()) != null) {
                String[] split = testLine.trim().split(s+);
                double[] array = turnDoubleArray(split);
                X x = new X(array[0], array[1], array[2], array[3], array[4], array[5],
                        array[6], array[7], array[8], array[9], array[10]);
                testX[testLineIndex] = x;
                testLineIndex++;
            }

            Node root = DTL(trainData, minleaf);

            for (int i = 0; i  testLineIndex; i++) {
                int predictDTL = PredictDTL(root, testX[i]);
                System.out.println(predictDTL);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double[] turnDoubleArray(String[] split) {
        int length = split.length;
        double[] array = new double[length];

        for (int i = 0; i  length; i++) {
            array[i] = Double.parseDouble(split[i]);
        }

        return array;
    }

    
    Require data in the form of N input-output pairs {xi, yi}Ni=1, minleaf ≥ 1
    1 if (N ≤ minleaf) or (yi = yj for all i, j) or (xi = xj for all i, j) then
    2 Create new leaf node n.
    3 if there is a unique mode (most frequent value) in {yi}Ni=1 then
    4 n.label ← mode in {yi}Ni=1
    5 else
    6 n.label ← unknown
    7 end if
    8 return n
    9 end if
    10 [attr, splitval] ← ChooseSplit(data) =⇒ Algorithm 2
    11 Create new node n.
    12 n.attr ← attr
    13 n.splitval ← splitval
    14 n.lef t ← DTL(data with xi[attr] ≤ splitval, minleaf)
    15 n.right ← DT
     

    public static Node DTL(Data[] data, int minleaf) {
        if (minleaf = 1) {
            boolean flagY = true;
            Y tmpY = data[0].getY();

            for (int i = 1; i  data.length; i++) {
                if (tmpY.getQuality() != data[i].getY().getQuality()) {
                    flagY = false;

                    break;
                }
            }

            boolean flagX = true;
            X tmpX = data[0].getX();

            for (int i = 1; i  data.length; i++) {
                if (tmpX != data[i].getX()) {
                    flagX = false;

                    break;
                }
            }

            if (N ≤ minleaf) or (yi = yj for all i, j) or (xi = xj for all i, j)
            if (data.length = minleaf  flagY  flagX) {
                System.out.println(data.length = minleaf  flagY  flagX);
                Create new leaf node n
                Node node = new Node();
                node.setData(data);
                System.out.println(Node node = new Node(););

                if there is a unique mode (most frequent value) in {yi}Ni=1
                n.label ← mode in {yi}Ni=1
                int i = getUniqueMode(data);
                node.setLabel(i);
                System.out.println(node.setLabel(i););

                node.left = null;
                node.right = null;

                return node;
            }

            AttrAndSplitval attrAndSplitval = ChooseSplit(data);
            System.out.println(AttrAndSplitval.getAttr() =  + attrAndSplitval.getAttr());
            System.out.println(AttrAndSplitval.getSplitval() =  + attrAndSplitval.getSplitval());
            System.out.println(AttrAndSplitval attrAndSplitval = ChooseSplit(data););
            need to be work continue
            Node node = new Node();
            double splitval = attrAndSplitval.getSplitval();
            int attr = attrAndSplitval.getAttr();
            node.setAttr(attr);
            node.setSplitval(splitval);
            System.out.println(attr =  + attr);
            System.out.println(splitval =  + splitval);
            node.setData(data);
            int data1num = 0;
            int data2num = 0;
            System.out.println(int data2num = 0;);

            n.left ← DTL(data with xi[attr] ≤ splitval, minleaf)
            for (Data datum  data) {
                if (datum.getX().get(attr) = splitval) {
                    data1num++;
                } else {
                    data2num++;
                }
            }

            System.out.println(first for (Data datum  data) finished);

            Data[] data1 = new Data[data1num];
            int data1index = 0;
            Data[] data2 = new Data[data2num];
            int data2index = 0;

            for (Data datum  data) {
                if (datum.getX().get(attr) = splitval) {
                    data1[data1index] = datum;
                    data1index++;
                } else {
                    data2[data2index] = datum;
                    data2index++;
                }
            }

            System.out.println(data.length =  + data.length);

            System.out.println(second for (Data datum  data) finished);

            if (data1num  0) {
                System.out.println(data1num =  + data1num);
                System.out.println(call DTL(data1, minleaf););
                node.left = DTL(data1, minleaf);
                System.out.println(call DTL(data1, minleaf); finished);
            }

            if (data2num  0) {
                System.out.println(data2num =  + data2num);
                System.out.println(call DTL(data2, minleaf););
                node.right = DTL(data2, minleaf);
                System.out.println(call DTL(data2, minleaf); finished);
            }

            return node;
        }

        return null;
    }


    
    Algorithm 2 ChooseSplit(data)
    Require data in the form of N input-output pairs {xi, yi}Ni=1.
    1 bestgain ← 0
    2 for each attr in data do
    3 Sort the array x1[attr], x2[attr], ..., xN [attr].
    4 for i = 1, 2, ...N − 1 do
    5 splitval ← 0.5(xi[attr] + xi+1[attr])
    6 gain ← Information gain of (attr, splitval)  See lecture slides.
    7 if gain  begingain then
    8 bestattr ← attr and bestsplitval ← splitval
    9 end if
    10 end for
    11 end for
    12 return (bestattr, bestsplitval)
     
    public static AttrAndSplitval ChooseSplit(Data[] data) {
        double bestgain = 0;
        int bestattr = 0;
        double bestsplitval = 0;

        for (int i = 0; i  11; i++) {
            int attrIndex = i;
            Arrays.sort(data, (o1, o2) - {
                double diff = o1.getX().get(attrIndex) - o2.getX().get(attrIndex);
                if (diff  0) {
                    return 1;
                }

                if (diff == 0) {
                    return 0;
                }

                return -1;
            });
            System.out.println(Arrays.toString(data));

            double rate5num = 0;
            double rate6num = 0;
            double rate7num = 0;

            for (Data datum  data) {
                if (datum.getY().getQuality() == 5) {
                    rate5num++;
                } else if (datum.getY().getQuality() == 6) {
                    rate6num++;
                } else {
                    rate7num++;
                }
            }

            System.out.println(rate5num =  + rate5num);
            System.out.println(rate6num =  + rate6num);
            System.out.println(rate7num =  + rate7num);
            System.out.println(data.length =  + data.length);
            System.out.println(rate5num  data.length =  + rate5num  data.length);
            System.out.println(rate6num  data.length =  + rate6num  data.length);
            System.out.println(rate7num  data.length =  + rate7num  data.length);
            System.out.println(Math.log(rate5num  data.length) =  + Math.log(rate5num  data.length));
            System.out.println(Math.log(rate6num  data.length) =  + Math.log(rate6num  data.length));
            System.out.println(Math.log(rate7num  data.length) =  + Math.log(rate7num  data.length));

            double I = ((rate5num == 0)  0  (-rate5num  data.length)  (Math.log(rate5num  data.length)  Math.log(2))) +
                    ((rate6num == 0)  0  (-rate6num  data.length)  (Math.log(rate6num  data.length)  Math.log(2))) +
                    ((rate7num == 0)  0  (-rate7num  data.length)  (Math.log(rate7num  data.length)  Math.log(2)));

            System.out.println(I =  + I);

            for (int j = 0; j  data.length - 1; j++) {
                double splitval = 0.5  (data[j].getX().get(i) + data[j + 1].getX().get(i));
                System.out.println(data[j].getX().get(i) =  + data[j].getX().get(i));
                System.out.println(data[j + 1].getX().get(i) =  + data[j + 1].getX().get(i));
                System.out.println(i =  + i + ; j =  + j + ; splitval =  + splitval);

                double lowerOrEqualSplitval = 0;
                double lowerOrEqualNum5 = 0;
                double lowerOrEqualNum6 = 0;
                double lowerOrEqualNum7 = 0;
                double higherSplitval = 0;
                double higherNum5 = 0;
                double higherNum6 = 0;
                double higherNum7 = 0;

                for (int k = 0; k  data.length; k++) {
                    if (data[k].getX().get(i) = splitval) {
                        lowerOrEqualSplitval++;
                        if (data[k].getY().getQuality() == 5) {
                            lowerOrEqualNum5++;
                        } else if (data[k].getY().getQuality() == 6) {
                            lowerOrEqualNum6++;
                        } else {
                            lowerOrEqualNum7++;
                        }
                    } else {
                        higherSplitval++;
                        if (data[k].getY().getQuality() == 5) {
                            higherNum5++;
                        } else if (data[k].getY().getQuality() == 6) {
                            higherNum6++;
                        } else {
                            higherNum7++;
                        }
                    }
                }

                System.out.println(data.length =  + data.length);
                System.out.println(lowerOrEqualSplitval =  + lowerOrEqualSplitval);
                System.out.println(lowerOrEqualNum5 =  + lowerOrEqualNum5);
                System.out.println(lowerOrEqualNum6 =  + lowerOrEqualNum6);
                System.out.println(lowerOrEqualNum7 =  + lowerOrEqualNum7);
                System.out.println(higherSplitval =  + higherSplitval);
                System.out.println(higherNum5 =  + higherNum5);
                System.out.println(higherNum6 =  + higherNum6);
                System.out.println(higherNum7 =  + higherNum7);

                double remainder = 0;
                if (lowerOrEqualSplitval == 0) {
                    System.out.println(if (lowerOrEqualSplitval == 0));
                    if (higherNum5  0) {
                        remainder += -(higherNum5  higherSplitval)  (Math.log(higherNum5  higherSplitval)  Math.log(2));
                    }
                    if (higherNum6  0) {
                        remainder += -(higherNum6  higherSplitval)  (Math.log(higherNum6  higherSplitval)  Math.log(2));
                    }
                    if (higherNum7  0) {
                        remainder += -(higherNum7  higherSplitval)  (Math.log(higherNum7  higherSplitval)  Math.log(2));
                    }
                } else if (higherSplitval == 0) {
                    System.out.println(else if (higherSplitval == 0));
                    if (lowerOrEqualNum5  0) {
                        remainder += -(lowerOrEqualNum5  lowerOrEqualSplitval)  (Math.log(lowerOrEqualNum5  lowerOrEqualSplitval)  Math.log(2));
                    }
                    if (lowerOrEqualNum6  0) {
                        remainder += -(lowerOrEqualNum6  lowerOrEqualSplitval)  (Math.log(lowerOrEqualNum6  lowerOrEqualSplitval)  Math.log(2));
                    }
                    if (lowerOrEqualNum7  0) {
                        remainder += -(lowerOrEqualNum7  lowerOrEqualSplitval)  (Math.log(lowerOrEqualNum7  lowerOrEqualSplitval)  Math.log(2));
                    }
                } else {
                    System.out.println(else);
                    if (lowerOrEqualNum5  0) {
                        remainder += (lowerOrEqualSplitval  data.length)  (-(lowerOrEqualNum5  lowerOrEqualSplitval)  (Math.log(lowerOrEqualNum5  lowerOrEqualSplitval)  Math.log(2)));
                    }
                    if (lowerOrEqualNum6  0) {
                        remainder += (lowerOrEqualSplitval  data.length)  (-(lowerOrEqualNum6  lowerOrEqualSplitval)  (Math.log(lowerOrEqualNum6  lowerOrEqualSplitval)  Math.log(2)));
                    }
                    if (lowerOrEqualNum7  0) {
                        remainder += (lowerOrEqualSplitval  data.length)  (-(lowerOrEqualNum7  lowerOrEqualSplitval)  (Math.log(lowerOrEqualNum7  lowerOrEqualSplitval)  Math.log(2)));
                    }
                    if (higherNum5  0) {
                        remainder += (higherSplitval  data.length)  (-(higherNum5  higherSplitval)  (Math.log(higherNum5  higherSplitval)  Math.log(2)));
                    }
                    if (higherNum6  0) {
                        remainder += (higherSplitval  data.length)  (-(higherNum6  higherSplitval)  (Math.log(higherNum6  higherSplitval)  Math.log(2)));
                    }
                    if (higherNum7  0) {
                        remainder += (higherSplitval  data.length)  (-(higherNum7  higherSplitval)  (Math.log(higherNum7  higherSplitval)  Math.log(2)));
                    }
                }

                System.out.println(remainder =  + remainder);

                double gain = I - remainder;

                System.out.println(gain =  + gain);

                if (Double.compare(gain,bestgain) = 0) {
                    bestgain = gain;
                    bestattr = i;
                    bestsplitval = splitval;
                }

            }
        }

        return new AttrAndSplitval(bestattr, bestsplitval);
    }

    private static int getUniqueMode(Data[] data) {
        int[] yArray = new int[data.length];

        for (int i = 0; i  data.length; i++) {
            yArray[i] = data[i].getY().getQuality();
        }

        MapInteger, Integer map = new HashMap();

        int tmpCount = 0;
        for (int ele  yArray) {
            tmpCount = map.getOrDefault(ele, 0);
            map.put(ele, tmpCount + 1);
        }

        CollectionInteger valueSet = map.values();

        int maxCount = Collections.max(valueSet);
        int count = 0;
        int maxNum = 0;

        for (Map.EntryInteger, Integer integerEntry  map.entrySet()) {
            if (maxCount == integerEntry.getValue()) {
                maxNum = integerEntry.getKey();
                count++;
            }
        }

        if (count == 1) {
            return maxNum;
        }

        return Integer.MIN_VALUE;
    }

    
    1 while n is not a leaf node do
    2 if x[n.attr] ≤ n.splitval then
    3 n ← n.lef t
    4 else
    5 n ← n.right
    6 end if
    7 end while
    8 return n.label
     
    public static int PredictDTL(Node node, X x) {
        while (node.left != null  node.right != null) {
            if (x.get(node.getAttr()) = node.getSplitval()) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        return node.getLabel();
    }
}

class AttrAndSplitval {
    private int attr;
    private double splitval;

    public AttrAndSplitval() {
    }

    public AttrAndSplitval(int attr, double splitval) {
        this.attr = attr;
        this.splitval = splitval;
    }

    public int getAttr() {
        return attr;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public double getSplitval() {
        return splitval;
    }

    public void setSplitval(double splitval) {
        this.splitval = splitval;
    }
}

 Decision tree root node n, data in the form of attribute values x.
class Node {
    private Data[] data;
    private int label;
    private int attr;
    private double splitval;
    Node left;
    Node right;

    public Node() {
    }

    public Node(Data[] data, int label, int attr, double splitval) {
        this.data = data;
        this.label = label;
        this.attr = attr;
        this.splitval = splitval;
    }

    public Data[] getData() {
        return data;
    }

    public void setData(Data[] data) {
        this.data = data;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public int getAttr() {
        return attr;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public double getSplitval() {
        return splitval;
    }

    public void setSplitval(double splitval) {
        this.splitval = splitval;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }
}

class X {
    private double f_acid;
    private double v_acid;
    private double c_acid;
    private double res_sugar;
    private double chlorides;
    private double fs_dioxide;
    private double ts_dioxide;
    private double density;
    private double pH;
    private double sulphates;
    private double alcohol;

    public X() {
    }

    public X(double f_acid, double v_acid, double c_acid, double res_sugar,
             double chlorides, double fs_dioxide, double ts_dioxide,
             double density, double pH, double sulphates, double alcohol) {
        this.f_acid = f_acid;
        this.v_acid = v_acid;
        this.c_acid = c_acid;
        this.res_sugar = res_sugar;
        this.chlorides = chlorides;
        this.fs_dioxide = fs_dioxide;
        this.ts_dioxide = ts_dioxide;
        this.density = density;
        this.pH = pH;
        this.sulphates = sulphates;
        this.alcohol = alcohol;
    }

    public double getAttr(int i){
        if (i == 0){}
    }

    public double get(int i) {
        if (i == 0) {
            return this.f_acid;
        }
        if (i == 1) {
            return this.v_acid;
        }
        if (i == 2) {
            return this.c_acid;
        }
        if (i == 3) {
            return this.res_sugar;
        }
        if (i == 4) {
            return this.chlorides;
        }
        if (i == 5) {
            return this.fs_dioxide;
        }
        if (i == 6) {
            return this.ts_dioxide;
        }
        if (i == 7) {
            return this.density;
        }
        if (i == 8) {
            return this.pH;
        }
        if (i == 9) {
            return this.sulphates;
        }
        return this.alcohol;
    }

    public double getF_acid() {
        return f_acid;
    }

    public void setF_acid(double f_acid) {
        this.f_acid = f_acid;
    }

    public double getV_acid() {
        return v_acid;
    }

    public void setV_acid(double v_acid) {
        this.v_acid = v_acid;
    }

    public double getC_acid() {
        return c_acid;
    }

    public void setC_acid(double c_acid) {
        this.c_acid = c_acid;
    }

    public double getRes_sugar() {
        return res_sugar;
    }

    public void setRes_sugar(double res_sugar) {
        this.res_sugar = res_sugar;
    }

    public double getChlorides() {
        return chlorides;
    }

    public void setChlorides(double chlorides) {
        this.chlorides = chlorides;
    }

    public double getFs_dioxide() {
        return fs_dioxide;
    }

    public void setFs_dioxide(double fs_dioxide) {
        this.fs_dioxide = fs_dioxide;
    }

    public double getTs_dioxide() {
        return ts_dioxide;
    }

    public void setTs_dioxide(double ts_dioxide) {
        this.ts_dioxide = ts_dioxide;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getpH() {
        return pH;
    }

    public void setpH(double pH) {
        this.pH = pH;
    }

    public double getSulphates() {
        return sulphates;
    }

    public void setSulphates(double sulphates) {
        this.sulphates = sulphates;
    }

    public double getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(double alcohol) {
        this.alcohol = alcohol;
    }

    @Override
    public String toString() {
        return X{ +
                f_acid= + f_acid +
                , v_acid= + v_acid +
                , c_acid= + c_acid +
                , res_sugar= + res_sugar +
                , chlorides= + chlorides +
                , fs_dioxide= + fs_dioxide +
                , ts_dioxide= + ts_dioxide +
                , density= + density +
                , pH= + pH +
                , sulphates= + sulphates +
                , alcohol= + alcohol +
                '}';
    }
}

class Y {
    private int quality;

    public Y() {
    }

    public Y(int quality) {
        this.quality = quality;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public String toString() {
        return Y{ +
                quality= + quality +
                '}';
    }
}

class Data {
    private X x;
    private Y y;

    public Data() {
    }

    public Data(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return Data{ +
                x= + x +
                , y= + y +
                '}';
    }
}
