import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class OrderScreen extends JFrame {

	static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private Order order = new Order();
	private Container cp;
	private	JScrollPane scrollPane;
	private JButton tableChoiceButton, addDinerButton;
	private JPanel northPane, southPane, centrePane, dinersPanel, tableChoicePanel;
	private JLabel orderHeadingLabel, tableNumberLabel, dinersHeadingLabel, totalPriceLabel;

	/**
	 * Create the dialog.
	 */
	public OrderScreen() {
		super("Restaurant Menu Selector - Order Screen");
		this.cp = this.getContentPane();
		this.cp.setLayout(new BorderLayout());

		// North pane
		this.northPane = new JPanel();
		
		this.orderHeadingLabel = new JLabel();
		this.orderHeadingLabel.setFont(this.orderHeadingLabel.getFont().deriveFont((float) 75.0));

		this.dinersHeadingLabel = new JLabel("Diners");
		this.dinersHeadingLabel.setFont(this.orderHeadingLabel.getFont().deriveFont((float) 50.0));

		this.dinersHeadingLabel.setFont(this.orderHeadingLabel.getFont().deriveFont((float) 50.0));

		this.tableNumberLabel = new JLabel();
		this.tableChoiceButton = new JButton("Change");
		this.tableChoiceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					TableChoiceDialog dialog = new TableChoiceDialog(OrderScreen.this);
					dialog.setLocationRelativeTo(OrderScreen.this);
					dialog.pack();
					dialog.setVisible(true);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

		});
		
		// Centre pane
		this.centrePane = new JPanel();
		this.centrePane.setLayout(new BoxLayout(this.centrePane, BoxLayout.Y_AXIS));

		this.scrollPane = new JScrollPane(this.centrePane);
		
		this.dinersPanel = new JPanel();
		this.dinersPanel.setLayout(new BoxLayout(this.dinersPanel, BoxLayout.Y_AXIS));

		this.tableChoicePanel = new JPanel();
		this.tableChoicePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		this.tableChoicePanel.add(this.tableNumberLabel);
		this.tableChoicePanel.add(this.tableChoiceButton);

		this.northPane.add(this.orderHeadingLabel);
		this.northPane.add(this.tableChoicePanel);
		
		this.centrePane.add(this.dinersHeadingLabel);
		this.centrePane.add(this.dinersPanel);
		
		// South pane
		this.southPane = new JPanel();
		
		this.totalPriceLabel = new JLabel();
		
		this.addDinerButton = new JButton("Add another diner");
		this.addDinerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				OrderScreen.this.order.addDiner(new Diner());
				System.out.println(OrderScreen.this.order.getDiners().toString());
				OrderScreen.this.refreshData();
			}

		});
		
		this.southPane.add(this.totalPriceLabel);
		this.southPane.add(this.addDinerButton);
		
		// Content pane
		this.cp.add(this.northPane, BorderLayout.NORTH);
		this.cp.add(this.scrollPane, BorderLayout.CENTER);
		this.cp.add(this.southPane, BorderLayout.SOUTH);
		
		this.refreshData();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.pack();
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		

	}

	private void insertDataToWindowFromOrderObject() {
		String tableNumber = "Not chosen";

		if (this.order.getTable() != null) {
			tableNumber = String.valueOf(this.order.getTable().getNumber());
		}

		this.tableNumberLabel.setText("Table number: " + tableNumber);
		this.orderHeadingLabel.setText("Order no. " + this.order.getNumber());
		this.totalPriceLabel.setText("Total: £" + DECIMAL_FORMAT.format(this.order.getTotalPrice()));

		// Remove all the diners from the screen
		this.dinersPanel.removeAll();
		this.dinersPanel.repaint();
		this.dinersPanel.revalidate();

		// Generate all the diners again
		if (this.order.getDiners().size() > 0) {
			int i = 1;
			for (Diner diner : this.order.getDiners()) {
				this.dinersPanel.add(this.generateDinerRow(diner, i));
				i++;
			}
		} else {
			this.dinersPanel.add(new JLabel("No diners defined."));
		}

	}

	private JPanel generateDinerRow(Diner diner, int dinerNumber) {
		// Create a panel for the row
		JPanel dinerRow = new JPanel();
		dinerRow.setLayout(new BoxLayout(dinerRow, BoxLayout.Y_AXIS));

		// Create a heading label for the row
		JLabel dinerHeadingLabel = new JLabel("Diner no. " + dinerNumber + "(total: £"
				+ DECIMAL_FORMAT.format(diner.getTotalPrice()) + ", " + diner.getTotalKiloCalories() + "kcal)");
		dinerHeadingLabel.setFont(this.orderHeadingLabel.getFont().deriveFont((float) 30.0));
		dinerRow.add(dinerHeadingLabel);

		if (this.order.getDiners().size() > 1) {
			JButton deleteDinerButton = new JButton("Delete a diner");
			deleteDinerButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int response = JOptionPane.showConfirmDialog(null, "Do you want to delete diner no. " + dinerNumber,
							"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (response == JOptionPane.YES_OPTION) {
						OrderScreen.this.order.deleteDiner(diner);
						OrderScreen.this.refreshData();
					}
				}

			});
			dinerRow.add(deleteDinerButton);
		}

		// Go through the courses and add them to our panel
		if (diner.getCourses().size() > 0) {
			for (Course course : diner.getCourses()) {
				dinerRow.add(this.generateDinerCourseRow(course, diner));
			}
		} else {
			dinerRow.add(new JLabel("No courses have been added yet."));
		}

		// Create a button to add a new course
		JButton addCourseButton = new JButton("Add a course for the diner");
		addCourseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						try {
							AddCourseToDinerScreen dialog = new AddCourseToDinerScreen(OrderScreen.this, diner);
							dialog.setLocationRelativeTo(OrderScreen.this);
							dialog.setVisible(true);
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}

				});

			}

		});
		dinerRow.add(addCourseButton);

		return dinerRow;
	}

	private JPanel generateDinerCourseRow(Course course, Diner diner) {
		JPanel courseRow = new JPanel();

		JButton deleteButton = new JButton("Delete a course");
		deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				diner.deleteCourse(course);
				OrderScreen.this.refreshData();

			}
		});

		courseRow.setLayout(new BoxLayout(courseRow, BoxLayout.X_AXIS));
		courseRow.add(new JLabel(course.getName()));
		courseRow.add(new JLabel("£" + DECIMAL_FORMAT.format(course.getPrice())));
		courseRow.add(new JLabel(course.getKiloCalories() + "kcal"));
		courseRow.add(new JLabel(course.getDescription()));

		if (course.getGlutenFree()) {
			courseRow.add(new JLabel("gluten-free"));
		}

		if (course.getNutFree()) {
			courseRow.add(new JLabel("nut-free"));
		}

		if (course.getVegan()) {
			courseRow.add(new JLabel("vegan"));
		}

		if (course.getVegetarian()) {
			courseRow.add(new JLabel("vegetarian"));
		}

		courseRow.add(deleteButton);
		return courseRow;
	}

	/**
	 * Gets an Order instance for the current screen.
	 * 
	 * @return Order instance
	 */
	public Order getOrder() {
		return this.order;
	}

	/**
	 * Refreshes data in the window after they have been altered.
	 */
	public void refreshData() {
		this.insertDataToWindowFromOrderObject();
	}

	public static void main(String[] args) {
		try {
			OrderScreen frame = new OrderScreen();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
