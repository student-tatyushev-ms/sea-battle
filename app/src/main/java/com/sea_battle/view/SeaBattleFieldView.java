package com.sea_battle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sea_battle.SeaBattleField;

import static com.sea_battle.BattleField.ATTACKED;
import static com.sea_battle.BattleField.ATTACKED_SHIP;
import static com.sea_battle.BattleField.CELL_COUNT;
import static com.sea_battle.BattleField.EMPTY;
import static com.sea_battle.BattleField.SHIP;

public class SeaBattleFieldView extends View {

    private static final int STROKE_WIDTH = 2;
    private static final int DEFAULT_FIELD_SIZE = 100;

    private Paint linePaint;
    private Paint cellPaint;
    private SeaBattleField seaBattleField;
    private GestureDetector mDetector;
    private OnCellTouchListener onCellTouchListener;

    void init(Context context) {
        mDetector = new GestureDetector(context, new mListener());
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(STROKE_WIDTH);

        cellPaint = new Paint();
        cellPaint.setAntiAlias(true);
    }

    public SeaBattleFieldView(Context context) {
        super(context);
        init(context);
    }

    public SeaBattleFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SeaBattleFieldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int length = getMeasuredWidth() - STROKE_WIDTH;
        int cellSize = length / CELL_COUNT;
        onDrawField(canvas, length, cellSize);
        if (seaBattleField != null) {
            onDrawGame(canvas, cellSize, seaBattleField.getData());
        }
    }

    private void onDrawField(Canvas canvas, int length, int cellSize) {
        for (int i = 0; i < CELL_COUNT + 1; i++) {
            int offset = cellSize * i;
            canvas.drawLine(offset, 0, offset, length, linePaint);
            canvas.drawLine(0, offset, length, offset, linePaint);
        }
    }

    private void onDrawGame(Canvas canvas, int cellSize, int[][] data) {
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                if (data[i][j] == EMPTY) {
                    cellPaint.setARGB(0, 0, 0, 0);
                } else if (data[i][j] == SHIP) {
                    cellPaint.setARGB(150, 0, 150, 100);
                } else if (data[i][j] == ATTACKED) {
                    cellPaint.setARGB(150, 0, 0, 100);
                } else if (data[i][j] == ATTACKED_SHIP) {
                    cellPaint.setARGB(150, 250, 0, 0);
                }
                canvas.drawRect(i * cellSize, j * cellSize, (i + 1) * cellSize, (j + 1) * cellSize, cellPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int length = Math.max(Math.min(width, height), DEFAULT_FIELD_SIZE);
        length = (length / CELL_COUNT) * CELL_COUNT + STROKE_WIDTH;
        setMeasuredDimension(length, length);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("MyLogs", "onTouchEvent");
        if (!isClickable()) {
            return false;
        }
        return mDetector.onTouchEvent(event);
    }

    public void setOnCellTouchListener(OnCellTouchListener onCellTouchListener) {
        this.onCellTouchListener = onCellTouchListener;
    }

    class mListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("MyLogs", "onDown");
            int length = getMeasuredWidth();
            int cellSize = length / CELL_COUNT;
            int i = (int) ((e.getX() - 1) / cellSize);
            int j = (int) ((e.getY() - 1) / cellSize);
            Log.d("MyLogs", "x,y (" + e.getX() + ", " + e.getY() + ")");
            Log.d("MyLogs", "x,y RAW (" + e.getRawX() + ", " + e.getRawY() + ")");
            Log.d("MyLogs", "i,j (" + i + ", " + j + ")");

            Log.d("MyLogs", "length = " + length);
            Log.d("MyLogs", "cellSize = " + cellSize);
            onCellTouchListener.onCellTouch(i, j);
            return super.onDown(e);
        }
    }

    public void setSeaBattleField(SeaBattleField seaBattleField) {
        this.seaBattleField = seaBattleField;
    }

    public interface OnCellTouchListener {
        void onCellTouch(int i, int j);
    }

}
