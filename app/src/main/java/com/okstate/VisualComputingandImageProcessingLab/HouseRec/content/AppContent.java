package com.okstate.VisualComputingandImageProcessingLab.HouseRec.content;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class AppContent implements Serializable {

    /**
     * An array of sample (content) items.
     */
    public List<HomeContext> ITEMS = new ArrayList<HomeContext>();

    /**
     * A map of sample (content) items, by ID.
     */
    public final Map<String, HomeContext> ITEM_MAP = new HashMap<String, HomeContext>();


    /**
     * A map of sample (content) items, by layout.
     */
    public HashMap<String, List<HomeContext>> LAYOUT_MAP = new HashMap<String, List<HomeContext>>();


    public final int MAX_CLASSES = 20;

    // Singleton Stuff:
    private static volatile AppContent instance;

//    public TransferLearningModelWrapper tlModel;

    private AppContent() {
        // Add some sample items.
//        for (int i = 1; i <= count; i++) {
//            addItem(createDummyItem(i));
//        }

        //Prevent form the reflection api.
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static void findOrSave(File path) {
        File data = new File(path, "appSaveState.data");
        Log.v("AppContent","Size of appSaveState.data = " + data.length());

        if (instance == null) {
            synchronized (AppContent.class) {
                if (instance == null) {
                    try {
                        ObjectInputStream in = new ObjectInputStream(new FileInputStream(data));
                        instance = (AppContent) in.readObject();
                    } catch (FileNotFoundException e) {
                        instance = new AppContent();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (instance == null) {
                        instance = new AppContent();
                    }
                }
            }
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(data));
            out.writeObject(getInstance());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Log.d("AppContent", path.list()[0]);
    }

    public static AppContent getInstance() {
        return instance;
    }

    //Make singleton from serialize and deserialize operation.
    protected AppContent readResolve() {
        return getInstance();
    }



    public void newItem(int position,String layout) {
        if (position < MAX_CLASSES) {
            addItem(createDummyItem(position,layout));
        }
    }


//    public void newLayout(UUID id, String layoutName, ) {
//            createDummyLayout(id,layoutName);
//    }


    private void addItem(HomeContext item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id,item);
    }



    public void addLayout() {
        HashMap<String, List<HomeContext>> TEMP_LAYOUT_MAP = new HashMap<String, List<HomeContext>>();
        for(HomeContext single : ITEMS){
            List<HomeContext> tempList = TEMP_LAYOUT_MAP.get(single.layout);
            if(tempList == null){
                tempList = new ArrayList<>();
                tempList.add(single);
                TEMP_LAYOUT_MAP.put(single.layout, tempList);
            }
            else{
                tempList.add(single);
            }
        }
        LAYOUT_MAP = TEMP_LAYOUT_MAP;
    }

    private HomeContext createDummyItem(int position,String layout) {
        return new HomeContext(String.valueOf(position), layout,"Location" + position, makeDetails());
    }
//
//    private LayoutContext createDummyLayout(UUID id,String layout) {
//        return new LayoutContext(id, layout,"Location" + position, makeDetails());
//    }

    private String makeDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nMore details information here.");
        return builder.toString();
    }
}
