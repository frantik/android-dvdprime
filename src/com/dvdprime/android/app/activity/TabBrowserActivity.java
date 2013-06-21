/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dvdprime.android.app.activity;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;
import com.dvdprime.android.app.constants.IntentKeys;
import com.dvdprime.android.app.constants.PreferenceKeys;
import com.dvdprime.android.app.constants.RequestCode;
import com.dvdprime.android.app.db.DpDB;
import com.dvdprime.android.app.dialog.DialogBuilder;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;
import com.dvdprime.android.app.util.StringUtil;

import android.app.ExpandableListActivity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

public class TabBrowserActivity extends ExpandableListActivity implements View.OnCreateContextMenuListener
{
    private String mCurrentBbsId;
    private String mCurrentBbsTitle;
    private String mCurrentGroupId;
    private String mCurrentGroupTitle;

    private BbsListAdapter mAdapter;
    private boolean mAdapterSent;
    private int mTabId = -1;
    private int mLastListPosCourse = -1;
    private int mLastListPosFine = -1;
    
    private static final int MENU_DP_SCRAP		= 1;
    private static final int MENU_DP_DOCUMENT	= 2;
    private static final int MENU_DP_COMMENT	= 3;
    private static final int MENU_DP_MEMO		= 4;
    private static final int MENU_DP_SETTING	= 5;

