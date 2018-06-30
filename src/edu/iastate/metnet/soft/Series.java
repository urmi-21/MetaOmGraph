package edu.iastate.metnet.soft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Series extends Entity {
    private static final String[] validAttributes = {"Series_contributor",
            "Series_geo_accession", "Series_overall_design",
            "Series_pubmed_id", "Series_repeats_[n]",
            "Series_repeats_sample_list_[n]", "Series_sample_id",
            "Series_summary", "Series_title", "Series_type",
            "Series_variable_[n]", "Series_variable_description_[n]",
            "Series_variable_sample_list_[n]", "Series_web_link"};

    private static ArrayList<Entity> entities;

    private Platform myPlatform;
    private Sample[] samples;

    public Platform getMyPlatform() {
        return myPlatform;
    }

    public Sample[] getSamples() {
        return samples;
    }


    public Series(String identifier) {
        super(identifier);
    }

    public boolean isValidAttribute(String attribute) {
        String trueAttrib;
        if (attribute.matches(".*_\\d*")) {
            trueAttrib =
                    attribute.substring(0, attribute.lastIndexOf('_')) + "_[n]";
        } else {
            trueAttrib = attribute;
        }
        return Arrays.binarySearch(validAttributes, trueAttrib) >= 0;
    }

    public String[] getRequiredAttributes() {
        return null;
    }

    public boolean addEntity(Entity addMe) {
        if (((addMe instanceof Platform)) || ((addMe instanceof Sample))) {
            if (entities == null)
                entities = new ArrayList();
            entities.add(addMe);
            return true;
        }
        return false;
    }

    public void setPlatform(Platform myPlatform) {
        this.myPlatform = myPlatform;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = new Sample[samples.size()];
    }
}
