package com.uwo.crystalli.jukebox;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ConnectionActivityFragment extends Fragment {

    private final String LOG_TAG = ConnectionActivity.class.getSimpleName();
    private ArrayAdapter<String> mHostActionsAdapter;
    private ArrayAdapter<String> mAvailableHostsAdapter;

    public ConnectionActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_connection, container, false);

        //TODO: probably shouldn't use ListView for this, but I like the UI for listView items
        List<String> hostActions = Arrays.asList(getString(R.string.set_as_host));

        mHostActionsAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_host_action,
                R.id.list_item_host_action_textview,
                hostActions);

        ListView listViewHostActions = (ListView) rootView.findViewById(R.id.list_host_actions);
        listViewHostActions.setAdapter(mHostActionsAdapter);

        listViewHostActions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: set as host activity
                Toast toast = Toast.makeText(getContext(), "set as host", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //TODO: make this not hard coded
        List<String> availableConnections = Arrays.asList("Crystal", "Dan", "Sabrina");
        mAvailableHostsAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_available_host,
                R.id.list_item_available_host_textview,
                availableConnections);

        ListView listViewAvailableHosts = (ListView) rootView.findViewById(R.id.list_available_hosts);
        listViewAvailableHosts.setAdapter(mAvailableHostsAdapter);

        listViewAvailableHosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Connect to host
                Toast toast = Toast.makeText(getContext(), "connect to host", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return rootView;
    }
}
