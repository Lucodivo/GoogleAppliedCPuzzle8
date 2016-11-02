package com.google.engedu.puzzle8;

import java.util.Comparator;

public class PuzzleBoardComparator implements Comparator<PuzzleBoard>
{
    @Override
    public int compare(PuzzleBoard x, PuzzleBoard y)
    {
        if (x.priority < y.priority)
        {
            return -1;
        }
        if (x.priority > y.priority)
        {
            return 1;
        }
        return 0;
    }
}
