package edu.iastate.metnet.soft;

import java.util.Arrays;

public class Platform extends Entity {
    private static final String[] validAttributes = {
            "Platform_catalog_number",
            "Platform_coating",
            "Platform_contributor",
            "Platform_description",
            "Platform_distribution",
            "Platform_geo_accession",
            "Platform_manufacture_protocol",
            "Platform_manufacturer",
            "Platform_organism",
            "Platform_pubmed_id",
            "Platform_support",
            "Platform_table_begin",
            "Platform_table_end",
            "Platform_technology",
            "Platform_title",
            "Platform_web_link"};

    public Platform(String identifier) {
        super(identifier);
    }

    public boolean isValidAttribute(String attribute) {
        return Arrays.binarySearch(validAttributes, attribute) >= 0;
    }

    public String[] getRequiredAttributes() {
        return null;
    }
}
