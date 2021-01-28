import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


/**
 * Auxiliary class
 * 
 * !!! NOT PART OF SOLUTION !!!
 */
class DepthFirstSearch {

    // **** members ****
    private boolean[] marked;
    private Stack<Character> reversePost;

    /**
     * Constructor
     */
    public DepthFirstSearch(Map<Character, Set<Character>> g) {

        // **** initialization ****
        this.reversePost    = new Stack<Character>();
        this.marked         = new boolean[26];

        // **** visit all nodes in the graph ****
        for (char ch : g.keySet()) {
            if (!this.marked[ch - 'a'])
                dfs(g, ch);
        }
    }
    
    /**
     * Depth first search
     */
    private void dfs(   Map<Character, Set<Character>> g,
                        char ch) {

        // **** flag we visited this node ****
        this.marked[ch - 'a'] = true;

        // **** visit all adjacent nones ****
        for (char w : g.get(ch)) {

            // ???? ????
            System.out.println("dfs <<< w: " + w);

            // **** ****
            if (!marked[w - 'a'])
                dfs(g, w);
        }

        // **** ****
        reversePost.push(ch);
    }

    /**
     * 
     */
    public Iterable<Character> reversePost() {
        return this.reversePost;
    }

    /**
     * Utility to check if graph contains a cycle.
     * Recursive call.
     */
    private boolean isCyclicUtil(   char ch,
                                    Map<Character, Set<Character>> g, 
                                    boolean[] visited, 
                                    boolean[] recStack) {

        // **** ****
        if (recStack[ch - 'a'])
            return true;

        if (visited[ch - 'a'])
            return true;

        // **** ****
        visited[ch - 'a'] = true;
        recStack[ch - 'a'] = true;

        // **** ****
        for (char c : g.get(ch)) {
            if (isCyclicUtil(c, g, visited, recStack))
                return true;
        }

        // **** ****
        recStack[ch - 'a'] = false;

        // **** ****
        return false;
    }

    /**
     * Check if graph contains a cycle.
     * 
     * visited nodes.
     * recursion stack.
     */
    public boolean isCyclic(Map<Character, Set<Character>> g) {

        // **** initialization ****
        boolean[] visited = new boolean[26];
        boolean[] recStack = new boolean[26];

        // **** ****
        for (char ch : g.keySet()) {
            if (isCyclicUtil(ch, g, visited, recStack))
                return false;
        }

        // **** is NOT cyclic ****
        return false;
    }
}


/**
 * LeetCode 269. Alien Dictionary
 * https://leetcode.com/problems/alien-dictionary/
 */
public class AlienDictionary {

    
    /**
     * Topological graph sort using BFS approach.
     */
    private static String bfs(  Map<Character, Set<Character>> g,
                                int[] inDegree) {

        // **** initialization ****
        Queue<Character> q  = new LinkedList<>();
        StringBuilder sb    = new StringBuilder();
        int totalChars      = g.size();

        // ???? ????
        // System.out.println("bfs <<< totalChars: " + totalChars);

        // **** prime the queue ****
        for (char c : g.keySet()) {
            if (inDegree[c - 'a'] == 0) {
                sb.append(c);
                q.offer(c);
            }
        }

        // **** ****
        while (!q.isEmpty()) {

            // ???? ????
            // System.out.println("bfs <<<   q: " + q.toString());

            // **** retrieve and remove current character from the queue ****
            char cur = q.poll();

            // ???? ????
            // System.out.println("bfs <<< cur: " + cur);

            // **** current node might not have neighbors ****
            if ((g.get(cur) == null) || (g.get(cur).size() == 0))
                continue;

            // **** ****
            for (char neighbor : g.get(cur)) {
                inDegree[neighbor - 'a']--;
                if (inDegree[neighbor - 'a'] == 0) {
                    q.offer(neighbor);
                    sb.append(neighbor);
                }
            }  
        }

        // **** return the string ****
        return (sb.length() == totalChars) ? sb.toString() : "";
    }


    /**
     * Build graph.
     */
    private static void buildGraph( Map<Character, Set<Character>> g, 
                                    String[] words, 
                                    int[] inDegree) {

        // **** initialization ****
        for (String word : words) {
            for (char c : word.toCharArray()) {
                g.putIfAbsent(c, new HashSet<>());
            }
        }

        // **** traverse the words[] two words at a time ****
        for (int i = 1; i < words.length; i++) {

            // **** for ease of use ****
            String first    = words[i - 1];
            String second   = words[i];
            int len         = Math.min(first.length(), second.length());

            // ???? ????
            // System.out.println("buildGraph <<< len: " + len 
            //                     + " first: " + first + " second: " + second);

            // **** look for different characters in these words ****
            for (int j = 0; j < len; j++) {

                // ???? ????
                // System.out.println("buildGraph <<< j: " + j);
                // System.out.println("buildGraph <<< firstCh: " + first.charAt(j) 
                //                     + " secondCh: " + second.charAt(j));

                // **** check if characters differ ****
                if (first.charAt(j) != second.charAt(j)) {

                    // **** for ease of use ****
                    char out    = first.charAt(j);
                    char in     = second.charAt(j);

                    // ???? ????
                    // System.out.println("buildGraph <<< out: " + out + " in: " + in);

                    // **** add incomming edge ****
                    if (!g.get(out).contains(in)) {
                        g.get(out).add(in);
                        inDegree[in - 'a']++;
                    }

                    // **** ****
                    break;
                } else {

                    // **** ****
                    if (j + 1 == len && first.length() > second.length()) {
                        g.clear();
                        break;
                    }
                }
            }
        }

        // ???? ????
        // System.out.println("buildGraph <<<        g: " + g.toString());
        // System.out.println("buildGraph <<< inDegree: " + Arrays.toString(inDegree));
    }


