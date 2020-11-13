package edu.neu.madcourse.recoio.ui.privacy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.recoio.R;

public class PrivacyPolicy extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PrivacyAdapter myAdapter;
    private ArrayList<PrivacyModel> myModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.privacyPolicy);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myModelArrayList.add(new PrivacyModel("<b>Reco.IO Privacy Policy</b>"));

        myModelArrayList.add(new PrivacyModel("This privacy policy (\"policy\") will help you " +
                "understand how Reco.IO (\"us\", \"we\", \"our\") uses and protects the data " +
                "you provide to us when you visit and use Reco.IO (\"website\", \"service\")."));

        myModelArrayList.add(new PrivacyModel("We reserve the right to change this policy at " +
                "any given time, of which you will be promptly updated. If you want to make sure " +
                "that you are up to date with the latest changes, we advise you to frequently visit this page."));

        myModelArrayList.add(new PrivacyModel("<b>What User Data We Collect</b>"));

        myModelArrayList.add(new PrivacyModel("When you visit the website, we may collect the following data:\n" +
                "-Your IP address.\n" +
                "-Your contact information and email address.\n" +
                "Other information such as interests and preferences.\n" +
                "Data profile regarding your online behavior on our website."));

        myModelArrayList.add(new PrivacyModel("<b>Why We Collect Your Data</b>"));

        myModelArrayList.add(new PrivacyModel("We are collecting your data for several reasons:\n" +
                "-To better understand your needs.\n" +
                "-To improve our services and products.\n" +
                "-To send you promotional emails containing the information we think you will find interesting.\n" +
                "-To contact you to fill out surveys and participate in other types of market research.\n" +
                "-To customize our website according to your online behavior and personal preferences."));

        myModelArrayList.add(new PrivacyModel("<b>Safeguarding and Securing the Data</b>"));

        myModelArrayList.add(new PrivacyModel("Reco.IO is committed to securing your data and " +
                "keeping it confidential. Reco.IO has done all in its power to prevent data theft, " +
                "unauthorized access, and disclosure by implementing the latest technologies and " +
                "software, which help us safeguard all the information we collect online."));

        myModelArrayList.add(new PrivacyModel("<b>Restricting the Collection of your Personal Data</b>"));

        myModelArrayList.add(new PrivacyModel("When you are filling the forms on the website, " +
                "make sure to check if there is a box which you can leave unchecked, if you don't " +
                "want to disclose your personal information."));

        myModelArrayList.add(new PrivacyModel("If you have already agreed to share your " +
                "information with us, feel free to contact us via email and we will be more than" +
                " happy to change this for you."));

        myModelArrayList.add(new PrivacyModel("Reco.IO will not lease, sell or distribute " +
                "your personal information to any third parties, unless we have your permission. " +
                "We might do so if the law forces us. Your personal information will be used when" +
                " we need to send you promotional materials if you agree to this privacy policy."));

        myModelArrayList.add(new PrivacyModel("<b>Contact us</b>"));

        myModelArrayList.add(new PrivacyModel("To contact us or request a copy of this policy," +
                " reach out to Northeastern University and reference Reco.IO in regards to CS5520 - Fall 2020."));

        myAdapter = new PrivacyAdapter(this, myModelArrayList);
        recyclerView.setAdapter(myAdapter);
    }
}