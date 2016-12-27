import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Quiz {
	public Quiz() {
		File f = new File(_.ext_questionBankURL);
		if (f.exists()) {
			new Tkinnter();
			_.ext_questionBankFilefound = true;
			_.readQuestionBankFile();

		} else {
			_.ext_questionBankFilefound = false;
			new Tkinnter();
		}
	}

	public static void main(String a[]) {
		try {

			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			new Alert(
					"GUI Plugin Missing",
					"<html> <b>Error</b> : Look and Feel plugin 'Nimbus' is missing <html>",
					"Please use Java 7 or higher version");
			e.printStackTrace();
		}
		new Quiz();
	}
}

class _ {

	public static int TSCORE;
	// Settings
	public static int postivePoint = 5;
	public static int negativePoint = -3;
	public static int leavePoint = -1;

	public static boolean config_liveScore = true; // Show liveScore
	public static boolean config_correctAnswer = true; // Show Correct answer //
														// after answering
	public static boolean config_skipButton = true; // Enable skip button
	public static boolean config_report = true; // Generate report

	public static boolean ext_questionBankFileLoaded = false;
	public static boolean ext_questionBankFilefound = false;// The Question bank
															// file found

	public static String ext_questionBankURL = "Question.xml";

	public static void scoreCorrect() {
		TSCORE = TSCORE + postivePoint;
	}

	public static void scoreWrong() {
		TSCORE = TSCORE + negativePoint;
	}

	public static void scoreLeave() {
		TSCORE = TSCORE + leavePoint;
	}

	public static ArrayList<Question> QuestionBank = new ArrayList<Question>();

	/**
	 * This function is using to add a new question to the Quiz program
	 * 
	 * @param question
	 *            -> Provide your question here,
	 * @param RightAnswer
	 *            -> Set the right answer here.
	 * @param OptionX
	 *            -> Set one option here.
	 * @param OptionY
	 *            -> Set another option here.
	 * @param OptionZ
	 *            -> Set remaining last option here.
	 */

	public static void addQuestion(String question, String RightAnswer,
			String OptionX, String OptionY, String OptionZ) {
		QuestionBank.add(new Question(question, RightAnswer, OptionX, OptionY,
				OptionZ));
	}

	public static int getNoOfQ() {
		return QuestionBank.size();
	}

	public static ArrayList<Question> getQList() {
		return QuestionBank;
	}

	public static void QuestionLoading_Changes() {

		if (ext_questionBankFilefound && ext_questionBankFileLoaded) {
			Tkinnter.LoadingStatus_Change(new Color(0, 100, 0), "<HTML> <i> "
					+ _.getNoOfQ() + " Questions has been loaded from <b> "
					+ ext_questionBankURL + " </b> </i> <HTML>");
			Tkinnter.Change_ButtonText(ext_questionBankURL);

		} else if ((!ext_questionBankFilefound)
				&& (!ext_questionBankFileLoaded)) {

			// If both are false;
			Tkinnter.LoadingStatus_Change(Color.BLACK,
					"<HTML> <code>Please Select a QuestionBank</code><HTML>");

		} else if (ext_questionBankFilefound) {

			Tkinnter.LoadingStatus_Change(Color.GRAY,
					"<HTML> <i> Reading questions from <b>"
							+ _.ext_questionBankURL + "</b> </i> <HTML>");
			Tkinnter.Change_ButtonText(ext_questionBankURL);
		} else {
			Tkinnter.LoadingStatus_Change(Color.RED,
					"<HTML> <b>Some Errors are occured in QuestionBank File</b> <HTML>");
		}

	}

