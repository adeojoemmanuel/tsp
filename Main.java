import gui.DemoPanel;
import gui.StatusBar;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import controller.Controller;
import method.GraphDemonstration;
import method.MinimumSpanningTree;
import method.tsp.BranchBound;
import method.tsp.CheapestInsertion;
import method.tsp.HeldKarp;
import method.tsp.ImproveRoutine;
import method.tsp.NearestAddition;
import method.tsp.NearestInsertion;
import method.tsp.NearestNeighbor;
import method.tsp.NoImprovement;
import method.tsp.OneTree;
import method.tsp.Opt2;
import method.tsp.Opt3;
import method.tsp.OrOpt;
import method.tsp.TspConstruction;
import method.tsp.TspImprovement;
import model.Node;

/**
 * 起動のためのクラスです。
 * GUIの構築を行います。
 * @author ma38su
 */
public class Main {
	/**
	 * ファイルの文字コード
	 */
	private static final String charset = "UTF-8";

	/**
	 * プログラムタイトル
	 */
	private static final String TITLE = "TSP Demonstration";

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final List<GraphDemonstration> demoType = new ArrayList<GraphDemonstration>();
		demoType.add(new BranchBound(30, null));
		demoType.add(new BranchBound(40, null));
		demoType.add(new BranchBound(50, null));
		demoType.add(new BranchBound(20, new Opt2()));
		demoType.add(new BranchBound(30, new Opt2()));
		demoType.add(new BranchBound(40, new Opt2()));
		demoType.add(new BranchBound(50, new Opt2()));
		demoType.add(new BranchBound(100, new Opt2()));
		demoType.add(new BranchBound(150, new Opt2()));
		demoType.add(new BranchBound(200, new Opt2()));
		demoType.add(new BranchBound(10, new Opt3()));
		demoType.add(new BranchBound(25, new Opt3()));
		demoType.add(new BranchBound(50, new Opt3()));
		demoType.add(new BranchBound(100, new Opt3()));
		demoType.add(new BranchBound(150, new Opt3()));
		demoType.add(new BranchBound(200, new Opt3()));
		demoType.add(new BranchBound(300, new Opt3()));
		demoType.add(new HeldKarp(100));
		demoType.add(new MinimumSpanningTree());
		demoType.add(new OneTree());
		
		final List<TspConstruction> tspConstruct = new ArrayList<TspConstruction>();
		tspConstruct.add(new NearestNeighbor());
		tspConstruct.add(new NearestInsertion());
		tspConstruct.add(new CheapestInsertion());
		tspConstruct.add(new NearestAddition());

		final List<TspImprovement> tspImprovement = new ArrayList<TspImprovement>();
		Opt2 opt2 = new Opt2();
		Opt3 opt3 = new Opt3();
		OrOpt optOr = new OrOpt();
		tspImprovement.add(new NoImprovement());
		tspImprovement.add(opt2);
		tspImprovement.add(opt3);
		tspImprovement.add(optOr);
		tspImprovement.add(new ImproveRoutine(opt2, optOr));
		tspImprovement.add(new ImproveRoutine(opt2, opt3));
		tspImprovement.add(new ImproveRoutine(opt3, optOr));

		final JFrame frame = new JFrame(TITLE);

		final Observable observable = new Observable() {
			@Override
			public void notifyObservers(Object arg) {
				super.setChanged();
				super.notifyObservers(arg);
			}
		};

		final DemoPanel panel = new DemoPanel(observable);
		
		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		JMenu menu1 = new JMenu("ファイル");
		menubar.add(menu1);

