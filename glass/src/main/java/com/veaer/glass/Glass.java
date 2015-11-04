/*
 * Copyright (C) 2015 Veaer <veaer.king@gmail.com>
 *
 * This file is part of Veaer's project
 *
 * You should have received a copy of the GNU General Public License
 * along with this project.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.veaer.glass;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.veaer.glass.setter.Setter;
import com.veaer.glass.setter.SetterFactory;
import com.veaer.glass.util.LocalDisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Veaer on 15/11/3.
 */
public class Glass {
    private final List<Setter> setters;

    private Glass(List<Setter> setters) {
        this.setters = setters;
    }

    public void setColor(@ColorInt int colour) {
        for (Setter setter : setters) {
            setter.setColor(colour);
        }
    }

    public static final class Builder {
        private List<Setter> setters;


        public static Builder newInstance() {
            return new Builder(new ArrayList<Setter>());
        }

        private Builder(List<Setter> setters) {
            this.setters = setters;
        }

        public Builder add(Setter setter) {
            if(null != setter) {
                setters.add(setter);
            }
            return this;
        }

        public Builder background(@NonNull View view) {
            return add(SetterFactory.getBackgroundSetter(view));
        }

        public Builder text(@NonNull TextView view) {
            return add(SetterFactory.getTextSetter(view));
        }

        public Builder statusBar(Window window, @ColorInt int defaultColor) {
            return statusBarWithLower(window, null, defaultColor);
        }

        public Builder statusBarWithLower(Window window, Context context, @ColorInt int defaultColor) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                // get status bar height in pixel
                int statusBarHeight = 0;
                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
                }

                WindowManager.LayoutParams localLayoutParams = window.getAttributes();
                localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

                // set attributes again to trigger #dispatchWindowAttributesChanged
                window.setAttributes(localLayoutParams);

                ViewGroup decorView = (ViewGroup)window.getDecorView();

                ViewGroup decorChild = (ViewGroup)decorView.getChildAt(0);

                decorView.removeView(decorChild);

                FrameLayout frameLayout = new FrameLayout(context);
                frameLayout.setPadding(0, statusBarHeight, 0, 0);

                frameLayout.addView(decorChild);
                frameLayout.setBackgroundColor(defaultColor);
                background(frameLayout);

                decorView.addView(frameLayout);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Setter windowSetter = SetterFactory.getSystemSetter(window);
                windowSetter.setColor(defaultColor);
                add(windowSetter);
            }
            return this;
        }

        public Glass build() {
            return new Glass(setters);
        }
    }

}
