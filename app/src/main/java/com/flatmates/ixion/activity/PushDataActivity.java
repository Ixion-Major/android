package com.flatmates.ixion.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.flatmates.ixion.InitApplication;
import com.flatmates.ixion.R;
import com.flatmates.ixion.activity.chat.UserChatActivity;
import com.flatmates.ixion.model.Data;
import com.flatmates.ixion.utils.Endpoints;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.flatmates.ixion.utils.Constants.USER_EMAIL;


public class PushDataActivity extends AppCompatActivity {

    @BindView(R.id.btn_submit)
    Button buttonSubmit;
    @BindView(R.id.btn_upload_image)
    Button buttonUploadImage;
    @BindView(R.id.switch_upload_decentralised)
    Switch switchUploadDecentralised;
    @BindView(R.id.EditText_Name)
    MaterialEditText etname;
    @BindView(R.id.EditText_EMail)
    MaterialEditText etemail;
    @BindView(R.id.EditText_PhoneNo)
    MaterialEditText etphone;
    @BindView(R.id.EditText_Address)
    MaterialEditText etaddress;
    @BindView(R.id.EditText_Rent)
    MaterialEditText etrent;
    @BindView(R.id.EditText_BHK)
    MaterialEditText etbhk;
    @BindView(R.id.EditText_city)
    MaterialEditText etcity;
    @BindView(R.id.EditText_Area)
    MaterialEditText etarea;
    @BindView(R.id.EditText_state)
    MaterialEditText etstate;
    @BindView(R.id.EditText_title)
    MaterialEditText etTitle;
    @BindView(R.id.EditText_desc)
    MaterialEditText etDescription;

    String lon_str, lat_str;
    Data data;
    String image1 = "", image2 = "", image3 = "";
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRefUserData = database.getReference("Data");

    private static final String TAG = PushDataActivity.class.getSimpleName();

    private StorageReference storageReference;

    private static final int PICK_IMAGE_REQUEST = 234;

    private int REQUEST_CODE_PICKER = 2000;

    private Uri filePath;
    private Uri firebaseUri = Uri.parse("");
    private ArrayList<Image> images = new ArrayList<>();
    SharedPreferences preferences;
    private String userEmail;
    private int check = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_data);
        ButterKnife.bind(this);
        setTitle("List Property");
        storageReference = FirebaseStorage.getInstance().getReference();
        preferences = PreferenceManager.getDefaultSharedPreferences(PushDataActivity.this);
        userEmail = preferences.getString(USER_EMAIL, "").replace(".", "_@_");

        data = new Data();

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

    }

    @OnClick(R.id.btn_upload_image)
    public void uploadImage() {
        showFileChooser();
    }

    private void showFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

        ImagePicker.create(this)
                .imageTitle("Tap to select")
                .multi()
                .limit(3)
                .start(REQUEST_CODE_PICKER);
    }

    //handling the image chooser activity result
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            filePath = data.getData();
//            uploadFile();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
//            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0, l = images.size(); i < l; i++) {
                filePath = Uri.fromFile(new File(images.get(i).getPath()));
                uploadFile(i);
            }
