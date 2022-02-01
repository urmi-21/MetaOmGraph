package edu.iastate.metnet.metaomgraph;

public class ComplexQuery {
    String search;
    int colAsInt;
    String colAsString;
    boolean isColInt = false;
    boolean isAnd = false;
    boolean isOr = false;

    boolean isNotFlag = false;
    boolean doesNotFlag = false;
    boolean isFlag = false;

    boolean caseFlag = false;

    public ComplexQuery(String findMe, String column, String andOr) {
        search = findMe;
        try {
            colAsInt = Integer.parseInt(column);
            isColInt = true;
        } catch (NumberFormatException ne) {
            colAsString = column;
        }
        if ("AND".equalsIgnoreCase(andOr)) {
            isAnd = true;
        } else if ("OR".equalsIgnoreCase(andOr)) {
            isOr = true;
        }
    }

    public String generateFilter() {
        String delim = ":::";
        String filter = "";
        filter += getFlag() + getSearchTerm() + (isCaseFlag() ? "--C" : "") + delim;
        if (isAllCol()) {
            filter += "ALL";
        } else if (isAnyCol()) {
            filter += "ANY";
        } else {
            filter += getColumnAsInt();
        }
        return filter;
    }

    public String getSearchTerm() {
        return search;
    }

    public boolean isColInt() {
        return isColInt;
    }

    public boolean isAllCol() {
        return "ALL".equalsIgnoreCase(colAsString);
    }

    public boolean isAnyCol() {
        return "ANY".equalsIgnoreCase(colAsString);
    }

    public int getColumnAsInt() {
        return colAsInt;
    }

    public boolean isAnd() {
        return isAnd;
    }

    public boolean isOr() {
        return isOr;
    }

    public boolean isNotAndOr() {
        if (isAnd() || isOr()) {
            return false;
        }
        return true;
    }

    public String getFlag() {
        String flag = "";
        if (isNotFlag()) {
            flag = "!=";
        } else if (isDoesNotFlag()) {
            flag = "!";
        } else if (isFlag()) {
            flag = "=";
        }
        return flag;
    }

    public boolean isNotFlag() {
        return isNotFlag;
    }

    public boolean isDoesNotFlag() {
        return doesNotFlag;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public boolean isCaseFlag() {
        return caseFlag;
    }

    public void markCaseFlagTrue() {
        this.caseFlag = true;
    }

    public void markIsNotFlagTrue() {
        this.isNotFlag = true;
        this.doesNotFlag = false;
        this.isFlag = false;
    }

    public void markDoesNotFlagTrue() {
        this.doesNotFlag = true;
        this.isNotFlag = false;
        this.isFlag = false;
    }

    public void markIsFlagTrue() {
        this.isFlag = true;
        this.doesNotFlag = false;
        this.isNotFlag = false;
    }

    public void markAsContains() {
        this.isFlag = false;
        this.doesNotFlag = false;
        this.isNotFlag = false;
    }
}
