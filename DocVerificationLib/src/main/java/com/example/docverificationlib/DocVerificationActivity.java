package com.example.docverificationlib;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.logging.FileHandler;

public class DocVerificationActivity extends AppCompatActivity {
    Button viewDoc,removeDoc,confirmUploadButton;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_verification);

        viewDoc = findViewById(R.id.viewDocButton);
        removeDoc = findViewById(R.id.removeDocButton);
        confirmUploadButton = findViewById(R.id.confirmUploadButton);

        viewDoc.setVisibility(View.GONE);
        removeDoc.setVisibility(View.GONE);
        confirmUploadButton.setVisibility(View.GONE);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DocVerificationActivity.this).setTitle("Exit")
                        .setMessage("Are you sure you want to exit application?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });
    }

    public void showFileChooser(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        File upload = new File(uri.getPath());
                        viewDoc.setVisibility(View.VISIBLE);
                        /*viewDoc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    Uri uploadUri = FileProvider.getUriForFile(DocVerificationActivity.this,getPackageName()+ ".provider",upload);
                                    intent.setDataAndType(uploadUri,getMimeType(upload.getAbsolutePath()));
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    // no Activity to handle this kind of files
                                }
                            }
                        });*/
                        removeDoc.setVisibility(View.VISIBLE);
                        removeDoc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewDoc.setVisibility(View.GONE);
                                removeDoc.setVisibility(View.GONE);
                                confirmUploadButton.setVisibility(View.GONE);
                            }
                        });
                        confirmUploadButton.setVisibility(View.VISIBLE);
                        confirmUploadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(DocVerificationActivity.this).setTitle("Success")
                                        .setMessage("Your file is uploaded Succesfully")
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).show();

                            }
                        });
                        viewDoc.setText(getFileName(uri));
                        Log.d("shankiekaddocverify", "File Uri: " + getFileName(uri));
                    }
                }
            });

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getMimeType(String url)
    {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(DocVerificationActivity.this).setTitle("Exit")
                .setMessage("Are you sure you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}