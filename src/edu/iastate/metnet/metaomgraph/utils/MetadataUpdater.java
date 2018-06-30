package edu.iastate.metnet.metaomgraph.utils;

import edu.iastate.metnet.metaomgraph.utils.qdxml.DocHandler;
import edu.iastate.metnet.metaomgraph.utils.qdxml.QDParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class MetadataUpdater {
	public MetadataUpdater() {
	}

	public static void main(String[] args) throws Exception {
		File infile = Utils.mandatoryFileOpen();
		File outfile = new File(infile.getParent(), "newmetadata.xml");

		update(new FileInputStream(infile), new FileOutputStream(outfile));
		System.out.println("Done!");
		System.exit(0);

		SAXBuilder builder = new SAXBuilder();
		Document myDoc = builder.build(infile);
		Element root = (Element) myDoc.getContent(new ElementFilter()).get(0);
		Element newRoot = new Element("MOGMetadata");
		List<Element> exps = root.getChildren();
		for (Element exp : exps) {
			newRoot.addContent(makeNewExperimentElement(exp));
		}
		XMLOutputter output = new XMLOutputter();
		Document newDoc = new Document(newRoot);
		output.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));
		BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
		output.output(newDoc, out);
		System.out.println("Done!");
		System.exit(0);
	}

	public static void update(InputStream source, OutputStream dest) throws Exception {
		OldMetadataHandler handler = new OldMetadataHandler(dest);
		BufferedReader in = new BufferedReader(new InputStreamReader(source));
		QDParser.parse(handler, in);
	}

	/*
	 * Important UPDATE urmi 2/10/2018 Previously MOG had only 2 levels of depth.
	 * Now I added One more i.e. sample so now we have in metada Study --Sample
	 * --Run In older MOG code run had node name as "chip or "samp" therefore to
	 * keep new code backwards compatible I call the Sample as OuterSamp instead of
	 * sample. Therefore, a sample tag can still refer to run but in newer version I
	 * will generate XML with the "run" tag. Finding reps functions also had to be
	 * changed Change the icons in MetadataPanel.java file
	 */

	/*
	 * Important UPDATE urmi 3/2/2018 Previously MOG had only 2 levels of depth. Now
	 * it can support general structure. All XML nodes will have their column names
	 * in tags. Changed how this is handeled while loading and displaying metadata
	 * 
	 */
	// make a node other than data node
	static Element makeNewElement(Element exp, String colname) {
		Element result = new Element(colname);
		result.setAttribute("name", exp.getAttributeValue("name"));
		List<Element> children = exp.getChildren();
		for (Element child : children) {
			String name = child.getName().replaceAll("_or_", "/").replaceAll("_", " ");
			result.addContent(makeNewElement(child,child.getName()));
		}
		return result;
	}


	static Element makeNewExperimentElement(Element exp) {
		Element result = new Element("Experiment");
		result.setAttribute("name", exp.getAttributeValue("name"));
		List<Element> children = exp.getChildren();
		for (Element child : children) {
			String name = child.getName().replaceAll("_or_", "/").replaceAll("_", " ");
			if (name.equalsIgnoreCase("sample") || name.equalsIgnoreCase("chip") || name.equalsIgnoreCase("run")) {
				result.addContent(makeNewSampleElement(child));
			} else if ((name.equalsIgnoreCase("OuterSamp"))) {
				result.addContent(makeNewOuterSampElement(child));

			} else {
				Element addMe = new Element("md").setAttribute("field", name);
				addMe.setAttribute("value", child.getText());
				result.addContent(addMe);
			}
		}
		return result;
	}

	// urmi This refers to making a node which represents a run. An OuterSamp is the
	// true sample node
	// Didn't change sample coz of issues with backward compatibility
	static Element makeNewSampleElement(Element samp) {
		Element result = new Element("Sample");
		result.setAttribute("name", samp.getAttributeValue("name"));
		String col = samp.getAttributeValue("col");
		if (col != null) {
			result.setAttribute("col", col);
		}
		List<Element> children = samp.getChildren();
		for (Element child : children) {
			String name = child.getName().replaceAll("_or_", "/").replaceAll("_", " ");
			Element addMe = new Element("md").setAttribute("field", name);
			addMe.setAttribute("value", child.getText());
			result.addContent(addMe);
		}
		return result;
	}

	// urmi add Sample which is higher step than a run element known here as
	// OuterSamp
	static Element makeNewOuterSampElement(Element samp) {
		Element result = new Element("OuterSamp");
		result.setAttribute("name", samp.getAttributeValue("name"));
		String col = samp.getAttributeValue("col");
		if (col != null) {
			result.setAttribute("col", col);
		}
		List<Element> children = samp.getChildren();
		for (Element child : children) {
			String name = child.getName().replaceAll("_or_", "/").replaceAll("_", " ");
			Element addMe = new Element("md").setAttribute("field", name);
			addMe.setAttribute("value", child.getText());
			result.addContent(addMe);
		}
		return result;
	}

	private static class OldMetadataHandler implements DocHandler {
		private ArrayDeque<String> tagStack;
		private ArrayDeque<String> textStack;
		private BufferedWriter out;
		private int depth;

		public OldMetadataHandler(File dest) throws IOException {
			tagStack = new ArrayDeque();
			textStack = new ArrayDeque();
			out = Utils.getUTF8Writer(dest);
			depth = 0;
		}

		public OldMetadataHandler(OutputStream dest) throws IOException {
			tagStack = new ArrayDeque();
			textStack = new ArrayDeque();
			out = new BufferedWriter(new OutputStreamWriter(dest));
			depth = 0;
		}

		// urmi changed to add run
		public void startElement(String tag, Hashtable<String, String> h) throws Exception {
			if ("Experiments".equalsIgnoreCase(tag)) {
				out.write("<MOGMetadata>");
				out.newLine();
				depth += 1;
			} else if (("OuterSamp".equalsIgnoreCase(tag) || "Sample".equalsIgnoreCase(tag))
					|| ("Chip".equalsIgnoreCase(tag)) || ("Experiment".equalsIgnoreCase(tag))
					|| ("Run".equalsIgnoreCase(tag))) {
				String name = h.get("name");
				String col = h.get("col");
				for (int i = 0; i < depth; i++) {
					out.write("  ");
				}
				if ("Experiment".equalsIgnoreCase(tag)) {
					out.write("<Experiment");
				} else if ("OuterSamp".equalsIgnoreCase(tag)) {
					out.write("<OuterSamp");
				} else {
					out.write("<Sample");
				}
				if (name != null) {
					name = name.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;")
							.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
					out.write(" name=\"" + name + "\"");
				}
				if ((col != null) && ("" != col)) {
					out.write(" col=\"" + col + "\"");
				}
				out.write(">");
				out.newLine();
				depth += 1;
			} else {
				tagStack.push(tag.replaceAll("_or_", "/").replaceAll("_", " "));
			}
		}

		// urmi changed to add Run node
		public void endElement(String tag) throws Exception {
			if ("Experiments".equalsIgnoreCase(tag)) {
				out.write("</MOGMetadata>");
				depth -= 1;
			} else if (("OuterSamp".equalsIgnoreCase(tag))) {
				depth -= 1;
				for (int i = 0; i < depth; i++) {
					out.write("  ");
				}
				out.write("</OuterSamp>");
				out.newLine();
			} else if ("Experiment".equalsIgnoreCase(tag)) {
				depth -= 1;
				for (int i = 0; i < depth; i++) {
					out.write("  ");
				}
				out.write("</Experiment>");
				out.newLine();
			} else if ("Run".equalsIgnoreCase(tag) || "Chip".equalsIgnoreCase(tag) || "Sample".equalsIgnoreCase(tag)) {
				depth -= 1;
				for (int i = 0; i < depth; i++) {
					out.write("  ");
				}
				out.write("</Samp>");
				out.newLine();
			}

			else {
				String field = tagStack.pop().replaceAll("&", "&amp;").replaceAll("\"", "&quot;")
						.replaceAll("'", "&apos;").replaceAll("&", "&amp;").replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;");
				String val = textStack.pop().replaceAll("&", "&amp;").replaceAll("\"", "&quot;")
						.replaceAll("'", "&apos;").replaceAll("&", "&amp;").replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;");
				for (int i = 0; i < depth; i++) {
					out.write("  ");
				}
				out.write("<md field=\"" + field + "\" value=\"" + val + "\" />");
				out.newLine();
			}
		}

		public void startDocument() throws Exception {
		}

		public void endDocument() throws Exception {
			out.close();
		}

		public void text(String str) throws Exception {
			textStack.push(str);
		}
	}
}
