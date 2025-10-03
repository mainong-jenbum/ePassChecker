package com.jenbumapps.e_passchecker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jenbumapps.core.model.EPass;
import com.jenbumapps.core.model.time.DateHelper;
import com.jenbumapps.e_passchecker.R;
import com.jenbumapps.e_passchecker.app.App;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class PermitAdapter extends RecyclerView.Adapter<PermitAdapter.AdapterVh> {

    public interface Listener{
        void onPermitSelected(EPass formData, int pos);
    }

    private List<EPass> permits;
    private Listener mListener;

    public PermitAdapter(List<EPass> permits, Listener mListener) {
        this.permits = permits;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public AdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_permit_item, parent, false);

        final AdapterVh vh = new AdapterVh(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPermitSelected(permits.get(vh.getAdapterPosition()), vh.getAdapterPosition());
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterVh holder, int position) {
        if(URLUtil.isValidUrl(permits.get(position).getQrCodeUrl()))
            Picasso.get().load(permits.get(position).getQrCodeUrl()).into(holder.ivQrCode);

        holder.tvRoute.setText(permits.get(position).getRouteOfJourney());
        String doj = DateHelper.formatDate(permits.get(position).getDateOfJourney()) + " " +DateHelper.formatTime(permits.get(position).getDateOfJourney());

        holder.tvDate.setText(doj);

        holder.tvRefId.setText(String.format(Locale.ENGLISH,"%s%d", permits.get(position).getIdPrefix(), permits.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return permits.size();
    }

    class AdapterVh extends RecyclerView.ViewHolder {

        ImageView ivQrCode;
        TextView tvRoute;
        TextView tvDate;
        TextView tvRefId;

        public AdapterVh(@NonNull View view) {
            super(view);

            ivQrCode = view.findViewById(R.id.iv_qr_code);
            tvRoute = view.findViewById(R.id.tv_route);
            tvDate = view.findViewById(R.id.tv_date);
            tvRefId = view.findViewById(R.id.tv_ref_no);
        }
    }
}
