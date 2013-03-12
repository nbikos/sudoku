import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.Math;
import java.text.DecimalFormat;
public class Sudoku implements Runnable, ActionListener {

	// Where final values must be assigned
    int[][] vals = new int[9][9];
    Board board = null;
	
	/// --- DEPTH FIRST SEARCH ---//
	/**
	 * Discussion and Comments about DFS:
	 * Depth First Search is a combinitoric style of solving problems, reminiscient of a brute-force attack.
	 * Every possible value is checked until a solution is found.  The approach is attractive insofar as  it
	 * is very simple to implement.  In fact, it is more than capable of solving even difficult problems.  
	 * 
	 * Although it takes magnitudes more cycles to find a solution, it is guaranteed to eventually find one, if one
	 * exists.  The main problem is that every solution must be checked in order to declare there is no solution possible.
	 * This leads to intractable problems, as can be seen with both no-Solution test variants.  Although the
	 * algorithm will eventually find the correct solution, it can take enormous amounts of time, which is
	 * not viable.
	 * 
	 * A guess in the first cell is selected, followed by a guess in the second cell.  If the guesses conflict,
	 * the function returns, changes its initial guess, and recurses again.  Following are the first few guesses
	 * that will be made: [1,...], [1,1,...], [1,2,...], [1,2,1,...]
	 * 
	 */
	private void DFS() {
		// Zero out values prior to Running
        board.Clear();
        ops = 0;
        recursions = 0;
		
		// Recurisively call your code.  Init: Cell 0 (Top Left)
        boolean success = RecursiveDFS(0);
		
		// Print evaluation of run
		Finished(success);
    }
	
	// YOU MAY NOT CHANGE INTERFACE DEFINITION
    private boolean RecursiveDFS(int cell){	
        recursions += 1;
        // YOUR CODE GOES HERE
        //System.out.println("cell = " + cell);  ##COMMENTED LINES WERE USED IN DEBUGGING
        int curValue; 			//holds current value to insert into cell
        boolean result;			//holds the result of the next recursion
        boolean validation;		//holds the result of whether the current value set is valid
        
        for(curValue = 1; curValue < 10; ++curValue) //values 1-9 must be checked
        {
        	if(vals[cell/9][cell%9] == 0) //if there is already a value set, it must be static
        	{
        		validation = valid(cell/9, cell%9, curValue);//eg cell 9 is (cell/9)1, (cell%9)0
        		
        		if(validation == true)//if the curValue can be placed at the current cell...
        		{
        			//System.out.println("validation in cell " + cell + "for value" + curValue);
        			//System.out.println("col " + (cell/9+1) + " row " + (cell%9+1));
        			vals[cell/9][cell%9] = curValue; //first place the value on the board
        			if(cell < 80) 	//ensure the cell is not the last
        			{
        				result = RecursiveDFS(cell+1); 
        				if(result == true) //if the next cell is true, this cell is certainly true
        					return true;
        				if(result == false)//if the next cell is false, clear the current board placement
        					vals[cell/9][cell%9] = 0;        					
        			}
        			else
        				return true;
        		
        		}
        		else
        		{
        			//System.out.println("validation was false in cell" + cell + " for " + curValue);
        			vals[cell/9][cell%9] = 0;//ensure there is no leftover board placement
        		}
        	}
        	else if(cell == 80) //if the current cell is the last and holds a static
        		return true;    //a viable solution has been found, start returning up the chain
        	else		//if there is a static value(given) in the current cell, skip to the next
        	{
        		//System.out.println("holding value = " + vals[cell/9][cell%9] + " in cell " + cell);
        		result = RecursiveDFS(cell+1);
        		if(result == true)
        			return true;
        		else
        			return false;
        	}
        }
        //System.out.println("no values fit into cell " + cell);
        //System.out.println("RETURNING FALSE FROM cell = " + cell);
        return false;
        
		
    }