	public static void readQuestionBankFile() {
		if (ext_questionBankFilefound) {

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuild = dbf.newDocumentBuilder();
				Document doc = docBuild.parse(new File(ext_questionBankURL));
				doc.getDocumentElement().normalize();
				NodeList xmlQuestions = doc
						.getElementsByTagName("QuestionPacket"); // getting
																	// all
																	// question_packets
																	// list
				for (int i = 0; i < xmlQuestions.getLength(); i++) {// Iterating
																	// question_packet
																	// array

					Node questionPacket = xmlQuestions.item(i); // Getting
																// individual
																// question_packet
					if (questionPacket.getNodeType() == Node.ELEMENT_NODE) {

						Element questionElements = (Element) questionPacket;

						_.addQuestion(

								questionElements
										.getElementsByTagName("Question")
										.item(0).getTextContent().trim(),

								questionElements.getElementsByTagName("Answer")
										.item(0).getTextContent().trim(),

								questionElements.getElementsByTagName("Option")
										.item(0).getTextContent().trim(),

								questionElements.getElementsByTagName("Option")
										.item(1).getTextContent().trim(),

								questionElements.getElementsByTagName("Option")
										.item(2).getTextContent().trim()

						);// closing question
					}// closing if
				}// closing loop

				ext_questionBankFileLoaded = true; // The Question has been
													// loaded
				QuestionLoading_Changes();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void resetQuestionBank() {
		QuestionBank.clear();

	}

}

class Question {

	private String que, ra;
	private String[] option;
	private Status ans;

	public Question(String _que, String _r_ans, String _f_opt, String _s_opt,
			String _t_opt) {
		/*
		 * Question("Question as String","Answers","Option","Option", "Option");
		 */
		ans = Status.NA;
		que = _que;
		ra = _r_ans;
		option = new String[] { _r_ans, _f_opt, _s_opt, _t_opt };
		optionsort();

	}

	private void optionsort() {
		int r1 = 0;
		int r2 = 0;
		String temp = "";
		Random rnd = new Random();

		for (int i = 0; i < 20; i++) {

			r1 = rnd.nextInt(4);
			r2 = rnd.nextInt(4);

			if (r1 == r2) {
				if (r1 == 3) {
					r1 -= 1;
				} else {
					r1 += 1;
				}
			}
			// System.out.println(r1 +" "+r2); // Remove this
			temp = option[r1];
			option[r1] = option[r2];
			option[r2] = temp;
		}
	}

	public String getQuestion() {
		return que;
	}

	public String getAnswer() {
		return ra;
	}

	public String[] getOption() {
		return option;
	}

	public Status getAnswerStatus() {
		return ans;
	}

	public void setAnswerStatus(Status sts) {
		ans = sts;
	}
}

enum Status {
	LEAVE, WRONG, CORRECT, NA,
}

class Tkinnter extends JFrame implements ActionListener {

	private static final long serialVersionUID = 8895754935909962772L;

	private JButton startbtn;
	private JTextField t_leave, t_correct, t_wrong;
	private JCheckBox livescore, correctans, report, skipOption;
	private JLabel heading, credits, scoreSet, leave, correct, wrong;
	private static JLabel questionLoad;
	private static JButton editQuestions;

	public Tkinnter() {

		setSize(750, 380);
		setLocation(100, 150);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setTitle("Configration");

		heading = new JLabel(
				"<HTML><h2><u><b>Configure quiz rules</b></u></h2></HTML>");
		credits = new JLabel(
				"<HTML>Credits :<i> Nandana , Pavithra ,Greeshma </i></HTML>");
		scoreSet = new JLabel(
				"<HTML><i><strong>Set your score rules here</strong> </i></HTML>");
		leave = new JLabel(
				"<HTML> <em> Score on question leave </em> </em> </HTML>");
		correct = new JLabel(
				"<HTML> <em> Score for right answers </em> </HTML>");
		wrong = new JLabel("<HTML> <em>Score for wrong answers </em> </HTML>");
		questionLoad = new JLabel();

		t_leave = new JTextField(3);
		t_correct = new JTextField(3);
		t_wrong = new JTextField(3);

		livescore = new JCheckBox("Show live score ");
		correctans = new JCheckBox("Show correct answer after answering ");
		report = new JCheckBox("Generate summary after quiz has been done");
		skipOption = new JCheckBox("Enable skip Button");

		startbtn = new JButton("Start Quiz");
		editQuestions = new JButton("Please select question bank");

		heading.setBounds(300, 30, 400, 40);
		livescore.setBounds(60, 90, 400, 20);
		correctans.setBounds(60, 120, 400, 20);
		skipOption.setBounds(60, 150, 400, 20);
		report.setBounds(60, 180, 400, 20);

		editQuestions.setBounds(60, 240, 250, 30);

		startbtn.setBounds(600, 270, 100, 40);
		credits.setBounds(30, 330, 400, 20);

		scoreSet.setBounds(420, 80, 400, 30);
		correct.setBounds(400, 120, 400, 30);
		wrong.setBounds(400, 160, 400, 30);
		leave.setBounds(400, 200, 400, 30);

		questionLoad.setBounds(80, 280, 500, 60);

		t_correct.setBounds(550, 125, 40, 25);
		t_wrong.setBounds(550, 165, 40, 25);
		t_leave.setBounds(550, 205, 40, 25);

		Color c = new Color(214, 217, 223);

		t_correct.setText("" + _.postivePoint);
		t_correct.setBackground(c);
		t_correct.setHorizontalAlignment(SwingConstants.CENTER);

		t_wrong.setText("" + _.negativePoint);
		t_wrong.setBackground(c);
		t_wrong.setHorizontalAlignment(SwingConstants.CENTER);

		t_leave.setText("" + _.leavePoint);
		t_leave.setBackground(c);
		t_leave.setHorizontalAlignment(SwingConstants.CENTER);

		questionLoad.setForeground(Color.BLUE);
		credits.setForeground(Color.GRAY);
		heading.setForeground(Color.BLUE);

		startbtn.addActionListener(this);
		editQuestions.addActionListener(this);

		livescore.setSelected(_.config_liveScore);
		skipOption.setSelected(_.config_skipButton);
		report.setSelected(_.config_report);
		correctans.setSelected(_.config_correctAnswer);

		add(heading); // Label - The Heading
		add(credits); // Label - The Credits
		add(questionLoad); // Label - Loaded Question details
		add(livescore); // Checkbox - show live score or not
		add(correctans); // Checkbox - show correct answer after answering or
							// not;
		add(skipOption); // Checkbox - show skip option or not
		add(report); // Checkbox - show detail summary after quiz

		add(startbtn); // Button - start the quiz
		add(editQuestions); // Button to edit question

		add(scoreSet); // Label - for Score Setting title
		add(correct); // Label - for Textbox which contain correct score
		add(wrong); // Label - for Textbox which contain wrong score
		add(leave); // Label - for Textbox which contain leave score

		add(t_correct); // TextBox Textbox which contain correct score
		add(t_wrong); // Textbox - Textbox which contain wrong score
		add(t_leave); // TextBox - Textbox which contain leave score

		setVisible(true);
		_.QuestionLoading_Changes();
	}

	public static void LoadingStatus_Change(Color fc, String labelText) {
		questionLoad.setForeground(fc);
		questionLoad.setText(labelText);
	}

	public static void Change_ButtonText(String str) {
		editQuestions.setText(str);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource().equals(startbtn)) {
			if (_.ext_questionBankFilefound && _.ext_questionBankFileLoaded) {
				saveConfig();
				QBoard Qbord = new QBoard();
				this.dispose();
			} else {
				new Alert(
						"QuestionBanK Not Found",
						"<HTML><B> ERROR:</B> Please select a valid QuestionBank<HTML>",
						"The QuestionBank is'nt loaded properly");
			}

		}

		if (ae.getSource().equals(editQuestions)) {
			_.resetQuestionBank();
			File f;

			JFileChooser loadFile = new JFileChooser();
			loadFile.setApproveButtonText("Select File");
			loadFile.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filte = new FileNameExtensionFilter(
					"NCM QUIZ -Question File", "xml");
			loadFile.setFileFilter(filte);
			int returnVal = loadFile.showOpenDialog(this);
			if (returnVal == loadFile.APPROVE_OPTION) {
				f = loadFile.getSelectedFile();
				_.ext_questionBankFilefound = true;
				_.ext_questionBankURL = f.getPath();
			}
			_.QuestionLoading_Changes();
			_.readQuestionBankFile();
			_.QuestionLoading_Changes();

		}

	}

	private void saveConfig() {

		_.postivePoint = Integer.parseInt(t_correct.getText());
		_.negativePoint = Integer.parseInt(t_wrong.getText());
		_.leavePoint = Integer.parseInt(t_leave.getText());
		_.config_liveScore = livescore.isSelected();
		_.config_correctAnswer = correctans.isSelected();
		_.config_skipButton = skipOption.isSelected();
		_.config_report = report.isSelected();

	}

}

class QBoard extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private int AttentedCount = 0;
	private int SkippedCount = 0;
	Color silver;
	Font myfont,correctAnsFont;
	Font scorefont;
	// Component Declaration
	private JLabel scoreLabel, infoLabel, correctAnswer;
	private JButton optionA, optionB, optionC, optionD, skip;
	private JPanel topPanel;
	private JTextArea txtArea;