		JMenuItem menu1_1 = new JMenuItem("ファイルを開く");
		menu1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(frame);
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
			    	File file = chooser.getSelectedFile();
			    	BufferedReader in = null;
			    	List<Node> list = new ArrayList<Node>();
			    	try {
			    		in = new BufferedReader(new FileReader(file));
			    		String line;
			    		while ((line = in.readLine()) != null) {
			    			list.add(new Node(line));
			    		}
			    		panel.setNodes(list);
			    	} catch (IOException ex) {
			    		ex.printStackTrace();
			    	} finally {
			    		if (in != null) {
			    			try {
			    				in.close();
			    			} catch (IOException ex) {
			    				ex.printStackTrace();
			    			}
			    		}
			    	}
			    }
			}
		});
		menu1.add(menu1_1);
		
		JMenuItem menu1_2 = new JMenuItem("名前を付けて保存");
		menu1_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedWriter out = null;
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showSaveDialog(frame);
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
			    	File file = chooser.getSelectedFile();
			    	try {
			    		out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
			    		synchronized (panel) {
			    			for (Node node : panel.getNodes()) {
			    				out.write(node.toString());
			    				out.newLine();
			    			}
			    		}
			    	} catch (IOException ex) {
			    		ex.printStackTrace();
			    	} finally {
			    		try {
			    			if (out != null) {
			    				out.close();
			    			}
			    		} catch (IOException ex) {
			    			ex.printStackTrace();
			    		}
			    	}
			    }
			}
		});
		menu1.add(menu1_2);
		
		menu1.addSeparator();
		
		JMenuItem menu1_3 = new JMenuItem("Exit");
		menu1_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu1.add(menu1_3);
		
		frame.setLayout(new BorderLayout());

		Controller controller = new Controller(panel);
		panel.addMouseListener(controller);
		frame.add(panel, BorderLayout.CENTER);

		JPanel subPanel = new JPanel(new BorderLayout());
		StatusBar statusbar = new StatusBar();
		observable.addObserver(statusbar);
		
		subPanel.add(statusbar, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		subPanel.add(buttonPanel, BorderLayout.EAST);

		final JButton startButton = new JButton("実行");
		final JButton formulationButton = new JButton("定式化");
		formulationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new JDialog(frame, true);
				JTextArea textarea = new JTextArea();
				JScrollPane textpane = new JScrollPane(textarea);
				String formulation = panel.getFormulation();
				textarea.setText(formulation);
				dialog.add(textpane);
				textarea.setEditable(false);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setTitle("定式化");
				dialog.setSize(480, 360);
				dialog.setLocationRelativeTo(frame);
				dialog.setVisible(true);
			}
		});
		buttonPanel.add(startButton);
		buttonPanel.add(formulationButton);
		final boolean[] isImprove = new boolean[tspImprovement.size()];
		final boolean[] isConstract = new boolean[tspConstruct.size()];
		final boolean[] isDemoType = new boolean[demoType.size()];

		isDemoType[0] = true;
		isImprove[0] = true;
		final ActionListener calc = new ActionListener() {
			private Thread thread;
			public void actionPerformed(ActionEvent e) {
				panel.setCost(0D);
				startButton.setText("Stop");
				Thread thread = new Thread() {
					@Override
					public void run() {
						boolean isRun = false;
						for (int i = 0; i < isConstract.length; i++) {
							if (isConstract[i]) {
								TspConstruction constraction = tspConstruct.get(i);
								final List<Node> route = constraction.method(panel);
								for (int j = 0; j < tspImprovement.size(); j++) {
									if (isImprove[j]) {
										final TspImprovement tsp = tspImprovement.get(j);
										if (j == 0) {
											frame.setTitle(TITLE + " - "+ constraction);
										} else {
											frame.setTitle(TITLE + " - "+ constraction + " + "+ tsp);
										}
										panel.set(route);
										observable.notifyObservers(route);
										while (tsp.method(route)) {
											panel.set(route);
										}
										observable.notifyObservers(route);
									}
								}
								isRun = true;
								break;
							}
						}
						if (!isRun) {
							for (int i = 0; i < isDemoType.length; i++) {
								if (isDemoType[i]) {
									GraphDemonstration demo = demoType.get(i);
									frame.setTitle(TITLE + " - "+ demo);
									demo.method(panel);
									isRun = true;
									break;
								}
							}
						}
						startButton.setText("Run");
					}
				};
				if (this.thread == null) {
					this.thread = thread;
					this.thread.start();
				} else {
					synchronized (this.thread) {
						if (this.thread.isAlive()) {
							this.thread.stop();
						}
						this.thread = thread;
						this.thread.start();
					}
				}
			}
		};
		startButton.addActionListener(calc);

		JButton clearButton = new JButton("Clear");
		buttonPanel.add(clearButton);
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.clear();
			}
		});

		/**
		 * 11:10
		 */
		JMenu menu2 = new JMenu("表示");
		JMenu menu2_1 = new JMenu("フレームレート");
		menu2.add(menu2_1);
		ButtonGroup group2 = new ButtonGroup();
		int timeIndex = 0;
		final int[] time = new int[]{0, 10, 50, 100, 500, 1000, 2500, 5000};
		panel.setInterval(time[timeIndex]);
		for (int i = 0; i < time.length; i++) {
			final int j = i;
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(Integer.toString(time[i]) + "ms", i == timeIndex);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					panel.setInterval(time[j]);
				}
			});
			group2.add(item);
			menu2_1.add(item);
		}

		menu2.addSeparator();
		JMenuItem menu2_2 = new JCheckBoxMenuItem("辺", true);
		menu2.add(menu2_2);
		menu2_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.switchEdgeView();
				panel.repaint();
			}
		});
		JMenuItem menu2_3 = new JCheckBoxMenuItem("接続制約", false);
		menu2.add(menu2_3);
		menu2_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.switchConnectViwe();
				panel.repaint();
			}
		});
		JMenuItem menu2_4 = new JCheckBoxMenuItem("非接続制約", false);
		menu2.add(menu2_4);
		menu2_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.switchDisconnectView();
				panel.repaint();
			}
		});
		JMenuItem menu2_5 = new JCheckBoxMenuItem("頂点番号", true);
		menu2.add(menu2_5);
		menu2_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.switchNodeIndexView();
				panel.repaint();
			}
		});
		
		menubar.add(menu2);

		JMenu menu3 = new JMenu("Type");
		ButtonGroup demoGroup = new ButtonGroup();
		for (int i = 0; i < demoType.size(); i++) {
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(demoType.get(i).toString(), isDemoType[i]);
			final int index = i;
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < isConstract.length; i++) {
						isConstract[i] = false;
					}
					for (int i = 0; i < isDemoType.length; i++) {
						isDemoType[i] = false;
					}
					isDemoType[index] = item.isSelected();
					calc.actionPerformed(null);
				}
			});
			demoGroup.add(item);
			menu3.add(item);
		}
		menubar.add(menu3);
		
		JMenu menu4 = new JMenu("構築法");
		for (int i = 0; i < tspConstruct.size(); i++) {
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(tspConstruct.get(i).toString(), isConstract[i]);
			final int index = i;
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < isConstract.length; i++) {
						isConstract[i] = false;
					}
					for (int i = 0; i < isDemoType.length; i++) {
						isDemoType[i] = false;
					}
					isConstract[index] = item.isSelected();
					calc.actionPerformed(null);
				}
			});
			demoGroup.add(item);
			menu4.add(item);
		}
		menubar.add(menu4);

		JMenu menu5 = new JMenu("改善法");
		ButtonGroup group5 = new ButtonGroup();
		for (int i = 0; i < tspImprovement.size(); i++) {
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(tspImprovement.get(i).toString(), isImprove[i]);
			final int index = i;
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < isImprove.length; i++) {
						isImprove[i] = false;
					}
					isImprove[index] = item.isSelected();
					calc.actionPerformed(null);
				}
			});
			group5.add(item);
			menu5.add(item);
		}
		menubar.add(menu5);
		
		frame.add(subPanel, BorderLayout.SOUTH);
		frame.setSize(600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static double getLength(List<Node> route) {
		double length = 0;
		if (route.size() > 0) {
			Node n0 = route.get(route.size() - 1);
			for (Node node : route) {
				length += n0.getDistance(node);
				n0 = node;
			}
		}
		return length;
	}
}
