/*
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.ux.application.view;

public class ViewSize {

    private Integer absoluteWidth;
    private Float relativeWidth;
    private Integer absoluteHeight;
    private Float relativeHeight;


    public static ViewSize ofAbsoluteWidth(int width) {
        return new ViewSize(width, null, null, null);
    }

    public static ViewSize ofRelativeWidth(float width) {
        return new ViewSize(null, width, null, null);
    }

    public static ViewSize ofAbsoluteHeight(int height) {
        return new ViewSize(null, null, height, null);
    }

    public static ViewSize ofRelativeHeight(float height) {
        return new ViewSize(null, null, null, height);
    }

    public ViewSize() {
    }

    public ViewSize(Integer absoluteWidth, Float relativeWidth, Integer absoluteHeight, Float relativeHeight) {
        this.absoluteWidth = absoluteWidth;
        this.relativeWidth = relativeWidth;
        this.absoluteHeight = absoluteHeight;
        this.relativeHeight = relativeHeight;
    }

    public boolean isWidthAvailable() {
	    return absoluteWidth != null || relativeWidth != null;
    }

    public Integer getAbsoluteWidth() {
        return absoluteWidth;
    }

    public void setAbsoluteWidth(Integer absoluteWidth) {
        this.absoluteWidth = absoluteWidth;
    }

    public Float getRelativeWidth() {
        return relativeWidth;
    }

    public void setRelativeWidth(Float relativeWidth) {
        this.relativeWidth = relativeWidth;
    }

    public boolean isHeightAvailable() {
	    return absoluteHeight != null || relativeHeight != null;
    }

    public Integer getAbsoluteHeight() {
        return absoluteHeight;
    }

    public void setAbsoluteHeight(Integer absoluteHeight) {
        this.absoluteHeight = absoluteHeight;
    }

    public Float getRelativeHeight() {
        return relativeHeight;
    }

    public void setRelativeHeight(Float relativeHeight) {
        this.relativeHeight = relativeHeight;
    }
}
