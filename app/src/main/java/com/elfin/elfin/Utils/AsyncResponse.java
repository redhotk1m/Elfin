package com.elfin.elfin.Utils;

import java.util.ArrayList;

/**
 * Interface for collecting the Arraylist of arraylist for DisplaySuggestion from the Asynctask.
 * The first arraylist is for the Adress and the other Arraylist is for placesId which is in some way the
 * coordinat for that posision.
 *
 */

public interface AsyncResponse {
    void processFinish(ArrayList<ArrayList<String>> lists);
}
