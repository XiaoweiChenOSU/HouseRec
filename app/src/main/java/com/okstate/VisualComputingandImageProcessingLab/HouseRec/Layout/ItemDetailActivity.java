package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Button;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;

import androidx.appcompat.widget.Toolbar;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    private ToDoItem mPhotoLayoutPath;
    private String ITEM_ID;
    public static final String LAYOUT_ITEM = "LAYOUT_ITEM";
    public static final String ARG_ITEM_ID = "ITEM_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        mPhotoLayoutPath = (ToDoItem) b.get(ItemPhotosActivity.LAYOUT_ITEM);
        ITEM_ID = (String) b.get(ItemPhotosActivity.ARG_ITEM_ID);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }


        Button galleryButton = findViewById(R.id.photos);
        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GalleryActivity.class);
            intent.putExtra(ARG_ITEM_ID,ITEM_ID);
            intent.putExtra(LAYOUT_ITEM, mPhotoLayoutPath);
            startActivity(intent);
        });
/*
        Button ItemButton = findViewById(R.id.items_not_used);
        ItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddItems_TempTest.class);
            //intent.putExtra(ARG_ITEM_ID,ITEM_ID);
            //intent.putExtra(LAYOUT_ITEM, mPhotoLayoutPath);
            startActivity(intent);
        });
*/




    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent = new Intent(this, ItemPhotosActivity.class);
            intent.putExtra(LAYOUT_ITEM, mPhotoLayoutPath);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
