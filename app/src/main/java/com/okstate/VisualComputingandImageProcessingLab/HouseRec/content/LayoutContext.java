package com.okstate.VisualComputingandImageProcessingLab.HouseRec.content;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class LayoutContext implements Serializable {
    public final UUID id;
    public String layout;
    public List<HomeContext> Locations;



    public LayoutContext(UUID id, String layout, List<HomeContext> Locations) {
        this.id = id;
        this.layout = layout;
        this.Locations = Locations;
    }

    @Override
    public String toString() {
        return layout;
    }
}