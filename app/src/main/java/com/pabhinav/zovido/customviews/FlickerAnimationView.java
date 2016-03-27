package com.pabhinav.zovido.customviews;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

/**
 * This class uses {@link android.graphics.drawable.AnimationDrawable}
 * object for creating a frame animation.
 * It requires list of drawable resource ids to be flickered in
 * frame animation, flickering time in milliseconds and {@link ImageView}
 * object on which animation is to be done.
 *
 * @author pabhinav.
 */
public class FlickerAnimationView {

    /**
     * Images will be flickered on this ImageView object.
     */
    private ImageView flickerImageView;

    /**
     * List of all images to be displayed on ImageView object.
     */
    private Drawable[]  intResIds;

    /**
     * Time after which images will be displayed on ImageView object.
     */
    private int flickTimeInMillis;

    /**
     * This is used to build frame animation.
     */
    private AnimationDrawable animationDrawable;

    /**
     * Default Constructor used to set some private fields of this class.
     *
     * @param flickerImageView the image view in which flicker effect has to be rendered
     * @param intResIds the drawable resources ids for each image which will be 
     *                  rendered as part of flicker animation
     * @param flickTimeInMillis time in between two flicker of images
     */
    public FlickerAnimationView(ImageView flickerImageView, Drawable[] intResIds, int flickTimeInMillis){

        this.flickerImageView = flickerImageView;
        this.intResIds = intResIds;
        this.flickTimeInMillis = flickTimeInMillis;

        /** Creates AnimationDrawable object adding drawable resources **/
        createAnimationDrawable();

        /** Sets image background as animation drawable **/
        flickerImageView.setImageDrawable(animationDrawable);
    }

    /**
     * Default Constructor used to set some private fields of this class.
     *
     * @param flickerImageView the image view in which flicker effect has to be rendered
     * @param intResIds the drawable resources ids for each image which will be
     *                  rendered as part of flicker animation
     * @param flickTimeInMillis time in between two flicker of images
     */
    public FlickerAnimationView(ImageView flickerImageView, Drawable[] intResIds, int flickTimeInMillis, boolean isFlickerInfinite){

        this.flickerImageView = flickerImageView;
        this.intResIds = intResIds;
        this.flickTimeInMillis = flickTimeInMillis;

        /** Creates AnimationDrawable object adding drawable resources **/
        createAnimationDrawable(isFlickerInfinite);

        /** Sets image background as animation drawable **/
        flickerImageView.setImageDrawable(animationDrawable);
    }

    /**
     * Creates {@link AnimationDrawable} object,
     * uses Drawable resources given to this class
     * in its constructor.
     * Also, makes sure animation is done only once by
     * setting {@code setOneShot} method input as true.
     */
    private void createAnimationDrawable(){
        animationDrawable = new AnimationDrawable();
        for (Drawable d : intResIds) {
            animationDrawable.addFrame(d,flickTimeInMillis);
        }
        animationDrawable.setOneShot(true);
    }

    /**
     * Creates {@link AnimationDrawable} object,
     * uses Drawable resources given to this class
     * in its constructor.
     * Also, makes sure animation is done only once by
     * setting {@code setOneShot} method input as true.
     */
    private void createAnimationDrawable(boolean isFlickerInfinite){
        animationDrawable = new AnimationDrawable();
        for (Drawable d : intResIds) {
            animationDrawable.addFrame(d,flickTimeInMillis);
        }
        animationDrawable.setOneShot(!isFlickerInfinite);
    }

    /**
     * This method begins flickering of images,
     * after certain time interval.
     * Also, makes sure that image is visible.
     */
    public void startFlicking(){

        /** Make Image visible first **/
        flickerImageView.setVisibility(View.VISIBLE);

        /** Start it **/
        if(!animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    /**
     * This method is invoked when flickering needs to be stopped.
     * Simple method, signals stop of flickering
     */
    public void stopFlicking(){

        /** Stop it **/
        if(animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }
}


