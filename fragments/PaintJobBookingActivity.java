package com.car.cartique.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.car.cartique.R;
import com.car.cartique.ResultsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class PaintJobBookingActivity extends Fragment {

    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static int RESULT_LOAD_IMG = 1;
    EditText txtMake;
    EditText txtModel;
    EditText txtYear;
    TextView txtFilesChosenText;
    AppCompatButton btnChoose;
    AppCompatButton btnUpload;
    ByteArrayInputStream imagestream;
    View decodeview;
    String imgDecodableString;
    View v;
    Activity activity;
    FirebaseStorage firebaseStorageReference;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Uri> chosenImages;
    private FirebaseAuth auth;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = inflater.inflate(R.layout.pain_job_booking, container, false);

        firebaseStorageReference = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        permissionStatus = activity.getSharedPreferences("permissionStatus", MODE_PRIVATE);

        txtMake = v.findViewById(R.id.txtPaintMake);
        txtModel = v.findViewById(R.id.txtPaintModel);
        txtYear = v.findViewById(R.id.txtPaintYear);
        txtFilesChosenText = v.findViewById(R.id.txtFilesChosen);
        btnChoose = v.findViewById(R.id.btnPaintUpload);
        btnUpload = v.findViewById(R.id.btnGetQuotes);
        btnChoose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                } else {
                    // Create intent to Open Image applications like Gallery, Google Photos
                    // Undocumented way to get multiple photo selections from Android Gallery ( on Samsung )
                    Intent intent = new Intent("android.intent.action.MULTIPLE_PICK");//("Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    // Check to see if it can be handled...
                    PackageManager manager = getActivity().getApplicationContext().getPackageManager();
                    List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                    String action = "";
                    if (infos.size() > 0) {
                        // Ok, "android.intent.action.MULTIPLE_PICK" can be handled
                        action = "android.intent.action.MULTIPLE_PICK";
                    } else {
                        action = Intent.ACTION_GET_CONTENT;
         /* This is the documented way you are to get multiple images from a gallery BUT IT DOES NOT WORK with Android Gallery! (at least on Samsung )
           But the Android Email client WORKS! What the f'k!
               */
                        intent.setAction(action);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Note: only supported after Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT, harmless if used below 19, but no mutliple selection supported
                    }
                    startActivityForResult(intent, RESULT_LOAD_IMG);

                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

        return v;
    }

    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (chosenImages != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            int count = 0;
            final int sum = chosenImages.size();

            for (int i = 0; i < chosenImages.size(); i++) {
                count++;
                final int finalCount = count;
                String uniqueID = UUID.randomUUID().toString();
                StorageReference riversRef = firebaseStorageReference.getReferenceFromUrl("gs://cartique-1516308965713.appspot.com").child("/" + auth.getCurrentUser().getEmail() + "/images/PaintJob/UserPaintUpload" + uniqueID + ".jpg");
                riversRef.putFile(chosenImages.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //if the upload is successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying a success toast
                               /* Toast.makeText(activity.getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();*/


                                Intent resultIntent = new Intent(activity.getApplicationContext(), ResultsActivity.class);
                                startActivity(resultIntent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying error message
                                Toast.makeText(activity.getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                //displaying percentage in progress dialog
                                progressDialog.setMessage("Uploaded image " + finalCount + "of" + sum + ((int) progress) + "%...");
                            }
                        });
            }
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == -1 && requestCode == 1 && data != null) {
                if (data.getAction().equals(Intent.ACTION_SEND_MULTIPLE)) {
                    final Bundle extras = data.getExtras();
                    int count = extras.getInt("selectedCount");
                    Object obj = extras.get("selectedItems");

                    chosenImages = (ArrayList<Uri>) obj;

                    txtFilesChosenText.setVisibility(View.VISIBLE);
                    txtFilesChosenText.setText(count + " images chosen to upload");

                } else {
                    if (data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        // do somthing
                        imgDecodableString = uri.toString();
                        txtYear.setText(imgDecodableString);

                        Toast.makeText(activity, "File chosen" + imgDecodableString, Toast.LENGTH_LONG)
                                .show();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                ArrayList<Uri> uris = new ArrayList<>();
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    ClipData.Item item = clipData.getItemAt(i);
                                    Uri uri = item.getUri();
                                    uris.add(uri);
                                }
                                imgDecodableString = uris.toString();
                                txtYear.setText(imgDecodableString);
                            }
                        }
                    }
                }
            } else if (requestCode == REQUEST_PERMISSION_SETTING) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    //Got Permission
                    proceedAfterPermission();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Toast.makeText(activity, "Something went wrong " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void proceedAfterPermission() {
        //We've got the permission, now we can proceed further
        Toast.makeText(activity.getBaseContext(), "We got the Storage Permission", Toast.LENGTH_LONG).show();
    }

    private void requestPermissions() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //Show Information about why you need the permission
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Need Storage Permission");
            builder.setMessage("This app needs storage permission.");
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else if (permissionStatus.getBoolean(Manifest.permission.READ_EXTERNAL_STORAGE, false)) {
            //Previously Permission Request was cancelled with 'Dont Ask Again',
            // Redirect to Settings after showing Information about why you need the permission
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Need Storage Permission");
            builder.setMessage("This app needs storage permission.");
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    sentToSettings = true;
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    Toast.makeText(activity.getApplicationContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else {
            //just request the permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
        }

        SharedPreferences.Editor editor = permissionStatus.edit();
        editor.putBoolean(Manifest.permission.READ_EXTERNAL_STORAGE, true);
        editor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                proceedAfterPermission();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

}