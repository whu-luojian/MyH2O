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
import android.support.annotation.Nullable;
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
public class UploadFragment extends Fragment {


    private EditText location;
    private EditText date;
    private EditText date2;
    private SimpleDateFormat dateFormatter;

    private EditText smell;
    private EditText gross;
    private EditText nitrate;
    private EditText cod;
    private EditText fe;
    private EditText as;
    private EditText an;
    private EditText tds;
    private EditText ph;
    private EditText coli;
    private EditText notes;
    ImageView imgView;
    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload, container, false);
        Button btn_img = (Button)v.findViewById(R.id.buttionLoadPics);
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent(v);
            }
        });
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        location = (EditText)getActivity().findViewById(R.id.edit_location);
        date = (EditText)getActivity().findViewById(R.id.edit_date);
        date2 = (EditText)getActivity().findViewById(R.id.edit_date2);
        smell = (EditText)getActivity().findViewById(R.id.edit_smell);
        gross = (EditText)getActivity().findViewById(R.id.edit_gross);
        nitrate = (EditText)getActivity().findViewById(R.id.edit_no3);
        cod = (EditText)getActivity().findViewById(R.id.edit_cod);
        fe = (EditText)getActivity().findViewById(R.id.edit_fe);
        as = (EditText)getActivity().findViewById(R.id.edit_as);
        an = (EditText)getActivity().findViewById(R.id.edit_AN);
        tds = (EditText)getActivity().findViewById(R.id.edit_tds);
        ph = (EditText)getActivity().findViewById(R.id.edit_ph);
        coli = (EditText)getActivity().findViewById(R.id.edit_coli);
        notes = (EditText)getActivity().findViewById(R.id.edit_notes);
        // edit location
        openMap();
        //edit date
        openDateDialog();
        //edit notes
        makeTextScroll(R.id.edit_notes);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)    {
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
        EditText fields[] = {location,date,date2,smell,gross,nitrate,cod,fe,as,an,tds,ph,coli};
        String fields_text[]=new String[fields.length];
        int i =0;int flag=0;
        for(;i<fields.length;i++)
            fields_text[i]=fields[i].getText().toString();
        int field_max_values[] = {-1,-1,-1,-1,-1,200,50,5,1,5,15000,10,10000};
        int field_warns[] = {R.string.warn4blank,R.string.warn4blank,R.string.warn4blank,R.string.warn4blank,R.string.warn4blank,
                R.string.warn4nitrate,R.string.warn4cod,R.string.warn4fe,R.string.warn4as,R.string.warn4an,
                R.string.warn4tds,R.string.warn4ph,R.string.warn4coli};

        for(i=0;i<fields.length;i++)
        {
            if(fields_text[i].equals(""))
            {
                fields[i].setError(this.getString(R.string.warn4blank));
                flag++;
            }
            else {
                switch (fields[i].getInputType()) {
                    case InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL:
                        if (Float.parseFloat(fields[i].getText().toString()) - field_max_values[i] > 0) {
                            fields[i].setError(this.getString(field_warns[i]));
                            flag++;
                        }
                        break;
                    case InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL:
                        if (Integer.parseInt(fields[i].getText().toString()) - field_max_values[i] > 0) {
                            fields[i].setError(this.getString(field_warns[i]));
                            flag++;
                        }
                        break;
                }
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
        smell.setText("");
        gross.setText("");
        nitrate.setText("");
        cod.setText("");
        fe.setText("");
        as.setText("");
        an.setText("");
        tds.setText("");
        ph.setText("");
        coli.setText("");
        notes.setText("");
        if(imgView!=null)
            imgView.setImageBitmap(null);
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
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);

        date.setInputType(InputType.TYPE_NULL);
        date2.setInputType(InputType.TYPE_NULL);

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
        date2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar c = Calendar.getInstance();
                    new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(i, i1, i2);
                            date2.setText(dateFormatter.format(newDate.getTime()));
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(i, i1, i2);
                        date2.setText(dateFormatter.format(newDate.getTime()));
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
                    imgView = (ImageView)getActivity().findViewById(R.id.imgView);

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
    @Override
    public void onResume() {
        super.onResume();
    }

}
