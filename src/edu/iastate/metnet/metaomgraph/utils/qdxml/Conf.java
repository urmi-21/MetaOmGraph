package edu.iastate.metnet.metaomgraph.utils.qdxml;

import java.io.FileReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;


public class Conf
        implements DocHandler {
    Stack stack;

    public Conf() {
    }

    public void text(String s) {
    }

    static class Propulsion {
        Conf.Engine portWarpDrive = new Conf.Engine();
        Conf.Engine starboardWarpDrive = new Conf.Engine();

        Propulsion() {
        }
    }

    static class Defense {
        Conf.DefenseMechanism forceField = new Conf.DefenseMechanism();
        Conf.DefenseMechanism ecm = new Conf.DefenseMechanism();


        Defense() {
        }
    }


    static class Weapons {
        Conf.Weapon mainEnergyBeam = new Conf.Weapon();
        Conf.Weapon secondaryEnergyBeam = new Conf.Weapon();
        Conf.Weapon energyTorpedo = new Conf.Weapon();
        Conf.CombatComputer combatComputer = new Conf.CombatComputer();

        Weapons() {
        }
    }

    static class AtmosphereControl {
        AtmosphereControl() {
        }

        Conf.AtmosphereSetting engineering = new Conf.AtmosphereSetting();
        Conf.AtmosphereSetting bridge = new Conf.AtmosphereSetting();
        Conf.AtmosphereSetting hydroponics = new Conf.AtmosphereSetting();

    }

    static class LifeSupport {
        Conf.AtmosphereControl atmosphere = new Conf.AtmosphereControl();

        LifeSupport() {
        }
    }

    static class ShipConfig {
        Conf.Weapons weapons = new Conf.Weapons();
        Conf.Propulsion propulsion = new Conf.Propulsion();
        Conf.Defense defense = new Conf.Defense();
        Conf.LifeSupport lifeSupport = new Conf.LifeSupport();


        ShipConfig() {
        }
    }


    Object model = new ShipConfig();


    public void startElement(String name, Hashtable h) {
        System.out.println("  Configuring: " + name);
        if (stack.empty()) {
            stack.push(model);
        } else {
            stack.push(model);
            try {
                Field field = model.getClass().getDeclaredField(jname(name));
                model = field.get(model);
            } catch (Exception ex) {
                System.out.println("Error:  missing field on " +
                        model.getClass().getName() + ": " + name);
                model = new MissingSystem();
                return;
            }
        }


        Enumeration e = h.keys();
        while (e.hasMoreElements()) {
            String key = null;
            String val = null;
            try {
                key = (String) e.nextElement();
                val = (String) h.get(key);
                key = jname(key);
                System.out.println("    setting: " + key + " => " + val);
                Field field = model.getClass().getDeclaredField(key);


                field.set(model, val.toString());
            } catch (Exception ex) {
                System.out.println("Error:  missing field on " +
                        model.getClass().getName() + ": " + key);
            }
        }
    }

    public void endElement(String name) {
        model = stack.pop();
    }

    public void startDocument() {
        stack = new Stack();
    }

    public void endDocument() {
        stack = null;
    }


    public String jname(String s) {
        boolean ucase = false;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '-') {
                ucase = true;
            } else if (ucase) {
                sb.append(Character.toUpperCase(c));
                ucase = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        Conf c = new Conf();


        QDParser.parse(c, new FileReader("config.xml"));
    }

    static class AtmosphereSetting {
        String race;
        String scent;
        String level;

        AtmosphereSetting() {
        }
    }

    static class CombatComputer {
        String subSystem;
        String state;

        CombatComputer() {
        }
    }

    static class DefenseMechanism {
        String setting;
        String frequency;

        DefenseMechanism() {
        }
    }

    static class Engine {
        String spaceTimeSetting;
        String spaceFoldRate;

        Engine() {
        }
    }

    static class MissingSystem {
        MissingSystem() {
        }
    }

    static class Weapon {
        String power;
        String setting;

        Weapon() {
        }
    }
}