    /**
     * Derive the order of letters in this language, and return it.
     * 
     * 1) build graph
     * 2) graph topological sort
     * 
     * Runtime: 4 ms, faster than 37.06% of Java online submissions.
     * Memory Usage: 38.4 MB, less than 63.50% of Java online submissions.
     */
    static String alienOrder(String[] words) {

        // **** sanity check ****
        if (words.length == 0)
            return "";

        // **** initialization ****
        int[] inDegree                      = new int[26];
        Map<Character, Set<Character>> g    = new HashMap<>();

        // **** 1) build graph ****
        buildGraph(g, words, inDegree);


        // ???? ????
        // System.out.println("alienOrder <<<        g: " + g.toString());
        // System.out.println("alienOrder <<< g.size(): " + g.size());

        // ???? ????
        // DepthFirstSearch dfs = new DepthFirstSearch(g);
        // Iterator<Character> it = dfs.reversePost().iterator();
        // StringBuilder sb = new StringBuilder();
        // while (it.hasNext()) {
        //     sb.append(it.next());
        // }
        // System.out.println("main <<< sb: " + sb.reverse().toString());

        // ???? ????
        // boolean cyclic = dfs.isCyclic(g);
        // System.out.println("main <<< cyclic: " + cyclic);


        // **** 2) graph topological sort ****
        return bfs(g, inDegree);
    }


    /**
     * Derive the order of letters in this language, and return it.
     */
    static String alienOrder1(String[] words) {

        // **** initialization ****
        Map<Character, List<Character>> hm  = new HashMap<>();
        Map<Character, Integer> inDegree    = new HashMap<>();
        
        for(String word: words) {
            for(char c: word.toCharArray()) {
                hm.put(c, new ArrayList<>());
                inDegree.put(c, 0);
            }
        }

        // ???? ????
        System.out.println("<<<       hm: " + hm.toString());
        System.out.println("<<< inDegree: " + inDegree.toString());

        // **** compare two consecutive words at a time ****
        for (int i = 0; i < words.length - 1; i++) {

            // **** for ease of use ****
            String s1 = words[i];
            String s2 = words[i + 1];

            // **** same characters; different lengths ****
            if (s1.length() > s2.length() && s1.startsWith(s2)) 
                return "";

            // **** use length of shorter string to compare characters ****
            int len = Math.min(s1.length(), s2.length());

            // ???? ????
            System.out.println("<<< len: " + len);


            // **** ****
            for(int j = 0; j < len; j++) {

                // **** for ease of use ****
                char c1 = s1.charAt(j);
                char c2 = s2.charAt(j);

                // **** ****
                if(c1 != c2) {
                    hm.get(c1).add(c2);
                    inDegree.put(c2, inDegree.get(c2) + 1);
                    break;
                }
            }
        }

        // **** ****
        Deque<Character> q = new ArrayDeque<>();

        // ???? ????
        System.out.println("<<< inDegree: " + inDegree.toString());

        // **** ****
        for (Character c: inDegree.keySet()) {
            if(inDegree.get(c) == 0) {
                q.add(c);
            }
        }

        // ???? ????
        System.out.println("<<<        q: " + q.toString());
        System.out.println("<<<       hm: " + hm.toString());

        // **** holds string to return ****
        StringBuilder sb = new StringBuilder();

        // **** traverse the queue ****
        while (!q.isEmpty()) {

            // ???? ????
            System.out.println("<<<        q: " + q.toString());
            System.out.println("<<<       sb: " + sb.toString());

            // **** get the nexh character from the queue ****
            char cur = q.poll();

            // **** append it to the string ****
            sb.append(cur);

            // **** ****
            if (!hm.containsKey(cur))
                continue;

            // **** ****
            for (char next: hm.get(cur)) {
                inDegree.put(next, inDegree.get(next) - 1);
                if (inDegree.get(next) == 0) {
                    q.add(next);
                }
            }
        }

        // ???? ????
        System.out.println("<<< inDegree: " + inDegree.toString());

        // **** ****
        return (sb.length() == inDegree.size()) ? sb.toString() : "";
    }


    /**
     * Test scaffolding.
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        // **** open buffered reader ****
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // **** read string[] ****
        String[] words = br.readLine().trim().split(",");

        // **** close buffered reader ****
        br.close();

        // **** remove "s from all words ****
        for (int i = 0; i < words.length; i++)
            words[i] = words[i].substring(1, words[i].length() - 1);

        // ???? ????
        System.out.println("main <<<  words: " + Arrays.toString(words));

        // **** call method and display result ****
        System.out.println("main <<< result: " + alienOrder(words));

        // **** call method and display result ****
        System.out.println("main <<< result: " + alienOrder1(words));
    }

}