	// Temp Registery
	private String Answer = null;
	private String preAnswer = null;
	private int question_Id = -1;

	public QBoard() {
		myfont = new Font("SansSerif", Font.PLAIN, 18);
		correctAnsFont = new Font("SansSerif", Font.PLAIN, 14);
		scorefont = new Font("Serif", Font.PLAIN, 56);
		silver = new Color(220, 220, 220);
		setSize(700, 480);
		setLocation(150, 150);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		setTitle("Quiz Board");
		addComponents();
		setVisible(true);
		refreshInfoValue();
		ItrateQuestioner();

	}

	private void ItrateQuestioner() {
		question_Id++;
		
		
		
		if (question_Id < _.QuestionBank.size()) {
			ShowQuestion(question_Id);
		}
		else{
			optionA.setEnabled(false);
			optionB.setEnabled(false);
			optionC.setEnabled(false);
			optionD.setEnabled(false);
			skip.setEnabled(false);
			
			optionA.setVisible(false);
			optionB.setVisible(false);
			optionC.setVisible(false);
			optionD.setVisible(false);
			skip.setVisible(false);
			
			txtArea.setEnabled(false);	
			txtArea.setText("Thank you :)");
			
			if(!(_.config_liveScore)){
				txtArea.setText("Thank you , yours Total Score is = "+_.TSCORE);
			}
		}
	refreshInfoValue();

	}