	/** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (icicle != null) {
        	mCurrentBbsId = icicle.getString("selectedbbs");
        	mCurrentBbsTitle = icicle.getString("selectedbbstitle");
        	mCurrentGroupId = icicle.getString("selectedgroup");
        	mCurrentGroupTitle = icicle.getString("selectedgrouptitle");
        }
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (extras != null) {
			mTabId = extras.getInt(IntentKeys.TAB_ID);
		}
		
        setContentView(R.layout.bbs_activity_expanding);
        DpUtil.updateButtonBar(this, mTabId);
        ExpandableListView lv = getExpandableListView();
        lv.setOnCreateContextMenuListener(this);
        lv.setTextFilterEnabled(true);

        mAdapter = (BbsListAdapter) getLastNonConfigurationInstance();
        if (mAdapter == null) {
            mAdapter = new BbsListAdapter(
                    getApplication(),
                    this,
                    null, // cursor
                    R.layout.bbs_list_item_group,
                    new String[] {},
                    new int[] {},
                    R.layout.bbs_list_item_child,
                    new String[] {},
                    new int[] {});
            setListAdapter(mAdapter);
            getCommunityCursor(mAdapter.getQueryHandler(), null);
        } else {
            mAdapter.setActivity(this);
            setListAdapter(mAdapter);
            mBbsCursor = mAdapter.getCursor();
            if (mBbsCursor != null) {
                init(mBbsCursor);
            } else {
            	getCommunityCursor(mAdapter.getQueryHandler(), null);
            }
        }

        // 메인 배경에 테마 적용
        if (StringUtil.equals(PrefUtil.getInstance().getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
        	findViewById(R.id.bbs_activity_expanding_layout).setBackgroundColor(Color.BLACK);
        } else {
        	findViewById(R.id.bbs_activity_expanding_layout).setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        mAdapterSent = true;
        return mAdapter;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outcicle) {
        // need to store the selected item so we don't lose it in case
        // of an orientation switch. Otherwise we could lose it while
        // in the middle of specifying a playlist to add the item to.
        outcicle.putString("selectedbbs", mCurrentBbsId);
        outcicle.putString("selectedbbstitle", mCurrentBbsTitle);
        outcicle.putString("selectedgroup", mCurrentGroupId);
        outcicle.putString("selectedgrouptitle", mCurrentGroupTitle);
        super.onSaveInstanceState(outcicle);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mCommunityListListener.onReceive(null, null);
    }

    @Override
    public void onDestroy() {
        ExpandableListView lv = getExpandableListView();
        if (lv != null) {
            mLastListPosCourse = lv.getFirstVisiblePosition();
            View cv = lv.getChildAt(0);
            if (cv != null) {
                mLastListPosFine = cv.getTop();
            }
        }
        
        if (!mAdapterSent) {
            Cursor c = mAdapter.getCursor();
            if (c != null) {
                c.close();
            }
        }
        // Because we pass the adapter to the next activity, we need to make
        // sure it doesn't keep a reference to this activity. We can do this
        // by clearing its DatasetObservers, which setListAdapter(null) does.
        setListAdapter(null);
        mAdapter = null;
        setListAdapter(null);
        
        // 목록 바로 가기 활성화시 카운트 초기화
        if (PrefUtil.getInstance().getBoolean(PreferenceKeys.DIRECT_BBS_ENABLED, false))
        	PrefUtil.getInstance().setInt(PreferenceKeys.REQUEST_AD_COUNT, 1);
        super.onDestroy();
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
    
    @Override
    public void onBackPressed() {
    	if (PrefUtil.getInstance().getBoolean(PreferenceKeys.DIRECT_BBS_ENABLED, false)) {
			DialogInterface.OnClickListener positivListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					dialog.dismiss();
					finish();
//					System.exit(0);
				}
			};
			DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					dialog.dismiss();
				}
			};
			if (PrefUtil.getInstance().getBoolean(PreferenceKeys.CLOSE_DIALOG_ENABLED, true)) {
				DialogBuilder.createConfirmDialog(this, 
											getString(R.string.show_close_title),
											getString(R.string.show_warning_close),
											R.string.button_ok,
											R.string.button_cancel,
											positivListener,
											negativeListener).show();
			} else {
				super.onBackPressed();
//				System.exit(0);
			}
		} else {
			super.onBackPressed();
	    }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(Menu.NONE, MENU_DP_SCRAP, Menu.NONE, R.string.list_scrap).setIcon(R.drawable.ic_menu_scrap);
		menu.add(Menu.NONE, MENU_DP_DOCUMENT, Menu.NONE, R.string.list_document).setIcon(R.drawable.ic_menu_document);
		menu.add(Menu.NONE, MENU_DP_COMMENT, Menu.NONE, R.string.list_comment).setIcon(R.drawable.ic_menu_comment);
		menu.add(Menu.NONE, MENU_DP_MEMO, Menu.NONE, R.string.menu_view_memo).setIcon(android.R.drawable.ic_menu_agenda);
		menu.add(Menu.NONE, MENU_DP_SETTING, Menu.NONE, R.string.menu_setting).setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		Intent intent;
		switch (item.getItemId()) {
			case MENU_DP_SCRAP:
				intent = new Intent(this, ScrapListActivity.class);
				startActivity(intent);
				break;
			case MENU_DP_DOCUMENT:
				intent = new Intent(this, DocumentListActivity.class);
				startActivity(intent);
				break;
			case MENU_DP_COMMENT:
				intent = new Intent(this, CommentListActivity.class);
				startActivity(intent);
				break;
			case MENU_DP_MEMO:
				intent = new Intent(this, MemoTabActivity.class);
				startActivity(intent);
				break;
			case MENU_DP_SETTING:
				intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
		}
		
		return false;
	}

//    private BroadcastReceiver mCommunityListListener = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            getExpandableListView().invalidateViews();
//        }
//    };

    public void init(Cursor c) {

        if (mAdapter == null) {
            return;
        }
        mAdapter.changeCursor(c); // also sets mBbsCursor

        if (mBbsCursor == null) {
            closeContextMenu();
            return;
        }

        // restore previous position
        if (mLastListPosCourse >= 0) {
            ExpandableListView elv = getExpandableListView();
            elv.setSelectionFromTop(mLastListPosCourse, mLastListPosFine);
            mLastListPosCourse = -1;
        }

        DpUtil.updateButtonBar(this, mTabId);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

    	Intent intent;
        Cursor c = (Cursor) getExpandableListAdapter().getChild(groupPosition, childPosition);
        
    	intent = new Intent(TabBrowserActivity.this, ArticleListActivity.class);
        intent.putExtra(IntentKeys.BBS_TITLE, c.getString(c.getColumnIndex(DpDB.Bbs.TITLE)));
        intent.putExtra(IntentKeys.BBS_MAJOR, c.getString(c.getColumnIndex(DpDB.Bbs.MAJOR)));
        intent.putExtra(IntentKeys.BBS_MINOR, c.getString(c.getColumnIndex(DpDB.Bbs.MINOR)));
        intent.putExtra(IntentKeys.BBS_MASTER, c.getString(c.getColumnIndex(DpDB.Bbs.MASTER_ID)));
        intent.putExtra(IntentKeys.BBS_URL, c.getString(c.getColumnIndex(DpDB.Bbs.TARGET_URL)));
        intent.putExtra(IntentKeys.IS_LOGIN_CHECK, c.getInt(c.getColumnIndex(DpDB.Bbs.LOGIN_CHECK)));
        
        startActivity(intent);
        return true;
    }
    
    private Cursor getCommunityCursor(AsyncQueryHandler async, String filter) {

    	String catId = "0";
        Cursor ret = null;
        switch (mTabId) {
	        case R.id.communitytab:
	        	catId = "0";
	        	break;
	        case R.id.hardwaretab:
	        	catId = "1";
	        	break;
	        case R.id.softwaretab:
	        	catId = "2";
	        	break;
	        case R.id.bluraytab:
	        	catId = "3";
	        	break;
	        case R.id.smartphonetab:
	        	catId = "4";
	        	break;
        }
        if (async != null) {
        	async.startQuery(42, null, Uri.withAppendedPath(DpDB.Bbs.CONTENT_URI, Uri.encode(catId)),
        		  RequestCode.BbsCols, null , null, DpDB.Bbs.DESC_SORT_ORDER);
        } else {
            ret = DpUtil.query(this, Uri.withAppendedPath(DpDB.Bbs.CONTENT_URI, Uri.encode(catId)),
            		RequestCode.BbsCols, null , null, DpDB.Bbs.DESC_SORT_ORDER);
        }
        return ret;
    }
    
    static class BbsListAdapter extends SimpleCursorTreeAdapter {
        
        private final BitmapDrawable mDefaultBbsIcon;
        private int mGroupTitleIdx;
        
        private TabBrowserActivity mActivity;
        private AsyncQueryHandler mQueryHandler;
        private String mConstraint = null;
        private boolean mConstraintIsValid = false;
        
        static class ViewHolder {
            TextView line;
            ImageView icon;
        }

        class QueryHandler extends AsyncQueryHandler {
            QueryHandler(ContentResolver res) {
                super(res);
            }
            
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                mActivity.init(cursor);
            }
        }

        BbsListAdapter(Context context, TabBrowserActivity currentactivity,
                Cursor cursor, int glayout, String[] gfrom, int[] gto, 
                int clayout, String[] cfrom, int[] cto) {
            super(context, cursor, glayout, gfrom, gto, clayout, cfrom, cto);
            mActivity = currentactivity;
            mQueryHandler = new QueryHandler(context.getContentResolver());

            Resources r = context.getResources();
            mDefaultBbsIcon = (BitmapDrawable) r.getDrawable(R.drawable.ic_bbs_list);
            // no filter or dither, it's a lot faster and we can't tell the difference
            mDefaultBbsIcon.setFilterBitmap(false);
            mDefaultBbsIcon.setDither(false);
            
//            mContext = context;
            getColumnIndices(cursor);
        }
        
        private void getColumnIndices(Cursor cursor) {
            if (cursor != null) {
                mGroupTitleIdx = cursor.getColumnIndexOrThrow(DpDB.Bbs.TITLE);
            }
        }
        
        public void setActivity(TabBrowserActivity newactivity) {
            mActivity = newactivity;
        }
        
        public AsyncQueryHandler getQueryHandler() {
            return mQueryHandler;
        }

        @Override
        public View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
            View v = super.newGroupView(context, cursor, isExpanded, parent);
            ImageView iv = (ImageView) v.findViewById(R.id.icon);
            ViewGroup.LayoutParams p = iv.getLayoutParams();
            p.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewHolder vh = new ViewHolder();
            vh.line = (TextView) v.findViewById(R.id.line);
            vh.icon = (ImageView) v.findViewById(R.id.icon);
            vh.icon.setPadding(0, 0, 1, 0);
            v.setTag(vh);

            // 리스트에 테마적용
            if (StringUtil.equals(PrefUtil.getInstance().getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
            	v.setBackgroundResource(R.drawable.theme_black_list_selector);
            	vh.line.setTextAppearance(context, R.style.CommonTextAppearanceListContentsWhite);
            } else {
            	v.setBackgroundResource(R.drawable.theme_white_list_selector);
            	vh.line.setTextAppearance(context, R.style.CommonTextAppearanceListContents);
            }

            return v;
        }

        @Override
        public View newChildView(Context context, Cursor cursor, boolean isLastChild,
                ViewGroup parent) {
            View v = super.newChildView(context, cursor, isLastChild, parent);
            ViewHolder vh = new ViewHolder();
            vh.line = (TextView) v.findViewById(R.id.line);
            vh.line.setPadding(85, 0, 0, 0);
            vh.icon = (ImageView) v.findViewById(R.id.icon);
            vh.icon.setBackgroundDrawable(mDefaultBbsIcon);
            vh.icon.setPadding(0, 0, 1, 0);
            v.setTag(vh);

            // 리스트에 테마적용
            if (StringUtil.equals(PrefUtil.getInstance().getString(PreferenceKeys.DP_THEME, Const.BLACK_THEME), Const.BLACK_THEME)) {
            	v.setBackgroundResource(R.drawable.theme_black_list_selector);
            	vh.line.setTextAppearance(context, R.style.CommonTextAppearanceListContentsWhite);
            } else {
            	v.setBackgroundResource(R.drawable.theme_white_list_selector);
            	vh.line.setTextAppearance(context, R.style.CommonTextAppearanceListContents);
            }

            return v;
        }
        
        @Override
        public void bindGroupView(View view, Context context, Cursor cursor, boolean isexpanded) {

            ViewHolder vh = (ViewHolder) view.getTag();

            String title = cursor.getString(mGroupTitleIdx);
            vh.line.setText(title);
        }

        @Override
        public void bindChildView(View view, Context context, Cursor cursor, boolean islast) {

            ViewHolder vh = (ViewHolder) view.getTag();

            String title = cursor.getString(cursor.getColumnIndexOrThrow(DpDB.Bbs.TITLE));
            vh.line.setText(title);
            
            ImageView iv = vh.icon;
            iv.setBackgroundDrawable(mDefaultBbsIcon);
            iv.setImageDrawable(null);
        }

        
        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            String id = groupCursor.getString(groupCursor.getColumnIndexOrThrow(DpDB.Bbs.CAT_ID));
            
            // 인증 여부 값 설정 (로그인 상태 : 2, 비로그인 상태 : 1)
            String authValue = "1";
            if (DpUtil.isAutoLoginEnabled() || DpUtil.isLogined())
            	authValue = "2";
            
            Uri mUri = Uri.withAppendedPath(DpDB.Bbs.CATEGORY_URI, Uri.encode(id));
            mUri = Uri.withAppendedPath(mUri, Uri.encode(authValue));
            Cursor c = DpUtil.query(mActivity, mUri, RequestCode.BbsCols, null , null, DpDB.Bbs.DEFAULT_SORT_ORDER);
            
            class MyCursorWrapper extends CursorWrapper {
                String mBbsTitle;
                int mMagicColumnIdx;
                MyCursorWrapper(Cursor c, String title) {
                    super(c);
                    mBbsTitle = title;
                    mMagicColumnIdx = c.getColumnCount();
                }
                
                @Override
                public String getString(int columnIndex) {
                    if (columnIndex != mMagicColumnIdx) {
                        return super.getString(columnIndex);
                    }
                    return mBbsTitle;
                }
                
                @Override
                public int getColumnIndexOrThrow(String name) {

                    return super.getColumnIndexOrThrow(name); 
                }
                
                @Override
                public String getColumnName(int idx) {
                    if (idx != mMagicColumnIdx) {
                        return super.getColumnName(idx);
                    }
                    return DpDB.Bbs.TITLE;
                }
                
                @Override
                public int getColumnCount() {
                    return super.getColumnCount() + 1;
                }
            }
            return new MyCursorWrapper(c, groupCursor.getString(mGroupTitleIdx));
        }

        @Override
        public void changeCursor(Cursor cursor) {
            if (cursor != mActivity.mBbsCursor) {
                mActivity.mBbsCursor = cursor;
                getColumnIndices(cursor);
                super.changeCursor(cursor);
            }
        }
        
        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            String s = constraint.toString();
            if (mConstraintIsValid && (
                    (s == null && mConstraint == null) ||
                    (s != null && s.equals(mConstraint)))) {
                return getCursor();
            }
            Cursor c = mActivity.getCommunityCursor(null, s);
            mConstraint = s;
            mConstraintIsValid = true;
            return c;
        }
    }
    
    private Cursor mBbsCursor;
}

