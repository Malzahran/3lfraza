package com.alfraza.app.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.activities.misc.ActivityMessages;
import com.alfraza.app.adapter.MessageAdapter;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.listener.EndlessRecyclerViewScrollListener;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.models.MessagesData;
import com.alfraza.app.models.MiscData;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class MessagesFragment extends Fragment implements View.OnClickListener {
    private Session session;
    private RecyclerView recycler;
    private Call<ServerResponse> response;
    private TextView actv_title;
    private Snackbar snackbar;
    private LinearLayout loading_overlay, warning_overlay;
    private MessageAdapter adapter;
    private ArrayList<MessagesData> data;
    private String searchq, msgtitle;
    private EditText et_msgtext;
    private MenuItem acProgress;
    private MenuItem acSearch;
    private int pushInterval = 5;
    private boolean firststart = true, msgsent = false;
    private int convid = 0, contype = 0, getpage = 1, lastmsg = 0, firstmsg = 0;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            pushInterval = session.pref().Pushinterval();
            CheckNewMessages();
            handler.postDelayed(this, pushInterval * 1000); // for interval...
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, pushInterval * 1000);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getActivity() instanceof ActivityMessages) {
            ((ActivityMessages) getActivity()).setFragment(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        session = new Session(getActivity());
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.getInt("convid") != 0) {
                convid = getArguments().getInt("convid");
                contype = getArguments().getInt("contype", 0);
                msgtitle = getArguments().getString("contit");
            }
        }
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment, menu);
        acSearch = menu.findItem(R.id.menu_search_f);
        acProgress = menu.findItem(R.id.action_progress);
        acSearch.setVisible(false);
        loaddata();
        try {
            acSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {

                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    data.clear();
                    searchq = null;
                    getpage = 1;
                    lastmsg = 0;
                    firstmsg = 0;
                    loaddata();
                    return true;
                }
            });

            final SearchView searchView = (SearchView) acSearch.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    searchq = s;
                    data.clear();
                    lastmsg = 0;
                    firstmsg = 0;
                    getpage = 1;
                    loaddata();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home_f) {
            session.goHome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        data = new ArrayList<>();
        LinearLayoutManager linerLayout = new LinearLayoutManager(getActivity());
        actv_title.setText(msgtitle);
        linerLayout.setStackFromEnd(true);
        linerLayout.setReverseLayout(true);
        adapter = new MessageAdapter(getActivity(), data, 2);
        recycler.setLayoutManager(linerLayout);
        recycler.setAdapter(adapter);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linerLayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (!firststart && data.size() >= 20) {
                    getpage++;
                    loaddata();
                }
            }
        };
        recycler.addOnScrollListener(scrollListener);
    }

    private void initViews(View view) {
        loading_overlay = this.getActivity().findViewById(R.id.loading_overlay);
        warning_overlay = this.getActivity().findViewById(R.id.warning_overlay);
        ImageView btn_send_msg = view.findViewById(R.id.btn_send_msg);
        et_msgtext = view.findViewById(R.id.et_msgtext);
        actv_title = view.findViewById(R.id.actv_title);
        btn_send_msg.setOnClickListener(this);
        recycler = view.findViewById(R.id.card_recycler_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_msg:
                SendMessage();
                break;
        }
    }


    private void SendMessage() {
        String msgtext = et_msgtext.getText().toString();
        if (!msgtext.isEmpty()) {
            session.hideKeyboard(getActivity().getCurrentFocus());
            SendMessageProcess(msgtext, convid);
        } else {
            showSnackBar(getString(R.string.error_msg_empty), 0, 1);
        }
    }

    public void hideSnackBar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    private void SendMessageProcess(final String msgtext, int userid) {
        try {
            acProgress.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MiscData misc = new MiscData();
        misc.setUserid(userid);
        misc.setMessageText(msgtext);
        misc.setType(String.valueOf(contype));
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent("messages");
        request.setSubType("sendmsg");
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
                                    acProgress.setVisible(false);
                                    et_msgtext.setText(null);
                                    msgsent = true;
                                    handler.removeCallbacks(runnable);
                                    CheckNewMessages();
                                    handler.postDelayed(runnable, pushInterval * 1000);
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
                                    acProgress.setVisible(false);
                                    String error = getString(R.string.error_msg_not_sent);
                                    if (resp.getMessage() != null) {
                                        error = resp.getMessage();
                                    }
                                    showSnackBar(error, 2, 2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        acProgress.setVisible(false);
                        String error = getString(R.string.internetcheck);
                        showSnackBar(error, 0, 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    acProgress.setVisible(false);
                    String error = getString(R.string.internetcheck);
                    showSnackBar(error, 2, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loaddata() {
        try {
            acProgress.setVisible(true);
            if (firststart) {
                warning_overlay.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MiscData misc = new MiscData();
        misc.setSearchq(searchq);
        misc.setPage(getpage);
        misc.setItem_id(convid);
        misc.setType(String.valueOf(contype));
        misc.setFirstid(firstmsg);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent("messages");
        request.setSubType("getsingle");
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
                                    acProgress.setVisible(false);
                                    if (firststart) {
                                        data.clear();
                                        lastmsg = resp.getLastid();
                                    }
                                    if (resp.getMessages().length > 0) {
                                        firstmsg = resp.getFirstid();
                                    }
                                    data.addAll((Arrays.asList(resp.getMessages())));
                                    adapter.notifyDataSetChanged();
                                    if (firststart) {
                                        if (resp.getLayoutInfo() != null && resp.getLayoutInfo().allowSearch() != 0) {
                                            acSearch.setVisible(true);
                                        }
                                        loading_overlay.setVisibility(View.GONE);
                                        recycler.scrollToPosition(0);
                                        firststart = false;
                                    }
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
                                    acProgress.setVisible(false);
                                    String error = getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) {
                                        error = resp.getMessage();
                                    }
                                    if (firststart) {
                                        loading_overlay.setVisibility(View.GONE);
                                        warning_overlay.setVisibility(View.VISIBLE);
                                    }
                                    showSnackBar(error, 1, 2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        acProgress.setVisible(false);
                        String error = getString(R.string.error_failed_later);
                        showSnackBar(error, 0, 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    acProgress.setVisible(false);
                    String error = getString(R.string.internetcheck);
                    if (firststart) {
                        loading_overlay.setVisibility(View.GONE);
                        warning_overlay.setVisibility(View.VISIBLE);
                    }
                    showSnackBar(error, 1, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void CheckNewMessages() {
        MiscData misc = new MiscData();
        misc.setSearchq(searchq);
        misc.setPage(getpage);
        misc.setItem_id(convid);
        misc.setType(String.valueOf(contype));
        misc.setLastid(lastmsg);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent("messages");
        request.setSubType("getsingle");
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            try {
                                if (resp.getMessages().length > 0) {
                                    data.addAll(0, (Arrays.asList(resp.getMessages())));
                                    adapter.notifyDataSetChanged();
                                    if (!msgsent) {
                                        session.showToast(getString(R.string.new_message_alert), 0);
                                    } else {
                                        session.showToast(getString(R.string.success_message_sent), 0);
                                        recycler.scrollToPosition(0);
                                        msgsent = false;
                                    }
                                    lastmsg = resp.getLastid();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (resp.getResult().equals(Constants.LOGOUT)) {
                            try {
                                session.Logout();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void showSnackBar(String msg, final int action, int length) {
        int SnLength = Snackbar.LENGTH_SHORT;
        switch (length) {
            case 1:
                SnLength = Snackbar.LENGTH_LONG;
                break;
            case 2:
                SnLength = Snackbar.LENGTH_INDEFINITE;
                break;
        }
        CoordinatorLayout main_layout = getActivity().findViewById(R.id.main_layout);
        snackbar = Snackbar.make(main_layout, msg, SnLength);
        if (action != 0) {
            snackbar.setAction(getString(R.string.reloadbtn), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (action == 1) {
                        loaddata();
                    } else {
                        SendMessage();
                    }
                }
            });

            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }
}