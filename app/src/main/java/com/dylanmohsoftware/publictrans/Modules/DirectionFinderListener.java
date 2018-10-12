package com.dylanmohsoftware.publictrans.Modules;

import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderList(List<Route> route);
}
