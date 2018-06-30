package edu.iastate.metnet.metaomgraph.test;

import org.jfree.chart.axis.TickUnit;

public class SpacedTickUnits implements org.jfree.chart.axis.TickUnitSource {
    public SpacedTickUnits() {
    }

    public TickUnit getCeilingTickUnit(TickUnit unit) {
        int num = unit.getMinorTickCount() / 100 * 100;
        return new MyTickUnit(num);
    }

    public TickUnit getCeilingTickUnit(double size) {
        return new MyTickUnit((int) (size / 100.0D) * 100);
    }

    public TickUnit getLargerTickUnit(TickUnit unit) {
        int num = (int) Math.ceil(unit.getMinorTickCount() / 100) * 100;
        return new MyTickUnit(num);
    }

    private class MyTickUnit extends TickUnit {
        private int num;

        public MyTickUnit(int num) {
            super(100);
            this.num = num;
        }

        public double getSize() {
            return 100.0D;
        }

        public int getMinorTickCount() {
            return num;
        }
    }
}