	/// --- AC-3 Constraint Satisfication --- ///
	/**
	 * Discussion and Comments about AC3:
	 * AC-3, Arc-Consistency method #3, makes problems possible to solve.  There is a major contrast between AC-3
	 * and basic DFS.  With basic DFS, every possible solution must be tested.  This leads to many redundant test
	 * cases.  For example, a row will be filled with [1,1...], fail, then [1,2,1,...], fail, then [1,2,2,...], etc.
	 * 
	 * In the worst case(a blank board) there are 9^81 possible solutions.
	 * With boards containing no possible solution, this leads to an intractable problem.  AC-3 seeks to eliminate this
	 * issue by first REDUCING the solution space.  AC-3 operates by first establishing all constraints into a list.
	 * Then, the givens(statics) are weighed against these constraints, and the domains of all neighboring cells are
	 * adjusted.  If any neighbor is actually adjusted, all of its neighbors must be checked again because further
	 * reduction may be possible.
	 * 
	 * With this approach, even at the outset, the solution space is significantly reduced.  The algorithm continues,
	 * however, and performs this same neighbor-domain elimination with each "guess" made.  This eliminates the above problem
	 * with DFS; the algorithm will not try to assign [1,1,...] because placing the first 1 will remove "1" from
	 * the second cells domain.
	 * 
	 * Each cell is visited, in the style of DFS, and values are recursively chosen.  If AC-3 fails, meaning there
	 * is an unnavoidable conflict, function returns, the faulty value is eliminated, and the next value is tested.
	 * Once the cell being tested runs out of possible values, the function returns.  It continues "up the chain"
	 * until there are no more possible moves, meaning there is no solution.  If the last cell is reached, and 
	 * there is a valid assignment made, the function returns a true "up the chain".  The initial recursion will
	 * then terminate, displaying the solution.
	 * 
	 * AC-3 is more complex than DFS and may require more operations, since every Revision requires all neighbors
	 * be rechecked, but the benefits definitely outweigh the costs when there are possible no-solution problems.
	 * 
	 * Unfortunately, I was unable to get my AC-3 algorithm functioning properly.  When I would choose a value
	 * for the first cell, I would then make the necessary AC-3 calls and revisions, and continue to the next cell.
	 * When all of the next cells' values failed, however, I was unable to restore them back to their original values
	 * in order to properly guess the next first cell value.
	 * 
	 * I know my diff function properly worked in generating relations, and I also was able to confirm the
	 * initial AC-3 run with the givens(statics) was correct.
	 * 
	 */
	
	// Useful but not required Data-Structures;
    HashSet<Integer>[] globalDomains = new HashSet[81];
    HashSet<Integer>[] helpDomains = new HashSet[81];
    HashSet<Integer>[] neighbors = new HashSet[81];
    //static TreeSet<Arc> globalQueue = new TreeSet<Arc>();
    //static TreeSet<Arc> helpQueue = new TreeSet<Arc>();
    Queue<Integer> globQueue = new LinkedList<Integer>();
    Queue<Integer> glob2Queue = new LinkedList<Integer>();
    Queue<Integer> helpQueue = new LinkedList<Integer>();
    Queue<Integer> help2Queue = new LinkedList<Integer>();
    private final void AC3(){
        board.Clear();
        ops = 0;
        recursions = 0;
		
        /**
		*  YOUR CODE HERE:
        *  Create Datastructures ( or populate the ones defined above )
		*  These will be the datastructures necessary for AC-3.  They encode
		*  the board.
        **/
        
        int cell;
        int num;
        
        int row;
        int column;
        
        
        boolean result;
        
        for(num = 0; num < 81; ++num)
        {	
        	neighbors[num] = new HashSet<Integer>();
        	globalDomains[num] = new HashSet<Integer>();
        } //allocate space for the neighbor and domain arrays.
        
        for(cell = 0; cell < 81; ++cell)
        {
        	row = cell/9;
        	for(column = 0; column < 9; ++column)
        	{
        		if(((row*9)+column)!=cell)
        			neighbors[cell].add((row*9)+column);
        	}//adding all the across neighbors
        	
        	column = cell%9;
        	for(row = 0; row < 9; ++row)
        	{
        		if((column+(row*9)) != cell)
        			neighbors[cell].add(column+(row*9));
        	}//adding all the down neighbors
        	
        	row = cell/9;
        	column = cell%9;
        	if(vals[row][column] == 0)
        	{
        		for(num = 1; num < 10; ++num)
        		{
        			globalDomains[cell].add(num);
        		}
        	}
        	else
        	{
        		globalDomains[cell].add(vals[row][column]);
        	}//populating the globalDomains against the board
        }
        
        for(cell = 0; cell < 81; ++cell) //the following section adds "unit" relations
        {
        	row = cell/9;
        	column = cell%9;
        	if(row < 3)
        	{
        		if(column < 3)
        		{
        			neighbors[cell].add(0);
        			neighbors[cell].add(1);
        			neighbors[cell].add(2);
        			neighbors[cell].add(9);
        			neighbors[cell].add(10);
        			neighbors[cell].add(11);
        			neighbors[cell].add(18);
        			neighbors[cell].add(19);
        			neighbors[cell].add(20);
        			//neighbors[cell].remove(cell);
        		}
        		else if(column > 5)
        		{
        			neighbors[cell].add(6);
        			neighbors[cell].add(7);
        			neighbors[cell].add(8);
        			neighbors[cell].add(15);
        			neighbors[cell].add(16);
        			neighbors[cell].add(17);
        			neighbors[cell].add(24);
        			neighbors[cell].add(25);
        			neighbors[cell].add(26);
        			//neighbors[cell].remove(cell);
        		}
        		else
        		{
        			neighbors[cell].add(3);
        			neighbors[cell].add(4);
        			neighbors[cell].add(5);
        			neighbors[cell].add(12);
        			neighbors[cell].add(13);
        			neighbors[cell].add(14);
        			neighbors[cell].add(21);
        			neighbors[cell].add(22);
        			neighbors[cell].add(23);
        			//neighbors[cell].remove(cell);
        		}
        		
        	}
        	else if(row > 5)
        	{
        		if(column < 3)
        		{
        			neighbors[cell].add(54);
        			neighbors[cell].add(55);
        			neighbors[cell].add(56);
        			neighbors[cell].add(63);
        			neighbors[cell].add(64);
        			neighbors[cell].add(65);
        			neighbors[cell].add(72);
        			neighbors[cell].add(73);
        			neighbors[cell].add(74);
        			//neighbors[cell].remove(cell);
        		}
        		else if(column > 5)
        		{
        			neighbors[cell].add(60);
        			neighbors[cell].add(61);
        			neighbors[cell].add(62);
        			neighbors[cell].add(69);
        			neighbors[cell].add(70);
        			neighbors[cell].add(71);
        			neighbors[cell].add(78);
        			neighbors[cell].add(79);
        			neighbors[cell].add(80);
        			//neighbors[cell].remove(cell);
        		}
        		else
        		{
        			neighbors[cell].add(57);
        			neighbors[cell].add(58);
        			neighbors[cell].add(59);
        			neighbors[cell].add(66);
        			neighbors[cell].add(67);
        			neighbors[cell].add(68);
        			neighbors[cell].add(75);
        			neighbors[cell].add(76);
        			neighbors[cell].add(77);
        			//neighbors[cell].remove(cell);
        		}        		
        	}
        	else
        	{
        		if(column < 3)
        		{
        			neighbors[cell].add(27);
        			neighbors[cell].add(28);
        			neighbors[cell].add(29);
        			neighbors[cell].add(36);
        			neighbors[cell].add(37);
        			neighbors[cell].add(38);
        			neighbors[cell].add(45);
        			neighbors[cell].add(46);
        			neighbors[cell].add(47);
        			//neighbors[cell].remove(cell);
        		}
        		else if(column > 5)
        		{
        			neighbors[cell].add(33);
        			neighbors[cell].add(34);
        			neighbors[cell].add(35);
        			neighbors[cell].add(42);
        			neighbors[cell].add(43);
        			neighbors[cell].add(44);
        			neighbors[cell].add(51);
        			neighbors[cell].add(52);
        			neighbors[cell].add(53);
        			//neighbors[cell].remove(cell);
        		}
        		else
        		{
        			neighbors[cell].add(30);
        			neighbors[cell].add(31);
        			neighbors[cell].add(32);
        			neighbors[cell].add(39);
        			neighbors[cell].add(40);
        			neighbors[cell].add(41);
        			neighbors[cell].add(48);
        			neighbors[cell].add(49);
        			neighbors[cell].add(50);
        			//neighbors[cell].remove(cell);
        		}        		
        	}
        }
        //globalQueue.clear();
        for(num = 0; num < 81; ++num)
        {
        	allDiff(num, neighbors[num]);
        } // generates relations from neighbors
       
        //customAC3 deals directly with globalDomains as there is no guessing involved.
        result = customAC3(globalDomains); //add detection for initial false
        if(result == false)
        	globalDomains[0].clear(); //sentinel value, incase problem is obvious conflicted.
       

        // Recurisively call your code.  Init: Cell 0 (Top Left)
        boolean success = AC3_DFS(0,globalDomains);

		// Print evaluation of run
		Finished(success);
        
    }

