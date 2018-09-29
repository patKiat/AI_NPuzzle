import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class NPuzzle {

    public static class Pair<L, R> {
        public L l;
        public R r;
        public Pair(L l, R r){
            this.l = l;
            this.r = r;
        }
    }

    public static int parity(NPuzzleState state) {
        int inversions = 0;
        ArrayList<Integer> nums = new ArrayList<>();
        int[][] board = state.getBoard();
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                nums.add(board[i][j]);
            }
        }
        Integer[] copy = new Integer[nums.size()];
        nums.toArray(copy);
        for (int i = 0; i < copy.length; i++){
            for (int j = i + 1; j < copy.length; j++){
                if (copy[i] != 0 && copy[j] != 0 && copy[i]> copy[j]){
                    inversions++;
                }
            }
        }
        return inversions % 2;
    }

    public static boolean isSolvable(NPuzzleState initState, NPuzzleState goalState) {
        return parity(initState) == parity(goalState);
    }

    public static enum Action{ 
        // Define possible actions for the N-Puzzle search problem
        // [start:1]
        UP, DOWN, LEFT, RIGHT
        // [end:1]
    }

    public static NPuzzleState successor(NPuzzleState state, Action action) {
        int[][] board = state.getBoard();
        int s = board.length;
        int r = state.getR();   //row
        int c = state.getC();   //column
        // Implement a successor function
        // Your code should return a new state if the action is value
        // otherwise return null
        // [start:2]
        int[][] newBoard = state.copyBoard();
        if(action == Action.UP){
            if(r-1 >= 0){
                newBoard[r][c] = newBoard[r-1][c];
                newBoard[r-1][c] = 0;
                return new NPuzzleState(newBoard);
            }
        }
        else if(action == Action.DOWN){
            if(r+1 < s){
                newBoard[r][c] = newBoard[r+1][c];
                newBoard[r+1][c] = 0;
                return new NPuzzleState(newBoard);
            }
        }
        else if(action == Action.RIGHT){
            if(c+1 < s){
                newBoard[r][c] = newBoard[r][c+1];
                newBoard[r][c+1] = 0;
                return new NPuzzleState(newBoard);
            }

        }
        else if(action == Action.LEFT){
            if(c-1 >= 0){
                newBoard[r][c] = newBoard[r][c-1];
                newBoard[r][c-1] = 0;
                return new NPuzzleState(newBoard);
            }
        }
        // [end:2]
        return null;  // <- action is invalid
    }

    public static ArrayList<TreeNode<NPuzzleState>> expandSuccessors(TreeNode<NPuzzleState> node) {
        ArrayList<TreeNode<NPuzzleState>> successors = new ArrayList<>();
        NPuzzleState state = node.getState(); //node = mother node
        // Define a successor function for the N-Puzzle search problem
        // Your code should add all child nodes to "successors"
        // Hint: use successor(.,.) function above
        // [start:3]
        if(successor(state, Action.UP) != null){
            TreeNode<NPuzzleState> nodeU    = new TreeNode<NPuzzleState>(successor(state, Action.UP), node, Action.UP, 1); //T state, TreeNode<T> parent, Object action, double stepCost
            successors.add(nodeU);
        }
        if(successor(state, Action.DOWN) != null){
            TreeNode<NPuzzleState> nodeD = new TreeNode<NPuzzleState>(successor(state, Action.DOWN), node, Action.DOWN, 1);
            successors.add(nodeD);
        }
        if(successor(state, Action.RIGHT) != null) {
            TreeNode<NPuzzleState> nodeR = new TreeNode<NPuzzleState>(successor(state, Action.RIGHT), node, Action.RIGHT, 1);
            successors.add(nodeR);
        }
        if(successor(state, Action.LEFT) != null){
            TreeNode<NPuzzleState> nodeL    = new TreeNode<NPuzzleState>(successor(state, Action.LEFT), node, Action.LEFT, 1);
            successors.add(nodeL);
        }
        // [end:3]
        return successors;
    }

    public static boolean isGoal(NPuzzleState state, NPuzzleState goalState) {
        boolean desiredState = true;
        // Implement a goal test function
        // Your code should change desiredState to false if the state is not a goal 
        // [start:4]
        int[][] start = state.getBoard(); //{{1, 4, 2},{3, 0, 5},{6, 7, 8}}
        int[][] goalArr = goalState.getBoard(); //{{0, 1, 2},{3, 4, 5},{6, 7, 8}};
        int s = start.length;
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (start[i][j] != goalArr[i][j]) {
                    desiredState = false;
                }
            }
        }
        // [end:4]
        return desiredState;
    }

    public static Pair<ArrayList<Action>, Integer> solve(NPuzzleState initState, NPuzzleState goalState, Queue<TreeNode<NPuzzleState>> frontier, boolean checkClosedSet, int limit) {
        HashSet<String> closed = new HashSet<>();
        ArrayList<Action> solution = new ArrayList<>();
        int numSteps = 0;
        // Implement Graph Search algorithm
        // Your algorithm should add action to 'solution' and
        // for every node you remove from the frontier add 1 to 'numSteps'
        // [start:5]
        // BFS uses Queue data structure
        TreeNode<NPuzzleState> startNode = new TreeNode<NPuzzleState>(initState);
        frontier.add(startNode);

        //while(!frontier.isEmpty())
        for(numSteps = 1; numSteps < limit; numSteps++)
        {
            TreeNode<NPuzzleState> currentBoard = frontier.remove();
//            System.out.println("Expand\n"+currentBoard.getState().toString());
            ArrayList<TreeNode<NPuzzleState>> childrenPuzzles = expandSuccessors(currentBoard);
            closed.add(currentBoard.getState().toString());
            //numSteps++;
            if(!isGoal(currentBoard.getState(), goalState) && numSteps <= limit)
            {
                for(TreeNode<NPuzzleState> pn : childrenPuzzles)
                {
                    if(checkClosedSet == true){ //checkClosedSet ->> successor is in close
                        if(!closed.contains(pn.getState().toString())){
                            frontier.add(pn);
//                        System.out.println("add\n"+pn.getState()+"\n");
//                        System.out.println(pn.getAction());
                        }
                    }

                }
            }
            else
            {
                while(currentBoard.getParent() != null){
                    solution.add((Action) currentBoard.getAction());
                    currentBoard = currentBoard.getParent();
                }
                Collections.reverse(solution); //inverse action
                break;
            }
        }
        // [end:5]
        return new Pair<ArrayList<Action>, Integer>(solution, numSteps);
    }

    public static class HeuristicComparator implements Comparator<TreeNode<NPuzzleState>> {

        private NPuzzleState goalState;
        private int heuristicNum;
        private boolean isAStar;
        private HashMap<Integer, Pair<Integer, Integer>> goalStateMap = null;

        public HeuristicComparator(NPuzzleState goalState, int heuristicNum, boolean isAStar) {
            this.goalState = goalState;
            this.heuristicNum = heuristicNum;
            this.isAStar = isAStar;
        }

        public int compare(TreeNode<NPuzzleState> n1, TreeNode<NPuzzleState> n2) {
            Double s1V = 0.0;
            Double s2V = 0.0;
            if (this.heuristicNum == 1) {
                s1V = h1(n1.getState());
                s2V = h1(n2.getState());
            } else {
                s1V = h2(n1.getState());
                s2V = h2(n2.getState());
            }
            if (this.isAStar) {  // AStar h(n) + g(n)
                s1V += n1.getPathCost();
                s2V += n2.getPathCost();
            }
            return s1V.compareTo(s2V);
        }

        public double h1(NPuzzleState s) {
            double h = 0.0;
            int[][] board = s.getBoard();
            int[][] goalBoard = goalState.getBoard();
            // Implement misplaced tiles heuristic
            // Your code should update 'h'
            // [start:6]
            for(int i = 0; i < board.length; i++) {    //scan for misplace of tiles.
                for (int j = 0; j < board.length; j++) {
                    if (board[i][j] != goalBoard[i][j]) {
                        h++;    //whenever it found a misplace, increase result by 1.
                    }
                }
            }
            // [end:6]
            s.setEstimatedCostToGoal(h);
            return h;
        }

        public double h2(NPuzzleState s) {
            double h = 0.0;
            int[][] board = s.getBoard();
            int[][] goalBoard = goalState.getBoard();
            // Implement number-of-blocks-away heuristic
            // Your code should update 'value'
            // [start:7]
            int size = board.length;
            //4loop
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    for (int k = 0; k < size; k++) {
                        for (int l = 0; l < size; l++) {
                            if (board[i][j] == goalBoard[k][l] && board[i][j] != 0)
                                h += Math.abs(i - k) + Math.abs(j - l);
                        }
                    }
                }
            }
            // [end:7]
            s.setEstimatedCostToGoal(h);
            return h;
        }

    }

    public static void testRun(NPuzzleState initState, NPuzzleState goalState, Queue<TreeNode<NPuzzleState>> frontier) {
        TreeNode<NPuzzleState> node = new TreeNode<NPuzzleState>(initState);
        //expandSuccessors(node);
        //isGoal(initState, goalState);
        if (NPuzzle.isSolvable(initState, goalState)) {
            Pair<ArrayList<Action>, Integer> solution = solve(
                initState, goalState, frontier, true, 500000);
            System.out.println("initState =");
            System.out.println(initState);
            NPuzzleState curState = initState;
            for (Action action : solution.l) {
                curState = successor(curState, action);
                System.out.print("Action: ");
                System.out.println(action.toString());
                System.out.println(curState);
            }
            System.out.print("Number of steps in the solution: ");
            System.out.println(solution.l.size());
            System.out.print("Number of nodes expanded: ");
            System.out.println(solution.r);
        } else{
            System.out.println("Not solvable!");
        }
    }

    public static void experiment(
            NPuzzleState goalState, Queue<TreeNode<NPuzzleState>> frontier1, Queue<TreeNode<NPuzzleState>> frontier2) {
        double sum_h1[] = new double[100];
        double sum_h2[] = new double[100];
        int num_h1[] = new int[100];
        int num_h2[] = new int[100];
        for (int i = 0; i < 1000; i++){
            NPuzzleState initState = new NPuzzleState(8);  // random
            if (!NPuzzle.isSolvable(initState, goalState)) {
                i--;
                continue;
            }
            // Experiment to evaluate a search setting
            // [start:8]

            Pair<ArrayList<Action>, Integer> sol_h1 = solve(initState, goalState, frontier1, true, 500000);
            Pair<ArrayList<Action>, Integer> sol_h2 = solve(initState, goalState, frontier2, true, 500000);

            int size1 = sol_h1.l.size();
            int size2 = sol_h2.l.size();

            sum_h1[size1] += sol_h1.r;
            num_h1[size1]++;

            sum_h2[size2] += sol_h2.r;
            num_h2[size2]++;

            // [end:8]
        }
        NumberFormat formatter = new DecimalFormat("#0.00");
        double step_h1[] = new double[100];
        double step_h2[] = new double[100];
        for(int i = 0; i < 100; i++) {
            if (num_h1[i] != 0 && num_h2[i] != 0) {
                step_h1[i] =  sum_h1[i]/num_h1[i];
                step_h2[i] =  sum_h2[i]/num_h2[i];
                System.out.println(i + ", " + formatter.format(step_h1[i]) + ", " + formatter.format(step_h2[i]) + ", " + num_h1[i] + ", " + num_h2[i]);
            }
        }
    }

    public static void main(String[] args) {
        NPuzzleState.studentID = 68;

        int[][] goalBoard = {{0, 1, 2},{3, 4, 5},{6, 7, 8}};
        NPuzzleState goalState = new NPuzzleState(goalBoard);

        /*
         *  Select an implementation of a frontier from the code below
         */
        
        // Stack
//         Queue<TreeNode<NPuzzleState>> frontier = Collections.asLifoQueue(
//             new LinkedList<TreeNode<NPuzzleState>>());

        // Queue
//        Queue<TreeNode<NPuzzleState>> frontier = new LinkedList<TreeNode<NPuzzleState>>();
        
        // Priority Queue: A* with h1  
//         Queue<TreeNode<NPuzzleState>> frontier = new PriorityQueue<>(
//             new HeuristicComparator(goalState, 1, true));
        
        // Priority Queue: A* with h2
//         Queue<TreeNode<NPuzzleState>> frontier = new PriorityQueue<>(
//             new HeuristicComparator(goalState, 2, true));

        // h1 for experiment
        Queue<TreeNode<NPuzzleState>> frontier3 = new PriorityQueue<>(
             new HeuristicComparator(goalState, 1, true));

        // h2 for experiment
        Queue<TreeNode<NPuzzleState>> frontier4 = new PriorityQueue<>(
                new HeuristicComparator(goalState, 2, true));
        

//        int[][] easy = {{1, 4, 2},{3, 0, 5},{6, 7, 8}};
//        NPuzzleState initState = new NPuzzleState(easy);

        int[][] hard = {{7, 2, 4}, {5, 0, 6}, {8, 3, 1}};
        NPuzzleState initState = new NPuzzleState(hard);
//
//         testRun(initState, goalState, frontier);
         experiment(goalState, frontier3, frontier4);
    }
}

