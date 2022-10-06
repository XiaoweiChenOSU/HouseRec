package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.AddToDo.AddToDoFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.content.AppContent;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.content.HomeContext;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GalleryActivity extends AppCompatActivity {

    private GridView gridView;

    public String layoutname;

    public String locationName;
    public File photoPath;

    private String item_id;

    public static final String ARG_ITEM_ID = "item_id";

    public static final String ARG_ITEM = "item_id";

    private HomeContext mPhotoLayoutItem;
    private ToDoItem mPhotoLayoutPath;
    //图片的文字标题
//    private String[] titles;
    //    private String[] titles =
//            { "pic1", "pic2", "pic3", "pic4", "pic5", "pic6", "pic7", "pic8", "pic9"};
    //图片ID数组
//    private int[] p_w_picpaths;
//    private int[] p_w_picpaths = new int[]{
//            R.drawable.home, R.drawable.home, R.drawable.home,
//            R.drawable.home, R.drawable.home, R.drawable.home,
//            R.drawable.home, R.drawable.home,R.drawable.home
//    };
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = (GridView) findViewById(R.id.gridview);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        item_id = (String) b.get(ItemDetailActivity.ARG_ITEM_ID);
        mPhotoLayoutPath = (ToDoItem) b.get(ItemDetailActivity.LAYOUT_ITEM);
        layoutname = mPhotoLayoutPath.getToDoText().replaceAll(" ","");
        mPhotoLayoutItem = AppContent.getInstance().LAYOUT_MAP.get(layoutname).get(Integer.parseInt(item_id));;
        locationName = mPhotoLayoutItem.name;

        photoPath = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/" + locationName+"/" );

        PictureAdapter adapter = new PictureAdapter(photoPath, this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                Toast.makeText(GalleryActivity.this, "pic" + (position+1), Toast.LENGTH_SHORT).show();
            }
        });



    }
}

//自定义适配器
class PictureAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<Picture> pictures;
    public PictureAdapter(File photoPath, Context context)
    {
        super();
        pictures = new ArrayList<Picture>();
        inflater = LayoutInflater.from(context);
        File list[] = photoPath.listFiles();

        for (int i = 0; i < list.length; i++)
        {
            String title = "picture" +  Integer.toString(i+1);
            Picture picture = new Picture(title, list[i]);
            pictures.add(picture);
        }
    }
    @Override
    public int getCount()
    {
        if (null != pictures)
        {
            return pictures.size();
        } else
        {
            return 0;
        }
    }
    @Override
    public Object getItem(int position)
    {
        return pictures.get(position);
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        byte[] imageData = null;
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.picture_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.p_w_picpath = (ImageView) convertView.findViewById(R.id.p_w_picpath);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(pictures.get(position).getTitle());
        try {
//            FileInputStream inStream = new FileInputStream(pictures.get(position).getImageId());
//            Bitmap bitmap  = BitmapFactory.decodeStream(inStream);
//
//            BitmapDrawable bd= new BitmapDrawable(bitmap);
//            viewHolder.p_w_picpath.setBackground(bd);
//            final int THUMBSIZE  = 64;
//
//            FileInputStream fis = new FileInputStream(pictures.get(position).getImageId());
//            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
//
//            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pictures.get(position).getImageId().getPath()),
//                    THUMBSIZE, THUMBSIZE);
//            BitmapDrawable bd= new BitmapDrawable(null, ThumbImage);
//            viewHolder.p_w_picpath.setBackground(bd);

            FileInputStream fis = new FileInputStream(pictures.get(position).getImageId());

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            String filePath = pictures.get(position).getImageId().getPath();
            String dateTaken = pictures.get(position).getImageId().getName();

            Bitmap bitmap = null;
                bitmap = BitmapFactory.decodeFile(filePath, options);

            viewHolder.p_w_picpath.setImageBitmap(bitmap);



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return convertView;
    }

}

class ViewHolder
{
    public TextView title;
    public ImageView p_w_picpath;
}

class Picture
{
    private String title;
    private File p_w_picpathId;
    public Picture()
    {
        super();
    }
    public Picture(String title, File p_w_picpathId)
    {
        super();
        this.title = title;
        this.p_w_picpathId = p_w_picpathId;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public File getImageId()
    {
        return p_w_picpathId;
    }
    public void setImageId(File p_w_picpathId)
    {
        this.p_w_picpathId = p_w_picpathId;
    }
}