/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

/**
 *
 * @author greg
 */
public class SudokuCreator {

    public int[] slots;//The values of the 81 possible slots, either a num or blank
    public int[][] domains;//The possible values for the 81 slots
    public int[][] constraints;//A list of the column, row, and box restraints
    public boolean isNull;//Is the sudoku unsolveable

    public SudokuCreator() {
        this.slots = createEmptySlots();
        this.domains = createEmptyDomains();
        this.constraints = createConstraints();
        this.isNull = false;
    }

    //created an array of 81 0's
    private int[] createEmptySlots() {
        int[] v = new int[81];
        for (int i = 0; i < 81; i++) {
            v[i] = 0;
        }
        return v;
    }

    //created 81 arrays of 1-9
    private int[][] createEmptyDomains() {
        int[][] d = new int[81][9];
        for (int i = 0; i < 81; i++) {
            for (int j = 0; j <= 8; j++) {
                d[i][j] = j + 1;
            }
        }
        return d;
    }

    /*
     * createConstraints --
     * ------------------------------------------------------------- Output- A
     * 27-9 int[][] that contains the 27 different 9-box constraints that exist
     * within a typical game of sudoku
     * -------------------------------------------------------------
     *
     */
    private int[][] createConstraints() {

        int[][] constraintArr = new int[27][9];

        /*
         * The row constraint was the row multiplied by 9 plus the column  
         *       ;; ROWS
      ( 0  1  2  3  4  5  6  7  8) ;; row0, index 0
      ( 9 10 11 12 13 14 15 16 17) ;; row1, index 1
      (18 19 20 21 22 23 24 25 26) ;; row2, index 2
      (27 28 29 30 31 32 33 34 35) ;; row3, index 3
      (36 37 38 39 40 41 42 43 44) ;; row4, index 4
      (45 46 47 48 49 50 51 52 53) ;; row5, index 5
      (54 55 56 57 58 59 60 61 62) ;; row6, index 6
      (63 64 65 66 67 68 69 70 71) ;; row7, index 7
      (72 73 74 75 76 77 78 79 80) ;; row8, index 8

         */
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                constraintArr[i][j] = i * 9 + j;
            }
        }

        /*
         * The column constraint was the column multiplied by 9 plus the row
         *;; COLUMNS
      ( 0  9 18 27 36 45 54 63 72) ;; col0, index 9
      ( 1 10 19 28 37 46 55 64 73) ;; col1, index 10
      ( 2 11 20 29 38 47 56 65 74) ;; col2, index 11
      ( 3 12 21 30 39 48 57 66 75) ;; col3, index 12
      ( 4 13 22 31 40 49 58 67 76) ;; col4, index 13
      ( 5 14 23 32 41 50 59 68 77) ;; col5, index 14
      ( 6 15 24 33 42 51 60 69 78) ;; col6, index 15
      ( 7 16 25 34 43 52 61 70 79) ;; col7, index 16
      ( 8 17 26 35 44 53 62 71 80) ;; col8, index 17
         */
        for (int i = 9; i < 18; i++) {
            for (int j = 0; j < 9; j++) {
                constraintArr[i][j] = (i - 9) + j * 9;
            }
        }

        int[][] finConstraints = insertBoxConstraints(constraintArr);

        return finConstraints;

    }

    
    /*
     * The box constraint had no discernible clear pattern 
     *     ;; BOXES
      ( 0  1  2  9 10 11 18 19 20) ;; box0, index 18
      ( 3  4  5 12 13 14 21 22 23) ;; box1, index 19
      ( 6  7  8 15 16 17 24 25 26) ;; box2, index 20
      (27 28 29 36 37 38 45 46 47) ;; box3, index 21
      (30 31 32 39 40 41 48 49 50) ;; box4, index 22
      (33 34 35 42 43 44 51 52 53) ;; box5, index 23
      (54 55 56 63 64 65 72 73 74) ;; box6, index 24
      (57 58 59 66 67 68 75 76 77) ;; box7, index 25
      (60 61 62 69 70 71 78 79 80) ;; box8, index 26
     */
    private int[][] insertBoxConstraints(int[][] intarr) {
        for (int i = 18; i < 27; i++) {
            for (int j = 0; j < 9; j++) {
                if (i == 18 && j == 0) {
                    intarr[i][j] = 0;
                } else if ((i % 3) == 0 && j == 0) {
                    intarr[i][j] = intarr[i - 1][j] + 21;
                } else if (j == 0) {
                    intarr[i][j] = intarr[i - 1][j] + 3;
                } else if (j % 3 != 0) {
                    intarr[i][j] = intarr[i][j - 1] + 1;
                } else {
                    intarr[i][j] = intarr[i][j - 1] + 7;
                }
            }
        }
        return intarr;
    }

    //primarily for debugging
    public void printPuzzle(SudokuCreator sudoku) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku.slots[i * 9 + j] == 0) {
                    System.out.print('_');
                } else {
                    System.out.print(sudoku.slots[i * 9 + j]);
                }
            }
            System.out.println();
        }
    }

    /*
     * copyPuzzle --
     * ------------------------------------------------------------- Inputs:
     * SUDOKU - a sudokuCreator struct Output-An identical puzzle to the input
     * one with no common pointers in slots or domains
     * ------------------------------------------------------------- This was
     * necessary if a recursive answer was to be done as we need to be able to
     * undo moves as well as do moves. Therefore, each new move would result in
     * a completely new sudoku.
     */
    public SudokuCreator copyPuzzle(SudokuCreator sudoku) {
        SudokuCreator temp = new SudokuCreator();
        temp.slots = copySlots(sudoku.slots);
        temp.domains = copyDomains(sudoku.domains);
        temp.constraints = sudoku.constraints;
        return temp;
    }

    public int[] copySlots(int[] slots) {
        int[] newSlots = new int[81];
        System.arraycopy(slots, 0, newSlots, 0, slots.length);
        return newSlots;
    }

    public int[][] copyDomains(int[][] domains) {
        int[][] newDomains = new int[81][9];
        for (int i = 0; i < domains.length; i++) {
            System.arraycopy(domains[i], 0, newDomains[i], 0, domains[i].length);
        }
        return newDomains;
    }
}
