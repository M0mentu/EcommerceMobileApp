package com.example.momentu.ecommercemobileapp;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.media.Image;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.example.momentu.ecommercemobileapp.models.prodcuts;
import com.example.momentu.ecommercemobileapp.viewholder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import io.paperdb.Paper;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;

public class fragment_search extends Fragment {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private static final String TAG = "VoiceRecognition";
    private SpeechRecognizer sr;
    private EditText searchtext;
    private ImageView searchImage, searchVoiceImage;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference pref;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    TextView t;


    public fragment_search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchtext = (EditText) rootView.findViewById(R.id.searchbarEditText);
        searchImage = (ImageView) rootView.findViewById(R.id.searchImageView);

        searchVoiceImage = (ImageView) rootView.findViewById(R.id.searchVoiceImageView);
        surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceview);

        barcodeDetector = new BarcodeDetector.Builder(rootView.getContext()).
                setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(rootView.getContext(), barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                try{
                    cameraSource.start(holder);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();

            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcode=detections.getDetectedItems();
                if (qrcode.size()!=0)
                {
                    searchtext.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator=(Vibrator)rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            searchtext.setText(qrcode.valueAt(0).displayValue);
                            productsSearch();
                        }
                    });

                }
            }
        });



        recyclerView=(RecyclerView)rootView.findViewById(R.id.search_recycleview);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        pref=FirebaseDatabase.getInstance().getReference().child("products");


        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productsSearch();
            }
        });
        searchVoiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();

            }
        });





        return rootView;
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!");

        // Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Specify how many results you want to receive. The results will be sorted
        // where the first result is the one with higher confidence.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        Log.i(TAG,"Calling the Voice Intenet");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {


            // Fill the list view with the strings the recognizer thought it could have heard, there should be 5, based on the call
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            //display results.
            logthis("results: "+String.valueOf(matches.size()));
            for (int i = 0; i < matches.size(); i++)
            {
                searchtext.setText(matches.get(0).toLowerCase());
                Log.d(TAG, "result " + matches.get(i));
                logthis("result " +i+":"+ matches.get(i));
                productsSearch();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void logthis (String newinfo) {
        if (newinfo != "") {
            Log.d(TAG, "logthis: "+newinfo);
        }
    }

    private void productsSearch() {

        Query FireQuery=pref.orderByChild("name").startAt(searchtext.getText().toString().toLowerCase()).endAt(searchtext.getText().toString()+"\uf8ff");
        FirebaseRecyclerOptions<prodcuts> options=new FirebaseRecyclerOptions.Builder<prodcuts>().setQuery(FireQuery,prodcuts.class).build();

        final FirebaseRecyclerAdapter<prodcuts,ProductViewHolder> adabter= new FirebaseRecyclerAdapter<prodcuts, ProductViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull prodcuts model) {



                    final   String productKey=getRef(position).getKey();  //getting the key of the item position

                    holder.etProductName.setText(model.getName());
                    holder.etProductyDesc.setText(model.getDescription());
                    holder.getEtProductPrice.setText(model.getPrice()+" EGP");
                    holder.etProductQuantity.setText("Quantity "+model.getQuantity());
                    Picasso.get().load(model.getImage()).into(holder.imageView);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent clickProductIntent=new Intent(getActivity(),product_details_Activity.class);
                            clickProductIntent.putExtra("postKey",productKey);
                            startActivity(clickProductIntent);
                        }
                    });
                }


            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.products_layout,viewGroup,false);
                ProductViewHolder holder=new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adabter);
        adabter.startListening();
    }





}
