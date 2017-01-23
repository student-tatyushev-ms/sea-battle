package com.sea_battle;

import static com.sea_battle.BattleField.ATTACKED;
import static com.sea_battle.BattleField.ATTACKED_SHIP;
import static com.sea_battle.BattleField.CELL_COUNT;
import static com.sea_battle.BattleField.EMPTY;
import static com.sea_battle.BattleField.SHIP;

public class SeaBattleField {

    private int[][] data;

    public SeaBattleField() {
        data = new int[CELL_COUNT][CELL_COUNT];
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                data[i][j] = EMPTY;
            }
        }
    }

    public int[][] getData() {
        return data;
    }

    public void hit(int i, int j) {
        data[i][j] = ATTACKED_SHIP;
    }

    public void miss(int i, int j) {
        data[i][j] = ATTACKED;
    }

    public boolean checkCell(int i, int j) {
        boolean isHit = data[i][j] == SHIP;
        data[i][j] = isHit ? ATTACKED_SHIP : ATTACKED;
        return isHit;
    }

    public void setData(int[][] a) {
        data = a;
    }

    public int hitCount() {
        int result = 0;
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++) {
                if (data[i][j] == ATTACKED_SHIP) {
                    result++;
                }
            }
        }
        return result;
    }

}
