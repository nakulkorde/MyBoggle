package com.hoiwanlouis.myboggle;

/*
    Copyright (c) 2014  Hoi Wan Louis

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/


import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BoggleDriver {
    private final String DEBUG_TAG = this.getClass().getSimpleName();

    public static String randomChar() {
        // Randomly select letter from alphabet
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Random r = new Random();
        int index = r.nextInt(26);
        return alphabet.substring(index, index + 1);

    }

    public static HashMap<String, String> loadDictionary(Context context) {
        // Load dictionary from assets directory into HashMap myDict
        // Hash function is the string value itself (for simplicity)
        HashMap<String, String> myDict = new HashMap<String, String>();
        AssetManager assetManager = context.getAssets();
        String line;

        try {
            InputStream ims = assetManager.open("dictionary.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(ims));
            try {
                while ((line=r.readLine()) != null) {
                    myDict.put(line, line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return myDict;

    }

    /*
    Imagine Boggle board as an array indexed as so:

    [0,  1,  2,   3,  4]
    [5,  6,  7,   8,  9]
    [10, 11, 12, 13, 14]
    [15, 16, 17, 18, 19]
    [20, 21, 22, 23, 24]

    Need to check which sequence of indices represents a valid horizontal, vertical or diagonal sequence.
     */

    private static boolean isHorizontal(ArrayList<Integer> sequence, int direction) {
        // Must all be +/- 1 and be divisible by 5 same # of times
        for (int count = 0; count < sequence.size() - 1; count++) {
            if ( (sequence.get(count) != sequence.get(count + 1) - direction) ||  (sequence.get(count)/5 != sequence.get(count + 1)/5)) {
                return false;
            }
        }

        return true;

    }

    private static boolean isVertical(ArrayList<Integer> sequence, int direction) {
        // Just a flipped (i.e. divided by 5) version of horizontal
        for (int count = 0; count < sequence.size() - 1; count++) {
            if (sequence.get(count) != sequence.get(count + 1) - 5*direction) {
                return false;
            }
        }

        ArrayList<Integer> newSequence = new ArrayList<Integer>();

        for (int count = 0; count < sequence.size(); count++) {
            newSequence.add(sequence.get(count)/5);
        }

        return isHorizontal(newSequence, direction);

    }

    private static boolean isUpRight(ArrayList<Integer> sequence, int direction) {
        // Must all be +/- 4, can't have entries in left-most column in middle of sequence
        for (int count = 0; count < sequence.size() - 1; count++) {
            if (sequence.get(count) != sequence.get(count + 1) - direction*4) {
                return false;
            }
        }

        int last_index = sequence.size() - 1;

        if ( (sequence.lastIndexOf(0) != -1 && sequence.lastIndexOf(0) != 0 && sequence.lastIndexOf(0) != last_index) ||
                (sequence.lastIndexOf(5) != -1 && sequence.lastIndexOf(5) != 0 && sequence.lastIndexOf(5) != last_index) ||
                (sequence.lastIndexOf(10) != -1 && sequence.lastIndexOf(10) != 0 && sequence.lastIndexOf(10) != last_index) ||
                (sequence.lastIndexOf(15) != -1 && sequence.lastIndexOf(15) != 0 && sequence.lastIndexOf(15) != last_index) ||
                (sequence.lastIndexOf(20) != -1 && sequence.lastIndexOf(20) != 0 && sequence.lastIndexOf(20) != last_index)) {
            return false;
        }

        return true;
    }

    private static boolean isUpLeft(ArrayList<Integer> sequence, int direction) {
        // Must all be +/- 6, can't have entries in right-most column in middle of sequence
        for (int count = 0; count < sequence.size() - 1; count++) {
            if (sequence.get(count) != sequence.get(count + 1) - direction*6) {
                return false;
            }
        }

        int last_index = sequence.size() - 1;

        if ( (sequence.lastIndexOf(4) != -1 && sequence.lastIndexOf(4) != 0 && sequence.lastIndexOf(4) != last_index) ||
                (sequence.lastIndexOf(9) != -1 && sequence.lastIndexOf(9) != 0 && sequence.lastIndexOf(9) != last_index) ||
                (sequence.lastIndexOf(14) != -1 && sequence.lastIndexOf(14) != 0 && sequence.lastIndexOf(14) != last_index) ||
                (sequence.lastIndexOf(19) != -1 && sequence.lastIndexOf(19) != 0 && sequence.lastIndexOf(19) != last_index) ||
                (sequence.lastIndexOf(24) != -1 && sequence.lastIndexOf(24) != 0 && sequence.lastIndexOf(24) != last_index)) {
            return false;
        }

        return true;
    }

    public static boolean isValidSequence(ArrayList<Integer> sequence) {
        if (sequence.size() < 2) {
            return false;
        } else if (sequence.get(1) == sequence.get(0) + 1) {
            // Check for a +1 group
            return isHorizontal(sequence, 1);
        } else if (sequence.get(1) == sequence.get(0) - 1) {
            // Check for a -1 group
            return isHorizontal(sequence, -1);
        } else if (sequence.get(1) == sequence.get(0) + 4) {
            // Check for a +4 group
            return isUpRight(sequence, 1);
        } else if (sequence.get(1) == sequence.get(0) - 4) {
            // Check for a -4 group
            return isUpRight(sequence, -1);
        } else if (sequence.get(1) == sequence.get(0) + 5) {
            // Check for a +5 group
            return isVertical(sequence, 1);
        } else if (sequence.get(1) == sequence.get(0) - 5) {
            // Check for a -5 group
            return isVertical(sequence, -1);
        } else if (sequence.get(1) == sequence.get(0) + 6) {
            // Check for a +6 group
            return isUpLeft(sequence, 1);
        } else if (sequence.get(1) == sequence.get(0) - 6) {
            // Check for a -6 group
            return isUpLeft(sequence, -1);
        }
        else
            return false;
    }

}