    private final boolean customAC3(HashSet<Integer>[] Domains) {
		// YOUR CODE HERE
    	
    	int off;
    	int off2;
		boolean result;
		int size;
		//HashSet<Integer>[] help = new HashSet[1];
		//help[0] = new HashSet<Integer>(globalDomains[0]);
    	//deep copy global arcs
    	Queue<Integer> localQ = new LinkedList<Integer>(globQueue);
    	Queue<Integer> local2Q = new LinkedList<Integer>(glob2Queue);//create local copy of relations
		for(int num = 0; num < 81; ++num)
    	//while loop of queue not empty
    	while(!localQ.isEmpty())//begin checking each relation for revisions
    	{
    		 
    		//pop arcs, call revise
    		off = localQ.remove();
    		off2 = local2Q.remove();
    		result = Revise(off, off2, globalDomains);

    		if(result == true)
    		{
    			//	check if either of current domains is zero
    			if(globalDomains[off].isEmpty()) //terminate is AC3 fails
    				return false;
    			if(globalDomains[off2].isEmpty())
    				return false;
    	//		if one is empty, return false
    	//		if neither is empty, continue
    			helpallDiff(off, neighbors[off]);//helpallDiff finds which neighbors to add
    			helpallDiff(off2, neighbors[off2]);
    			//	call helperallDiff
    			//pop from helper, push to deep copy
    			size = helpQueue.size();
    			for(num = 0; num < size; ++num)
    			{
    				if(!(localQ.contains(helpQueue.peek()) && local2Q.contains(help2Queue.peek())) &&
    						!(localQ.contains(help2Queue.peek()) && local2Q.contains(helpQueue.peek())))
    				{//only unique pairs are added
    					localQ.add(helpQueue.remove());
    					local2Q.add(help2Queue.remove());
    				}
    				else
    				{
    					helpQueue.remove();//redundant pairs are discarded
    					help2Queue.remove();
    				}
    			}
    			helpQueue.clear();
    			help2Queue.clear();
    		}
    		
    	//if revise returns false, restart loop
    	}		
    	
    	return true;
    }
    
