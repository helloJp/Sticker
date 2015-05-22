package me.jp.sticker;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.jp.sticker.adapter.GalleryAdapter;
import me.jp.sticker.view.StickerView;


public class MainActivity extends AppCompatActivity {
    //effect picture save absolute path
    private final String EFFECT_PICTURE = Environment
            .getExternalStorageDirectory() + File.separator + "Sticker" + File.separator + "effect_picture.jpg";

    Toolbar mToolbar;

    ImageView mImageView;
    RecyclerView mRecyclerView;
    GalleryAdapter mGalleryAdapter;
    ImageView mEffectImg;

    int mStatusBarHeight;
    int mToolBarHeight;

    List<StickerView> mStickers = new ArrayList<>();
    int[] mResIds = new int[]{R.mipmap.ic_sticker_01, R.mipmap.ic_sticker_02, R.mipmap.ic_sticker_03, R.mipmap.ic_sticker_04, R.mipmap.ic_sticker_05, R.mipmap.ic_sticker_06, R.mipmap.ic_sticker_07, R.mipmap.ic_sticker_08};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();

    }

    private void initEvent() {
        mGalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int resId) {
                addStickerItem(resId);
            }
        });
    }

    private void addStickerItem(int resId) {
        resetStickersFocus();
        StickerView stickerView = new StickerView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.image);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.image);
        ((ViewGroup) mImageView.getParent()).addView(stickerView, params);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        stickerView.setWaterMark(bitmap);
        mStickers.add(stickerView);
        stickerView.setOnStickerDeleteListener(new StickerView.OnStickerDeleteListener() {
            @Override
            public void onDelete(StickerView stickerView) {
                if (mStickers.contains(stickerView))
                    mStickers.remove(stickerView);
            }
        });
    }

    private void resetStickersFocus() {
        for (StickerView stickerView : mStickers) {
            stickerView.setFocusable(false);
        }
    }

    private void initView() {
        mStatusBarHeight = getStatusBarHeight();
        mToolBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpToolbar();
        mImageView = (ImageView) findViewById(R.id.image);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mImageView.setImageBitmap(getImageFromAssetsFile("photo.jpg"));
        mEffectImg = (ImageView) findViewById(R.id.effect_image);

        mGalleryAdapter = new GalleryAdapter(mResIds);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mGalleryAdapter);
    }

    private void setUpToolbar() {
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("Sticker");
        setSupportActionBar(mToolbar);//remember set theme to NoActionBar
        mToolbar.setOnMenuItemClickListener(OnMenuItemListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    Toolbar.OnMenuItemClickListener OnMenuItemListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_reset:
                    resetDisplay();
                    break;
                case R.id.action_save:
                    saveEffectBitmap();
                    break;
            }
            return true;
        }
    };

    private void resetDisplay() {
        mEffectImg.setVisibility(View.GONE);
        mStickers.clear();

        ViewGroup viewGroup = (ViewGroup) mImageView.getParent();
        int childCount = viewGroup.getChildCount();
        if (childCount > 1)
            viewGroup.removeViews(1, childCount - 1);

    }

    /**
     * save image with stickers
     */
    public void saveEffectBitmap() {

        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache();
        Bitmap bmBg = mImageView.getDrawingCache();//get background bitmap
        bmBg = Bitmap.createBitmap(bmBg, 0, 0, bmBg.getWidth(), bmBg.getHeight());//create bitmap with size
        mImageView.destroyDrawingCache();
        Canvas canvas = new Canvas(bmBg);//create canvas with background bitmap size
        canvas.drawBitmap(bmBg, 0, 0, null);

        //draw stickers on canvas
        for (StickerView stickerView : mStickers) {
            Bitmap bmSticker = stickerView.getBitmap();
            canvas.drawBitmap(bmSticker, 0, 0, null);
        }

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        //save effect bitmap into sd-card
//        BitmapUtil.saveBitmap(bmBg, EFFECT_PICTURE);
        mEffectImg.setVisibility(View.VISIBLE);
        mEffectImg.setImageBitmap(bmBg);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            //calculate action point Y apart from Container layout origin
            int y = (int) ev.getY() - mStatusBarHeight - mToolBarHeight;
            for (StickerView stickerView : mStickers) {
                // dispatch focus to the sticker based on Coordinate
                boolean isContains = stickerView.getContentRect().contains(x, y);
                if (isContains) {
                    resetStickersFocus();
                    stickerView.setFocusable(true);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
