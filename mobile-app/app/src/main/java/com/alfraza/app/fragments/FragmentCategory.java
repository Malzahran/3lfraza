package com.alfraza.app.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alfraza.app.ActivityCategoryDetails;
import com.alfraza.app.ActivityStore;
import com.alfraza.app.R;
import com.alfraza.app.adapter.AdapterCategory;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.models.Category;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.helpers.utilities.NetworkCheck;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class FragmentCategory extends Fragment {

    private View root_view;
    private RecyclerView recyclerView;
    private Session session;
    private int catid;
    private Call<ServerResponse> response;
    private AdapterCategory adapter;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        session = new Session(getActivity());
        root_view = inflater.inflate(R.layout.fragment_category, null);
        Bundle arguments = getArguments();
        catid = 8000;
        if (arguments != null)
            if (arguments.getInt("cid", 0) != 0) catid = arguments.getInt("cid", 0);
        initComponent();
        requestListCategory();

        return root_view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (response != null && response.isExecuted()) response.cancel();
    }

    private void initComponent() {
        recyclerView = root_view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        //set data and list adapter
        adapter = new AdapterCategory(getActivity(), new ArrayList<Category>());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setVisibility(View.GONE);

        adapter.setOnItemClickListener(new AdapterCategory.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Category obj) {
                Fragment fragment = null;
                if (getActivity() instanceof ActivityStore) {
                    if (obj.hassub() == 0)
                        ActivityCategoryDetails.navigate(getActivity(), obj);
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putInt("cid", obj.getId());
                        fragment = new FragmentCategory();
                        //set Fragment Arguments
                        fragment.setArguments(bundle);
                    }
                }
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_content_category, fragment);
                    ft.addToBackStack("category");
                    ft.commitAllowingStateLoss();
                }
            }
        });
    }


    private void requestListCategory() {
        MiscData misc = new MiscData();
        misc.setCatid(catid);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.STORE_TYPE);
        request.setSubType(Constants.CATS_TYPE);
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    adapter.setItems(resp.getCats());
                                    ActivityStore.getInstance().category_load = true;
                                    ActivityStore.getInstance().showDataLoaded();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    session.Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    onFailRequest();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        onFailRequest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    Log.e("onFailure", t.getMessage());
                    if (!call.isCanceled()) onFailRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void onFailRequest() {
        if (NetworkCheck.isConnect(getActivity())) {
            showFailedView(R.string.msg_failed_load_data);
        } else {
            showFailedView(R.string.no_internet_text);
        }
    }

    private void showFailedView(@StringRes int message) {
        ActivityStore.getInstance().showDialogFailed(message);
    }

}
