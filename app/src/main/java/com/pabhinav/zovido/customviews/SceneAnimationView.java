package com.pabhinav.zovido.customviews;

import android.widget.ImageView;

/**
 * @author pabhinav
 */
public class SceneAnimationView {

    private ImageView mImageView;
    private int[] mFrameRess;
    private int mDuration;
    private SceneAnimationEndListener sceneAnimationEndListener;
    private int mLastFrameNo;

    public SceneAnimationView(ImageView pImageView, int[] pFrameRess, int pDuration){
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDuration = pDuration;
        mLastFrameNo = pFrameRess.length - 1;

        mImageView.setImageResource(mFrameRess[0]);
        playConstant(1);
    }

    private void playConstant(final int pFrameNo){
        mImageView.postDelayed(new Runnable() {
            public void run() {
                mImageView.setImageResource(mFrameRess[pFrameNo]);

                if (pFrameNo == mLastFrameNo) {
                    //playConstant(0);  // this repeats the animation

                    if(sceneAnimationEndListener != null){
                        sceneAnimationEndListener.onEnd();
                    }

                } else {
                    playConstant(pFrameNo + 1);
                    try {
                        System.gc();
                    } catch (Throwable ignored) {
                    }
                }
            }
        }, mDuration);
    }

    public void setSceneAnimationEndListener(SceneAnimationEndListener sceneAnimationEndListener){
        this.sceneAnimationEndListener = sceneAnimationEndListener;
    }

    public interface SceneAnimationEndListener{
        void onEnd();
    }
}
