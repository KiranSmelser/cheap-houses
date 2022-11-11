/**
 * The CheapHouses program reads a CSV file that includes the addresses, prices, and latitude/longitude coordinates of 
 * houses in a county and then plots the locations of the houses within the specified price range on a small graphical user 
 * interface.
 * 
 * @author Kiran Smelser
 */

import java.util.*;
import java.util.Map.Entry;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import javax.swing.JOptionPane;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * Houses
 * reads the .csv file and maps the houses' addresses to an array containing their price, latitude, and longitude.
 */
class Houses 
{
	HashMap<String, String[]> newHouses = new HashMap<>();
	
	/**
	 * Instantiates a new houses map.
	 *
	 * @param file is the file
	 */
	Houses(Scanner file)
	{
		String[] row = file.nextLine().split(",");
		
		while (file.hasNext())
		{
			row = file.nextLine().split(",");
			newHouses.put(row[0], Arrays.copyOfRange(row, 9, row.length));
		}
		file.close();
	}
	
	/**
	 * Limits the range of houses based on the cutoff price.
	 *
	 * @param cutoffPrice is the cutoff price
	 */
	public void limitRange(int cutoffPrice)
	{
		this.newHouses.entrySet().removeIf(entry -> Integer.parseInt(entry.getValue()[0]) > cutoffPrice); // Removes every key, value pair in the map that is greater than the cutoff price
	}

	/**
	 * Values.
	 *
	 * @return the collection of map values
	 */
	public Collection<String[]> values() {
		return this.newHouses.values();
	}

	/**
	 * Size.
	 *
	 * @return an int that is the size of the map
	 */
	public int size() {
		return this.newHouses.size();
	}
}

/**
 * CheapHouses
 * collects input from the user through a GUI and plots the houses present in the entered .csv file based on the latitude 
 * and longitude.
 */
public class CheapHouses
{	
	
	/**
	 * Creates and shows the GUI.
	 */
	public static void createAndShowGUI()
	{
		JFrame mainFrame = new JFrame("Home Price Distribution");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(400,474);
		
		JPanel mainPanel = new JPanel(null);
		mainPanel.setLayout(new BorderLayout());
		JPanel widgetsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		widgetsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		
		JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel fileLabel = new JLabel("File:");
		JFormattedTextField fileField = new JFormattedTextField("houses.csv");
		fileField.setColumns(8);
		filePanel.add(fileLabel);
		filePanel.add(fileField);
		 
		JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel priceLabel = new JLabel("Price:");
		JFormattedTextField priceField = new JFormattedTextField("0");
		priceField.setColumns(8);
		pricePanel.add(priceLabel);
		pricePanel.add(priceField);
		
		JButton plotButton = new JButton("Plot");
		plotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Scanner csvFile = new Scanner(new File(fileField.getText()));
					Houses housesMap = new Houses(csvFile);
					int cutoffPrice = Integer.parseInt(priceField.getText());
					housesMap.limitRange(cutoffPrice);
					JPanel graphicsPanel = new GPanel(housesMap);
					graphicsPanel.setPreferredSize(new Dimension(400,400));
					graphicsPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
					mainPanel.add(graphicsPanel, BorderLayout.NORTH);
					mainFrame.add(mainPanel);
					mainFrame.setVisible(true);
				} catch (NumberFormatException | IOException e1) {
		           String msg = (e1 instanceof NumberFormatException) ? "Please re-enter or try another number." : "File could not be found.";
		           JOptionPane.showMessageDialog(mainFrame, msg);
				}
			}
		});
		
		widgetsPanel.add(filePanel);
		widgetsPanel.add(pricePanel);
		widgetsPanel.add(plotButton);
		mainPanel.add(widgetsPanel, BorderLayout.SOUTH);
		mainFrame.add(mainPanel);
		mainFrame.setVisible(true);
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args)
	{
		createAndShowGUI();
	}
	
	/**
	 * The Class GPanel.
	 */
	private static class GPanel extends JPanel
	{
		
		/** The houses to plot. */
		Houses housesToPlot;
		
		/**
		 * Instantiates a new g panel.
		 *
		 * @param housesMap the houses map
		 */
		public GPanel(Houses housesMap)
		{
			this.housesToPlot = housesMap;
		}
		
		/**
		 * Blanks the board and plots the houses.
		 *
		 * @param g is the Graphics library.
		 */
		public void paintComponent(Graphics g)
		{
			getGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, 400, 400);
			g.setColor(Color.black);
			
			List<Double> lats = new ArrayList<>();
			List<Double> longs = new ArrayList<>();
			for (String[] value : this.housesToPlot.values())
			{
				lats.add(Double.parseDouble(value[1]));
				longs.add(Double.parseDouble(value[2]));
			}
			
			double maxLat = Collections.max(lats);
			double minLat = Collections.min(lats);
			double maxLong = Collections.max(longs);
			double minLong = Collections.min(longs);

			List<Double> normalizedLats = new ArrayList<>();
			List<Double> normalizedLongs = new ArrayList<>();
			for (int i = 0; i < lats.size(); i++)
			{
				normalizedLats.add(400 * ((lats.get(i) - minLat) / (maxLat - minLat)));
				normalizedLongs.add(400 * ((longs.get(i) - minLong) / (maxLong - minLong)));
			}
			
			for (int i = 0; i < normalizedLats.size(); i++)
			{
				double tempLat = normalizedLats.get(i);
				double tempLong = normalizedLongs.get(i);
				g.fillOval((int) tempLong, (int) tempLat, 5, 5);
			}
		}
	}
}