	// This is the actual AC-3 Algorithm ( You may change this function )
    private final boolean AC3(HashSet<Integer>[] Domains) {
		// YOUR CODE HERE
		//Arc offstack;
    	int size;
    	int off;
    	int off2;   	
    	boolean result;
    	//System.out.println("          inside AC3 "+Domains[0]);
    	//deep copy global arcs
    	Queue<Integer> localQ = new LinkedList<Integer>(globQueue);
    	Queue<Integer> local2Q = new LinkedList<Integer>(glob2Queue);
    	
    	
    	//while loop of queue not empty
    	//while(!localArcs.isEmpty())
    	while(!localQ.isEmpty())
    	{
    		//pop arcs, call revise
    		off = localQ.remove();
    		off2 = local2Q.remove();
    		if(Domains[off].isEmpty())
				return false;
			if(Domains[off2].isEmpty())
				return false;
    		result = Revise(off, off2, Domains);//check for revisions
    		
    		if(result == true)
    		{
    			//	check if either of current domains is zero
    			if(Domains[off].isEmpty())
    				return false;
    			if(Domains[off2].isEmpty())
    				return false;
    	//		if one is empty, return false
    	//		if neither is empty, continue
    			helpallDiff(off, neighbors[off]);
    			helpallDiff(off2, neighbors[off2]);
    			//	call helperallDiff
    			//pop from helper, push to deep copy
    			size = helpQueue.size();
    			//System.out.println("         TRUE revise, updating q "+Domains[0]);
    			for(int num = 0; num < size; ++num)
    			{
    				if(!(localQ.contains(helpQueue.peek()) && local2Q.contains(help2Queue.peek())) &&
    						!(localQ.contains(help2Queue.peek()) && local2Q.contains(helpQueue.peek())))
    				{
    					localQ.add(helpQueue.remove());
    					local2Q.add(help2Queue.remove());
    				}
    				else
    				{
    					helpQueue.remove();
    					help2Queue.remove();
    				}
    			}
    			helpQueue.clear();
    			help2Queue.clear();
    		}
    		
    	//if revise returns false, restart loop
    	}
    	//System.out.println("         return from AC3 "+Domains[0]);
    	
    	return true;
    }
	
	// This is the Depth First Search.  ( YOU MAY NOT CHANGE THIS INTERFACE )
    private final boolean AC3_DFS(int cell, HashSet<Integer>[] Domains) {
        recursions += 1;
        // YOUR CODE HERE
        HashSet<Integer>[] copy = new HashSet[81];
        for(int num = 0; num < 81; ++num)
        {
        	copy[num] = new HashSet<Integer>(Domains[num]);//deep copy of Domains
        }
        //System.out.println("size of cell "+ copy[cell].size());
        if(copy[cell].isEmpty())
        	return false;
        //System.out.println("size of cell "+ copy[cell].size());
        int dom;
        boolean result;
        if(globalDomains[0].size() == 0)
        	return false;
        
        
        //make deep copy of Domains
  
        //start loop
        while(!copy[cell].isEmpty())
        {
        	Iterator<Integer> itr = copy[cell].iterator();
        	itr = copy[cell].iterator();
        	dom = ((Number)copy[cell].toArray()[0]).intValue(); //load "guess" value
        	//select domain value for current cell
        	//if(copy[cell].size()==0)
        		//System.out.println("empty");
        	if(itr.hasNext())
        	{
        		//System.out.println("working");
        		//System.out.println("working "+copy[cell]);
        		copy[cell].clear();
        		//System.out.println("   working clear "+copy[cell]);
            	copy[cell].add(dom);//place guess into current cell
            	//System.out.println("     working to add "+dom + " --- "+copy[cell]);
            	vals[cell/9][cell%9] = dom;
            	//System.out.println("   about to call AC3 "+copy[cell]);
            	//System.out.println("----NEW CELL "+cell+" DOMAIN IS "+copy[1]);
            	
            	result = AC3(copy);//check guess
            	//System.out.println("         returned AC3 "+copy[cell]);
            	//for(int num = 0; num < 81; ++num)
            	//{
            		//System.out.println("cell " +num+" ---"+copy[num]);
            	//}
            	if(result == false)
            	{
            		//System.out.println("FAILURE OF AC3 REMOVING VALUES");
            		//System.out.println("OLD CELL "+cell+" DOMAIN IS "+copy[cell]+" removing "+dom);
            		copy[cell].remove(dom);
            		//System.out.println("----NEW CELL "+cell+" DOMAIN IS "+copy[cell]);
            		vals[cell/9][cell%9] = 0;
            		if(copy[cell].isEmpty())
            		{	
            			for(int num = cell+1; num < 81; ++num)
                        {
                        	copy[num] = new HashSet<Integer>(globalDomains[num]);
                        }
            			//System.out.println("**********EMPTY RETURNING*********");
            			//copy[cell] = new HashSet<Integer>(globalDomains[cell]);
            			//System.out.println("----NEW CELL "+cell+" DOMAIN IS "+copy[cell]);
            			return false;
            		}
            		//for(num = 0; num < 81; ++num)
            		//{
            			//copy[cell] = new HashSet<Integer>(globalDomains[cell]);
            			//System.out.println("----NEW WORKING SET "+cell+" DOMAIN IS "+copy[cell]);
            		//}
            		//if ac3 failed, remove current value from copy of domain
                    //	clear domain, set val to 0, restore copy, restart loop
            	}
            	else
            	{
            		if((cell+1)!= 81)
            		{
            			result = AC3_DFS((cell+1), copy);
            			if(result == true)
            				return true;
            			else
            			{
            				globalDomains[cell].remove(dom);
                    		vals[cell/9][cell%9] = 0;
                    		//for(num = 0; num < 81; ++num)
                    		//{
                    			copy[cell] = new HashSet<Integer>(globalDomains[cell]);
                    		//}
                    		//if ac3 success, recurse and call next cell on Domains
                            //	if above returns true, return true
                            //	if above returns false, remove current value from copy of domain
                            //		clear domain, set val to 0, restore copy, restart loop
                            //if above, and last cell, return true
            			}
            		}
            		else
            			return true;
            	}
        	}
        	else if(copy[cell].size() == 1)
        	{
        		vals[cell/9][cell%9] = dom;
        		result = AC3_DFS((cell+1), globalDomains); //if there is only 1 choice, try it
        		return result;
        	}
        	else
        		return false;
    
        }
        //if domain is empty, return false
        return false;
    }

