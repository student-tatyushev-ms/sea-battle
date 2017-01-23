package com.sea_battle;

import android.support.annotation.IntRange;

import static com.sea_battle.BattleField.CELL_COUNT;
import static com.sea_battle.BattleField.CROSS;
import static com.sea_battle.BattleField.CURRENT_SHIP;
import static com.sea_battle.BattleField.EMPTY;
import static com.sea_battle.BattleField.SHIP;

public class ShipPlacement {

    private int[][] data;
    private int[][] currentShip;

    private int lengthCurrentShip;
    private boolean isVerticalCurrentShip;

    public ShipPlacement() {
        data = new int[CELL_COUNT][CELL_COUNT];
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                data[i][j] = EMPTY;
            }
        }

        currentShip = new int[CELL_COUNT][CELL_COUNT];
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                currentShip[i][j] = EMPTY;
            }
        }
    }

    public int[][] getData() {
        return data;
    }

    public int[][] getCurrentShip() {
        return currentShip;
    }

    public void reset() {
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                data[i][j] = EMPTY;
                currentShip[i][j] = EMPTY;
            }
        }
    }

    public void up() {
        for (int i = 0; i < CELL_COUNT; i++) {
            if (currentShip[i][0] != EMPTY) {
                return;
            }
        }
        for (int j = 1; j < CELL_COUNT; j++) {
            for (int i = 0; i < CELL_COUNT; i++) {
                currentShip[i][j - 1] = currentShip[i][j];
            }
        }
        for (int i = 0; i < CELL_COUNT; i++) {
            currentShip[i][CELL_COUNT - 1] = EMPTY;
        }
        check();
    }

    public void down() {
        for (int i = 0; i < CELL_COUNT; i++) {
            if (currentShip[i][CELL_COUNT - 1] != EMPTY) {
                return;
            }
        }
        for (int j = CELL_COUNT - 2; j >= 0; j--) {
            for (int i = 0; i < CELL_COUNT; i++) {
                currentShip[i][j + 1] = currentShip[i][j];
            }
        }
        for (int i = 0; i < CELL_COUNT; i++) {
            currentShip[i][0] = EMPTY;
        }
        check();
    }

    public void right() {
        for (int j = 0; j < CELL_COUNT; j++) {
            if (currentShip[CELL_COUNT - 1][j] != EMPTY) {
                return;
            }
        }
        for (int i = CELL_COUNT - 2; i >= 0; i--) {
            for (int j = 0; j < CELL_COUNT; j++) {
                currentShip[i + 1][j] = currentShip[i][j];
            }
        }
        for (int j = 0; j < CELL_COUNT; j++) {
            currentShip[0][j] = EMPTY;
        }
        check();
    }

    public void left() {
        for (int j = 0; j < CELL_COUNT; j++) {
            if (currentShip[0][j] != EMPTY) {
                return;
            }
        }
        for (int i = 1; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                currentShip[i - 1][j] = currentShip[i][j];
            }
        }
        for (int j = 0; j < CELL_COUNT; j++) {
            currentShip[CELL_COUNT - 1][j] = EMPTY;
        }
        check();
    }

    private void check() {
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                if (currentShip[i][j] != EMPTY) {
                    if (isNeighbours(i, j)) {
                        currentShip[i][j] = CROSS;
                    } else {
                        currentShip[i][j] = CURRENT_SHIP;
                    }
                }
            }
        }
    }

    private boolean isNeighbours(int x, int y) {
        int startX = x - 1 == -1 ? 0 : x - 1;
        int endX = x + 1 == CELL_COUNT ? CELL_COUNT - 1 : x + 1;
        int startY = y - 1 == -1 ? 0 : y - 1;
        int endY = y + 1 == CELL_COUNT ? CELL_COUNT - 1 : y + 1;
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                if (data[i][j] != EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    public void turn() {
        if (!isEmpty()) {
            for (int i = 0; i < CELL_COUNT; i++) {
                for (int j = 0; j < CELL_COUNT; j++) {
                    currentShip[i][j] = EMPTY;
                }
            }
            isVerticalCurrentShip = !isVerticalCurrentShip;
            addShip(lengthCurrentShip, isVerticalCurrentShip);
        }
    }

    private boolean isEmpty() {
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                if (currentShip[i][j] != EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addShip(@IntRange(from = 1, to = 4) int length, boolean isVertical) {
        lengthCurrentShip = length;
        isVerticalCurrentShip = isVertical;
        if (isVertical) {
            for (int j = 0; j < length; j++) {
                currentShip[4][4 + j] = CURRENT_SHIP;
            }
        } else {
            for (int i = 0; i < length; i++) {
                currentShip[4 + i][4] = CURRENT_SHIP;
            }
        }
        check();
    }

    public boolean done() {
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                if (currentShip[i][j] == CROSS) {
                    return false;
                }
            }
        }
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                if (currentShip[i][j] == CURRENT_SHIP) {
                    data[i][j] = SHIP;
                    currentShip[i][j] = EMPTY;
                }
            }
        }
        return true;
    }

}
