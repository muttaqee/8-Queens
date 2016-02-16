package edu.unc.muttaqee.dynamicgrid;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GridView grid;
    ImageButton[] tiles;
    Boolean[][] queens; // TODO: May not need this
    ArrayList<Coordinates> queenPositions;
    private int num_queens_placed;
    private static final int MAX_QUEENS = 8;
    View.OnClickListener listener;

    public static int evenColor = Color.parseColor("#FFFFFF");
    public static int oddColor = Color.parseColor("#7BAFD4");
    public static int queenColor = Color.parseColor("#990044");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tiles = new Tile[64];
        queenPositions = new ArrayList<>();

        grid = (GridView) this.findViewById(R.id.gridView);

        queens = new Boolean[8][8];
        num_queens_placed = 0;

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAction(v);
            }
        };

        showBubble("App started");

        /* TODO: Put this in its own method! */
        //String s;
        int id;
        for (int row = 0; row < queens.length; row++) {
            //s = "";
            for (int col = 0; col < queens[row].length; col++) {
                Log.v("Working on ImageButton", "(" + row + ", " + col + ")");

                queens[row][col] = false;
                id = this.getIdFromCoordinates(row, col);
                tiles[id] = new Tile(this, id);
                //s = s + queens[row][col] + ", ";
            }
            //Log.v("Row " + row, s);
            grid.setAdapter(new GridAdapter());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickAction(View v) {
        Tile tile = (Tile) this.findViewById(v.getId());

        //showBubble((String) v.getTag()); // TERMINATES PROGRAM
        //Log.v("logName", (String) v.getTag()); // TERMINATES PROGRAM
        //String s = (String) v.getTag(); // TERMINATES PROGRAM

        if (Integer.parseInt((String) v.getTag()) == 0) {
            if (num_queens_placed < MAX_QUEENS) {
                v.setTag(this.getString(R.string.queen));
                tile.setQueen(true);
                queens[tile.c.getRow()][tile.c.getCol()] = true;
                queenPositions.add(tile.c);
                num_queens_placed++;
                checkMove(tile.c.getRow(), tile.c.getCol());
            }
        } else {
            queenPositions.remove(getQueenCoordinates(tile.c));
            v.setTag(this.getString(R.string.no_queen));
            tile.setQueen(false);
            queens[tile.c.getRow()][tile.c.getCol()] = false;
            num_queens_placed--;
            // Reset queen images
            for (Coordinates c : queenPositions) {
                if (!checkMove(c.getRow(), c.getCol())) {
                    tiles[getIdFromCoordinates(c)].setBackgroundResource(R.drawable.queen_bevel);
                }
            }
        }
        printGridValues();
        Log.v("Queens placed", "" + num_queens_placed);
    }

    private void placeQueen(int row, int col) {
        // TODO: move contents from clickAction here
        // change tile image
        // increment/decrement queen count & add coordinate pair to coordinates array
        // checkMove()
    }

    private Boolean checkMove(int row, int col) {
        Boolean badMove = false;

        for (Coordinates c : queenPositions) {
            if ((c.getRow() == row || c.getCol() == col || // Row and column
                    (Math.abs(c.getRow() - row) == Math.abs(c.getCol() - col))) && // Diagonals
                    (c.getRow() != row || c.getCol() != col)) { // Not at position (row, col)
                tiles[getIdFromCoordinates(c)].setBackgroundResource(R.drawable.queen_bevel_bad);
                badMove = true;
            }
        }
        return !badMove;
    }

    private void showBubble(String s) {
        Context context = this.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }

    /**
     * Tiles are the visual representation of each grid cell only.
     * The logical cell values are manipulated separately in the Boolean array.
     */
    class Tile extends ImageButton {
        Coordinates c;

        public Tile(Context context, int id) {
            super(context);
            c = getCoordsFromId(id);
            this.setLayoutParams(new ViewGroup.LayoutParams(120, 120)); // TODO: CHANGE THIS?
            this.setId(id);
            this.setTag(getString(R.string.no_queen));
            if ((c.getRow() + c.getCol()) % 2 == 0) {
                this.setBackgroundColor(evenColor);
            } else {
                this.setBackgroundColor(oddColor);
            }
            this.setOnClickListener(listener);
        }

        public void setQueen(boolean queen) {
            if (queen) {
                setBackgroundColor(queenColor);
                this.setBackgroundResource(R.drawable.queen_bevel);
            } else {
                setBackgroundColor(isEvenTile() ? evenColor : oddColor);
            }
        }

        /**
         * Helper: checks if tile is "even" or "odd," where the (0, 0) tile is even
         * @return
         */
        public boolean isEvenTile() {
            return (c.getRow() + c.getCol()) % 2 == 0;
        }
    }


    /* HELPER METHODS */

    /**
     * Converts a given View item id of the grid to (row, column) coordinates
     * @param id
     * @return corresponding Coordinates object
     */
    private Coordinates getCoordsFromId(int id) {
        int row = (int) Math.floor(id / 8);
        int col = id % 8;
        return new Coordinates(row, col);
    }

    /**
     * Converts (row, column) coordinates of a View item of the grid to its corresponding id
     * @param c
     * @return corresponding id
     */
    private int getIdFromCoordinates(Coordinates c) {
        return getIdFromCoordinates(c.getRow(), c.getCol());
    }

    /**
     * Converts (row, column) coordinates of a View item of the grid to its corresponding id
     * @param row
     * @param col
     * @return corresponding id
     */
    private int getIdFromCoordinates(int row, int col) {
        return (row * 8) + col;
    }

    private Coordinates getQueenCoordinates(Coordinates c) {
        for (Coordinates coords : queenPositions) {
            if (c.getRow() == coords.getRow() && c.getCol() == coords.getCol()) {
                return coords;
            }
        }
        return null;
    }

    void printGridValues() {
        String s;
        for (int row = 0; row < queens.length; row++) {
            s = "";
            for (int col = 0; col < queens[row].length - 1; col++) {
                s = s + ((queens[row][col]) ? "[Q]" : "[ ]");
            }
            s = s + ((queens[row][queens.length - 1]) ? "[Q]" : "[ ]");
            Log.v("Row " + row, s);
        }
    }


    /* INNER CLASSES */

    /**
     * Coordinate object represents (row, column) coordinates for a View item in the grid
     */
    class Coordinates {
        private int row;
        private int col;

        Coordinates (int x, int y) {
            this.row = x;
            this.col = y;
        }

        int getRow() {
            return this.row;
        }

        int getCol() {
            return this.col;
        }
    }

    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tiles.length;
        }

        @Override
        public Object getItem(int position) {
            return tiles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return tiles[position];
        }
    }
}
