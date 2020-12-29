package com.example.clone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clone.fragment.HomeFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {


    Uri imageUrl;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView close, imageadded;
    TextView post;
    EditText description;
    int request_code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close_post);
        imageadded = findViewById(R.id.image_added);
        post = findViewById(R.id.post_added);
        description = findViewById(R.id.description);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, HomePageActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadImage();
            }
        });


        try {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, request_code);
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
        }


        //Commented by Ajay
        //CropImage.activity()
          //      .setAspectRatio(1, 1)
            //    .start(PostActivity.this);

    }

    private String getFileExtension(Uri uri) {
        ContentResolver CR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(CR.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog P = new ProgressDialog(this);
        P.setMessage("Posting...");
        P.show();

        if (imageUrl != null) {
            final StorageReference filerefrence = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUrl));

            uploadTask = filerefrence.putFile(imageUrl);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isComplete()) {
                        throw task.getException();


                    }
                    return filerefrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

                        String postid = reference.push().getKey();


                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("postid", postid);
                        hm.put("postimage", myUrl);
                        hm.put("description", description.getText().toString());
                        hm.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).setValue(hm);
                        P.dismiss();

                        startActivity(new Intent(PostActivity.this, HomePageActivity.class));
                        finish();

                    } else {
                        Toast.makeText(PostActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image Selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,HomeFragment.class));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(request_code == requestCode){
            Toast.makeText(getApplicationContext(),"Image load" ,Toast.LENGTH_LONG).show();

            imageadded.setImageURI(data.getData());
            imageUrl = data.getData();
        }
        else{
            Toast.makeText(getApplicationContext(),"Something went Wrong",Toast.LENGTH_LONG).show();
        }


    }
}


// if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && requestCode == RESULT_OK) {
  //       CropImage.ActivityResult result = CropImage.getActivityResult(data);
    //     imageUrl = result.getUri();

    //     imageadded.setImageURI(imageUrl);
     //    } else {
     //    Toast.makeText(this, "Uploading Failed", Toast.LENGTH_SHORT).show();
    //     startActivity(new Intent(PostActivity.this, HomePageActivity.class));
     //   finish();
     //   }
