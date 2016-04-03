package com.pabhinav.zovido.util;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.pabhinav.zovido.BuildConfig;
import com.pabhinav.zovido.application.ZovidoApplication;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author pabhinav
 */
public class CategoryFirebaseListener {

    public void createCategoryFirebaseListeners(){
        Firebase refProduct = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + "product");
        Firebase refPurpose = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + "purpose");
        Firebase refSport = new Firebase(BuildConfig.FIREBASE_DASHBOARD_LINK + "sport");

        if(refProduct == null
                || refPurpose == null
                || refSport == null){
            return;
        }

        refProduct.addValueEventListener(new FirebaseValueEventListener(CategoryTypes.Product));
        refPurpose.addValueEventListener(new FirebaseValueEventListener(CategoryTypes.Purpose));
        refSport.addValueEventListener(new FirebaseValueEventListener(CategoryTypes.Sport));
    }

    /** Value event listener for each category type **/
    class FirebaseValueEventListener implements ValueEventListener{

        private CategoryTypes categoryTypes;

        public FirebaseValueEventListener(CategoryTypes categoryTypes){
            this.categoryTypes = categoryTypes;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if(ZovidoApplication.getInstance() == null){
                return;
            }

            ArrayList<String> arrayList = new ArrayList<>();
            HashMap<String, String> stringStringHashMap = (HashMap < String, String >)dataSnapshot.getValue();
            for(HashMap.Entry<String,String> entry : stringStringHashMap.entrySet()){
                arrayList.add(entry.getValue());
            }

            /** Get the default value at top of the list **/
            int index = -1;
            switch (categoryTypes){
                case Product:
                    index = arrayList.indexOf("Select Product related in Call");
                    break;
                case Purpose:
                    index = arrayList.indexOf("Select Purpose for Call");
                    break;
                case Sport :
                    index = arrayList.indexOf("Select Sport related in Call");
                    break;
            }
            if(index != -1){
                String value = arrayList.get(index);
                arrayList.remove(index);
                arrayList.add(0, value);
            }

            /** Save the category list **/
            switch (categoryTypes){
                case Product:
                    ZovidoApplication.getInstance().setProductList(arrayList);
                    break;
                case Purpose:
                    ZovidoApplication.getInstance().setPurposeList(arrayList);
                    break;
                case Sport :
                    ZovidoApplication.getInstance().setSportList(arrayList);
                    break;
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    enum CategoryTypes{
        Product, Purpose, Sport
    }
}
