/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

/**
 *
 * @author greg
 * Many of the design choices made in this section were based off of a college
 * project to solve sudoku done in Lisp. While I no longer have the code, some
 * design ideas (the use of recursion) were reused for this project.
 */
public class SudokuSolver {

    //Can't return a sudoku in a recursive solve function so used a global var
    private SudokuCreator solvedSudoku;
    private boolean flag = false;
    //flag used to signify when sudoku has been solved

    /*
     * SudokuSolver --
     * ------------------------------------------------------------- Inputs:
     * SUDOKU - a sudokuCreator struct Output: A solved sudoku
     * ------------------------------------------------------------- This
     * function takes in a given sudoku, and acts as a wrapper for the
     * solveWrapper function before returning the completed sudoku
     *
     */
    public SudokuCreator SudokuSolver(SudokuCreator sudoku) {

        SudokuCreator newSudoku = setDomains(sudoku);
        newSudoku = solveWrapper(newSudoku);
        return newSudoku;
    }

    /*
     * setDomains --
     * ------------------------------------------------------------- Inputs:
     * SUDOKU - a sudokuCreator struct Output: A sudoku with all 81 DOMAINS
     * properly reduced based on the values of SUDOKU's int[]SLOTS
     * ------------------------------------------------------------- This
     * function takes in a given sudoku, and performs the reduceDomains()
     * function (if necessary) on all 81 slots
     *
     */
    public SudokuCreator setDomains(SudokuCreator sudoku) {
        for (int i = 0; i < sudoku.slots.length; i++) {
            int slotNumber = sudoku.slots[i];
            if (slotNumber != 0) {//if slot is filled
                sudoku = reduceDomains(sudoku, i, slotNumber);
            }
        }
        return sudoku;
    }

    /*
     * reduceDomains --
     * ------------------------------------------------------------- Inputs:
     * SUDOKU - a sudokuCreator struct SLOT - an integer representing the which
     * slot's domains to reduce VALUE - the integer value placed in SLOT Output:
     * A sudoku with the domains properly reduced based on the VALUE and the
     * slot's pre-determined CONSTRAINTS
     * -------------------------------------------------------------
     *
     */
    public SudokuCreator reduceDomains(SudokuCreator sudoku, int slot, int value) {
        /*
         * This for loop eliminates all but one domain for the filled slot.
         * Obviously, since the slot has a value, it's can only have one
         * possible value.
         */
        for (int j = 0; j < 9; j++) {
            if (sudoku.domains[slot][j] != value) {
                sudoku.domains[slot][j] = 0;
            }
        }

        /*
         * We then have to account for the slot's constraints (i.e. the spaces
         * in the slot's corresponding row, column and box and eliminate the
         * value in the slot from their domains as they cannot have the same
         * value
         */
        int[] constraintIndices = getConstraintIndices(slot);
        for (int k = 0; k < 3; k++) {
            int constraintLine = constraintIndices[k];
            for (int l = 0; l < 9; l++) {
                int constraintSlot = sudoku.constraints[constraintLine][l];
                if (constraintSlot != slot) {
                    // System.out.println(constraintSlot + "   " + value);
                    sudoku.domains[constraintSlot][value - 1] = 0;

                }
            }
        }
        return sudoku;
    }

    /*
     * getConstraintIndices --
     * ------------------------------------------------------------- 
     * Inputs:
     * INDEX - integer value representing what slot we need to find 
     * the constraints of
     * Output: a three length int[] with each value representing the row,column 
     * and box constraints defined and built into the SudokuCreator struct
     * -------------------------------------------------------------
     *
     */
    public int[] getConstraintIndices(int index) {
        int[] consIndices = new int[3];
        int row = index / 9;
        int col = index % 9;
        int boxrow = row / 3;
        int boxcol = col / 3;
        int box = (boxrow * 3) + boxcol;
        consIndices[0] = row;
        consIndices[1] = col + 9;
        consIndices[2] = box + 18;
        return consIndices;
    }
    
    
    /*
     * returnBestVar --
     * ------------------------------------------------------------- Inputs:
     * SUDOKU - a sudokuCreator struct Output: An integer representing the best
     * slot to find a value for next
     * ------------------------------------------------------------- This
     * function checks every slot, finds which one has the fewest domains, and
     * returns that occurrence. In case of a tie, we go with the first one
     * found.
     */

    public int returnBestVar(SudokuCreator sudoku) {
        int currBestVar = -1;
        int currDomain = 10;
        for (int i = 0; i < sudoku.slots.length; i++) {
            //System.out.println(sudoku.slots[i]);
            if (sudoku.slots[i] == 0) {
                int tempDomain = 0;
                for (int j = 0; j < 9; j++) {
                    if (sudoku.domains[i][j] != 0) {
                        tempDomain++;
                    }
                }

                if (tempDomain < currDomain) {
                    currBestVar = i;
                    currDomain = tempDomain;
                }
            }
        }
        return currBestVar;
    }

    /*
     * doMove -- -------------------------------------------------------------
     * Inputs: SUDOKU - a sudokuCreator struct 
     * SLOT - integer representing a slot on the game board 
     * VALUE - integer representing what value to place in the SLOT 
     * Output: A SUDOKU with the SLOT filled by VALUE
     * -------------------------------------------------------------
     */
    public SudokuCreator doMove(SudokuCreator sudoku, int slot, int value) {
        if (sudoku.slots[slot] == 0) {
            sudoku.slots[slot] = value;
        } else {
            System.out.println("error in the code");
        }

        return sudoku;
    }

    
    /*
     * solve -- -------------------------------------------------------------
     * Inputs: SUDOKU - a sudokuCreator struct 
     * Output: None, with the side-effect of placing the solved sudoku in
     * the solvedSudoku variable
     * -------------------------------------------------------------
     * Works via a depth-first search using recursion
     */
    public void solve(SudokuCreator sudoku) {
        int nextSlot = returnBestVar(sudoku);

        if (nextSlot == -1) {//base case: puzzle is finished
            solvedSudoku = sudoku;
            flag = true;
        } else {
            /*Recursive case:
             * for every possible value in that slot, create a new sudoku with
             * its slots and domains changed appropriately, then recursively
             * call the solve function with the new puzzle
             */
            for (int i = 0; i < sudoku.domains[nextSlot].length; i++) {
                if (sudoku.domains[nextSlot][i] != 0) {
                    SudokuCreator sudo = sudoku.copyPuzzle(sudoku);
                    sudo = doMove(sudo, nextSlot, i + 1);
                    //sudo.printPuzzle(sudo);
                    //System.out.println("SUDOKU");
                    // sudoku.printPuzzle(sudoku);
                    sudo = reduceDomains(sudo, nextSlot, i + 1);
                    solve(sudo);
                    if (flag == true) {
                        break;//if puzzle is complete, break the loop
                    }
                }
                // System.out.println(i);
            }
        }

        // System.out.println("here");
    }

    /*
     * solveWrapper --
     * ------------------------------------------------------------- Inputs:
     * SUDOKU - a sudokuCreator struct Output: A solved SUDOKU
     * ------------------------------------------------------------- Calls the
     * solve function and then resets the global variables used so that the
     * solve function may be re-used
     */
    public SudokuCreator solveWrapper(SudokuCreator sudoku) {

        solve(sudoku);
        SudokuCreator solution = solvedSudoku;
        solvedSudoku = new SudokuCreator();
        flag = false;
        return solution;
    }
}