	private void ShowQuestion(int i) {
		preAnswer = Answer;
		Answer = _.QuestionBank.get(i).getAnswer();
		txtArea.setText(_.QuestionBank.get(i).getQuestion());
		optionA.setText(_.QuestionBank.get(i).getOption()[0]);
		optionB.setText(_.QuestionBank.get(i).getOption()[1]);
		optionC.setText(_.QuestionBank.get(i).getOption()[2]);
		optionD.setText(_.QuestionBank.get(i).getOption()[3]);
	}

	private void refreshInfoValue() {
		System.out.println("Pre Answer = " + preAnswer + "Answer = " +Answer);
		
		scoreLabel.setText("<HTML><I><B>" + _.TSCORE + "</B></I></H1></HTML>");
		infoLabel.setText("<HTML> <I> You attented " + AttentedCount
				+ " question and skipped " + SkippedCount + " in out of "
				+ _.getNoOfQ() + " Questions </I><HTML> ");
		
		if (!(preAnswer == null)) {
			if(_.QuestionBank.get(question_Id-1).getAnswerStatus()==Status.CORRECT){
				correctAnswer.setForeground(new Color(0,102,0));
				correctAnswer.setText("<HTML><I>  Your answer <B>('"+ preAnswer + "')</B> is correct  <B><I> </HTML>");
				
			}
			else if(_.QuestionBank.get(question_Id-1).getAnswerStatus()==Status.WRONG){
				correctAnswer.setForeground(new Color(204,0,0));
				correctAnswer.setText("<HTML><I>Sorry wrong answer  , <B> '"+ preAnswer + "'</B> is correct option</I> </HTML>");
			}
			else if(_.QuestionBank.get(question_Id-1).getAnswerStatus()==Status.LEAVE){
				correctAnswer.setForeground(new Color(146,48,137));
				correctAnswer.setText("<HTML><I> You skipped :P ,  <B> '"
						+ preAnswer + "'</B>is correct option</I> </HTML>");
			}
			
		} else {
			correctAnswer.setText("");
		}


	}

