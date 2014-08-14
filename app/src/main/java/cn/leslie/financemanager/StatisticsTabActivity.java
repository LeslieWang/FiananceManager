package cn.leslie.financemanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;

public class StatisticsTabActivity extends Activity implements ActionBar.TabListener {
    private static final int[] TabNames = new int[] {R.string.current_week,
            R.string.current_month, R.string.current_year, R.string.statistics_all};

    public static void show(Activity activity) {
        activity.startActivity(new Intent(activity, StatisticsTabActivity.class));
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_tab);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        actionBar.setDisplayHomeAsUpEnabled(true);

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return StatisticsFragment.newInstance(TabNames[position]);
        }

        @Override
        public int getCount() {
            return TabNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(TabNames[position]);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StatisticsFragment extends Fragment {
        private static final String ARG_NAME_RES_ID = "name_res_id";
        private List<Record> mRecords;

        /**
         * Returns a new instance of this fragment for the given section name resource id.
         */
        public static StatisticsFragment newInstance(int nameResId) {
            StatisticsFragment fragment = new StatisticsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_NAME_RES_ID, nameResId);
            fragment.setArguments(args);
            return fragment;
        }

        public StatisticsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            loadData();
            View rootView = inflater.inflate(R.layout.fragment_statistics_tab, container, false);
            PieChart pieChart = (PieChart) rootView.findViewById(R.id.pie_chart);
            Map<String, Float> data = Utility.calculateAmountByCategory(mRecords);
            pieChart.setDrawHoleEnabled(false);
            pieChart.setDrawXValues(true);
            pieChart.setDrawYValues(true);
            pieChart.setDrawLegend(false);
            pieChart.setDescription(getString(R.string.statistics_by_category));
            pieChart.setData(new ArrayList<String>(data.keySet()),
                    new ArrayList<Float>(data.values()));
            pieChart.setValuePaintColor(Color.GRAY);
            pieChart.setCenterText(getString(R.string.statistics_in_total,
                    (int) pieChart.getYValueSum()));
            pieChart.prepare();

            return rootView;
        }

        private void loadData() {
            int id = getArguments().getInt(ARG_NAME_RES_ID);
            switch (id) {
                case R.string.current_week:
                    mRecords = DataManager.getInstance().getRecordsOfCurrentWeek();
                    return;
                case R.string.current_month:
                    mRecords = DataManager.getInstance().getRecordsOfCurrentMonth();
                    return;
                case R.string.current_year:
                    mRecords = DataManager.getInstance().getRecordsOfCurrentYear();
                    return;
                case R.string.statistics_all:
                    mRecords = DataManager.getInstance().getRecords();
                    return;
                default:
                    // TODO:
            }
        }
    }

}