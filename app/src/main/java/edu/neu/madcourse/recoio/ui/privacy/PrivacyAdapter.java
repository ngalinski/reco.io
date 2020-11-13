package edu.neu.madcourse.recoio.ui.privacy;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.recoio.R;

public class PrivacyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<PrivacyModel> mStoreMyModelArrayList;


    public PrivacyAdapter(Activity activity, ArrayList<PrivacyModel> storeMyModelArrayList) {
        this.mContext = activity;
        this.mStoreMyModelArrayList = storeMyModelArrayList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.privacy_item,
                parent, false);
        return new MyCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PrivacyModel item = getValueAt(position);
        PrivacyAdapter.MyCartViewHolder myCartViewHolder = (PrivacyAdapter.MyCartViewHolder)holder;
        if (item != null) {
            setupValuesInWidgets(myCartViewHolder, item);
        }
    }


    private PrivacyModel getValueAt(int position) {
        return mStoreMyModelArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return mStoreMyModelArrayList.size();
    }

    private void setupValuesInWidgets(PrivacyAdapter.MyCartViewHolder itemHolder, PrivacyModel
            cartMyModel) {
        if (cartMyModel != null) {
            itemHolder.title.setText(Html.fromHtml(cartMyModel.getTitle()));
        }
    }


    public class MyCartViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView title;

        public MyCartViewHolder(View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.title);

        }
    }
}