//            textView.setText(stringBuffer.toString());
        }
    }

    private void uploadFile(final int count) {
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = storageReference.child(userEmail+ "/pic" + count + ".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            firebaseUri = taskSnapshot.getDownloadUrl();
                            buttonUploadImage.setText("Images Uploaded");
                            switch (count) {
                                case 0:
                                    data.setPurl(firebaseUri.toString());
                                    data.setPurl1(firebaseUri.toString());
                                    break;
                                case 1:
                                    data.setPurl2(firebaseUri.toString());
                                    break;
                                case 2:
                                    data.setPurl3(firebaseUri.toString());
                                    Toast.makeText(PushDataActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                    check = 1;
                                    break;
                                default:
                                    System.out.println("default");
                            }

                            //hiding the progress dialog
                            progressDialog.dismiss();
                            //and displaying a success toast
                            if(check == 1)
                                Toast.makeText(PushDataActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }


    @OnClick(R.id.btn_submit)
    public void pushToFirebase() {
        data.setName(etname.getText().toString().trim());
        data.setEmail(etemail.getText().toString().trim());
        data.setMobile(etphone.getText().toString().trim());
        data.setAddress(etaddress.getText().toString().trim());
        data.setRent(etrent.getText().toString().trim());
        data.setCity(etcity.getText().toString().toLowerCase().trim());
        data.setArea(etarea.getText().toString().toLowerCase().trim());
        data.setState(etstate.getText().toString().toLowerCase().trim());
        data.setBhk(etbhk.getText().toString() + "bhk");
        String address = etaddress.getText().toString() + "," + etarea.getText().toString() + ", " + etcity.getText().toString()
                + ", " + etstate.getText().toString();
        getLocationFromAddress(address);
        data.setLon(lon_str);
        data.setLat(lat_str);
        data.setPurl(firebaseUri.toString());

        //TODO: Check for correct images input before pushing
        if (!switchUploadDecentralised.isChecked() && !etTitle.getText().toString().equals("")
                && !etDescription.getText().toString().equals("") && !etbhk.getText().toString().equals("")
                && !etphone.getText().toString().equals("") && !etstate.getText().toString().equals("") &&
                !etarea.getText().toString().equals("") && !etcity.getText().toString().equals("") &&
                !etrent.getText().toString().equals("") && !etname.getText().toString().equals("") && check==1) {
            myRefUserData.push().setValue(data);
            Toast.makeText(this, "Property Listed!", Toast.LENGTH_SHORT).show();
            etname.setText("");
            etemail.setText("");
            etbhk.setText("");
            etDescription.setText("");
            etTitle.setText("");
            etaddress.setText("");
            etarea.setText("");
            etphone.setText("");
            etcity.setText("");
            etstate.setText("");
            etrent.setText("");
            buttonUploadImage.setText("Upload Image");
        } else {
            if (switchUploadDecentralised.isChecked() && !etTitle.getText().toString().equals("")
                    && !etDescription.getText().toString().equals("") && !etbhk.getText().toString().equals("")
                    && !etphone.getText().toString().equals("") && !etstate.getText().toString().equals("") &&
                    !etarea.getText().toString().equals("") && !etcity.getText().toString().equals("") &&
                    !etrent.getText().toString().equals("") && !etname.getText().toString().equals("")) {
                myRefUserData.push().setValue(data);
                uploadToBlockChain();
            } else {
                Toast.makeText(this, "Please fill all information", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadToBlockChain() {
        StringRequest request = new StringRequest(Request.Method.POST,
                Endpoints.endpointCreateListing(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "onResponse: " + response);
                        if (response.contains("true")) {
                            Toast.makeText(PushDataActivity.this, "Successfully created contract listing",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                        Toast.makeText(PushDataActivity.this, "Couldn't create a contract",
                                Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("expiration_date", "");
                params.put("metadata_category", "PHYSICAL_GOOD");
                params.put("title", etTitle.getText().toString());
                params.put("description", etDescription.getText().toString());
                params.put("currency_code", "INR");
                params.put("price", etrent.getText().toString());
                params.put("process_time", "3");
                params.put("nsfw", "false");
                params.put("shipping_origin", "India");
                params.put("shipping_regions", "India");
                params.put("est_delivery_domestic", "7");
                params.put("est_delivery_international", "15");
                params.put("keywords", "['ixion']");
                params.put("category", "['" + etbhk.getText().toString() + "bhk" + "', '"
                        + etcity.getText().toString() + "']");
                params.put("terms_conditions", "");
                params.put("returns", "");
                params.put("shipping_currency_code", "INR");
                params.put("shipping_domestic", "100");
                params.put("shipping_international", "500");
                params.put("condition", "new");
                params.put("free_shipping", "false");
                params.put("sku", "");
                params.put("images", "2e541a02e89c532d726d95e62389c721563cdd29");   //TODO: use upload image api
                params.put("moderators", "['1010c55065e1248ce55485b92f7b3cf408b2c9aa']");

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        InitApplication.getInstance().addToQueue(request);
    }

    public String getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            lat_str = String.valueOf(location.getLatitude());

            lon_str = String.valueOf(location.getLongitude());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}