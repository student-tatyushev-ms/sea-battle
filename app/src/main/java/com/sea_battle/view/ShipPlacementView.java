package com.sea_battle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.sea_battle.ShipPlacement;

import static com.sea_battle.BattleField.ATTACKED;
import static com.sea_battle.BattleField.ATTACKED_SHIP;
import static com.sea_battle.BattleField.CELL_COUNT;
import static com.sea_battle.BattleField.CROSS;
import static com.sea_battle.BattleField.CURRENT_SHIP;
import static com.sea_battle.BattleField.EMPTY;
import static com.sea_battle.BattleField.SHIP;

public class ShipPlacementView extends View {

    private static final int STROKE_WIDTH = 2;
    private static final int DEFAULT_FIELD_SIZE = 100;

    private Paint linePaint;
    private Paint cellPaint;

    private ShipPlacement shipPlacement;

    void init(Context context) {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(STROKE_WIDTH);

        cellPaint = new Paint();
        cellPaint.setAntiAlias(true);
    }

    public ShipPlacementView(Context context) {
        super(context);
        init(context);
    }

    public ShipPlacementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShipPlacementView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int length = getMeasuredWidth() - STROKE_WIDTH;
        int cellSize = length / CELL_COUNT;
        onDrawField(canvas, length, cellSize);

        if (shipPlacement != null) {
            onDrawLayer(canvas, cellSize, shipPlacement.getData());
            onDrawLayer(canvas, cellSize, shipPlacement.getCurrentShip());
        }
    }

    private void onDrawField(Canvas canvas, int length, int cellSize) {
        for (int i = 0; i < CELL_COUNT + 1; i++) {
            int offset = cellSize * i;
            canvas.drawLine(offset, 0, offset, length, linePaint);
            canvas.drawLine(0, offset, length, offset, linePaint);
        }
    }

    private void onDrawLayer(Canvas canvas, int cellSize, int[][] data) {
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                if (data[i][j] == EMPTY) {
                    cellPaint.setARGB(0, 0, 0, 0);
                } else if (data[i][j] == SHIP) {
                    cellPaint.setARGB(150, 0, 0, 100);
                } else if (data[i][j] == ATTACKED) {
                    cellPaint.setARGB(150, 0, 150, 100);
                } else if (data[i][j] == ATTACKED_SHIP) {
                    cellPaint.setARGB(150, 250, 0, 0);
                } else if (data[i][j] == CURRENT_SHIP) {
                    cellPaint.setARGB(150, 46, 153, 0);
                } else if (data[i][j] == CROSS) {
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

    public void setShipPlacement(ShipPlacement shipPlacement) {
        this.shipPlacement = shipPlacement;
    }

}