	// This is the Revise function defined in the book ( arc-reduce on wiki )
	// ( You may change this function definition )
    //private final boolean Revise(Arc t, HashSet<Integer>[] Domains){
    private final boolean Revise(int off, int off2, HashSet<Integer>[] Domains){  
    	ops += 1;
    	//if(off==0||off2==0)
    		//System.out.println("before ops...off/off2 "+off+"/"+off2+"    "+Domains[0]);
		// YOUR CODE HERE
        //check domain size of each arc
        //if size of either is 1, remove that from other, return true
    	//System.out.println("         in Revise "+Domains[0]);
        int size;
        int size2;
        boolean result;
        Object[] toRem;
        int intRem = 0;
        int intRem2 = 0;
        size = Domains[off].size();
        size2 = Domains[off2].size();
        if(size==1)
        {
        	//Iterator<Integer> itr = Domains[off].iterator();
        	//intRem = ((Number)itr.next()).intValue();
        	intRem = ((Number)Domains[off].toArray()[0]).intValue();//check if there is a value to attempt to remove
        }
        if(size2==1)
        {
        	//Iterator<Integer> itr2 = Domains[off2].iterator();
        	//intRem2 = ((Number)itr2.next()).intValue();
        	intRem2 = ((Number)Domains[off2].toArray()[0]).intValue();
        }
        if(size == 1 && size2 == 1)
        {
        	if(intRem == intRem2)
        	{
        		Domains[off2].remove(intRem);
        		Domains[off].remove(intRem);//unavoidable conflict, create two empty spots and return
        		return true;
        	}
        	else
        		return false;
        }
        
        
        if(size == 1)
        {
        	//System.out.println("domain of "+off2+" is "+Domains[off2]+"going to remove "+intRem);
        	result = Domains[off2].remove(intRem); // attempt to remove
        	//System.out.println("after removal domain of "+off2+" is "+Domains[off2]);
        	if(result == true)
        	{
        		return true;
        	}
        	return false;
        }
        else if(size2 == 1)
        {
        	
        	//System.out.println("domain of2 "+off+" is "+Domains[off]+ "going to remove "+intRem2);
        	result = Domains[off].remove(intRem2);
        	//System.out.println("after removal domain of2 "+off+" is "+Domains[off]);
        	if(result == true)
        	{
        		return true;
        	}
        	return false;
        }
        //else return false
        return false;
	}

	//helpallDiff adds all the neighbors of the caller to helpQueue;
    //  helpQueue then adds itself to the list of relations.
    private final void helpallDiff(int caller, HashSet<Integer> all){
    	
		Iterator<Integer> itr = all.iterator();
		int size;
		size = all.size();
		int[] numAr;
		numAr = new int[size];
		int num;
		
		for(num = 0; num < size; ++num)
		{
			numAr[num] = itr.next();
		}
		
		for(num = 0; num < size; ++num)
		{
			if(!(helpQueue.contains(caller) && help2Queue.contains(numAr[num])) && 
					!(helpQueue.contains(numAr[num]) && help2Queue.contains(caller)) && 
					(caller != numAr[num] ))
			{
				helpQueue.add(caller);
				help2Queue.add(numAr[num]);
			}
		}
    	
    }
    //private final void allDiff(int[] all){
	private final void allDiff(int caller, HashSet<Integer> all){	
    // YOUR CODE HERE
		
		Iterator<Integer> itr = all.iterator();
		int size;
		size = all.size();
		int[] numAr;
		numAr = new int[size];
		int num;
		
		for(num = 0; num < size; ++num)
		{
			numAr[num] = itr.next();//convert list of neighbors into integer array for convenience
		}
		
		for(num = 0; num < size; ++num)
		{
			if(!(globQueue.contains(caller) && glob2Queue.contains(numAr[num])) && 
					!(globQueue.contains(numAr[num]) && glob2Queue.contains(caller)) && 
					(caller != numAr[num] ))
			{
				globQueue.add(caller);
				glob2Queue.add(numAr[num]);
			}
		}
    }

	
	/// ---------- HELPER FUNCTIONS --------- ///
	/// ----   DO NOT EDIT REST OF FILE   --- ///
	
