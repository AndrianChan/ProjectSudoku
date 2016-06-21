package org.ace.game;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.LocalSocketAddress;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import java.util.jar.Attributes;

public class PuzzleView extends View 
{
private static final String TAG = "Sudoku";
private float width;    // width of one tile
private float height;   // height of one tile
private int selX;       // X index of selection
private int selY;       // Y index of selection
private final Rect selRect = new Rect();
private final Game game;
public PuzzleView(Context context)
{
    super(context);
    this.game = (Game) context;
          setFocusable(true);
          setFocusableInTouchMode(true);
}
protected void onSizeChanged(int w, int h, int oldw, int oldh) 
{
      width = w / 9f;
      height = (h - 100)/ 9f;
      getRect(selX, selY, selRect);

      super.onSizeChanged(w, h, oldw, oldh);
}
protected void onDraw(Canvas canvas) 
{


    // Draw the background...
      Paint background = new Paint();
      background.setColor(getResources().getColor(
            R.color.puzzle_background));
      canvas.drawRect(0, 0, getWidth(), getHeight(), background);
      // Draw the board...
      // Define colors for the grid lines
      Paint dark = new Paint();
      dark.setColor(getResources().getColor(R.color.puzzle_dark));
      Paint hilite = new Paint();
      hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
      Paint light = new Paint();
      light.setColor(getResources().getColor(R.color.puzzle_light));
      // Draw the minor grid lines
        for (int i = 0; i <= 9; i++) {
              canvas.drawLine(0, i * height, getWidth(), i * height,
                       dark);
              canvas.drawLine(0, i * height + 1, getWidth(), i * height
                       + 1, dark);
              canvas.drawLine(i * width, 0, i * width, getHeight() - 100,
                       dark);
              canvas.drawLine(i * width + 1, 0, i * width + 1,
                       getHeight() - 100, dark);

        }


        // Draw the major grid lines
        for (int i = 0; i <= 9; i++)
        {
            if (i % 3 != 0)
            continue;
              canvas.drawLine(0, i * height, getWidth(), i * height,
                       dark);
              canvas.drawLine(0, i * height + 1, getWidth(), i * height
                       + 1, dark);
              canvas.drawLine(i * width, 0, i * width, getHeight() - 100, dark);
              canvas.drawLine(i * width + 1, 0, i * width + 1,
              getHeight() - 100, dark);
        }



      // Draw the numbers...
      
      // Define color and style for numbers
      Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
      foreground.setColor(getResources().getColor(
            R.color.puzzle_foreground));
      foreground.setStyle(Style.FILL);
      foreground.setTextSize(height * 0.75f);
      foreground.setTextScaleX(width / height);
      foreground.setTextAlign(Paint.Align.CENTER);

      // Draw the number in the center of the tile
      FontMetrics fm = foreground.getFontMetrics();
      // Centering in X: use alignment (and X at midpoint)
      float x = width / 2;
      // Centering in Y: measure ascent/descent first
      float y = height / 2 - (fm.ascent + fm.descent) / 2;
      for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 9; j++) {
            canvas.drawText(this.game.getTileString(i, j), i
                  * width + x, j * height + y, foreground);
         }
      }
      
      // Draw the hints...

      // Pick a hint color based on #moves left
//      Paint hint = new Paint();
//      int c[] = { getResources().getColor(R.color.puzzle_hint_0),
//            getResources().getColor(R.color.puzzle_hint_1),
//            getResources().getColor(R.color.puzzle_hint_2), };
//      Rect r = new Rect();
//      for (int i = 0; i < 9; i++) {
//         for (int j = 0; j < 9; j++) {
//            int movesleft = 9 - game.getUsedTiles(i, j).length;
//            if (movesleft < c.length) {
//               getRect(i, j, r);
//               hint.setColor(c[movesleft]);
//               canvas.drawRect(r, hint);
//            }
//         }
//      }
//

      
      // Draw the selection...
      

      Paint selected = new Paint();
      selected.setColor(getResources().getColor(
            R.color.puzzle_selected));
      canvas.drawRect(selRect, selected);
    Drawable d = getResources().getDrawable(R.drawable.ic_action_name);
        d.setBounds(0, 986, getWidth(), getHeight());
    d.draw(canvas);

   }

   public boolean onTouchEvent(MotionEvent event) {
      if (event.getAction() != MotionEvent.ACTION_DOWN)
         return super.onTouchEvent(event);

       if ((event.getX() >= 0 && event.getX() <= getWidth()) && (event.getY() >= 986 && event.getY() <= getHeight()) ){

           DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   switch (which){
                       case DialogInterface.BUTTON_POSITIVE:
                           //Yes button clicked
                           game.puzzle = game.solvedPuzzle();
                           invalidate();
                           game.calculateUsedTiles();

                           break;
                       case DialogInterface.BUTTON_NEGATIVE:
                           break;
                   }
               }
           };
           AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
           builder.setMessage("Give UP").setPositiveButton("Yes", dialogClickListener)
                   .setNegativeButton("No", dialogClickListener).show();


       } else {
           select((int) (event.getX() / width),
                   (int) (event.getY() / height));
           game.showKeypadOrError(selX, selY);

       }
      return true;
   }
   
   

   public boolean onKeyDown(int keyCode, KeyEvent event) {

      switch (keyCode) {
      case KeyEvent.KEYCODE_DPAD_UP:
         select(selX, selY - 1);
         break;
      case KeyEvent.KEYCODE_DPAD_DOWN:
         select(selX, selY + 1);
         break;
      case KeyEvent.KEYCODE_DPAD_LEFT:
         select(selX - 1, selY);
         break;
      case KeyEvent.KEYCODE_DPAD_RIGHT:
         select(selX + 1, selY);
         break;
      
      
      case KeyEvent.KEYCODE_0:
      case KeyEvent.KEYCODE_SPACE: setSelectedTile(0); break;
      case KeyEvent.KEYCODE_1:     setSelectedTile(1); break;
      case KeyEvent.KEYCODE_2:     setSelectedTile(2); break;
      case KeyEvent.KEYCODE_3:     setSelectedTile(3); break;
      case KeyEvent.KEYCODE_4:     setSelectedTile(4); break;
      case KeyEvent.KEYCODE_5:     setSelectedTile(5); break;
      case KeyEvent.KEYCODE_6:     setSelectedTile(6); break;
      case KeyEvent.KEYCODE_7:     setSelectedTile(7); break;
      case KeyEvent.KEYCODE_8:     setSelectedTile(8); break;
      case KeyEvent.KEYCODE_9:     setSelectedTile(9); break;
      case KeyEvent.KEYCODE_ENTER:
      case KeyEvent.KEYCODE_DPAD_CENTER:
         game.showKeypadOrError(selX, selY);
         break;
         
         
      default:
         return super.onKeyDown(keyCode, event);
      }
      return true;
   }
   

   
   public void setSelectedTile(int tile) {
      if (game.setTileIfValid(selX, selY, tile)) {
         invalidate();// may change hints
      } else {
         
         // Number is not valid for this tile

         
         startAnimation(AnimationUtils.loadAnimation(game,
               R.anim.shake));
         
         
      }
   }
   

   
   private void select(int x, int y) {
      invalidate(selRect);
      selX = Math.min(Math.max(x, 0), 8);
      selY = Math.min(Math.max(y, 0), 8);
      getRect(selX, selY, selRect);
      invalidate(selRect);
   }
   

   
   private void getRect(int x, int y, Rect rect) {
      rect.set((int) (x * width), (int) (y * height), (int) (x
            * width + width), (int) (y * height + height));
   }
   
   
}
