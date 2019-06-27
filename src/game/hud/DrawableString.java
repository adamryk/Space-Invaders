package game.hud;

import game.GameObject;
import game.enumeration.DrawableStringType;
import processing.core.PShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static game.Game.*;
import static game.enumeration.DrawableStringType.FLASHING;
import static game.enumeration.DrawableStringType.PLAYER_SCORE;
import static game.play.singleton.Player.getPlayer;
import static game.play.singleton.Saucer.getSaucer;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;

public class DrawableString extends GameObject {
    private static HashMap<Character, ArrayList<int[]>> charToCharAttributes = getAllCharToCharAttributes();
    private static int charWidth = 24;

    private String string;
    private DrawableStringType type;
    private int renderedAfterFrameNo;
    private int addCharAfterFrameNo;

    public DrawableString(float x, float y, String string, DrawableStringType type, int[] colour) {
        super(x, y);
        this.string = string;
        this.type = type;
        getPApplet().fill(colour[0], colour[1], colour[2]);
        create();
        int charAccumulator = 0;
        for(char c : string.toCharArray()) {
            PShape charShape = getPApplet().createShape(GROUP);
            for(int[] charAttributes : charToCharAttributes.get(c)) {
                charShape.addChild(getPApplet().createShape(RECT,
                        charAttributes[0] + (charAccumulator * charWidth), charAttributes[1],
                        charAttributes[2], charAttributes[3]));
            }
            getShape().addChild(charShape);
            charAccumulator++;
        }
    }

    @Override
    public void create() {
        setShape(getPApplet().createShape(GROUP));
    }

    @Override
    public void update() {
        switch(type) {
            case FLASHING:
                if(getPApplet().frameCount >= renderedAfterFrameNo + 5)
                    renderedAfterFrameNo+=8;
                if(getPApplet().frameCount == 184) {
                    getGameObjectsMarkedForRemoval().add(this);
                    getGameObjectsToBeAdded().add(new DrawableString(288, 72,
                            "0000", PLAYER_SCORE, new int[]{255, 255, 255}));
                }
                break;
            case PLAYER_SCORE:
                if(!String.format("%04d", getPlayerScore()).equals(string)) {
                    getGameObjectsMarkedForRemoval().add(this);
                    getGameObjectsToBeAdded().add(new DrawableString(288, 72,
                            String.format("%04d", getPlayerScore()), PLAYER_SCORE, new int[]{255, 255, 255}));
                }
                break;
            case SAUCER_SCORE_VALUE:
                if(getPApplet().frameCount == getSaucer().getSaucerCycleOverFrameNo() - 1)
                    getGameObjectsMarkedForRemoval().add(this);
                break;
            case LIVES:
                if(getPApplet().frameCount == getPlayer().getFinishedExplodingFrameNo() &&
                        !Objects.equals(string, String.valueOf(getPlayerLives()))) {
                    replaceString(String.valueOf(getPlayerLives()));
                }
                break;
            case FADING_OUT:
                if(getPApplet().frameCount > 179)
                    getShape().removeChild(0);
                if(getPApplet().frameCount == 182)
                    getGameObjectsMarkedForRemoval().add(this);
                break;
            case GAME_OVER_FADING_IN:
                if(addCharAfterFrameNo == 0 || getPApplet().frameCount == addCharAfterFrameNo) {
                    addCharAfterFrameNo = getPApplet().frameCount + 6;
                    if(!string.equals("game over")) {
                        getPApplet().fill(255, 255, 255);
                        replaceString("game over".substring(0, string.length() + 1));
                    }
                }
                break;
        }
    }

    @Override
    public void render() {
        if(type == FLASHING) {
            if(getPApplet().frameCount > renderedAfterFrameNo) {
                super.render();
            }
        } else {
            super.render();
        }
    }

    private void replaceString(String replacementString) {
        setShape(getPApplet().createShape(GROUP));
        string = replacementString;
        int charAccumulator = 0;
        for(char c : string.toCharArray()) {
            PShape charShape = getPApplet().createShape(GROUP);
            for(int[] charAttributes : charToCharAttributes.get(c)) {
                charShape.addChild(getPApplet().createShape(RECT,
                        charAttributes[0] + (charAccumulator * charWidth), charAttributes[1],
                        charAttributes[2], charAttributes[3]));
            }
            getShape().addChild(charShape);
            charAccumulator++;
        }
    }

