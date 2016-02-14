package se.frand.dictionarytree;

import java.util.*;

import se.frand.dictionarytree.DictionaryTree.TreeNode;

public class DictionaryTree {
	/*
	 * this is a firstChild/nextSibling implementation of a tree
	 * 
	 * root node of dictionary Tree is
	 * TreeNode('')
	 * its first child would be TreeNode('a')
	 * and its next sibling would be null;
	 * 
	 * DictionaryTree.add('cat')
	 * this would yield TreeNode('') -> TreeNode('c') -> TreeNode('a') -> TreeNode('t')
	 * followed by DictionaryTree.add('cup') 
	 * would yield TreeNode('') -> TreeNode('c') -> TreeNode('a') --> TreeNode('u') -> TreeNode('p')
	 * where --> represents a sibling relationship and the child relationship to TreeNode('t')
	 * still exists.
	 * 
	 * add the word a
	 * DictionaryTree.add("a");
	 * we would obviously end up with a tree: root -> 'a'*
	 * the length of word for every step would be
	 * root -> 'a'* -> null
	 *  1       1       0
	 *  
	 * at root it would see that letter is null and it is not the end of any word.
	 * 
	 * a tree represents a root with two leafs, a child and a sibling.
	 * each leaf non null leaf represents a sub tree.
	 * 
	 * how about you think about it as you are just adding a letter to where it belongs in
	 * a tree. if the word length is longer than zero after that call add on the rest of the word
	 * 
	 */
	
	private TreeNode root;
	
	public DictionaryTree() {
		root = new TreeNode();
	}
	
	public void add(String word) {
		boolean upper = Character.isUpperCase(word.charAt(0));
		StringBuilder sb = new StringBuilder(word.toLowerCase());
		if(root.firstChild == null)
			root.firstChild = add(sb, new TreeNode(), upper);
		else
			add(sb, root.firstChild, upper);
	}
	
	private TreeNode add(StringBuilder word, TreeNode node, boolean upperTerminator) {
		//System.out.println(word);

		char letter = word.charAt(0);
		
		// if node.letter is empty then put the letter there and add the rest to 
		// its first child.
		if(node.letter == '\0') {
			node.letter = letter;
			word = word.deleteCharAt(0);
			if(word.length() > 0) {
				node.firstChild = add(word, new TreeNode(), upperTerminator);
			} else {
				node.terminator = true;
				node.upperTerminator = upperTerminator;
			}
		}

		// determine if we are adding a child or adding a sibling
		// if the letter equals the current node then call add for the child
		// if not add it to the first null sibling
		else if(letter == node.letter) {
			word = word.deleteCharAt(0);
			if(word.length() > 0) {
				if(node.firstChild == null) {
					node.firstChild = add(word, new TreeNode(), upperTerminator);
				} else {
					add(word, node.firstChild, upperTerminator);
				}
			} else {
				node.terminator = true;
				node.upperTerminator = upperTerminator;
			}
		} else {
			if(node.nextSibling == null)
				node.nextSibling = add(word, new TreeNode(), upperTerminator);
			else
				add(word, node.nextSibling, upperTerminator);
		}

		return node;
	}
	
	public boolean isWord(String word) {
		return isWord(new StringBuilder(word.toLowerCase()), root.firstChild);
	}
	
	private boolean isWord(StringBuilder word, TreeNode node) {
		if(node == null) {
			return false;
		}
		
		char letter = word.charAt(0);
		
		if(letter == node.letter) {
			word.deleteCharAt(0);
			if(word.length() == 0) {
				if(node.terminator)
					return true;
				return false;
			}
			return isWord(word, node.firstChild);
		}
		return isWord(word, node.nextSibling);
	}
	
	private TreeNode findNode(StringBuilder wordStart, TreeNode node) {
		if(node == null) {
			return null;
		}
		
		char letter = wordStart.charAt(0);
		
		if(letter == node.letter) {
			wordStart.deleteCharAt(0);
			if(wordStart.length() == 0) {
				return node;
			}
			return findNode(wordStart, node.firstChild);
		}
		return findNode(wordStart, node.nextSibling);
	}
	
	public String[] nextNWords(String wordStart, int n) {
		TreeNode node = findNode(new StringBuilder(wordStart),root.firstChild);
		if(node == null) {
			return new String[0];
		}
		return nextNWords(node.firstChild,
				new Vector<String>(10),
				wordStart,
				n,
				node.upperTerminator)
				.toArray(new String[10]);
	}
	
	private Vector<String> nextNWords(TreeNode node, Vector<String> v, String wordStart, int n, boolean upper) {
		StringBuilder sb;
		if(isWord(wordStart)) {
			sb = new StringBuilder(wordStart);
			if(upper)
				sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
			v.addElement(sb.toString());
		}
		LinkedList<Pair<TreeNode, StringBuilder>> queue =
				new LinkedList<Pair<TreeNode, StringBuilder>>(); 
		TreeNode currentNode = node;
		sb = new StringBuilder(wordStart);
		while(v.size() < n && currentNode != null) {
			if(currentNode.firstChild != null)
				queue.add(
						new Pair<TreeNode, StringBuilder>(
								currentNode.firstChild,
								(new StringBuilder(sb)).append(currentNode.letter)));
			
			if(currentNode.terminator) {
				sb.append(currentNode.letter);
				if(currentNode.upperTerminator)
					sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
				v.addElement(sb.toString());
				sb.deleteCharAt(sb.length()-1);
			}
			
			if(currentNode.nextSibling != null)
				currentNode = currentNode.nextSibling;
			else {
				if(queue.isEmpty())
					break;
				Pair<TreeNode, StringBuilder> p = queue.poll();
				currentNode = p.o1;
				sb = p.o2;
			}
			
		}

		return v;
	}
	
	
	public class TreeNode {
		public char letter;
		public TreeNode firstChild;
		public TreeNode nextSibling;
		public boolean terminator;
		public boolean upperTerminator;
		public TreeNode() {
			terminator = false;
			upperTerminator = false;
			letter = '\0';
		}

	}
}
