package edu.iastate.metnet.soft;

import java.util.Vector;

public class Sample extends Entity {
    private static final String[] validAttributes = {"Sample_anchor",
            "Sample_biomaterial_provider_ch[n]",
            "Sample_characteristics_ch[n]", "Sample_data_processing",
            "Sample_description", "Sample_extract_protocol_ch[n]",
            "Sample_geo_accession", "Sample_growth_protocol_ch[n]",
            "Sample_hyb_protocol", "Sample_label_ch[n]",
            "Sample_label_protocol_ch[n]", "Sample_molecule_ch[n]",
            "Sample_organism_ch[n]", "Sample_platform_id",
            "Sample_scan_protocol", "Sample_source_name_ch[n]",
            "Sample_table_begin", "Sample_table_end", "Sample_tag_count",
            "Sample_tag_length", "Sample_title",
            "Sample_treatment_protocol_ch[n]", "Sample_type"};

    private Vector<String> fixedAttribs;
    private int channels;

    public Sample(String identifier) {
        super(identifier);
        channels = 1;
        fixAttributes();
    }

    private void fixAttributes() {
        fixedAttribs = new Vector();
        if (channels == 1) {
            for (int x = 0; x < validAttributes.length; x++) {
                if (validAttributes[x].endsWith("ch[n]")) {
                    fixedAttribs.add(validAttributes[x].substring(0,
                            validAttributes[x].length() - 6));
                } else {
                    fixedAttribs.add(validAttributes[x]);
                }
            }
        }
        for (int x = 0; x < validAttributes.length; x++) {
            if (validAttributes[x].endsWith("ch[n]")) {
                for (int ch = 1; ch <= channels; ch++) {
                    String fixed = validAttributes[x]
                            .substring(0, validAttributes[x].length() - 6) +
                            "_ch" + ch;
                    fixedAttribs.add(fixed);
                }
            } else {
                fixedAttribs.add(validAttributes[x]);
            }
        }
    }

    public boolean isValidAttribute(String attribute) {
        return fixedAttribs.contains(attribute);
    }

    public String[] getRequiredAttributes() {
        return null;
    }

    public boolean addAttribute(String key, String value) throws SOFTException {
        int ch;
        if (key.endsWith("channel_count")) {

            try {
                ch = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                System.err.println("Error on " + key + "=" + value);
                return false;
            }

            if (ch > channels) {
                channels = ch;
                fixAttributes();
            }
        } else if (key.contains("_ch")) {
            try {
                ch = Integer.parseInt(key.substring(key.length() - 1));
            } catch (NumberFormatException nfe) {
                System.err.println("Error on " + key + "=" + value);
                return false;
            }
            if (ch > channels) {
                channels = ch;
                fixAttributes();
            }
        }
        return super.addAttribute(key, value);
    }
}
