package org.ace.game;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import java.util.Random;
import android.view.View.OnClickListener;

public class Game extends Activity {
   private static final String TAG = "Sudoku";

   public static final String KEY_DIFFICULTY =
      "org.example.sudoku.difficulty";
   public static final int DIFFICULTY_EASY = 0;

   public int puzzle[] = new int[9 * 9];

   
   
   public String easyPuzzle[] = new String[]
           {
                   "130200740025010000480060050" + "000780210500090370900030005" + "040006890053001400600000000",
                   "100000276009140000020006091" + "080009610730084000002005080" + "506003000007000050340590000",
                   "892003014000000000000068070" + "450080001008000200103700500" + "071006050509200080600007009",
                   "916240000230000000000108002" + "521000007089000510760090003" + "000080721090004605073005000",
                   "438760102200090530000002608" + "004023050300000800600000000" + "005010309010000080900600070",
                   "108369075070010000300000000" + "007002109000000050000901040" + "980520400520604003000008000",
                   "732940600010060000450080000" + "000300086283007400060000010" + "070025000800070000005400790",
                   "160300000008509340004270058" + "000905030000000005003010400" + "000600904090083006052000700",
                   "304018509100064000082000000" + "010006940096000000000701630" + "020053000057900080000000400",
                   "468030570002050810130009600" + "004195700900000004001003090" + "000004021040000908000500000"
           };
   public String solPuzzle[] = new String[]
           {
                   "136259748725418936489367152" + "364785219518692374972134685" + "241576893853921467697843521",
                   "154938276679142835823756491" + "485279613731684529962315784" + "516823947297461358348597162",
                   "892573614746921835315468972" + "457682391968135247123749568" + "271896453539214786684357129",
                   "916247358238956174457138962" + "521863497389472516764591283" + "645389721892714635173625849",
                   "438765192261894537579132648" + "184923756392576814657481923" + "845217369716349285923658471",
                   "148369275672815934395247681" + "857432169419786352263951748" + "986523417521674893734198526",
                   "738294561921536874456781923" + "147359286283617459569842317" + "674925138892173645315468792",
                   "165348279728569341934271658" + "846925137219437865573816492" + "387652914491783526652194783",
                   "364218579179564823582379164" + "713826945896435217245791638" + "428153796657942381931687452",
                   "468231579792456813135789642" + "824195736953672184671843295" + "587964321246317958319528467"
           };



   
   private PuzzleView puzzleView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);


      int diff = getIntent().getIntExtra(KEY_DIFFICULTY,
            DIFFICULTY_EASY);
      puzzle = getPuzzle();
      calculateUsedTiles();

      puzzleView = new PuzzleView(this);
      setContentView(puzzleView);
      puzzleView.requestFocus();
   }


   Random rand = new Random();
   public int rnd = rand.nextInt(5);

   public static String puz;
   public int[] getPuzzle() {
      puz = easyPuzzle[rnd];
      return fromPuzzleString(puz);
   }

   public int[] solvedPuzzle() {
      puz = solPuzzle[rnd];
      return fromPuzzleString(puz);
   }
   
//   /** Convert an array into a puzzle string */
//      static public String toPuzzleString(int[] puz) {
//         StringBuilder buf = new StringBuilder();
//         for (int element : puz) {
//            buf.append(element);
//         }
//      return buf.toString();
//   }

   /** Convert a puzzle string into an array */
   static public int[] fromPuzzleString(String string) {
      int[] puz = new int[string.length()];
      for (int i = 0; i < puz.length; i++) {
         puz[i] = string.charAt(i) - '0';
      }
      return puz;
   }
   

   
   /** Return the tile at the given coordinates */
   private int getTile(int x, int y) {
      return puzzle[y * 9 + x];
   }

   /** Change the tile at the given coordinates */
   private void setTile(int x, int y, int value) {
      puzzle[y * 9 + x] = value;
   }
   

   
   /** Return a string for the tile at the given coordinates */
   protected String getTileString(int x, int y) {
      int v = getTile(x, y);
      if (v == 0)
         return "";
      else
         return String.valueOf(v);
   }
   

   
   /** Change the tile only if it's a valid move */
   protected boolean setTileIfValid(int x, int y, int value) {
      int tiles[] = getUsedTiles(x, y);
      if (value != 0) {
         for (int tile : tiles) {
            if (tile == value)
               return false;
         }
      }
      setTile(x, y, value);
      calculateUsedTiles();
      return true;
   }
   

   
   /** Open the keypad if there are any valid moves */
   protected void showKeypadOrError(int x, int y) {
      int tiles[] = getUsedTiles(x, y);
      if (tiles.length == 9) {
         Toast toast = Toast.makeText(this,
               R.string.no_moves_label, Toast.LENGTH_SHORT);
         toast.setGravity(Gravity.CENTER, 0, 0);
         toast.show();
      } else {

         Dialog v = new Keypad(this, tiles, puzzleView);
         v.show();
      }
   }
   

   
   /** Cache of used tiles */
   private final int used[][][] = new int[9][9][];

   /** Return cached used tiles visible from the given coords */
   protected int[] getUsedTiles(int x, int y) {
      return used[x][y];
   }
   

   
   /** Compute the two dimensional array of used tiles */
   public void calculateUsedTiles() {
      for (int x = 0; x < 9; x++) {
         for (int y = 0; y < 9; y++) {
            used[x][y] = calculateUsedTiles(x, y);

         }
      }
   }
   

   
   /** Compute the used tiles visible from this position */
   private int[] calculateUsedTiles(int x, int y) {
      int c[] = new int[9];
      // horizontal
      for (int i = 0; i < 9; i++) { 
         if (i == y)
            continue;
         int t = getTile(x, i);
         if (t != 0)
            c[t - 1] = t;
      }
      // vertical
      for (int i = 0; i < 9; i++) { 
         if (i == x)
            continue;
         int t = getTile(i, y);
         if (t != 0)
            c[t - 1] = t;
      }
      // same cell block
      int startx = (x / 3) * 3; 
      int starty = (y / 3) * 3;
      for (int i = startx; i < startx + 3; i++) {
         for (int j = starty; j < starty + 3; j++) {
            if (i == x && j == y)
               continue;
            int t = getTile(i, j);
            if (t != 0)
               c[t - 1] = t;
         }
      }
      // compress
      int nused = 0; 
      for (int t : c) {
         if (t != 0)
            nused++;
      }
      int c1[] = new int[nused];
      nused = 0;
      for (int t : c) {
         if (t != 0)
            c1[nused++] = t;
      }
      return c1;
   }
   
   
}
