package edu.iastate.metnet.metaomgraph.ui;

import edu.iastate.metnet.metaomgraph.MetaOmProject;
import edu.iastate.metnet.metaomgraph.TableSorter;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.AbstractBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;


public class RepInfoPanel
        extends JPanel
        implements ActionListener, TableModelListener {
    private static final int COLUMN_COL = 0;
    private static final int EXP_COL = 1;
    private static final int SAMPLE_COL = 2;
    private static final int GNAME_COL = 3;
    private static final int TREATMENT_COL = 4;
    private static final int QUALITY_COL = 5;
    private static final int BAD_COL = 6;
    private static final String MARK_REPS_COMMAND = "mark as reps";
    private static final String UNMARK_REPS_COMMAND = "These aren't actually reps you know";
    private static final String SORT_COMMAND = "Sort them reps!";
    private MetaOmProject myProject;
    private boolean update;
    private NoneditableTableModel model;
    private TableSorter sorter;
    private JTable table;

    public RepInfoPanel(MetaOmProject paramMetaOmProject) {
    }

    public void _RepInfoPanel(MetaOmProject paramMetaOmProject) {
        throw new Error("Unresolved compilation problems: \n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getPathForCol(int) is undefined for the type Metadata\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tpathForCol cannot be resolved or is not a field\n\tThe method getReps() is undefined for the type MetaOmProject\n\tExtendedInfoTree cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n\tInfoNode cannot be resolved to a type\n");
    }


    public void refreshTable() {
        throw new Error("Unresolved compilation problem: \n\tThe method getReps() is undefined for the type MetaOmProject\n");
    }


    public void actionPerformed(ActionEvent paramActionEvent) {
        throw new Error("Unresolved compilation problems: \n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n");
    }


    public void tableChanged(TableModelEvent paramTableModelEvent) {
        throw new Error("Unresolved compilation problems: \n\tThe method getReps() is undefined for the type MetaOmProject\n\tThe method getReps() is undefined for the type MetaOmProject\n");
    }


    public JTable getTable() {
        throw new Error("Unresolved compilation problem: \n");
    }

    public static class VariableEtchedBorder
            extends AbstractBorder {
        private boolean top;
        private boolean bottom;
        private boolean left;
        private boolean right;

        public VariableEtchedBorder() {
        }

        public boolean isTop() {
            throw new Error("Unresolved compilation problem: \n");
        }

        public void setTop(boolean paramBoolean) {
            throw new Error("Unresolved compilation problem: \n");
        }

        public boolean isBottom() {
            throw new Error("Unresolved compilation problem: \n");
        }

        public void setBottom(boolean paramBoolean) {
            throw new Error("Unresolved compilation problem: \n");
        }

        public boolean isLeft() {
            throw new Error("Unresolved compilation problem: \n");
        }

        public void setLeft(boolean paramBoolean) {
            throw new Error("Unresolved compilation problem: \n");
        }

        public boolean isRight() {
            throw new Error("Unresolved compilation problem: \n");
        }

        public void setRight(boolean paramBoolean) {
            throw new Error("Unresolved compilation problem: \n");
        }

        public Insets getBorderInsets(Component paramComponent) {
            throw new Error("Unresolved compilation problem: \n");
        }


        public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
            throw new Error("Unresolved compilation problem: \n");
        }


        private void paintTop(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
            throw new Error("Unresolved compilation problem: \n");
        }


        private void paintBottom(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
            throw new Error("Unresolved compilation problem: \n");
        }


        private void paintLeft(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
            throw new Error("Unresolved compilation problem: \n");
        }


        private void paintRight(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
            throw new Error("Unresolved compilation problem: \n");
        }
    }
}