    private static HashMap<Character, ArrayList<int[]>> getAllCharToCharAttributes() {
        HashMap<Character, ArrayList<int[]>> charToCharAttributes = new HashMap<>();
        ArrayList<int[]> charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {9, 3, 3, 3});
        charAttributes.add(new int[] {6, 6, 3, 3});
        charAttributes.add(new int[] {12, 6, 3, 3});
        charAttributes.add(new int[] {3, 9, 3, 15});
        charAttributes.add(new int[] {6, 15, 9, 3});
        charAttributes.add(new int[] {15, 9, 3, 15});
        charToCharAttributes.put('a', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {6, 3, 9, 3});
        charAttributes.add(new int[] {3, 6, 3, 15});
        charAttributes.add(new int[] {15, 6, 3, 3});
        charAttributes.add(new int[] {6, 21, 9, 3});
        charAttributes.add(new int[] {15, 18, 3, 3});
        charToCharAttributes.put('c', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 3, 21});
        charAttributes.add(new int[] {6, 3, 12, 3});
        charAttributes.add(new int[] {6, 12, 9, 3});
        charAttributes.add(new int[] {6, 21, 12, 3});
        charToCharAttributes.put('e', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {6, 3, 12, 3});
        charAttributes.add(new int[] {3, 6, 3, 15});
        charAttributes.add(new int[] {12, 15, 6, 3});
        charAttributes.add(new int[] {15, 18, 3, 3});
        charAttributes.add(new int[] {6, 21, 12, 3});
        charToCharAttributes.put('g', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 3, 21});
        charAttributes.add(new int[] {6, 12, 9, 3});
        charAttributes.add(new int[] {15, 3, 3, 21});
        charToCharAttributes.put('h', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {6, 3, 9, 3});
        charAttributes.add(new int[] {9, 6, 3, 15});
        charAttributes.add(new int[] {6, 21, 9, 3});
        charToCharAttributes.put('i', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 3, 21});
        charAttributes.add(new int[] {6, 21, 12, 3});
        charToCharAttributes.put('l', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 3, 21});
        charAttributes.add(new int[] {15, 3, 3, 21});
        charAttributes.add(new int[] {6, 6, 3, 3});
        charAttributes.add(new int[] {12, 6, 3, 3});
        charAttributes.add(new int[] {9, 9, 3, 6});
        charToCharAttributes.put('m', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {6, 3, 9, 3});
        charAttributes.add(new int[] {3, 6, 3, 15});
        charAttributes.add(new int[] {6, 21, 9, 3});
        charAttributes.add(new int[] {15, 6, 3, 15});
        charToCharAttributes.put('o', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 3, 21});
        charAttributes.add(new int[] {6, 3, 9, 3});
        charAttributes.add(new int[] {15, 6, 3, 6});
        charAttributes.add(new int[] {6, 12, 9, 3});
        charToCharAttributes.put('p', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 3, 21});
        charAttributes.add(new int[] {6, 3, 9, 3});
        charAttributes.add(new int[] {15, 6, 3, 6});
        charAttributes.add(new int[] {6, 12, 9, 3});
        for(int i = 9; i < 18; i+=3)
            charAttributes.add(new int[] {i, i + 6, 3, 3});
        charToCharAttributes.put('r', charAttributes);

        charAttributes = new ArrayList<>();
        for(int i = 3; i < 24; i+=9)
            charAttributes.add(new int[] {6, i, 9, 3});
        charAttributes.add(new int[] {3, 6, 3, 6});
        charAttributes.add(new int[] {15, 6, 3, 3});
        charAttributes.add(new int[] {3, 18, 3, 3});
        charAttributes.add(new int[] {15, 15, 3, 6});
        charToCharAttributes.put('s', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 3, 15});
        charAttributes.add(new int[] {15, 3, 3, 15});
        charAttributes.add(new int[] {6, 18, 3, 3});
        charAttributes.add(new int[] {12, 18, 3, 3});
        charAttributes.add(new int[] {9, 21, 3, 3});
        charToCharAttributes.put('v', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 3, 6});
        charAttributes.add(new int[] {15, 3, 3, 6});
        charAttributes.add(new int[] {6, 9, 3, 3});
        charAttributes.add(new int[] {12, 9, 3, 3});
        charAttributes.add(new int[] {9, 12, 3, 12});
        charToCharAttributes.put('y', charAttributes);

        charAttributes = new ArrayList<>();
        charToCharAttributes.put(' ', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 12, 15, 3});
        charToCharAttributes.put('-', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {6, 3, 9, 3});
        charAttributes.add(new int[] {3, 6, 3, 15});
        charAttributes.add(new int[] {6, 21, 9, 3});
        charAttributes.add(new int[] {15, 6, 3, 15});
        for(int i = 6; i < 15; i+=3)
            charAttributes.add(new int[] {i, 21 - i, 3, 3});
        charToCharAttributes.put('0', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {9, 3, 3, 18});
        charAttributes.add(new int[] {6, 6, 3, 3});
        charAttributes.add(new int[] {6, 21, 9, 3});
        charToCharAttributes.put('1', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {6, 3, 9, 3});
        charAttributes.add(new int[] {3, 6, 3, 3});
        charAttributes.add(new int[] {15, 6, 3, 6});
        charAttributes.add(new int[] {9, 12, 6, 3});
        charAttributes.add(new int[] {6, 15, 3, 3});
        charAttributes.add(new int[] {3, 18, 3, 3});
        charAttributes.add(new int[] {3, 21, 15, 3});
        charToCharAttributes.put('2', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 15, 3});
        charAttributes.add(new int[] {15, 6, 3, 3});
        charAttributes.add(new int[] {12, 9, 3, 3});
        charAttributes.add(new int[] {9, 12, 6, 3});
        charAttributes.add(new int[] {15, 15, 3, 6});
        charAttributes.add(new int[] {3, 18, 3, 3});
        charAttributes.add(new int[] {6, 21, 9, 3});
        charToCharAttributes.put('3', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {12, 3, 3, 21});
        for(int i = 3; i < 12; i+=3)
            charAttributes.add(new int[] {i, 15 - i, 3, 3});
        charAttributes.add(new int[] {3, 15, 15, 3});
        charToCharAttributes.put('4', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 15, 3});
        charAttributes.add(new int[] {3, 6, 3, 3});
        charAttributes.add(new int[] {3, 9, 12, 3});
        charAttributes.add(new int[] {15, 12, 3, 9});
        charAttributes.add(new int[] {3, 18, 3, 3});
        charAttributes.add(new int[] {6, 21, 9, 3});
        charToCharAttributes.put('5', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {9, 3, 9, 3});
        charAttributes.add(new int[] {6, 6, 3, 3});
        charAttributes.add(new int[] {3, 9, 3, 12});
        charAttributes.add(new int[] {6, 12, 9, 3});
        charAttributes.add(new int[] {15, 15, 3, 6});
        charAttributes.add(new int[] {6, 21, 9, 3});
        charToCharAttributes.put('6', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {3, 3, 15, 3});
        for(int i = 6; i < 18; i+=3)
            charAttributes.add(new int[] {i, 21 - i, 3, 3});
        charAttributes.add(new int[] {6, 18, 3, 6});
        charToCharAttributes.put('7', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {6, 3, 9, 3});
        charAttributes.add(new int[] {3, 6, 3, 6});
        charAttributes.add(new int[] {15, 5, 3, 6});
        charAttributes.add(new int[] {6, 12, 9, 3});
        charAttributes.add(new int[] {3, 15, 3, 6});
        charAttributes.add(new int[] {15, 15, 3, 6});
        charAttributes.add(new int[] {6, 21, 9, 3});
        charToCharAttributes.put('8', charAttributes);

        charAttributes = new ArrayList<>();
        charAttributes.add(new int[] {6, 3, 9, 3});
        charAttributes.add(new int[] {3, 6, 3, 6});
        charAttributes.add(new int[] {15, 5, 3, 12});
        charAttributes.add(new int[] {6, 12, 9, 3});
        charAttributes.add(new int[] {12, 18, 3, 3});
        charAttributes.add(new int[] {3, 21, 9, 3});
        charToCharAttributes.put('9', charAttributes);
        return charToCharAttributes;
    }
}
