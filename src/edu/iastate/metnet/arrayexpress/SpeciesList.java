package edu.iastate.metnet.arrayexpress;

public enum SpeciesList {
    HUMAN_HGU133A("hgu133a", "Homo Sapiens (HG-U133A)", "A-AFFY-33"),
    MOUSE_430_2("mouse430_2", "Mus Musculus (430 2.0)", "A-AFFY-45"),
    ARABIDOPSIS_ATH1("ATH1", "Arabidopsis Thaliana (ATH1)", "A-AFFY-2"),
    SOYBEAN("soybean", "Soybean Genome Array", "A-AFFY-59"),
    RAT_RAE230A("rae230a", "Rat Genome (230 A)", null),
    RAT_RAEX1("raex1", "Rat Genome Exon 1.0", null),
    RAT_RAGENE1("ragene1", "Rat Gene 1.0", null),
    RAT_230_2("rat230_2", "Rat Genome (230 2.0)", "A-AFFY-43"),
    RAT_U34A("u34a", "Rat Genome (U34A)", null),
    YEAST_S98("yeasts98", "Yeast Genome (S98)", "A-AFFY-27"),
    RICE("rice", "Rice Genome Array", "A-AFFY-126"),
    BARLEY("barley", "Barley Genome Array", "A-AFFY-31"),
    HUMAN_HGU133PLUS2("hgu133plus2", "Homo Sapiens (HG-U133 Plus 2.0)", "A-AFFY-44"),
    YEAST2("yeast2", "Yeast Genome 2.0 Array", "A-AFFY-47"),
    ZEBRAFISH("zebrafish", "Zebrafish Genome Array", "A-AFFY-38"),
    CUSTOM("custom", "Custom Array", null);

    private String folderName;

    SpeciesList(String folderName, String fullName, String acc) {
        this.folderName = folderName;
        this.fullName = fullName;
        this.acc = acc;
    }

    private String fullName;
    private String acc;

    public String getFolderName() {
        return folderName;
    }


    public String getFullName() {
        return fullName;
    }

    public String getAcc() {
        return acc;
    }

    public String toString() {
        return fullName;
    }
}
