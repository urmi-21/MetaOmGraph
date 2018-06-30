package edu.iastate.metnet.metaomgraph.utils;

import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;

public class IntegerSpinner extends JSpinner {
    public IntegerSpinner(boolean onlyPositive) {
        SpinnerModel model = new IntegerModel(onlyPositive);
        setModel(model);
        if (onlyPositive) {
            final JComponent origEditor = this.getEditor();
            JFormattedTextField.AbstractFormatter af = new JFormattedTextField.AbstractFormatter() {
                public Object stringToValue(String text) throws ParseException {
                    try {
                        return new Integer(text);
                    } catch (NumberFormatException nfe) {
                    }
                    return new Integer(-1);
                }

                public String valueToString(Object value) throws ParseException {
                    if ((value instanceof Integer)) {
                        Integer intValue = (Integer) value;
                        if (intValue.intValue() < 0) {
                            return "???";
                        }
                        return ((Integer) value).intValue() + "";
                    }
                    return null;
                }


            };
            ((JSpinner.NumberEditor) origEditor).getTextField()
                    .setFormatterFactory(new DefaultFormatterFactory(af));
        }
    }

    private class IntegerModel
            implements SpinnerModel {
        private int value;
        private ArrayList<ChangeListener> listeners;
        private boolean onlyPositive;

        public IntegerModel(boolean onlyPositive) {
            this.onlyPositive = onlyPositive;
        }

        public void addChangeListener(ChangeListener l) {
            if (listeners == null) {
                listeners = new ArrayList();
            }
            listeners.add(l);
        }

        public Object getNextValue() {
            return Integer.valueOf(value + 1);
        }

        public Object getPreviousValue() {
            int prev = value - 1;
            if ((onlyPositive) && (prev < 0)) {
                prev = -1;
            }
            return Integer.valueOf(prev);
        }

        public Integer getValue() {
            return new Integer(value);
        }

        public void removeChangeListener(ChangeListener l) {
            if (listeners == null) {
                return;
            }
            listeners.remove(l);
        }

        public void setValue(Object value) {
            Integer newVal;
            try {
                newVal = new Integer(value + "");
            } catch (Exception e) {
                newVal = this.getValue();
            }
            if (onlyPositive && newVal < 0) {
                newVal = -1;
            }
            this.value = newVal;
        }
    }
}
