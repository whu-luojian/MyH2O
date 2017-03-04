package com.whu.myh2o;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {

    EditText location;
    EditText date;
    EditText notes;
    ImageView imgView;
    public CommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_comment, container, false);
        location = (EditText)v.findViewById(R.id.cmt_location);
        date = (EditText)v.findViewById(R.id.cmt_date);
        notes = (EditText)v.findViewById(R.id.cmt_notes);
        Button btn_img = (Button)v.findViewById(R.id.cmt_btn_loadpic);
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent(v);
            }
        });
        // edit location
        openMap();
        //edit date
        openDateDialog();
        //edit notes
        makeTextScroll(R.id.edit_notes);

        setHasOptionsMenu(true);
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)    {
        //MenuInflater inflater = getMenuInflater();
        menu.clear();
        inflater.inflate(R.menu.actionbar, menu);
        //return true;
        //super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_upload:
                if(CheckData())
                {
                    UploadData();
                    ClearData();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private boolean CheckData(){
        EditText fields[] = {location,date,notes};
        int flag=0;
        for(int i=0;i<fields.length;i++)
        {
            if(fields[i].getText().toString().equals(""))
            {
                fields[i].setError(this.getString(R.string.warn4blank));
                flag++;
            }
        }
        if(flag!=0) {
            Toast.makeText(getActivity(), "此条记录有错，请根据提示仔细修改！", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            Toast.makeText(getActivity(), "正在上传中，请稍候...", Toast.LENGTH_LONG).show();
            return true;
        }
    }
    private void UploadData(){
        //connect to database

        //store the record

        Toast.makeText(getActivity(), "恭喜您，记录上传成功！感谢您为中国水质所做的贡献！", Toast.LENGTH_LONG).show();
    }
    private void ClearData(){
        notes.setText("");
        if(imgView!=null)
            imgView.setImageBitmap(null);
    }


    //choose and upload images
    private static int PICK_IMAGE = 1;
    String imgDecodableString;
    Bitmap photoBit;
    public void openImageIntent(View view) {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "请选择图片";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == PICK_IMAGE) {
                    final boolean isCamera;
                    if (data == null || data.getData() == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        isCamera = action != null && action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                    imgView = (ImageView)getActivity().findViewById(R.id.cmt_imgView);

                    if (isCamera) {
                        if (data != null || data.getExtras() != null) {
                            Bundle extras = data.getExtras();
                            photoBit = (Bitmap) extras.get("data");
                            imgView.setImageBitmap(photoBit);
                            //save pics
                            createDirectoryAndSaveFile(photoBit,createFileName());
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "找不到图片！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        if (data != null) {
                            Uri selectedImage = data.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            // Get the cursor
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imgDecodableString = cursor.getString(columnIndex);
                            cursor.close();
                            imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                        } else {
                            Toast.makeText(getActivity(), "您还没有选择任何图片！", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else if(requestCode==OPEN_MAP){
                    Bundle extras = data.getExtras();
                    double[] result = extras.getDoubleArray("result");
                    String txt = "(" + String.valueOf(result[0]) + "," + String.valueOf(result[1]) + ")";
                    location.setText(txt);
                }
            }
        } catch (Exception e){
            Toast.makeText(getActivity(),"oops!出错啦！",Toast.LENGTH_LONG).show();
        }
    }
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/DCIM/MyH2O");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/DCIM/MyH2O");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/DCIM/MyH2O"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String createFileName(){
        String fileName="";
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dataFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        fileName = dataFormat.format(date)+".jpg";
        return fileName;
    }

    private static int OPEN_MAP = 2;
    private void openMap(){
        location.setInputType(InputType.TYPE_NULL);
        location.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent show_map_intent = new Intent(getActivity(), MapActivity.class);
                startActivityForResult(show_map_intent,OPEN_MAP);
            }
        });
    }

    private void openDateDialog() {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);

        date.setInputType(InputType.TYPE_NULL);

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar c = Calendar.getInstance();
                    new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(i, i1, i2);
                            date.setText(dateFormatter.format(newDate.getTime()));
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(i, i1, i2);
                        date.setText(dateFormatter.format(newDate.getTime()));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }

        });


    }

    private void makeTextScroll(final int resourceId){
        EditText editText = (EditText)getActivity().findViewById(resourceId);
        editText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == resourceId) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()& MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }
}
