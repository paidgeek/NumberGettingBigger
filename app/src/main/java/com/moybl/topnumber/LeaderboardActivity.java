package com.moybl.topnumber;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LeaderboardActivity extends AppCompatActivity {

	private static LeaderboardActivity sInstance;

	public static LeaderboardActivity getInstance() {
		return sInstance;
	}

	@BindView(R.id.leaderboard_toolbar)
	Toolbar mToolbar;
	@BindView(R.id.leaderboard_tabs)
	TabLayout mTabLayout;
	@BindView(R.id.leaderboard_viewpager)
	ViewPager mViewPager;
	private boolean mSwitched;
	@BindView(R.id.loading_indicator)
	View mLoadingIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);
		ButterKnife.bind(this);

		sInstance = this;

		setSupportActionBar(mToolbar);
		ActionBar actionBar = getSupportActionBar();

		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		setupViewPager(mViewPager);
		mTabLayout.setupWithViewPager(mViewPager);
	}

	public View getLoadingIndicator() {
		return mLoadingIndicator;
	}

	@OnClick(R.id.btn_invite_friends)
	void onInviteFriendsClick() {
		String appLinkUrl, previewImageUrl;

		appLinkUrl = "https://www.mydomain.com/myapplink";
		previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";

		if (AppInviteDialog.canShow()) {
			AppInviteContent content = new AppInviteContent.Builder()
					.setApplinkUrl(appLinkUrl)
					.setPreviewImageUrl(previewImageUrl)
					.build();
			AppInviteDialog.show(this, content);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		AlarmController.cancelNotificationSetup(getApplicationContext());
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (!mSwitched) {
			AlarmController.scheduleNotificationSetup(getApplicationContext());
		}

		mSwitched = false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		mSwitched = true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				mSwitched = true;
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new FriendsLeaderboardFragment(), getString(R.string.friends));
		adapter.addFragment(new GlobalLeaderboardFragment(), getString(R.string.global));
		viewPager.setAdapter(adapter);
	}

	class ViewPagerAdapter extends FragmentPagerAdapter {

		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}

	}

}
