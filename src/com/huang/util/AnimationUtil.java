package com.huang.util;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
/**
 * 动画工具 用于产生一些特定的动画
 */
public class AnimationUtil
{
	
	public final static int START_UPANIMATION = 0;
	
	public final static int START_DOWNANIMATION = 1;
	
	/**
	 * 放大1.3倍 同时旋转360° 动画功能
	 * @return 
	 */
	public static AnimationSet getAddBalanceAnimation()
	{
		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation enlargeAnimation = new ScaleAnimation(1f, 1.3f, 1f, 1.3f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		RotateAnimation rorateAnimation =new RotateAnimation(0f,360f,
				Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		animationSet.addAnimation(rorateAnimation);
		animationSet.addAnimation(enlargeAnimation);
		animationSet.setDuration(2000);
		return animationSet;
		
	}
	
	/**
	 * 缩小0.6倍 同时旋转-360° 动画功能
	 * @param reducePercent 缩小的百分比 0f缩小至消失
	 * @return 
	 */
	public static AnimationSet getSubBalanceAnimation(float reducePercent)
	{
		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation decreaseAnimation = new ScaleAnimation(1f, reducePercent, 1f, reducePercent,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		RotateAnimation rorateAnimation =new RotateAnimation(0f,-360f,
				Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		animationSet.addAnimation(rorateAnimation);
		animationSet.addAnimation(decreaseAnimation);
		animationSet.setDuration(2000);
		boolean fillAfter = reducePercent == 0f ? true : false;
		animationSet.setFillAfter(fillAfter);
		return animationSet;
		
	}
	
	/**
	 * 从fromX移动到Tox 同时旋转-360°*4圈
	 * @param duration 持续时间 
	 * @return  动画集合
	 */
	public static AnimationSet getFaceRollOutAnimation(float fromX,float ToX,long duration)
	{
		LogUtil.d("huang", "fromX="+fromX+" ToX"+ToX);
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation moveOutToLeftAnimaiton = new TranslateAnimation
				(fromX,ToX, 0f, 0f);
		moveOutToLeftAnimaiton.setDuration(duration);
		
		RotateAnimation rollAnimation = new RotateAnimation
				(0f,-360f*4,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		//rollAnimation.setRepeatCount(1);
		rollAnimation.setDuration(duration);
		animationSet.addAnimation(rollAnimation);
		animationSet.addAnimation(moveOutToLeftAnimaiton);
		animationSet.setInterpolator(new AccelerateInterpolator());
		//animationSet.setDuration(2000);
		return animationSet;
	}
	
	
	public static AnimationSet getFaceRollInAndOutAnimation(float fromX,float ToX,long duration)
	{
		LogUtil.d("huang", "fromX="+fromX+" ToX"+ToX);
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation moveOutToLeftAnimaiton = new TranslateAnimation
				(fromX,ToX, 0f, 0f);
		moveOutToLeftAnimaiton.setDuration(duration);
		
		RotateAnimation rollAnimation = new RotateAnimation
				(0f,-360f*4,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		//rollAnimation.setRepeatCount(1);
		rollAnimation.setDuration(duration);
		animationSet.addAnimation(rollAnimation);
		animationSet.addAnimation(moveOutToLeftAnimaiton);
		animationSet.setInterpolator(new OvershootInterpolator());
		//animationSet.setDuration(2000);
		return animationSet;
	}
	
	/**
	 * 向上跳动 
	 * @param fromY 开始位置
	 * @param ToY结束位置
	 * @param duration 持续时间 
	 * @return 
	 */
	public static AnimationSet getJumpUpAnimation(final float fromY,final float ToY,long duration)
	{
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation jumpUpAnimaiton = new TranslateAnimation
				(0f,0f, fromY, ToY);
		jumpUpAnimaiton.setDuration(duration);
		animationSet.addAnimation(jumpUpAnimaiton);
		animationSet.setFillAfter(false);//jumpUpAnimaiton.setFillAfter(true);//无效
		animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
		return animationSet;
	}
	
	/**
	 * 向下跳蛋
	 * @param duration 持续时间 
	 * @return 
	 */
	public static AnimationSet getDownAnimation(float fromY,float ToY,long duration)
	{
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation dwonAnimation = new TranslateAnimation(0f, 0f, fromY,ToY );
		dwonAnimation.setDuration(1000);
		dwonAnimation.setFillAfter(true);
		animationSet.setInterpolator(new BounceInterpolator());//dwonAnimation.setinterploator没用
		animationSet.addAnimation(dwonAnimation);
		return animationSet;
	}
}
