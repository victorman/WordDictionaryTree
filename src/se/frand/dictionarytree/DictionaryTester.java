package se.frand.dictionarytree;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class DictionaryTester {
	public static void main(String[] args) {
		
		final DictionaryTree dict = new DictionaryTree();
		int cnt = 0;
		
		try {
			File file = new File("/usr/share/dict/words");
			Scanner scanner = new Scanner(file);
	
			while(scanner.hasNext()) {
				String word = scanner.next();
				word = word.trim();
				dict.add(word);
				cnt++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.printf("%d words added\n", cnt);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Word Complete");
		frame.setSize(500, 500);
		
		JPanel panel = new JPanel();
		panel.setSize(frame.getWidth(), 50);
		final JTextField textField = new JTextField(30);
		final JList<String> list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(textField);
		frame.add(panel, BorderLayout.NORTH);
		frame.add(list, BorderLayout.CENTER);
		
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				setList(e, list, dict);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				setList(e, list, dict);
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				setList(e, list, dict);
			}
		});
		
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				JList t = (JList) e.getSource();
				if(t.getSelectedValue() != null) {
					textField.setText(t.getSelectedValue().toString());
				}
			}
		});
		
		frame.setVisible(true);
	}
	
	private static void isWord(DictionaryTree dict, String word) {
		if(dict.isWord(word))
			System.out.printf("%s is a word\n", word);
		else
			System.out.printf("%s is not a word\n", word);
	}
	
	private static void setList(DocumentEvent event, JList<String> list, DictionaryTree dict) {

		Document d = event.getDocument();
		try {
			list.setListData(dict.nextNWords(d.getText(0, d.getLength()).toLowerCase(), 20));
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			list.setListData(new Vector<String>());
		}
	}
}
