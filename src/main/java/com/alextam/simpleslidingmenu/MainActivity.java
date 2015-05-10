package com.alextam.simpleslidingmenu;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {
    private LinearLayout linearLayout;
    private ListView listView;
    private SlidingMenu drawerLayout;
    private String[] mPlanetTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (SlidingMenu)findViewById(R.id.drawer_layout);
        linearLayout = (LinearLayout)findViewById(R.id.ly_main_a);

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        listView = (ListView)findViewById(R.id.listview_main);
        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item,mPlanetTitles));
        listView.setOnItemClickListener(new DrawerItemClickListener());

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            listView.setSelection(position);
            selectItem(position);
        }
    }


    private void selectItem(int position)
    {
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        listView.setItemChecked(position, true);
        //折合侧滑菜单
        drawerLayout.closeMenu();
    }


    public static class PlanetFragment extends Fragment
    {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment()
        {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
    }

}
