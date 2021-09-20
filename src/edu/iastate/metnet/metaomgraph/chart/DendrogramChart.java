/**
 * 
 */
package edu.iastate.metnet.metaomgraph.chart;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.FrameModel;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;
import edu.iastate.metnet.metaomgraph.ui.TaskbarInternalFrame;

/**
 * @author sumanth
 * Dendrogram chart panel to display the heatmap chart dendrogram
 *
 */
public class DendrogramChart extends TaskbarInternalFrame{
	private DendrogramPanel dendrogramPanel;
	
	JScrollPane scrollPane;

	// toolbar buttons
	private JButton save;
	
	/**
	 * Constructor
	 * @param dendrogramPanel
	 */
	public DendrogramChart(DendrogramPanel dendrogramPanel) {
		this.dendrogramPanel = dendrogramPanel;
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		getContentPane().add(panel, BorderLayout.NORTH);
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(dendrogramPanel);
		
		save = new JButton(MetaOmGraph.getIconTheme().getSaveAs());
		save.setToolTipText("Save Chart as Image");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveImage();
			}
		});
		panel.add(save);
		this.setClosable(true);
		putClientProperty("JInternalFrame.frameType", "normal");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		this.setTitle("Dendrogram chart");
		FrameModel dendrogramPlotFrameModel = new FrameModel("Dendrogram Plot", "Dendrogram plot", 8);
		setModel(dendrogramPlotFrameModel);
	}
	
	// Save the chart as image
	private void saveImage() {
		HashMap<String, String> fileTypes = new HashMap<String, String>();
		fileTypes.put("PNG", ".png");
		JFileChooser fileSave = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG files", "png");
		fileSave.addChoosableFileFilter(filter);

		if(fileSave.showSaveDialog(MetaOmGraph.getMainWindow()) == JFileChooser.APPROVE_OPTION){
			new AnimatedSwingWorker("Saving image...", true) {

				@Override
				public Object construct() {
					File fileToSave = fileSave.getSelectedFile();
					if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
						fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".png");
					}
					BufferedImage image = new BufferedImage(dendrogramPanel.getWidth(),dendrogramPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = image.createGraphics();
					dendrogramPanel.paint(g2);
					try{
						ImageIO.write(image, "png", fileToSave);
						JOptionPane.showMessageDialog(null, 
								"Image saved", "File saved", JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, 
								"Image not saved", "File save error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
						return false;
					}
					return null;
				}
			}.start();
		}	
	}
	
}