	private void addComponents() {

		topPanel = new JPanel();
		txtArea = new JTextArea();
		infoLabel = new JLabel();
		scoreLabel = new JLabel();

		optionA = new JButton("Option 1");
		optionB = new JButton("Option 2");
		optionC = new JButton("Option 3");
		optionD = new JButton("Option 4");

		skip = new JButton("SKIP");

		correctAnswer = new JLabel();

		topPanel.setBounds(0, 0, 700, 230);
		topPanel.setLayout(null);
		topPanel.setBackground(silver);

		txtArea.setBounds(20, 80, 660, 150);
		txtArea.setFont(myfont);
		txtArea.setLineWrap(true);
		txtArea.setEditable(false);
		txtArea.setBorder(null);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(txtArea);
		txtArea.setBackground(silver);

		infoLabel.setBounds(15, 10, 550, 80);
		infoLabel.setFont(myfont);
		infoLabel.setForeground(Color.GRAY);

		scoreLabel.setBounds(565, 5, 125, 70);
		scoreLabel.setFont(scorefont);
		scoreLabel.setForeground(Color.BLUE);

		optionA.setBounds(10, 230, 340, 60);
		optionB.setBounds(350, 230, 340, 60);
		optionC.setBounds(10, 290, 340, 60);
		optionD.setBounds(350, 290, 340, 60);

		optionA.addActionListener(this);
		optionB.addActionListener(this);
		optionC.addActionListener(this);
		optionD.addActionListener(this);

		skip.setBounds(600, 360, 80, 80);
		skip.setFont(myfont);
		skip.addActionListener(this);

		correctAnswer.setForeground(new Color(0, 100, 0));
		correctAnswer.setFont(correctAnsFont);
		correctAnswer.setBounds(15, 380, 590, 30);

		topPanel.add(txtArea);
		topPanel.add(infoLabel);
		add(topPanel);
		add(optionA);
		add(optionB);
		add(optionC);
		add(optionD);

		// Adding Optional Components

		if (_.config_skipButton) {
			add(skip);
		}
		if (_.config_correctAnswer) {
			add(correctAnswer);
		}
		if (_.config_liveScore) {
			topPanel.add(scoreLabel);
		}

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(skip)) {
			setAnswer(0);
		}
		else{
			if (ae.getActionCommand().equals(Answer)) {
				setAnswer(1);
			}else{
				setAnswer(-1);
			}
		}
				
		
	}
	public void setAnswer(int x){
		
		
		switch (x){
			case 0:	
			SkippedCount++;
			_.QuestionBank.get(question_Id).setAnswerStatus(Status.LEAVE);
			_.scoreLeave();
			break;
		
			case 1:
			AttentedCount++;
			_.QuestionBank.get(question_Id).setAnswerStatus(Status.CORRECT);
			_.scoreCorrect();
			break;
		
			case -1:
				AttentedCount++;
				_.QuestionBank.get(question_Id).setAnswerStatus(Status.WRONG);
				_.scoreWrong();
			break;
				
			default: 
				new Alert("Error","Some Errors occured ..!,","please contact devlopers");
            break;              
		}
		ItrateQuestioner();
	}
}
class Alert extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public Alert(String title, String text, String tooltip) {
		setSize(400, 150);
		setTitle(title);
		setLocation(300, 300);
		setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
		JLabel label = new JLabel(text);
		label.setToolTipText(tooltip);
		add(label);
		setResizable(false);
		setVisible(true);
	}
}


