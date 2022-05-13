package solutions;

public class Node {
    Node left; Node right;
    int lo; int hi;
    int numFemales; int numICU;

    public Node(Node left, Node right, int lo, int hi, int numFemales, int numICU) {
        this.left = left;
        this.right = right;
        this.lo = lo;
        this.hi = hi;
        this.numFemales = numFemales;
        this.numICU = numICU;
    }

    boolean isALeaf(){
        return left == null;
    }
}