	// Returns true if that move does not invalidate board
    public final boolean valid(int x, int y, int val){	// DO NOT EDIT
        ops +=1;
        if (vals[x][y] == val)
            return true;
        if (rowContains(x,val))
            return false;
        if (colContains(y,val))
            return false;
        if (blockContains(x,y,val))
            return false;
        return true;
    }

	// This defines a new data-type Arc which you can use for storing 
	// pairs of cells. We use these in the TreeSet Data-Structure above
	// you can opt to avoid this class or create your own helper class.
	class Arc implements Comparable<Object>{ 	// DO NOT EDIT
        int Xi, Xj;
        public Arc(int cell_i, int cell_j){
            if (cell_i == cell_j){
                try {
                    throw new Exception(cell_i+ "=" + cell_j);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            Xi = cell_i;      Xj = cell_j;
        }
		
        public int compareTo(Object o){
            return this.toString().compareTo(o.toString());
        }
		
        public String toString(){
            return "(" + Xi + "," + Xj + ")";
        }
    }
	
	// Returns true if move does not invalidate block
    public final boolean blockContains(int x, int y, int val){	// DO NOT EDIT
        int block_x = x / 3;
        int block_y = y / 3;
        for(int r = (block_x)*3; r < (block_x+1)*3; r++){
            for(int c = (block_y)*3; c < (block_y+1)*3; c++){
                if (vals[r][c] == val)
                    return true;
            }
        }
        return false;
    }

	// Returns true if move does not invalidate column
    public final boolean colContains(int c, int val){	// DO NOT EDIT
        for (int r = 0; r < 9; r++){
            if (vals[r][c] == val)
                return true;
        }
        return false;
    }

	// Returns true if move does not invalidate row
    public final boolean rowContains(int r, int val) {	// DO NOT EDIT
        for (int c = 0; c < 9; c++)
        {
            if(vals[r][c] == val)
                return true;
        }
        return false;
    }

	// Returns success if int[][] vals contains a valid solution to Sudoku
    private void CheckSolution() { 	// DO NOT EDIT
        // If played by hand, need to grab vals
        board.updateVals(vals);

        for (int v = 1; v <= 9; v++){
            // Every row is valid
            for (int r = 0; r < 9; r++)
            {
                if (!rowContains(r,v))
                {
                    board.showMessage("Invalid Row: " + (r+1));// + " val: " + v);
                    return;
                }
            }
            // Every row is valid
            for (int c = 0; c < 9; c++)
            {
                if (!colContains(c,v))
                {
                    board.showMessage("Invalid Column: " + (c+1));// + " val: " + v);
                    return;
                }
            }
            // Every block is valid
            for (int r = 0; r < 3; r++){
                for (int c = 0; c < 3; c++){
                    if(!blockContains(r, c, v))
                    {
                        board.showMessage("Invalid Block: " + (r+1) + "," + (c+1));// + " val: " + v);
                        return;
                    }
                }
            }
        }
        board.showMessage("Success!");
    }

	/// ---------- GUI + APP Code --------- ////
	/// ----   DO NOT EDIT REST OF FILE --- ////
    enum algorithm { 	// DO NOT EDIT
        DFS, AC3
    }

    enum difficulty { 	// DO NOT EDIT
        easy, medium, noSolution, hardNoSolution, random, custom
    }
	
    public static void main(String[] args) {  // DO NOT EDIT
        if (args.length == 0)
        {
            System.out.println();
            System.out.println("The code can be run with or without a GUI:");
            System.out.println();
            System.out.println("\tGUI\t$ java Sudoku <difficulty>");
            System.out.println("\tnoX\t$ java Sudoku <difficulty> <algorithm>");
            System.out.println();
            System.out.println("difficulty:\teasy, medium, noSolution, hardNoSolution");
            System.out.println("algorithm:\tDFS, AC3");
            System.out.println();
            System.exit(1);
        }
		if (args.length >= 1) {
			level = difficulty.valueOf(args[0]);
		}
		if (args.length == 2) {
            alg = algorithm.valueOf(args[1]);
            gui = false;
		}

		System.out.println("Difficulty: " + level);
		
        Sudoku app = new Sudoku();
		app.run();
    }
	
	public void run() { 	// DO NOT EDIT
        board = new Board(gui,this);
        while(!initialize());
		if (gui)
			board.initVals(vals);
		else {
			board.writeVals();
			System.out.println("Algorithm: " + alg);
			switch(alg) {
				default:
				case DFS:
					DFS();
					break;
				case AC3:
					AC3();
					break;
			}
			CheckSolution();
		}
    }

	public final boolean initialize(){ // DO NOT EDIT
        switch(level) {
            case easy:
                vals[0] = new int[] {0,0,0,1,3,0,0,0,0};
                vals[1] = new int[] {7,0,0,0,4,2,0,8,3};
                vals[2] = new int[] {8,0,0,0,0,0,0,4,0};
                vals[3] = new int[] {0,6,0,0,8,4,0,3,9};
                vals[4] = new int[] {0,0,0,0,0,0,0,0,0};
                vals[5] = new int[] {9,8,0,3,6,0,0,5,0};
                vals[6] = new int[] {0,1,0,0,0,0,0,0,4};
                vals[7] = new int[] {3,4,0,5,2,0,0,0,8};
                vals[8] = new int[] {0,0,0,0,7,3,0,0,0};
                break;
            case medium:
                vals[0] = new int[] {0,4,0,0,9,8,0,0,5};
                vals[1] = new int[] {0,0,0,4,0,0,6,0,8};
                vals[2] = new int[] {0,5,0,0,0,0,0,0,0};
                vals[3] = new int[] {7,0,1,0,0,9,0,2,0};
                vals[4] = new int[] {0,0,0,0,8,0,0,0,0};
                vals[5] = new int[] {0,9,0,6,0,0,3,0,1};
                vals[6] = new int[] {0,0,0,0,0,0,0,7,0};
                vals[7] = new int[] {6,0,2,0,0,7,0,0,0};
                vals[8] = new int[] {3,0,0,8,4,0,0,6,0};
                break;
            case noSolution:
                vals[0] = new int[] {0,0,6,0,0,0,0,0,0};
                vals[1] = new int[] {0,0,0,0,0,0,0,0,0};
                vals[2] = new int[] {0,0,0,0,0,0,3,0,2};
                vals[3] = new int[] {0,0,0,0,0,0,0,0,0};
                vals[4] = new int[] {0,0,0,0,0,8,0,0,0};
                vals[5] = new int[] {0,0,0,0,0,0,0,0,0};
                vals[6] = new int[] {3,8,0,0,0,0,9,0,0};
                vals[7] = new int[] {6,0,1,0,5,0,0,0,0};
                vals[8] = new int[] {0,9,0,3,0,0,7,2,4};
                break;
            case hardNoSolution:
                vals[0] = new int[] {2,0,0,0,0,0,0,0,0};
                vals[1] = new int[] {0,4,8,0,0,0,0,5,0};
                vals[2] = new int[] {0,0,0,0,0,0,7,9,8};
                vals[3] = new int[] {0,0,0,0,0,0,2,0,0};
                vals[4] = new int[] {0,0,0,0,0,0,0,0,5};
                vals[5] = new int[] {5,0,0,0,0,0,0,0,0};
                vals[6] = new int[] {8,6,0,0,0,0,1,0,0};
                vals[7] = new int[] {0,5,0,0,0,0,0,0,0};
                vals[8] = new int[] {0,0,0,0,0,0,4,0,0};
                break;
            case random:
            default:
                ArrayList<Integer> preset = new ArrayList<Integer>();
                while (preset.size() < numCells)
                {
                    int r = rand.nextInt(81);
                    if (!preset.contains(r))
                    {
                        preset.add(r);
                        int x = r / 9;
                        int y = r % 9;
                        if (!assignRandomValue(x, y))
                            return false;
                    }
                }
				break;
		}
        return true;
    }
	
	public void actionPerformed(ActionEvent e){		// DO NOT EDIT
        String label = ((JButton)e.getSource()).getText();
        if (label.equals("DFS"))
            DFS();
        else if (label.equals("AC-3"))
            AC3();
        else if (label.equals("Clear"))
            board.Clear();
        else if (label.equals("Check"))
            CheckSolution();
    }
	
	public final boolean assignRandomValue(int x, int y){ // DO NOT EDIT
        ArrayList<Integer> pval = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
		
        while(!pval.isEmpty()){
            int ind = rand.nextInt(pval.size());
            int i = pval.get(ind);
            if (valid(x,y,i)) {
                vals[x][y] = i;
                return true;
            } else 
                pval.remove(ind);
        }
        System.err.println("No valid moves exist.  Recreating board.");
        for (int r = 0; r < 9; r++){
            for(int c=0;c<9;c++){
                vals[r][c] = 0;
			}    }
        return false;
    }
	
	private void Finished(boolean success){  // DO NOT EDIT
		if(success) {
			board.writeVals();
			board.showMessage("Solved in " + myformat.format(ops) + " ops \t(" + myformat.format(recursions) + " recusive ops)");
		} else {
		    board.showMessage("No valid configuration found in " + myformat.format(ops) + " ops \t(" + myformat.format(recursions) + " recursive ops)");
		}
	}
	
    class Board {  // DO NOT EDIT
        GUI G = null;
        boolean gui = true;
        
        public Board(boolean X, Sudoku s) {
            gui = X;
            if (gui)
                G = new GUI(s);
        }

        public void initVals(int[][] vals){
            G.initVals(vals);
        }

        public void writeVals(){
            if (gui)
                G.writeVals();
			else {
				for (int r = 0; r < 9; r++) {
					if (r % 3 == 0)
						System.out.println(" ----------------------------");
					for (int c = 0; c < 9; c++) {
						if (c % 3 == 0)
							System.out.print (" | ");
						if (vals[r][c] != 0) {
							System.out.print(vals[r][c] + " ");
						} else {
							System.out.print("_ ");
						}
					}
					System.out.println(" | ");
				}
				System.out.println(" ----------------------------");
			}
        }

        public void Clear(){
            if(gui)
                G.clear();
        }

        public void showMessage(String msg) {
            if (gui)
                G.showMessage(msg);
            System.out.println(msg);
        }

        public void updateVals(int[][] vals){
            if (gui)
                G.updateVals(vals);
        }

    }

    class GUI {  // DO NOT EDIT
        // ---- Graphics ---- //
        int size = 40;
        JFrame mainFrame = null;
        JTextField[][] cells;
        JPanel[][] blocks;

        public void initVals(int[][] vals){
            // Mark in gray as fixed
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (vals[r][c] != 0) {
                        cells[r][c].setText(vals[r][c] + "");
                        cells[r][c].setEditable(false);
                        cells[r][c].setBackground(Color.lightGray);
                    }
                }
            }
        }

