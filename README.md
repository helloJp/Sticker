#Sticker
![Sticker art](https://github.com/helloJp/Sticker/blob/master/art/sticker.gif)

1.change **StickerView**'s two fields to decide ***MAX*** or ***MIN*** scale size

	public static final float MAX_SCALE_SIZE = 3.2f;
    public static final float MIN_SCALE_SIZE = 0.6f;
    
2.override **dispatchTouchEvent** if have to add more than one sticker
    
```
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

```  
    
