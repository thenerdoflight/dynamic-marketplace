package org.dynamicmarketplace.dynamicmarketplace.savedata;

import java.util.ArrayList;

/* ==================================================================
    Recipie Save Data
    - Controlls access and loading of the varius recipies/*.txt files 
    - Reads and interprits file as item name and recipie listings
================================================================== */

public class Recipies{
 
    private SingleRecipieFile[] recipieFiles;
    
    // Initalization

    public Recipies ( ArrayList<String> filePaths ) {

        int recipieFileCount = filePaths.size();
        recipieFiles = new SingleRecipieFile[recipieFileCount];
        
        for ( int i=0; i < recipieFileCount; i ++ ){
            recipieFiles[i] = new SingleRecipieFile( filePaths.get(i) );
        }

    }

    // Get Data

    public Recipie getRecipie ( String item ){
        for ( int i=0 ; i<recipieFiles.length ; i++)
            if ( recipieFiles[i].recipies.containsKey( item ))
                return recipieFiles[i].recipies.get(item);
        return null;
    }



}