        public void showMessage(String msg){
            JOptionPane.showMessageDialog(null,
               msg,"Message",JOptionPane.INFORMATION_MESSAGE);
        }

        public void updateVals(int[][] vals) {
            for (int r = 0; r < 9; r++) {
                for (int c=0; c < 9; c++) {
                    try {
                        vals[r][c] = Integer.parseInt(cells[r][c].getText());
                    } catch (java.lang.NumberFormatException e) {
                        showMessage("Invalid Board");
                        return;
                    }
                }
            }
        }

        public void clear() {
            for (int r = 0; r < 9; r++){
                for (int c = 0; c < 9; c++){
                    if (cells[r][c].isEditable())
                    {
                        cells[r][c].setText("");
                        vals[r][c] = 0;
                    } else {
                        cells[r][c].setText("" + vals[r][c]);
                    }
                }
            }
        }

        public void writeVals(){
		    for (int r=0;r<9;r++){
			    for(int c=0; c<9; c++){
				    cells[r][c].setText(vals[r][c] + "");
			}   }
        }

        public GUI(Sudoku s){

            mainFrame = new javax.swing.JFrame();
            mainFrame.setLayout(new BorderLayout());
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            JPanel gamePanel = new javax.swing.JPanel();
            gamePanel.setBackground(Color.black);
            mainFrame.add(gamePanel, BorderLayout.NORTH);
            gamePanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            gamePanel.setLayout(new GridLayout(3,3,3,3));
            
            blocks = new JPanel[3][3];
            for (int i = 0; i < 3; i++){
                for(int j =2 ;j>=0 ;j--){
                    blocks[i][j] = new JPanel();
                    blocks[i][j].setLayout(new GridLayout(3,3));
                    gamePanel.add(blocks[i][j]);
                }
            }
            
            cells = new JTextField[9][9];
            for (int cell = 0; cell < 81; cell++){
                int i = cell / 9;
                int j = cell % 9;
                cells[i][j] = new JTextField();
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setSize(new java.awt.Dimension(size, size));
                cells[i][j].setPreferredSize(new java.awt.Dimension(size, size));
                cells[i][j].setMinimumSize(new java.awt.Dimension(size, size));
                blocks[i/3][j/3].add(cells[i][j]);
            }
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            mainFrame.add(buttonPanel, BorderLayout.SOUTH);
            JButton DFS_Button = new JButton("DFS");
            DFS_Button.addActionListener(s);
            JButton AC3_Button = new JButton("AC-3");
            AC3_Button.addActionListener(s);
            JButton Clear_Button = new JButton("Clear");
            Clear_Button.addActionListener(s);
            JButton Check_Button = new JButton("Check");
            Check_Button.addActionListener(s);
            buttonPanel.add(DFS_Button);
            buttonPanel.add(AC3_Button);
            buttonPanel.add(Clear_Button);
            buttonPanel.add(Check_Button);
            
            mainFrame.pack();
            mainFrame.setVisible(true);

        }
    }

    Random rand = new Random();
	
	// ----- Helper ---- //
    static algorithm alg = algorithm.DFS;
    static difficulty level = difficulty.easy;
    static boolean gui = true;
    static int ops;
    static int recursions;	
    static int numCells = 15;
    static DecimalFormat myformat = new DecimalFormat("###,###");
}
