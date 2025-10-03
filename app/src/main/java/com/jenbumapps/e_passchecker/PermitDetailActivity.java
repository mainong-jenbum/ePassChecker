package com.jenbumapps.e_passchecker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jenbumapps.core.api.ApiManager;
import com.jenbumapps.core.model.EPass;
import com.jenbumapps.core.model.EPassTerm;
import com.jenbumapps.core.model.File;
import com.jenbumapps.core.model.Traveller;
import com.jenbumapps.core.model.codec.ApproveStatus;
import com.jenbumapps.core.model.time.DateHelper;
import com.jenbumapps.e_passchecker.adapter.FileAdapter;
import com.jenbumapps.e_passchecker.adapter.TermsAdapter;
import com.jenbumapps.e_passchecker.adapter.TravellerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PermitDetailActivity extends AppCompatActivity implements TravellerAdapter.Listener, FileAdapter.Listener {

    private TextView tvPermitId;
    private TextView tvPermitApprovalDate;
    private TextView tvPermissionDetail;
    private RecyclerView rvTravellers;
    private TextView tvDoj;
    private TextView tvVehicleRc;
    private TextView tvDriverName;
    private TextView tvDriverContact;
    private TextView tvRoute;
    private RecyclerView rvTerms;
    private ImageView ivQrCode;
    private ImageView ivAuthSign;
    private TextView tvAuthName;
    private TextView tvAuthDesignation;
    private TextView tvAuthAddress;
    private TextView tvCity;
    private ImageView ivApprove;

    private CardView cvSupportingDoc;
    private RecyclerView rvDocs;

    // Header
    private TextView tvAuthorityDesignationAbbr;
    private TextView tvAuthorityOfficeName;
    private TextView tvAuthorityOfficeAddress;

    // Adapters
    private TermsAdapter termsAdapter;
    private TravellerAdapter travellerAdapter;
    private FileAdapter fileAdapter;

    // List
    private List<EPassTerm> terms = new ArrayList<>();
    private List<Traveller> travellers = new ArrayList<>();
    private List<File> files = new ArrayList<>();
    private int ePassId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_e_pass_detail);

        initView();
        initAdapter();

        checkData();

    }

    private void initView() {
        // Header
        tvAuthorityDesignationAbbr = findViewById(R.id.tv_authority_designation_abbr);
        tvAuthorityOfficeName = findViewById(R.id.tv_authority_office_name);
        tvAuthorityOfficeAddress = findViewById(R.id.tv_authority_office_address);

        tvCity = findViewById(R.id.tv_city);
        ivApprove = findViewById(R.id.iv_approved);
        tvPermitId = findViewById(R.id.tv_permit_id);
        tvPermitApprovalDate = findViewById(R.id.tv_permit_approval_date);
        tvPermissionDetail = findViewById(R.id.tv_permit_detail);
        rvTravellers = findViewById(R.id.rv_traveller);
        tvDoj = findViewById(R.id.tv_doj);
        tvVehicleRc = findViewById(R.id.tv_vehicle_rc);
        tvDriverName = findViewById(R.id.tv_driver_name);
        tvDriverContact = findViewById(R.id.tv_driver_contact);
        tvRoute = findViewById(R.id.tv_route);
        rvTerms = findViewById(R.id.rv_terms);
        ivQrCode = findViewById(R.id.iv_qr_code);
        ivAuthSign = findViewById(R.id.iv_authority_sign);
        tvAuthName = findViewById(R.id.tv_authority_name);
        tvAuthDesignation = findViewById(R.id.tv_authority_designation);
        tvAuthAddress = findViewById(R.id.tv_authority_address);
        rvDocs = findViewById(R.id.rv_doc);
        cvSupportingDoc = findViewById(R.id.cv_supporting_doc);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    private void initAdapter() {
        travellerAdapter = new TravellerAdapter(travellers, this, false);
        rvTravellers.setAdapter(travellerAdapter);
        rvTravellers.setLayoutManager(new LinearLayoutManager(this));

        termsAdapter = new TermsAdapter(terms);
        rvTerms.setAdapter(termsAdapter);
        rvTerms.setLayoutManager(new LinearLayoutManager(this));

        fileAdapter = new FileAdapter(files, this, false);
        rvDocs.setAdapter(fileAdapter);
        rvDocs.setLayoutManager(new LinearLayoutManager(this));
    }

    private void checkData() {
        ePassId = getIntent().getIntExtra("KEY_E_PASS_ID",0);
        Log.d("PermitDetail","Pass Id : "+ePassId);

        if(ePassId == 0) {
            Toast.makeText(this, "Invalid KEY ID Received", Toast.LENGTH_SHORT).show();
            finish();
        }

        fetchData();

    }

    private void fetchData() {
        final Dialog dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Fetching data..")
                .setCancelable(true)
                .build();
        dialog.show();
        ApiManager.form().fetchById(ePassId).enqueue(new Callback<EPass>() {
            @Override
            public void onResponse(Call<EPass> call, Response<EPass> response) {
                dialog.dismiss();
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        setData(response.body());
                    }
                } else {
                    Toast.makeText(PermitDetailActivity.this, "No data found for the given Id", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<EPass> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(PermitDetailActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData(EPass data) {
        // Header
        tvAuthorityOfficeName.setText(String.format(Locale.ENGLISH,"OFFICE OF THE %s", data.getAuthorityDetail().getAuthorityDesignation()));
        tvAuthorityDesignationAbbr.setText(data.getAuthorityDetail().getAuthorityDesignationAbbr());
        tvAuthorityOfficeAddress.setText(data.getAuthorityDetail().getAuthorityAddress());

        if(data.getStatus() == ApproveStatus.APPROVED) {
            ivApprove.setVisibility(View.VISIBLE);

            Calendar expiry = data.getDateOfJourney().getDate().getDate();
            expiry.set(Calendar.HOUR, data.getDateOfJourney().getTime().getHour());
            expiry.set(Calendar.MINUTE, data.getDateOfJourney().getTime().getMinute());
            expiry.add(Calendar.HOUR, 8);
            if(Calendar.getInstance().after(expiry)){
                Picasso.get().load(R.drawable.expired).into(ivApprove);
            } else {
                Picasso.get().load(R.drawable.approved).into(ivApprove);
            }
        } else {
            ivApprove.setVisibility(View.GONE);
        }

        tvPermitId.setText(String.format(Locale.ENGLISH,"%s%d", data.getIdPrefix(), data.getId()));
        String approvalDate = "NOT APPROVED";
        if(data.getPermitApprovalDate()!= null) {
            approvalDate = "Dated "+data.getCity().getName()+", "+DateHelper.formatDate(data.getPermitApprovalDate().getDate().getTime());
        }
        tvPermitApprovalDate.setText(approvalDate);
        tvPermissionDetail.setText(data.getPermissionDetail());
        String doj = DateHelper.formatDate(data.getDateOfJourney()) +" "+ DateHelper.formatTime(data.getDateOfJourney());
        tvDoj.setText(doj);
        tvVehicleRc.setText(data.getVehicleRcNumber());
        tvDriverName.setText(data.getDriverName());
        tvDriverContact.setText(String.valueOf(data.getDriverContact()));
        tvRoute.setText(data.getRouteOfJourney());
        tvCity.setText(data.getCity().getName());

        if(URLUtil.isValidUrl(data.getQrCodeUrl())) {
            Picasso.get().load(data.getQrCodeUrl()).into(ivQrCode);
        }

        if(URLUtil.isValidUrl(data.getAuthorityDetail().getAuthoritySign())) {
            Picasso.get().load(data.getAuthorityDetail().getAuthoritySign()).into(ivAuthSign);
        }

        tvAuthName.setText(data.getAuthorityDetail().getAuthorityName());
        tvAuthDesignation.setText(data.getAuthorityDetail().getAuthorityDesignation());
        tvAuthAddress.setText(data.getAuthorityDetail().getAuthorityAddress());

        travellers.clear();
        travellers.addAll(data.getTravellers());
        travellerAdapter.notifyDataSetChanged();

        terms.clear();
        terms.addAll(data.getTermsAndConditions());
        termsAdapter.notifyDataSetChanged();

        files.clear();
        files.addAll(data.getDocumentDetail().getOtherSupportingDocuments());
        if(files.size()>0) {
            cvSupportingDoc.setVisibility(View.VISIBLE);
            fileAdapter.notifyDataSetChanged();
        } else {
            cvSupportingDoc.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDeleteClicked(Traveller traveller, int pos) {

    }

    @Override
    public void onFileDeleteClicked(File file, int pos) {

    }

    @Override
    public void onFileSelected(File file, int pos) {
        showImageViewActivity(file.getUrl());
    }

    private void showImageViewActivity(String url) {
        Intent intent = new Intent(this, ImageViewerActivity.class);

        intent.putExtra("KEY_IMAGE_URL", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, ivQrCode,
                "image");

        startActivity(intent, options.toBundle());
    }
}
